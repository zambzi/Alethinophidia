package alethinophidia.game;

import java.io.IOException;

import alethinophidia.utils.GameSettings;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicGenerator {

	public static final int MUSIC_MAIN_THEME = 0;
	public static final int MUSIC_SHARK_TUNE = 1;
	public static final int MUSIC_TUNE_3 = 2;
	public static final int MUSIC_TUNE_4 = 3;
	
	private MediaPlayer player;
	private Context context;
	private boolean paused = false;
	private boolean stopped = true;
	private float volume;
	private GameSettings settings;
	
	public MusicGenerator(Context context, GameSettings settings){
		this.settings = settings;
		this.context = context;
		player = new MediaPlayer();
		AudioManager manager;
		manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		volume = (float)manager.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	public void play(int tune, boolean loop){
		if(settings.music)player.setVolume(volume, volume);
		else player.setVolume(0, 0);
		String tuneName;
		switch(tune){
		case 0 : tuneName = "audio/music/main_theme.ogg"; break;
		case 1 : tuneName = "audio/music/theme2.ogg"; break;
		case 2 : tuneName = "audio/music/theme3.ogg"; break;
		case 3 : tuneName = "audio/music/theme4.ogg"; break;
		default : tuneName = "audio/music/main_theme.ogg";
		}
		try {
			AssetFileDescriptor afd = context.getAssets().openFd(tuneName);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
		} catch (IOException e) {
			Log.e("MusicGenerator", "loading music file IOException");
			e.printStackTrace();
		}
		player.setLooping(loop);
		start();
	}
	
	public void pause(){
		if(!paused && !stopped){
			player.pause();
			paused = true;
		}
	}
	
	public void stop(){
		if(!stopped){
			player.stop();
			player.reset();
			stopped = true;
		}
	}
	
	public void resume(){
		if(!stopped && paused){
			start();
			paused = false;
		}
	}
	
	public void pauseResume(){
		if(!paused){
			player.pause();
			paused = true;
		}
		else{
			player.start();
			paused = false;
		}
	}
	
	private void start(){
		try {
			if(stopped){
				player.prepare();
			}
			player.start();
			stopped = false;
			paused = false;
		} catch (IllegalStateException e) {
			Log.e("MusicGenerator", "player preparation illegal state");
		} catch (IOException e) {
			Log.e("MusicGenerator", "player preparration IO exception");
		}
	}
	
	public boolean isPlayerPlaying(){
		return player.isPlaying();
	}
	
	public void setMute(boolean mute){
		player.setVolume(mute ? 0 : volume, mute ? 0 : volume);
	}
}
