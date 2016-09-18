package xyz.zsckare.doramasdownloader.Views;

import android.content.Intent;
import android.graphics.Color;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Helpers.DoramaGridAdapter;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.GenereModel;
import xyz.zsckare.doramasdownloader.Models.PageModel;
import xyz.zsckare.doramasdownloader.R;

public class GeneresActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutGeneres;
    GridView gridView;
    LinearLayout pagination;
    String TAG = "TAG";
    LinkedList<PageModel> pageArr = new LinkedList();
    LinkedList<Button> btnArr = new LinkedList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generes);
        gridView = (GridView) findViewById(R.id.gridViewDoramas);
        pagination = (LinearLayout) findViewById(R.id.paginationContainer);
        layoutGeneres = (LinearLayout)findViewById(R.id.layoutGeneres);
        fillGeneres();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DoramaActivity.postion = position;

                Intent intent = new Intent(GeneresActivity.this,DoramaActivity.class);
                startActivity(intent);
                //Toast.makeText(LastSeriesActivity.this, Comun.list_doramas_thumbs.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    getPages(genere.getUrl());
                    getLastChapters(genere.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (PageModel pagina : pageArr){
            if (pagina.getName().compareToIgnoreCase(btn.getText().toString())==0){
                Toast.makeText(GeneresActivity.this, "es una pagina", Toast.LENGTH_SHORT).show();
                String pagna = btn.getText().toString();
                Comun.list_doramas_thumbs.clear();
                gridView.setAdapter(null);
                for (PageModel page: pageArr) {
                    if(!(btn.getText().toString().compareToIgnoreCase("1") ==0)){

                        if (page.getName().compareToIgnoreCase(pagna)==0){
                            Toast.makeText(GeneresActivity.this, page.getUrl(), Toast.LENGTH_SHORT).show();
                            try {

                                getLastChapters(page.getUrl());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {

                            getLastChapters(page.getUrl());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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

                    fillGrid();
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


    private void fillGrid(){
        final DoramaGridAdapter adapter = new DoramaGridAdapter(this, Comun.list_doramas_thumbs);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                gridView.setAdapter(adapter);

            }
        });
    }


    private void getPages(final String _url) {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d("Fetching Pages %s...", _url);

                    int j = 1;
                    int i = 1;

                    Document doc = Jsoup.connect(_url).userAgent("Mozilla").timeout(5000).get();
                    Elements pages = doc.select("a.page");
                    // Log.d(TAG,"size------>"+pages.size());
                    if (!pageArr.isEmpty()){
                        pageArr.clear();
                    }
                    for (Element page:pages) {

                        //Log.d(TAG, "run: --->"+page.text()+"--------"+page.attr("href"));
                        PageModel pageModel = new PageModel(page.text(),page.attr("href"));
                        pageArr.add(pageModel);

                    }

                    fillBtnArr();
                    fillPagination();
                    //clearPagination();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void fillBtnArr() {
        if (!btnArr.isEmpty()){
            btnArr.clear();
        }

        if (btnArr.isEmpty()){
            int j = 1;
            int i = 2;
            Button btn = new Button(GeneresActivity.this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btn.setText("1");
            btn.setBackgroundColor(Color.rgb(48,63,159));
            btn.setTextColor(Color.WHITE);
            btn.setId(j + 1 + (i * 4));
            btn.setOnClickListener(GeneresActivity.this);
            btnArr.add(btn);
            j++;
            i++;
            for (PageModel page :pageArr) {

                Button btnTag = new Button(GeneresActivity.this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(page.getName());
                btnTag.setBackgroundColor(Color.rgb(48,63,159));
                btnTag.setTextColor(Color.WHITE);
                btnTag.setId(j + 1 + (i * 4));
                btnTag.setOnClickListener(GeneresActivity.this);
                btnArr.add(btnTag);
                j++;
                i++;
            }
        }
    }


    private void clearPagination(){

        if (!btnArr.isEmpty()){
            for (Button btn :btnArr) {
                pagination.removeAllViews();
            }
        }else{
            fillPagination();
        }
    }

    private void fillPagination() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!btnArr.isEmpty()){
                    pagination.removeAllViews();
                }
                for (Button btn :btnArr) {
                    pagination.addView(btn);
                }

            }
        });
    }

}
