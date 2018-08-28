package com.space.blue.bluespace;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.space.blue.bluespace.MainActivity.openApp;

public class AppGrid extends AppCompatActivity {

    private RecyclerView list;
    private ArrayList<ResolveInfo> apps = new ArrayList<ResolveInfo>();
    private static PackageManager manager;
    LinearLayout appDrawerLayout;
    LovelyProgressDialog loadAppDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_grid);

        appDrawerLayout = findViewById(R.id.app_drawer_layout);
        loadAppDialog = new LovelyProgressDialog(this);

        list = findViewById(R.id.list);

        long t0 = System.currentTimeMillis();
        new LoadApps().execute();
        Log.i("PLUM",String.valueOf((System.currentTimeMillis()-t0)));

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addClickListener() {

        list.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                openApp(AppGrid.this, apps.get(position).activityInfo.packageName);

            }


        }));
    }

    private void getAppsList(){

        manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        ArrayList<ResolveInfo> availableActivities = (ArrayList<ResolveInfo>) manager.queryIntentActivities(i,0);
        apps = availableActivities;
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo r1, ResolveInfo r2) {
                return r1.activityInfo.loadLabel(manager).toString().compareToIgnoreCase(r2.loadLabel(manager).toString());
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        PowerManager p = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if(!p.isInteractive()){System.gc();}}else {if(!p.isScreenOn()){System.gc();}}}
    public static PackageManager getPM(){
        return manager;
    }

    class LoadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadAppDialog.setTitle("Loading Apps")
                    .setTopColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setIcon(getResources().getDrawable(R.drawable.dial_icon))
                    .setIconTintColor(getResources().getColor(R.color.appBarColor))
                    .setMessage("Your Apps are being sorted in alphabetical order, Be Calm")
                    .show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AppListAdaptor adaptor = new AppListAdaptor(apps);
            list.setAdapter(adaptor);
            GridLayoutManager glm = new GridLayoutManager(AppGrid.this, 3);
            list.setLayoutManager(glm);
            addClickListener();
            loadAppDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getAppsList();
            return null;
        }
    }

}
