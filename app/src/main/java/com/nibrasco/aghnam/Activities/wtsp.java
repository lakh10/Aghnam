package com.nibrasco.aghnam.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nibrasco.aghnam.R;

public class wtsp extends AppCompatActivity {

    Button btnwtsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtsp);
        btnwtsp = (Button)findViewById(R.id.wtsp);

        btnwtsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toNumber = "+966 55404 0181"; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                String message = "وصل الدفع :";
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                }


        });
    }
}
