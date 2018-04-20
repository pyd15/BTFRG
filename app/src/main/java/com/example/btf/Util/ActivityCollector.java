package com.example.btf.Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.P on 2018/3/2.
 * runas /user:Dr.P "cmd /k"
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void AddActivity(Activity activity) {
        activities.add(activity);
    }

    public static void RemoveActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
