package com.wewantgreenllc.speedsquare_dapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alvinalexander.rectangledemo.CustomView2;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
import org.sol4k.Base58;
import org.sol4k.PublicKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Request;
//import okhttp3.Response;

public class MainActivity extends Activity {

//    private Button mButton;
    private Activity thiss = this;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    Random rand = new Random();
    int[] colors = new int[9];

    Button connectButton;

    Button playButton;
    Button leaderboardButton;


    int height;
    int width;

    int start = 555;
    int difference = 440;

    int colorSelected = -1;
    int score = 0;
    TextView scoreLabel;
    ImageView timerImg;
    RelativeLayout linearLayout;
    ValueAnimator widthAnimator;
    View timerBox;
    int startingTimerWidth = 270;
    int timerLength = 5400;
    boolean squares = false;
    boolean startTimer = false;
    Phantom phantom;
    MyUtils myUtils = new MyUtils(thiss);


    View.OnClickListener clickSquare = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getDrawingCacheBackgroundColor() == colorSelected) {
                view.setBackgroundColor(Color.argb(0, 0, 205, 25));
                ((ImageView) view).setImageResource(R.drawable.check);

                colorSelected = -1;
                score++;
                scoreLabel.setText(""+score);
                timerBox.getLayoutParams().width = startingTimerWidth;
                widthAnimator.cancel();
                if (timerLength > 3600) {
                    timerLength = timerLength - 200;
                } else if (timerLength > 1800) {
                    timerLength = timerLength - 100;
                } else if (timerLength > 0.45) {
//                timer = timer - 0.005
                }
                widthAnimator.setDuration(timerLength);
                widthAnimator.start();
            }
        }
    };


    View.OnClickListener clickRect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            colorSelected = view.getDrawingCacheBackgroundColor();
            if (startTimer){
                runTimer();
                startTimer = false;
            }
        }
    };


    protected void changeMenuColors(){

//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        playButton.setBackgroundColor(colors[rand.nextInt(9)]);
                        leaderboardButton.setBackgroundColor(colors[rand.nextInt(9)]);
                        connectButton.setBackgroundColor(colors[rand.nextInt(9)]);
                        changeMenuColors();
                    }
                },
                1000);
    }


    protected void runSquares(){

//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            if(squares) {
                                createSquare(start, width + 200, false);
                                createSquare(start + difference, -200, true);
                                createSquare(start + (2 * difference), width + 200, false);
                                createSquare(start + (3 * difference), -200, true);
                                runSquares();
                            }
                        }
                    },
                    486);
    }

    protected void runTimer(){
        widthAnimator.setDuration(5400);
        widthAnimator.setInterpolator(new LinearInterpolator());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                timerBox.getLayoutParams().width = (int) animation.getAnimatedValue();
                timerBox.requestLayout();
                if ((int) animation.getAnimatedValue() == 0){
                    gameEnd();
                }
            }
        });
        widthAnimator.start();
    }


    public Uri response1And2Same(Intent intent){
        byte[] secret = myUtils.readPermanent("my-secret", 44);
        phantom = new Phantom(thiss, secret);
        return intent.getData();
    }

    public void handleFirstResponse(String phantomEncryptPK, Uri data){
        String nonce = data.getQueryParameter("nonce");
        String encryptedData = data.getQueryParameter("data");
        myUtils.savePermanent("phantom-public", Base58.decode(phantomEncryptPK));
        phantom.signMessage(phantomEncryptPK, nonce, encryptedData, myUtils);
    }


    public void handleSecondResponse(Uri data){
        System.out.println("Second response data: " + data);
        String phantomEncryptPK = null;
        FileInputStream fin = null;
        byte [] phantomPublic = myUtils.readPermanent("phantom-public", 44);
        byte [] userPublicKey = myUtils.readPermanent("user-public", 44);
        phantomEncryptPK = Base58.encode(phantomPublic);
        System.out.println("Phantom public 2nd response: " + phantomEncryptPK);
        String nonce = data.getQueryParameter("nonce");
        String encryptedData = data.getQueryParameter("data");

        System.out.println("helloooooooooo6");
        String userPublicS = new String(userPublicKey, StandardCharsets.UTF_8);
        String sig = phantom.readSig(phantomEncryptPK, nonce, encryptedData);
        sendPublic(sig, userPublicS);


//        myUtils.post("https://104.236.68.131:3006/login", json, new Callback() {
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        String responseStr = response.body().string();
//                        System.out.println(responseStr);
//                        // Do what you want to do with the response.
//                    } else {
//                        System.out.println("what the fuck happened wrong?");
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    System.out.println("beep beep beep failure!");
//                }
//            });

    }


    public void sendPublic(String sig, String userPublicS){
        long epoch = System.currentTimeMillis();
        String epochString = Long.toString(epoch);
        String encryptedMsg = Encrypt.encryptLogin(sig, userPublicS, epochString);

//        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
////        ["epoch": epoch, "msg": enc]
//        params.add(new BasicNameValuePair("epoch", epochString));
//        params.add(new BasicNameValuePair("msg", Encrypt.encryptLogin(sig, new String(userPublicKey, StandardCharsets.UTF_8), epochString)));
//        System.out.println("helloooooooooo5");
//        myUtils.sendRequest("104.236.68.131:3006/login", params);
        String json = "{\"epoch\": \"" + epochString + "\", \"msg\": \"" + encryptedMsg + "\"}";
        System.out.println(json);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    myUtils.httpPost("http://104.236.68.131:3006/login", json);
                String response = myUtils.httpsPost("https://104.236.68.131:3004/login", json);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObjectOutput
                        = (JsonObject)jsonParser.parse(
                        response);
                String msg = jsonObjectOutput.get("encRes").getAsString();
                System.out.println(msg);

                System.out.println(userPublicS);
                String login_token = Encrypt.decryptLogin(msg, userPublicS, epochString);
                System.out.println(login_token);
                myUtils.savePermanent("login-token", login_token.getBytes());
                myUtils.savePermanent("signature", sig.getBytes());

//            usernameOrPubKey: (signature != nil ? pub_key : username)!, epoch: epoch, msgEnc: res["encRes"] as! String)
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });
        t1.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        System.out.println("helloooooooooo9");
        Intent intent = getIntent();
        if(intent.getAction() == "android.intent.action.VIEW") {
            System.out.println("helloooooooooo8");
            // BOTH INCOMING PHANTOM RESPONSES
            Uri data = response1And2Same(intent);
            String phantomEncryptPK = data.getQueryParameter("phantom_encryption_public_key");
            if (phantomEncryptPK != null) {
                // FIRST RESPONSE ONLY
                handleFirstResponse(phantomEncryptPK, data);
            }else{
                // SECOND RESPONSE ONLY
                handleSecondResponse(data);
            }
        }else{
            // ORIGINAL OUTGOING PHANTOM REQUEST
                phantom = new Phantom(thiss);
                myUtils.savePermanent("my-secret", phantom.kp.getSecretKey());
        }

        colors[0] = Color.RED;
        colors[1] = Color.argb(255, 255, 140, 0); //orange
        colors[2] = Color.YELLOW;
        colors[3] = Color.GREEN;
        colors[4] = Color.BLUE;
        colors[5] = Color.argb(255, 255, 0, 255);;
        colors[6] = Color.BLACK;
        colors[7] = Color.GRAY;
        colors[8] = Color.argb(255, 160, 82, 45);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        linearLayout = (RelativeLayout)findViewById(R.id.main_layout);



        showMenu();


