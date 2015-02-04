package com.l3.android.ccbuptserviceadmin;

import android.content.Intent;
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

/**
 * Created by Ihsan on 15/2/3.
 */
public class QueueFragment extends Fragment {
    private static final String TAG="QueueFragment";

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

        mQueueDetailButton = (Button) view.findViewById(R.id.queue_detail);
        mQueueDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),QueueDetailActivity.class);
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
            if (aBoolean){
                updateView();
                mQueueLinearLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
