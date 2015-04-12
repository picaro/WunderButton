package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.R;
import com.op.wunderbutton.tools.Constants;


public class RefreshTokenRequest extends WebApiRequest
{
	public RefreshTokenRequest(Context context, String refreshToken)
	{
		super(context.getResources().getString(R.string.wunderlist_api_auth), Constants.POST, context);
        setMethod(R.string.feedly_api_exchange_code_for_tokens);
        addParam(R.string.feedly_api_param_client_id, R.string.wunderlist_client_id);
        addParam(R.string.wunderlist_param_code, refreshToken);
        addParam(R.string.feedly_api_param_client_secret, R.string.wunderlist_client_secret);
	}
}
