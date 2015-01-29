package com.l3.android.ccbuptserviceadmin;

import android.app.Fragment;

/**
 * Created by Ihsan on 15/1/28.
 */
public class NoticeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int noticeId = getIntent().getIntExtra(NoticeFragment.EXTRA_NOTICE_ID, -1);
        if (noticeId != -1) {
            return NoticeFragment.newInstance(noticeId);
        } else {
            return new NoticeFragment();
        }
    }
}
