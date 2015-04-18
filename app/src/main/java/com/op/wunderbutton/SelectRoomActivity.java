package com.op.wunderbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.op.wunderbutton.tools.Constants;


public class SelectRoomActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
    }

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
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), AddProductActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor currentEditor = currentPreferences.edit();
        currentEditor.putInt(Constants.ROOM_ID,v.getId());
        currentEditor.commit();

        this.startActivity(i);
    }
}