//createSquare(80, -200, true, linearLayout);
//System.out.println("iiiiiiiii"+width);
//for (int h = 0; h < 4; h++) {
//    for (int i = 0; i < 8; i++) {
////        createSquare(h*300, (i%2==0 ? -1 : 1) * (width + (i%2*220)), i%2==0, linearLayout);
//    }
//}



//        Handler handler = new Handler();
//        private Runnable updateData = new Runnable(){
//            public void run(){
//                //call the service here
//                ////// set the interval time here
//                createSquare(300, 0, true, linearLayout);
//
//                handler.postDelayed(updateData,10000);
//            }
//        }


//        new Timer().scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run(){
//                createSquare(300, 0, true, linearLayout);
//            }
//        },0,1000);
//        mButton = findViewById(R.id.button_send);
//
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                System.out.println("mehhh");
//                mButton.setBackgroundColor(Color.argb(255, 0, 20, 255));
//            }
//        });
    }


    protected void gameEnd(){
        new AlertDialog.Builder(thiss)
                .setTitle("Game End")
                .setPositiveButton("OK", null)
                .setMessage("You got " + score + "!")
                .show();
        long epoch = System.currentTimeMillis();
        String publicKey = new String(myUtils.readPermanent("user-public", 44), StandardCharsets.UTF_8);
        String loginToken = new String(myUtils.readPermanent("login-token", 48), StandardCharsets.UTF_8);
//        System.out.println("SENDING SCORES");
//        System.out.println(publicKey);
//        System.out.println(loginToken);
        String msg = Encrypt.encryptGeneral(loginToken, "{\"epoch\": \"" + epoch + "\", \"score\": " + score + ", \"pub_key\": \"" + publicKey + "\"}");
//    System.out.println(msg);
        String json = "{\"msg\": \"" + msg + "\", \"pub_key\": \"" + publicKey + "\"}";
//        System.out.println(json);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
              myUtils.httpsPost("https://104.236.68.131:3004/score", json);
            }
        });
        t1.start();
        showMenu();
    }


    View.OnClickListener playButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showGame();
        }
    };

    View.OnClickListener leaderboardClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openWebpage("http://leaderboards.popgames.net");
        }
    };

    View.OnClickListener connectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            phantom.connect();
        }
    };

    protected void openWebpage(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }



    protected void showMenu(){
        squares = false;
        linearLayout.removeAllViews();

        playButton = new Button(thiss);
        playButton.setText("Play");
        playButton.setTextSize(72);
        playButton.setOnClickListener(playButtonClick);
        playButton.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams playerButtonLayoutParams = new RelativeLayout.LayoutParams((2*width)/3, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        playerButtonLayoutParams.topMargin = 500;
        playerButtonLayoutParams.leftMargin = width/6;
        linearLayout.addView(playButton, playerButtonLayoutParams);

        leaderboardButton = new Button(thiss);
        leaderboardButton.setText("Leaders");
        leaderboardButton.setTextSize(63);
        leaderboardButton.setOnClickListener(leaderboardClick);
        leaderboardButton.setBackgroundColor(Color.BLUE);
        RelativeLayout.LayoutParams leaderboardButtonLayoutParams = new RelativeLayout.LayoutParams(3*width/4, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leaderboardButtonLayoutParams.topMargin = 1000;
        leaderboardButtonLayoutParams.leftMargin = width/8;
        linearLayout.addView(leaderboardButton, leaderboardButtonLayoutParams);

        connectButton = new Button(thiss);
        connectButton.setText("Connect");
        connectButton.setTextSize(63);
        connectButton.setOnClickListener(connectClick);
        connectButton.setBackgroundColor(Color.GRAY);
        RelativeLayout.LayoutParams connectButtonLayoutParams = new RelativeLayout.LayoutParams(3*width/4, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        connectButtonLayoutParams.topMargin = 1500;
        connectButtonLayoutParams.leftMargin = width/8;
        linearLayout.addView(connectButton, connectButtonLayoutParams);

        changeMenuColors();
    }


    protected void showGame(){
        linearLayout.removeAllViews();
        score = 0;
        timerLength = 5400;
        scoreLabel = new TextView(thiss);
        scoreLabel.setX(width/2 + 270);
        scoreLabel.setY(36);
        scoreLabel.setTextSize(72);
        scoreLabel.setText("0");
        scoreLabel.setTextColor(Color.BLACK);
        linearLayout.addView(scoreLabel);

        timerImg = new ImageView(thiss);
        RelativeLayout.LayoutParams timerLayoutParams = new RelativeLayout.LayoutParams(180, 180);
        timerImg.setImageResource(R.drawable.timer);
        timerImg.setX(width/2 - 450);
        timerImg.setY(72);
        linearLayout.addView(timerImg, timerLayoutParams);



        timerBox = new View(thiss);

        timerBox.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams timerBoxLayoutParams = new RelativeLayout.LayoutParams(startingTimerWidth, 100); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        timerBoxLayoutParams.topMargin = 120;
        timerBoxLayoutParams.leftMargin = width/2 - 250;
        linearLayout.addView(timerBox, timerBoxLayoutParams);

        widthAnimator = ValueAnimator.ofInt(timerBoxLayoutParams.width, 0);
        startTimer = true;

        int rectStart = difference/2+5;
        createRectangle(start-rectStart);
        createRectangle(start-rectStart+(1*difference));
        createRectangle(start-rectStart+(2*difference));
        createRectangle(start-rectStart+(3*difference));

        squares = true;
        runSquares();
    }


    protected void createRectangle(int top) {
        View btn = new View(thiss);
//        btn.setText("Submit");
        btn.setOnClickListener(clickRect);
        int color = colors[rand.nextInt(9)];
        btn.setBackgroundColor(color);
        btn.setDrawingCacheBackgroundColor(color);
//        btn.setWidth(30);
//        btn.setHeight(180);

        RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(width, 210); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.topMargin = top;
        buttonLayoutParams.leftMargin = 0;
        linearLayout.addView(btn, buttonLayoutParams);
    }

    protected void createSquare(int top, int left, boolean movingRight){
        ImageView btn = new ImageView(thiss);
//        btn.setText("Submit");
        btn.setOnClickListener(clickSquare);
        int color = colors[rand.nextInt(9)];
        btn.setBackgroundColor(color);
        btn.setDrawingCacheBackgroundColor(color);
//        btn.setWidth(30);
//        btn.setHeight(180);

        RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(200, 200); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.topMargin = top;
        buttonLayoutParams.leftMargin = left;
        linearLayout.addView(btn, buttonLayoutParams);

        ObjectAnimator animation = ObjectAnimator.ofFloat(btn, "translationX", (movingRight ? width+400 : -width-400));
        animation.setDuration(3400);
        animation.setInterpolator(new LinearInterpolator());
        animation.addListener(new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                linearLayout.removeView(btn);
                animation.removeAllListeners();
                animation.cancel();
                btn.invalidate();
//                System.out.println("count: "+linearLayout.getChildCount());
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        animation.start();

    }


}