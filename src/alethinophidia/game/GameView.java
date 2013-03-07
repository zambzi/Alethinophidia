package alethinophidia.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import alethinophidia.utils.BitmapLoader;
import alethinophidia.utils.GameSettings;
import alethinophidia.utils.HiScores;

/**
 * 
 * GameView is the main Manager of entire app.
 * Game Loop thread, and other game elements are created
 * and managed in this class.
 * 
 * Additional nested class, SurfaceControl is used to access
 * abstract methods of Callback from android.view.SurfaceHolder
 * 
 * @author £ukasz Piotrowski
 */

public class GameView extends SurfaceView{	
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private Context context;
	private GamePlayGenerator gamePlayGenerator;
	private IntroGenerator introGenerator;
	private MenuGenerator menuGenerator;
	private GameSettings settings;
	private HiScores hiScores;
	private BitmapLoader bitmapLoader;
	private boolean firstRun;
	private SoundGenerator sound;
	private MusicGenerator music;
	
	private boolean gameInProgress;
	Canvas canvas;

	class SurfaceControl implements Callback{
		
		public void surfaceDestroyed(SurfaceHolder holder){}
		
		public void surfaceCreated(SurfaceHolder holder){
			if(firstRun){
				generateMenu();
				menuController(0,0,null,null);
			}
			gameLoopThread.setRunState(true);
			if(!gameLoopThread.isAlive())
				gameLoopThread.start();
			firstRun = false;
		}
		public void surfaceChanged (SurfaceHolder holder, int format, int width, int height){}
	}
	
	public GameView(Context context){
		super(context);
		firstRun = true;
		this.context = context;
		gameLoopThread = new GameLoopThread(this);
		gameInProgress = false;
		settings = new GameSettings(this.context);
		settings.parseSettings(false);
		bitmapLoader = new BitmapLoader(context,settings,this);
		hiScores = new HiScores();
		holder = getHolder();
		canvas = gameLoopThread.getCanvas();
		holder.addCallback(new SurfaceControl());
		generateSounds();
		
	}
		
	protected void generateGame(int mode, String map, String theme){
		if(gamePlayGenerator != null)
			gamePlayGenerator = null;
		music.stop();
		gamePlayGenerator = new GamePlayGenerator(this,mode, map, theme);
		gameInProgress = true;
	}
	
	protected void generateSounds(){
		music = new MusicGenerator(context, settings);
		sound = new SoundGenerator(context, settings);
		sound.putSound(SoundGenerator.SOUND_CLICK, "audio/sounds/click.ogg"); //OK
		
		sound.putSound(SoundGenerator.SOUND_EAT_1, "audio/sounds/eat1.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_EAT_2, "audio/sounds/eat2.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_EAT_3, "audio/sounds/eat3.ogg"); //OK
		
		sound.putSound(SoundGenerator.SOUND_DIE_1, "audio/sounds/die1.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_DIE_2, "audio/sounds/die2.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_DIE_3, "audio/sounds/die3.ogg"); //OK
		
		sound.putSound(SoundGenerator.SOUND_ITEM_CREATE, "audio/sounds/create.ogg");  //OK
		sound.putSound(SoundGenerator.SOUND_ITEM_DESTROY, "audio/sounds/destroy.ogg"); //OK
		
		sound.putSound(SoundGenerator.SOUND_SPEED_UP, "audio/sounds/test.wav"); //NU
		sound.putSound(SoundGenerator.SOUND_SLOW_DOWN, "audio/sounds/test.wav"); //NU
		
		sound.putSound(SoundGenerator.SOUND_POISON_1, "audio/sounds/poison1.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_POISON_2, "audio/sounds/poison2.ogg"); //OK
		sound.putSound(SoundGenerator.SOUND_POISON_3, "audio/sounds/poison3.ogg"); //OK
	}
	
	protected void generateMenu(){
		menuGenerator = new MenuGenerator(this);
	}
	
	protected void generateIntro(){
		music.play(MusicGenerator.MUSIC_MAIN_THEME, true);
		introGenerator = new IntroGenerator(this,menuGenerator);
	}
	
	public void update(){
		if(gamePlayGenerator!=null){
			gamePlayGenerator.update();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		if(gameInProgress){
			gamePlayGenerator.onDraw(canvas);
		}
		else{
			introGenerator.onDraw(canvas);
		}
		menuGenerator.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//if(gameLoopThread.getTouch() == true){
		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {
			Log.e("onTouchEvent", "Sleep Exception");
			return true;
		}
			synchronized(event){
					if(gameInProgress && gamePlayGenerator != null){
						gamePlayGenerator.onTouchEvent(event);
					}
					else if(!gameInProgress && introGenerator != null){
						introGenerator.onTouchEvent(event);
					}
					menuGenerator.onTouchEvent(event);
					gameLoopThread.disableTouch();
					return true;
			}
		//}
	}
	
	
	public void onBackDown(){
		if(gameInProgress && gamePlayGenerator != null){
			gamePlayGenerator.onBackDown();
		}
	}
	
	
	public Context getViewContext(){
		return context;
	}
	
	public void menuController(int value, int mode, String mapName, String theme){
		gameLoopThread.setPauseState(true);
		switch(value){
			case 0 : generateIntro(); break;
			case 1 : generateGame(mode, mapName, theme); introGenerator = null; break;
			default : break;
		}
		gameLoopThread.setPauseState(false);
	}
	
	public GameSettings getSettings(){
		return settings;
	}
	
	public MenuGenerator getMenuGenerator(){
		return menuGenerator;
	}
	
	public GamePlayGenerator getGame(){
		return gamePlayGenerator;
	}
	
	public IntroGenerator getIntro(){
		return introGenerator;
	}
	
	public HiScores getHiScores(){
		return hiScores;
	}
	
	public void setHiscores(HiScores scores){
		hiScores = scores;
	}
	
	public void setRunState(boolean state){
		gameLoopThread.setRunState(state);
	}
	
	public Bitmap loadBitmap(String name){
		return bitmapLoader.loadBitmap(name);
	}
	
	public void resetGraphics(){
		canvas = holder.lockCanvas();
		if(getGame()!=null)
			getGame().resetGraphics();
		if(getIntro()!=null)
			getIntro().setGraphics();
		getMenuGenerator().resetGraphics();
		holder.unlockCanvasAndPost(canvas);
	}
	
	public void onPause(){
		music.pause();
		menuGenerator.unhideMainMenu();
		pauseGameLoop(true);
	}
	
	public void pauseGameLoop(boolean pause){
		gameLoopThread.setPauseState(pause);
	}
	
	public void onResume(){
		music.resume();
		pauseGameLoop(false);
	}
	
	public void onDestroy(){
		music.stop();
		boolean retry = true;
		gameLoopThread.setRunState(false);
		while(retry){
			try{
				gameLoopThread.join();
				retry = false;
			
			} catch (InterruptedException e) {
				
			}
		}
		bitmapLoader.clearBitmaps();
	}
	
	public GameLoopThread getLoopTread(){
		return gameLoopThread;
	}
	
	public SoundGenerator getSound(){
		return sound;
	}
	
	public MusicGenerator getMusic(){
		return music;
	}
	
}
