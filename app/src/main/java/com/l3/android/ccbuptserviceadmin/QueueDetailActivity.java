package com.l3.android.ccbuptserviceadmin;

import android.app.Fragment;

/**
 * Created by Ihsan on 15/2/4.
 */
public class QueueDetailActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new QueueDetailFragment();
    }
}
