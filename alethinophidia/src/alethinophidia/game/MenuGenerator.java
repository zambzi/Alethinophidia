package alethinophidia.game;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import alethinophidia.userInterface.GameModesMenu;
import alethinophidia.userInterface.HelpMenu;
import alethinophidia.userInterface.MainMenu;
import alethinophidia.userInterface.MapChoiceMenu;
import alethinophidia.userInterface.MessageWindow;
import alethinophidia.userInterface.OptionsMenu;
import alethinophidia.userInterface.Scoreboard;

/**
 * 
 * Main control center of in game menus
 * (except for InGameInterface).
 * Creates and holds all the menu windows
 * 
 * @author £ukasz Piotrowski
 */

public class MenuGenerator {
	public static final int NEW_GAME = 1;
	public static final int OPTIONS = 2;
	public static final int BACK = 3;
	public static final int SCOREBOARD = 4;
	public static final int HELP = 5;
	public static final int ACCEPT = 6;
	public static final int START_GAME = 7;
	public static final int SCOREBOARD_SWITCH_GAME_MODES = 8;
	
	private MainMenu mainMenu;
	private GameView gameView;
	private GameModesMenu gameModesMenu;
	private OptionsMenu optionsMenu;
	private Scoreboard scoreboard;
	private MapChoiceMenu mapMenu;
	private boolean lockNextTouch;
	private MessageWindow msgWindow;
	private HelpMenu helpMenu;
	
	public MenuGenerator(GameView gameView){
		this.gameView = gameView;
		mainMenu = new MainMenu(gameView);
		optionsMenu = new OptionsMenu(gameView, mainMenu, gameView.getSettings());
		scoreboard = new Scoreboard(gameView, mainMenu, gameView.getSettings());
		gameModesMenu = new GameModesMenu(gameView, mainMenu, this);
		msgWindow = new MessageWindow(gameView, scoreboard);
		mapMenu = new MapChoiceMenu(gameView, mainMenu);
		helpMenu = new HelpMenu(gameView, mainMenu);
		lockNextTouch = false;
	}
	
	public void onDraw(Canvas canvas){
		mainMenu.onDraw(canvas);
		optionsMenu.onDraw(canvas);
		scoreboard.onDraw(canvas);
		gameModesMenu.onDraw(canvas);
		msgWindow.onDraw(canvas);
		mapMenu.onDraw(canvas);
		helpMenu.onDraw(canvas);
	}
	
	public void onTouchEvent(MotionEvent event){
		if(!areAllHidden()){
			switch(event.getAction()){
			case (MotionEvent.ACTION_DOWN) : if(lockNextTouch == false){
												if(chooseMenu(mainMenu.onTouchEvent(event)));
												else if(optionsMenu.onTouchEvent(event));
												else if(scoreboard.onTouchEvent(event));
												else if(gameModesMenu.onTouchEvent(event));
												else if(msgWindow.onTouchEvent(event));
												else if(mapMenu.onTouchEvent(event));
												else if(helpMenu.onTouchEvent(event));
												lockNextTouch = true;
												gameView.getSound().playSound(SoundGenerator.SOUND_CLICK, 0);
											}
											break;
			case (MotionEvent.ACTION_MOVE) : if(lockNextTouch == false){
												mapMenu.onTouchEvent(event);
												lockNextTouch = true;
											}
			case (MotionEvent.ACTION_UP) : mapMenu.onTouchEvent(event);lockNextTouch = false;
			case (MotionEvent.ACTION_CANCEL) : lockNextTouch = false;
			}
		}
	}
	
	public void onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(mainMenu.isHidden()){
				if(gameView.getGame() != null)
					gameView.getGame().setPause(true);
				hideAll(true);
				unhideMainMenu();
			}
			else{
				hideAll(false);
				if(gameView.getGame() != null)
					gameView.getGame().setPause(false);
			}
		}
	}
	
	public void onBackDown(){
		if(mainMenu.isHidden()){
			if(gameView.getGame() != null)
				gameView.getGame().setPause(true);
			hideAll(true);
			unhideMainMenu();
		}
		else{
			hideAll(false);
			if(gameView.getGame() != null)
				gameView.getGame().setPause(false);
		}
	}
	
	private boolean chooseMenu(int choice){
		switch(choice){
		case NEW_GAME :	mainMenu.setHiddenStatus(true);
						gameModesMenu.setHiddenStatus(false);
						return true;
		case OPTIONS : 	optionsMenu.setHiddenStatus(false);
						optionsMenu.onOpen();
						return true;
		case BACK : 	if(gameView.getGame()!=null){
							if(!gameView.getGame().isGameLost()){
								gameView.getGame().setPause(false);
								gameView.getGame().lockTouchEvent(true);
								hideAll(false);
							}
						}
						else
							hideAll(false);
						return true;
		case SCOREBOARD : 	scoreboard.setHiddenStatus(false);
							scoreboard.onOpen();
							return true;
		case HELP : 	helpMenu.setHiddenStatus(false);
						mainMenu.setHiddenStatus(true);
						return true;
		default : return false;
		}
	}
	
	public void unhideMainMenu(){
		mainMenu.setHiddenStatus(false);
		if(gameView.getGame() != null)
			gameView.getGame().setPause(true);
		//gameView.getSound().playSound(SoundGenerator.MUSIC_MAIN_THEME, -1);
	}
	
	public void hideAll(boolean keepPaused){
		mainMenu.setHiddenStatus(true);
		optionsMenu.setHiddenStatus(true);
		if(gameView.getGame() != null && !keepPaused)
			gameView.getGame().setPause(false);
		scoreboard.setHiddenStatus(true);
		gameModesMenu.setHiddenStatus(true);
		msgWindow.setHiddenStatus(true);
		mapMenu.setHiddenStatus(true);
		helpMenu.setHiddenStatus(true);
	}
	
	public void resetGraphics(){
		mainMenu.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		optionsMenu.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		scoreboard.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		gameModesMenu.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		msgWindow.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		mapMenu.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		helpMenu.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
	}
	
	public void popScoreWindow(int score, int place, int gameMode, int time[]){
		msgWindow.setHiddenStatus(false);
		msgWindow.onOpen(score, place, gameMode, time);
	}
	
	public MapChoiceMenu getMapChoiceMenu(){
		return mapMenu;
	}
	
	private boolean areAllHidden(){
		if(mainMenu.isHidden() &&
				optionsMenu.isHidden() &&
				scoreboard.isHidden() &&
				gameModesMenu.isHidden() &&
				msgWindow.isHidden() &&
				mapMenu.isHidden() &&
				helpMenu.isHidden())
			return true;
		else
			return false;
	}
}
