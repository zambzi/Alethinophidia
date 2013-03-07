package alethinophidia.userInterface;


import java.io.IOException;
import java.util.Vector;

import alethinophidia.game.GameView;
import alethinophidia.map.MapThumb;
import alethinophidia.utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 * The most complex of menus. this
 * class takes care of map menu that
 * pops up after choosing game mode
 * 
 * Map choice menu uses list of
 * MapThumb classes to draw mini
 * maps on the screen
 * 
 * @author £ukasz Piotrowski
 */

public class MapChoiceMenu {
	private Vector<MapThumb> maps;
	private String[] mapNames;
	private Field back;
	private Field theme1;
	private Field theme2;
	private Field theme3;
	private Paint paint;
	private Paint paintBg;
	private Paint paintPanel;
	private Paint textPaint;
	private Paint selectedThemePaint;
	private boolean hidden;
	private boolean readyToShowMaps;
	private GameView gameView;
	private MainMenu mainMenu;
	private boolean scroll;
	private boolean canClickMap;
	private boolean loading;
	private Field scrollField;
	private float initialPos;
	private int gameMode;
	private Rect bgColor;
	private Rect upperPanel;
	private Rect lowerPanel;
	private Rect selectedTheme;
	private boolean lockFirstActionUp;
	private String theme;
	
	public MapChoiceMenu(GameView gameView, MainMenu mainMenu){
		this.gameView = gameView;
		this.mainMenu = mainMenu;
		readyToShowMaps = false;
		loading = false;
		scroll = false;
		canClickMap = true;
		hidden = true;
		paintBg = new Paint();
		paint = new Paint();
		paintPanel = new Paint();
		paintBg.setARGB(150, 0, 0, 0);
		paintPanel.setARGB(200, 0, 0, 0);
		selectedThemePaint = new Paint();
		selectedThemePaint.setColor(Color.WHITE);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize((float)(0.04*gameView.getHeight()));
		lockFirstActionUp = true;
		theme = "brick";
		textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
		setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		setThemeSelection();
		scrollField = new Field(new Vector2(0.05,0),1.0f,0.75f,null,gameView);
	}
	
	public void onOpen(int gameMode){
		loading = true;
		readMaps();
		loadMaps();
		loading = false;
		lockFirstActionUp = true;
		readyToShowMaps = true;
		this.gameMode = gameMode;
		if(gameView.getGame() != null)
			gameView.getGame().setPause(true);
	}
	
	public void onDraw(Canvas canvas){
		if(!hidden){
			canvas.drawRect(bgColor, paintBg);
			if(readyToShowMaps){
				for(MapThumb thumb : maps){
					thumb.onDraw(canvas);
				}
			}
			canvas.drawRect(upperPanel, paintPanel);
			canvas.drawRect(lowerPanel, paintPanel);
			canvas.drawText("Choose a map:", (float)(0.05*gameView.getWidth()), (float)(0.04*gameView.getHeight()), textPaint);
			canvas.drawText("Choose a theme:", (float)(0.05*gameView.getWidth()), (float)(0.85*gameView.getHeight()), textPaint);
			canvas.drawRect(selectedTheme, selectedThemePaint);
			theme1.onDraw(canvas);
			theme2.onDraw(canvas);
			theme3.onDraw(canvas);
			back.onDraw(canvas);
			if(loading)
				canvas.drawText("LOADING...", getCenteredTextX("LOADING...",textPaint), (float)(0.5*gameView.getHeight()), textPaint);
		}
	}
	
	public float getCenteredTextX(String string, Paint paint){
		Rect rect = new Rect();
		paint.getTextBounds(string, 0, string.length(), rect);
		return gameView.getWidth()/2-rect.width()/2;
	}
	
