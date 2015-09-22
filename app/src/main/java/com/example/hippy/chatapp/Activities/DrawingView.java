package com.example.hippy.chatapp.Activities;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint,canvasPaint,tmp;
    private int paintColor = 0xFF660000;
    private Canvas canvas;
    private Bitmap bitmap;

    private float brushSize,lastBrushSize;
    private boolean erase = false;



    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupDrawing();
    }


    public void setMode(){
        drawPaint.setXfermode(null);
    }

    public void setMode(PorterDuff.Mode mode){
        drawPaint.setXfermode(new PorterDuffXfermode(mode));
    }

    int temp = Color.BLACK;
    public void setErase(boolean value){
        erase = value;
        if(erase)
        {temp = drawPaint.getColor();
            drawPaint.setColor(Color.WHITE);
        }
        else {
            drawPaint.setColor(temp);

        }

    }

    public void setColor(String color){
        invalidate();
        paintColor = Color.parseColor(color);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        drawPaint.setStrokeWidth(brushSize);

    }

    public void setLastBrushSize(float value){
        lastBrushSize = value;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void startNew(){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    private void setupDrawing() {
        drawPath =  new Path();
        drawPaint = new Paint();


        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w,int h,int ow, int oh){
        super.onSizeChanged(w, h, ow, oh);
        bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);



        invalidate();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(drawPath, drawPaint);
                sendFileToServer();
                drawPath.reset();
                break;
            default: return false;
        }

        invalidate();
        return true;

    }

    private void sendFileToServer() {
        this.setDrawingCacheEnabled(true);
        Bitmap bm = this.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] data = stream.toByteArray();
        final ParseFile parseFile = new ParseFile("ass", data);

        parseFile.saveInBackground();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("fileupload");
        query.whereEqualTo("Sender","1");
        query.whereEqualTo("Receiver","kum");

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                parseObject.put("FileName", "asd");
                parseObject.put("File", parseFile);
                parseObject.put("Receiver","kum");
                parseObject.put("Sender","1");
                parseObject.saveInBackground();
            }
        });
        this.destroyDrawingCache();
    }

    public void drawImage(Bitmap bm){
        //Ve

        //ratio = min {origin height/bm height, origin width/bm width}
//        double ratio = (double) canvas.getHeight()/bm.getHeight();
//        if (ratio> (double)canvas.getWidth()/bm.getWidth()) ratio =(double)canvas.getWidth()/bm.getWidth();


        canvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()),
                //new Rect(0, 0,(int) (bm.getWidth()*ratio),(int) (bm.getHeight()*ratio))
                new Rect(0,0,canvas.getWidth(),canvas.getHeight()) , drawPaint);

    }

    public Bitmap getBitmap(){
        return bitmap;
    }

}
