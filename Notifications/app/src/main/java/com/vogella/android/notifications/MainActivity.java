package com.vogella.android.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {
    private int notificationID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button notificationButton = (Button) findViewById(R.id.button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotification();
            }
        });

        Button alarmButton = (Button) findViewById(R.id.alarm);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });
        Button cancelAlarmButton = (Button) findViewById(R.id.cancel);
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }

    protected void displayNotification() {
        Notifier notifier = new Notifier();
        notifier.createNotification(this);
    }

    protected void setAlarm() {
        Alarm alarm = new Alarm();
        alarm.setAlarm(this);
    }
    protected void cancelAlarm() {
        Alarm alarm = new Alarm();
        alarm.cancelAlarm(this);
    }

}
