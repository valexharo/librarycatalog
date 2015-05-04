package com.ucuenca.ucmobile.librarycatalog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Usuario on 29/04/15.
 */
public class ItemBookAdapter extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<ItemBook> items;

    public ItemBookAdapter(Activity activity, ArrayList<ItemBook> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear()
    {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

   /* @Override
    public String getItemId(int position) {
        return items.get(position).getUri();
    }*/

    public void add(ItemBook item){
        items.add(item);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View vi=contentView;

        if(contentView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.list_item_forecast, null);
        }

        ItemBook item = items.get(position);

        //ImageView image = (ImageView) vi.findViewById(R.id.imagen);
        //int imageResource = activity.getResources().getIdentifier(item.getRutaImagen(), null, activity.getPackageName());
        //image.setImageDrawable(activity.getResources().getDrawable(imageResource));

        TextView titulo = (TextView) vi.findViewById(R.id.list_item_forecast_textview);
        titulo.setText(item.getTitulo());

        TextView autor = (TextView) vi.findViewById(R.id.list_item_autor);
        autor.setText(item.getAutor());

        return vi;
    }
}
