package com.op.wunderbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.requests.AddProductRequest;
import com.op.wunderbutton.tools.Constants;
import com.op.wunderbutton.tools.MixpanelUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import lombok.extern.java.Log;


@Log
public class AddProductActivity extends ActionBarActivity {

    private MixpanelAPI mMixpanel;

    private int listId;

    private ArrayList<String> prodListStr;

    private Map<String, View> selectedItems = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("onCreate Addprod");

        mMixpanel = MixpanelAPI.getInstance(getApplicationContext(), Constants.MIXPANEL_TOKEN);
        MixpanelUtil.sendMixPOpened(mMixpanel, "Add product", "");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String preferenceValue = preferences.getString(Constants.LIST_ID, "0");
        listId = Integer.parseInt(preferenceValue);

        prodListStr = restoreProductLists(preferences);

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
                            prodListStr.add(0, addProduct.getText().toString());
                            MixpanelUtil.sendMixPOpened(mMixpanel, "product", addProduct.getText().toString());

                            scrollView.addView(imageButton, 0);
                            addProduct.setText("");
                            return true;
                        }
                        return false;
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        mMixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveProducts();
    }

    private void saveProducts() {
        if (prodListStr != null && prodListStr.size() > 0) {
            String strToSave = TextUtils.join("|", prodListStr);
            SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor currentEditor = currentPreferences.edit();
            currentEditor.putString(Constants.SAVED_LIST, strToSave);
            currentEditor.commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        //prodListStr = restoreProductLists(preferences);
    }


    private Button createProductButton(final String prodTitle) {

        final Button imageButton = new Button(getApplicationContext());

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (selectedItems.get(prodTitle) != null) {
                    selectedItems.remove(prodTitle);
                    imageButton.setBackgroundColor(getResources().getColor(R.color.prod_default_color));
                } else {
                    selectedItems.put(prodTitle, imageButton);
                    imageButton.setBackgroundColor(getResources().getColor(R.color.prod_selected_color));
                }

                invalidateOptionsMenu();

                log.info("sel view");
                return true;
            }
        });

        imageButton.setBackgroundColor(getResources().getColor(R.color.prod_default_color));
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
                if (selectedItems.size() > 0){
                    clearSelected();
                } else {
                    sendAddProductRequest(prodTitle);
                    Toast.makeText(view.getContext(), getResources().getString(R.string.product_added) , Toast.LENGTH_SHORT).show();
                }
            }
        });
        return imageButton;
    }

    private void clearSelected() {
        for (String pstr : prodListStr) {
            View sButton = selectedItems.get(pstr);
            if (sButton != null) {
                sButton.setBackgroundColor(getResources().getColor(R.color.prod_default_color));
            }
            selectedItems.remove(pstr);
        }
        invalidateOptionsMenu();
    }

    private ArrayList<String> restoreProductLists(SharedPreferences preferences) {
        ArrayList<String> prodListStr = new ArrayList();
        StringTokenizer stokens = null;
        String savedList = preferences.getString(Constants.SAVED_LIST, "");
        if (savedList.length() > 0) {
            log.info("found savedList:" + savedList);
            stokens = new StringTokenizer(savedList, "|");
            while (stokens.hasMoreElements()) {
                prodListStr.add(stokens.nextElement().toString());
            }
        } else {
            stokens = new StringTokenizer(getApplicationContext().getResources().getString(R.string.def_products), "|");
            while (stokens.hasMoreElements()) {
                prodListStr.add(stokens.nextElement().toString());
            }
        }
        return prodListStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        if (selectedItems.size() > 0) {
            menu.findItem(R.id.action_delete).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_list) {
            saveProducts();
            SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            currentPreferences.edit().putString(Constants.LIST_ID, "0").commit();

            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            return true;
        }
        if (id == R.id.action_delete) {
            final LinearLayout scrollView = (LinearLayout) findViewById(R.id.productslist);
            for (Iterator<String> i = prodListStr.iterator(); i.hasNext(); ) {
                String str = i.next();
                if (selectedItems.get(str) != null) {
                    i.remove();
                    scrollView.removeView(selectedItems.get(str));
                }

            }
            selectedItems.clear();
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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



