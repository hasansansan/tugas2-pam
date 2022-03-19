package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.shuffleBoolean;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends AppCompatActivity {
    Button btnPlay, btnNext, btnPrevious,btnShuffle;
    TextView txtSongName, txtSongStart, txtSongEnd;
    SeekBar seekMusicBar;


    ImageView imageView;


    String songName;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;

    ArrayList<File> mySongs;

    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Play");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        btnPlay = (Button) findViewById(R.id.BtnPlay);
        btnNext = (Button) findViewById(R.id.BtnNext);
        btnPrevious = (Button) findViewById(R.id.BtnPrevious);


        btnShuffle = (Button)findViewById(R.id.BtnShuffle);
        txtSongName = (TextView) findViewById(R.id.SongTxt);
        txtSongStart = (TextView) findViewById(R.id.TxtSongStart);
        txtSongEnd = (TextView) findViewById(R.id.TxtSongEnd);

        seekMusicBar = (SeekBar) findViewById(R.id.SeekBar);


        imageView = (ImageView) findViewById(R.id.MusicImage);


        if (mediaPlayer != null) {

            mediaPlayer.start();
            mediaPlayer.release();
        }


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getIntegerArrayList("songs");
        String sName = intent.getStringExtra("songname");
        position = bundle.getInt("pos");
        txtSongName.setSelected(true);


        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txtSongName.setText(songName.replace(".mp3","").replace( ".m4a", ""));


        //passing lagu ke media player
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        songEndTime();




        updateSeekBar = new Thread() {
            @Override
            public void run() {

                int TotalDuration = mediaPlayer.getDuration();
                int CurrentPosition = 0;

                while (CurrentPosition < TotalDuration) {
                    try {

                        sleep(500);
                        CurrentPosition = mediaPlayer.getCurrentPosition();
                        seekMusicBar.setProgress(CurrentPosition);

                    } catch (InterruptedException | IllegalStateException e) {

                        e.printStackTrace();
                    }
                }

            }
        };



        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();



        seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });



        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Getting the current duration from the media player
                String currentTime = createDuration(mediaPlayer.getCurrentPosition());

                //Setting the current duration in textView
                txtSongStart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);




        //tombol play
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()) {

                    btnPlay.setBackgroundResource(R.drawable.ic_play_24);

                    mediaPlayer.pause();

                } else {

                    btnPlay.setBackgroundResource(R.drawable.ic_pause_24);

                    mediaPlayer.start();

                    //Creating the Animation
                    TranslateAnimation moveAnim = new TranslateAnimation(-25, 25, -25, 25);
                    moveAnim.setInterpolator(new AccelerateInterpolator());
                    moveAnim.setDuration(600);
                    moveAnim.setFillEnabled(true);
                    moveAnim.setFillAfter(true);
                    moveAnim.setRepeatMode(Animation.REVERSE);
                    moveAnim.setRepeatCount(1);

                    //Setting the Animation for the Image
                    imageView.startAnimation(moveAnim);

                    //Calling the BarVisualizer
//                    visualizer();
                }
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });


        //tombol next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();

                    if (shuffleBoolean){
                        position =getRandom(mySongs.size()-1);
                    }else{
                        position=((position+1)%mySongs.size());
                    }

//                    position = ((position + 1) % mySongs.size());

                    Uri uri1 = Uri.parse(mySongs.get(position).toString());

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);

                    songName = mySongs.get(position).getName();
                    txtSongName.setText(songName);

                    mediaPlayer.start();

                    songEndTime();


                    startAnimation(imageView, 360f);
//                visualizer();
                }else{
                    mediaPlayer.stop();
                    mediaPlayer.release();
                        if (shuffleBoolean){
                            position =getRandom(mySongs.size()-1);
                        }else{
                            position=((position+1)%mySongs.size());
                        }

                    Uri uri1 = Uri.parse(mySongs.get(position).toString());


                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);


                    songName = mySongs.get(position).getName();
                    txtSongName.setText(songName);


                    mediaPlayer.start();


                    songEndTime();



                    startAnimation(imageView, 360f);


                }

                }




        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean){
                    shuffleBoolean =false;
                    btnShuffle.setBackgroundResource(R.drawable.ic_notshuffle_24);
                }
                else{
                    shuffleBoolean =true;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_24);
                }
            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                mediaPlayer.stop();
                mediaPlayer.release();


                position = ((position - 1) % mySongs.size());
                if (position < 0)
                    position = mySongs.size() - 1;


                Uri uri1 = Uri.parse(mySongs.get(position).toString());


                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);

                mediaPlayer.start();
                songEndTime();

                startAnimation(imageView, -360f);


            }

        });







    }





    public void startAnimation(View view, Float degree) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, degree);
        objectAnimator.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }


    //Preparing the Time format for setting to textView
    public String createDuration(int duration) {

        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time = time + min + ":";

        if (sec < 10) {

            time += "0";

        }
        time += sec;
        return time;

    }




    //Method To extract the duration of the current media and setting it to TextView
    public void songEndTime() {
        String endTime = createDuration(mediaPlayer.getDuration());
        txtSongEnd.setText(endTime);
    }




                private int getRandom(int i) {
                Random random = new Random();
                return random.nextInt(i+1);
            }
    public boolean onSupportNavigateUp(){
        onBackPressed();
return true;
    }

    public void onBackPressed(){super.onBackPressed();}
}