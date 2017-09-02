package com.mlcheckers.android;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/**
 * Created by Michael Li on 7/29/2017.
 */

public class FoeAi {

    private List<Sprite> sprites;
    private List<MapPixel> terrain;
    private Sprite foe;
    private int[][] walkBox;
    private int[] prio;
    private Sprite target;
    private int index=0;
    private int horizontal;
    private int vertical;
    private int distance;
    int row;
    int col;
    int range;

    public FoeAi(List<Sprite> s,List<MapPixel> t){
        this.sprites=s;
        this.terrain=t;
    }

    public void decide(Sprite f,int v, int h, String job){
        foe=f;
        vertical=v;
        horizontal=h;
        if(job.equals("slime"))slime();
        else if(job.equals("centaur"));
    }

    public void centaur(Sprite f, int v, int h){
        initial();
    }

    public void slime(){
        initial();

        int nearest=0;
        if(!inRange()){
            for (int i = 1; i < walkBox[0].length; i++) {
                if (Math.abs(target.getCol() - walkBox[0][i]) + Math.abs(target.getRow() - walkBox[1][i]) <=
                        Math.abs(target.getCol() - walkBox[0][nearest]) + Math.abs(target.getRow() - walkBox[1][nearest]) &&
                        !(walkBox[0][i] == target.getCol() && walkBox[1][i] == target.getRow()) &&
                        walkBox[0][i] != -100 && walkBox[1][i] != -100) {
                    nearest = i;
                }
            }
        }


        if(!inRange()) {
            foe.setCol(walkBox[0][nearest]);
            foe.setRow(walkBox[1][nearest]);
        }

        if((inRange())){
            target.takeDmg(foe.getDmg());
        }

    }

    public void decideRock(Sprite f, int v, int h){
        foe=f;
        vertical=v;
        horizontal=h;
        int row=foe.getRow();
        int col=foe.getCol();
        int distance;
        int direction;
        int range=foe.getRange();
        String type=null;
        for(MapPixel t:terrain){
            if(t.getRow()==row&&t.getCol()==col){
                type=t.getType();
                break;
            }
        }
        int walk=foe.getWalk(type);
        walkBox=new int[2][2*walk*walk+2*walk];
        prio=new int[2*walk*walk+walk*2];
        String[] DIRECTION={"1=Q1","2=Q2","3=Q3","4=Q4","5 = positive x","6 = positive y","7 = negative x", "8 = negative y"};
       target=null;

        for (int i=0;i<sprites.size();i++){
            if(sprites.get(i).getSide().equals("ally")){
                Sprite temp=sprites.get(i);
                int tempR=temp.getRow();
                int tempC=temp.getCol();
                distance=(row-tempR)*(row-tempR)+(col-tempC)*(col-tempC);
                int closest=10000;
                if(distance<closest){
                    target=temp;
                }
            }
        }

        if(target==null) return;

        index=0;
        //saveMove(0,0);
        for(int i=1;i<=walk;i++) {
            saveMove(i, 0);
            saveMove(-i, 0);
            saveMove(0, i);
            saveMove(0, -i);
            for (int j = 1; j <= walk - i; j++) {
                saveMove(-i, j);
                saveMove(i, j);
                saveMove(i, -j);
                saveMove(-i, -j);
            }
        }

        int nearest=0;
        int priority=100;
        boolean notInRange=true;
        for(int i=1;i<walkBox[0].length;i++){
            int d=Math.abs(row-target.getRow())+Math.abs(col-target.getCol());
            if(d<=foe.getRange()) {
                notInRange = false;
                break;
            }
            if(Math.abs(target.getCol()-walkBox[0][i])+Math.abs(target.getRow()-walkBox[1][i])<=
                    Math.abs(target.getCol()-walkBox[0][nearest])+Math.abs(target.getRow()-walkBox[1][nearest])&&
                    !(walkBox[0][i]==target.getCol()&&walkBox[1][i]==target.getRow())&&
                    walkBox[0][i]!=-100&&walkBox[1][i]!=-100&&prio[i]<=priority){
                nearest=i;
                priority=prio[i];
            }
        }


        if(notInRange) {
            foe.setCol(walkBox[0][nearest]);
            foe.setRow(walkBox[1][nearest]);
        }

        if((Math.abs(foe.getCol()-target.getCol())+Math.abs(foe.getRow()-target.getRow()))<=foe.getRange()){
            target.takeDmg(foe.getDmg());
        }

    }

    public void initial(){
        target=null;
        row=foe.getRow();
        col=foe.getCol();
        range=foe.getRange();
        String type=null;
        for(MapPixel t:terrain){
            if(t.getRow()==row&&t.getCol()==col){
                type=t.getType();
                break;
            }
        }
        int walk=foe.getWalk(type);
        walkBox=new int[2][2*walk*walk+2*walk];
        prio=new int[2*walk*walk+walk*2];
        target=null;
        int closest=10000;
        for(Sprite s:sprites){
            if(s.getSide().equals("ally")&&s.getAlive()){
                int r=s.getRow();
                int c=s.getCol();
                distance=Math.abs(row-r)+Math.abs(col-c);
                if(distance<closest){
                    closest=distance;
                    target=s;
                }
            }
        }

        if(target==null) return;
        index=0;
        //saveMove(0,0);
        for(int i=1;i<=walk;i++) {
            saveMove(i, 0);
            saveMove(-i, 0);
            saveMove(0, i);
            saveMove(0, -i);
            for (int j = 1; j <= walk - i; j++) {
                saveMove(-i, j);
                saveMove(i, j);
                saveMove(i, -j);
                saveMove(-i, -j);
            }
        }
    }

    public boolean inRange(){
        int d=Math.abs(foe.getRow()-target.getRow())+Math.abs(foe.getCol()-target.getCol());
        if(d<=foe.getRange()) return true;
        return false;
    }

    public void saveMove(int dx, int dy){
        walkBox[0][index] = foe.getCol() + dx;
        walkBox[1][index] = foe.getRow() + dy;
        prio[index]=1;
        for(MapPixel t:terrain){
            if(t.getType().equals("rock")&&t.getCol()==walkBox[0][index] && t.getRow()==walkBox[1][index]){
                prio[index]=2;
            }
        }
        for(Sprite s:sprites){
            if(s!=foe) {
                if (walkBox[0][index] == s.getCol() && walkBox[1][index] == s.getRow()) {
                    walkBox[0][index] = -100;
                    walkBox[1][index] = -100;
                }
            }
        }
        index++;
    }
}