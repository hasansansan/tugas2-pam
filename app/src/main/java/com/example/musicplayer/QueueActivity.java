package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;


public class QueueActivity extends AppCompatActivity {

    Queue queue = new LinkedList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
    }


}