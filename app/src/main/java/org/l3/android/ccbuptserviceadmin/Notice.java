package org.l3.android.ccbuptserviceadmin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/1/23.
 */
public class Notice {
    private int mId;
    private String mTitle;
    private String mContent;
    private String mDateTime;

    public Notice(JSONObject jsonNotice) throws JSONException{
        mId = Integer.valueOf(jsonNotice.getString("id"));
        mTitle = jsonNotice.getString("title");
        mContent = jsonNotice.getString("content");
        mDateTime = jsonNotice.getString("dateTime");
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }
}
