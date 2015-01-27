package com.l3.android.ccbuptserviceadmin;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/23.
 */
public class NoticeArray {
    private ArrayList<Notice> mNotices;

    private static NoticeArray sNoticeArray;
    private Context mAppContext;

    private NoticeArray(Context appContext) {
        mAppContext = appContext;
        mNotices = new ArrayList<Notice>();
    }

    public static NoticeArray get(Context context) {
        if (sNoticeArray == null) {
            sNoticeArray = new NoticeArray(context);
        }
        return sNoticeArray;
    }

    public ArrayList<Notice> getNotices() {
        return mNotices;
    }

    public Notice getNotice(int id) {
        for (Notice notice : mNotices) {
            if (notice.getId() == id) {
                return notice;
            }
        }
        return null;
    }

    public void refreshNotices(int position, ArrayList<Notice> notices) {
        mNotices.clear();
        mNotices.addAll(position, notices);
    }
}
