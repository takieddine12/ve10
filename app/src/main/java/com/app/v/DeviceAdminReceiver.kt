package com.app.v

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class DeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
    }
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return "admin_receiver_status_disable_warning"
    }
    override fun onDisabled(context: Context, intent: Intent) {}
}