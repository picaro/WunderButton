package com.op.wunderbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.requests.AddProductRequest;
import com.op.wunderbutton.tools.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import lombok.extern.java.Log;


@Log
public class AddProductActivity extends ActionBarActivity {

    private int listId;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("onCreate Addprod");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String preferenceValue = preferences.getString(Constants.LIST_ID, "0");
        listId = Integer.parseInt(preferenceValue);

        ArrayList<String> prodListStr = restoreProductLists(preferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        final LinearLayout scrollView = (LinearLayout) findViewById(R.id.productslist);
        for (final String prodTitle : prodListStr) {
            Button imageButton = createProductButton(prodTitle);
            scrollView.addView(imageButton);
        }

        final EditText addProduct = (EditText) findViewById(R.id.add_an_item);
        addProduct.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Button imageButton = createProductButton(addProduct.getText().toString());
                            scrollView.addView(imageButton, 0);
                            addProduct.setText("");
                            return true;
                        }
                        return false;
                    }
                }
        );

    }

    private Button createProductButton(final String prodTitle) {
        Button imageButton = new Button(getApplicationContext());
        imageButton.setBackgroundColor(0xffffb333);
        imageButton.setTextColor(Color.BLACK);
        imageButton.setText(prodTitle);
        imageButton.setHeight(80);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 20, 30, 0);
        imageButton.setLayoutParams(layoutParams);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAddProductRequest(prodTitle);
                Toast.makeText(view.getContext(), "Product added", Toast.LENGTH_SHORT).show();
            }
        });
        return imageButton;
    }

    private ArrayList<String> restoreProductLists(SharedPreferences preferences) {
        Integer roomId = preferences.getInt(Constants.ROOM_ID, R.id.img_wc);
        ArrayList<String> prodListStr = new ArrayList();
        StringTokenizer stokens = null;

        String savedList = preferences.getString(Constants.SAVED_LIST, "");
        if (savedList.length() > 0 ) {
            log.info("found savedList:" + savedList);
            stokens = new StringTokenizer(savedList, "|");
            while (stokens.hasMoreElements()) {
                prodListStr.add(stokens.nextElement().toString());
            }
        } else {
            switch (roomId) {
                case R.id.img_kitchen: {
                    stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.kitchen_products), "|");
                    while (stokens.hasMoreElements()) {
                        prodListStr.add(stokens.nextElement().toString());
                    }
                    break;
                }
                case R.id.img_wc: {
                    stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.wc_products), "|");
                    while (stokens.hasMoreElements()) {
                        prodListStr.add(stokens.nextElement().toString());
                    }
                    break;
                }
                case R.id.img_bath: {
                    stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.bath_products), "|");
                    while (stokens.hasMoreElements()) {
                        prodListStr.add(stokens.nextElement().toString());
                    }
                    break;
                }
                case R.id.img_bath_wc: {
                    stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.bath_products), "|");
                    while (stokens.hasMoreElements()) {
                        prodListStr.add(stokens.nextElement().toString());
                    }
                    stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.wc_products), "|");
                    while (stokens.hasMoreElements()) {
                        prodListStr.add(stokens.nextElement().toString());
                    }
                    break;
                }
            }
        }
        return prodListStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_list) {
            SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            currentPreferences.edit().clear().commit();

            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void sendAddProductRequest(String title) {
        HashMap params2 = new HashMap();

        params2.put(Constants.LIST_ID, listId);
        params2.put(Constants.TITLE, title);
        params2.put(Constants.COMPLETED, false);
        params2.put(Constants.STARRED, false);

        LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask();
        WebApiRequest request = new AddProductRequest(this.getApplicationContext(), new JSONObject(params2));
        getFeedlyAccessTokenAsyncTask.execute(request);

    }

}



