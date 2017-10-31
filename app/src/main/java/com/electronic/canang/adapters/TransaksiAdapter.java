package com.electronic.canang.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.electronic.canang.DetailTransactionActivity;
import com.electronic.canang.R;
import com.electronic.canang.models.Transaksi;
import com.electronic.canang.utility.Helper;

import java.util.List;


public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {
    public class TransaksiViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvId;
        TextView tvTitle;
        TextView tvTotal;
        TextView tvCreated;
        TextView tvStatus;

        public TransaksiViewHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Transaksi feedItem = listItems.get(getAdapterPosition());
                    Intent i = new Intent(mContext, DetailTransactionActivity.class);
                    i.putExtra("id_transaksi",feedItem.id_transaksi);
                    i.putExtra("canang_id",feedItem.canang_id);
                    i.putExtra("user_id",feedItem.user_id);
                    i.putExtra("telp",feedItem.telp);
                    i.putExtra("address",feedItem.address);
                    i.putExtra("total",feedItem.total);
                    i.putExtra("img_bukti",feedItem.img_bukti);
                    i.putExtra("status",feedItem.status);
                    i.putExtra("created_at",feedItem.created_at);
                    i.putExtra("label_status",feedItem.label_status);
                    i.putExtra("nama_paket",feedItem.nama_paket);
                    i.putExtra("feedback",feedItem.feedback);
                    i.putExtra("img_feedback",feedItem.img_feedback);
                    Log.d("feedItem",">"+feedItem.img_feedback);
                    mContext.startActivity(i);
                }
            });
            cv = (CardView)itemView.findViewById(R.id.cvCanang);
            tvId = (TextView)itemView.findViewById(R.id.tvId);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvTotal = (TextView)itemView.findViewById(R.id.tvTotal);
            tvCreated = (TextView)itemView.findViewById(R.id.tvCreated);
            tvStatus = (TextView)itemView.findViewById(R.id.tvStatus);
        }
    }

    List<Transaksi> listItems;
    private Context mContext;

    public TransaksiAdapter(Context context, List<Transaksi> listItems){
        this.listItems = listItems;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TransaksiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_transaksi, viewGroup, false);
        TransaksiViewHolder view_holder = new TransaksiViewHolder(v);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(TransaksiViewHolder view_holder, int i) {
        view_holder.tvId.setText(listItems.get(i).id_transaksi);
        view_holder.tvTitle.setText(listItems.get(i).nama_paket);
        view_holder.tvCreated.setText(listItems.get(i).created_at);
        view_holder.tvStatus.setText(listItems.get(i).label_status);
        Helper helper = new Helper();
        view_holder.tvTotal.setText(helper.formatNumber(Integer.valueOf(listItems.get(i).total)));
        //view_holder.tvPrice.setText(listItems.get(i).harga);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
