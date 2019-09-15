/*
 *
 *  * Created by Murillo Comino on 15/09/19 14:50
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 04/05/19 21:57
 *
 */

package com.onimus.runtimerequestpermissions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.onimus.runtimerequestpermissions.utils.Permission;

import static com.onimus.runtimerequestpermissions.utils.Permission.neverAskAgainSelected;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCES = "preferences";
    public static final String PERMISSION_FIRST_ENTRY_ACTIVITY = "permission_first";
    private static final int REQUEST_PERMISSIONS = 1;
    public static final String TAG = "Onimus";


    @Override
    protected void onStop() {
        super.onStop();
        //reset SharedPreference
        setSharedPreferencesToFirstPermissionActivity(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        checkPermissions();
    }

    private void checkPermissions() {
        // If the user has accepted all permissions, then go to the second activity
        if (Permission.hasPermissions(this, permissions())) {
            Intent i = new Intent(this, SecondActivity.class);
            startActivity(i);
            this.finish();
        }
    }

    private void setPermissions() {
        // The request code used in ActivityCompat.requestPermissions
        // is returned in Activity onRequestPermissionsResult
        if (!Permission.hasPermissions(this, permissions())) {
            if (neverAskAgainSelected(this, permissions())) {
                displayNeverAskAgainDialog();
            } else {
                ActivityCompat.requestPermissions(this, permissions(), REQUEST_PERMISSIONS);
            }
        }
    }

    private String[] permissions() {
        return new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
    }

    private void displayNeverAskAgainDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.text_dialog_permission));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.text_permit_manually, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                startActivityForResult(intent, REQUEST_PERMISSIONS);
            }
        });
        builder.setNegativeButton(getString(R.string.alert_title_cancel), null);
        builder.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (REQUEST_PERMISSIONS == requestCode) {
            int count = 0;
            for (int aGrantResult : grantResults) {
                if (aGrantResult == PackageManager.PERMISSION_GRANTED) {
                    //counter for each permission granted
                    count++;
                }
            }
            //If the counter equals the amount of permission then permission was a success
            if (count == permissions.length) {
                Log.i(TAG, getString(R.string.text_permission_granted));
                Toast.makeText(this, getString(R.string.text_permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Permission.setShouldShowStatus(this, permissions);
            }
        }
        setSharedPreferencesToFirstPermissionActivity(false);
        checkPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSIONS) {
            setSharedPreferencesToFirstPermissionActivity(false);
            checkPermissions();
        }
    }

    private void setSharedPreferencesToFirstPermissionActivity(boolean f) {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PERMISSION_FIRST_ENTRY_ACTIVITY, f).apply();
    }

    public void onClickRequestPermission(View view) {
        setPermissions();
    }
}