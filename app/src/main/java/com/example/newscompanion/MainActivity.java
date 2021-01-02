 package com.example.newscompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

 public class MainActivity extends AppCompatActivity {

    RecyclerView newsRecycler;
    JSONArray articles;
    NewsAdapter newsAdapter;
    String[] commandsTitles, commandsDescs, commandsSpeaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("News");

        commandsTitles = getResources().getStringArray(R.array.commands_titles);
        commandsDescs = getResources().getStringArray(R.array.commands_descs);
        commandsSpeaks = getResources().getStringArray(R.array.commands_speaks);

        // Load commands list on start
        articles = createCommandCards();

        // Set the RecyclerView and adapter
        newsRecycler = findViewById(R.id.newsList);
        newsAdapter = new NewsAdapter(this, articles);
        newsRecycler.setAdapter(newsAdapter);
        newsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Alan button
        AlanButton alanButton = findViewById(R.id.alan_button);

        // Alan config object
        AlanConfig alanConfig = AlanConfig.builder()
                .setProjectId("c3975d3367b66ed3349f8ed1b7df1c762e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(alanConfig);

        AlanCallback myCallback = new AlanCallback() {
            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                //Handle command here
                try {
                    JSONObject apiData = eventCommand.getData();
                    JSONObject data = apiData.getJSONObject("data");
                    String command = data.getString("command");
                    if(command.equals("newHeadlines")){
                        articles = data.getJSONArray("articles");
                        if(articles.length() == 0)
                            articles = createCommandCards();
                        newsAdapter.setArticles(articles);
                        newsAdapter.notifyDataSetChanged();
                    } else if(command.equals("open")){
                        JSONObject article = data.getJSONObject("article");
                        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                        intent.putExtra("url", article.getString("url"));
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        alanButton.registerCallback(myCallback);
    }

    public JSONArray createCommandCards(){
        JSONArray jsonArray = new JSONArray();

        JSONObject latestNews = new JSONObject();
        JSONObject newsByCategories = new JSONObject();
        JSONObject newsByTerm = new JSONObject();
        JSONObject newsBySource = new JSONObject();

        try{
            latestNews.put("commandName", commandsTitles[0]);
            latestNews.put("commandDesc", commandsDescs[0]);
            latestNews.put("commandSpeak", commandsSpeaks[0]);

            newsByCategories.put("commandName", commandsTitles[1]);
            newsByCategories.put("commandDesc", commandsDescs[1]);
            newsByCategories.put("commandSpeak", commandsSpeaks[1]);

            newsByTerm.put("commandName", commandsTitles[2]);
            newsByTerm.put("commandDesc", commandsDescs[2]);
            newsByTerm.put("commandSpeak", commandsSpeaks[2]);

            newsBySource.put("commandName", commandsTitles[3]);
            newsBySource.put("commandDesc", commandsDescs[3]);
            newsBySource.put("commandSpeak", commandsSpeaks[3]);
        } catch(JSONException ex){
            ex.printStackTrace();
        }

        jsonArray.put(latestNews);
        jsonArray.put(newsByCategories);
        jsonArray.put(newsByTerm);
        jsonArray.put(newsBySource);

        return jsonArray;
    }
}