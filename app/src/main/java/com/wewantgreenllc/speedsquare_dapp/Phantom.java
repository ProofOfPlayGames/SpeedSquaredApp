package com.wewantgreenllc.speedsquare_dapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sol4k.PublicKey;
import org.sol4k.tweetnacl.TweetNacl;
import org.sol4k.tweetnacl.TweetNaclFast;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

//import dorkbox.json.Json;


public class Phantom {

    TweetNacl.Box.KeyPair kp;
    Activity activity;

    public Phantom(Activity activity){
        System.out.println("penis3");
        this.activity = activity;
        this.kp = TweetNacl.Box.keyPair();
        System.out.println(new PublicKey(kp.getSecretKey()));
    }

    public Phantom(Activity activity, byte[] secretKey){
        System.out.println("penis4");
        this.activity = activity;
        this.kp = TweetNacl.Box.keyPair_fromSecretKey(secretKey);
        System.out.println(new PublicKey(kp.getSecretKey()));
    }

    public void connect(){
        System.out.println("oeroekdmd");
        // pass the uri (scheme & screen path) of a screen defined from app XXX that you want to open (e.g HomeActivity)
        Uri uri = Uri.parse("https://phantom.app/ul/v1/connect");
        Uri.Builder newUri = uri.buildUpon();
        newUri.appendQueryParameter("app_url", "https://apps.apple.com/us/app/facebook/id284882215");
//        String encoded = Base58.encode("HAE1oNnc3XBmPudphRcHhyCvGShtgDYtZVzx2MocKEr1".getBytes(StandardCharsets.UTF_8));
//        System.out.println(encoded);
        System.out.println(kp.getPublicKey().toString());
        PublicKey pk = new PublicKey(kp.getPublicKey());
        newUri.appendQueryParameter("dapp_encryption_public_key", pk.toString());
        newUri.appendQueryParameter("redirect_link", "app://speed-square");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, newUri.build());
        System.out.println("peerur");


        //Verify if app XYZ has this screen path
//        PackageManager packageManager = activity.getPackageManager();
//        List<ResolveInfo> activities =
//                packageManager.queryIntentActivities(mapIntent, 0);
//        boolean isIntentSafe = activities.size() > 0;

        //Start HomeActivity of app XYZ because it's existed
//        if (isIntentSafe) {
            System.out.println("84nueu");
            this.activity.startActivity(mapIntent);
//        }
    }

    static long convertToLong(byte[] bytes)
    {
        long value = 0l;

        // Iterating through for loop
        for (byte b : bytes) {
            // Shifting previous value 8 bits to right and
            // add it with next value
            value = (value << 8) + (b & 255);
        }

        return value;
    }

//    final protected static char[] encoding = "0123456789ABCDEF".toCharArray();
//    public String convertToString(byte[] arr) {
//        char[] encodedChars = new char[arr.length * 4 * 2];
//        for (int i = 0; i < arr.length; i++) {
//            int v = arr[i];
//            int idx = i * 4 * 2;
//            for (int j = 0; j < 8; j++) {
//                encodedChars[idx + j] = encoding[(v >>> ((7-j)*4)) & 0x0F];
//            }
//        }
//        return new String(encodedChars);
//    }

