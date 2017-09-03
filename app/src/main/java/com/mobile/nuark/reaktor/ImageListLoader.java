package com.mobile.nuark.reaktor;

/**
 * Created by Nuark with love on 21.05.2017.
 * Protected by QPL-1.0
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ImageListLoader extends AsyncTask<Object, Void, Object> {

    String offset = "0";
    Document doc;
    RecyclerView lv;
    Activity act;
    TextView pagesShw;
    LinearLayout mainContent, loadingNotification;
    ArrayList<MPost> imgs = new ArrayList<>();
    String url;
    RecyclerView.Adapter lv_adptr;

    public ImageListLoader(RecyclerView lv, Activity act, LinearLayout loadingNotification, LinearLayout mainContent, TextView pagesShw)
    {
        this.url = MainActivity.getUrl();
        this.lv = lv;
        this.act = act;
        this.loadingNotification = loadingNotification;
        this.mainContent = mainContent;
        this.pagesShw = pagesShw;
    }

    public ImageListLoader(RecyclerView lv, Activity act, LinearLayout loadingNotification, LinearLayout mainContent, TextView pagesShw, String offset)
    {
        this.url = MainActivity.getUrl() + "/";
        this.lv = lv;
        this.act = act;
        this.loadingNotification = loadingNotification;
        this.mainContent = mainContent;
        this.pagesShw = pagesShw;
        this.offset = offset;
        url = url + offset;
    }

    @Override
    protected void onPreExecute() {
        loadingNotification.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... p1)
    {
        try {
            doc = Jsoup.connect(url).ignoreContentType(true).followRedirects(true).get();
            MainActivity.setLastpage(doc.select("div.pagination_expanded span.current").first().text());
            for (Element el : doc.select("div.postContainer")) {
                String author = el.select("div.uhead_nick a").first().text();
                for (Element _el : el.select("div.post_content img")){
                    imgs.add(new MPost(author, _el.attr("src")));
                }
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error\n" + e.getLocalizedMessage();
        }
    }

    @Override
    protected void onPostExecute(Object result)
    {
        MainActivity.p_comparer();
        String tmp = MainActivity.getCurrentpage() + "/" + MainActivity.getLastpage();
        pagesShw.setText(tmp);
        lv.setAdapter(new AppAdapter(act, imgs));
        loadingNotification.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        if (result.toString().contains("error")){
            Toast.makeText(act, result.toString().replace("error", "Ошибка!"), Toast.LENGTH_LONG).show();
        } else if (result.toString().contains("warning")){
            Toast.makeText(act, result.toString().replace("warning", "Предупреждение!"), Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }
}
