package com.op.wunderbutton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.op.wunderbutton.oauth2.WebApiHelper;
import com.op.wunderbutton.requests.GetCodeRequest;
import com.op.wunderbutton.requests.RetrieveOAuth2TokenRequest;

import org.json.JSONObject;

import java.util.HashMap;

import lombok.extern.java.Log;

@Log
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("init");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            WebView description = (WebView)rootView.findViewById(R.id.description);
            description.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            description.getSettings().setJavaScriptEnabled(true);
            description.getSettings().setDefaultTextEncodingName("utf-8");
            description.getSettings().setLoadWithOverviewMode(true);
            description.getSettings().setSupportZoom(true);
            description.getSettings().setBuiltInZoomControls(true);
            description.requestFocus(View.FOCUS_DOWN);
            description.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            GetCodeRequest request = new GetCodeRequest(getActivity());

               /* WebViewClient must be set BEFORE calling loadUrl! */
            description.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url)  {
                    if (url.contains("localhost")) {
                        try {
                            if (url.contains("code=")) {
                                String code = url.substring(url.indexOf("code=")+5);
                                log.info("code: " + code);
                                WebApiHelper.register(view.getContext());
                                WebApiHelper.getInstance().saveToSharedPreferences(view.getContext(), R.string.feedly_api_refresh_token, code);

                                RetrieveOAuth2TokenRequest oAuth2TokenRequest = new RetrieveOAuth2TokenRequest(view.getContext(),code);
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("client_id", view.getContext().getResources().getString(R.string.feedly_client_id));
                                params.put("client_secret", view.getContext().getResources().getString(R.string.feedly_client_secret));
                                params.put("code", code);

                                WebApiHelper.getInstance().refreshAccessToken(new JSONObject(params));

                                //CHANGE VIEW
                                Intent i = new Intent(view.getContext().getApplicationContext(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                view.getContext().getApplicationContext().startActivity(i);
                            } else if (url.indexOf("error=")!=-1) {
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    System.out.println("onPageFinished : " + url);

                }

            });


            description.loadUrl(request.getEncodedUrl());//
            return rootView;
        }
    }
}
