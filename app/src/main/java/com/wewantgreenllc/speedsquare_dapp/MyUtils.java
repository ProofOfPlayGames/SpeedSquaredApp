package com.wewantgreenllc.speedsquare_dapp;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClients;

//import org.apache.http.client.HttpClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyUtils {

    Activity activity;

    public MyUtils(Activity activity){
        this.activity = activity;
    }

    public void savePermanent(String key, byte[] value){
        try {
            FileOutputStream outputStream;
            outputStream = activity.getApplicationContext().openFileOutput(key, Context.MODE_PRIVATE);
            outputStream.write(value);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public byte[] readPermanent(String key, int size){
        try {
            FileInputStream fin = activity.getApplicationContext().openFileInput(key);
            byte[] secret = new byte[size];
            fin.read(secret);
            return secret;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    protected String httpsPost(String urlS, String json) {

//        final String MULTIPART_BOUNDARY = "xxYYzzSEPARATORzzYYxx";
//        final String MULTIPART_SEPARATOR = "--" + MULTIPART_BOUNDARY + "\n";
//        final String MULTIPART_FORM_DATA = "Content-Disposition: form-data; name=\"%s\"\n\n";
//        final String FORM_DATA_FILE1 = "file1";
//        final String FORM_DATA_FILE2 = "file2";

//        try {
//            trustServer(new URL("https", "104.236.68.131", 3004, ""));
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            HttpsURLConnection h = CustomCAHttpsProvider.getHttpsUrlConnection(url, activity, 3, true);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        OutputStream outputStream;
        Integer responseCode = 0;
        String responseBody = "";
        try {
            URL url = new URL(urlS);
            HttpsURLConnection urlConnection = CustomCAHttpsProvider.getHttpsUrlConnection(urlS, activity, R.raw.server, true);//(HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setConnectTimeout(6000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(json);
            writer.flush();
            writer.close();
            outputStream.close();
            urlConnection.connect();
            responseCode = urlConnection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            responseBody = br.lines().collect(Collectors.joining());
            System.out.println(responseBody);
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }


//    try {
//        AsyncHttpClient client = new AsyncHttpClient();
//        // Http Request Params Object
//        RequestParams params = new RequestParams();
//        String u = "B2mGaME";
//        String au = "gamewrapperB2M";
//        // String mob = "880xxxxxxxxxx";
//        params.put("usr", u.toString());
//        params.put("aut", au.toString());
//        params.put("uph", MobileNo.toString());
//        //  params.put("uph", mob.toString());
//        client.post("http://196.6.13.01:88/ws/game_wrapper_reg_check.php", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                playStatus = response;
//                //////Get your Response/////
//                Log.i(getClass().getSimpleName(), "Response SP Status. " + playStatus);
//            }
//            @Override
//            public void onFailure(Throwable throwable) {
//                super.onFailure(throwable);
//            }
//        });
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//

//    public String sendHttpsRequest(String urlS, String params, Activity activity) {
//        String result = "";
//        try {
//            URL url = new URL(urlS);
//            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//            connection.setSSLSocketFactory(KeyPinStore.getInstance(activity).getContext().getSocketFactory()); // Tell the URLConnection to use a SocketFactory from our SSLContext
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setConnectTimeout(10000);
//            connection.setReadTimeout(10000);
//            PrintWriter out = new PrintWriter(connection.getOutputStream());
//            out.println(params);
//            out.close();
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8192);
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                result = result.concat(inputLine);
//            }
//            in.close();
//            //} catch (IOException e) {
//        } catch (IOException e) {
//            result = e.toString();
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            throw new RuntimeException(e);
//        } catch (KeyStoreException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (KeyManagementException e) {
//            throw new RuntimeException(e);
//        }
//        return result;
//    }
//
//
//
//    private InputStream getInputStream(String urlStr) throws IOException //, String user, String password
//    {
//        URL url = new URL(urlStr);
//        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//
//        // Create the SSL connection
//        SSLContext sc;
//
//        try {
//            sc = SSLContext.getInstance("TLS");
//            sc.init(null, null, new java.security.SecureRandom());
//        } catch (KeyManagementException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        conn.setSSLSocketFactory(sc.getSocketFactory());
//
//        // Use this if you need SSL authentication
////        String userpass = user + ":" + password;
////        String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
////        conn.setRequestProperty("Authorization", basicAuth);
//
//        // set Timeout and method
//        conn.setReadTimeout(7000);
//        conn.setConnectTimeout(7000);
//        conn.setRequestMethod("POST");
//        conn.setDoInput(true);
//
//        // Add any data you wish to post here
//
//        conn.connect();
//        return conn.getInputStream();
//    }




    public void trustServer(URL url){

        try
        {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] tmlist = {new MyTrustManager()};
            context.init(null, tmlist, null);
            conn.setSSLSocketFactory(context.getSocketFactory());
            conn.setRequestMethod("GET");
            int rcode = conn.getResponseCode();
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("ya fucked1");
        } catch (KeyManagementException e)
        {
            System.out.println("ya fucked2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();


    public String httpPost(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        System.out.println("lololol2");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        System.out.println("lololol3");
        try (Response response = client.newCall(request).execute()) {
            System.out.println("lololol4");
            return response.body().string();
        }
    }

//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    OkHttpClient client = new OkHttpClient();
//
//    public Call post(String url, String json, Callback callback) {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(callback);
//        return call;
//    }


//    public void sendRequest(String uri, List<NameValuePair> params) {

//        HttpClient client = HttpClient.newHttpClient();



//        System.out.println("helloooooooooo4");
//        HttpClient httpclient = HttpClients.createDefault();
//        System.out.println("helloooooooooo7");
//        HttpPost httppost = new HttpPost(uri);
//System.out.println("helloooooooooo");
//// Request parameters and other properties.
//
//        try {
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            System.out.println("helloooooooooo2");
//            //Execute and get the response.
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//            System.out.println("helloooooooooo3");
//            if (entity != null) {
//                try (InputStream instream = entity.getContent()) {
//                    // do something useful
//                }
//            }
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        } catch (ClientProtocolException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
