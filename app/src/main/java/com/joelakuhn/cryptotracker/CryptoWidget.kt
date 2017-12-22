package com.joelakuhn.cryptotracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CryptoWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        for (widgetId in appWidgetIds) {
            var text = "error";
            val sharedPreferences = context
                    .getSharedPreferences("CRYPTOTRACKER_WIDGET_SETTINGS", Context.MODE_PRIVATE)

            val prefCurrency = sharedPreferences.getString("widget:" + widgetId + ":currency", "--")
            val prefAmount = sharedPreferences.getString("widget:" + widgetId + ":amount", "--")
            val doubleAmount = prefAmount.toDoubleOrNull()
            var updated: Date? = null

            try {
                if (prefCurrency == "--" || prefAmount == "--") {
                    text = "Tap to Configure"
                } else if (doubleAmount == null) {

                } else {
                    var json = CryptoAPI().execute(prefCurrency).get()
                    if (json != null) {
                        val data = parse_json(json)
                        val fromUsd = data?.getDouble(prefCurrency)
                        if (fromUsd != null) {
                            if (fromUsd != 0.0) {
                                val inverse: Double = 1.0 / fromUsd
                                val multiplied = inverse * doubleAmount
                                text = "$" + format_number(multiplied)
                            }
                        }

                    }
                }

                updated = Date()
            }
            catch (e: Exception) {

            }

            val remoteViews = RemoteViews(context.packageName,
                    R.layout.crypto_widget)
            remoteViews.setTextViewText(R.id.widgetCurrency, prefCurrency)
            remoteViews.setTextViewText(R.id.widgetValue, text)

            if (updated != null) {
                val dateFormat = SimpleDateFormat("hh:mm:ss a")
                remoteViews.setTextViewText(R.id.widgetUpdated, dateFormat.format(updated))
            }

            if (prefCurrency == "--") {
                remoteViews.setViewVisibility(R.id.details, View.GONE)
            }
            else {
                remoteViews.setViewVisibility(R.id.details, View.VISIBLE)
            }


            val intent = Intent("andoid.intent.action.MAIN")
            intent.addCategory("android.intent.category.LAUNCHER")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.setComponent(ComponentName("com.joelakuhn.cryptotracker", "com.joelakuhn.cryptotracker.MainActivity"))
            intent.putExtra("CRYPTOWIDGET_ID", widgetId)
            var pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0)

            remoteViews.setOnClickPendingIntent(R.id.widgetValue, pendingIntent)


            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    fun format_number(d: Double): String {
        var str = String.format("%.8f", d)
        var i = str.indexOf('.')
        if (i + 2 < str.length) {
            str = str.substring(0, i + 3)
        }
        if (i == -1) {
            i = str.length
        }
        if (i <= 3) {
            return str
        }
        i = i - 3
        while (i > 0) {
            str = str.substring(0, i) + "," + str.substring(i, str.length)
            i -= 3
        }
        return str
    }

    private fun parse_json(json: String): JSONObject? {
        try {
            return JSONObject(json)
        } catch (e: JSONException) {
            return null
        }
    }
}