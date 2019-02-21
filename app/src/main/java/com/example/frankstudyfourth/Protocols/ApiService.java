package com.example.frankstudyfourth.Protocols;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.support.v4.content.ContextCompat.getSystemService;

public class ApiService {

    public static JSONObject GetStreamData(String meetingId,String language){
        JSONObject streamsInfo = new JSONObject();
        String ApiEntry = "GetStreamData";
        String targetURL = BasicSettings.getUrlBaseParlvuStream(language) + ApiEntry + "?id=" + meetingId;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("ApiService.StreamData",response.toString());
                streamsInfo.put("Streams",new JSONArray(response.toString()));
            }else{
                Log.e("ApiService.StreamData","HttpCode:"+responseCode+";Error:"+connection.getResponseMessage());
            }
            streamsInfo.put("responseCode",responseCode);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                streamsInfo.put("responseCode",-1);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            connection.disconnect();
        }
        return streamsInfo;
    }
    public static JSONObject GetEventList(String fromDate, String endDate, String language){
        JSONObject eventList = new JSONObject();
        String ApiEntry = "GetEventList";
        String targetURL = BasicSettings.getUrlBaseParlvuAPI(language) + ApiEntry + "?maxCount=-1";
        targetURL += (fromDate != null && fromDate != "") ? "&fromDate=" + fromDate : "";
        targetURL += (fromDate != null && endDate != "") ? "&endDate=" + endDate : "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Log.d("ApiService.EventList",response.toString());
            if(responseCode == 200){
                eventList = new JSONObject(response.toString());
                eventList.put("responseMessage","Successfully fetch meeting list!");
            }else {
                eventList.put("responseMessage",response.toString());
            }
            eventList.put("responseCode",responseCode);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                eventList.put("responseCode",-1);
                eventList.put("responseMessage",e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            connection.disconnect();
        }
        return eventList;
    }

    public static Bitmap GetThumbnail(String Uri){
        URL url = null;
        Bitmap bmp = null;
        try {
            url = new URL(Uri);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetAvailable(){
        boolean rst = false;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 204 &&
                    urlConnection.getContentLength() == 0) {
                Log.d("Network Checker", "Successfully connected to internet");
                rst = true;
            }
        } catch (IOException e) {
            Log.e("Network Checker", "Error checking internet connection", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return rst;
    }
}
