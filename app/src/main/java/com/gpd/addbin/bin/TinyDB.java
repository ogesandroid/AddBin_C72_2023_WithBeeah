package com.gpd.addbin.bin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lenovo on 4/4/2018.
 */

public class TinyDB {

    SharedPreferences preference;
    SharedPreferences.Editor PreferenceEditor;
    public static int UserId;
    /* constructor */
    public TinyDB(Context Context)
    {
        this.setPreference(Context);
    }
    public void setPreference(Context Context)
    {
        preference = PreferenceManager.getDefaultSharedPreferences(Context);
        UserId= preference.getInt("UserId", 0);
    }
    public void setBoolean(String VariableName, boolean Value)
    {
        PreferenceEditor = preference.edit();
        PreferenceEditor.putBoolean(VariableName, Value);
        PreferenceEditor.commit();
    }
    public void setInt(String VariableName, int Value)
    {
        PreferenceEditor = preference.edit();
        PreferenceEditor.putInt(VariableName, Value);
        PreferenceEditor.commit();
    }
    public void setString(String VariableName, String Value)
    {
        PreferenceEditor = preference.edit();
        PreferenceEditor.putString(VariableName, Value);
        PreferenceEditor.commit();
    }

    public void SignOut()
    {
        PreferenceEditor = preference.edit();
        PreferenceEditor.clear();
        PreferenceEditor.commit();
        setInt("status_request",1);
    }
    public void resetSetting()
    {
        PreferenceEditor = preference.edit();
        PreferenceEditor.clear();
        PreferenceEditor.commit();
    }
}
