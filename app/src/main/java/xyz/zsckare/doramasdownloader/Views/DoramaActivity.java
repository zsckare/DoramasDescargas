package xyz.zsckare.doramasdownloader.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.PageModel;
import xyz.zsckare.doramasdownloader.R;

public class DoramaActivity extends AppCompatActivity {

    public static String dorama_url = "";
    public static String img_url = "";
    public static int postion = 0;
    static String TAG = "DoramaActivity";
    ImageView portadaDorama;
    DoramaThumbModel dorama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorama);
        TextView titleDorama = (TextView)findViewById(R.id.title_dorama);
        dorama = Comun.list_doramas_thumbs.get(postion);
        dorama_url = dorama.getUrl();
        portadaDorama = (ImageView)findViewById(R.id.img_Dorama);

        titleDorama.setText(dorama.getName());


        Picasso.with(getApplicationContext()).load(dorama.getImg()).into(portadaDorama);
        getInfo();
    }

    private void getInfo() {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching Pages %s...", dorama_url);

                    int j = 1;
                    int i = 1;

                    Document doc = Jsoup.connect(dorama_url).userAgent("Mozilla").timeout(5000).get();
                    Elements pages = doc.select("a.page");
                    Log.d(TAG,"size------>"+pages.size());


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
