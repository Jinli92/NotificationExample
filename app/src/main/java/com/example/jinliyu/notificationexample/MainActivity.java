package com.example.jinliyu.notificationexample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_REPLY = "NotificationReply";
    public static final String CHANNNEL_ID = "SimplifiedCodingChannel";
    public static final String CHANNEL_NAME = "SimplifiedCodingChannel";
    public static final String CHANNEL_DESC = "This is a channel for Simplified Coding Notifications";

    public static final String KEY_INTENT_MORE = "keyintentmore";
    public static final String KEY_INTENT_HELP = "keyintenthelp";

    public static final int REQUEST_CODE_MORE = 100;
    public static final int REQUEST_CODE_HELP = 101;
    public static final int NOTIFICATION_ID = 200;

    private NotificationManager notificationManager;
    private Button btnBundleNotification, btnSingleNotification;
    String bundle_notification_id = "1";

    int bundleNotificationId = 100;
    int singleNotificationId = 100;
    NotificationCompat.Builder summaryNotificationBuilder;

    Button channelbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESC);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        findViewById(R.id.buttonCreateNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotification();
            }
        });
        channelbtn = findViewById(R.id.channelbtn);
        channelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addnotification();
            }
        });
    }



    public void addnotification(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        bundle_notification_id = "bundle_notification_" + bundleNotificationId;

        Intent resultIntent = new Intent(this, MainActivity.class);

        resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notification", "Summary Notification Clicked");
        resultIntent.putExtra("notification_id", bundleNotificationId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //We need to update the bundle notification every time a new notification comes up.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannels().size() < 2) {
                NotificationChannel groupChannel = new NotificationChannel("bundle_channel_id", "bundle_channel_name", NotificationManager.IMPORTANCE_LOW);
                groupChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(groupChannel);
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(true);
                notificationManager.createNotificationChannel(channel);
            }
        }
        summaryNotificationBuilder = new NotificationCompat.Builder(this, "bundle_channel_id")
                .setGroup(bundle_notification_id)
                .setGroupSummary(true)
                .setContentTitle("Bundled Notification ")
                .setContentText("Summary-------")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent);



        if (singleNotificationId == bundleNotificationId)
            singleNotificationId = bundleNotificationId + 1;
        else
            singleNotificationId++;

        resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notification", "Bundle notification clicked");
        resultIntent.putExtra("notification_id", singleNotificationId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channel_id")
                .setGroup(bundle_notification_id)
                .setContentTitle("Bundle Notification ")
                .setContentText("Content for the notification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroupSummary(false)
                .setContentIntent(resultPendingIntent);

        notificationManager.notify(singleNotificationId, notification.build());
        notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build());


    }


    public void displayNotification() {

        //Pending intent for a notification button named More
        PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                MainActivity.this,
                REQUEST_CODE_MORE,
                new Intent(MainActivity.this, NotificationReceiver.class)
                        .putExtra(KEY_INTENT_MORE, REQUEST_CODE_MORE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Pending intent for a notification button help
        PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this,
                REQUEST_CODE_HELP,
                new Intent(MainActivity.this, NotificationReceiver.class)
                        .putExtra(KEY_INTENT_HELP, REQUEST_CODE_HELP),
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        //We need this object for getting direct input from notification
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
                .setLabel("Please enter your name")
                .build();


        //For the remote input we need this action object
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.arrow_down_float,
                        "Reply Now...", helpPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        //Creating the notification builder object
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Notification...")
                .setContentText("Please share your name with us")
                .setAutoCancel(true)
                .setBadgeIconType(R.drawable.ic_launcher_background)
                .setContentIntent(helpPendingIntent)
                .addAction(action)
                .addAction(android.R.drawable.ic_menu_compass, "More", morePendingIntent)
                .addAction(android.R.drawable.ic_menu_directions, "Help", helpPendingIntent);


        //finally displaying the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
