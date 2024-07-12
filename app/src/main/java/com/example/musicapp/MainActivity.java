package com.example.musicapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView play, prev, next, headphone;
    TextView songtitle;
    SeekBar seekbartime, seekbarvol;
    static MediaPlayer mMediaPlayer;
    private Runnable runnable;
    private MediaController mediaController;
    private AudioManager audioManager;
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//initializing view
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        play = findViewById(R.id.play);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        headphone = findViewById(R.id.headphone);
        seekbartime = findViewById(R.id.seekbartime);
        seekbarvol = findViewById(R.id.seekbarvol);
        songtitle = findViewById(R.id.songtitle);
// song decliration

        ArrayList<Integer> songs = new ArrayList<>();
        songs.add(0, R.raw.s1);
        songs.add(1, R.raw.s2);
        songs.add(2, R.raw.s3);
        songs.add(3, R.raw.s4);

        int maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbarvol.setMax(maxV);
        seekbarvol.setProgress(curV);

        seekbarvol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));

        play.setOnClickListener(v -> {
            seekbartime.setMax(mMediaPlayer.getDuration());
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                play.setImageResource(R.drawable.play);//pause
            } else {
                mMediaPlayer.start();
                play.setImageResource(R.drawable.pause);//play
            }
            songnames();
        });

        next.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                play.setImageResource(R.drawable.pause);//play
            }
            if (currentIndex < songs.size() - 1) {
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer = mMediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
            mMediaPlayer.start();
            songnames();
        });
        prev.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                play.setImageResource(R.drawable.pause);//play
            }
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = songs.size() - 1;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer = mMediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
            mMediaPlayer.start();
            songnames();
        });

    }

    private void songnames() {
        if (currentIndex == 0) {
            songtitle.setText("Chak De India Song _ Title Song _ Shah Rukh Khan");
            headphone.setImageResource(R.drawable.s1);
        } else if (currentIndex == 1) {
            songtitle.setText("M S  DHONI_ Parwah Nahi");
            headphone.setImageResource(R.drawable.s2);
        } else if (currentIndex == 2) {
            songtitle.setText("Zinda Lyric Video - Bhaag Milkha Bhaag");
           headphone.setImageResource(R.drawable.s3);
        } else if (currentIndex == 3) {
            songtitle.setText("Sultan Title Song _ Salman Khan_ Anushka Sharma");
            headphone.setImageResource(R.drawable.s44);
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekbartime.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
            }
        });

        seekbartime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekbartime.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("HANDLERLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            seekbartime.setProgress(msg.what);
        }
    };

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
}
}