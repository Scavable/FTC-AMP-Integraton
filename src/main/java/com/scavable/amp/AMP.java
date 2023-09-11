package com.scavable.amp;

import com.scavable.Main;
import com.scavable.amp.methods.GetInstances;
import com.scavable.amp.methods.Login;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AMP implements Runnable {
    public static final Properties properties = new Properties();
    private final String coreURL;
    private final String baseURL;
    public AMP() throws IOException {
        properties.load(Main.class.getClassLoader().getResourceAsStream("app.properties"));
        baseURL = properties.getProperty("baseAMPURL");
        coreURL = baseURL.concat("API/Core/");

        try {
            JSONObject loginInfo = Login.Login(properties, coreURL);
            JSONObject sessionID = new JSONObject();
            sessionID.put("SESSIONID", loginInfo.getString("sessionID"));

            System.out.println(sessionID);
            GetRoleIDs(sessionID);

            GetInstances getInstances = new GetInstances(baseURL, sessionID);
            getInstances.GetInstances();

            Logout(sessionID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
