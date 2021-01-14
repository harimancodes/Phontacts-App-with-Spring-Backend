package com.example.phontacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PrefContactList {

    public static void writeListInPref(Context context, ArrayList<ContactItem> list){
        Gson gson=new Gson();
        String jsonString=gson.toJson(list);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("list_key",jsonString);
    }
    public static ArrayList<ContactItem> readListFromPref(Context context){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString=preferences.getString("list_key",null);
        Gson gson=new Gson();
        Type type=new TypeToken<ArrayList<ContactItem>>(){}.getType();
        ArrayList<ContactItem> list=gson.fromJson(jsonString,type);
        return list;
    }
}
