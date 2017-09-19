package io.github.h4mu.appprotec;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AppDbHelper appDb;
    private ArrayList<HashMap<String, String>> apps = new ArrayList<>();
    private ArrayList<HashMap<String, String>> removedApps = new ArrayList<>();
    private static final String COLUMN_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        appDb = new AppDbHelper(this);
        HashMap<String, HashMap<String, String>> trustedApps = appDb.getAllApps();

        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : installedApplications) {
            if (appInfo.enabled && (appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 0) {
                HashMap<String, String> row = trustedApps.get(appInfo.packageName);
                if (row == null) {
                    row = new HashMap<>();
                    row.put(AppDbHelper.COLUMN_PACKAGE, appInfo.packageName);
                    row.put(AppDbHelper.COLUMN_NAME, appInfo.loadLabel(getPackageManager()).toString());
                    row.put(COLUMN_STATUS, getString(R.string.appInstalledNotTrusted));
                } else {
                    row.put(COLUMN_STATUS, getString(R.string.appInstalledTrusted));
                }
                apps.add(row);
                trustedApps.remove(appInfo.packageName);
            }
        }

//        for (HashMap<String, String> row : trustedApps.values()) {
//            row.put(COLUMN_STATUS, getString(R.string.appNotInstalledTrusted));
//            apps.add(row);
//        }

        ListView listView = (ListView) findViewById(R.id.packageList);
        listView.setAdapter(new SimpleAdapter(this, apps, android.R.layout.simple_list_item_2,
                new String[]{AppDbHelper.COLUMN_NAME, COLUMN_STATUS}, new int[]{android.R.id.text1, android.R.id.text2}));
        listView.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        apps.clear();
        HashMap<String, HashMap<String, String>> trustedApps = appDb.getAllApps();

        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : installedApplications) {
            if (appInfo.enabled && (appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 0) {
                HashMap<String, String> row = trustedApps.get(appInfo.packageName);
                if (row == null) {
                    row = new HashMap<>();
                    row.put(AppDbHelper.COLUMN_PACKAGE, appInfo.packageName);
                    row.put(AppDbHelper.COLUMN_NAME, appInfo.loadLabel(getPackageManager()).toString());
                    row.put(COLUMN_STATUS, getString(R.string.appInstalledNotTrusted));
                } else {
                    row.put(COLUMN_STATUS, getString(R.string.appInstalledTrusted));
                }
                apps.add(row);
                trustedApps.remove(appInfo.packageName);
            }
        }

        ListView listView = (ListView) findViewById(R.id.packageList);
        listView.invalidateViews();
    }

    public void onTrustAllAppsClicked(View view) {
        for (HashMap<String, String> app : apps) {
            if (getString(R.string.appInstalledNotTrusted).equals(app.get(COLUMN_STATUS))) {
                app.put(COLUMN_STATUS, getString(R.string.appInstalledTrusted));
            }
        }
        ListView listView = (ListView) findViewById(R.id.packageList);
        listView.invalidateViews();
    }

    public void onUntrustAllAppsClicked(View view) {
        for (HashMap<String, String> app : apps) {
            String status = app.get(COLUMN_STATUS);
            if (getString(R.string.appInstalledTrusted).equals(status)) {
                app.put(COLUMN_STATUS, getString(R.string.appInstalledNotTrusted));
            } else if (getString(R.string.appNotInstalledTrusted).equals(status)) {
                removedApps.add(app);
            }
        }
        apps.removeAll(removedApps);
        ListView listView = (ListView) findViewById(R.id.packageList);
        listView.invalidateViews();
    }

    public void onSaveWhiteListClicked(View view) {
        for (HashMap<String, String> app : apps) {
            String status = app.get(COLUMN_STATUS);
            if (getString(R.string.appInstalledTrusted).equals(status) || getString(R.string.appNotInstalledTrusted).equals(status)) {
                appDb.replaceApp(app.get(AppDbHelper.COLUMN_NAME), app.get(AppDbHelper.COLUMN_PACKAGE));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(ApplicationsActivity.this, AppDetailActivity.class);
        intent.putExtra(AppDbHelper.COLUMN_PACKAGE, apps.get(i).get(AppDbHelper.COLUMN_PACKAGE));
        startActivity(intent);
    }
}
