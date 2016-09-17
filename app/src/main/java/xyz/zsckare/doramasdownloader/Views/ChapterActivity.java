package xyz.zsckare.doramasdownloader.Views;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.zsckare.doramasdownloader.Models.IframeLink;
import xyz.zsckare.doramasdownloader.R;

public class ChapterActivity extends AppCompatActivity {


    static String url_video = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);



        try {
            getInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Button btnDescargar = (Button)findViewById(R.id.btn_download);

        btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_video));
                request.setDescription("Descargando capitulo");
                request.setTitle(title);
// in order for this if to run, you must use the android 3.2 to compile your app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

// get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                Toast.makeText(ChapterActivity.this, "Descarga Iniciada", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static LinkedList<IframeLink> arrayIframes = new LinkedList();
    static String title = "";
    static String url = "http://www.estrenosdoramas.org/2016/09/moon-lovers-scarlet-heart-ryeo-capitulo-7.html";
    //static String url = "http://mundoasia.net/repro/reproductor/amz2/dr.php?code=3042796E53635770725F2D42394D585650654446465A4777785A5863";
    public static void getInfo() throws IOException, NetworkOnMainThreadException {

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //Your code goes here

                    Log.d("Fetching %s...", url);


                    Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5000).get();
                    Elements links = doc.select("iframe");
                    Elements titles = doc.select("title");
                    //System.out.println("\nVideos: (%d)"+ links.size());
                    for (Element link : links) {
                        //  System.out.println(" * video:"+ link.attr("src"));
                        IframeLink iframe = new IframeLink(link.attr("src"));
                        if(iframe.getUrl().contains("mundoasia")){
                            System.out.println("----->"+iframe.getUrl());
                            arrayIframes.add(iframe);
                        }

                    }
                    for(Element t: titles){
                        System.out.println(t.text());
                        String cap = t.text();
                        title = cap+".mp4";
                    }

                    getIframeslinks();
                    //System.out.println(doc);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private static void getIframeslinks() throws IOException{
        IframeLink link;
        //System.out.println(arrayIframes.size());
        for(IframeLink iframe:  arrayIframes){
            //System.out.println("Clases.MainWindow.getIframeslinks()");
            //  System.out.println(iframe.getUrl());
            moreInfo(iframe.getUrl());
        }

    }

    private static void moreInfo(String frame_url) throws IOException{
        Document doc = Jsoup.connect(frame_url).userAgent("Mozilla").timeout(10000).get();
        Elements links = doc.select("script[type]");
        //System.out.println(links);

        String pattern = "\\[(.*?)\\]";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        for(Element link: links){
            String text = link+"";
            if(text.contains("jwplayer('embed').setup({")){
                //JOptionPane.showMessageDialog(null, text.trim());
                // System.out.println(text.trim());
                Matcher m = r.matcher(text);
                if (m.find( )) {
                    Log.d("INFO","---->Found value: " +m.group(0) );

                    processResults(m.group(0));

                    //JOptionPane.showMessageDialog(null, m.group(0));
                }else {
                    // System.out.println("NO MATCH");
                }
            }
        }
    }

    public static void processResults(String result) throws IOException {
        System.out.println(result);
        LinkedList<String> srcs = new LinkedList();
        Matcher m = Pattern.compile(
                Pattern.quote("file:'")
                        + "(.*?)"
                        + Pattern.quote("',")
        ).matcher(result);
        while(m.find()){

            String match = m.group(1);
            System.out.println(">"+match+"<");
            srcs.add(match);

        }
        if(srcs.size() > 0){
            Log.d("INFO",srcs.get(0));
            //saveUrl(title, srcs.get(0));

        }

    }


    private void downloadVideo(String url_video){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_video));
        request.setDescription("Descargando capitulo");
        request.setTitle(title);
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }



    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }


}
