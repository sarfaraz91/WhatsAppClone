package com.example.whatsappclonetry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsAdap extends RecyclerView.Adapter<RequestsAdap.RequestViewHolder> {


    Context context;
    ArrayList<User> arrayList;

    public RequestsAdap(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    //declare interface
    private OnAcceptClicked onAcceptClicked;

    //make interface like this
    public interface OnAcceptClicked {
        void onAcceptClick(int position,User user);
    }

    private OnCancelClicked onCancelClicked;

    //make interface like this
    public interface OnCancelClicked {
        void onCancelClick(int position,User user);
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.request_items,parent,false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, final int position) {
        holder.txt_name.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .into(holder.profile_image);

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptClicked.onAcceptClick(position,arrayList.get(position));
            }
        });

        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked.onCancelClick(position,arrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView txt_name;
        Button btn_accept;
        Button btn_cancel;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_name = itemView.findViewById(R.id.txt_name);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
        }
    }

    public void setOnClickForAccept(OnAcceptClicked onClick)
    {
        this.onAcceptClicked=onClick;
    }

    public void setOnClickForCancel(OnCancelClicked onClick)
    {
        this.onCancelClicked=onClick;
    }


}
