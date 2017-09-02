package com.mlcheckers.android;

import android.graphics.Bitmap;

/**
 * Created by Michael Li on 7/8/2017.
 */

public class MapPixel extends Object{
    private String colour;
    private int num;
    private Bitmap bitmap;
    private int row;
    private int col;
    private String type;

    public MapPixel(String color, int n,int r, int c){
        this.colour=color;
        this.num=n;
        this.row=r;
        this.col=c;
    }

    //Whole png
    public MapPixel(Bitmap b){
        this.bitmap=b;
    }

    public MapPixel(int n, int r, int c,Bitmap b, int row,  int col){
        super(b,r,c);
        //this.colour=color;
        this.num=n;
        this.bitmap=this.createSubImage(row,col);
    }

    public MapPixel(Bitmap image, int row, int col, int fileRow, int fileCol, String type) {
        super(image, row, col);
        this.bitmap=this.createSubImage(fileRow,fileCol);
        this.type=type;
    }

    public MapPixel(Bitmap image, int row, int col, int fileRow, int fileCol, String type,int r, int c) {
        super(image, row, col);
        this.bitmap=this.createSubImage(fileRow,fileCol);
        this.type=type;
        this.setLocation(r,c);
    }

    public MapPixel(Bitmap image, int row, int col, int fileRow, int fileCol) {
        super(image, row, col);
        this.bitmap=this.createSubImage(fileRow,fileCol);
    }

    public void setLocation(int r, int c){
        row=r;
        col=c;
    }

    public String getColour(){
        return colour;
    }

    public int getNum(){
        return num;
    }

    public Bitmap getBitmap(){return bitmap;}

    public void setBitmap(Bitmap b){this.bitmap=b;}
    public int getRow(){return row;}
    public int getCol(){return col;}
    public String getType(){return type;}
}