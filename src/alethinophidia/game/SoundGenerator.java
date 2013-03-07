package alethinophidia.game;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import alethinophidia.utils.GameSettings;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


//TODO:Finish sounds

public class SoundGenerator {
	
	public static final int SOUND_CLICK = 4;
	
	public static final int SOUND_EAT_1 = 5;
	public static final int SOUND_EAT_2 = 6;
	public static final int SOUND_EAT_3 = 7;
	
	public static final int SOUND_DIE_1 = 8;
	public static final int SOUND_DIE_2 = 9;
	public static final int SOUND_DIE_3 = 10;
	
	public static final int SOUND_ITEM_CREATE = 11;
	public static final int SOUND_ITEM_DESTROY = 12;

	public static final int SOUND_SPEED_UP = 13;
	public static final int SOUND_SLOW_DOWN = 14;
	
	public static final int SOUND_POISON_1 = 15;
	public static final int SOUND_POISON_2 = 16;
	public static final int SOUND_POISON_3 = 17;
	
	private SoundPool pool;
	private HashMap<Integer, Integer> poolMap;
	private AudioManager manager;
	private Context context;
	private GameSettings settings;
	
	public SoundGenerator(Context context, GameSettings settings){
		this.settings = settings;
		initSound(context);
	}
	
	public void initSound(Context context){
		this.context = context;
		initSound();
	}
	
	private void initSound(){
		pool = new SoundPool(8,AudioManager.STREAM_MUSIC,0);
		poolMap =  new HashMap<Integer,Integer>();
		manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void putSound(int key, String filename){
		AssetFileDescriptor afd;
		try {
			afd = context.getAssets().openFd(filename);
			poolMap.put(key, pool.load(afd, 1));
			afd.close();
			
		} catch (IOException e) {
			Log.e("SoundGenerator", "putSound IOException");
			e.printStackTrace();
			poolMap.put(key,  null);
		}		
	}
	
	public void stopSound(int streamID){
		pool.stop(streamID);
	}
	
	/**
	 * @param loopTimes : 0 - no loop; -1 - loop forever, n > 0 - loop n+1 times
	 */
	public void playSound(int key, int loopTimes){
		float volume = (float)manager.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if(!settings.sound)volume = 0;
		int poolGet = poolMap.get(key);
		if(pool.play(poolGet, volume, volume, 1, loopTimes, 1.0f)==0)
			Log.e("playSound", "pool.play zeroed");
	}

	
}

