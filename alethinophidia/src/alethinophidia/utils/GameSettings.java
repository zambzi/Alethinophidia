package alethinophidia.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * Reads/Writes and holds settings values
 * 
 * @author £ukasz Piotrowski
 */

public class GameSettings {
	public boolean antiAlias;
	public boolean filtering;
	public boolean smallTextures;
	public boolean sound;
	public boolean music;
	public int difficulty;
	
	public GameSettings(Context context){
		setDefaults();
		parseSettings(false);
	}
	
	private void setDefaults(){
		antiAlias = true;
		filtering = true;
		smallTextures = false;
		sound = true;
		music = true;
		difficulty = 1;
	}
	
	public void becomes(GameSettings settings){
		antiAlias = settings.antiAlias;
		filtering = settings.filtering;
		smallTextures = settings.smallTextures;
		sound = settings.sound;
		music = settings.music;
		difficulty = settings.difficulty;
	}
	
	public void parseSettings(boolean write){
		String state = Environment.getExternalStorageState();
		if(!state.equals(Environment.MEDIA_MOUNTED)){
			Log.e("GameSettings","parser, no External Storage detected");
			if(!write) setDefaults();
		}
		else{
				File externalDir = Environment.getExternalStorageDirectory();
				File settingsDir = new File(externalDir.getAbsolutePath() + File.separator + ".alethinophidia");
				if(!settingsDir.exists() || !settingsDir.isDirectory()){
					settingsDir.mkdir();
				}
				File configFile = new File(externalDir.getAbsolutePath() + File.separator + ".alethinophidia" + File.separator + "config.ini");
				try{
					if(write) saveSettings(configFile);
					else readSettings(configFile);
				} catch (IOException e) {
					if(!write) setDefaults();
					Log.e("GameSettings","parser, IOException");
				}
		}
	}
	
	private void saveSettings(File configFile) throws IOException{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
		bufferedWriter.write(new StringBuilder("antiAliasing=").append((antiAlias==true) ? 1 : 0).toString());
		bufferedWriter.write("\n");
		bufferedWriter.write(new StringBuilder("filtering=").append((filtering==true) ? 1 : 0).toString());
		bufferedWriter.write("\n");
		bufferedWriter.write(new StringBuilder("smallTextures=").append((smallTextures==true) ? 1 : 0).toString());
		bufferedWriter.write("\n");
		bufferedWriter.write(new StringBuilder("sound=").append((sound==true) ? 1 : 0).toString());
		bufferedWriter.write("\n");
		bufferedWriter.write(new StringBuilder("music=").append((music==true) ? 1 : 0).toString());
		bufferedWriter.write("\n");
		bufferedWriter.write(new StringBuilder("difficulty=").append(difficulty).toString());
		bufferedWriter.close();
	}
	
	private void readSettings(File configFile) throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
		String line;
		while((line = bufferedReader.readLine()) != null){
			if(line.startsWith("antiAliasing=")){
				antiAlias = (line.endsWith("1") ? true : false);
			}
			if(line.startsWith("filtering=")){
				filtering = (line.endsWith("1") ? true : false);
			}
			if(line.startsWith("smallTextures=")){
				smallTextures = (line.endsWith("1") ? true : false);
			}
			if(line.startsWith("sound=")){
				sound = (line.endsWith("1") ? true : false);
			}
			if(line.startsWith("music=")){
				music = (line.endsWith("1") ? true : false);
			}
			if(line.startsWith("difficulty=")){
				difficulty = (line.endsWith("1") ? 1 : line.endsWith("2") ? 2 : line.endsWith("0") ? 0 : 1);
			}
		}
	}
}


