package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.R;

public class RetrieveSubscriptionsRequest extends WebApiRequest
{
	public RetrieveSubscriptionsRequest(Context context)
	{
		super(context.getResources().getString(R.string.feedly_api_url), "GET", context);
		setMethod(R.string.feedly_api_get_subscriptions);
	}
}
