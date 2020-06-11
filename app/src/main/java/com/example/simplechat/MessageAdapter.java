package com.example.simplechat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView messageText;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_layout);
        }
    }

    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_Uid = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        if (from_user.equals(current_Uid)){
            holder.messageText.setBackgroundResource(R.drawable.message_text_send_background);
            holder.messageText.setTextColor(Color.BLACK);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }
        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
