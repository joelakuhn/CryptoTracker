package com.joelakuhn.cryptotracker

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent





class MainActivity : AppCompatActivity() {

    val PREFS_NAME = "CryptoPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val widgetId = intent.getIntExtra("CRYPTOWIDGET_ID", -1)
//        val widgetId = intent.extras.getInt("CRYPTOTRACKER_WIDGET_SETTINGS")

        setContentView(R.layout.activity_main)

        val currencySpinner = findViewById<View>(R.id.currency) as Spinner
        val amountEditText = findViewById<View>(R.id.amount) as EditText

        val adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter


        if (widgetId >= 0) {
            val sharedPreferences = applicationContext
                    .getSharedPreferences("CRYPTOTRACKER_WIDGET_SETTINGS", Context.MODE_PRIVATE)
            val prefCurrency = sharedPreferences.getString("widget:" + widgetId + ":currency", currencySpinner.selectedItem.toString())
            val prefAmount = sharedPreferences.getString("widget:" + widgetId + ":amount", amountEditText.text.toString())

            val spinnerPos = adapter.getPosition(prefCurrency)
            currencySpinner.setSelection(spinnerPos)
            amountEditText.setText(prefAmount)
        }


        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            val sharedPreferences = this.applicationContext
                    .getSharedPreferences("CRYPTOTRACKER_WIDGET_SETTINGS", Context.MODE_PRIVATE)
            val prefs = sharedPreferences.edit();

            prefs.putString("widget:" + widgetId + ":currency", currencySpinner.selectedItem.toString())
            prefs.putString("widget:" + widgetId + ":amount", amountEditText.text.toString())

            prefs.commit()

            val intent = Intent(this, CryptoWidget::class.java)
            intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
            val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, CryptoWidget::class.java!!))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(intent)
        }

    }
}
