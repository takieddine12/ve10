package com.app.v

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class AppActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    private var selectedDuration = 0
    private val timeIntervals = arrayOf("1 min","15 min", "30 min", "45 min", "60 min", "75 min", "90 min", "120 min")
    private lateinit var numberPicker : NumberPicker
    private lateinit var biometricImage : ImageView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        biometricImage = findViewById(R.id.biometric)
        numberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 0
        numberPicker.maxValue = timeIntervals.size - 1
        numberPicker.displayedValues = timeIntervals
        numberPicker.wrapSelectorWheel = true
        numberPicker.textColor = Color.WHITE

        val btn0 = findViewById<TextView>(R.id.btn0)
        val btn1 = findViewById<TextView>(R.id.btn1)
        val btn2 = findViewById<TextView>(R.id.btn2)
        val btn3 = findViewById<TextView>(R.id.btn3)
        val btn4 = findViewById<TextView>(R.id.btn4)
        val btn5 = findViewById<TextView>(R.id.btn5)
        val btn6 = findViewById<TextView>(R.id.btn6)
        val btn7 = findViewById<TextView>(R.id.btn7)
        val btn8 = findViewById<TextView>(R.id.btn8)
        val btn9 = findViewById<TextView>(R.id.btn9)
        val tick = findViewById<ImageView>(R.id.tick)
        val edit = findViewById<EditText>(R.id.passCodeEdit)

        val passcodeBuilder = StringBuilder()
        val numberButtons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)

        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            Toast.makeText(this,"Selected : " + numberPicker.value,Toast.LENGTH_LONG).show()
            val selectedValueIndex = numberPicker.value
            val selectedInterval = timeIntervals[selectedValueIndex]
            val minutes: Int = convertIntervalToMinutes(selectedInterval)
            selectedDuration = minutes
            Log.d("TAG", "Selected Value $selectedDuration")
        }
        numberButtons.forEach { button ->
            button.setOnClickListener {
                passcodeBuilder.append(button.text)
                edit.setText(passcodeBuilder.toString())
            }
        }
        tick.setOnClickListener {
            val passcode = passcodeBuilder.toString()
            if (selectedDuration == 0){
                Toast.makeText(this,"Please select a duration",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (passcode == "1234") {
                edit.text.clear()
                stopLockTask()
                Utils.fireAlarmManager(this,selectedDuration)
                finishAffinity()
            }
            else {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        val greenColor = ContextCompat.getColor(this, R.color.greenColor)
        val colorFilter = PorterDuffColorFilter(greenColor, PorterDuff.Mode.SRC_IN)
        edit.compoundDrawablesRelative[2]?.colorFilter = colorFilter

        edit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = edit.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= edit.right - drawableEnd.bounds.width()) {
                    if (passcodeBuilder.isNotEmpty()) {
                        passcodeBuilder.deleteCharAt(passcodeBuilder.length - 1)
                        edit.setText(passcodeBuilder.toString())
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
        biometricImage.setOnClickListener {
            if(selectedDuration == 0){
                Toast.makeText(this,"Please select a duration",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            unLockBiometric()
        }
    }
    private fun convertIntervalToMinutes(interval: String): Int {
        return interval.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toInt()
    }
    private fun unLockBiometric(){
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                stopLockTask()
                Utils.fireAlarmManager(this@AppActivity,selectedDuration)
                finishAffinity()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric check for kiosk mode")
            .setSubtitle("Unlock kiosk mode using biometric")
            .setNegativeButtonText("Cancel")
            .build()

        showBiometricPrompt()
    }
    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("Biometric","BIOMETRIC_SUCCESS")
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("Biometric","BIOMETRIC_ERROR_NO_HARDWARE")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d("Biometric","BIOMETRIC_ERROR_HW_UNAVAILABLE")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d("Biometric","BIOMETRIC_ERROR_NONE_ENROLLED")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.d("Biometric","BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Log.d("Biometric","BIOMETRIC_ERROR_UNSUPPORTED")
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Log.d("Biometric","BIOMETRIC_STATUS_UNKNOWN")
            }
        }
    }

}