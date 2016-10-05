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
import xyz.zsckare.doramasdownloader.MainActivity;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.PageModel;
import xyz.zsckare.doramasdownloader.R;

public class LastSeriesActivity extends AppCompatActivity implements View.OnClickListener {

    String main_url = "http://www.estrenosdoramas.org/category/doramas-online";
    String TAG = "LastSeries";
    GridView gridView;
    LinearLayout pagination;
    LinkedList<PageModel>pageArr = new LinkedList();
    MaterialDialog progressDialog, progressInicio;
    MaterialDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_series);

        gridView = (GridView) findViewById(R.id.gridViewDoramas);
        pagination = (LinearLayout) findViewById(R.id.paginationContainer);

        MaterialDialog.Builder builderInicio = new MaterialDialog.Builder(this)
                .content(R.string.loading_series)
                .title(R.string.wait)
                .progress(true, 0);

        progressInicio = builderInicio.build();

        try {
            getLastChapters(main_url);
            getPages(main_url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            DoramaActivity.postion = position;
            DoramaActivity.full_info = 0;
            Intent intent = new Intent(LastSeriesActivity.this,DoramaActivity.class);
            startActivity(intent);
            //Toast.makeText(LastSeriesActivity.this, Comun.list_doramas_thumbs.get(position).toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("Fetching Pages %s...", main_url);

                    int j = 1;
                    int i = 1;

                    Document doc = Jsoup.connect(_url).userAgent("Mozilla").timeout(5000).get();
                    Elements pages = doc.select("a.page");
                   // Log.d(TAG,"size------>"+pages.size());
                    for (Element page:pages) {

                        //Log.d(TAG, "run: --->"+page.text()+"--------"+page.attr("href"));
                        PageModel pageModel = new PageModel(page.text(),page.attr("href"));
                        pageArr.add(pageModel);

                    }

                    fillPagination();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void fillPagination() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int j = 1;
                int i = 2;
                Button btn = new Button(LastSeriesActivity.this);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btn.setText("1");
                btn.setBackgroundColor(Comun.comunColor);
                btn.setTextColor(Color.WHITE);
                btn.setId(j + 1 + (i * 4));
                btn.setOnClickListener(LastSeriesActivity.this);
                pagination.addView(btn);
                j++;
                i++;
                for (PageModel page :pageArr) {

                    Button btnTag = new Button(LastSeriesActivity.this);
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText(page.getName());
                    btnTag.setBackgroundColor(Color.rgb(48,63,159));
                    btnTag.setTextColor(Color.WHITE);
                    btnTag.setId(j + 1 + (i * 4));
                    btnTag.setOnClickListener(LastSeriesActivity.this);
                    pagination.addView(btnTag);
                    j++;
                    i++;
                }

            }
        });
    }


    private void getLastChapters(final String my_url) throws IOException, NetworkOnMainThreadException {
        progressInicio.show();
        //Log.d("------>","<-----");
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //Log.d("Fetching %s...", main_url);


                    Document doc = Jsoup.connect(my_url).userAgent("Mozilla").timeout(5000).get();
                    Elements doramas_img = doc.select("div.clearfix > a >img");
                    Elements doramas_titles = doc.select("div.clearfix > h3 > a");
                    Elements doramas_links = doc.select("div.clearfix > a");
                    if (!Comun.list_doramas_thumbs.isEmpty()){
                        Comun.list_doramas_thumbs.clear();
                    }
                    for (int i = 0; i < doramas_img.size(); i++) {

                        DoramaThumbModel doramaThumbModel = new DoramaThumbModel(doramas_titles.get(i).text(),doramas_img.get(i).attr("src"),doramas_links.get(i).attr("href"));
                        //Log.d(TAG,doramaThumbModel.toString());

                        Comun.list_doramas_thumbs.add(doramaThumbModel);
                    }

                    //Log.d(TAG, "size: "+Comun.list_doramas_thumbs.size());

                    fillGrid();
                }
                catch (Exception e)
                {
                    new MaterialDialog.Builder(LastSeriesActivity.this)
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
                progressInicio.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button)findViewById(view.getId()) ;
        String pagina = btn.getText().toString();
        Comun.list_doramas_thumbs.clear();
        gridView.setAdapter(null);
        for (PageModel page: pageArr) {
            if(!(btn.getText().toString().compareToIgnoreCase("1") ==0)){

                if (page.getName().compareToIgnoreCase(pagina)==0){
                    Toast.makeText(LastSeriesActivity.this, page.getUrl(), Toast.LENGTH_SHORT).show();
                    try {

                        getLastChapters(page.getUrl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                try {

                    getLastChapters(main_url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
