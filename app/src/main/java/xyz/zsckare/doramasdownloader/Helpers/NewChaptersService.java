package xyz.zsckare.doramasdownloader.Helpers;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.os.Handler;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.R;
import xyz.zsckare.doramasdownloader.Views.HomeActivity;

public class NewChaptersService extends Service {

    public static LinkedList<String> old_chapters = new LinkedList();
    public static LinkedList<String> new_chapters = new LinkedList();
    static String main_url = "http://estrenosdoramas.org/";

    String TAG = getClass().getSimpleName();
    Context context;
    public NewChaptersService() {
     //   context =  getApplicationContext();
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        context = HomeActivity.mContext;
        HomeActivity.firstTime = 1;
        try {
            getLastChapters();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }
    int algo = 0;
    private void checkForNewChapters() {

        int size = old_chapters.size();

        for (int i = 0; i < size; i++) {
            if (old_chapters.get(i).compareToIgnoreCase(new_chapters.get(i))==0){
                Log.d(TAG, "checkForNewChapters: mismo capitlulo");
                String message = "Se ha agregado "+new_chapters.get(i);


            }else{
                Log.d(TAG, "checkForNewChapters:  Nuevo Capitulo ");
                if (i==0) {

                    String message = "Se ha agregado " + new_chapters.get(i);
                    PugNotification.with(context)
                            .load()
                            .title(R.string.new_chapter_title)
                            .message(R.string.new_chapter_message)
                            .bigTextStyle(message)
                            .smallIcon(R.drawable.ic_stat_sinfondo)
                            .largeIcon(R.drawable.logo_doramas_small)
                            .flags(Notification.DEFAULT_ALL)
                            .click(HomeActivity.class)
                            .simple()
                            .build();
                }

            }
        }
        old_chapters = new_chapters;

        boolean network = Comun.isNetworkAvailable(context);

        if (network ==true) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                public void run() {
                    checkForNewChapters();
                }
            }, 600000);
        }

    }

    private void getLastChapters() throws IOException, NetworkOnMainThreadException {

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching %s...", main_url);

                    Document doc = Jsoup.connect(main_url).userAgent("Mozilla").timeout(5000).get();
                    Elements links_text = doc.select("div.thumb-cap > strong > a");

                    for (int i = 0; i <links_text.size() ; i++) {
                        new_chapters.add(links_text.get(i).text());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }finally {
                    checkForNewChapters();
                }
            }
        });

        thread.start();
    }
}
