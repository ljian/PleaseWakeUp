package org.moonforest.pleasewakeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.moonforest.pleasewakeup.httpserver.SimpleHttpServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.startServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        SimpleHttpServer.getInstance().startHttpServer();
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
