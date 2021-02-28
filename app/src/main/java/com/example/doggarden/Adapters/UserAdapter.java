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
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final String ONLINE = "online";
    private final String OFFLINE = "offline";
    private final String IMG_ONLINE = "presence_offline";
    private final String IMG_OFFLINE = "presence_offline";

    private List<User> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private User user;
    private Context context;


    // data is passed into the constructor
    public UserAdapter(Context context, List<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         user = mData.get(position);

        //todo set img
        holder.list_LBL_name.setText(user.getDogName());

        if (user.getStatus().equals(ONLINE)) {
            holder.list_IMG_status.setImageResource(android.R.drawable.presence_online);
        } else if(user.getStatus().equals(OFFLINE)){
            holder.list_IMG_status.setImageResource(android.R.drawable.presence_offline);
        }
        showUserImage(holder);

    }

    private void showUserImage(ViewHolder holder) {
            if(user.getImageUrl() != null){
                Glide.with(context)
                        .load(user.getImageUrl())
                        .into(holder.list_IMG_user);
            }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    User getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    //Holds variables in a View
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView list_IMG_status;
        TextView list_LBL_name;
        ShapeableImageView list_IMG_user;
        //RelativeLayout list_RL_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            list_IMG_status = itemView.findViewById(R.id.list_IMG_status);
            list_LBL_name = itemView.findViewById(R.id.list_LBL_name);
            list_IMG_user = itemView.findViewById(R.id.list_IMG_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

        }
    }

}
