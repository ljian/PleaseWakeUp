package org.moonforest.pleasewakeup;

import android.app.Application;

/**
 * Created by ljian on 17/8/18.
 */

public class MyApplication extends Application {
    public static MyApplication sMyApplication;

    public MyApplication() {
        sMyApplication = this;
    }
}
