package com.scavable.amp;

import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class TwoFactorAuth {

    public static void EnableTwoFactorAuth(String coreURL) throws IOException {
        JSONObject object = new JSONObject();
        object.put("Username", AMP.properties.getProperty("username"));
        object.put("Password", AMP.properties.getProperty("password"));

        JSONObject enableTwoFactor = AMP.SendPostRequest(new URL(coreURL.concat("/EnableTwoFactor")), object);

        JSONObject result = enableTwoFactor.getJSONObject("result");
        JSONObject resultWithKey = result.getJSONObject("Result");
        String key = resultWithKey.getString("ManualKey");

        AMP.properties.setProperty("key", key);

        String twoFactorCode = getTOTPCode(key);

        ConfirmTwoFactorAuth(twoFactorCode);
    }

    public static void ConfirmTwoFactorAuth(String twoFactorCode) throws IOException {
        JSONObject object = new JSONObject();
        object.put("Username", AMP.properties.getProperty("username"));
        object.put("TwoFactorCode", twoFactorCode);

        AMP.SendPostRequest(new URL("https://amp.ftc.gg/API/Core/ConfirmTwoFactorSetup"), object);
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }


}
