package saco.ProjectFireTruckV2.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import saco.ProjectFireTruckV2.R;

public class StartupScreenActivity extends AppCompatActivity {

    boolean loaded = false;
    private SoundPool soundPool;
    private int sirenSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set activity to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Removes the title
        getSupportActionBar().hide();
        //Set background to white color
        setContentView(R.layout.activity_loading_screen);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        initialise();
        onLoadListener();
    }

    public void initialise(){
        soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION,0);
        sirenSound = soundPool.load(getApplicationContext(),R.raw.siren,0);
        Log.d("Loading Screen", "Loading sound");
    }

    public void onLoadListener() {
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(final SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                Log.d("Loading Screen", "Loading sound complete");
                if (loaded) {
                    soundPool.play(sirenSound, 0.03f, 0.03f, 1, 0, 1f);
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        //Start the next activity at the end of count down
                        @Override
                        public void onFinish() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            soundPool.stop(sirenSound);
                            finish();
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * Disable the back button
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
