package io.github.h4mu.appprotec;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class InstallBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri intentData = intent.getData();
        if (intentData != null && intentData.getSchemeSpecificPart() != null && !new AppDbHelper(context).isTrusted(intentData.getSchemeSpecificPart())) {
            Intent resultIntent = new Intent(context, AppDetailActivity.class);
            resultIntent.putExtra(AppDbHelper.COLUMN_PACKAGE, intent.getPackage());
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.appInstalled))
                    .setContentText(context.getString(R.string.untrustedAppFound, intent.getPackage()))
                    .setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(1, builder.build());
        }
    }
}
