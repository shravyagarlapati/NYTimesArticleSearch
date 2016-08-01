package com.shravyagarlapati.android.nytimessearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shravyagarlapati on 7/27/16.
 */
public class Article implements Serializable {

    String webUrl;
    String thumbNail;
    String headLine;

    String pubDate;
    String newsDesk;

    public String getWebUrl() {
        return webUrl;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getPubDate() { return  pubDate; }

    public String getNewsDesk() { return newsDesk; }


    public Article(JSONObject jsonObject)
    {
        try
        {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");
            this.pubDate = jsonObject.getString("pub_date");
            this.newsDesk = jsonObject.getString("news_desk");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() >0)
            {
                JSONObject multimediajson = multimedia.getJSONObject(0);
                this.thumbNail = "http://nytimes.com/" + multimediajson.getString("url");
            }
            else
                this.thumbNail = "";
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array)
    {
        ArrayList<Article> results = new ArrayList<>();

        for(int i=0;i< array.length();i++)
        {
            try
            {
                results.add(new Article(array.getJSONObject(i)));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return results;
    }
}
