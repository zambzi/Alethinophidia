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
import alethinophidia.map.MapGrid;
import alethinophidia.pawns.SnakeCareCenter;
import alethinophidia.utils.Vector2;

/**
 * 
 * Class that controls in-game interface panel,
 * score panel and healthbar
 * 
 * @author £ukasz Piotrowski
 */

public class InGameInterface {
	private Field healthBar;
	private Field scorePanel;
	private Field menuButton;
	private Field panelBackdrop;
	
	private boolean startGreyOut;
	private SnakeCareCenter snake;
	private GameView gameView;
	private Paint paint;
	private Paint greyOutPaint;
	private Paint survivalCountdownPaint;
	private boolean showTime;
	private boolean showCounter;
	private MapGrid grid;
	private Field help;
	private int gameMode;
	
	
	public InGameInterface(SnakeCareCenter snake, MapGrid grid, GameView gameView, int gameMode){
		this.gameView = gameView;
		this.snake = snake;
		startGreyOut = true;
		this.gameMode = gameMode;
		this.grid = grid;
		paint = new Paint();
		greyOutPaint = new Paint();
		greyOutPaint.setARGB(150, 0, 0, 0);
		survivalCountdownPaint = new Paint();
		survivalCountdownPaint.setTextSize((float)(grid.getSquareHeight()*1.5));
		survivalCountdownPaint.setARGB(150, 255, 255, 255);
		survivalCountdownPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		survivalCountdownPaint.setAntiAlias(gameView.getSettings().antiAlias);
		survivalCountdownPaint.setShadowLayer(2, 2, 2, Color.argb(150, 0, 0, 0));
		paint.setTextSize(grid.getSquareHeight()*3/4);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		paint.setAntiAlias(gameView.getSettings().antiAlias);		
		setBitmaps();
		if(gameMode==GamePlayGenerator.MODE_CERTAIN_DEATH)
			showTime = true;
		else
			showTime = false;
		if(gameMode==GamePlayGenerator.MODE_SURVIVAL)
			showCounter = true;
		else
			showCounter = false;

	}
	
	private void setBitmaps(){
		//HEALTH BAR FIELD
		Bitmap bmp = gameView.loadBitmap("/interface/healthbar.png");
		healthBar = new Field(new Vector2(0.01,0.9), 0.5f, 0.05f,bmp,gameView);
		healthBar.setAnimationRowsAndColumns(11, 1);
		//SCORE PANEL FIELD
		bmp = gameView.loadBitmap("/interface/scorepanel.png");
		scorePanel = new Field(new Vector2(0.05, 0.95), 0.5f, 0.05f,bmp,gameView);
		//MENU BUTTON FIELD
		bmp = gameView.loadBitmap("/interface/menubutton.png");
		menuButton = new Field(new Vector2(0.725, 0.9), 0.25f, 0.1f,bmp,gameView);
		//BACKDROP FIELD
		bmp = gameView.loadBitmap("/interface/panelbg.png");
		panelBackdrop = new Field(new Vector2(0,0.899), 1f, 0.12f, bmp, gameView);
		
		if(gameMode == GamePlayGenerator.MODE_CLASSIC){
			bmp = gameView.loadBitmap("/help/help4.png");
		}
		else if(gameMode == GamePlayGenerator.MODE_FREE){
			bmp = gameView.loadBitmap("/help/help2.png");
		}
		else if(gameMode == GamePlayGenerator.MODE_CERTAIN_DEATH){
			bmp = gameView.loadBitmap("/help/help3.png");
		}
		else if(gameMode == GamePlayGenerator.MODE_SURVIVAL){
			bmp = gameView.loadBitmap("/help/help5.png");
		}
		help = new Field(new Vector2(0,0), 1.0f, 1.0f, bmp, gameView);
		
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(menuButton.isPointOnField(event.getX(), event.getY())){
			gameView.getMenuGenerator().unhideMainMenu();
			return true;
		}
		return false;
	}
	
	public boolean onStartTouchEvent(MotionEvent event){
		if(startGreyOut && event.getAction()==MotionEvent.ACTION_DOWN){
			gameView.getGame().setPause(false);
			startGreyOut = false;
			return true;
		}
		return false;
	}
	
	public void onDraw(Canvas canvas){
		panelBackdrop.onDraw(canvas);
		healthBar.onDraw(canvas);
		scorePanel.onDraw(canvas);
		if(showTime){
			String time = String.format("%02d:%02d:%02d",snake.getTime()[0],snake.getTime()[1],snake.getTime()[2] );
			canvas.drawText(("TIME:"+time), (float)(scorePanel.getPosition().getX()+(scorePanel.getWidth()/18)), (float)(scorePanel.getPosition().getY()+(scorePanel.getHeight()*3/4)), paint);
		}
		else{
			canvas.drawText("SCORE:"+snake.getScore(), (float)(scorePanel.getPosition().getX()+(scorePanel.getWidth()/18)), (float)(scorePanel.getPosition().getY()+(scorePanel.getHeight()*3/4)), paint);
		}
		if(showCounter){
			if(snake.getTime()[1]>0 && snake.getTime()[2]>30){
				survivalCountdownPaint.setARGB(190, 255, 0, 0);
			}
			String time = String.format("%02d:%02d",1-snake.getTime()[1],59-snake.getTime()[2]);
			Rect bounds = new Rect();
			survivalCountdownPaint.getTextBounds(("TIME LEFT:"+time),0, ("TIME LEFT:"+time).length(), bounds);
			canvas.drawText(("TIME LEFT:"+time), (float)(gameView.getWidth()-bounds.width()-10), (float)0.885*gameView.getHeight(), survivalCountdownPaint);
		}
		menuButton.onDraw(canvas);
		setHealthBar();
		if(startGreyOut){
			help.onDraw(canvas);
			if(gameMode == GamePlayGenerator.MODE_SURVIVAL){
				paint.setTextSize((int)(grid.getSquareHeight()*1.5));
				paint.setColor(Color.WHITE);
				canvas.drawText("You are now at level "+gameView.getGame().getLevel(), (float)0.1*gameView.getWidth(), (float)0.7*gameView.getHeight(),paint);
				paint.setColor(Color.BLACK);
				paint.setTextSize(grid.getSquareHeight()*3/4);
			}
		}
	}
	
	public void setHealthBar(){
		healthBar.setFrame(10-snake.getHealth());
	}	
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		healthBar.setGraphics(antiAlias, filtering);
		scorePanel.setGraphics(antiAlias, filtering);
		menuButton.setGraphics(antiAlias, filtering);
		panelBackdrop.setGraphics(antiAlias, filtering);
		survivalCountdownPaint.setAntiAlias(gameView.getSettings().antiAlias);
		paint.setAntiAlias(gameView.getSettings().antiAlias);
	}
	
	public void setGreyOut(boolean set){
		startGreyOut = set;
	}
}
