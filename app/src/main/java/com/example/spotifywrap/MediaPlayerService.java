package com.example.spotifywrap;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MediaPlayerService extends Service {

    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playTrack(String audioUrl) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } else {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                // Ensure that mediaPlayer is not null and is prepared before starting
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
