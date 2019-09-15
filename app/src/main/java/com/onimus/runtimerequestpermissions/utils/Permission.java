/*
 *
 *  * Created by Murillo Comino on 15/09/19 14:50
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 15/09/19 14:49
 *
 */

package com.onimus.runtimerequestpermissions.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import static com.onimus.runtimerequestpermissions.MainActivity.PREFERENCES;

public class Permission {
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //saves if user cancels permissions
    public static void setShouldShowStatus(Context context, String... permissions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String aPermissions : permissions) {
            editor.putBoolean(aPermissions, true);
        }
        editor.apply();
    }

    public static boolean neverAskAgainSelected(Activity activity, String permission) {
        boolean prevShouldShowStatus = getRationaleDisplayStatus(activity, permission);
        boolean currShouldShowStatus = activity.shouldShowRequestPermissionRationale(permission);
        return prevShouldShowStatus != currShouldShowStatus;
    }

    public static boolean neverAskAgainSelected(Activity activity, String... permission) {
        int count = 0;
        // count how many true returns from the above method, if you have the same amount of true
        // and permissions, then the user clicked on the "don't ask again" dialog box.
        for (String aPermission : permission) {
            if (neverAskAgainSelected(activity, aPermission)) {
                count++;
            }
        }
        return count == permission.length;
    }

    private static boolean getRationaleDisplayStatus(Context context, String permission) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(permission, false);
    }
}
