/*******************************************************************************
 * Copyright (c) 2011 Aalto University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package fi.hut.soberit.sensors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.hut.soberit.sensors.generic.Session;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class SessionDao {

	DatabaseHelper dbHelper;
	
	public static final String SESSION_CREATE =
        "create table session (" +
	        "session_id integer primary key, " +
	        "start datetime not null," +
	        "end datetime)";    
	
    public static final String SESSION_DROP = 
    	"DROP TABLE IF EXISTS session";
    
    public static final String SESSION_TABLE = "session";
	
	public SessionDao(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;		
	}

	public long insertSession(long start) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		final ContentValues values = new ContentValues();
		values.put("start", DatabaseHelper.getUtcDateString(start));
		
		return db.insert(SESSION_TABLE, "", values);
	}
	
	public void updateSession(long sessionId, long end) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		final ContentValues values = new ContentValues();
		values.put("end", DatabaseHelper.getUtcDateString(end));
		
		String whereClause = SessionsTable.SESSION_ID + " = ? ";
		
		db.update(
				SESSION_TABLE, values, 
				whereClause, 
				new String[] {Long.toString(sessionId)});
	}

	public Cursor getSessions() {

		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		builder.setTables(SESSION_TABLE);
		
		return builder.query(dbHelper.getReadableDatabase(), null, null, null, null, null, null);
	}
	
	public List<Session> getSessionObjects() {
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		builder.setTables(SESSION_TABLE);
		
		final Cursor c = builder.query(dbHelper.getReadableDatabase(), 
				null, null, null, 
				null, null, null);
		
		ArrayList<Session> list = new ArrayList<Session>();
		
		for(int i = 0; i<c.getCount(); i++) {
			c.moveToPosition(i);
			
			final Date start = DatabaseHelper.getDateFromUtcDateString(c.getString(c.getColumnIndexOrThrow(SessionsTable.START)));
			final String endString = c.getString(c.getColumnIndexOrThrow(SessionsTable.END));
			final Date end = endString != null ? DatabaseHelper.getDateFromUtcDateString(endString) : null;
			long sessionId = c.getLong(c.getColumnIndexOrThrow(SessionsTable.SESSION_ID));
			list.add(new Session(sessionId, start, end));
		}
		c.close();
		
		return list;
	}
}
