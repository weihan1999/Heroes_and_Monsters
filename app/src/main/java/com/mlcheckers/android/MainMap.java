package com.mlcheckers.android;

/**
 * Created by Michael Li on 7/8/2017.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainMap {
    final int ROW=20;
    final int COL=20;
    final int sROW=9;
    final int sCOL=18;
    private int touchR;
    private int touchC;
    private int gridW;
    private int gridH;
    private int vertical;
    private int horizontal;
    private List<Sprite> sprites;
    private List<MapPixel> terrain;
    private List<MapPixel> background;
    private MapPixel[][] effects=new MapPixel[ROW][COL];
    private MapPixel[][] whole;
    private int[] moveR;
    private int[] moveC;
    private int[][] hitArr;
    private int [][] skillArr;
    private int pos=0;
    private int apos=0;
    private int spos=0;
    private Sprite currentS;
    private Sprite target;
    private Canvas currentCanvas;
    private int[] walkBox=new int[2];
    private int [] atkBox=new int[2];
    private int[][] skillBar=new int[2][4];
    private int [] cancel=new int[2];
    private int[] wait=new int[2];
    private int[] save=new int[2];
    private boolean displayBot=false;
    private boolean walked=false;
    private int skillID;
    private FoeAi foeai;
    private boolean myTurn=true;
    private DatabaseReference db;
    private DatabaseReference saveDb;
    private List<SaveEntry> saveEntries;
    private FirebaseUser user;
    private Bitmap test;
    public boolean allFoesDead;
    public boolean allAlliesDead;
    private Sprite selected;

    public MainMap(Display display,List<Sprite> sprites, List<MapPixel> background, List<MapPixel> terrain,MapPixel[][] whole){
        this.vertical=0;
        this.horizontal=0;
        this.sprites=sprites;
        this.terrain=terrain;
        this.background=background;
        this.whole=whole;
        Point size= new Point();
        display.getSize(size);

        double width = size.x;
        double height = size.y;

        this.gridW=(int)width/sCOL;
        this.gridH=(int)height/sROW;
        test=background.get(0).getBitmap();
        foeai=new FoeAi(sprites,terrain);

        user= FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseDatabase.getInstance().getReference();
        saveDb=db.child("saveentries");
        saveEntries=new ArrayList<>();
        saveDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saveEntries.clear();
                /*for(DataSnapshot s:dataSnapshot.getChildren()){
                    SaveEntry sEntry=s.getValue(SaveEntry.class);
                    saveEntries.add(sEntry);
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*for(int r=0;r<ROW;r++){
            for(int c=0;c<COL;c++){
                effects[r][c]=new MapPixel(whole[0][0].getBitmap());
                effects[r][c].setLocation(r,c);
            }
        }*/
    }

    private void saveGame(){
        String key=saveDb.push().getKey();
        SaveEntry sEntry=new SaveEntry(user.getUid(),key,1);
        saveDb.child(key).setValue(sEntry);
    }

    public void draw(Canvas canvas){
        currentCanvas=canvas;
        Paint painter=new Paint();
        painter.setColor(Color.WHITE);
        painter.setTextSize(16);
        //draw background
        for(int i=0;i<sROW-1;i++){
            for (int j=0;j<sCOL;j++){
                Rect dest = new Rect(gridW * j, gridH * i, gridW * (j + 1), gridH * (i + 1));
                canvas.drawBitmap(test,null,dest,painter);
            }
        }

        //draw terrain
        for (MapPixel mp:terrain) {
            if(mp.getRow()!=sROW-1) {
                Rect dest = new Rect(gridW * (mp.getCol() - horizontal), gridH * (mp.getRow() - vertical),
                        gridW * (mp.getCol() + 1 - horizontal), gridH * (mp.getRow() + 1 - vertical));
                canvas.drawBitmap(mp.getBitmap(), null, dest, painter);
            }
        }
        //draw sprites
        for(Sprite s:sprites){
            int x = (s.getCol()-horizontal)*gridW;
            int y = (s.getRow()-vertical)*gridH;
            if(s.getRow()-vertical!=sROW-1&&s.getAlive()) {
                Rect dest = new Rect(gridW * (s.getCol()-horizontal), gridH * (s.getRow()-vertical),
                        gridW * (s.getCol() + 1-horizontal), gridH * (s.getRow() + 1-vertical));
                canvas.drawBitmap(s.getBitmap(), null, dest, painter);
            }
        }
        //draw effects
        /*for(int r=0;r<ROW;r++){
            for(int c=0;c<ROW;c++){
                MapPixel mp=effects[r][c];
                if(mp.getRow()-vertical!=sROW-1) {
                    Rect dest = new Rect(gridW * (mp.getCol() - horizontal), gridH * (mp.getRow() - vertical),
                            gridW * (mp.getCol() + 1 - horizontal), gridH * (mp.getRow() + 1 - vertical));
                    canvas.drawBitmap(mp.getBitmap(), null, dest, painter);
                }
            }
        }*/

        canvas.drawText("Menu",16,(sROW-1)*gridH+16,painter);


        if(myTurn){
            painter.setStyle(Paint.Style.STROKE);
            canvas.drawText("Save",gridW*(sCOL-3)+16,(sROW-1)*gridH+16,painter);
            save[0]=sCOL-3+horizontal;
            save[1]=sROW-1+vertical;
            for(Sprite s:sprites){
                if (s.getSide().equals("ally")&&s.getAlive()){
                    currentS=s;
                    if(true){
                        currentS=s;
                        //draw bottom bar
                        if(!s.getState().equals("ready")&&displayBot){
                            if (s.getRow()-vertical<sROW-1) {
                                canvas.drawRect((selected.getCol() - horizontal) * gridW, (selected.getRow() - vertical) * gridH,
                                        (selected.getCol() - horizontal) * gridW + gridW,
                                        (selected.getRow() - vertical) * gridH + gridH, painter);
                            }
                            canvas.drawText("HP: "+selected.getHp(),gridW+16,(sROW-1)*gridH+16,painter);
                            canvas.drawText("MP: "+selected.getMp(),gridW+16,(sROW-1)*gridH+16*5/2,painter);
                            walkBox[0]=2+horizontal;
                            walkBox[1]=sROW-1+vertical;
                            canvas.drawText("Walk", gridW*2+16, (sROW-1)*gridH+16,painter);
                            atkBox[0]=3+horizontal;
                            atkBox[1]=sROW-1+vertical;
                            canvas.drawText("Attack",gridW*3+16,(sROW-1)*gridH+16,painter);
                            skillBar[0][0]=4+horizontal;
                            skillBar[1][0]=sROW-1+vertical;
                            canvas.drawText(""+selected.getSkill(0).getName(),gridW*4+16, (sROW-1)*gridH+16,painter);
                            wait[0]=10+horizontal;
                            wait[1]=sROW-1+vertical;
                            canvas.drawText("Wait",gridW*10+16,(sROW-1)*gridH+16,painter);
                            cancel[0]=11+horizontal;
                            cancel[1]=sROW-1+vertical;
                            canvas.drawText("Cancel",gridW*11+16,(sROW-1)*gridH+16,painter);
                        }

                        //draw walk boxes
                        if (s.getState().equals("walk")){
                            pos=0;
                            String type=null;
                            for(MapPixel t:terrain){
                                if(t.getRow()==s.getRow()&&t.getCol()==s.getCol()){
                                    type=t.getType();
                                    break;
                                }
                            }
                            int walkRange=s.getWalk(type);
                            moveR=new int[walkRange*walkRange*2+2*walkRange];
                            moveC=new int[walkRange*walkRange*2+2*walkRange];
                            for(int i=1;i<=walkRange;i++) {
                                drawMove(i, 0, painter);
                                drawMove(-i, 0, painter);
                                if(s.getRow()-vertical+i<sROW-1) drawMove(0, i, painter);
                                drawMove(0, -i, painter);
                                for (int j = 1; j <= walkRange - i; j++) {
                                    if(s.getRow()-vertical+j<sROW-1){
                                        drawMove(-i, j, painter);
                                        drawMove(i, j, painter);
                                    }

                                    drawMove(i, -j, painter);
                                    drawMove(-i, -j, painter);

                                }
                            }
                        }
                        //draw cast boxes for skills
                        else if(s.getState().equals("cast")){
                            spos=0;
                            int range=s.getSkill(0).getRange();
                            skillArr=new int[2][range*range*2+2*range];
                            painter.setColor(Color.parseColor("green"));
                            for(int i=1;i<=range;i++) {
                                drawSkill(i, 0, painter);
                                drawSkill(-i, 0, painter);
                                if(s.getRow()-vertical+i<sROW-1) drawSkill(0, i, painter);
                                drawSkill(0, -i, painter);
                                for (int j = 1; j <= range - i; j++) {
                                    if(s.getRow()-vertical+j<sROW-1){
                                        drawSkill(-i, j, painter);
                                        drawSkill(i, j, painter);
                                    }

                                    drawSkill(i, -j, painter);
                                    drawSkill(-i, -j, painter);
                                }
                            }
                            painter.setColor(Color.parseColor("white"));
                        }
                        //draw hit boxes
                        else if(s.getState().equals("hit")){
                            apos=0;
                            int hitRange=s.getRange();
                            hitArr=new int[2][hitRange*hitRange*2+2*hitRange];
                            painter.setStyle(Paint.Style.STROKE);
                            painter.setColor(Color.CYAN);
                            for (int j=1;j<=currentS.getRange();j++){
                                drawAttack(j,0,painter);
                                drawAttack(-j,0,painter);
                                if(s.getRow()-vertical+j<sROW-1) drawAttack(0,j,painter);
                                drawAttack(0,-j,painter);

                                for(int k=1;k<=s.getRange()-j;k++){
                                    if(s.getRow()-vertical+k<sROW-1){
                                        drawAttack(j,k,painter);
                                        drawAttack(-j,k,painter);
                                    }
                                    drawAttack(j,-k,painter);
                                    drawAttack(-j,-k,painter);
                                }
                            }
                            painter.setColor(Color.parseColor("white"));
                        }
                    }

                }
                else if(s.getSide().equals("foe")&&touchR==s.getRow()&&touchC==s.getCol()&&currentS.getState().equals("ready")
                        &&s.getAlive()){
                    if(touchR!=sROW-1) {
                        canvas.drawRect((s.getCol() - horizontal) * gridW, (s.getRow() - vertical) * gridH,
                                (s.getCol() - horizontal) * gridW + gridW, (s.getRow() - vertical) * gridH + gridH, painter);
                        canvas.drawText("HP: " + s.getHp(), gridW + 16, (sROW - 1) * gridH + 16, painter);
                        canvas.drawText("MP: " + s.getMp(), gridW + 16, (sROW - 1) * gridH + 16 * 5 / 2, painter);
                        displayBot=false;
                    }
                }
                myTurn=false;
                for(Sprite sp:sprites){
                    if(sp.getAlive()&&sp.getSide().equals("ally")&&!sp.getState().equals("rest")){
                        myTurn=true;
                        for(Sprite sprite:sprites){
                            sprite.hadWalked=false;
                        }
                        break;
                    }
                    else if(sp.getSide().equals("ally")){
                    }
                }

            }
        }
        else{
            for(Sprite s:sprites){
                if(s.getSide().equals("foe")&&s.getAlive())
                    foeai.decide(s, vertical, horizontal,s.getJob());
            }
            myTurn=true;
            for(Sprite s:sprites){
                if(s.getSide().equals("ally")) s.setState("ready");
            }
        }
        if(myTurn){
            canvas.drawText("Your turn",gridW*6+16,(sROW-1)*gridH+16,painter);
        }
        else canvas.drawText("Enemy's turn",gridW*6+16,(sROW-1)*gridH+16,painter);

        for(Sprite sprite:sprites){
            if(!sprite.getAlive()){
                for( Sprite item:sprite.getItems()){
                    item.setAlive(false);
                }
            }
        }


        allFoesDead=true;
        allAlliesDead=true;
        for(Sprite s:sprites){
            if(s.getAlive()) Log.d("state","***");
            else if(!s.getAlive()) Log.d("state","****");
            if(s.getSide().equals("foe")&&s.getAlive()){
                allFoesDead=false;
            }
            if(s.getSide().equals("ally")&&s.getAlive()){
                allAlliesDead=false;
            }
        }
        if(allFoesDead){
            canvas.drawText("Victory",16,16,painter);
        }
        if(allAlliesDead){
            canvas.drawText("Game Over",16,16,painter);
        }
    }

    public boolean confirmAction(double dmg, Sprite tgt, int targetR, int targetC, Paint painter){
        currentCanvas.drawRect((targetC-horizontal)*gridW,(targetR-vertical)*gridH,
                (targetC-horizontal)*gridW+gridW,(targetR-vertical)*gridH+gridH,painter);
        double after;
        if (tgt.getHp()-dmg>0) after=tgt.getHp()-dmg;
        else after=0;
        currentCanvas.drawText("HP: "+tgt.getHp()+" -> "+ after,gridW+16,(sROW-1)*gridH+16,painter);

        if (touchC==targetC&&touchR==targetR) return true;
        else return false;
    }

    public void actionDown(int x, int y){
        touchR = vertical + y / gridH;
        touchC = horizontal + x / gridW;
        for(Sprite s:sprites){
            if (!s.getSide().equals("ally")) continue;
            //selecting sprite
            if(!s.getState().equals("equipment")&&s.getAlive()) {
                if (s.getState().equals("ready") && touchR == s.getRow() && touchC == s.getCol()) {
                    displayBot=true;
                    s.setState("selected");
                    selected=s;
                }

                //Select actions
                else if (s.getState().equals("selected")) {
                    if (touchR == walkBox[1] && touchC == walkBox[0] && !s.hadWalked) {
                        s.setState("walk");
                    } else if (touchR == atkBox[1] && touchC == atkBox[0]) {
                        s.setState("hit");
                    } else if (touchR == skillBar[1][0] && touchC == skillBar[0][0]) {
                        s.setState("cast");
                    } else if (touchC == wait[0] && touchR == wait[1]) {
                        s.setState("rest");
                        displayBot=false;
                        resetTouch();
                    }
                    else if (touchR != s.getRow() || touchC != s.getCol()) s.setState("ready");
                    /*for(Sprite sprite:sprites){
                        if(sprite==selected) continue;
                        else if(touchR==sprite.getRow()&&touchC==sprite.getCol())displayBot=false;

                    }*/
                }

                //casting spell on target
                else if (s.getState().equals("cast") && skillArr != null) {
                    for (int i = 0; i < skillArr[0].length; i++) {
                        if (touchR == skillArr[1][i] && touchC == skillArr[0][i]) {
                            for (Sprite s2 : sprites) {
                                if (s2.getSide().equals("foe") && touchR == s2.getRow() && touchC == s2.getCol()) {
                                    target = s2;
                                    break;
                                }
                            }
                            if (target != null) {
                                target.takeDmg(currentS.getSkill(0).getDmg());
                                //if(target.getHp()<=0) sprites.remove(target);
                                displayBot = false;
                                s.setState("rest");
                                displayBot=false;
                                resetTouch();
                            }
                            //s.setState("ready");
                            break;
                        }

                    }
                }

                //Moving sprite
                else if (s.getState().equals("walk") && moveR != null && moveC != null) {
                    boolean occupied=false;
                    for (int i = 0; i < moveR.length; i++) {
                        if (touchR == moveR[i] && touchC == moveC[i]) {
                            for(Sprite sp:sprites){
                                if(sp.sameLocation(touchR,touchC)&&sp.getAlive()) occupied=true;
                            }
                            int currentR = s.getRow();
                            int currentC = s.getCol();
                            if(!occupied) {
                                for (Sprite sprite : sprites) {
                                    if (sprite.sameLocation(currentR, currentC)) {
                                        sprite.setLocation(moveR[i], moveC[i]);
                                    }
                                }
                                s.setState("selected");
                                s.hadWalked = true;
                                break;
                            }
                            else{
                                s.setState("walk");
                                break;
                            }
                        }

                    }
                }

                //Hitting target
                else if (s.getState().equals("hit") && hitArr != null) {
                    for (int i = 0; i < hitArr[0].length; i++) {
                        if (touchR == hitArr[1][i] && touchC == hitArr[0][i]) {
                            for (Sprite s2 : sprites) {
                                if (s2.getSide().equals("foe") && touchR == s2.getRow() && touchC == s2.getCol()) {
                                    target = s2;
                                    break;
                                }
                            }
                            if (target != null) {
                                target.takeDmg(currentS.getDmg());
                                //if(target.getHp()<=0) sprites.remove(target);
                                displayBot = false;
                                s.setState("rest");
                                displayBot=false;
                                resetTouch();
                            }
                            //s.setState("ready");
                            break;
                        }
                    }
                }
                //Re-selecting action
                if (s.getState().equals("walk") || s.getState().equals("hit") || s.getState().equals("cast")) {
                    if (touchC == cancel[0] && touchR == cancel[1]) {
                        s.setState("selected");
                    }
                }
            }
        }

        if (touchC == save[0] && touchR == save[1]) {
            saveGame();
            resetTouch();
        }
    }

    public void drawSkill(int dx, int dy, Paint p){
        int startx=(currentS.getCol()-horizontal+dx)*gridW;
        int starty=(currentS.getRow()-vertical+dy)*gridH;
        int endx=startx+gridW;
        int endy=starty+gridH;
        skillArr[0][spos]=startx/gridW+horizontal;
        skillArr[1][spos]=starty/gridH+vertical;
        spos++;
        currentCanvas.drawRect(startx, starty, endx, endy, p);
    }

    public void drawAttack(int dx, int dy, Paint p){
        int startx=(currentS.getCol()-horizontal+dx)*gridW;
        int starty=(currentS.getRow()-vertical+dy)*gridH;
        int endx=startx+gridW;
        int endy=starty+gridH;
        hitArr[0][apos]=startx/gridW+horizontal;
        hitArr[1][apos]=starty/gridH+vertical;
        apos++;
        currentCanvas.drawRect(startx, starty, endx, endy, p);
    }

    public void drawMove(int dx, int dy, Paint p){
        int startx=(currentS.getCol()-horizontal+dx)*gridW;
        int starty=(currentS.getRow()-vertical+dy)*gridH;
        int endx=startx+gridW;
        int endy=starty+gridH;
        moveC[pos]=startx/gridW+horizontal;
        moveR[pos]=starty/gridH+vertical;
        pos++;
        currentCanvas.drawRect(startx, starty, endx, endy, p);
    }



    public void resetTouch(){
        touchC = -1;
        touchR = -1;
    }

    public void swipeUp(){ if (vertical<=ROW-sROW+1) vertical+=1; }

    public void swipeDown(){ if (vertical-1>=0) vertical-=1; }

    public void swipeLeft(){
        if (horizontal<COL-sCOL) horizontal+=1;
    }

    public void swipeRight(){
        if (horizontal-1>=0) horizontal-=1;
    }

    public void update(){
        /*
        Paint painter=new Paint();
        painter.setColor(android.graphics.Color.WHITE);
        painter.setStyle(Paint.Style.STROKE);

        painter.setTextSize(16);
        int boundaryRow=currentRow+sROW;
        int boundaryCol=currentCol+sCOL;
        for(int i=currentRow;i<boundaryRow;i++){
            for(int j = currentCol;j<boundaryCol;j++){
                MapPixel mp = map[i][j];
                canvas.drawText((i-currentRow)*sCOL+(j-currentCol)+"",gridW*j,gridH*i,painter);

            }
        }
        */
    }
}