//    public static class ByteArrayDeserializer extends StdDeserializer<byte[]> {
//
//        private static final long serialVersionUID = 1514703510863497028L;
//
//        public ByteArrayDeserializer() {
//            super(byte[].class);
//        }
//
//        @Override
//        public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//            JsonNode node = p.getCodec().readTree(p);
//            String base64 = node.asText();
//            return Base64.getDecoder().decode(base64);
//        }
//    }

    public void signMessage(String phantomEncryptPK, String nonce, String encryptedData){
//        System.out.println("nonce: " + nonce);
//        System.out.println("nonce decoded: " + Arrays.toString(nonce));
////        byte[] k = new byte[32];
//        long y = convertToLong(nonce);
//        System.out.println(y);
//        com.wewantgreenllc.speedsquare_dapp.TweetNacl tn = new com.wewantgreenllc.speedsquare_dapp.TweetNacl();
//        System.out.println(Arrays.toString(kp.getSecretKey()));
//        System.out.println(Arrays.toString(phantomEncryptPK));
//        com.wewantgreenllc.speedsquare_dapp.TweetNacl.Box box = new com.wewantgreenllc.speedsquare_dapp.TweetNacl.Box(phantomEncryptPK, kp.getSecretKey());
////        byte[] sharedSecret = box.before();
////        System.out.println(sharedSecret.length);
////                byte[] j = h.open(Base58.decode(encryptedData));
//        System.out.println(encryptedData);
//        System.out.println(nonce.length);
//        System.out.println(Arrays.toString(encryptedData));
//        byte[] x = box.after(encryptedData, nonce);
//        System.out.println(x.toString());
//        System.out.println(Arrays.toString(x)); //convertToString(x));
//        byte[] bk = new byte[32];
//        System.out.println(TweetNacl.crypto_box_beforenm(bk, phantomEncryptPK.getBytes(), kp.getSecretKey()));
//        System.out.println(Arrays.toString(bk));
//        byte[] m = Base58.decode(encryptedData);
//        System.out.println(TweetNacl.crypto_secretbox_open(m, new byte[32], 32, Base58.decode(nonce), bk));
        TweetNacl.Box g = new TweetNacl.Box(Base58.decode(phantomEncryptPK), kp.getSecretKey());
        byte[] x = g.open(Base58.decode(encryptedData), Base58.decode(nonce));
        System.out.println(x.toString());



//        Data data = null;
//        ObjectMapper objectMapper = new ObjectMapper();
//        try{
//            data = objectMapper.readValue(x, Data.class);
//            System.out.println(data.public_key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }






//        System.out.println(convertToString(x));
//        String userDetailsBytesToStrings = new String(j, StandardCharsets.UTF_8);
//        System.out.println(userDetailsBytesToStrings);
//        System.out.println(Arrays.toString(j));
//        JsonParser jsonParser = new JsonParser();
//        // As the output is of the pattern of JsonObject, it
//        // has to be converted in this way
//        JsonObject jsonObjectOutput
//                = (JsonObject)jsonParser.parse(
//                userDetailsBytesToStrings);



//        String geekPortalUserDetail
//                = "{\"firstName\": \"GeekA\",\"last_name\": \"GeekA\", \"email_address\": \"geeka@gfg.com\"}";
//
//        // string to byte[]
//        byte[] userDetailsBytes
//                = geekPortalUserDetail.getBytes(
//                StandardCharsets.UTF_8);
//
//        // converting the bytes to String
        String userDetailsBytesToStrings = new String(
                x, StandardCharsets.UTF_8);
        JsonParser jsonParser = new JsonParser();
////        JSONArray
//        // As the output is of the pattern of JsonObject, it
//        // has to be converted in this way
        JsonObject jsonObjectOutput
                = (JsonObject)jsonParser.parse(
                userDetailsBytesToStrings);
        System.out.println("Output : " + jsonObjectOutput);
        System.out.println(jsonObjectOutput.get("public_key"));
//
//        JsonSerializer js = new JsonSerializer() {
//            @Override
//            public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
//                return null;
//            }
//        }



//        System.out.println(JSO);

//        User user = new User();
//        user.setId(1);
//        user.setName("Mark");
//        System.out.println(TweetNacl.crypto_box_beforenm(k, kp.getSecretKey(), phantomEncryptPK.getBytes()));
//        System.out.println("ewrf");
//        System.out.println(k);
//        System.out.println("ewrf2");
//        System.out.println(TweetNacl.crypto_box_open_afternm(encryptedData.getBytes(), phantomEncryptPK.getBytes(), 0, nonce.getBytes(), k));
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        gson.toJson(user, x);
//        gson.
    }


//    public class User {
//        private int id;
//        private String name;
//
//        //getters and setters
//    }

    public class Data {
        private String public_key;
        private String session;
    }

    boolean verifySignature(String signature, String walletAddress) {
        String message = "You are verifying you wallet with sol4k";
        byte[] messageBytes = message.getBytes();
        PublicKey publicKey = new PublicKey(walletAddress);
        byte[] signatureBytes = Base58.decode(signature);
        return publicKey.verify(signatureBytes, messageBytes);
    }

}
