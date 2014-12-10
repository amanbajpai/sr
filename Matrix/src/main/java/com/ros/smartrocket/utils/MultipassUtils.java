package com.ros.smartrocket.utils;

import android.util.Base64;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MultipassUtils {
    private final static String TAG = MultipassUtils.class.getSimpleName();
    private final static String SITE_KEY = "smartrocket"; // this has to be all lower cases
    private final static String API_KEY = "49dff71c32cef9b00b4cdb92446b054861167b2d"; // you'll find the multipass key in your admin

    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    private String uid;
    private String expires;
    private String customerEmail;
    private String customerName;

    public MultipassUtils(String uid, long validTimeInMillisFromCurrent,
                          String customerEmail, String customerName) {
        this.uid = uid;
        this.expires = getExpireFormatedDate(validTimeInMillisFromCurrent);
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }

    public String buildUrl() {
        String resultUrl = "";
        try {
            L.i(TAG, "== Generating ==");

            L.i(TAG, "Create the encryption key using a 16 byte SHA1 digest of your api key and subdomain");
            String salted = API_KEY + SITE_KEY;
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(salted.getBytes("utf-8"));
            byte[] digest = md.digest();
            SecretKeySpec key = new SecretKeySpec(Arrays.copyOfRange(digest, 0, 16), "AES");

            L.i(TAG, "Generate a random 16 byte IV");
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            L.i(TAG, "Build json data");
            JSONObject userData = new JSONObject();
            userData.put("uid", uid);
            userData.put("expires", expires);
            userData.put("customer_email", customerEmail);
            userData.put("customer_name", customerName);

            L.i(TAG, "Data: " + userData.toString());

            String data = userData.toString();

            L.i(TAG, "Encrypt data using AES128-cbc");
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encrypted = aes.doFinal(data.getBytes("utf-8"));

            L.i(TAG, "Prepend the IV to the encrypted data");
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            L.i(TAG, "Base64 encode the encrypted data");
            byte[] multipass = Base64.encode(combined, Base64.DEFAULT);

            L.i(TAG, "Build an HMAC-SHA1 signature using the encoded string and your api key");
            SecretKeySpec apiKey = new SecretKeySpec(API_KEY.getBytes("utf-8"), "HmacSHA1");
            Mac hmac = Mac.getInstance("HmacSHA1");
            hmac.init(apiKey);
            byte[] rawHmac = hmac.doFinal(multipass);
            byte[] signature = Base64.encode(rawHmac, Base64.DEFAULT);

            L.i(TAG, "Finally, URL encode the multipass and signature");
            String multipassString = URLEncoder.encode(new String(multipass));
            String signatureString = URLEncoder.encode(new String(signature));

            L.i(TAG, "== Finished ==");

            resultUrl = "https://" + SITE_KEY + ".desk.com/customer/authentication/multipass/callback?multipass=" + multipassString + "&signature=" + signatureString;
            L.i(TAG, "URL: " + resultUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultUrl;
    }

    private String getExpireFormatedDate(long validTimeFromCurrent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() + validTimeFromCurrent);

        return df.format(calendar.getTime());
    }
}