	public void update(){
		
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(!hidden){
			if(scrollField.isPointOnField(event.getX(), event.getY())){
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:	canClickMap = false;
												if(scroll && readyToShowMaps){
													for(MapThumb thumb : maps){
														thumb.move(event.getY(), initialPos);
													}
													initialPos = event.getY();
												}
												break;
				case MotionEvent.ACTION_DOWN:	lockFirstActionUp = false;
												scroll = true;
												canClickMap = true;
												initialPos = event.getY();
												break;
				case MotionEvent.ACTION_UP: scroll = false;
											if(canClickMap && !lockFirstActionUp)clickMap(event);
											canClickMap = true; break;
				case MotionEvent.ACTION_CANCEL: scroll = false; canClickMap = true; break;
				default : scroll = false; break;
				}
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN && back.isPointOnField(event.getX(), event.getY())){
				closeMapMenu(true);
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN && theme1.isPointOnField(event.getX(), event.getY())){
				theme = "brick";
				setThemeSelection();
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN && theme2.isPointOnField(event.getX(), event.getY())){
				theme = "sea";
				setThemeSelection();
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN && theme3.isPointOnField(event.getX(), event.getY())){
				theme = "gooey";
				setThemeSelection();
			}
		}
		return false;
	}
	
	private void clickMap(MotionEvent event){
		for(MapThumb thumb : maps){
			if(thumb.onTouchEvent(event)){
				loading=true;
				closeMapMenu(false);
				gameView.menuController(1, gameMode, thumb.getMapName(), theme);
				loading = false;
			}
		}
	}
	
	private void closeMapMenu(boolean openMenu){
		gameView.pauseGameLoop(true);
		gameView.getMenuGenerator().hideAll(false);
		if(openMenu)mainMenu.setHiddenStatus(false);
		readyToShowMaps = false;
		maps.clear();
		gameView.pauseGameLoop(false);
	}
	
	private void loadMaps(){
		maps = new Vector<MapThumb>();
		int i=0;
		int rows = (int)Math.ceil(mapNames.length*0.3);
		for(int y=0; y<rows; y++){
			for(int x=0; x<3; x++){
				if(i>=mapNames.length)
					break;
				maps.add(new MapThumb(gameView,gameView.getContext(),new Vector2((((double)x/10)*3)+0.08,(((double)y/10)*3)+0.1), mapNames[i]));
				i++;
			}
		}
	}
	
	private void readMaps(){
		try{
			mapNames = gameView.getContext().getAssets().list("Maps");
		} catch (IOException e) {
			Log.e("MapChoiceMenu", "IOException in readMaps()");
		}
	}
	
	private void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/interface/small_back.png");
		back = new Field(new Vector2(0.1,0.90), 0.3f, 0.075f, bmp, gameView);
		bgColor = new Rect(0,0,gameView.getWidth(), gameView.getHeight());
		upperPanel = new Rect(0,0,gameView.getWidth(), (int)(0.05*gameView.getHeight()));
		lowerPanel = new Rect(0,(int)(0.8*gameView.getHeight()),gameView.getWidth(), gameView.getHeight());
		bmp = gameView.loadBitmap("/themes/brickwall.png");
		theme1 = new Field(new Vector2(0.57, 0.89), 0.1f,bmp, gameView);
		bmp = gameView.loadBitmap("/themes/seawall.png");
		theme2 = new Field(new Vector2(0.69, 0.89), 0.1f,bmp, gameView);
		bmp = gameView.loadBitmap("/themes/gooeywall.png");
		theme3 = new Field(new Vector2(0.81, 0.89), 0.1f,bmp, gameView);
	}
	
	private void setThemeSelection(){
		if(theme == "brick"){
			selectedTheme = new Rect((int)(theme1.getPosition().getX()-5), (int)(theme1.getPosition().getY()-5),
					(int)(theme1.getPosition().getX()+theme1.getWidth()+5), (int)(theme1.getPosition().getY()+theme1.getHeight()+5));
		}
		else if(theme == "sea"){
			selectedTheme = new Rect((int)(theme2.getPosition().getX()-5), (int)(theme2.getPosition().getY()-5),
					(int)(theme2.getPosition().getX()+theme2.getWidth()+5), (int)(theme2.getPosition().getY()+theme2.getHeight()+5));
		}
		else if(theme == "gooey"){
			selectedTheme = new Rect((int)(theme3.getPosition().getX()-5), (int)(theme3.getPosition().getY()-5),
					(int)(theme3.getPosition().getX()+theme3.getWidth()+5), (int)(theme3.getPosition().getY()+theme3.getHeight()+5));
		}
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		back.setGraphics(antiAlias, filtering);
		theme1.setGraphics(antiAlias, filtering);
		theme2.setGraphics(antiAlias, filtering);
		theme3.setGraphics(antiAlias, filtering);
		paint.setAntiAlias(antiAlias);
		textPaint.setAntiAlias(antiAlias);
	}
	
	public void setHiddenStatus(boolean status){
		hidden = status;
	}
	
	public boolean isHidden(){
		return hidden;
	}
}
