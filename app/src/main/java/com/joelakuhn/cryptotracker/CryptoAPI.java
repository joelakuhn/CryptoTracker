package com.joelakuhn.cryptotracker;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class CryptoAPI extends AsyncTask<String, String, String> {

    @Override
    public String doInBackground(String... urls) {
        String ret_val = "";
        for (int i=0; i<urls.length; i++) {
            String url = urls[i];
            if (url != null) {
                ret_val = send_get(url);
            }
        }
        return ret_val;
    }

    private String send_get(String url) {
        try {
            URL obj = new URL(url);

            HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuffer response = new StringBuffer();

            String inputLine = reader.readLine();
            while (inputLine != null) {
                response.append(inputLine);
                inputLine = reader.readLine();
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
