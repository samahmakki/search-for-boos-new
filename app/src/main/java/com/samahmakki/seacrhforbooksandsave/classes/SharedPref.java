package com.samahmakki.seacrhforbooksandsave.classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aws on 21/12/2017.
 */

public class SharedPref {
    SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    // this method will save the nightMode State : True or False
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public Boolean loadNightModeState() {
        Boolean state = mySharedPref.getBoolean("NightMode", false);
        return state;
    }
}
