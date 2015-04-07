package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.R;
import com.op.wunderbutton.oauth2.WebApiRequest;

public class RetrieveSubscriptionsRequest extends WebApiRequest
{
	public RetrieveSubscriptionsRequest(Context context)
	{
        //145872426 prod
        /**
         * {
         "list_id": 145872426,
         "title": "Toilet Paper",
         "assignee_id": 13724835,
         "completed": true,
         "starred": false
         }
         */
		super(context.getResources().getString(R.string.feedly_api_url), "GET", context);
		setMethod(R.string.feedly_api_get_subscriptions);
	}
}
