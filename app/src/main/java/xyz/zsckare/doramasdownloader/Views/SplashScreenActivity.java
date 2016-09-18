package xyz.zsckare.doramasdownloader.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.zsckare.doramasdownloader.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                        Intent intent = new Intent(SplashScreenActivity.this,HomeActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.left_in,R.anim.left_out);


                }
            }
        };
        timerThread.start();
    }
}
