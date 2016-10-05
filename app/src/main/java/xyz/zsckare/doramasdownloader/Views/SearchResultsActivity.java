package xyz.zsckare.doramasdownloader.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import xyz.zsckare.doramasdownloader.R;

public class SearchResultsActivity extends AppCompatActivity {

    public static String params = "";
    String TAG = getClass().getSimpleName();
    public static LinkedList<String>az_names_lowercase = new LinkedList<>();
    static LinkedList<String> resultsName = new LinkedList<>();
    static LinkedList<String> resultsUrl = new LinkedList<>();

    ListView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        result = (ListView)findViewById(R.id.listResults);

        search();


        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchResultsActivity.this, resultsUrl.get(i), Toast.LENGTH_SHORT).show();
                getInfo(resultsUrl.get(i));
            }
        });


    }
    public void getInfo(String url){
        //Toast.makeText(AllSeriesActivity.this, url, Toast.LENGTH_SHORT).show();
        DoramaActivity.full_info = 1;
        DoramaActivity.dorama_url = url;

        startActivity(new Intent(this, DoramaActivity.class));
    }

    private void search() {

        if (!az_names_lowercase.isEmpty()){
            resultsName.clear();
            resultsUrl.clear();
            az_names_lowercase.clear();
        }

        for (String serie:AllSeriesActivity.az_names) {
            az_names_lowercase.add(serie.toLowerCase());
        }

        Log.d(TAG, "search: "+params);
        Log.d(TAG, "search: ---->"+AllSeriesActivity.az_names.size());
        for (int i = 0; i <az_names_lowercase.size() ; i++) {
            if (az_names_lowercase.get(i).contains(params.toLowerCase())){
                Log.d(TAG, "search: OK"+i);
                Log.d(TAG, "search: "+AllSeriesActivity.az_names.get(i));
                resultsName.add(AllSeriesActivity.az_names.get(i));
                resultsUrl.add(AllSeriesActivity.az_urls.get(i));
            }
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, resultsName);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                result.setAdapter(null);
                result.setAdapter(adapter);
            }
        });

    }


}
