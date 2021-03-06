/*******************************************************************************
 * Copyright (c) 2011 Aalto University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Authors:
 * Maksim Golivkin <maksim@golivkin.eu>
 ******************************************************************************/
package fi.hut.soberit.manager.storage;

import java.util.List;
import java.util.WeakHashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import fi.hut.soberit.sensors.generic.ObservationType;
import fi.hut.soberit.sensors.generic.Storage;

public class AvailableStorageListAdapter extends BaseAdapter {
    
	private static final String TAG = AvailableStorageListAdapter.class.getSimpleName();
	List<ObservationTypeStoragePair> list;
	private Context context;
	
    private int layout;
    
    private LayoutInflater inflater;
    
    
    protected final WeakHashMap<View, View[]> holders = new WeakHashMap<View, View[]>();
	private int [] to = new int [] {
		android.R.id.text1,
		android.R.id.text2	
	};
    
	public AvailableStorageListAdapter(Context context, int layout, List<ObservationTypeStoragePair> list) {
		this.layout = layout;
		this.context = context;
		
		this.list = list;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        super.notifyDataSetChanged();

	}

	public List<ObservationTypeStoragePair> getList() {
		return list;
	}

	public void setList(List<ObservationTypeStoragePair> list) {
		this.list = list;
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ObservationTypeStoragePair getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return list.get(position).type.getId();
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(context, parent);

        } else {
            v = convertView;
        }

        final View[] holder = holders.get(v);
        final ObservationTypeStoragePair from = getItem(position);
        Log.d(TAG, "" + from.type.getName());
        ((TextView)holder[0]).setText(from.type.getName());
        ((TextView)holder[1]).setText(from.storage.getUrl());
        
        return v;
    }
	
    public View newView(Context context, ViewGroup parent) {
    	final View view = inflater.inflate(layout, parent, false);
        return generateViewHolder(view);
    }
    
    private View generateViewHolder(View v) {
        final int count = to.length;
        final View[] holder = new View[count];

        for (int i = 0; i < count; i++) {
            holder[i] = v.findViewById(to[i]);
        }
        holders.put(v, holder);

        return v;
    }
    
	public void clear() {
		list.clear();	
        super.notifyDataSetChanged();

	}

	public void addItem(ObservationTypeStoragePair pair) {
		list.add(pair);
        super.notifyDataSetChanged();
	}
	
	public static class ObservationTypeStoragePair {
		
		public ObservationTypeStoragePair(ObservationType type, Storage storage) {

			this.type = type;
			this.storage = storage;
		}
		
		ObservationType type;
		Storage storage;
	}
}
