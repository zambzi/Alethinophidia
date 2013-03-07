package alethinophidia.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import alethinophidia.game.GameView;
import android.util.Log;

/**
 * 
 * small class to hold randomly arranged
 * list of maps for the survival mode
 * 
 * @author £ukasz Piotrowski
 */

public class SurvivalMapListManager {
	private String maps[];
	private int level;
	
	public SurvivalMapListManager(GameView gameView){
		level = 0;
		loadMaps(gameView);
	}
	
	private void loadMaps(GameView gameView){
		try{
			maps = gameView.getContext().getAssets().list("Maps");
			Collections.shuffle(Arrays.asList(maps));
		} catch (IOException e) {
			Log.e("SurvivalMapListManager", "IOException in loadMaps()");
		}
	}
	
	public String getRandomMap(){
		level++;
		if(level>maps.length)
			level = 1;
		return maps[level-1];
	}
}
