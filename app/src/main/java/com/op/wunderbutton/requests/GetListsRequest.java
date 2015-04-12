package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.R;
import com.op.wunderbutton.oauth2.WebApiRequest;

import org.json.JSONObject;

public class GetListsRequest extends WebApiRequest
{
	public GetListsRequest(Context context, JSONObject jsonObject)
	{
		super(context.getResources().getString(R.string.wunderlist_api_url), "GET", context);
		setMethod(R.string.wunderlist_api_lists);
        setJsonObject(jsonObject);
	}
}
