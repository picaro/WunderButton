package com.op.wunderbutton.oauth2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.op.wunderbutton.R;
import com.op.wunderbutton.oauth2.tasks.LoadWebUrlAsyncTask;
import com.op.wunderbutton.oauth2.tasks.OnApiRequestListener;
import com.op.wunderbutton.requests.RefreshTokenRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;

public class WebApiHelper
{

	private static WebApiHelper instance;
	
	private final Context context;
	
	private WebApiHelper(Context context)
	{
		if (context == null)
		{
			throw new InvalidParameterException();
		}
		this.context = context;
	}
	
	public static void register(Context context)
	{
		instance = new WebApiHelper(context);
	}
	
	public static WebApiHelper getInstance()
	{
		return instance;
	}
	
//	public boolean handleFeedlyAuthenticationResponse(String url, OnApiRequestListener callback, JSONObject jsonObject)
//	{
//		if (!url.startsWith(getResourceString(R.string.wunderlist_redirect_uri)))
//		{
//			return false;
//		}
//
//		String code = getCodeFromUrl(url);
//		if (code == null)
//		{
//			return false;
//		}
//
//		LoadWebUrlAsyncTask getFeedlyAccessTokenAsyncTask = new LoadWebUrlAsyncTask();
//		getFeedlyAccessTokenAsyncTask.setOnWebRequestCallback(callback);
//		WebApiRequest request = new RetrieveOAuth2TokenRequest(context, code);
//        request.setJsonObject(jsonObject);
//		getFeedlyAccessTokenAsyncTask.execute(request);
//		return true;
//	}
	
//	public void refreshAccessTokenIfNeeded(JSONObject jsonObject)
//	{
//		if (shouldRefreshAccesToken())
//		{
//			refreshAccessToken(jsonObject);
//		}
//	}
	
//	public boolean shouldRefreshAccesToken()
//	{
//		try
//		{
////			long expirationDelta = Long.parseLong(getSharedPreferenceValue(R.string. feedly_api_expires_in));
////			long timestamp = Long.parseLong(getSharedPreferenceValue(R.string.feedly_api_timestamp));
//			long currentTime = System.currentTimeMillis()/1000;
////			if (currentTime > timestamp + expirationDelta)
////			{
////				return true;
////			}
//		}
//		catch (NumberFormatException e)
//		{
//			e.printStackTrace();
//			return true;
//		}
//		return false;
//	}
	
	public void refreshAccessToken(JSONObject jsonObject)
	{
		String refreshToken = getSharedPreferenceValue(R.string.feedly_api_refresh_token);
		if (TextUtils.isEmpty(refreshToken))
		{
			return;
		}
		LoadWebUrlAsyncTask refreshFeedlyAcessTokensAsyncTask = new LoadWebUrlAsyncTask();
		OnApiRequestListener requestListener = new OnApiRequestListener() {
			
			@Override
			public void onStartRequest()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinishRequest(String response)
			{
				saveFeedlyRefreshTokenFromResponseToPreferences(response);
			}
			
			@Override
			public void onException(Exception ex)
			{
				// TODO Auto-generated method stub
				
			}
		};
		
		refreshFeedlyAcessTokensAsyncTask.setOnWebRequestCallback(requestListener);
		WebApiRequest request = new RefreshTokenRequest(context, refreshToken);
        request.setJsonObject(jsonObject);
		refreshFeedlyAcessTokensAsyncTask.execute(request);
	}
	
	private boolean saveFeedlyRefreshTokenFromResponseToPreferences(String response)
	{
		try
		{
			JSONObject json = new JSONObject(response);
			String accessToken = json.getString(getResourceString(R.string.feedly_api_access_token));
			String clientId = getResourceString(R.string.wunderlist_client_id);
			saveToSharedPreferences(R.string.feedly_api_access_token, accessToken);
			saveToSharedPreferences(R.string.feedly_api_param_client_id, clientId);
			return true;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return false;
	}

//	public boolean saveFeedlyTokensFromResponseToPreferences(String response)
//	{
//		try
//		{
//			JSONObject json = new JSONObject(response);
//			String accessToken = json.getString(getResourceString(R.string.feedly_api_access_token));
//			String refreshToken = json.getString(getResourceString(R.string.feedly_api_refresh_token));
//			String userId = json.getString(getResourceString(R.string.feedly_api_user_id));
//			String expiresIn = json.getString(getResourceString(R.string.feedly_api_expires_in));
//			String timestamp = Long.toString(System.currentTimeMillis()/1000);
//			saveToSharedPreferences(R.string.feedly_api_access_token, accessToken);
//			saveToSharedPreferences(R.string.feedly_api_refresh_token, refreshToken);
//			saveToSharedPreferences(R.string.feedly_api_user_id, userId);
//			saveToSharedPreferences(R.string.feedly_api_expires_in, expiresIn);
//			saveToSharedPreferences(R.string.feedly_api_timestamp, timestamp);
//			return true;
//
//		}
//		catch (JSONException e)
//		{
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	private void saveToSharedPreferences(int prefKeyId, String value)
	{
		SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor currentEditor = currentPreferences.edit();
		currentEditor.putString(getResourceString(prefKeyId), value);
		currentEditor.commit();
	}

    public void saveToSharedPreferences(Context context, int prefKeyId, String value)
    {
        SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor currentEditor = currentPreferences.edit();
        currentEditor.putString(getResourceString(prefKeyId), value);
        currentEditor.commit();
    }

//    private String getCodeFromUrl(String url)
//    {
//    	try
//    	{
//			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), HTTP.UTF_8);
//			String paramName = getResourceString(R.string.wunderlist_param_code);
//	    	for (NameValuePair param : params)
//	    	{
//	    		if (param.getName().equals(paramName))
//	    		{
//	    			return param.getValue();
//	    		}
//	    	}
//		}
//    	catch (URISyntaxException e)
//    	{
//			// TODO do something??
//		}
//    	return null;
//    }
    
	private String getResourceString(int resourceId)
	{
		return context.getResources().getString(resourceId);
	}
	
	private String getSharedPreferenceValue(int resourceKeyId)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String preferenceValue = preferences.getString(context.getResources().getString(resourceKeyId), "");
		return preferenceValue;
	}
}
