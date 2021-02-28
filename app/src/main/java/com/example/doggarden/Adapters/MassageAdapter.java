package com.example.doggarden.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doggarden.Objects.Message;
import com.example.doggarden.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MassageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Message> massageList;
    private DatabaseReference database;
    private String currentUid;


    public MassageAdapter(Context context, List<Message> massageList, DatabaseReference databaseReference, String currentUid) {
        this.context = context;
        this.massageList = massageList;
        this.database = database;
        this.currentUid = currentUid;

    }


    @Override
    public int getItemViewType(int position) {
        Message massage = massageList.get(position);

        if (massage.getUser().getUid().equals(currentUid)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_other, parent, false);
            return new ReceivedMessageHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message massage = massageList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            ((SentMessageHolder) holder).bind(massage);

        } else if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {

            ((ReceivedMessageHolder) holder).bind(massage);
        }
    }

    @Override
    public int getItemCount() {
        return massageList.size();
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView me_LBL_massage, me_LBL_time;

        SentMessageHolder(View itemView) {
            super(itemView);

            me_LBL_massage = (TextView) itemView.findViewById(R.id.me_LBL_massage);
            me_LBL_time = (TextView) itemView.findViewById(R.id.me_LBL_time);
        }

        void bind(Message message) {
            me_LBL_massage.setText(message.getMessage());
            me_LBL_time.setText(message.getTime());
        }
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView other_LBL_massage;
        TextView other_LBL_time;
        TextView other_LBL_name;
        ImageView other_IMV_img;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            other_LBL_massage = (TextView) itemView.findViewById(R.id.other_LBL_massage);
            other_LBL_time = (TextView) itemView.findViewById(R.id.other_LBL_time);
            other_LBL_name = (TextView) itemView.findViewById(R.id.other_LBL_name);
            other_IMV_img = (ImageView) itemView.findViewById(R.id.other_IMV_img);
        }

        void bind(Message message) {
            other_LBL_massage.setText(message.getMessage());
            if(message.getUser().getImageUrl() != null){
                Glide.with(context)
                        .load(message.getUser().getImageUrl())
                        .into(other_IMV_img);
            }

            other_LBL_time.setText(message.getTime());
            other_LBL_name.setText(message.getUser().getDogName());

        }


    }


}