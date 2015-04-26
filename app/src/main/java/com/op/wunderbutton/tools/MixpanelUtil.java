package com.op.wunderbutton.tools;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alex on 26.04.2015.
 */
public class MixpanelUtil {

    public static void sendMixPOpened(MixpanelAPI mMixpanel,  String eventName, String value) {
        JSONObject props = new JSONObject();
        try {
            props.put(eventName, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMixpanel.track("opened", props);
    }
}
