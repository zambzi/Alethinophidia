package alethinophidia.userInterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import alethinophidia.game.GamePlayGenerator;
import alethinophidia.game.GameView;
import alethinophidia.utils.Vector2;

/**
 * 
 * Class controls the popup that
 * shows after the game is over
 * 
 * @author £ukasz Piotrowski
 */

public class MessageWindow {
	private Field back;
	private Field retry;
	private Field backdrop;
	private boolean hidden;
	private GameView gameView;
	private Paint paint;
	private Paint rectPaint;
	private Rect bgColor;
	private Scoreboard scoreboard;
	
	private String[] message;
	
	private final String BIG1 = "Well done";
	private final String BIG2 = "Nice job";
	private final String BIG3 = "Fair enough";
	private final String BIG4 = "So-so";
	private final String BIG5 = "Barely";
	private final String BIG6 = "Meh...";
	
	private final String SMALL1 = "Awesome results. First place!";
	private final String SMALL2 = "You are placed among the best!";
	private final String SMALL3 = "Could have gone better";
	private final String SMALL4 = "Eat food, not bricks...";
	
	private final String INFO1 = "Your score:";
	private final String INFO2 = "Time survived:";
	
	private final String POS1 = "1st: ";
	private final String POS2 = "2nd: ";
	private final String POS3 = "3rd: ";
	private final String POS4 = "4th: ";
	private final String POS5 = "5th: ";
	
	public MessageWindow(GameView gameView, Scoreboard scoreboard){
		this.gameView = gameView;
		this.scoreboard = scoreboard;
		hidden = true;
		paint = new Paint();
		rectPaint = new Paint();
		rectPaint.setARGB(150, 0, 0, 0);
		setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		message = new String[4];
		paint.setColor(Color.parseColor("#e3d000"));
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/interface/small_window.png");
		backdrop = new Field(new Vector2(0.09,0.2), 0.85f, 0.5f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_back.png");
		back = new Field(new Vector2(0.18,0.55), 0.3f, 0.075f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_retry.png");
		retry = new Field(new Vector2(0.52,0.55), 0.3f, 0.075f, bmp, gameView);
		bgColor = new Rect(0,0,gameView.getWidth(), gameView.getHeight());
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		backdrop.setGraphics(antiAlias, filtering);
		back.setGraphics(antiAlias, filtering);
		paint.setAntiAlias(antiAlias);
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			canvas.drawRect(bgColor, rectPaint);
			backdrop.onDraw(canvas);
			back.onDraw(canvas);
			retry.onDraw(canvas);
			paint.setTextSize((float)(0.045*gameView.getHeight()));
			canvas.drawText(message[0], getCenteredTextX(message[0],paint), (float)backdrop.getPosition().getY()+(float)(0.1*gameView.getHeight()), paint);
			paint.setTextSize((float)(0.025*gameView.getHeight()));
			canvas.drawText(message[1], getCenteredTextX(message[1],paint), (float)backdrop.getPosition().getY()+(float)(0.15*gameView.getHeight()), paint);
			paint.setTextSize((float)(0.03*gameView.getHeight()));
			canvas.drawText(message[2], (float)backdrop.getPosition().getX()+(float)(0.1*gameView.getWidth()), (float)backdrop.getPosition().getY()+(float)(0.2*gameView.getHeight()), paint);
			paint.setTextSize((float)(0.06*gameView.getHeight()));
			canvas.drawText(message[3], getCenteredTextX(message[3],paint), (float)backdrop.getPosition().getY()+(float)(0.26*gameView.getHeight()), paint);
		}
	}
	
	public float getCenteredTextX(String string, Paint paint){
		Rect rect = new Rect();
		paint.getTextBounds(string, 0, string.length(), rect);
		return gameView.getWidth()/2-rect.width()/2;
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden){
				if(back.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					gameView.getMenuGenerator().unhideMainMenu();
					return true;
				}
				if(retry.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					gameView.getGame().restartGame();
					return true;
				}
			}
		}
		return false;
	}
	
	public void onOpen(int score, int place, int gameMode, int time[]){
		switch(place){
		case 0 :	message[0]=BIG1;
					message[1]=POS1+SMALL1;
					break;
		case 1 : 	message[0]=BIG2;
					message[1]=POS2+SMALL2;
					break;
		case 2 : 	message[0]=BIG3;
					message[1]=POS3+SMALL2;
					break;
		case 3 : 	message[0]=BIG4;
					message[1]=POS4+SMALL2;
					break;
		case 4 : 	message[0]=BIG5;
					message[1]=POS5+SMALL3;
					break;
		case -1 : 	message[0]=BIG6;
					message[1]=SMALL4;
					break;
		}
		if(gameMode==GamePlayGenerator.MODE_CERTAIN_DEATH){
			message[2]=INFO2;
			String timeStr = String.format("%02d:%02d:%02d",time[0],time[1],time[2] );
			message[3]= timeStr;
		}
		else{
			message[2]=INFO1;
			message[3]= Integer.toString(score);
		}
		
		scoreboard.parseScores(true);
	}
	
	
	public void setHiddenStatus(boolean status){
		hidden = status;
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
