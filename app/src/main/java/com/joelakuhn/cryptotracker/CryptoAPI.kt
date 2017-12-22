package com.joelakuhn.cryptotracker

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class KCryptoAPI: AsyncTask<String, String, String>() {

    override fun doInBackground(vararg urls: String): String {
        var ret_val = "";
        for (url: String in urls) {
            if (url != null) {
                ret_val = send_get(url)
            }
        }
        return ret_val;
    }

    private fun send_get(url: String): String {
        val obj = URL(url)

        with(obj.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                return response.toString()
            }
        }
    }
}
