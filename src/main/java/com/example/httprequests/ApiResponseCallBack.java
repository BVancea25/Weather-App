package com.example.httprequests;

import org.json.JSONObject;

public interface ApiResponseCallBack {
    void onResponse(JSONObject data);
    void onFailure(Exception e);
}
