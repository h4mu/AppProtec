package io.github.h4mu.appprotec;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onApplicationsButtonClick(View view) {
        startActivity(new Intent(this, ApplicationsActivity.class));
    }

    public void onSettingsButtonClick(View view) {
    }

    public void onLogsButtonClick(View view) {
        startActivity(new Intent(this, LogsActivity.class));
    }

    public void onLicenseButtonClick(View view) {
        startActivity(new Intent(this, LicenseActivity.class));
    }
}

