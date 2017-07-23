package com.example.apple.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by justin on 2017/7/21.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> News) {
        super(context, 0, News);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder = new ViewHolder();
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
            holder.titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
            holder.authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
            holder.dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);

            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        News currentNews = getItem(position);
        holder.titleTextView.setText(currentNews.getTitle());
        holder.authorTextView.setText(currentNews.getAuthor());
        holder.dateTextView.setText(currentNews.getDate());

        return listItemView;
    }

    private class ViewHolder{
        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;
    }
}
