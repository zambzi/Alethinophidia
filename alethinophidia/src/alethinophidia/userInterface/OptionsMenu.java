package alethinophidia.userInterface;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import alethinophidia.game.GameView;
import alethinophidia.utils.Vector2;
import alethinophidia.utils.GameSettings;

/**
 * 
 * Controls options menu in game.
 * All settings are set via 
 * GameSettings instance created in
 * GameView.
 * 
 * @author £ukasz Piotrowski
 */

public class OptionsMenu {
	private GameView gameView;
	private MainMenu mainMenu;
	private boolean hidden;
	private Field background;
	private Field captions;
	private Field back;
	private Field apply;
	private Field switchAntiAlias;
	private Field switchTextureFilter;
	private Field switchSmallTextures;
	private Field switchSound;
	private Field switchMusic;
	private Field switchDifficulty;
	
	private GameSettings settings;
	
	private int difficulty;
	
	public OptionsMenu(GameView gameView, MainMenu mainMenu, GameSettings settings){
		this.gameView = gameView;
		this.mainMenu = mainMenu;
		this.settings = settings;
		hidden = true;		
		setBitmaps();
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/interface/bigwindow.png");
		background = new Field(new Vector2(0.055,0.1), 0.9f, 0.8f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_back.png");
		back = new Field(new Vector2(0.15,0.77), 0.3f, 0.075f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/small_apply.png");
		apply = new Field(new Vector2(0.55,0.77), 0.3f, 0.075f, bmp, gameView);
		bmp = gameView.loadBitmap("/interface/options_captions.png");
		captions = new Field(new Vector2(0.1,0.175), 0.5f, 0.5f, bmp, gameView);
		
		bmp = gameView.loadBitmap("/interface/on_off.png");
		switchAntiAlias = new Field(new Vector2(0.65,0.170), 0.2f, 0.07f, bmp, gameView);
		switchAntiAlias.setAnimationRowsAndColumns(2, 1);
		
		switchTextureFilter = new Field(new Vector2(0.65,0.268), 0.2f, 0.07f, bmp, gameView);
		switchTextureFilter.setAnimationRowsAndColumns(2, 1);
		
		switchSmallTextures = new Field(new Vector2(0.65,0.366), 0.2f, 0.07f, bmp, gameView);
		switchSmallTextures.setAnimationRowsAndColumns(2, 1);
		
		switchSound = new Field(new Vector2(0.65,0.464), 0.2f, 0.07f, bmp, gameView);
		switchSound.setAnimationRowsAndColumns(2, 1);
		
		switchMusic = new Field(new Vector2(0.65,0.562), 0.2f, 0.07f, bmp, gameView);
		switchMusic.setAnimationRowsAndColumns(2, 1);
		
		bmp = gameView.loadBitmap("/interface/difficulty.png");
		switchDifficulty = new Field(new Vector2(0.55,0.660), 0.3f, 0.07f, bmp, gameView);
		switchDifficulty.setAnimationRowsAndColumns(3, 1);
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!hidden){
				if(back.isPointOnField(event.getX(), event.getY())){
					setHiddenStatus(true);
					return true;
				}
				setSwitch(switchAntiAlias, event);
				setSwitch(switchTextureFilter, event);
				setSwitch(switchSmallTextures, event);
				setSwitch(switchSound, event);
				setSwitch(switchMusic, event);
				setDifficulty(event);
				if(apply.isPointOnField(event.getX(), event.getY())){
					saveOptionsValues();
					setHiddenStatus(true);
					gameView.getMusic().setMute(!settings.music);
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void onOpen(){
		readOptionsValues();
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			background.onDraw(canvas);
			back.onDraw(canvas);
			apply.onDraw(canvas);
			captions.onDraw(canvas);
			switchAntiAlias.onDraw(canvas);
			switchTextureFilter.onDraw(canvas);
			switchSmallTextures.onDraw(canvas);
			switchSound.onDraw(canvas);
			switchMusic.onDraw(canvas);
			switchDifficulty.onDraw(canvas);
		}
	}
	
	public void setHiddenStatus(boolean status){
		hidden = status;
		mainMenu.lockMenu(status);
	}
	
	
	private void readOptionsValues(){
		switchAntiAlias.setTriggeredStatus(settings.antiAlias);
		switchAntiAlias.setFrame((settings.antiAlias) ? 0 : 1);
		
		switchTextureFilter.setTriggeredStatus(settings.filtering);
		switchTextureFilter.setFrame((settings.filtering) ? 0 : 1);
		
		switchSmallTextures.setTriggeredStatus(settings.smallTextures);
		switchSmallTextures.setFrame((settings.smallTextures) ? 0 : 1);
		
		switchSound.setTriggeredStatus(settings.sound);
		switchSound.setFrame((settings.sound) ? 0 : 1);
	
		switchMusic.setTriggeredStatus(settings.music);
		switchMusic.setFrame((settings.music) ? 0 : 1);
		
		switchDifficulty.setFrame(settings.difficulty);
		difficulty = settings.difficulty;
	}
	
	private void saveOptionsValues(){
		settings.antiAlias = switchAntiAlias.isTriggered();
		settings.filtering = switchTextureFilter.isTriggered();
		settings.smallTextures = switchSmallTextures.isTriggered();
		settings.sound = switchSound.isTriggered();
		settings.music = switchMusic.isTriggered();
		settings.difficulty = difficulty;
		settings.parseSettings(true);
		gameView.resetGraphics();
	}
	
	private void setSwitch(Field field, MotionEvent event){
		if(field.isPointOnField(event.getX(), event.getY())){
			if(field.getFrame() == 0){
				field.setFrame(1);
			}
			else if(field.getFrame() == 1){
				field.setFrame(0);
			}
		}
	}
	
	private void setDifficulty(MotionEvent event){
		if(switchDifficulty.isPointOnField(event.getX(), event.getY())){
			if(switchDifficulty.getFrame() == 0){
				switchDifficulty.setFrame(1);
				difficulty = 1;
			}
			else if(switchDifficulty.getFrame() == 1){
				switchDifficulty.setFrame(2);
				difficulty = 2;
			}
			else if(switchDifficulty.getFrame() == 2){
				switchDifficulty.setFrame(0);
				difficulty = 0;
			}
		}
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		background.setGraphics(antiAlias, filtering);
		captions.setGraphics(antiAlias, filtering);
		back.setGraphics(antiAlias, filtering);
		apply.setGraphics(antiAlias, filtering);
		switchAntiAlias.setGraphics(antiAlias, filtering);
		switchTextureFilter.setGraphics(antiAlias, filtering);
		switchSmallTextures.setGraphics(antiAlias, filtering);
		switchSound.setGraphics(antiAlias, filtering);
		switchMusic.setGraphics(antiAlias, filtering);
		switchDifficulty.setGraphics(antiAlias, filtering);
	}
	
	public boolean isHidden(){
		return hidden;
	}

}
