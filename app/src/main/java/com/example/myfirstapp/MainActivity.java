package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Compass button */
    public void showCompass(View view) {
        startActivity(new Intent(this, CompassActivity.class));
    }

    /** Called when the user taps the Accelerometers button */
    public void showAccelerometers(View view) {
        startActivity(new Intent(this, AccelerometersActivity.class));
    }

}
