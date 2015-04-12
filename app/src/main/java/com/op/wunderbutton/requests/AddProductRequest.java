package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.R;
import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.tools.Constants;

import org.json.JSONObject;

public class AddProductRequest extends WebApiRequest
{
	public AddProductRequest(Context context, JSONObject jsonObject)
	{
		super(context.getResources().getString(R.string.wunderlist_api_url), Constants.POST, context);
		setMethod(R.string.wunderlist_api_tasks);
        setJsonObject(jsonObject);
	}
}
