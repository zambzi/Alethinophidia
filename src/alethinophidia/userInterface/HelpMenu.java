package alethinophidia.userInterface;

import alethinophidia.game.GameView;
import alethinophidia.utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Just a simple help screen
 * 
 * @author £ukasz Piotrowski
 * 
 */

public class HelpMenu {
	private Field help;
	private boolean hidden;
	private GameView gameView;
	private MainMenu mainMenu;
	
	public HelpMenu(GameView gameView, MainMenu mainMenu){
		hidden = true;
		this.gameView = gameView;
		this.mainMenu = mainMenu;
		setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/help/help1.png");
		help = new Field(new Vector2(0,0), 1.0f, 1.0f,bmp, gameView);
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		help.setGraphics(antiAlias, filtering);
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden){
				if(help.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					mainMenu.setHiddenStatus(false);
					return true;
				}
			}
		}
		return false;
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			help.onDraw(canvas);
		}
	}
	
	public void setHiddenStatus(boolean status){
		hidden = status;
		mainMenu.lockMenu(status);
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
