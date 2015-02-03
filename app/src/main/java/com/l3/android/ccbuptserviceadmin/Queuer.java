package com.l3.android.ccbuptserviceadmin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/2/3.
 */
public class Queuer {
    private int mNumber;
    private String mToken;
    private String mRequire;

    public Queuer(JSONObject jsonNotice) throws JSONException {
        mToken = jsonNotice.getString("token");
        mRequire = jsonNotice.getString("require");
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getRequire() {
        return mRequire;
    }

    public void setRequire(String require) {
        this.mRequire = require;
    }
}
