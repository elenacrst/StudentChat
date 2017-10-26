package com.example.elena.studentchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elena.studentchat.ChatMessage;
import com.example.elena.studentchat.R;

import java.util.List;

/**
 * Created by Elena on 10/25/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<ChatMessage> data;
    public void setData(List<ChatMessage> data){
        this.data= data;
        notifyDataSetChanged();
    }
    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.message_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        MessagesViewHolder viewHolder  = new MessagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {

        if (data.get(position).getName()!=null && data.get(position).getName().length()>0)
            holder.authorText.setText(data.get(position).getName());
        if (data.get(position).getBody()!=null && data.get(position).getBody().length()>0) {
            holder.messageText.setText(data.get(position).getBody());
            holder.messageText.setVisibility(View.VISIBLE);
            holder.sharedImage.setVisibility(View.GONE);
        }
        else if (data.get(position).getPhotoUrl()!=null && data.get(position).getPhotoUrl().length()>0){
            //if (isPhoto) {
                holder.messageText.setVisibility(View.GONE);
                holder.sharedImage.setVisibility(View.VISIBLE);
                Glide.with(holder.sharedImage.getContext())
                        .load(data.get(position).getPhotoUrl())
                        .into(holder.sharedImage);
          //  } else {

           // }
        }
    }

    @Override
    public int getItemCount() {
        if (data==null)return 0;
        return data.size();
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder{

        TextView messageText, authorText;
        ImageView sharedImage;
        public MessagesViewHolder(View itemView) {
            super(itemView);
            messageText =(TextView) itemView.findViewById(R.id.message_text);
            authorText = (TextView)itemView.findViewById(R.id.author_text);
            sharedImage = (ImageView) itemView.findViewById(R.id.shared_image);
        }
    }
}
