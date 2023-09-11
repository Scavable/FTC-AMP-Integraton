package com.scavable.amp.methods;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static com.scavable.amp.AMP.SendPostRequest;
import static com.scavable.amp.TwoFactorAuth.getTOTPCode;

public class Login {

    public static JSONObject Login(Properties properties, String coreURL) throws IOException {

        URL url = new URL(coreURL.concat("Login"));

        JSONObject login = new JSONObject();
        login.put("username", properties.getProperty("username"));
        login.put("password", properties.getProperty("password"));
        login.put("token", getTOTPCode(properties.getProperty("key")));
        login.put("rememberMe", false);

        JSONObject response = SendPostRequest(url, login);

        System.out.println("Login Method Data: "+response);

        return response;
    }
}
