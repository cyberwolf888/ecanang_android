package com.electronic.canang.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.electronic.canang.DetailCanangActivity;
import com.electronic.canang.R;
import com.electronic.canang.models.Canang;
import com.electronic.canang.utility.Helper;
import com.koushikdutta.ion.Ion;

import java.util.List;


public class CanangAdapter extends RecyclerView.Adapter<CanangAdapter.CanangViewHolder> {
    public class CanangViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvTitle;
        TextView tvPrice;
        ImageView imgCanang;

        public CanangViewHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Canang feedItem = listItems.get(getAdapterPosition());
                    Intent i = new Intent(mContext, DetailCanangActivity.class);
                    i.putExtra("id_canang",feedItem.id_canang);
                    i.putExtra("nama_paket",feedItem.nama_paket);
                    i.putExtra("image",feedItem.image);
                    i.putExtra("harga",feedItem.harga);
                    i.putExtra("keterangan",feedItem.keterangan);
                    i.putExtra("status",feedItem.status);
                    mContext.startActivity(i);
                }
            });
            cv = (CardView)itemView.findViewById(R.id.cvCanang);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView)itemView.findViewById(R.id.tvPrice);
            imgCanang = (ImageView)itemView.findViewById(R.id.imgCanang);
        }
    }

    List<Canang> listItems;
    private Context mContext;

    public CanangAdapter(Context context, List<Canang> listItems){
        this.listItems = listItems;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CanangViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_canang, viewGroup, false);
        CanangViewHolder view_holder = new CanangViewHolder(v);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(CanangViewHolder view_holder, int i) {
        view_holder.tvTitle.setText(listItems.get(i).nama_paket);
        Helper helper = new Helper();
        view_holder.tvPrice.setText(helper.formatNumber(Integer.valueOf(listItems.get(i).harga)));
        //view_holder.tvPrice.setText(listItems.get(i).harga);

        if(listItems.get(i).image.equals("")){
            view_holder.imgCanang.setImageResource(R.drawable.noimage);
        }else{
            Ion.with(mContext)
                    .load(listItems.get(i).image)
                    .withBitmap()
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .intoImageView(view_holder.imgCanang);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
