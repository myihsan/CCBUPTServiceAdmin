package com.l3.android.ccbuptserviceadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Ihsan on 15/2/3.
 */
public class QueueFragment extends Fragment {
    private static final String TAG = "QueueFragment";

    private LinearLayout mQueueLinearLayout;
    private TextView mNextNumberTextView, mTotalTextView;
    private Button mNextOneButtone, mQueueDetailButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        mQueueLinearLayout = (LinearLayout) view.findViewById(R.id.queue_linearLayout);
        mQueueLinearLayout.setVisibility(View.INVISIBLE);

        mNextNumberTextView = (TextView) view.findViewById(R.id.queue_now_textView);
        mTotalTextView = (TextView) view.findViewById(R.id.queue_total_textView);

        mNextOneButtone = (Button) view.findViewById(R.id.queue_next_one);
        mNextOneButtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NextQueuerTask().execute();
            }
        });

        mQueueDetailButton = (Button) view.findViewById(R.id.queue_detail);
        mQueueDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QueueDetailActivity.class);
                startActivity(intent);
            }
        });

        new FetchQueueTask().execute();

        return view;
    }

    private void updateView() {
        mNextNumberTextView.setText(String.valueOf(Queue.get(getActivity()).getNextNumber()));
        mTotalTextView.setText(String.valueOf(Queue.get(getActivity()).getTotal()));

    }

    private class FetchQueueTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            int adminId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logged_admin_id), -1);
            if (adminId != -1) {
                return new DataFetcher().fetchQueueByAdminId(getActivity(), adminId);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                updateView();
                mQueueLinearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class NextQueuerTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean flag=false;
            int adminId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logged_admin_id), -1);
            if (adminId == -1) {
                return false;
            }
            String fetchUrl = "http://10.168.1.124/CCBUPTService/nextqueuer.php";
            String url = Uri.parse(fetchUrl).buildUpon()
                    .appendQueryParameter("adminId", String.valueOf(adminId))
                    .build().toString();
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

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                new FetchQueueTask().execute();
            }else {
                Toast.makeText(getActivity(),"处理失败，请重试",Toast.LENGTH_LONG).show();
            }
        }
    }
}
