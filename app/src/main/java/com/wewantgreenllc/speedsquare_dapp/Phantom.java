package com.wewantgreenllc.speedsquare_dapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.sol4k.Base58;
import org.sol4k.PublicKey;
import org.sol4k.tweetnacl.TweetNacl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Phantom {

    TweetNacl.Box.KeyPair kp;
    Activity activity;

    public Phantom(Activity activity){
        this.activity = activity;
        this.kp = TweetNacl.Box.keyPair();
        System.out.println("CREATE NEW KP: " + new PublicKey(kp.getSecretKey()));
    }

    public Phantom(Activity activity, byte[] secretKey){
        this.activity = activity;
        this.kp = TweetNacl.Box.keyPair_fromSecretKey(secretKey);
        System.out.println("CREATE EXISTING KP: " + new PublicKey(kp.getSecretKey()));
    }

    public void connect(){
        Uri uri = Uri.parse("https://phantom.app/ul/v1/connect");
        Uri.Builder newUri = uri.buildUpon();
        newUri.appendQueryParameter("app_url", "https://apps.apple.com/us/app/facebook/id284882215");
        PublicKey pk = new PublicKey(kp.getPublicKey());
        System.out.println("Sending dapp_encryption_public_key: " + pk.toString());
        newUri.appendQueryParameter("dapp_encryption_public_key", pk.toString());
        newUri.appendQueryParameter("redirect_link", "app://speed-square");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, newUri.build());
        this.activity.startActivity(mapIntent);
    }


    public String signMessage(String phantomEncryptPK, String nonce, String encryptedData, MyUtils myUtils){
        PublicKey pk = new PublicKey(kp.getPublicKey());
        System.out.println("First return pahntom public: " + phantomEncryptPK);
        System.out.println("First return dapp public: " + pk.toString());
        System.out.println("First return dapp secret: " + new PublicKey(kp.getSecretKey()));
        TweetNacl.Box g = new TweetNacl.Box(Base58.decode(phantomEncryptPK), kp.getSecretKey());
        System.out.println(g);
        JsonObject jsonObjectOutput = decryptPayload(nonce, encryptedData, g);
        System.out.println(jsonObjectOutput);

        // CONSTRUCT AND SEND SIGN MESSAGE TO PHANTOM
        byte[] newNonce = new byte[24];
        TweetNacl.randombytes(newNonce, 24);
        Uri uri = Uri.parse("https://phantom.app/ul/v1/signMessage");
        Uri.Builder newUri = uri.buildUpon();
        newUri.appendQueryParameter("dapp_encryption_public_key", pk.toString());
        newUri.appendQueryParameter("nonce", Base58.encode(newNonce));
        newUri.appendQueryParameter("redirect_link", "app://speed-square");

        System.out.println("Decrypted Response 1: " + jsonObjectOutput);

        String s = jsonObjectOutput.get("session").getAsString();

        String userPublicKey = jsonObjectOutput.get("public_key").getAsString();
        myUtils.savePermanent("user-public", userPublicKey.getBytes());

        System.out.println("Session: " + s);
        String m = "{\"message\": \"" + Base58.encode("Proof of Play Games Login Message".getBytes(StandardCharsets.UTF_8)) + "\", \"session\": \"" + s + "\"}";
        System.out.println("Json response to first return: " + m);
        String q = encryptPayload(m, Base58.encode(newNonce), g.before());
        System.out.println("Encrypted payload for sign message: " + q);
        newUri.appendQueryParameter("payload", q);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, newUri.build());
        this.activity.startActivity(mapIntent);
        return jsonObjectOutput.get("public_key").getAsString();
    }


    public String readSig(String phantomEncryptPK, String nonce, String encryptedData){
        TweetNacl.Box g = new TweetNacl.Box(Base58.decode(phantomEncryptPK), kp.getSecretKey());
        JsonObject jsonObjectOutput = decryptPayload(nonce, encryptedData, g);
        System.out.println("Response decrypted message 2: " + jsonObjectOutput);
        String sig = jsonObjectOutput.get("signature").toString();
        System.out.println("signature: " + sig);
        return sig;
    }


    public JsonObject decryptPayload(String nonce, String encryptedData, TweetNacl.Box g){
        byte[] x = g.open(Base58.decode(encryptedData), Base58.decode(nonce));
        String userDetailsBytesToStrings = new String(
                x, StandardCharsets.UTF_8);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObjectOutput
                = (JsonObject)jsonParser.parse(
                userDetailsBytesToStrings);
        return jsonObjectOutput;
    }


    public String encryptPayload(String data, String nonce, byte[] sharedKey){
        TweetNacl.SecretBox g = new TweetNacl.SecretBox(sharedKey);
        byte[] x = g.box(data.getBytes(), Base58.decode(nonce));
        return Base58.encode(x);
    }




//    boolean verifySignature(String signature, String walletAddress) {
//        String message = "You are verifying you wallet with sol4k";
//        byte[] messageBytes = message.getBytes();
//        PublicKey publicKey = new PublicKey(walletAddress);
//        byte[] signatureBytes = Base58.decode(signature);
//        return publicKey.verify(signatureBytes, messageBytes);
//    }

}
