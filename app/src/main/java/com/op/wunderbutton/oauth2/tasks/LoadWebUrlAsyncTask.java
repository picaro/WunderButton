package com.op.wunderbutton.oauth2.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.op.wunderbutton.oauth2.WebApiRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadWebUrlAsyncTask extends AsyncTask<WebApiRequest, Void, String>
{
	private OnApiRequestListener apiListener;
	
	public void setOnWebRequestCallback(OnApiRequestListener callback)
	{
		this.apiListener = callback;
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (apiListener != null)
		{
			apiListener.onStartRequest();
		}
	}
	
	@Override
	protected String doInBackground(WebApiRequest... request)
	{
		try
		{
			if (request == null)
			{
				return null;
			}
			if (request.length != 1)
			{
				return null;
			}
			URL url = new URL(request[0].getEncodedUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(request[0].getRequestMethod());

            if (!TextUtils.isEmpty(request[0].getOAuthToken()))
            {
                conn.setRequestProperty("Authorization", "OAuth " + request[0].getOAuthToken());
            }

            if (request[0].getRequestMethod().equals("POST")) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                JSONObject jsonObject = request[0].getJsonObject();
                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonObject.toString());
                wr.flush();

                Log.i("TAG", "RESP O CODE>>>" + conn.getResponseCode());


            }

			return readStream(conn.getInputStream());
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			callWebRequestException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			callWebRequestException(e);
		}
		return null;
	}
	
	private String readStream(InputStream in)
	{
		BufferedReader reader = null;
		String page = "";
		try
		{
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null)
			{
				page += line;
				line = reader.readLine();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			callWebRequestException(ex);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					callWebRequestException(ex);
				}
			}
		}
		return page;
		
	}
	
	private void callWebRequestException(Exception ex)
	{
		if (apiListener == null)
		{
			return;
		}
		apiListener.onException(ex);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (apiListener != null)
		{
			apiListener.onFinishRequest(result);
		}
	}
}
