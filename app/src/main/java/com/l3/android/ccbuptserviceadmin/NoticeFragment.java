package com.l3.android.ccbuptserviceadmin;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/28.
 */
public class NoticeFragment extends Fragment {
    private static final String TAG = "NoticeFragment";
    public static final String EXTRA_NOTICE_ID =
            "com.l3.android.ccbuptserviceadmin.notice_id";

    private EditText mTitleEditText, mContentEditText;
    private TextView mTargetsTextView;
    private MenuItem mSendAction;
    private ArrayList<CheckBox> mCheckBoxes = new ArrayList<CheckBox>();

    private int mNoticeId = -1;
    private boolean mIsSent = false;

    public static NoticeFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_NOTICE_ID, id);

        NoticeFragment fragment = new NoticeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoticeId = getArguments().getInt(EXTRA_NOTICE_ID, -1);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        mTitleEditText = (EditText) view.findViewById(R.id.notice_titleTextView);
        mContentEditText = (EditText) view.findViewById(R.id.notice_contentTextView);
        mTargetsTextView = (TextView) view.findViewById(R.id.notice_targetsTextView);
        ArrayList<String> authority = new ArrayList<String>();
        try {
            authority = loadAuthority();
        } catch (Exception e) {
            Log.e(TAG, "Error loading authority: ", e);
        }
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.new_notice_linearLayout);
        for (int i = 0; i < authority.size(); i++) {
            CheckBox checkbox = (CheckBox) getActivity().getLayoutInflater().inflate(
                    R.layout.checkbox_new_notice, null);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTargetsTextView.setError(null);
                }
            });
            mCheckBoxes.add(checkbox);
            mCheckBoxes.get(i).setText(authority.get(i));
            linearLayout.addView(checkbox);
        }

        if (mNoticeId == -1) {
            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (pre.contains(getString(R.string.unsent_title))) {
                mTitleEditText.setText(pre.getString(getString(R.string.unsent_title), ""));
            }
            if (pre.contains(getString(R.string.unsent_content))) {
                mContentEditText.setText(pre.getString(getString(R.string.unsent_content), ""));
            }
        } else {
            Notice notice = NoticeArray.get(getActivity()).getNotice(mNoticeId);
            mTitleEditText.setText(notice.getTitle());
            mContentEditText.setText(notice.getContent());
            mIsSent = true;
            new GetSpecialtyTask().execute();
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
        inflater.inflate(R.menu.menu_notice, menu);
        mSendAction = menu.findItem(R.id.action_send);
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
                if (checkNotice()) {
                    mSendAction.setEnabled(false);
                    new SendTask().execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkNotice() {
        boolean flag = false;
        View focusView = null;
        mTitleEditText.setError(null);
        mContentEditText.setError(null);
        mTargetsTextView.setError(null);

        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();
        for (CheckBox checkBox : mCheckBoxes) {
            if (checkBox.isChecked()) {
                flag = true;
            }
        }
        if (!flag) {
            mTargetsTextView.setError(getString(R.string.error_field_required));
        }
        if (TextUtils.isEmpty(content)) {
            mContentEditText.setError(getString(R.string.error_field_required));
            focusView = mContentEditText;
            flag = false;
        }
        if (TextUtils.isEmpty(title)) {
            mTitleEditText.setError(getString(R.string.error_field_required));
            focusView = mTitleEditText;
            flag = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        return flag;
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

    private String getSpecialty() {
        String result = null;
        String fetchUrl = "http://10.168.1.124/CCBUPTService/getspecialty.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("noticeId", String.valueOf(mNoticeId))
                .build().toString();
        try {
            result = new DataFetcher().getUrl(url);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return result;
    }

    private boolean sendNotice() {
        boolean flag = false;
        String fetchUrl = "http://10.168.1.124/CCBUPTService/newnotice.php";
        int adminId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(getString(R.string.logged_admin_id), -1);
        String targets = null;
        for (CheckBox checkBox : mCheckBoxes) {
            if (checkBox.isChecked()) {
                if (targets == null) {
                    targets = checkBox.getText().toString();
                } else {
                    targets += "," + checkBox.getText().toString();
                }
            }
        }
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("title", mTitleEditText.getText().toString())
                .appendQueryParameter("content", mContentEditText.getText().toString())
                .appendQueryParameter("adminId", String.valueOf(adminId))
                .appendQueryParameter("targets", targets)
                .build().toString();
        Log.d(TAG, url);
        try {
            String result = new DataFetcher().getUrl(url);
            Log.d(TAG, result);
            if (result.equals("succeed")) {
                flag = true;
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return flag;
    }

    private boolean editNotice() {
        boolean flag = false;
        String fetchUrl = "http://10.168.1.124/CCBUPTService/editnotice.php";
        String targets = null;
        for (CheckBox checkBox : mCheckBoxes) {
            if (checkBox.isChecked()) {
                if (targets == null) {
                    targets = checkBox.getText().toString();
                } else {
                    targets += "," + checkBox.getText().toString();
                }
            }
        }
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("id", String.valueOf(mNoticeId))
                .appendQueryParameter("title", mTitleEditText.getText().toString())
                .appendQueryParameter("content", mContentEditText.getText().toString())
                .appendQueryParameter("targets", targets)
                .build().toString();
        Log.d(TAG, "edit: " + url);
        try {
            String result = new DataFetcher().getUrl(url);
            Log.d(TAG, result);
            if (result.equals("succeed")) {
                flag = true;
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return flag;
    }

    private void showResult(boolean result) {
        if (result) {
            mIsSent = true;
            Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "发送失败，请重试", Toast.LENGTH_LONG).show();
            mSendAction.setEnabled(true);
        }
    }

    private class SendTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (mNoticeId == -1) {
                return sendNotice();
            } else {
                return editNotice();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showResult(aBoolean);
        }
    }

    private class GetSpecialtyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return getSpecialty();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null && result.length() != 0) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        for (int j = 0; j < mCheckBoxes.size(); j++) {
                            if (jsonArray.get(i).toString().equals(mCheckBoxes.get(j).getText())) {
                                mCheckBoxes.get(j).setChecked(true);
                            }
                        }
                    }
                }
            } catch (JSONException jsone) {
                Log.e(TAG, "Failed to parse specialty", jsone);
            }
        }
    }
}
