package com.op.wunderbutton;

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

import com.google.gson.reflect.TypeToken;
import com.op.wunderbutton.model.TokenRequest;
import com.op.wunderbutton.model.WList;
import com.op.wunderbutton.oauth2.WebApiHelper;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.oauth2.tasks.OnApiRequestListener;
import com.op.wunderbutton.requests.GetCodeRequest;
import com.op.wunderbutton.requests.GetListsRequest;
import com.op.wunderbutton.tools.Constants;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;

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
            description.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
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

                    OnApiRequestListener requestListener = new OnApiRequestListener() {

                        @Override
                        public void onStartRequest()
                        {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinishRequest(String response)
                        {
                            log.info("FINISHED +" + response);
                            ArrayList<WList> list = Constants.gson.fromJson(response,
                                    new TypeToken<ArrayList<WList>>() {}.getType());

//                            Intent i = new Intent(view.getContext().getApplicationContext(), AddProductActivity.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            view.getContext().getApplicationContext().startActivity(i);

                            // saveFeedlyRefreshTokenFromResponseToPreferences(response);
                        }

                        @Override
                        public void onException(Exception ex)
                        {
                            // TODO Auto-generated method stub

                        }
                    };

                    if (url.contains(Constants.LOCALHOST)) {
                        try {
                            if (url.contains(Constants.CODE_EQ)) {
                                String code = url.substring(url.indexOf(Constants.CODE_EQ)+5);
                                WebApiHelper.register(view.getContext());
                                WebApiHelper.getInstance().saveToSharedPreferences(view.getContext(), R.string.feedly_api_refresh_token, code);

                                TokenRequest tokenRequest = new TokenRequest();
                                tokenRequest.setClient_id(view.getContext().getResources().getString(R.string.wunderlist_client_id));
                                tokenRequest.setCode(code);
                                tokenRequest.setClient_secret(view.getContext().getResources().getString(R.string.wunderlist_client_secret));

//                                HashMap<String, String> params = new HashMap<String, String>();
//                                params.put("client_id", view.getContext().getResources().getString(R.string.wunderlist_client_id));
//                                params.put("client_secret", view.getContext().getResources().getString(R.string.wunderlist_client_secret));
//                                params.put("code", code);
//                                ;
                                WebApiHelper.getInstance().refreshAccessToken(new JSONObject(Constants.gson.toJson(tokenRequest)));

                                WList wList = new WList();
                                LoadWebUrlAsyncTask task = new LoadWebUrlAsyncTask();
                                GetListsRequest request = new GetListsRequest(view.getContext());
                                task.execute(request);
                                task.setOnWebRequestCallback(requestListener);


                                //CHANGE VIEW
//                                Intent i = new Intent(view.getContext().getApplicationContext(), AddProductActivity.class);
//                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                view.getContext().getApplicationContext().startActivity(i);
                            } else if (url.indexOf("error=")!=-1) {
                                log.info("error in url:" + url);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }




                }

            });


            description.loadUrl(request.getEncodedUrl());//
            return rootView;
        }
    }
}
