package com.electronic.canang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.electronic.canang.adapters.CanangAdapter;
import com.electronic.canang.models.Canang;
import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Canang> canangs;
    protected RecyclerView mRecyclerView;
    protected CanangAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public JsonArray data;

    private View mProgressView;
    private SwipeRefreshLayout mMainView;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(MainActivity.this);
        setContentView(R.layout.activity_main);

        mMainView = (SwipeRefreshLayout) findViewById(R.id.main_view);
        mProgressView = findViewById(R.id.main_progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvCanang);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);

        mMainView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getData();
    }

    private void getData(){
        mMainView.setRefreshing(false);
        if(isNetworkAvailable()){
            showProgress(true);

            canangs = new ArrayList<>();
            data = new JsonArray();

            String url = new RequestServer().getServer_url() + "getCanang";
            JsonObject jsonReq = new JsonObject();
            jsonReq.addProperty("getCanang", true);

            Ion.with(MainActivity.this)
                    .load(url)
                    .setJsonObjectBody(jsonReq)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            data = result.getAsJsonArray("data");
                            for (int i=0; i<data.size(); i++){
                                JsonObject objData = data.get(i).getAsJsonObject();
                                String photo = "";
                                if(!objData.get("image").isJsonNull()){
                                    photo = new RequestServer().getImg_url()+objData.get("image").getAsString();
                                }
                                canangs.add(new Canang(
                                        objData.get("id").getAsString(),
                                        objData.get("nama_paket").getAsString(),
                                        photo,
                                        objData.get("harga").getAsString(),
                                        objData.get("keterangan").getAsString(),
                                        objData.get("status").getAsString()
                                ));
                            }

                            mAdapter = new CanangAdapter(MainActivity.this, canangs);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (session.isLoggedIn()){
            getMenuInflater().inflate(R.menu.login, menu);
        }else{
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_home) {
            //Intent i = new Intent(MainActivity.this, LoginActivity.class);
            //startActivity(i);
        }
        if (id == R.id.action_transaksi) {
            Intent i = new Intent(MainActivity.this, MyTransactionActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_account) {
            Intent i = new Intent(MainActivity.this, MyAccountActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_logout) {
            session.logoutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
