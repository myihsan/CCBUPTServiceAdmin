package com.l3.android.ccbuptserviceadmin;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/23.
 */
public class DataFetcher {
    private static final String TAG = "DataFetcher";
    private Context mContext;

    public DataFetcher(Context context) {
        mContext = context;
    }

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

    public ArrayList<Notice> fetchNoticeByAdminId(int adminId) {
        ArrayList<Notice> notices = new ArrayList<Notice>();

        String fetchUrl = mContext.getString(R.string.root_url)+"notice.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("adminId", String.valueOf(adminId))
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
        JSONArray itemArray = new JSONArray(jsonString);
        for (int i = 0; i < itemArray.length(); i++) {
            Notice notice = new Notice(itemArray.getJSONObject(i));
            notices.add(notice);
        }
    }

    public boolean fetchQueueByAdminId(Context context, int adminId) {
        String fetchUrl = mContext.getString(R.string.root_url)+"queue.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("adminId", String.valueOf(adminId))
                .build().toString();
        try {
            String jsonString = getUrl(url);
            Log.i(TAG, jsonString);
            parseQueue(context, jsonString);
            return true;
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
            return false;
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse queue", jsone);
            return false;
        }
    }

    private void parseQueue(Context context, String jsonString) throws JSONException {
        JSONObject queueObject = new JSONObject(jsonString);
        String title = queueObject.getString("title");
        int nextNumber = queueObject.getInt("nextNumber");
        int total = queueObject.getInt("total");
        ArrayList<Queuer> queuers = new ArrayList<Queuer>();
        JSONArray queuerArray = queueObject.getJSONArray("queuer");
        for (int i = 0; i < queuerArray.length(); i++) {
            Queuer queuer = new Queuer(queuerArray.getJSONObject(i));
            queuers.add(queuer);
        }
        Queue.get(context).refreshQueuer(title, nextNumber, total, queuers);
    }
}
