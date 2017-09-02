package com.mlcheckers.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.Display;
import android.widget.Toast;

/**
 * Created by Michael Li on 7/8/2017.
 */

public class CanvasActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent current=getIntent();
        int level=current.getIntExtra("level",1);

        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display=this.getWindowManager().getDefaultDisplay();

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final GameSurface gs=new GameSurface(this,display,level);
        this.setContentView(gs);

        gs.setOnTouchListener(new OnSwipeTouchListener(CanvasActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(CanvasActivity.this, "top", Toast.LENGTH_SHORT).show();

                gs.swipeU();
            }
            public void onSwipeRight() {
                //Toast.makeText(CanvasActivity.this, "right", Toast.LENGTH_SHORT).show();
                gs.swipeR();
            }
            public void onSwipeLeft() {
                //Toast.makeText(CanvasActivity.this, "left", Toast.LENGTH_SHORT).show();
                gs.swipeL();
            }
            public void onSwipeBottom() {
                //Toast.makeText(CanvasActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                gs.swipeD();
            }
            public boolean onTouch(View v, MotionEvent event) {
                super.onTouch(v, event);
                return gs.touch(event);
            }

        });


    }

    /*
    @Override
    public  void onDestroy(){
        super.onDestroy();
        startActivity(new Intent(CanvasActivity.this,MainActivity.class));
    }
    */
}