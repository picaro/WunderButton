package com.op.wunderbutton;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.op.wunderbutton.model.WList;
import com.op.wunderbutton.tools.Constants;
import com.op.wunderbutton.tools.MixpanelUtil;

import java.util.ArrayList;

import lombok.extern.java.Log;


@Log
public class SelectListActivity extends ListActivity {

    private ArrayList<WList> lists;

    private MixpanelAPI mMixpanel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_list) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_FROM_BACKGROUND);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mMixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("onCreate SelectListActivity");

        mMixpanel = MixpanelAPI.getInstance(getApplicationContext(), Constants.MIXPANEL_TOKEN);
        MixpanelUtil.sendMixPOpened(mMixpanel, "selectList opened", "");

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

        if (lists == null) {
            return; //?? go to main?
        }

        String[] listItems = new String[lists.size()];
        if (lists.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.lets_create_list), Toast.LENGTH_LONG).show();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };

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
        MixpanelUtil.sendMixPOpened(mMixpanel, "List", "" + lists.get(position).getId());

        currentEditor.apply();

        Intent i = new Intent(v.getContext().getApplicationContext(), AddProductActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.getContext().getApplicationContext().startActivity(i);

    }

}



