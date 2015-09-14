package com.example.hippy.chatapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.FileAdapter;

import java.io.File;

/**
 * Created by kum on 17/08/2015.
 */
public class FileChooser {

    ListView listView;
    Dialog dialog;
    FileAdapter listDirAdapter;
    String path=Environment.getExternalStorageDirectory().getPath();
    TextView textView;
    File[] listDir;
    Activity act;

    public FileChooser(Activity act){
        this.act = act;

        this.dialog = new Dialog(act, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);

    }

    public String getPath() {
        EditText editText = (EditText) dialog.findViewById(R.id.editText);
        return path+editText.getText().toString();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setActionAfterChoose(View.OnClickListener listener){

        dialog.setTitle("Choose a file:");
        dialog.setContentView(R.layout.file_chooser_dialog);

        textView = (TextView) dialog.findViewById(R.id.Dir);
        textView.setText(path);
        listDir = new File(path).listFiles();
        listDirAdapter = new FileAdapter(act,listDir);
        listView = (ListView) dialog.findViewById(R.id.listDir);
        listView.setAdapter(listDirAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                path = listDir[position].getPath();
                loadDir(path);

            }
        });

        Button btnUp = (Button) dialog.findViewById(R.id.btnUp);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpClicked(v);
            }
        });


        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(listener);

        dialog.show();

    }

    private void loadDir(String dir){
        if (new File(dir).isDirectory()){
            if (new File(dir).list()==null) {
                listView.setAdapter(null);
                textView.setText(dir);
            }
            else {

                listDir = new File(dir).listFiles();
                listDirAdapter = new FileAdapter(act, listDir);
                listView.setAdapter(listDirAdapter);
                textView.setText(dir);
            }
        }
        else{

            EditText editText = (EditText) dialog.findViewById(R.id.editText);
            editText.setText(new File(dir).getName());
            path = new File(dir).getParent()+"/";
        }
    }

    public void btnUpClicked(View view){
        try{
            path = new File(path).getParentFile().getAbsolutePath();}
        catch (NullPointerException e){return;}

        loadDir(path);
    }


}
