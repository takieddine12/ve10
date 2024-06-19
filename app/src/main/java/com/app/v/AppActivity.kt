package com.app.v

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.core.content.ContextCompat

class AppActivity : AppCompatActivity() {

    private var selectedDuration = 0
    private val timeIntervals = arrayOf("15 min", "30 min", "45 min", "60 min", "75 min", "90 min", "120 min")
    private lateinit var numberPicker : NumberPicker
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        numberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 0
        numberPicker.maxValue = timeIntervals.size - 1
        numberPicker.displayedValues = timeIntervals
        numberPicker.wrapSelectorWheel = true;

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
        // SET DRAWABLE END COLOR
        val greenColor = ContextCompat.getColor(this, R.color.greenColor)
        val colorFilter = PorterDuffColorFilter(greenColor, PorterDuff.Mode.SRC_IN)
        edit.compoundDrawablesRelative[2]?.colorFilter = colorFilter

        // DELETE USER INPUTS ON BACKSPACE CLICK
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
    }
    private fun convertIntervalToMinutes(interval: String): Int {
        return interval.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toInt()
    }
}