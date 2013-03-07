package alethinophidia.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alethinophidia.game.GameView;
import alethinophidia.game.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * This class is used to load all the bitmaps
 * for any object that needs them.
 * First it checks whether or not it should
 * load low quality textures via GameSettings.
 * Then bitmap is loaded from /assets/H-res/
 * or /assets/Low-res folders.
 * 
 * Bitmaps are stored in list in form of small
 * nested structure consisting of String and Bitmap.
 * loadBitmap method checks if bitmap with requested
 * name already exists, to avoid duplicates.
 * 
 * @author £ukasz Piotrowski
 */

public class BitmapLoader{
	private Context context;
	private GameSettings settings;
	private GameView gameView;
	private List<NamedBitmap> bitmaps; 
	
	private class NamedBitmap{
		public String name;
		public Bitmap bmp;
		
		public NamedBitmap(String name, Bitmap bmp){
			this.name = name;
			this.bmp = bmp;
		}
	}
	
	public BitmapLoader(Context context, GameSettings settings, GameView gameView){
		this.context = context;
		this.settings = settings;
		this.gameView = gameView;
		bitmaps = new ArrayList<NamedBitmap>();
	}
	
	public Bitmap loadBitmap(String name){
		String filename;
		if(settings.smallTextures)
			filename = "Lo-res"+name;
		else
			filename = "Hi-res"+name;
		if(!bitmaps.isEmpty()){
			for(NamedBitmap namedBitmap : bitmaps){
				if(filename.equalsIgnoreCase(namedBitmap.name))
					return namedBitmap.bmp;
			}
		}
		return addBitmapToList(filename);
	}
	
	private Bitmap addBitmapToList(String filename){
		Bitmap bmp;
		BufferedInputStream stream;
		try{
			AssetManager assetManager = context.getAssets();
			stream = new BufferedInputStream(assetManager.open(filename));
			BitmapFactory.Options bfopt = new BitmapFactory.Options();
			bfopt.inScaled = false; 
			bfopt.inPreferredConfig = Bitmap.Config.ARGB_8888; 
			bfopt.inDither = true; 
			bfopt.inPurgeable = true;
			bmp = BitmapFactory.decodeStream(stream, null, bfopt);
			stream.close();
			bitmaps.add(new NamedBitmap(filename,bmp));
			return bitmaps.get(bitmaps.size()-1).bmp;
		} catch (IOException e) {
			return BitmapFactory.decodeResource(gameView.getResources(), R.drawable.err);
		}
	}
	
	public void clearBitmaps(){
		for(NamedBitmap namedBitmap : bitmaps){
			namedBitmap.bmp.recycle();
		}
		bitmaps.clear();
	}
}
