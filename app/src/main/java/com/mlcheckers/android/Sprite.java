package com.mlcheckers.android;

/**
 * Created by Michael Li on 7/8/2017.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.List;
import java.util.ArrayList;

public class Sprite extends Object {

    private Bitmap sprite;
    private int width;
    private int height;
    private int row;
    private int col;
    private int walk=3;
    private int range=1;
    private String state;
    private String side;
    private String job;
    private int hp;
    private int mp;
    private int exp;
    private int dmg;
    private List<Skill> skills=new ArrayList<Skill>(0);
    private boolean alive;
    public boolean hadWalked=false;
    private List<Sprite> items=new ArrayList<Sprite>();

    public static String[] STATES={"ready","selected","walk","hit","cast","rest"};
    public static String[] SIDES={"ally","foe"};

    public Sprite(Bitmap image, int row, int col, int fileRow, int fileCol, String n) {
        super(image, row, col);
        this.sprite=this.createSubImage(fileRow,fileCol);
        job=n;
        if(job.equals("wizard")) this.setWizard();
        else if(job.equals("slime")) this.setSlime();
        else if(job.equals("lancer")) this.setLancer();
        else if(job.equals("armour")||job.equals("weapon")) {
            this.side="";
            this.state="equipment";
        }

        alive=true;
    }
    public Sprite(Bitmap image, int row, int col, int fileRow, int fileCol, String n,int r, int c) {
        super(image, row, col);
        this.sprite=this.createSubImage(fileRow,fileCol);
        job=n;
        this.setLocation(r,c);
        if(job.equals("wizard")) this.setWizard();
        else if(job.equals("slime")) this.setSlime();
        else if(job.equals("lancer")) this.setLancer();
        else if(job.equals("centaur")) this.setCentaur();
        else if(job.equals("armour")||job.equals("weapon")) {
            this.side="";
            this.state="equipment";
        }

        alive=true;
    }

    public void equip(Sprite item){
        items.add(item);
    }

    public List<Sprite> getItems(){
        return items;
    }

    public void setWizard(){
        this.hp=15;
        this.dmg=2;
        mp=10;
        this.range=2;
        skills.add(new Skill("Fire Blast",3,5,3,"point"));
        this.walk=3;

    }

    public void setLancer(){
        this.hp=20;
        this.dmg=5;
        mp=6;
        this.range=1;
        skills.add(new Skill("Lunge",2,6,2,"point"));
        this.walk=3;
    }

    public void setSlime(){
        this.hp=10;
        this.dmg=4;
        this.mp=10;
        this.range=1;
        state="rest";
        this.side="foe";
    }

    public void setCentaur(){
        this.hp=15;
        this.dmg=4;
        this.mp=10;
        this.range=3;
        state="rest";
        this.side="foe";
    }

    public void setAlive(boolean tf){
        this.alive=tf;
    }

    public boolean getAlive(){
        return this.alive;
    }

    public void takeDmg(int n){
        this.hp-=n;
        if (hp<=0) {
            this.alive=false;
            this.state="rest";
        }
    }

    public void setLocation(int r, int c){
        row=r;
        col=c;
    }

    public boolean sameLocation(int r, int c){
        return row==r&&col==c;
    }

    public void setBitmap(Bitmap b){sprite=b;}

    public String getJob(){return job;}

    public int getDmg(){return this.dmg;}

    public void useMp(int n){this.mp-=n;}

    public Skill getSkill(int i){return skills.get(i);}

    public void setHp(int h){ hp=h;}

    public int getHp(){return hp;}

    public void setMp(int m) {mp=m;}

    public int getMp(){return mp;}

    public void setSide(String side) { this.side=side;}

    public String getSide() { return side;}

    public void setState(String state){this.state=state;}

    public String getState(){ return state; }

    public void setRow(int row){ this.row=row; }

    public void setCol(int col){ this.col=col; }

    public int getRow(){ return row; }

    public int getCol(){ return col; }

    public int getWalk(String type){
        if(type==null) return walk;
        else if(type.equals("rock")) return walk-1;
        else return walk;
    }
    public int getWalk(){return walk;}

    public int getRange(){ return range; }

    public Bitmap getBitmap(){ return sprite; }

    public Bitmap getBitmap(int row, int col){ return this.createSubImage(row,col); }

    /*public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, x, y, null);
    } */ //allow the (x,y) coordinates to be updated by MainMap
}