package com.joelakuhn.cryptotracker

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

internal class CryptoAPI : AsyncTask<String, String, String>() {

    val CryptoCompareUrl = "https://min-api.cryptocompare.com/data/price?fsym=USD&tsyms="

    public override fun doInBackground(vararg currency: String): String {
        val url = CryptoCompareUrl + currency[0]
        try {
            val obj = URL(url)

            val conn = obj.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(conn.inputStream))

            val response = StringBuffer()

            var inputLine: String? = reader.readLine()
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = reader.readLine()
            }
            return response.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
