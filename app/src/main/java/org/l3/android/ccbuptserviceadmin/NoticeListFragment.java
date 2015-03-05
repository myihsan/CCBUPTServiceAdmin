package org.l3.android.ccbuptserviceadmin;

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
import android.widget.AdapterView;
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
    private static final int NEW_NOTICE = 1;
    private static final int EDIT_NOTICE = 2;

    private ListView mListView;
    private ArrayList<Notice> mNotices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_list, container, false);
        mListView = (ListView) view.findViewById(R.id.notice_list_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice notice = ((NoticeAdapter) mListView.getAdapter()).getItem(position);
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                intent.putExtra(NoticeFragment.EXTRA_NOTICE_ID, notice.getId());
                startActivityForResult(intent, EDIT_NOTICE);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.notice_list_fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                startActivityForResult(intent, NEW_NOTICE);
            }
        });
        NoticeAdapter adapter = new NoticeAdapter(NoticeArray.get(getActivity()).getNotices());
        mListView.setAdapter(adapter);
        new FetchNoticeTask().execute();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        new FetchNoticeTask().execute();
    }

    public void updateAdapter() {
        ((NoticeAdapter)mListView.getAdapter()).notifyDataSetChanged();
    }

    private class FetchNoticeTask extends AsyncTask<Void, Void, ArrayList<Notice>> {
        @Override
        protected ArrayList<Notice> doInBackground(Void... params) {
            int adminId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logged_admin_id), -1);
            Log.d(TAG, adminId + "");
            if (adminId != -1) {
                return new DataFetcher(getActivity()).fetchNoticeByAdminId(adminId);
            }
            return new ArrayList<Notice>();
        }

        @Override
        protected void onPostExecute(ArrayList<Notice> notices) {
            NoticeArray.get(getActivity()).refreshNotices(notices);
            updateAdapter();
        }
    }

    private class NoticeAdapter extends ArrayAdapter<Notice> {

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
