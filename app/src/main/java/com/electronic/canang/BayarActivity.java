package com.electronic.canang;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.electronic.canang.utility.RequestServer;
import com.electronic.canang.utility.Session;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

public class BayarActivity extends AppCompatActivity {
    private String transaksi_id;
    private ImageView imgBukti;
    private Button btnSubmit;
    private String imagePath;
    private ProgressDialog pDialog;
    private Session session;
    private final static int SELECT_PHOTO = 12345;
    private final static int WRITE_EXTERNAL_RESULT = 105;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transaksi_id = getIntent().getStringExtra("id_transaksi");
        session = new Session(BayarActivity.this);
        setContentView(R.layout.activity_bayar);
        imgBukti = (ImageView) findViewById(R.id.imgBukti);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        imgBukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shouldAskPermission()){
                    if(CheckStoragePermission()){
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    }else{
                        Toast.makeText(getApplicationContext(), "Aplikasi tidak diizinkan untuk mengakses file.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean CheckStoragePermission() {
        int permissionCheckRead = ContextCompat.checkSelfPermission(BayarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) BayarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions((Activity) BayarActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_RESULT);
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity) BayarActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_RESULT);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        /*
        if(permsRequestCode == WRITE_EXTERNAL_RESULT){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            try{
                Uri pickedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                //Cek file size
                File file = new File(imagePath);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                Log.d("File Size",">"+file_size);
                if(file_size>(3*1024)){
                    //TODO jika gambar terlalu besar
                    imagePath = "";
                    Toast.makeText(getApplicationContext(), "Ukuran gambar terlalu besar. Ukuran file maksimal 3 MB", Toast.LENGTH_LONG).show();
                }else{
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);*/
                    imgBukti.setImageBitmap(decodeSampledBitmapFromResource(imagePath, 300, 300));
                }
                cursor.close();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Ukuran gambar terlalu besar. Ukuran file maksimal 3 MB", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res, options);
    }

    private void submit(){
        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(getApplicationContext(), "Gambar tidak boleh kosong!", Toast.LENGTH_LONG).show();
        }else{
            if(isNetworkAvailable()){
                pDialog = new ProgressDialog(BayarActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                String url = new RequestServer().getServer_url()+"postPembayran";
                Log.d("Test Url",">"+url);

                Ion.with(BayarActivity.this)
                        .load(url)
                        .setMultipartParameter("user_id", session.getUserId())
                        .setMultipartParameter("transaksi_id", transaksi_id)
                        .setMultipartFile("image_bukti", "application/images", new File(imagePath))
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try{
                                    String status = result.get("status").toString();
                                    if (status.equals("1")){
                                        //Intent i = new Intent(PaymentActivity.this, CekPesananActivity.class);
                                        //startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Bukti Pembayaran berhasil diupload.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }else{
                                        //TODO jika status 0
                                        Toast.makeText(getApplicationContext(), "Gagal menyipan data", Toast.LENGTH_LONG).show();
                                    }
                                }catch (Exception ex){
                                    Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                                }
                                pDialog.dismiss();
                            }
                        });
            }else{
                Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
            }
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
}
