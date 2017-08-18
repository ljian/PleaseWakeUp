package org.moonforest.pleasewakeup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by ljian on 17/8/18.
 */

public class RedirectActivity extends AppCompatActivity {
    private static final String TAG = "RedirectActivity";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        TextView tv = new TextView(this);
        tv.setText("url:" + getIntent().getData());
        tv.setTextColor(Color.BLACK);
        setContentView(tv);
        Log.d(TAG, ">>>>>>>>>>>>>>>>onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, ">>>>>>>>>>>>>>>>onNewIntent");
    }

    public static void launch(Context context, String url) {
        Log.d(TAG, ">>>>>>>>>>>>>>>>launch");
        Intent intent = new Intent(context, RedirectActivity.class);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
