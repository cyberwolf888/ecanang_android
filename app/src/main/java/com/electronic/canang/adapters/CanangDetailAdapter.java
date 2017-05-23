package com.electronic.canang.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.electronic.canang.R;
import com.electronic.canang.models.CanangDetail;

import java.util.List;


public class CanangDetailAdapter extends RecyclerView.Adapter<CanangDetailAdapter.CanangDetailViewHolder> {

    public class CanangDetailViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvQty;
        TextView tvLabel;

        public CanangDetailViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvCanangDetail);
            tvQty = (TextView)itemView.findViewById(R.id.tvQty);
            tvLabel = (TextView)itemView.findViewById(R.id.tvLabel);
        }
    }

    List<CanangDetail> listItems;
    private Context mContext;

    public CanangDetailAdapter(Context context, List<CanangDetail> listItems){
        this.listItems = listItems;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CanangDetailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_canang_detail, viewGroup, false);
        CanangDetailViewHolder view_holder = new CanangDetailViewHolder(v);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(CanangDetailViewHolder view_holder, int i) {
        view_holder.tvQty.setText(listItems.get(i).qty);
        view_holder.tvLabel.setText(listItems.get(i).label);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
