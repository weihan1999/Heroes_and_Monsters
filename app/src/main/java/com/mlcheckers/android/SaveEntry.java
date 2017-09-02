package com.mlcheckers.android;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Michael Li on 8/5/2017.
 */

public class SaveEntry {
    private int level;
    private String sid;
    private String uid;
    public List<Sprite> sprite;

    private long updated;

    public SaveEntry(String uid, String sid, int level){
        this.uid=uid;
        this.sid=sid;
        this.level=level;


        Calendar c= GregorianCalendar.getInstance();
        this.updated=c.getTimeInMillis();
    }

    public int getLevel(){return level;}
    public String getSid(){return sid;}
    public String getUid(){return  uid;}
    public long getUpdated(){return updated;}

}
