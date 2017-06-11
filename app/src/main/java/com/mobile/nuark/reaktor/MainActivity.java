package com.mobile.nuark.reaktor;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String lastpage = "0";
    public static String currentpage = "0";
    private static Button gbks;
    Activity act = this;
    RecyclerView lv;
    RecyclerView.LayoutManager mLayoutManager;
    Button gbk, gfw;
    TextView pagesShw;
    LinearLayout mainContent, loadingNotification, navBar;
    public static String url = "http://pornreactor.cc/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (RecyclerView) findViewById(R.id.lvapp);
        lv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(mLayoutManager);
        loadingNotification = (LinearLayout) findViewById(R.id.loadingNotofiaction);
        navBar = (LinearLayout) findViewById(R.id.navigationBar);
        gbk = (Button) findViewById(R.id.navGOBACK);
        gbks = gbk;
        gfw = (Button) findViewById(R.id.navGOFORW);
        pagesShw = (TextView) findViewById(R.id.pagesShw);
        pagesShw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(act, "Direct offsetting not implemented yet 3:", Toast.LENGTH_SHORT).show();
            }
        });
        mainContent = (LinearLayout) findViewById(R.id.mainContent);
        contentLoader();
        p_comparer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    void contentLoader(){
        new ImageListLoader(lv, act, loadingNotification, mainContent, pagesShw).execute();
    }

    public static String getLastpage() {
        return lastpage;
    }

    public static void setLastpage(String _lastpage) {
        if (lastpage == "0") lastpage = _lastpage;
        currentpage = _lastpage;
    }

    public static String getCurrentpage() {
        return currentpage;
    }

    public static String getUrl() {
        return url;
    }

    public void navigationHandler(View view) {
        p_comparer();
        int tmp = Integer.parseInt(currentpage);
        switch (view.getId()){
            case R.id.navGOFORW:
                tmp--;
                currentpage = String.valueOf(tmp);
                new ImageListLoader(lv, act, loadingNotification, mainContent, pagesShw, currentpage).execute();
                break;
            case R.id.navGOBACK:
                tmp++;
                currentpage = String.valueOf(tmp);
                new ImageListLoader(lv, act, loadingNotification, mainContent, pagesShw, currentpage).execute();
                break;
        }
    }

    public static void p_comparer(){
        if (gbks != null)
            if (Integer.parseInt(lastpage) == Integer.parseInt(currentpage)) gbks.setVisibility(View.GONE);
            else gbks.setVisibility(View.VISIBLE);
    }

    public void menuHandler(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresher_m:
                contentLoader();
                break;
            case R.id.refresher_fromnow_m:
                new ImageListLoader(lv, act, loadingNotification, mainContent, pagesShw, currentpage).execute();
                break;
            case R.id.clearer_m:
                Ion.getDefault(act).getCache().clear();
                Toast.makeText(act, "Кэш изображений очищен!\nПерезапустите приложение", Toast.LENGTH_SHORT).show();
                contentLoader();
                break;
        }
    }

    public void menuTagsHandler(MenuItem item) {
        url = "http://pornreactor.cc/";
        lastpage = "0";
        switch (item.getItemId()){
            case R.id.normal_t:
                url = "http://pornreactor.cc/";
                break;
            case R.id.photoporn_t:
                url = url + "/tag/photo+porn";
                break;
            case R.id.r34_t:
                url = url + "/tag/r34";
                break;
            case R.id.yiff_t:
                url = url + "/tag/yiff";
                break;
            case R.id.pornart_t:
                url = url + "/tag/porn+art";
                break;
            case R.id.oralporn_t:
                url = url + "/tag/oral+porn";
                break;
            case R.id.vaginalporn_t:
                url = url + "/tag/vaginal+porn";
                break;
            case R.id.analporn_t:
                url = url + "/tag/anal+porn";
                break;
            case R.id.toysporn_t:
                url = url + "/tag/toys+porn";
                break;
            case R.id.groupporn_t:
                url = url + "/tag/group+porn";
                break;
            case R.id.lesbiansporn_t:
                url = url + "/tag/lesbian+porn";
                break;
            case R.id.soloporn_t:
                url = url + "/tag/solo+porn";
                break;
            case R.id.sperm_t:
                url = url + "/tag/sperm";
                break;
            case R.id.handjobporn_t:
                url = url + "/tag/handjob+porn";
                break;
            case R.id.mlpporn_t:
                url = url + "/tag/mlp+porn";
                break;
            case R.id.lolporn_t:
                url = url + "/tag/lol+porn";
                break;
            case R.id.overwatchporn_t:
                url = url + "/tag/overwatch+porn";
                break;
            case R.id.vnporn_t:
                url = url + "/tag/vn+porn";
                break;/*
            case R.id.hentai_t:
                url = url + "/tag/хентай";
                break;
            case R.id.loliconhentai_t:
                url = url + "/tag/lolicon";
                break;
            case R.id.masturbationhentai_t:
                url = url + "/tag/masturbation+хентай";
                break;
            case R.id.exotichentai_t:
                url = url + "/tag/exotic+хентай";
                break;
            case R.id.demonstrationhentai_t:
                url = url + "/tag/demonstration+хентай";
                break;*/
        }
        contentLoader();
    }
}
