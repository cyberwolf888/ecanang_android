package com.electronic.canang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class RegisterActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText nama_lengkap,telp,address,password,password_confirmation;
    private View mProgressView;
    private View mLoginFormView;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_register);

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        nama_lengkap = (EditText) findViewById(R.id.nama_lengkap);
        telp = (EditText) findViewById(R.id.telp);
        address = (EditText) findViewById(R.id.address);
        password = (EditText) findViewById(R.id.password);
        password_confirmation = (EditText) findViewById(R.id.password_confirmation);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        nama_lengkap.setError(null);
        telp.setError(null);
        address.setError(null);
        password.setError(null);
        password_confirmation.setError(null);

        String _email = mEmailView.getText().toString();
        String _nama_lengkap = nama_lengkap.getText().toString();
        String _telp = telp.getText().toString();
        String _address = address.getText().toString();
        String _password = password.getText().toString();
        String _password_confirmation = password_confirmation.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(_nama_lengkap)) {
            nama_lengkap.setError("Nama Lengkap tidak boleh kosong");
            focusView = nama_lengkap;
            cancel = true;
        }

        if (TextUtils.isEmpty(_telp)) {
            telp.setError("Telp tidak boleh kosong");
            focusView = telp;
            cancel = true;
        }

        if (TextUtils.isEmpty(_address)) {
            address.setError("Alamat tidak boleh kosong");
            focusView = address;
            cancel = true;
        }

        if (TextUtils.isEmpty(_password_confirmation)) {
            password_confirmation.setError("Password Confirmation tidak boleh kosong");
            focusView = password_confirmation;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(_password) && !isPasswordValid(_password)) {
            password.setError("Password tidak valid");
            focusView = password;
            cancel = true;
        }

        if(!_password.equals(_password_confirmation)){
            password_confirmation.setError("Password harus sama");
            focusView = password_confirmation;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(_email)) {
            mEmailView.setError("Email tidak valid");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(_email)) {
            mEmailView.setError("Email tidak valid");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if(isNetworkAvailable()){
                showProgress(true);
                String url = new RequestServer().getServer_url() + "register";
                JsonObject jsonReq = new JsonObject();
                jsonReq.addProperty("name", _nama_lengkap);
                jsonReq.addProperty("email", _email);
                jsonReq.addProperty("telp", _telp);
                jsonReq.addProperty("address", _address);
                jsonReq.addProperty("password", _password);
                jsonReq.addProperty("password_confirmation", _password_confirmation);

                Ion.with(RegisterActivity.this)
                        .load(url)
                        .setJsonObjectBody(jsonReq)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                String status = result.get("status").toString();
                                if (status.equals("1")){
                                    JsonObject data = result.getAsJsonObject("data");
                                    session.createLoginSession(data.get("id").getAsString(),data.get("name").getAsString(),data.get("address").getAsString(),data.get("telp").getAsString());
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    ComponentName cn = i.getComponent();
                                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                                    startActivity(mainIntent);
                                }else{
                                    Toast.makeText(getApplicationContext(), result.get("error").toString(), Toast.LENGTH_LONG).show();
                                }
                                showProgress(false);
                            }
                        });


            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
