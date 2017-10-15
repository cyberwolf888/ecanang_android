package com.electronic.canang;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electronic.canang.utility.Helper;
import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class DetailTransactionActivity extends AppCompatActivity {

    private String id_transaksi;
    private String canang_id;
    private String user_id;
    private String telp;
    private String address;
    private String total;
    private String img_bukti;
    private String status;
    private String created_at;
    private String label_status;
    private String nama_paket;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(DetailTransactionActivity.this);
        id_transaksi = getIntent().getStringExtra("id_transaksi");
        canang_id = getIntent().getStringExtra("canang_id");
        user_id = getIntent().getStringExtra("user_id");
        telp = getIntent().getStringExtra("telp");
        address = getIntent().getStringExtra("address");
        total = getIntent().getStringExtra("total");
        img_bukti = getIntent().getStringExtra("img_bukti");
        status = getIntent().getStringExtra("status");
        created_at = getIntent().getStringExtra("created_at");
        label_status = getIntent().getStringExtra("label_status");
        nama_paket = getIntent().getStringExtra("nama_paket");

        setContentView(R.layout.activity_detail_transaction);

        TextView tvId = (TextView) findViewById(R.id.tvId);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvCreated = (TextView) findViewById(R.id.tvCreated);
        TextView tvTotal = (TextView) findViewById(R.id.tvTotal);
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvTelp = (TextView) findViewById(R.id.tvTelp);
        Button btnMyReservation = (Button) findViewById(R.id.btnMyReservation);

        tvId.setText(id_transaksi);
        tvTitle.setText(nama_paket);
        tvCreated.setText(created_at);
        tvTotal.setText(new Helper().formatNumber(Integer.valueOf(total)));
        tvStatus.setText(label_status);
        tvAddress.setText(address);
        tvTelp.setText(telp);

        tvTitle.setTextColor(Color.BLUE);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = new RequestServer().getServer_url() + "getDetailCanang";
                JsonObject jsonReq = new JsonObject();
                jsonReq.addProperty("canang_id", canang_id);
                Ion.with(DetailTransactionActivity.this)
                        .load(url)
                        .setJsonObjectBody(jsonReq)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                JsonObject data = result.getAsJsonObject("data");
                                Intent i = new Intent(DetailTransactionActivity.this, DetailCanangActivity.class);
                                i.putExtra("id_canang",data.get("id").getAsString());
                                i.putExtra("nama_paket",data.get("nama_paket").getAsString());
                                i.putExtra("image",new RequestServer().getImg_url()+data.get("image").getAsString());
                                i.putExtra("harga",data.get("harga").getAsString());
                                i.putExtra("keterangan",data.get("keterangan").getAsString());
                                i.putExtra("status",data.get("status").getAsString());
                                startActivity(i);
                            }
                        });
            }
        });

        if(Integer.valueOf(status) == 1){
            btnMyReservation.setVisibility(View.VISIBLE);
        }

        btnMyReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailTransactionActivity.this, BayarActivity.class);
                i.putExtra("id_transaksi",id_transaksi);
                startActivity(i);
                finish();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetailTransactionActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Batal")
                        .setMessage("Apakah anda yakin untuk membatalkan transaksi ini?")
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancel();
                            }
                        })
                        .setNegativeButton("Tidak",null)
                        .show();
            }
        });
        if(status.equals("0")){
            btnCancel.setVisibility(View.GONE);
        }
        Log.d("url img",">"+img_bukti);
        if(!img_bukti.equals("")){
            Log.d("url img",">"+new RequestServer().getServer_url()+"../assets/img/pembayaran/"+img_bukti);
            ImageView imgBukti = (ImageView) findViewById(R.id.imgBukti);
            Ion.with(DetailTransactionActivity.this)
                    .load(new RequestServer().getServer_url()+"../assets/img/pembayaran/"+img_bukti)
                    .withBitmap()
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .intoImageView(imgBukti);
        }
    }

    private void cancel(){
        String url = new RequestServer().getServer_url() + "cancel";
        Log.d("url",">"+url);
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("transaksi_id", id_transaksi);
        Log.d("jsonReq",">"+jsonReq);
        Ion.with(DetailTransactionActivity.this)
                .load(url)
                .setJsonObjectBody(jsonReq)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.d("Result",">"+result);
                        finish();
                    }
                });
        finish();
    }
}
