package alethinophidia.userInterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import alethinophidia.game.GamePlayGenerator;
import alethinophidia.game.GameView;
import alethinophidia.game.MenuGenerator;
import alethinophidia.utils.Vector2;

/**
 * GameModesMenu
 * class that controls game modes menu
 * that pops up after choosing new game
 * from main menu
 * 
 * @author £ukasz Piotrowski
 */

public class GameModesMenu {
	private Field survival;
	private Field classic;
	private Field certainDeath;
	private Field free;
	private Field backdrop;
	private Field back;
	private Rect bgColor;
	private Paint paintBg;
	
	private boolean hidden;
	
	private GameView gameView;
	private MainMenu mainMenu;
	private MenuGenerator menuGenerator;
	
	public GameModesMenu(GameView gameView, MainMenu mainMenu, MenuGenerator menuGenerator){
		hidden = true;
		this.mainMenu = mainMenu;
		this.gameView = gameView;
		this.menuGenerator = menuGenerator;
		paintBg = new Paint();
		paintBg.setARGB(150, 0, 0, 0);
		
		survival = new Field(new Vector2(0.5,0.2), 0.4f, 0.25f, null, gameView);
		classic = new Field(new Vector2(0.1,0.2), 0.4f, 0.25f, null, gameView);
		certainDeath = new Field(new Vector2(0.1,0.5), 0.4f, 0.25f, null, gameView);
		free = new Field(new Vector2(0.5,0.5), 0.4f, 0.25f, null, gameView);
		setBitmaps();
	}
	
	private void setBitmaps(){
		bgColor = new Rect(0,0,gameView.getWidth(), gameView.getHeight());
		Bitmap bmp = gameView.loadBitmap("/interface/gamemode.png");
		backdrop = new Field(new Vector2(0.005,0.1), 0.998f, 0.7f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_back.png");
		back = new Field(new Vector2(0.35,0.899), 0.3f, 0.075f, bmp, gameView);
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		backdrop.setGraphics(antiAlias, filtering);
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			canvas.drawRect(bgColor, paintBg);
			backdrop.onDraw(canvas);
			back.onDraw(canvas);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden){
				if(back.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					mainMenu.setHiddenStatus(false);
					return true;
				}
				else if(survival.isPointOnField(event.getX(), event.getY())){
					menuGenerator.hideAll(true);
					menuGenerator.getMapChoiceMenu().setHiddenStatus(false);
					mainMenu.lockMenu(true);
					menuGenerator.getMapChoiceMenu().onOpen(GamePlayGenerator.MODE_SURVIVAL);
					return true;
				}
				else if(classic.isPointOnField(event.getX(), event.getY())){
					menuGenerator.hideAll(true);
					menuGenerator.getMapChoiceMenu().setHiddenStatus(false);
					mainMenu.lockMenu(true);
					menuGenerator.getMapChoiceMenu().onOpen(GamePlayGenerator.MODE_CLASSIC);
					return true;
				}
				else if(certainDeath.isPointOnField(event.getX(), event.getY())){
					menuGenerator.hideAll(true);
					menuGenerator.getMapChoiceMenu().setHiddenStatus(false);
					mainMenu.lockMenu(true);
					menuGenerator.getMapChoiceMenu().onOpen(GamePlayGenerator.MODE_CERTAIN_DEATH);
					return true;
				}
				else if(free.isPointOnField(event.getX(), event.getY())){
					menuGenerator.hideAll(true);
					menuGenerator.getMapChoiceMenu().setHiddenStatus(false);
					mainMenu.lockMenu(true);
					menuGenerator.getMapChoiceMenu().onOpen(GamePlayGenerator.MODE_FREE);
					return true;
				}
			}
		}
		return false;
	}
	
	public void setHiddenStatus(boolean status){
		hidden = status;
		mainMenu.lockMenu(status);
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
