package com.app.v

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar

object Utils {

    private  var alarmManager : AlarmManager? = null
    private var pendingIntent : PendingIntent? = null
    fun initAlarmManager(context : Context){
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    fun fireAlarmManager(context: Context,minutes : Int){
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }

        pendingIntent = PendingIntent.getActivity(context, 0,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes)

        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent!!)
    }
    fun cancelAlarManager(){
        pendingIntent?.let {
            alarmManager?.cancel(it)
        }
    }
}