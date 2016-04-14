package com.example.charisha.elternv1;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by e-Miracle workers.
 * Class to establish the connection with the online database Parse
 */

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Enable local data storing, specify the specific key for this app which enables the connection between app and server.
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "xkYVBG0Vv0FERNzfJv5mwaK7L0w91AIrgl4KVgTA", "di2gXlNIoV3dLLmN0iEfrn2u98jGLw4Y9SZV5yD7");

    }
}
