package alethinophidia.game;

import android.graphics.Canvas;

/**
 * Main game loop, created as an additional thread
 * controlled by GameView class
 * 
 * @author £ukasz Piotrowski
 */

public class GameLoopThread extends Thread{
	public static final int FPS = 30;
	private GameView gameView;
	private boolean isRunning = false;
	private Canvas canvas;
	private boolean paused;
	private boolean applyTouch = false;
	
	public void disableTouch(){
		applyTouch = false;
	}
	
	public boolean getTouch(){
		return applyTouch;
	}
	
	@Override
	public void run(){
		long ticks = 1000/FPS;
		long startTime;
		long sleepTime;
		while(isRunning){
			if(!paused){
				applyTouch = true;
				startTime = System.currentTimeMillis();
				drawAndUpdate();
				sleepTime = ticks - (System.currentTimeMillis() - startTime);
				try {
					if(sleepTime >0 )
						sleep(sleepTime);
					else
						sleep(10);
				} catch (Exception e) {
					/*No exception code here
					 * since it will not affect
					 * overall performance
					 */
				} 
			}
		}
	}
	
	public GameLoopThread(GameView gameView){
		this.gameView = gameView;
		canvas = null;
	}
	
	public void setRunState(boolean runState){
		isRunning = runState;
	}
	
	public void setPauseState(boolean pauseState){
		paused = pauseState;
	}
	
	public Canvas getCanvas(){
		return canvas;
	}
	
	private void drawAndUpdate(){
			canvas = gameView.getHolder().lockCanvas();
			synchronized (gameView.getHolder()) {
				gameView.update();
				gameView.onDraw(canvas);
			}
			if(canvas != null){
				gameView.getHolder().unlockCanvasAndPost(canvas);
		}
	}
}
