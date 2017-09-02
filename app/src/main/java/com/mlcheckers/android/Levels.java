package com.mlcheckers.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Li on 7/17/2017.
 */

class Level {
    private List<MapPixel> background;
    private List<Sprite> sprites;
    private List<MapPixel> terrain;
    private MapPixel[][] whole;

    public Level(List<MapPixel> background, List<MapPixel> terrain, List<Sprite> sprites,MapPixel[][] whole){
        this.background=background;
        this.sprites=sprites;
        this.terrain=terrain;
        this.whole=whole;
    }

    public List<MapPixel> getBackground(){return background;}
    public List<Sprite> getSprites(){return sprites;}
    public List<MapPixel> getTerrain(){ return terrain;}
    public MapPixel[][] getWhole(){return whole;}
}


public class Levels {

    private GameSurface gameSurface;
    private List<Sprite> sprites=new ArrayList<Sprite>();
    private List<MapPixel> terrain=new ArrayList<MapPixel>();
    private List<MapPixel> background=new ArrayList<MapPixel>();
    private MapPixel[][] whole=new MapPixel[48][64];
    

    public Levels(GameSurface gs) {
        this.gameSurface=gs;
        final BitmapFactory.Options options= new BitmapFactory.Options();
        options.inSampleSize=1;
        options.inJustDecodeBounds=false;
        gameSurface.MASTER=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.master48x64,options);
        /*Bitmap master1=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.master24x32_1,options);
        for(int r=0;r<24;r++){
            for(int c=0;c<32;c++){
                whole[r][c] =new MapPixel(master1,24,32,r,c);
            }
        }
        master1.recycle();
        Bitmap master2=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.master24x32_2,options);
        for (int r=0;r<24;r++){
            for(int c=32;c<64;c++) whole[r][c] = new MapPixel(master2,24,32,r,c-32);
        }
        master2.recycle();
        Bitmap master3=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.master24x32_3,options);
        for(int r=24;r<48;r++){
            for(int c=0;c<32;c++){
                whole[r][c]=new MapPixel(master3,24,32,r-24,c);
            }
        }
        master3.recycle();
        Bitmap master4=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.master24x32_4,options);
        for(int r=24;r<48;r++){
            for(int c=32;c<64;c++) whole[r][c]=new MapPixel(master4,24,32,r-24,c-32);
        }
        master4.recycle();*/
    }
    public Level getLevel(int lv){
        if(lv==0){
            createWizard(1,3);
            createLancer(1,4);


            Bitmap floor=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.floor);
            MapPixel ground=new MapPixel(floor,1,1,0,0,"");
            background.add(ground);
        }
        if(lv==1){
            //create allies
            createWizard(1,3);
            createLancer(3,1);
            //Create foes
            int[][] slimes={{1,14},{5,14},{3,16}};
            createSlimes(slimes);

            //Create Terrain
            //{row,col}
            int[][] rocks={{7,2},{5,4},{6,7},{3,9},{7,9},{5,11},{7,13},{5,14},{7,16}};
            createRocks(rocks);

            //Create Background
            Bitmap grass= BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.grass);
            MapPixel wholeGrass=new MapPixel(grass);
            Bitmap floor=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.floor);
            MapPixel ground=new MapPixel(floor,1,1,0,0,"");
            background.add(ground);
        }
        Level lvl=new Level(background,terrain,sprites,whole);
        return lvl;
        //return new Level(null,null, null);
    }

    public void createWizard(int r, int c){
        Sprite player1=new Sprite(gameSurface.MASTER,48,64,31,0,"wizard");
        player1.setLocation(r,c);
        player1.setSide("ally");
        player1.setState("ready");
        sprites.add(player1);
        Sprite armour1=new Sprite(gameSurface.MASTER,48,64,33,0,"armour");
        armour1.setLocation(r,c);
        player1.equip(armour1);
        sprites.add(armour1);
        Sprite weapon1=new Sprite(gameSurface.MASTER,48,64,38,1,"weapon");
        weapon1.setLocation(r,c);
        player1.equip(weapon1);
        sprites.add(weapon1);
    }
    public void createLancer(int r, int c){
        Sprite player2=new Sprite(gameSurface.MASTER,48,64,31,10,"lancer");
        player2.setLocation(r,c);
        player2.setSide("ally");
        player2.setState("ready");
        sprites.add(player2);
        Sprite armour2=new Sprite(gameSurface.MASTER,48,64,33,9,"armour");
        armour2.setLocation(r,c);
        player2.equip(armour2);
        sprites.add(armour2);
        Sprite weapon2=new Sprite(gameSurface.MASTER,48,64,38,18,"weapon");
        weapon2.setLocation(r,c);
        player2.equip(weapon2);
        sprites.add(weapon2);
        Sprite shield2=new Sprite(gameSurface.MASTER,48,64,39,7,"weapon");
        shield2.setLocation(r,c);
        sprites.add(shield2);
        player2.equip(shield2);
    }
    public void createRocks(int[][] pos){
        Bitmap stone=BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.rock);
        for(int i=0;i<pos.length;i++){
            terrain.add(new MapPixel(stone, 1, 1, 0, 0, "rock", pos[i][0], pos[i][1]));
        }
    }

    public void createSlimes(int[][]pos){
        for(int i=0;i<pos.length;i++) {
            sprites.add(new Sprite(gameSurface.MASTER, 48, 64, 1, 9, "slime", pos[i][0], pos[i][1]));
        }
    }

    public void createCentaurs(int[][] pos){
        for(int i=0;i<pos.length;i++){
            sprites.add(new Sprite(gameSurface.MASTER,48,64,1,16,"centaur",pos[i][0],pos[i][1]));
        }
    }
}