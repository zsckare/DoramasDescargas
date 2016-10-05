package xyz.zsckare.doramasdownloader.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import xyz.zsckare.doramasdownloader.Helpers.NewChaptersService;
import xyz.zsckare.doramasdownloader.R;

public class AboutActivity extends AppCompatActivity {

    static int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        ImageView imgImageView = (ImageView)findViewById(R.id.imgLogo);

        imgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                if (count == 3){
                    stopService(new Intent(getApplicationContext(), NewChaptersService.class));
                }

            }
        });


    }
}
