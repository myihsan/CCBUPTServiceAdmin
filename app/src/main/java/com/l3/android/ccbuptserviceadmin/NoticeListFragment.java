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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/23.
 */
public class NoticeListFragment extends Fragment {
    private static final String TAG = "NoticeListFragment";

    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_list, container, false);
        mListView = (ListView)view.findViewById(R.id.notice_list_listView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.notice_list_fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NoticeActivity.class);
                startActivity(intent);
            }
        });
        new FetchNoticeTask().execute();
        return view;
    }

    public void setupAdapter() {
        NoticeAdapter adapter = new NoticeAdapter(NoticeArray.get(getActivity()).getNotices());
        mListView.setAdapter(adapter);
    }

    private class FetchNoticeTask extends AsyncTask<Void, Void, ArrayList<Notice>> {
        @Override
        protected ArrayList<Notice> doInBackground(Void... params) {
            int teacherId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logged_teacher_id), -1);
            Log.d(TAG, teacherId + "");
            if (teacherId != -1) {
                return new NoticeFetcher().fetchNoticeByTeacherId(teacherId);
            }
            return new ArrayList<Notice>();
        }

        @Override
        protected void onPostExecute(ArrayList<Notice> notices) {
            NoticeArray.get(getActivity()).refreshNotices(0, notices);
            setupAdapter();
        }
    }

    public class NoticeAdapter extends ArrayAdapter<Notice> {

        public NoticeAdapter(ArrayList<Notice> notices) {
            super(getActivity(), 0, notices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_notice, null);
            }

            // Configure the view for this notice
            Notice notice = getItem(position);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.notice_list_item_titleTextView);
            titleTextView.setText(notice.getTitle());

            TextView dateTimeTextView =
                    (TextView) convertView.findViewById(R.id.notice_list_item_dateTimeTextView);
            dateTimeTextView.setText(notice.getDateTime());

            return convertView;
        }
    }
}
