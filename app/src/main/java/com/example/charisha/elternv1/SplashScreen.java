package com.example.charisha.elternv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * Created by e-Miracle workers.
 * The first screen to be shown when user opens app
 * screen will stay on for a few seconds and automatically go to next screen
 */

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //to diplay the image
        ImageView titleImage = (ImageView)  findViewById(R.id.titleImage);

        //title image is set
        titleImage.setImageResource(R.drawable.logo2);

        //show as splash screen
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer to show logo
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                //check if user has already singed in
                if(SaveSharedPreference.getUserName(SplashScreen.this).length() == 0) {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    //if user is signed in check what type of user and show correct home screen
                    if(SaveSharedPreference.getUserType(SplashScreen.this).equals("carer"))
                    {
                        MainActivity.userName = SaveSharedPreference.getUserName(SplashScreen.this);
                        MainActivity.userType = SaveSharedPreference.getUserType(SplashScreen.this);
                        Intent i = new Intent(SplashScreen.this, MainCarerScreenActivity.class);
                        startActivity(i);
                    }
                    else
                    if (SaveSharedPreference.getUserType(SplashScreen.this).equals("elderly"))
                    {
                        MainActivity.userName = SaveSharedPreference.getUserName(SplashScreen.this);
                        MainActivity.userType = SaveSharedPreference.getUserType(SplashScreen.this);
                        Intent i = new Intent(SplashScreen.this, MainElderScreenActivity.class);
                        startActivity(i);
                    }

                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
       //     return true;
      //  }

        return super.onOptionsItemSelected(item);
    }
}
