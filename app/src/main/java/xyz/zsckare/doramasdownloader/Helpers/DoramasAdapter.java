package xyz.zsckare.doramasdownloader.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import xyz.zsckare.doramasdownloader.MainActivity;
import xyz.zsckare.doramasdownloader.R;
import xyz.zsckare.doramasdownloader.Views.HomeActivity;


public class DoramasAdapter  extends ArrayAdapter<String> {
    public DoramasAdapter(Context context, LinkedList<String> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.custom_list_dorama,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.img_dorama);
        TextView name = (TextView) convertView.findViewById(R.id.dorama_title);

        // Lead actual.
        //Lead lead = getItem(position);
        String img = HomeActivity.list_img_urls.get(position);
        String title = HomeActivity.list_chapters_name.get(position);
        // Setup.
        Picasso.with(getContext()).load(img).into(avatar);
       // Glide.with(getContext()).load(lead.getImage()).into(avatar);
        name.setText(title);

        return convertView;
    }
}