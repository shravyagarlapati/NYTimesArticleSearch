package com.shravyagarlapati.android.nytimessearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shravyagarlapati.android.nytimessearch.AppStatusCheck;
import com.shravyagarlapati.android.nytimessearch.Article;
import com.shravyagarlapati.android.nytimessearch.ArticleArrayAdapter;
import com.shravyagarlapati.android.nytimessearch.EndlessScrollListener;
import com.shravyagarlapati.android.nytimessearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    //EditText etQuery;
    MenuItem searchItem;
    //Button btnSearch;
    GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    Toolbar toolbar;
    ImageButton filterBtn;
    RequestParams params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Checking the Internet connection
        if (AppStatusCheck.getInstance(this).isOnline()) {
            Toast.makeText(this,"U R ONLINE, Continue with Search.....",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"NO INTERNET BUMMER!!!!",Toast.LENGTH_SHORT).show();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //etQuery = (EditText) findViewById(R.id.etQuery);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        //Hook up onclick listener
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Create an intent to display activity
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                intent.putExtra("article",article);
                startActivity(intent);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener(){
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            // Triggered only when new data needs to be appended to the list
            loadData(gvResults,page, searchItem.toString());
            //Toast.makeText(getApplicationContext(), "Scolling Infintely ",
            //        Toast.LENGTH_SHORT).show();
            return true; // ONLY if more data is actually being loaded; false otherwise.
        }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == 200) {
//
//
//            // Extract name value from result extras
////            String code = data.getExtras().getString("Date");
////            String order = data.getExtras().getString("Order");
////            String newsDesk = data.getExtras().getString("NewsDesk");
////
////            formAdvancedQuery(date, newsDesk, order);
//        }
//
//    }
//
//    public void OnArticleSearch(View view) {
//        adapter.clear();
//        loadData(view,0);
//    }


    public void onClickFilterIcon(View view)
    {
        //Log.d("$$$$Intent created","ImageButton");
        filterBtn = (ImageButton) findViewById(R.id.filterBtn);

        Intent intent = new Intent(SearchActivity.this, FilterOptionsActivity.class);
        intent.putExtra("code",200);
        startActivityForResult(intent,200);
        
    }

    public void loadData(View view, int offset, String query)
    {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String date = mSettings.getString("DATE", "");
        String order = mSettings.getString("ORDER", "");
        String newsDesk = mSettings.getString("NEWSDESK","");

        formAdvancedQuery(date, newsDesk, order);

        //String query = etQuery.getText().toString();


        if(params==null)
            params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        params.put("api-key","94a90b535a584015ab49fda9102f0e81");
        params.put("page",offset);
        params.put("q",query);

        Log.d("Params",params.toString());

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("DEBUG", response.toString());

                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    //Log.d("Debug",articles.toString());
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void formAdvancedQuery(String date, String newsDesk, String order)
    {
        String formatDate = new String();
        String formatDesk = new String();

        //Form the advanced query and provide the filtering options.

        if(params==null) {
            params = new RequestParams();
        }

        if(newsDesk.length()>0)
        {
            String[] temp_news = newsDesk.split(":");
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<temp_news.length-1;i++)
            {
                //  (\"Sports\" \"Foreign\")
                buffer.append(temp_news[i]);
                buffer.append("\" \"");
            }
            buffer.append(temp_news[temp_news.length-1]);

            Log.d("News Desk",buffer.toString());
            formatDesk = String.format("news_desk:(" + "\"%s" + "\")",buffer.toString() );
            Log.d("Format DESK:",formatDesk);
        }

        if(date.length()>0)
        {
            formatDate = String.format("pub_date:(" + "\"%s" + "\")", date);
            Log.d("Format DATE:",formatDate);
            Log.d("Format Desk", ":" +formatDesk +":");
            if(formatDesk.length()>0 && formatDate.length()>0)
                params.put("fq", formatDate + " AND " + formatDesk);
            else if (formatDate.length()>0 && formatDesk.length()==0)
                params.put("fq", formatDate);
        }
        else
        {
            if(formatDesk.length()>0)
                params.put("fq", formatDesk);
        }

        if(order.length()>0)
            params.put("sort", order);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        searchItem = menu.findItem(R.id.action_search);
        Log.d("String Query", searchItem.toString());
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
       @Override
       public boolean onQueryTextSubmit(String query) {
            // perform query here

            // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
            // see https://code.google.com/p/android/issues/detail?id=24599
            adapter.clear();
            loadData(searchView,0, query);
            searchView.clearFocus();

            return true;
       }

       @Override
       public boolean onQueryTextChange(String newText) {
           return false;
       }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
