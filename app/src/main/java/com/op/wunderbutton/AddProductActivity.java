package com.op.wunderbutton;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.requests.AddProductRequest;

import org.json.JSONObject;

import java.util.HashMap;

import lombok.extern.java.Log;


@Log
public class AddProductActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("onCreate");

        String[] aaa = new String[]{"Мусорные пакеты", "Пленка упаковочная" ,"Масло сливочное", "Масло подс."};
//        String[] aaa = new String[]{"Туалетная бумага", "Стиральный порошек", "Зубная паста", "Ополаскивалка зубов"};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        LinearLayout scrollView = (LinearLayout) findViewById(R.id.productslist);
        for (final String prodTitle : aaa) {
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
            scrollView.addView(imageButton);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void sendAddProductRequest(String title) {
        HashMap params2 = new HashMap();
        params2.put("list_id", 145872426);
        params2.put("title", title);
        params2.put("completed", false);
        params2.put("starred", false);

        LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask();
        WebApiRequest request = new AddProductRequest(this.getApplicationContext(), new JSONObject(params2));
        getFeedlyAccessTokenAsyncTask.execute(request);

    }

}



