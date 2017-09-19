package io.github.h4mu.appprotec;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class AppDetailActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private AppDbHelper appDb;
    private ApplicationInfo applicationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        appDb = new AppDbHelper(this);

        String packageName = getIntent().getStringExtra(AppDbHelper.COLUMN_PACKAGE);

        try {
            applicationInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        ((TextView) findViewById(R.id.nameText)).setText(applicationInfo.loadLabel(getPackageManager()));
        ((TextView) findViewById(R.id.packageText)).setText(packageName);
        ((TextView) findViewById(R.id.appDescriptionText)).setText(applicationInfo.loadDescription(getPackageManager()));
        ToggleButton trustedToggle = (ToggleButton) findViewById(R.id.trustedSwitch);
        trustedToggle.setChecked(appDb.isTrusted(packageName));
        trustedToggle.setOnCheckedChangeListener(this);
    }

    public void onRemoveAppClicked(View view) {
        if (applicationInfo != null) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", applicationInfo.packageName, null));
            startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (applicationInfo != null) {
            if (b) {
                CharSequence label = applicationInfo.loadLabel(getPackageManager());
                appDb.replaceApp(label != null ? label.toString() : applicationInfo.packageName, applicationInfo.packageName);
            } else {
                // FIXME: 2017. 09. 19.
                appDb.deleteApp(applicationInfo.packageName);
            }
        }
    }
}
