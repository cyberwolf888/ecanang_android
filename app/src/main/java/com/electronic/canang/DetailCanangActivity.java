package com.electronic.canang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.electronic.canang.adapters.CanangDetailAdapter;
import com.electronic.canang.models.CanangDetail;
import com.electronic.canang.utility.Helper;
import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class DetailCanangActivity extends AppCompatActivity {

    private String id_canang;
    private String nama_paket;
    private String image;
    private String harga;
    private String keterangan;
    private String status;

    private List<CanangDetail> canangs;
    protected RecyclerView mRecyclerView;
    protected CanangDetailAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public JsonArray data;

    private View mProgressView;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(DetailCanangActivity.this);
        id_canang = getIntent().getStringExtra("id_canang");
        nama_paket = getIntent().getStringExtra("nama_paket");
        image = getIntent().getStringExtra("image");
        harga = getIntent().getStringExtra("harga");
        keterangan = getIntent().getStringExtra("keterangan");
        status = getIntent().getStringExtra("status");

        setContentView(R.layout.activity_detail_canang);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nama_paket);

        mProgressView = findViewById(R.id.main_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvCanangDetail);
        mLayoutManager = new LinearLayoutManager(DetailCanangActivity.this);

        ImageView imgTop =(ImageView) findViewById(R.id.imgTop);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        TextView tvKeterangan = (TextView) findViewById(R.id.tvKeterangan);

        Ion.with(DetailCanangActivity.this)
                .load(image)
                .withBitmap()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .intoImageView(imgTop);

        tvPrice.setText(new Helper().formatNumber(Integer.valueOf(harga)));
        tvKeterangan.setText(keterangan);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pesan();
            }
        });

        Button btnBuy = (Button) findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pesan();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getData();
    }

    public void pesan(){
        Intent i = new Intent(DetailCanangActivity.this, PesanActivity.class);
        i.putExtra("id_canang",id_canang);
        i.putExtra("nama_paket",nama_paket);
        i.putExtra("image",image);
        i.putExtra("harga",harga);
        i.putExtra("keterangan",keterangan);
        i.putExtra("status",status);
        startActivity(i);
    }

    private void getData(){
        if(isNetworkAvailable()){
            showProgress(true);

            canangs = new ArrayList<>();
            data = new JsonArray();

            String url = new RequestServer().getServer_url() + "getCanangDetail";
            JsonObject jsonReq = new JsonObject();
            jsonReq.addProperty("canang_id", id_canang);

            Ion.with(DetailCanangActivity.this)
                    .load(url)
                    .setJsonObjectBody(jsonReq)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            data = result.getAsJsonArray("data");
                            for (int i=0; i<data.size(); i++){
                                JsonObject objData = data.get(i).getAsJsonObject();
                                canangs.add(new CanangDetail(
                                        objData.get("id").getAsString(),
                                        objData.get("canang_id").getAsString(),
                                        objData.get("label").getAsString(),
                                        objData.get("qty").getAsString()
                                ));
                            }
                            mAdapter = new CanangDetailAdapter(DetailCanangActivity.this, canangs);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            showProgress(false);
                        }
                    });

        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Shows the progress UI and hides the main view.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
