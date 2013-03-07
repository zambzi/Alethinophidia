package alethinophidia.userInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import alethinophidia.game.GameView;
import alethinophidia.utils.GameSettings;
import alethinophidia.utils.HiScores;
import alethinophidia.utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 * Controls scoreboard menu.
 * It directly reads or saves scores
 * to file in .alethinophidia folder on
 * external storage.
 * 
 * @author £ukasz Piotrowski
 */

public class Scoreboard {
	private Field backdrop;
	private Field back;
	private Paint paint;
	private boolean hidden;
	private GameView gameView;
	private MainMenu mainMenu;
	private HiScores hiScores;
	
	private static final String label1 = "Survival Mode Top 5";
	private static final String label2 = "Classic Mode Top 5";
	private static final String label3 = "Free Roam Mode Top 5";
	private static final String label4 = "Certain Death Mode Top 5";

	
	public Scoreboard(GameView gameView, MainMenu mainMenu, GameSettings settings){
		this.gameView = gameView;
		this.mainMenu = mainMenu;
		this.hiScores = gameView.getHiScores();
		hidden = true;
		paint = new Paint();
		paint.setAntiAlias(gameView.getSettings().antiAlias);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		parseScores(false);
		setBitmaps();
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/interface/bigwindow.png");
		backdrop = new Field(new Vector2(0.015,0.05), 0.998f, 0.9f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_back.png");
		back = new Field(new Vector2(0.35,0.82), 0.3f, 0.075f, bmp, gameView);
	}
	
	public void onOpen(){
		parseScores(false);
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			backdrop.onDraw(canvas);
			back.onDraw(canvas);
			drawScores(canvas);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden){
				if(back.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					return true;
				}
			}
		}
		return false;
	}
	
	private void drawScores(Canvas canvas){
		float y = (float)(backdrop.getPosition().getY()+backdrop.getHeight()*0.1);
		paint.setColor(Color.parseColor("#321003"));
		paint.setTextSize((float)(0.037*gameView.getHeight()));
		canvas.drawText(label1, getCenteredTextX(label1,paint), y-(float)(0.030*gameView.getHeight()), paint);
		canvas.drawText(label2, getCenteredTextX(label2,paint), y+(float)(0.150*gameView.getHeight()), paint);
		canvas.drawText(label3, getCenteredTextX(label3,paint), y+(float)(0.330*gameView.getHeight()), paint);
		canvas.drawText(label4, getCenteredTextX(label4,paint), y+(float)(0.510*gameView.getHeight()), paint);
		paint.setTextSize((float)(0.03*gameView.getHeight()));
		paint.setColor(Color.parseColor("#653106"));
		StringBuilder build = new StringBuilder();
		for(int i=0; i<20; i++){
			build.delete(0, build.length());
			if(i<5){
				build.append(hiScores.survival[i].score).append("pts ").append(setDifficultyString(hiScores.survival[i].difficulty)).append("  tail:").append(hiScores.survival[i].tail).append("    level:").append(hiScores.survival[i].level);
				canvas.drawText(build.toString(), getCenteredTextX(build.toString(),paint), y+i*(float)(0.028*gameView.getHeight()), paint);
			}
			else if(i<10){
				build.append(hiScores.classic[i-5].score).append("pts ").append(setDifficultyString(hiScores.classic[i-5].difficulty)).append("  tail:").append(hiScores.classic[i-5].tail);
				canvas.drawText(build.toString(), getCenteredTextX(build.toString(),paint), y+i*(float)(0.028*gameView.getHeight())+(float)(0.038*gameView.getHeight()), paint);
			}
			else if(i<15){
				build.append(hiScores.free[i-10].score).append("pts ").append(setDifficultyString(hiScores.free[i-10].difficulty)).append("  tail:").append(hiScores.free[i-10].tail);
				canvas.drawText(build.toString(), getCenteredTextX(build.toString(),paint), y+i*(float)(0.028*gameView.getHeight())+(float)(0.078*gameView.getHeight()), paint);
			}
			else{
				String time = String.format("%02d:%02d:%02d",hiScores.certainDeath[i-15].time[0],hiScores.certainDeath[i-15].time[1],hiScores.certainDeath[i-15].time[2]);
				build.append(time).append("  ").append(setDifficultyString(hiScores.certainDeath[i-15].difficulty)).append("lost by: ").append(hiScores.certainDeath[i-15].death);
				canvas.drawText(build.toString(), getCenteredTextX(build.toString(),paint), y+i*(float)(0.028*gameView.getHeight())+(float)(0.118*gameView.getHeight()), paint);
			}
		}
	}
	
	public float getCenteredTextX(String string, Paint paint){
		Rect rect = new Rect();
		paint.getTextBounds(string, 0, string.length(), rect);
		return gameView.getWidth()/2-rect.width()/2;
	}
	
	private String setDifficultyString(int diff){
		switch(diff){
		case 0: return "    Easy    ";
		case 1: return "  Medium  ";
		case 2: return "    Hard    ";
		default : return "  Medium  ";
		}
	}
	
	private void loadScores(File scores) throws IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(scores));
		gameView.setHiscores((HiScores)in.readObject());
		gameView.getHiScores().saveable = true;
		in.close();
		hiScores = gameView.getHiScores();
	}
	
	private void saveScores(File scores) throws IOException{
		if(hiScores.saveable){
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(scores));
			os.writeObject(hiScores);
			os.close();
		}
	}
	
	public void parseScores(boolean write){
		String state = Environment.getExternalStorageState();
		if(!state.equals(Environment.MEDIA_MOUNTED)){
			Log.e("Scoreboard","parser, no External Storage detected");
			//TODO: add error window 
		}
		else{
			File externalDir = Environment.getExternalStorageDirectory();
			File scoresDir = new File(externalDir.getAbsolutePath() + File.separator + ".alethinophidia");
			if(!scoresDir.exists() || !scoresDir.isDirectory()){
				scoresDir.mkdir();
			}
			File scores = new File(scoresDir.getAbsolutePath() + File.separator + "data.ale");
			if(!scores.exists()){
				try{
					saveScores(scores);
				} catch (IOException e) {
					Log.e("Scoreboard", "parser can't create new file");
					write = false;
				}
			}
			try{
				if(write) saveScores(scores);
				else loadScores(scores);
			} catch (IOException e) {
				//hiScores.saveable = false;
				Log.e("Scoreboard","parser IOException");
			} catch (ClassNotFoundException e) {
				//hiScores.saveable = false;
				Log.e("Scoreboard","parser ClassNotFoundException");
			}
		}
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		backdrop.setGraphics(antiAlias, filtering);
		back.setGraphics(antiAlias, filtering);
		paint.setAntiAlias(antiAlias);
	}
	
	public void setHiddenStatus(boolean status){
		hidden = status;
		mainMenu.lockMenu(status);
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
