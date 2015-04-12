package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.R;
import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.tools.Constants;

public class GetListsRequest extends WebApiRequest
{
	public GetListsRequest(Context context)
	{
		super(context.getResources().getString(R.string.wunderlist_api_url), Constants.GET, context);
		setMethod(R.string.wunderlist_api_lists);
    }
}
