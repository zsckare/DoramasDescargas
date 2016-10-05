package xyz.zsckare.doramasdownloader.Views;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.IframeLink;
import xyz.zsckare.doramasdownloader.Models.PageModel;
import xyz.zsckare.doramasdownloader.R;

public class DoramaActivity extends AppCompatActivity {

    public static String dorama_url = "";
    public static String img_url = "";
    public static int postion = 0;
    static String name = "";
    static String TAG = "DoramaActivity";

    public static int full_info = 0;
    ImageView portadaDorama;
    TextView sinopsisTextView;
    DoramaThumbModel dorama;
    LinkedList<String> textLink = new LinkedList();
    LinkedList<String> hreflink = new LinkedList();
    ListView listChapters;
    TextView titleDorama;
    MaterialDialog progressInicio;
    String full_card_text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorama);
        titleDorama= (TextView)findViewById(R.id.title_dorama);
        sinopsisTextView = (TextView)findViewById(R.id.sinopsis_text_view);
        listChapters = (ListView)findViewById(R.id.listChapters);
        if (full_info==0){
            dorama = Comun.list_doramas_thumbs.get(postion);

            dorama_url = dorama.getUrl();
            titleDorama.setText(dorama.getName());
        }
        portadaDorama = (ImageView)findViewById(R.id.img_Dorama);



        MaterialDialog.Builder builderInicio = new MaterialDialog.Builder(this)
                .content(R.string.load_serie)
                .title(R.string.wait)
                .progress(true, 0);

        progressInicio = builderInicio.build();

        if (full_info==0){
            Picasso.with(getApplicationContext()).load(dorama.getImg()).into(portadaDorama);
        }
        getChapters();


        listChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                name = textLink.get(position);
                getInfo(hreflink.get(position));
            }
        });
    }

    private void getChapters() {
        progressInicio.show();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching Pages %s...", dorama_url);

                    Document doc = Jsoup.connect(dorama_url).userAgent("Mozilla").timeout(5000).get();
                    final String img = doc.select("div.font > img").first().attr("src");

                    //Log.d(TAG, "run: "+img);
                    final String titulo  = doc.select("h1.titulo").text();
                    if (full_info!=0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                titleDorama.setText(titulo);
                            }
                        });
                        Handler uiHandler = new Handler(Looper.getMainLooper());
                        uiHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                Picasso.with(getApplicationContext()).load(img).error(R.drawable.logo_doramas).into(portadaDorama, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess: Picasso");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "onError: Picasso");
                                    }
                                });
                            }
                        });

                    }
                    Elements chapters = doc.select("ul.lcp_catlist > li > a");

                    Elements descriptions = doc.select("div.font");
                    Log.d(TAG,"size------>"+descriptions.size());

                    for (int i = 0; i < (chapters.size()-1); i++) {
                        textLink.add(chapters.get(i).text());
                        hreflink.add(chapters.get(i).attr("href"));
                    }

                    for (Element desc: descriptions) {
                        full_card_text = desc.text();
                    }
                    processText(full_card_text);
                    fillList();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void processText(String text_to_process) {

        final String splited[] = text_to_process.split("Sinopsis:");

        Log.d(TAG, "processText: --->"+splited[1]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        sinopsisTextView.setText(splited[1]);

            }
        });

    }

    private void fillList() {

        final ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,textLink
                );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listChapters.setAdapter(adapter);
                progressInicio.dismiss();
                Toast.makeText(getApplicationContext(), "Visita estrenosdoramas.org/", Toast.LENGTH_SHORT).show();

            }
        });
    }



    public  void getInfo(final String chapter_url){
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching %s...", chapter_url);

                    Document doc = Jsoup.connect(chapter_url).userAgent("Mozilla").timeout(10000).get();
                    Elements links = doc.select("iframe");
                    Elements titles = doc.select("title");
                    for (Element link : links) {
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
                }
                catch (Exception e)
                { runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressInicio.dismiss();
                        new MaterialDialog.Builder(DoramaActivity.this)
                                .title(R.string.error)
                                .content(R.string.error_text)
                                .positiveText(R.string.load_again)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        getInfo(chapter_url);
                                    }
                                })
                                .show();
                    }
                });
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    public static LinkedList<IframeLink> arrayIframes = new LinkedList();
    static String title = "";


    private  void getIframeslinks() throws IOException {
        IframeLink link;
        for(IframeLink iframe:  arrayIframes){
            moreInfo(iframe.getUrl());
        }

    }

    private void moreInfo(String frame_url) throws IOException{
        Document doc = Jsoup.connect(frame_url).userAgent("Mozilla").timeout(10000).get();
        Elements links = doc.select("script[type]");

        String pattern = "\\[(.*?)\\]";

        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        for(Element link: links){
            String text = link+"";
            if(text.contains("jwplayer('embed').setup({")){
                Matcher m = r.matcher(text);
                if (m.find( )) {
                    Log.d("INFO","---->Found value: " +m.group(0) );

                    processResults(m.group(0));
                }else {
                    Log.d(TAG, "moreInfo: error no encontrado");
                    // System.out.println("NO MATCH");
                }
            }
        }
    }

    public void processResults(String result) throws IOException {
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
            Log.d("INFO------->",srcs.get(0));
            Comun.main_url = srcs.get(0);
            Log.d("------------------", "processResults: "+Comun.main_url);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new MaterialDialog.Builder(DoramaActivity.this)
                            .title(name)
                            .content("¿Deseas descargar "+name+"?")
                            .positiveText(R.string.download)


                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Comun.main_url));
                                    request.setDescription("Descargando capitulo");
                                    request.setTitle(title);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
                                    
                                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);

                                    Toast.makeText(DoramaActivity.this, "Descarga Iniciada", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });

        }

    }

}
