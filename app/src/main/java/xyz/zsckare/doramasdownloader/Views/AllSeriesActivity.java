package xyz.zsckare.doramasdownloader.Views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import xyz.zsckare.doramasdownloader.R;

public class AllSeriesActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();
    public static LinkedList<String>az_names = new LinkedList<>();
    public static LinkedList<String>az_urls = new LinkedList<>();
RecyclerView recyclerView;
    FastScroller fastScroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_series);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CountriesAdapter(this));

        //has to be called AFTER RecyclerView.setAdapter()
        fastScroller.setRecyclerView(recyclerView);

        Toast.makeText(getApplicationContext(), "Visita estrenosdoramas.org/", Toast.LENGTH_SHORT).show();

    }


    public void getInfo(String url){
        //Toast.makeText(AllSeriesActivity.this, url, Toast.LENGTH_SHORT).show();
        DoramaActivity.full_info = 1;
        DoramaActivity.dorama_url = url;

        startActivity(new Intent(this, DoramaActivity.class));
    }

    public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesHolder> implements SectionTitleProvider {

        private boolean isHorizontal = false;
        private List<String> countries;
        private LayoutInflater inflater;

        public CountriesAdapter(Context cxt) {
            //countries = Arrays.asList(cxt.getResources().getStringArray(R.array.countries_array));
            inflater = LayoutInflater.from(cxt);
        }

        public CountriesAdapter(Context cxt, boolean isHorizontal) {
            this(cxt);
            this.isHorizontal = isHorizontal;
        }


        @Override
        public CountriesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CountriesHolder holder;
            View view = inflater.inflate(isHorizontal ? R.layout.item_country : R.layout.item_country, parent, false);
            holder = new CountriesHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CountriesHolder holder, final int position) {
            holder.nameTv.setText(az_names.get(position));

            holder.nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: "+holder.nameTv.getText()+"---------"+position);
                    Log.d(TAG, "onClick: -------------"+az_urls.get(position));

                    getInfo(az_urls.get(position));
                }
            });
        }



        private String getCountry(int position) {
            return az_names.get(position);
        }

        @Override
        public int getItemCount() {
            return az_names.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return getCountry(position).substring(0, 1);
        }

        class CountriesHolder extends RecyclerView.ViewHolder {

            public TextView nameTv;

            public CountriesHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView;

            }



        }

    }

}
