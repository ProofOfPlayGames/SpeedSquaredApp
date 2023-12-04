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

import org.sol4k.PublicKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

//    private Button mButton;
    private Activity thiss = this;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    Random rand = new Random();
    int[] colors = new int[9];


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
    int timerLength = 5000;
    boolean squares = false;
    boolean startTimer = false;
    Phantom phantom;


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
                if(timerLength > 1000){
                    timerLength = timerLength - 500;
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
        widthAnimator.setDuration(5000);
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



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout); //CustomView2.generateViewId());

        Intent intent = getIntent();
        System.out.println(intent);
        System.out.println(intent.getAction());
        if(intent.getAction() == "android.intent.action.VIEW") {
            try {
                FileInputStream fin = openFileInput("phantom-secret");
//                String secret = "";
//                int i;
//                while ((i = fin.read()) != -1) {
//                    System.out.println(i);
//                    secret = secret + Character.toString((char) i);
//                }
                byte[] secret = new byte[32];
                fin.read(secret);

                System.out.println("hehehehehehhe");
                System.out.println(secret);
                phantom = new Phantom(thiss, secret);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String action = intent.getAction();
            Uri data = intent.getData();
            System.out.println(data);
            System.out.println(data.getQueryParameterNames());
            String phantomEncryptPK = data.getQueryParameter("phantom_encryption_public_key");
            System.out.println(phantomEncryptPK);
            System.out.println(Base58.decode(phantomEncryptPK).length);
            String nonce = data.getQueryParameter("nonce");
            String encryptedData = data.getQueryParameter("data");
            System.out.println(nonce);
            System.out.println(encryptedData);
            System.out.println(phantomEncryptPK);
            System.out.println(new PublicKey(phantom.kp.getSecretKey()).toString());
            phantom.signMessage(phantomEncryptPK, nonce, encryptedData);
        }else{
//            if(!f.exists()) {
                phantom = new Phantom(thiss);
                try {
                    System.out.println("iiiiiiiiiiiiiiiiiiii");
                    System.out.println(phantom.kp.getSecretKey().toString());
//                    File appFilesDirectory = thiss.getFilesDir();
//                    File q = new File(appFilesDirectory, "phantom-secret");
//
                    FileOutputStream outputStream;
                    outputStream = openFileOutput("phantom-secret", Context.MODE_PRIVATE);
                    outputStream.write(phantom.kp.getSecretKey());
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            }
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
                .setTitle("messege")
                .setPositiveButton("ok", null)
                .setMessage( "user name : steven" + "/n" +
                        "password : stevennn" + "/n"  )
                .show();
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

        Button playButton = new Button(thiss);
        playButton.setText("Play");
        playButton.setTextSize(72);
        playButton.setOnClickListener(playButtonClick);
        playButton.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams playerButtonLayoutParams = new RelativeLayout.LayoutParams((2*width)/3, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        playerButtonLayoutParams.topMargin = 500;
        playerButtonLayoutParams.leftMargin = width/6;
        linearLayout.addView(playButton, playerButtonLayoutParams);

        Button leaderboardButton = new Button(thiss);
        leaderboardButton.setText("Leaders");
        leaderboardButton.setTextSize(63);
        leaderboardButton.setOnClickListener(leaderboardClick);
        leaderboardButton.setBackgroundColor(Color.BLUE);
        RelativeLayout.LayoutParams leaderboardButtonLayoutParams = new RelativeLayout.LayoutParams(3*width/4, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leaderboardButtonLayoutParams.topMargin = 1000;
        leaderboardButtonLayoutParams.leftMargin = width/8;
        linearLayout.addView(leaderboardButton, leaderboardButtonLayoutParams);

        Button connectButton = new Button(thiss);
        connectButton.setText("Connect");
        connectButton.setTextSize(63);
        connectButton.setOnClickListener(connectClick);
        connectButton.setBackgroundColor(Color.GRAY);
        RelativeLayout.LayoutParams connectButtonLayoutParams = new RelativeLayout.LayoutParams(3*width/4, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        connectButtonLayoutParams.topMargin = 1500;
        connectButtonLayoutParams.leftMargin = width/8;
        linearLayout.addView(connectButton, connectButtonLayoutParams);
    }


    protected void showGame(){
        linearLayout.removeAllViews();
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