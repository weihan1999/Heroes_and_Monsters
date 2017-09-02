package com.mlcheckers.android;

/**
 * Created by Michael Li on 7/19/2017.
 */

public class Skill {
    private String name;
    private String dmgType;
    private int mana;
    private int dmg;
    private int cd;
    private int rank;
    private int range;
    private String[] types={"point","aoe","cross"};


    public Skill(String n, int cost, int d, int r, String type){
        this.name=new String(n);
        this.mana=cost;
        this.dmg=d;
        this.rank=1;
        this.range=r;
        this.dmgType=type;
    }

    public int getRange(){return range;}
    public String getName(){return name;}
    public void setMana(int m){ mana=m; }
    public int getMana(){ return mana;}
    public void setDmg(int d){dmg=d;}
    public int getDmg(){ return dmg; }
    public void setCd(int c){ cd=c;}
    public int getCd(){ return cd; }
    public void rankUp(){rank++;}
    public int getRank(){ return rank; }
}
