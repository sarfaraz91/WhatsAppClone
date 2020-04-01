package com.example.whatsappclonetry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclonetry.Fragments.ContactsFrag;
import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactsAdap extends RecyclerView.Adapter<ContactsAdap.MyViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public ContactsAdap(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    private OnContactCliked onContactCliked;

    //make interface like this
    public interface OnContactCliked {
        void onClick(int position,User user);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_items, null, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txt_name.setText(userArrayList.get(position).getName());
        holder.txt_status.setText(userArrayList.get(position).getStatus());
        Picasso.get()
                .load(userArrayList.get(position).getImage())
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactCliked.onClick(position,userArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView txt_name;
        TextView txt_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }

    public void setOnClickContact(OnContactCliked onClick)
    {
        this.onContactCliked=onClick;
    }


}
