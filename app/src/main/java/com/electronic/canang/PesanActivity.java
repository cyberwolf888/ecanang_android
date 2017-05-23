package com.electronic.canang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.electronic.canang.utility.Helper;
import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class PesanActivity extends AppCompatActivity {

    private String id_canang;
    private String nama_paket;
    private String image;
    private String harga;
    private String keterangan;
    private String status;

    public EditText etAlamat, etTelp;
    private View mProgressView;
    private ScrollView mMainView;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(PesanActivity.this);

        id_canang = getIntent().getStringExtra("id_canang");
        nama_paket = getIntent().getStringExtra("nama_paket");
        image = getIntent().getStringExtra("image");
        harga = getIntent().getStringExtra("harga");
        keterangan = getIntent().getStringExtra("keterangan");
        status = getIntent().getStringExtra("status");

        setContentView(R.layout.activity_pesan);

        mMainView = (ScrollView) findViewById(R.id.booking_form);
        mProgressView = findViewById(R.id.main_progress);

        TextView tvNama = (TextView) findViewById(R.id.tvNama);
        TextView tvTotal = (TextView) findViewById(R.id.tvTotal);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etTelp = (EditText) findViewById(R.id.etTelp);

        tvNama.setText(nama_paket);
        tvTotal.setText(new Helper().formatNumber(Integer.valueOf(harga)));
        etAlamat.setText(session.getUserAlamat());
        etTelp.setText(session.getTelp());

        Button btnPesan = (Button) findViewById(R.id.btnPesan);
        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempPesan();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if (!session.isLoggedIn()){
            Intent i = new Intent(PesanActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void attempPesan(){
        if(isNetworkAvailable()){
            showProgress(true);
            String url = new RequestServer().getServer_url() + "postTransaksi";
            JsonObject jsonReq = new JsonObject();
            jsonReq.addProperty("canang_id", id_canang);
            jsonReq.addProperty("user_id", session.getUserId());
            jsonReq.addProperty("telp", etTelp.getText().toString());
            jsonReq.addProperty("address", etAlamat.getText().toString());
            Ion.with(PesanActivity.this)
                    .load(url)
                    .setJsonObjectBody(jsonReq)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            showProgress(false);
                            Intent i = new Intent(PesanActivity.this, CompleteActivity.class);
                            startActivity(i);
                            finish();
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

            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
