package com.l3.android.ccbuptserviceadmin;

import android.app.Fragment;

/**
 * Created by Ihsan on 15/1/28.
 */
public class NoticeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new NewNoticeFragment();
    }
}
