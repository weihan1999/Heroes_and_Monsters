package com.mlcheckers.android;

/**
 * Created by Michael Li on 7/13/2017.
 */
import android.graphics.Bitmap;
public abstract class Object {
    protected Bitmap image;

    protected int fileheight;
    protected int filewidth;

    protected int imgheight;
    protected int imgwidth;

    protected int x;
    protected int y;

    public Object(){}

    public Object(Bitmap image, int row, int col){
        this.image=image;
        this.fileheight=image.getHeight();
        this.filewidth=image.getWidth();
        this.imgheight=this.fileheight/row;
        this.imgwidth=this.filewidth/col;
    }

    protected Bitmap createSubImage(int row, int col){
        Bitmap subImage=Bitmap.createBitmap(image, col*imgwidth, row*imgheight, imgwidth, imgheight);
        return subImage;
    }

}
