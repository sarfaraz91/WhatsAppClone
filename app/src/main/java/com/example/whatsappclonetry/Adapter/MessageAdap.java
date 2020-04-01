package com.example.whatsappclonetry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsappclonetry.Model.Message;
import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.R;
import com.example.whatsappclonetry.Utils.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdap extends BaseAdapter {
    Context context;
    ArrayList<Message> messageArrayList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentuser = auth.getCurrentUser();

    User user;

    public MessageAdap(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }


    @Override
    public int getCount() {
        return messageArrayList.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.message_items,null);
        }

        Message message = messageArrayList.get(position);
        TextView lbl_msg = view.findViewById(R.id.lbl_msg);
        TextView lbl_msg_sender = view.findViewById(R.id.lbl_msg_sender);

        LinearLayout container_sender = view.findViewById(R.id.container_sender);
        LinearLayout container_reciever = view.findViewById(R.id.container_reciever);

        user = Global.chatContact;

        ImageView profile_image = view.findViewById(R.id.profile_image);

        if(currentuser.getUid() == message.getFrom()){
            container_sender.setVisibility(View.VISIBLE);
            lbl_msg_sender.setVisibility(View.VISIBLE);
            lbl_msg_sender.setText(message.getMessage());

            container_reciever.setVisibility(View.GONE);
        }else{
            container_reciever.setVisibility(View.VISIBLE);
            container_sender.setVisibility(View.GONE);
            lbl_msg.setText(message.getMessage());
            profile_image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .into(profile_image);
        }

        return view;
    }
}
