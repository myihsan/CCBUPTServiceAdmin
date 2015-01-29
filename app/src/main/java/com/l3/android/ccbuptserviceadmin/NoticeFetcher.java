package com.l3.android.ccbuptserviceadmin;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/23.
 */
public class NoticeFetcher {
    private static final String TAG = "NoticeFetcher";

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<Notice> fetchNoticeByTeacherId(int teacherId) {
        ArrayList<Notice> notices = new ArrayList<Notice>();

        String fetchUrl = "http://10.168.1.124/CCBUPTService/notice.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("teacher_id", String.valueOf(teacherId))
                .build().toString();
        try {
            String jsonString = getUrl(url);
            Log.i(TAG, jsonString);
            parseNotices(notices, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse notices", jsone);
        }
        return notices;
    }

    private void parseNotices(ArrayList<Notice> notices, String jsonString) throws JSONException, IOException {
        JSONArray itemsArray = new JSONArray(jsonString);
        for (int i = 0; i < itemsArray.length(); i++) {
            Notice notice = new Notice(itemsArray.getJSONObject(i));
            notices.add(notice);
        }
    }
}
