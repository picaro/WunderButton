package com.op.wunderbutton;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.op.wunderbutton.model.WList;
import com.op.wunderbutton.tools.Constants;

import java.util.ArrayList;

import lombok.core.Main;
import lombok.extern.java.Log;


@Log
public class SelectListActivity extends ListActivity {

    private ArrayList<WList> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list);

        Intent intent = getIntent();
        String listsJson = intent.getStringExtra(Constants.LISTS_JSON);
        if (listsJson == null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }

        lists = Constants.gson.fromJson(listsJson,
                new TypeToken<ArrayList<WList>>() {
                }.getType());

        String[] listItems = new String[lists.size()];

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        int i = 0;
        for (final WList wList : lists) {
            listItems[i++] = wList.getTitle();
        }
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor currentEditor = currentPreferences.edit();
        currentEditor.putString(Constants.LIST_ID, "" + lists.get(position).getId());
        currentEditor.commit();

        Intent i = new Intent(v.getContext().getApplicationContext(), SelectRoomActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.getContext().getApplicationContext().startActivity(i);

    }

}



