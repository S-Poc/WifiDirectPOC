package com.example.sawaiparihar.wifidirectpoc;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sawai on 01/09/16.
 */
public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();

    public Adapter(Context mContext, List<WifiP2pDevice> peers) {
        this.mContext = mContext;
        this.mPeers = peers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = ((MyViewHolder) holder);

        WifiP2pDevice item = mPeers.get(position);
//        myViewHolder.isOwner.setText("isOwner: " + item.isGroupOwner());
        myViewHolder.address.setText("address: " + item.deviceAddress + " " + item.deviceName);


        myViewHolder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).connectToPeer(mPeers.get(position));
            }
        });

        myViewHolder.disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).disconnectFromPeer();
            }
        });

        myViewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).sendFile();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPeers.size();
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView isOwner, address, status;
        Button connect, disconnect, share;
        public MyViewHolder(View itemView) {
            super(itemView);

            isOwner = (TextView) itemView.findViewById(R.id.isOwner);
            address = (TextView) itemView.findViewById(R.id.address);
            status = (TextView) itemView.findViewById(R.id.status);
            connect = (Button) itemView.findViewById(R.id.btn_connect);
            disconnect = (Button) itemView.findViewById(R.id.btn_disconnect);
            share = (Button) itemView.findViewById(R.id.btn_share);
        }
    }
}
