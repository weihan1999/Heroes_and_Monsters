package com.mlcheckers.android;

/**
 * Created by Michael Li on 7/8/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Display display;
    private GameThread gameThread;
    private ChibiCharacter chibi1;
    private MainMap mainmap;
    private Context context;
    private int level;
    public Bitmap MASTER;
    //private Sprite test;
    //private Bitmap test1;

    public GameSurface(Context context, Display display,int level)  {
        super(context);
        this.level=level;
        this.display=display;
        this.context=context;
        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);

    }

    public void update()  {
        //this.chibi1.update();
    }



    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        this.mainmap.draw(canvas);
        //this.test.draw(canvas);
        //this.chibi1.draw(canvas);
        if(this.mainmap.allAlliesDead||this.mainmap.allFoesDead){
            context.startActivity(new Intent(context,MainActivity.class));
            //((Activity)context).finish();
            //this.mainmap.allAlliesDead=false;
            //this.mainmap.allFoesDead=false;
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       // Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        //this.chibi1 = new ChibiCharacter(this,chibiBitmap1,100,50);
        Levels levels=new Levels(this);
        Level lv=levels.getLevel(level);


        this.mainmap = new MainMap(display, lv.getSprites(), lv.getBackground(),lv.getTerrain(),lv.getWhole());

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MASTER.recycle();
        Log.d("state","***destroyed");
        this.gameThread.setRunning(false);
        /*
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
        */
    }

    /*
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x=  (int)event.getX();
            int y = (int)event.getY();

            int movingVectorX =x-  this.chibi1.getX() ;
            int movingVectorY =y-  this.chibi1.getY() ;

            this.chibi1.setMovingVector(movingVectorX,movingVectorY);
            return true;
        }
        return false;
    }
    */

    public void generateMap(){

    }

    public boolean touch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x =  (int)event.getX();
            int y = (int)event.getY();
            mainmap.actionDown(x,y);

            /*int movingVectorX =x-  this.chibi1.getX() ;
            int movingVectorY =y-  this.chibi1.getY() ;

            this.chibi1.setMovingVector(movingVectorX,movingVectorY);*/
            return true;
        }
        return false;
    }

    public void swipeU(){
        mainmap.swipeUp();
    }

    public void swipeD(){
        mainmap.swipeDown();
    }

    public void swipeL(){
        mainmap.swipeLeft();
    }

    public void swipeR(){
        mainmap.swipeRight();
    }

}