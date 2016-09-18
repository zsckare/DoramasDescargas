package xyz.zsckare.doramasdownloader.Helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import xyz.zsckare.doramasdownloader.Comun;
import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.R;
import xyz.zsckare.doramasdownloader.Views.HomeActivity;


public class DoramaGridAdapter  extends ArrayAdapter<DoramaThumbModel> {
    public DoramaGridAdapter(Context context, LinkedList<DoramaThumbModel> objects) {
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
                    R.layout.grid_item,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.grid_img_dorama);
        TextView name = (TextView) convertView.findViewById(R.id.name_dorama);
        if (!Comun.list_doramas_thumbs.isEmpty()) {
            String img = Comun.list_doramas_thumbs.get(position).getImg();

            String title = Comun.list_doramas_thumbs.get(position).getName();
            //Log.d("Adapter", "getView: --"+title+"----"+img);
            // Setup.
            Picasso.with(getContext()).load(img).into(avatar);
            // Glide.with(getContext()).load(lead.getImage()).into(avatar);
            name.setText(title);
        }
        return convertView;
    }
}