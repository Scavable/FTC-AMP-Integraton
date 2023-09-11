package com.scavable.amp.methods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.scavable.amp.AMP.SendPostRequest;

public class GetInstances {
    private String baseURL;
    private JSONObject sessionID;

    public  GetInstances(String baseURL, JSONObject sessionID) throws MalformedURLException {
        this.baseURL = baseURL;
        this.sessionID = sessionID;
    }

    public JSONObject GetInstances() throws MalformedURLException {
        JSONObject response;
        try {
            response = SendPostRequest(new URL(baseURL.concat("API/ADSModule/GetInstances")), sessionID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Get Instances Method Data: "+response);
        JSONArray result = (JSONArray) response.get("result");
        JSONObject instances = result.getJSONObject(0);
        System.out.println(instances.keySet());
        JSONArray availableInstances = (JSONArray) instances.get("AvailableInstances");
        for(Object temp : availableInstances){
            JSONObject object = (JSONObject) temp;
            System.out.println(object.get("FriendlyName").toString().concat(" - " + object.get("InstanceID").toString()));
        }

        return response;
    }
}
