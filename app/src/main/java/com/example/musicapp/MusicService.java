package com.example.musicapp;

import static com.example.musicapp.ApplicationClass.ACTION_NEXT;
import static com.example.musicapp.ApplicationClass.ACTION_PLAY;
import static com.example.musicapp.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicapp.ApplicationClass.CHANNEL_ID_2;
import static com.example.musicapp.PlayerActivity.listSongs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.net.URI;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFile> musicFiles = new ArrayList<>();
    Uri uri;
    int position=-1;
    ActionPlaying actionPlaying;
    public static final String MUSIC_LAST_PLAYED="LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME="ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","Method");
        return mBinder;
    }



    public class MyBinder extends Binder {
        public MusicService getService (){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition=intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition!=-1){
            playMedia(myPosition);
        }
        if (actionName != null){
            switch (actionName){
                case "playPause":
//                    Toast.makeText(this,"PlayPause",Toast.LENGTH_SHORT).show();
                    if (actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
//                    Toast.makeText(this,"Next",Toast.LENGTH_SHORT).show();
                    if (actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
//                    Toast.makeText(this,"Previous",Toast.LENGTH_SHORT).show();
                    if (actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles=listSongs;
        position=StartPosition;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles!=null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start() {
        mediaPlayer.start();
    }
    boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }
    void stop (){
        mediaPlayer.stop();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration (){
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int positionInner){
        position=positionInner;
        uri =  Uri.parse(musicFiles.get(position).getPath());
        SharedPreferences.Editor editor=getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE,uri.toString());
        editor.putString(ARTIST_NAME,musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME,musicFiles.get(position).getTitle());
        editor.apply();

        mediaPlayer=MediaPlayer.create(getBaseContext(),uri);
    }
    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    void pause(){
        mediaPlayer.pause();
    }
    void OnComplete(){
        mediaPlayer.setOnCompletionListener(this);

    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying!=null){
            actionPlaying.nextBtnClicked();
            if (mediaPlayer!=null){
                createMediaPlayer(position);
                mediaPlayer.start();
                OnComplete();
            }
        }

    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }
    void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying=actionPlaying;
    }
    void nextBtnClicked(){
        if (actionPlaying!=null){
            actionPlaying.nextBtnClicked();
        }
    }
    void previousBtnClicked(){
        if (actionPlaying!=null){
            actionPlaying.prevBtnClicked();
        }
    }
    void playPauseBtnClicked(){
        if (actionPlaying!=null){
            actionPlaying.playPauseBtnClicked();
        }
    }

}
