package com.op.wunderbutton.requests;

import android.content.Context;

import com.op.wunderbutton.oauth2.WebApiRequest;
import com.op.wunderbutton.R;
import com.op.wunderbutton.tools.Constants;


public class GetCodeRequest extends WebApiRequest
{
	public GetCodeRequest(Context context)
	{
        super(context.getResources().getString(R.string.wunderlist_api_auth), Constants.GET, context);
		setMethod(R.string.wunderlist_authenticate_user);
		addParam(R.string.feedly_api_param_response_type, R.string.feedly_api_param_response_type_default_val);
		addParam(R.string.feedly_api_param_client_id, R.string.wunderlist_client_id);
		addParam(R.string.feedly_api_param_redirect_uri, R.string.wunderlist_redirect_uri);
		addParam(R.string.feedly_api_param_scope, R.string.feedly_api_param_scope_default_val);
	}
}
