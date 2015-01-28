package com.l3.android.ccbuptserviceadmin;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/28.
 */
public class NewNoticeFragment extends Fragment {
    private static final String TAG = "NewNoticeFragment";

    private EditText mTitleEditText, mContentEditText;
    private ArrayList<CheckBox> mCheckBoxes = new ArrayList<CheckBox>();

    private boolean mIsSent = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_notice, container, false);
        mTitleEditText = (EditText) view.findViewById(R.id.new_notice_titleTextView);
        mContentEditText = (EditText) view.findViewById(R.id.new_notice_contentTextView);
        ArrayList<String> authority = new ArrayList<String>();
        try {
            authority = loadAuthority();
        } catch (Exception e) {
            Log.e(TAG, "Error loading authority: ", e);
        }
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.new_notice_linearLayout);
        for (int i = 0; i < authority.size(); i++) {
            //在通过动态填充的方式找到CheckBox的文件
            CheckBox checkbox = (CheckBox) getActivity().getLayoutInflater().inflate(
                    R.layout.checkbox_new_notice, null);
            mCheckBoxes.add(checkbox);
            mCheckBoxes.get(i).setText(authority.get(i));
            linearLayout.addView(checkbox);
        }
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (pre.contains(getString(R.string.unsent_title))){
            mTitleEditText.setText(pre.getString(getString(R.string.unsent_title),""));
        }
        if (pre.contains(getString(R.string.unsent_content))){
            mContentEditText.setText(pre.getString(getString(R.string.unsent_content),""));
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mIsSent) {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString(getString(R.string.unsent_title), mTitleEditText.getText().toString())
                    .putString(getString(R.string.unsent_content), mContentEditText.getText().toString())
                    .commit();
        } else {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .remove(getString(R.string.unsent_title))
                    .remove(getString(R.string.unsent_content))
                    .commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_new_notice, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.action_send:
                new sendTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<String> loadAuthority() throws IOException, JSONException {
        ArrayList<String> authority = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            InputStream in = getActivity().openFileInput("authority.json");
            reader = new BufferedReader(new InputStreamReader(in));
            JSONArray array = new JSONArray(reader.readLine());
            for (int i = 0; i < array.length(); i++) {
                authority.add(array.getString(i));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null)
                reader.close();
        }
        return authority;
    }

    private boolean sendNotice() {
        boolean flag = false;
        String fetchUrl = "http://10.168.1.124/CCBUPTService/newnotice.php";
        int teacherId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(getString(R.string.logged_teacher_id), -1);
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("title", mTitleEditText.getText().toString())
                .appendQueryParameter("content", mContentEditText.getText().toString())
                .appendQueryParameter("teacherId", String.valueOf(teacherId))
                .build().toString();

        for (CheckBox checkBox : mCheckBoxes) {
            if (checkBox.isChecked()) {
                url += "&tagList[]=" + checkBox.getText().toString();
            }
        }
        Log.d(TAG, url);
        try {
            String result = getUrl(url);
            Log.d(TAG, result);
            if (result.equals("succeed")) {
                flag = true;
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return flag;
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

    private String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private void showResult(boolean result) {
        if (result) {
            mIsSent = true;
            Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "发送失败，请重试", Toast.LENGTH_LONG).show();
        }
    }

    private class sendTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return sendNotice();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showResult(aBoolean);
        }
    }
}
