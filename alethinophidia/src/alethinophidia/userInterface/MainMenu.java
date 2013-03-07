package alethinophidia.userInterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import alethinophidia.game.GameView;
import alethinophidia.game.MenuGenerator;
import alethinophidia.utils.Vector2;

/**
 *
 * Class that controls all Main Menu
 * buttons
 * 
 * @author £ukasz Piotrowski
 */

public class MainMenu {	
	private GameView gameView;
	private boolean hidden;
	private Field scoreBoard;
	private Field newGame;
	private Field options;
	private Field back;
	private Field help;
	private Rect bgColor;
	private Paint paint;
	private boolean clickable;
	
	public MainMenu(GameView gameView){
		this.gameView = gameView;
		hidden = true;
		clickable = true;
		paint = new Paint();
		paint.setARGB(150, 0, 0, 0);
		setBitmaps();
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/interface/back.png");
		back = new Field(new Vector2(0.2,0.45), 0.6f, 0.095f, bmp, gameView);
		
		bmp = gameView.loadBitmap("/interface/newgame.png");
		newGame = new Field(new Vector2(0.2,0.55), 0.6f, 0.095f, bmp, gameView);
		
		bmp = gameView.loadBitmap("/interface/options.png");
		options = new Field(new Vector2(0.2,0.65), 0.6f, 0.095f, bmp, gameView);
		
		bmp = gameView.loadBitmap("/interface/scoreboard.png");
		scoreBoard = new Field(new Vector2(0.2,0.75), 0.6f, 0.095f, bmp, gameView);
		
		bmp = gameView.loadBitmap("/interface/help.png");
		help = new Field(new Vector2(0.2,0.85), 0.6f, 0.095f, bmp, gameView);
		
		bgColor = new Rect(0,0,gameView.getWidth(), gameView.getHeight());
	}
	
	public int onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden && clickable){
				if(newGame.isPointOnField(event.getX(), event.getY())){
					return MenuGenerator.NEW_GAME;
				}
				else if(options.isPointOnField(event.getX(), event.getY())){
					return MenuGenerator.OPTIONS;
				}
				else if(back.isPointOnField(event.getX(), event.getY())){
					if(gameView.getGame()!=null){
						if(gameView.getGame().isGameLost()!=true)
							return MenuGenerator.BACK;
					}
				}
				else if(scoreBoard.isPointOnField(event.getX(), event.getY())){
					return MenuGenerator.SCOREBOARD;
				}
				else if(help.isPointOnField(event.getX(), event.getY())){
					return MenuGenerator.HELP;
				}
			}
		}
		return 0;
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			if(gameView.getGame()!=null){
				canvas.drawRect(bgColor, paint);
			}
			newGame.onDraw(canvas);
			options.onDraw(canvas);
			scoreBoard.onDraw(canvas);
			help.onDraw(canvas);
			if(gameView.getGame()!=null){
				if(gameView.getGame().isGameLost()!=true)
					back.onDraw(canvas);
			}
				
		}
	}
	

	
	public void update(){
	}
	
	
	public void setHiddenStatus(boolean status){
		hidden = status;
	}
	
	public void lockMenu(boolean status){
		clickable = status;
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		scoreBoard.setGraphics(antiAlias, filtering);
		newGame.setGraphics(antiAlias, filtering);
		options.setGraphics(antiAlias, filtering);
		back.setGraphics(antiAlias, filtering);
		help.setGraphics(antiAlias, filtering);
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
