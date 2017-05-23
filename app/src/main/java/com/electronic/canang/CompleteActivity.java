package com.electronic.canang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        Button btnMyTransaksi = (Button) findViewById(R.id.btnMyTransaksi);

        btnMyTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CompleteActivity.this, MyTransactionActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
