package com.example.whatsappclonetry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.whatsappclonetry.Model.GroupChat;
import com.example.whatsappclonetry.R;

import java.util.ArrayList;

public class GroupChatAdap extends BaseAdapter {

    Context context;
    ArrayList<GroupChat> groupChats;

    public GroupChatAdap(Context context, ArrayList<GroupChat> groupChats) {
        this.context = context;
        this.groupChats = groupChats;
    }

    @Override
    public int getCount() {
        return groupChats.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.groupchat_items,null);
        }

        GroupChat groupChat = groupChats.get(position);

        TextView name = view.findViewById(R.id.name);
        name.setText("From: "+groupChat.getName());
        TextView message = view.findViewById(R.id.message);
        message.setText("Message: "+groupChat.getMessage());
        TextView date = view.findViewById(R.id.date);
        date.setText("Date: "+groupChat.getDate());
        TextView time = view.findViewById(R.id.time);
        time.setText("Time: "+groupChat.getTime());


        return view;
    }
}
