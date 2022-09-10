package com.mandeep.chatapplication2.MyService

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService :NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
        Log.d("fidngfd",sbn?.notification?.tickerText.toString())
        Log.d("fidngfd",sbn?.packageName.toString())
        Log.d("fidngfd",sbn?.notification?.tickerText.toString())
        Log.d("fidngfd",sbn?.notification?.tickerText.toString())

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d("fidngfd",sbn?.notification?.tickerText.toString())

    }
}