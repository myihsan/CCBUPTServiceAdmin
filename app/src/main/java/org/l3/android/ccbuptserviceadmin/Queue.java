package org.l3.android.ccbuptserviceadmin;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/3.
 */
public class Queue {
    private String mTitle;
    private int mNextNumber;
    private int mTotal;
    private ArrayList<Queuer> mQueuers;

    private static Queue sQueue;
    private Context mContext;

    private Queue(Context context) {
        mContext = context;
        mQueuers = new ArrayList<Queuer>();
    }

    public static Queue get(Context context) {
        if (sQueue == null) {
            sQueue = new Queue(context);
        }
        return sQueue;
    }

    public ArrayList<Queuer> getQueuers() {
        return mQueuers;
    }

    public Queuer getQueue(int number) {
        for (Queuer queuer : mQueuers) {
            if (queuer.getNumber() == number) {
                return queuer;
            }
        }
        return null;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getNextNumber() {
        return mNextNumber;
    }

    public void setNextNumber(int nextNumber) {
        mNextNumber = nextNumber;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public void refreshQueuer(String title,int nextNumber,int total,ArrayList<Queuer> queuers){
        sQueue.mTitle=title;
        sQueue.mNextNumber=nextNumber;
        sQueue.mTotal=total;
        sQueue.mQueuers=queuers;
    }
}
