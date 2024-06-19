package com.app.v

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager : DevicePolicyManager
    private lateinit var button : Button
    private lateinit var text : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        button = findViewById(R.id.next)
        text = findViewById(R.id.userType)

        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (devicePolicyManager.isAdminActive(getComponent())) {
            text.text = "User is admin"
            button.text = "Already Admin"
        }
        else {
            text.text = "User is owner"
            button.text = "Become Admin"
        }

        button.setOnClickListener {
            if (button.text == "Become Admin"){
                requestDeviceAdmin()
                button.text == "Remove Admin"
            } else if (button.text == "Remove Admin") {
                moveToNextActivity()
                Toast.makeText(this,"Admin Role removed",Toast.LENGTH_LONG).show()
                //deactivateAdminRole()
            }
        }
        requestDeviceAdmin()
    }

    private fun deactivateAdminRole(){
        val devAdminReceiver =  ComponentName(this, DeviceAdminReceiver::class.java)
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        dpm.removeActiveAdmin(devAdminReceiver)
    }
    private fun getComponent() : ComponentName{
        return ComponentName(this, DeviceAdminReceiver::class.java)
    }
    private fun moveToNextActivity() {
        Intent(this,HomeActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private val deviceAdminLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            if (devicePolicyManager.isAdminActive(getComponent())) {
                moveToNextActivity()
            } else {
                Toast.makeText(this, "Device admin activation failed.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Device admin activation failed.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun requestDeviceAdmin() {
        val componentName = ComponentName(this, DeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app requires device admin privileges to function properly.")
        }

        deviceAdminLauncher.launch(intent)
    }


}