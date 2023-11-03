package com.wewantgreenllc.speedsquare_dapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private Button mButton;
    private Activity thiss = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mButton = findViewById(R.id.button_send);

        View.OnClickListener clickSquare = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButton.setBackgroundColor(Color.argb(255, 0, 205, 25));
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                System.out.println("mehhh");
                mButton.setBackgroundColor(Color.argb(255, 0, 20, 255));
                Button btn = new Button(thiss);
                btn.setText("Submit");
                btn.setOnClickListener(clickSquare);
                btn.setWidth(10);
                btn.setHeight(10);
                RelativeLayout linearLayout = (RelativeLayout)findViewById(R.id.main_layout);
                RelativeLayout.LayoutParams buttonlayout = new RelativeLayout.LayoutParams(300, 300); //RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(btn, buttonlayout);
            }
        });
    }


}