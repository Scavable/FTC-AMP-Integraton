package com.scavable.amp;

import com.scavable.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static com.scavable.amp.TwoFactorAuth.getTOTPCode;

public class AMP implements Runnable {
    public static final Properties properties = new Properties();
    private String coreURL;
    private String baseURL;
    public AMP() throws IOException {
        properties.load(Main.class.getClassLoader().getResourceAsStream("app.properties"));
        baseURL = properties.getProperty("baseAMPURL");
        coreURL = baseURL.concat("API/Core/");

        try {
            JSONObject loginInfo = Login();
            JSONObject sessionID = new JSONObject();
            sessionID.put("SESSIONID", loginInfo.getString("sessionID"));

            System.out.println(sessionID);
            GetRoleIDs(sessionID);
            GetInstances(sessionID);
            Logout(sessionID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject GetInstances(JSONObject sessionID) throws MalformedURLException {
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

    private JSONObject GetRoleIDs(JSONObject sessionID) throws MalformedURLException {
        JSONObject response;

        try {
             response = SendPostRequest(new URL(coreURL.concat("GetRoleIds")), sessionID);
            //return SendPostRequest(new URL(coreURL.concat("GetRoleIds")), sessionID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Get Role Ids Method Data: "+response);
        return response;
    }

    private void Logout(JSONObject sessionID) throws MalformedURLException {

        try {
            SendPostRequest(new URL(coreURL.concat("Logout")), sessionID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    JSONObject Login() throws IOException {

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

    public static JSONObject SendPostRequest(URL url, JSONObject jsonObject) throws IOException {
        StringBuilder response;

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        con.setDoInput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            if (response.toString().equals("null")){
                return new JSONObject();
            }

            //System.out.println("Response: "+response);
        }
        return new JSONObject(response.toString());
    }


    @Override
    public void run() {

    }
}
