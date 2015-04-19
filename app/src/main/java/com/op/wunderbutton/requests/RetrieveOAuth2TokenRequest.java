package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.R;
import com.op.wunderbutton.tools.Constants;

public class RetrieveOAuth2TokenRequest extends WebApiRequest
{
	public RetrieveOAuth2TokenRequest(Context context, String feedlyCode)
	{
		super(context.getResources().getString(R.string.wunderlist_api_auth), Constants.POST, context);
		setMethod(R.string.wunderlist_exchange_code_for_tokens);
		addParam(R.string.wunderlist_param_client_id, R.string.wunderlist_client_id);
		addParam(R.string.wunderlist_param_code, feedlyCode);
		addParam(R.string.wunderlist_param_client_secret, R.string.wunderlist_client_secret);
	}
}
