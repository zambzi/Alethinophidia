package alethinophidia.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import alethinophidia.userInterface.Field;
import alethinophidia.utils.Vector2;

/**
 * 
 * class solely created for the purpose of
 * including intro sequence at the start
 * of the app.
 * 
 * its completely independent from the
 * GamePlayGenerator and is nullified
 * once first GamePlayGenerator instance
 * is created (when player starts a new game)
 * 
 * @author £ukasz Piotrowski
 */

public class IntroGenerator {
	private Field creatorsLogo;
	private Field title;
	private Field description1;
	private Field description2;
	private Field tapInfo;
	private Field background;
	private Field snake;
	private Field sun;
	private boolean skipIntro;
	private boolean introSkipped;
	
	private GameView gameView;
	private MenuGenerator menuGenerator;
	
	public IntroGenerator(GameView gameView, MenuGenerator menuGenerator){
		this.gameView = gameView;
		this.menuGenerator = menuGenerator;
		skipIntro = false;
		introSkipped = false;
		setBitmaps();
		setGraphics();
		creatorsLogo.setAlpha(0);
		title.setAlpha(0);
		description1.setAlpha(0);
		description2.setAlpha(0);
		background.setAlpha(0);
		snake.setAlpha(0);
		sun.setAlpha(0);
	}
	
	public void setBitmaps(){
		Bitmap bmp = gameView.loadBitmap("/intro/movablemanifold.png");
		creatorsLogo = new Field(new Vector2(0.1, 0.3), 0.8f, 0.25f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/title.png");
		title = new Field(new Vector2(0.1, 0.2), 0.5f, 0.072f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/description1.png");
		description1 = new Field(new Vector2(0.1, 0.4), 0.8f, 0.089f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/description2.png");
		description2 = new Field(new Vector2(0.15, 0.6), 0.7f, 0.042f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/tap.png");
		tapInfo = new Field(new Vector2(0.15, 0.9), 0.7f, 0.070f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/introbg.png");
		background = new Field(new Vector2(0, 0), 1.0f, 1.0f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/snake_rot.png");
		snake = new Field(new Vector2(0.3, 0.15), 0.4f, bmp, gameView);
		bmp = gameView.loadBitmap("/intro/sun.png");
		sun = new Field(new Vector2(0, 0.05), 1.0f, 0.45f, bmp, gameView);
	}
	protected void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		if(background.getTransitions().isFadeFinished()){
			skipIntro=true;
		}
		if(skipIntro && !introSkipped){
			menuGenerator.unhideMainMenu();
			introSkipped = true;
		}
		if(!skipIntro){
			creatorsLogo.getTransitions().fadeInThenOut(0, 25, 50, 0, 255, true, true);
			title.getTransitions().fadeInThenOut(100, 200, 100, 0, 255, true, true);
			description1.getTransitions().fadeInThenOut(150, 200, 100, 0, 255, true, true);
			description2.getTransitions().fadeInThenOut(200, 200, 100, 0, 255, true, true);
			tapInfo.getTransitions().blink(0, 20, 0, 50, 155);
			background.getTransitions().fadeInThenOut(500, 50 , 0, 0, 255, true, false);
			snake.getTransitions().fadeInThenOut(500, 50 , 0, 0, 255, true, false);
			sun.getTransitions().fadeInThenOut(450, 100 , 0, 0, 255, true, false);
			creatorsLogo.onDraw(canvas);
			title.onDraw(canvas);
			description1.onDraw(canvas);
			description2.onDraw(canvas);
			tapInfo.onDraw(canvas);
		}
		background.onDraw(canvas);
		snake.onDraw(canvas);
		sun.onDraw(canvas);
		snake.addOrientation(-3);
	}
	
	public void onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
				if(skipIntro && !introSkipped){
					menuGenerator.unhideMainMenu();
				}
				else{
					creatorsLogo.setAlpha(0);
					title.setAlpha(0);
					description1.setAlpha(0);
					description2.setAlpha(0);
					background.setAlpha(255);
					snake.setAlpha(255);
					sun.setAlpha(255);
					skipIntro = true;
				}
		}
	}
	
	public void setGraphics(){
		creatorsLogo.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		title.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		description1.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		description2.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		tapInfo.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		background.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		snake.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		sun.setGraphics(gameView.getSettings().antiAlias, gameView.getSettings().filtering);
		setBitmaps();
	}
}
