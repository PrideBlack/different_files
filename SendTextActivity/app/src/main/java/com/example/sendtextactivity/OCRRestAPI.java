package com.example.sendtextactivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputEditText;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;



public class OCRRestAPI {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void PhotoToText(Bitmap bitmap, final CallBack callBack) throws Exception
    {
        final String license_code = "C73C7A2D-D7E9-4D23-B338-0F6EFF2D0175";
        final String user_name = "SHCIPIR";
        String ocrURL = "http://www.ocrwebservice.com/restservices/processDocument?gettext=true&language=russian";

        ByteArrayOutputStream stream_bmp = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream_bmp);
        final byte[] fileContent = stream_bmp.toByteArray();
        bitmap.recycle();

        final URL url = new URL(ocrURL);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    String result = new String();
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");

                    connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((user_name + ":" + license_code).getBytes()));
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", Integer.toString(fileContent.length));
                    OutputStream stream = connection.getOutputStream();

                    stream.write(fileContent);
                    stream.close();

                    int httpCode = connection.getResponseCode();

                    System.out.println("HTTP Response code: " + httpCode);

                    if (httpCode == HttpURLConnection.HTTP_OK)
                    {
                        String jsonResponse = GetResponseToString(connection.getInputStream());
                        result = PrintOCRResponse(jsonResponse);
                    }
                    else if (httpCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                    {
                        result = "OCR Error Message: Unauthorizied request";
                    }
                    else
                    {
                        String jsonResponse = GetResponseToString(connection.getErrorStream());
                        JSONParser parser = new JSONParser();
                        JSONObject jsonObj = (JSONObject)parser.parse(jsonResponse);
                        result = "Error Message: " + jsonObj.get("ErrorMessage");
                    }
                    System.out.println(result);
                    callBack.onSuccess(result);
                    connection.disconnect();
                } catch (Exception e) {
                    callBack.onFail(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private static String PrintOCRResponse(String jsonResponse) throws ParseException, IOException
    {
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(jsonResponse);
        System.out.println("Available pages: " + jsonObj.get("AvailablePages"));
        JSONArray text= (JSONArray)jsonObj.get("OCRText");
        String result = new String();
        // For zonal OCR: OCRText[z][p]    z - zone, p - pages
        for(int i=0; i<text.size(); i++) {
            String current = text.get(i).toString();
            if(current.length() >= 4) current = current.substring(2, current.length() - 4);
            result += current + "\n";
        }
        return result;
    }

    private static String GetResponseToString(InputStream inputStream) throws IOException
    {
        InputStreamReader responseStream  = new InputStreamReader(inputStream);

        BufferedReader br = new BufferedReader(responseStream);
        StringBuffer strBuff = new StringBuffer();
        String s;
        while ( ( s = br.readLine() ) != null ) {
            strBuff.append(s);
        }

        return strBuff.toString();
    }

}
