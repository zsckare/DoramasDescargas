package xyz.zsckare.doramasdownloader.Views;

import android.graphics.Color;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.GenereModel;
import xyz.zsckare.doramasdownloader.R;

public class GeneresActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutGeneres;
    String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generes);

        layoutGeneres = (LinearLayout)findViewById(R.id.layoutGeneres);
        fillGeneres();
    }

    private void fillGeneres() {
        int j = 11;
        int i = 22;
        for (GenereModel genere: Comun.list_generes) {
            Button btnTag = new Button(GeneresActivity.this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(genere.getName());
            btnTag.setBackgroundColor(Color.rgb(48,63,159));
            btnTag.setTextColor(Color.WHITE);
            btnTag.setId(j + 1 + (i * 4));
            btnTag.setOnClickListener(GeneresActivity.this);
            layoutGeneres.addView(btnTag);
            j++;
            i++;
        }
    }


    @Override
    public void onClick(View view) {
        Button btn = (Button)findViewById(view.getId());


        for (GenereModel genere:Comun.list_generes) {
            if (genere.getName().compareToIgnoreCase(btn.getText().toString())==0){
                Toast.makeText(GeneresActivity.this, genere.getUrl(), Toast.LENGTH_SHORT).show();
                try {
                    getLastChapters(genere.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void getLastChapters(final String my_url) throws IOException, NetworkOnMainThreadException {
        //progressInicio.show();
        //Log.d("------>","<-----");
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching %s...", my_url);


                    Document doc = Jsoup.connect(my_url).userAgent("Mozilla").timeout(5000).get();
                    Elements doramas_img = doc.select("div.clearfix > a >img");
                    Elements doramas_titles = doc.select("div.clearfix > h3 > a");
                    Elements doramas_links = doc.select("div.clearfix > a");
                    if (!Comun.list_doramas_thumbs.isEmpty()){
                        Comun.list_doramas_thumbs.clear();
                    }
                    for (int i = 0; i < doramas_img.size(); i++) {

                        DoramaThumbModel doramaThumbModel = new DoramaThumbModel(doramas_titles.get(i).text(),doramas_img.get(i).attr("src"),doramas_links.get(i).attr("href"));
                        Log.d(TAG,doramaThumbModel.toString());

                        Comun.list_doramas_thumbs.add(doramaThumbModel);
                    }

                    Log.d(TAG, "size: "+Comun.list_doramas_thumbs.size());

                    //fillGrid();
                }
                catch (Exception e)
                {
                    new MaterialDialog.Builder(GeneresActivity.this)
                            .title(R.string.error)
                            .content(R.string.error_text)
                            .positiveText(R.string.load_again)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    try {
                                        getLastChapters(my_url);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
