package org.l3.android.ccbuptserviceadmin;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/4.
 */
public class QueueDetailFragment extends Fragment {

    private ListView mQueuerListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = Queue.get(getActivity()).getTitle();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_detail, container, false);
        mQueuerListView = (ListView) view.findViewById(R.id.queuer_list_listView);
        QueuerAdapter adapter = new QueuerAdapter(Queue.get(getActivity()).getQueuers());
        mQueuerListView.setAdapter(adapter);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class QueuerAdapter extends ArrayAdapter<Queuer> {

        public QueuerAdapter(ArrayList<Queuer> queuers) {
            super(getActivity(), 0, queuers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_queuer, null);
            }

            Queuer queuer = getItem(position);

            TextView numberTextView =
                    (TextView) convertView.findViewById(R.id.queuer_list_item_numberTextView);
            numberTextView.setText(String.valueOf(queuer.getNumber()));

            TextView requireTextView =
                    (TextView) convertView.findViewById(R.id.queuer_list_item_requireTextView);
            String require = queuer.getRequire();
            if (!TextUtils.isEmpty(require)) {
                requireTextView.setText(require);
            }

            TextView tokenTextView =
                    (TextView) convertView.findViewById(R.id.queuer_list_item_tokenTextView);
            tokenTextView.setText(queuer.getToken());

            return convertView;
        }
    }
}
