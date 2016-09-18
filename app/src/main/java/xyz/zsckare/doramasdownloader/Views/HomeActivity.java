package xyz.zsckare.doramasdownloader.Views;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Helpers.DoramasAdapter;
import xyz.zsckare.doramasdownloader.Models.GenereModel;
import xyz.zsckare.doramasdownloader.Models.IframeLink;
import xyz.zsckare.doramasdownloader.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SwipyRefreshLayout mSwipyRefreshLayout;

    static String main_url = "http://estrenosdoramas.org/";
    static String name = "";
    public static LinkedList<String> list_chapters_name = new LinkedList();
    static LinkedList<String>list_chapters_urls = new LinkedList();
    public static LinkedList<String>list_img_urls = new LinkedList();
    String TAG = "TAG";
    static ListView listViewChapters;
    MaterialDialog progressDialog, progressInicio;
    MaterialDialog.Builder builder;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        MaterialDialog.Builder builderInicio = new MaterialDialog.Builder(this)
                .content(R.string.loading_series)
                .title(R.string.wait)
                .progress(true, 0);

        progressInicio = builderInicio.build();


        listViewChapters = (ListView)findViewById(R.id.listview);

        mSwipyRefreshLayout = (SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                try {
                    getLastChapters();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            getLastChapters();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listViewChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int itemPosition     = position;

                String  itemValue    = (String) listViewChapters.getItemAtPosition(position);
                name = itemValue;
                // Show Alert
                //Toast.makeText(getApplicationContext(),"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();

                String url = list_chapters_urls.get(itemPosition);
                //Toast.makeText(MainActivity.this, "url ==>"+url, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, list_img_urls.get(position), Toast.LENGTH_SHORT).show();
                progressDialog.show();
                getInfo(url);



            }

        });



        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.getChapter)
                .content(R.string.wait)
                .progress(true, 0);



        progressDialog = builder.build();
    }

    private void fin(){
        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.series) {
            Intent intent = new Intent(HomeActivity.this,LastSeriesActivity.class);
            startActivity(intent);
        }
        if (id == R.id.generos){
            Intent intent = new Intent(HomeActivity.this,GeneresActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getLastChapters() throws IOException, NetworkOnMainThreadException {
        progressInicio.show();
        //Log.d("------>","<-----");
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching %s...", main_url);


                    Document doc = Jsoup.connect(main_url).userAgent("Mozilla").timeout(5000).get();
                    Elements links = doc.select("div.thumb-cap");
                    Elements links_text = doc.select("div.thumb-cap > strong > a");
                    Elements images = doc.select("div.thumb-cap > a > img");
                    Elements generes = doc.select("div#genuno > ul.alfa > li > a");
                    Log.d("TAG", "run: ---->"+generes.size());
                    //Log.d("size",""+images.size());
                    //Log.d("size",""+links_text.size());

                    for (int i = 0; i <links_text.size() ; i++) {
                        //Log.d("text",links_text.get(i).text());
                        //Log.d("href",links_text.get(i).attr("href"));
                        //Log.d("img","--->"+images.get(i).attr("src"));
                        list_chapters_name.add(links_text.get(i).text());
                        list_chapters_urls.add(links_text.get(i).attr("href"));
                        list_img_urls.add(images.get(i).attr("src"));
                    }

                    if (Comun.list_generes.isEmpty()){
                        for (Element genere:generes) {
                            Log.d("TAG", "run: --->"+genere.text()+"---"+genere.attr("href"));
                            GenereModel genereModel = new GenereModel(genere.text(),genere.attr("href"));
                            Log.d(TAG, "run: "+genereModel.toString());

                            Comun.list_generes.add(genereModel);
                        }
                    }


                    fillList();


                }
                catch (Exception e)
                {

                    showErr();
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    private void showErr(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressInicio.dismiss();
                new MaterialDialog.Builder(HomeActivity.this)
                        .title(R.string.error)
                        .content(R.string.error_text)
                        .positiveText(R.string.load_again)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    getLastChapters();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
        });

    }



    private void fillList(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list_chapters_name);

        final DoramasAdapter mLeadsAdapter = new DoramasAdapter(this,
                list_chapters_name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSwipyRefreshLayout.isRefreshing()){
                    mSwipyRefreshLayout.setRefreshing(false);
                }
                listViewChapters.setAdapter(mLeadsAdapter);
                progressInicio.dismiss();
            }
        });

    }



    //------- trae informacion del capitulo y procesa para dejar listo para descargar



    public  void getInfo(final String chapter_url){
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //Your code goes here

                    Log.d("Fetching %s...", chapter_url);


                    Document doc = Jsoup.connect(chapter_url).userAgent("Mozilla").timeout(5000).get();
                    Elements links = doc.select("iframe");
                    Elements titles = doc.select("title");
                    //System.out.println("\nVideos: (%d)"+ links.size());
                    for (Element link : links) {
                        //  System.out.println(" * video:"+ link.attr("src"));
                        IframeLink iframe = new IframeLink(link.attr("src"));
                        if(iframe.getUrl().contains("mundoasia")){
                            //System.out.println("----->"+iframe.getUrl());
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
    public static LinkedList<IframeLink> arrayIframes = new LinkedList();
    static String title = "";


    private  void getIframeslinks() throws IOException{
        IframeLink link;
        //System.out.println(arrayIframes.size());
        for(IframeLink iframe:  arrayIframes){
            //System.out.println("Clases.MainWindow.getIframeslinks()");
            //  System.out.println(iframe.getUrl());
            moreInfo(iframe.getUrl());
        }

    }

    private void moreInfo(String frame_url) throws IOException{
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
                    //Log.d("INFO","---->Found value: " +m.group(0) );

                    processResults(m.group(0));

                    //JOptionPane.showMessageDialog(null, m.group(0));
                }else {
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
            //Log.d("INFO------->",srcs.get(0));
            Comun.main_url = srcs.get(0);
            //Log.d("------------------", "processResults: "+Comun.main_url);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    new MaterialDialog.Builder(HomeActivity.this)
                            .title(name)
                            .content("¿Deseas descargar "+name+"?")
                            .positiveText(R.string.download)
                            .neutralText("Ver Online")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                                    mediaIntent.setDataAndType(Uri.parse(Comun.main_url), "video/*");
                                    startActivity(mediaIntent);
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Comun.main_url));
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

                                    Toast.makeText(HomeActivity.this, "Descarga Iniciada", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });

        }

    }
}
