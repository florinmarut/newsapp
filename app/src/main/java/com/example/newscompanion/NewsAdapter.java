package com.example.newscompanion;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private JSONArray articles;

    public NewsAdapter(Context context, JSONArray articles){
        this.context = context;
        this.articles = articles;
    }

    public void setArticles(JSONArray articles){
        this.articles = articles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if(viewType == 1) {
            view = inflater.inflate(R.layout.news_row, parent, false);
            return new NewsViewHolder(view);
        }else{
            view = inflater.inflate(R.layout.command_row, parent, false);
            return new CommandsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            JSONObject article = article = articles.getJSONObject(position);

            if(getItemViewType(position) == 0) {
                ((CommandsViewHolder) holder).name.setText(article.getString("commandName"));
                ((CommandsViewHolder) holder).desc.setText(article.getString("commandDesc"));
                ((CommandsViewHolder) holder).speak.setText(article.getString("commandSpeak"));
            }else{

                if (article != null) {
                    try {
                        ((NewsViewHolder) holder).source.setText(article.getJSONObject("source").getString("name"));
                        ((NewsViewHolder) holder).id.setText(String.valueOf(position+1));
                        ((NewsViewHolder) holder).title.setText(article.getString("title"));
                        ((NewsViewHolder) holder).desc.setText(article.getString("description"));
                        Picasso.get().load(article.getString("urlToImage")).into(((NewsViewHolder) holder).image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(articles != null)
            return articles.length() == 4 ? 0 : 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.length();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView source, title, desc, id;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.newsSource);
            id = itemView.findViewById(R.id.newsId);
            title = itemView.findViewById(R.id.newsTitle);
            desc = itemView.findViewById(R.id.newsDesc);
            image = itemView.findViewById(R.id.newsImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), WebActivity.class);
                    try {
                        intent.putExtra("url", articles.getJSONObject(getAdapterPosition()).getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public class CommandsViewHolder extends RecyclerView.ViewHolder{

        TextView name, desc, speak;

        public CommandsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.commandName);
            desc = itemView.findViewById(R.id.commandDesc);
            speak = itemView.findViewById(R.id.commandSpeak);
        }
    }
}
