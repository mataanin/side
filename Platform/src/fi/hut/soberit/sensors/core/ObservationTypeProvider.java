package fi.hut.soberit.sensors.core;

import fi.hut.soberit.sensors.DatabaseHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ObservationTypeProvider extends ContentProvider {

	private static final int TYPES = 0;

	private static final int TYPE_ID = 1;
	
	private static UriMatcher sUriMatcher;

	private DatabaseHelper dbHelper;

	@Override
	public boolean onCreate() {

		dbHelper = new DatabaseHelper(this.getContext());
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
        
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.OBSERVATION_TYPE_TABLE);
        
        Cursor c = qb.query(dbHelper.getReadableDatabase(), 
        		projection, selection, selectionArgs, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public String getType(Uri uri) {

		switch(sUriMatcher.match(uri)) {
		case TYPES:
			return ObservationTypeTable.CONTENT_TYPES;
		case TYPE_ID:
			return ObservationTypeTable.CONTENT_TYPE;
		}
		
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.OBSERVATION_TYPE_TABLE, "", values);
        
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(ObservationTypeTable.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        int count;
        switch (sUriMatcher.match(uri)) {
        case TYPES:
            count = db.delete(DatabaseHelper.OBSERVATION_TYPE_TABLE, selection, selectionArgs);
            break;

        case TYPE_ID:
            final String typeId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.OBSERVATION_TYPE_TABLE, 
            		ObservationTypeTable.OBSERVATION_TYPE_ID + "=" + typeId
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                    selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		
        int count;
        switch (sUriMatcher.match(uri)) {
        case TYPES:
            count = db.update(DatabaseHelper.OBSERVATION_TYPE_TABLE, values, selection, selectionArgs);
            break;

        case TYPE_ID:
            final String typeId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.OBSERVATION_TYPE_TABLE, values, 
            		ObservationTypeTable.OBSERVATION_TYPE_ID + "=" + typeId
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), 
                    selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ObservationTypeTable.AUTHORITY, "observation_types", TYPES);
        sUriMatcher.addURI(ObservationTypeTable.AUTHORITY, "observation_types/#", TYPE_ID);
    }
	
}
