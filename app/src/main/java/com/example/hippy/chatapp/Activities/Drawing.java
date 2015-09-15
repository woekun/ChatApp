package com.example.hippy.chatapp.Activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.utils.FileChooser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class Drawing extends AppCompatActivity {

    private DrawingView drawingView;
    private ImageButton currentColor,drawButton, eraseButton;
    private float smallBrush,mediumBrush,largeBrush;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = (DrawingView) findViewById(R.id.drawing);

        LinearLayout lnrLay = (LinearLayout) findViewById(R.id.colorspane);
        currentColor = (ImageButton) lnrLay.getChildAt(0);
        currentColor.setImageDrawable(getResources().getDrawable(R.drawable.paint_press));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawButton = (ImageButton) findViewById(R.id.brushbtn);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog brushDialog = new Dialog(Drawing.this);
                brushDialog.setTitle("Choose a brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);


                ImageButton smlBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                smlBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawingView.setBrushSize(smallBrush);
                        drawingView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }


                });

                ImageButton medBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                medBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawingView.setBrushSize(mediumBrush);
                        drawingView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton lrgBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
                lrgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawingView.setBrushSize(largeBrush);
                        drawingView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                drawingView.setErase(false);
                brushDialog.show();
            }
        });

        eraseButton = (ImageButton) findViewById(R.id.erasebtn);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog brushDialog = new Dialog(Drawing.this);
                brushDialog.setTitle("Erase size:");
                brushDialog.setContentView(R.layout.brush_chooser);

                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawingView.setErase(true);
                        drawingView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawingView.setErase(true);
                        drawingView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawingView.setErase(true);
                        drawingView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
            }
        });

    }

    public void newClicked(View view){
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                drawingView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
        drawingView.setErase(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void paintClicked(View view){

        if(view!=currentColor){
            ImageButton imBtn = (ImageButton) view;
            String color = view.getTag().toString();
            drawingView.setColor(color);
            imBtn.setImageDrawable(getResources().getDrawable(R.drawable.paint_press));
            currentColor.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currentColor = (ImageButton) view;
        }
        drawingView.setErase(false);

    }

    private AlertDialog.Builder saveDialog;
    private String path;
    public void btnSaveClicked(View view){
        saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save");
        saveDialog.setMessage("You really want to save?");
        saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawingView.setDrawingCacheEnabled(true);
                final Bitmap bm = drawingView.getDrawingCache();

                //getPictureNamehere
                final FileChooser fileChooser = new FileChooser(Drawing.this);
                fileChooser.setActionAfterChoose(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        path=fileChooser.getPath();
                        File file = new File(path);
                        Toast.makeText(Drawing.this, path, Toast.LENGTH_SHORT).show();
                        FileOutputStream fos;
                        try {
                            if (file.createNewFile()) {
                                fos = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.close();
                                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        drawingView.destroyDrawingCache();
                        fileChooser.getDialog().dismiss();
                    }
                });
            }

        });

        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        saveDialog.show();
        drawingView.setErase(false);
    }

    public void btnLoadClicked(View view){
        //Lay 1 cai anh = file chooser
        //Truyen anh vao cai DrawingView.
        final FileChooser fileChooser = new FileChooser(Drawing.this);
        fileChooser.setActionAfterChoose(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Drawing.this, fileChooser.getPath(), Toast.LENGTH_SHORT).show();

                Uri uri = Uri.fromFile(new File(fileChooser.getPath()));
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    BitmapFactory.Options op = new BitmapFactory.Options();
                    op.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is, null, op);
                    is.close();

                    //if (op.outWidth == -1 || op.outHeight == -1) return;
                    int origin = (op.outHeight > op.outWidth) ? op.outHeight : op.outWidth;

                    double ratio = (origin > 1200) ? (origin / 1200) : 1.0;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);


                    is = Drawing.this.getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
                    is.close();


                    drawingView.drawImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                fileChooser.getDialog().dismiss();
            }
        });
        drawingView.setErase(false);
    }

    public void btnRefreshClicked(View view){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("fileupload");

        query.getInBackground("q00ARDXTDs", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e ==null){
                    try {
                        ParseFile parseFile = parseObject.getParseFile("File");
                        byte[] data = parseFile.getData();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        drawingView.drawImage(bitmap);


                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Drawing.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        drawingView.setErase(false);
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


}


