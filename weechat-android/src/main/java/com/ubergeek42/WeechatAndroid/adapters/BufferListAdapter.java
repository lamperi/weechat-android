/*******************************************************************************
 * Copyright 2012 Keith Johnson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ubergeek42.WeechatAndroid.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ubergeek42.WeechatAndroid.BuildConfig;
import com.ubergeek42.WeechatAndroid.R;
import com.ubergeek42.WeechatAndroid.service.Buffer;
import com.ubergeek42.WeechatAndroid.service.BufferList;
import com.ubergeek42.WeechatAndroid.service.BufferListEye;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferListAdapter extends BaseAdapter implements BufferListEye {

    private static Logger logger = LoggerFactory.getLogger("BufferListAdapter");
    final private static boolean DEBUG = BuildConfig.DEBUG;

    Activity activity;
    LayoutInflater inflater;
    private ArrayList<Buffer> buffers = new ArrayList<Buffer>();

    final private static int[][] COLORS = new int[][] {
            {0xaa525252, 0xaa6c6c6c}, // other
            {0xaa44525f, 0xaa596c7d}, // channel
            {0xaa57474f, 0xaa735e69}, // private
    };
    
    public BufferListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////// adapter methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getCount() {
        return buffers.size();
    }

    @Override
    public Buffer getItem(int position) {
        return buffers.get(position);
    }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bufferlist_item, parent, false);
            holder = new ViewHolder();
            holder.ui_open = convertView.findViewById(R.id.open);
            holder.ui_buffer = (TextView) convertView.findViewById(R.id.buffer);
            holder.ui_warm = (TextView) convertView.findViewById(R.id.buffer_warm);
            holder.ui_hot = (TextView) convertView.findViewById(R.id.buffer_hot);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        Buffer buffer = getItem(position);

        holder.ui_buffer.setText((BufferList.SHOW_TITLE && buffer.printable2 != null) ? buffer.printable2 : buffer.printable1);

        int unreads = buffer.unreads;
        int highlights = buffer.highlights;

        int important = (highlights > 0 || (unreads > 0 && buffer.type == Buffer.PRIVATE)) ? 1 : 0;

        holder.ui_buffer.setBackgroundColor(COLORS[buffer.type][important]);
        holder.ui_open.setVisibility(buffer.is_open ? View.VISIBLE : View.GONE);

        if (highlights > 0) {
            holder.ui_hot.setText(Integer.toString(highlights));
            holder.ui_hot.setVisibility(View.VISIBLE);
        } else
            holder.ui_hot.setVisibility(View.INVISIBLE);

        if (unreads > 0) {
            holder.ui_warm.setText(Integer.toString(unreads));
            holder.ui_warm.setVisibility(View.VISIBLE);
        } else
            holder.ui_warm.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        TextView ui_hot;
        TextView ui_warm;
        TextView ui_buffer;
        View ui_open;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////// BufferListEye
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBuffersChanged() {
        final ArrayList<Buffer> buffers = BufferList.getBufferList();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BufferListAdapter.this.buffers = buffers;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHotCountChanged() {}
}
