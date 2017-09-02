package com.mlcheckers.android;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Michael Li on 8/23/2017.
 */

public class LevelsActivity extends AppCompatActivity{
    private Button level0, level1;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        level0=level1=(Button) findViewById(R.id.level0);
        level1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent temp=new Intent(LevelsActivity.this, CanvasActivity.class);
                temp.putExtra("level",0);
                startActivity(temp);
            }
        });

        level1=(Button) findViewById(R.id.level1);
        level1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent temp=new Intent(LevelsActivity.this, CanvasActivity.class);
                temp.putExtra("level",1);
                startActivity(temp);
            }
        });
    }

}
