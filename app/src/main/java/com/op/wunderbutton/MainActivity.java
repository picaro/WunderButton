package com.op.wunderbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.op.wunderbutton.model.TokenRequest;
import com.op.wunderbutton.oauth2.WebApiHelper;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.oauth2.tasks.OnApiRequestListener;
import com.op.wunderbutton.requests.GetCodeRequest;
import com.op.wunderbutton.requests.GetListsRequest;
import com.op.wunderbutton.tools.Constants;
import com.op.wunderbutton.tools.MixpanelUtil;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import lombok.extern.java.Log;
import com.optimizely.Optimizely;

@Log
public class MainActivity extends ActionBarActivity {

    private MixpanelAPI mMixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("init");

        Optimizely.startOptimizely("AAM7hIkAk9WQeP3RBPSmCMsQRRLzRKzb~2872823291", getApplication());


        mMixpanel = MixpanelAPI.getInstance(getApplicationContext(), Constants.MIXPANEL_TOKEN);
        setMixPIdentify();
        MixpanelUtil.sendMixPOpened(mMixpanel, "phoneModel", android.os.Build.MODEL);

        super.onCreate(savedInstanceState);

        if (redirectOnSecondLaunch()) {
            return;
        }

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }



    private void setMixPIdentify() {
        try {
            Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            if (mMixpanel != null) {
                mMixpanel.identify(c.getString(c.getColumnIndex("display_name")));
            }
            c.close();
        } catch (Exception ex){
            log.info(ex.getMessage());
        }
    }


    @Override
    protected void onDestroy() {
        mMixpanel.flush();
        super.onDestroy();
    }


    private boolean redirectOnSecondLaunch() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String preferenceValue = preferences.getString(Constants.LIST_ID, "0");
        if (Integer.parseInt(preferenceValue) != 0) {
            Intent i = new Intent(getApplicationContext(), AddProductActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(i);
            return true;
        }
        ;
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_list) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MixpanelUtil.sendMixPOpened(mMixpanel, "back pressed", "");
        WebView webView = (WebView) this.findViewById(R.id.webpage);
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        private WebView webView;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            webView = (WebView) rootView.findViewById(R.id.webpage);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            //webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.requestFocus(View.FOCUS_DOWN);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            GetCodeRequest request = new GetCodeRequest(getActivity());

               /* WebViewClient must be set BEFORE calling loadUrl! */
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
//                    view.findViewById(R.id.imageLoading1).setVisibility(View.GONE);
                    //view.findViewById(R.id.webpage).setVisibility(View.VISIBLE);

                    OnApiRequestListener requestListener = new OnApiRequestListener() {

                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void onFinishRequest(String response) {

                            Intent i = new Intent(getActivity().getApplicationContext(), SelectListActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra(Constants.LISTS_JSON, response);
                            getActivity().getApplicationContext().startActivity(i);
                        }

                        @Override
                        public void onException(Exception ex) {
                        }
                    };

                    if (url.contains(Constants.LOCALHOST)) {
                        try {
                            if (url.contains(Constants.CODE_EQ)) {
                                WebView webView = (WebView) view.findViewById(R.id.webpage);
                                webView.destroy();

                                String code = url.substring(url.indexOf(Constants.CODE_EQ) + 5);
                                WebApiHelper.register(view.getContext());
                                WebApiHelper.getInstance().saveToSharedPreferences(view.getContext(), R.string.wunderlist_refresh_token, code);

                                TokenRequest tokenRequest = new TokenRequest();
                                tokenRequest.setClient_id(view.getContext().getResources().getString(R.string.wunderlist_client_id));
                                tokenRequest.setCode(code);
                                tokenRequest.setClient_secret(view.getContext().getResources().getString(R.string.wunderlist_client_secret));

                                WebApiHelper.getInstance().refreshAccessToken(new JSONObject(Constants.gson.toJson(tokenRequest)));

                                LoadWebUrlAsyncTask task = new LoadWebUrlAsyncTask();
                                GetListsRequest request = new GetListsRequest(view.getContext());
                                task.execute(request);
                                task.setOnWebRequestCallback(requestListener);

                            } else if (url.indexOf("error=") != -1) {
                                log.info("error in url:" + url);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }

            });


            webView.loadUrl(request.getEncodedUrl());//
            return rootView;
        }
    }
}
