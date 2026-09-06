package com.yadstore.app;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NHostHelper {
    private static final String AUTH_URL = "https://crljwqjxbicovyufxive.auth.ap-southeast-1.nhost.run/v1";
    private static String authToken = "";
    
    public static void setToken(String token) { authToken = token; }
    public static String getToken() { return authToken; }
    
    public static JSONObject login(String email, String password) throws Exception {
        URL url = new URL(AUTH_URL + "/signin/email-password");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("password", password);
        
        OutputStream os = conn.getOutputStream();
        os.write(body.toString().getBytes());
        os.flush();
        os.close();
        
        int code = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
            code == 200 ? conn.getInputStream() : conn.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        
        JSONObject response = new JSONObject(sb.toString());
        
        if (code == 200) {
            JSONObject session = response.optJSONObject("session");
            if (session != null) {
                authToken = session.optString("accessToken", "");
                return new JSONObject().put("success", true).put("token", authToken);
            }
        }
        
        return new JSONObject().put("success", false).put("error", "Login gagal");
    }
    
    public static JSONObject register(String email, String password) throws Exception {
        URL url = new URL(AUTH_URL + "/signup/email-password");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("password", password);
        
        OutputStream os = conn.getOutputStream();
        os.write(body.toString().getBytes());
        os.flush();
        os.close();
        
        int code = conn.getResponseCode();
        
        if (code == 200) {
            return new JSONObject().put("success", true);
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        
        return new JSONObject().put("success", false).put("error", "Registrasi gagal");
    }
}