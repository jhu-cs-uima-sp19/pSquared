package com.example.psquared;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout root;
    public TextView sent;
    public TextView received;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        sent = itemView.findViewById(R.id.mymessage);
        received = itemView.findViewById(R.id.yourmessage);
    }

    public void setSent(String string) {
        sent.setText(string);
    }


    public void setReceived(String string) {
        received.setText(string);
    }
}

