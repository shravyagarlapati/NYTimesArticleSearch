package com.shravyagarlapati.android.nytimessearch;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shravyagarlapati on 7/27/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles)
    {
        super(context,android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);

        //Check if existing view is being recycled, else inflate layout
        if(convertView==null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_layout, parent, false);
        }
        ImageView imageView= (ImageView) convertView.findViewById(R.id.ivImage);
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadLine());

        String thumbNail = article.getThumbNail();
        Log.d("Print thumbnail",thumbNail);

        if(!TextUtils.isEmpty(thumbNail)) {
            Picasso.with(getContext()).load(thumbNail).error(R.drawable.oops).into(imageView);
        }

        return convertView;

    }
}
