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
import android.graphics.RectF;
import android.media.Image;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by kum on 01/08/2015.
 */
public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint,canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas canvas;
    private Bitmap bitmap;

    private float brushSize,lastBrushSize;
    private boolean erase = false;



    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupDrawing();
    }



    public void setErase(boolean value){
        erase = value;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);

    }

    public void setColor(String color){
        invalidate();
        paintColor = Color.parseColor(color);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
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
    public boolean onTouchEvent(MotionEvent event) {
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
                //sendFileToServer();
                drawPath.reset();
                break;
            default: return false;
        }

        invalidate();
        return true;

    }

    private void sendFileToServer() {

        Bitmap bm = this.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        ParseFile parseFile = new ParseFile("temp", data);
        parseFile.saveInBackground();

        ParseObject parseObject = new ParseObject("fileupload");
        parseObject.put("FileName", "abc");
        parseObject.put("File", parseFile);
        parseObject.saveInBackground();
    }

    public void drawImage(Bitmap bm){
        //Ve

        canvas.drawLine(0,0,200,200,drawPaint);
        canvas.drawBitmap(bm, 0, 0, drawPaint);
        Toast.makeText(getContext(), bm.toString(), Toast.LENGTH_LONG).show();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

}
