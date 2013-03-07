package alethinophidia.pawns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alethinophidia.game.GameLoopThread;
import alethinophidia.game.GamePlayGenerator;
import alethinophidia.game.GameView;
import alethinophidia.game.SoundGenerator;
import alethinophidia.map.MapGrid;
import alethinophidia.utils.CollisionSolver;
import alethinophidia.utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 
 * A weird name, I agree. But actually
 * fitting in fact. This class is basically
 * a main care taker of our snake. Responsible
 * of such tasks as creating new Snake Instances
 * (one head and list of tail segments);
 * controlling snake diet by checking if it
 * collided with an item, and then adding
 * item-specific properties to the snake
 * etc.
 * 
 * Also controls whether or not snake should
 * die (by low health or collision, using
 * CollisionSolver instance from GamePlayGenerator)
 * 
 * @author £ukasz Piotrowski
 */

public class SnakeCareCenter {
	private static final int MAX_HEALTH = 10;
	private static final int PROPERTY_TIMEOUT = 350;
	private boolean snakeMovable;
	private Snake snakeHead;
	private List<Snake> snakeTails = new ArrayList<Snake>();
	private CollisionSolver collisionSolver;
	private int score;
	private int health;
	private float speedMultiplier;
	private int propertyTimer;
	private MapGrid grid;
	private int gameMode;
	private int difficulty;
	private int time[];
	private int frames;
	
	private static final int NO_HEALTH = 4;
	
	Bitmap tailBitmap;
	Bitmap headBitmap;
	
	private GameView gameView;
	
	public SnakeCareCenter(GameView gameView, MapGrid grid, int gameMode, int score, int tailLength, int difficulty){
		this.gameMode = gameMode;
		this.grid = grid;
		this.gameView = gameView;
		this.difficulty = difficulty;
		this.score = score;
		time = new int[3];
		time[0] = 0;
		time[1] = 0;
		time[2] = 0;
		frames = 0;
		setBitmaps();
		createSnake(tailLength);
		snakeMovable = false;
		score = 0;
		health = MAX_HEALTH;
	}
	
	public void update(){
		snakeHead.update();
		for(Snake tail : snakeTails){
			tail.update();
		}
		int deathType;
		deathType = collisionSolver.testCollision();
		if(	deathType==CollisionSolver.COL_WALL ||
			deathType==CollisionSolver.COL_BORDER ||
			deathType==CollisionSolver.COL_TAIL		){
			playDeathSound();
			gameView.getGame().setGameLost(true);
			gameView.getGame().setPause(true);
			String deathTypeStr = setDeathType(deathType);
			popScoreWindow(gameView.getHiScores().setNewScore(gameMode, score, snakeTails.size(), gameView.getGame().getLevel(), difficulty,time,deathTypeStr));
		}
		if(health<=0){
			playDeathSound();
			deathType = NO_HEALTH;
			gameView.getGame().setGameLost(true);
			gameView.getGame().setPause(true);
			String deathTypeStr = setDeathType(deathType);
			popScoreWindow(gameView.getHiScores().setNewScore(gameMode, score, snakeTails.size(), gameView.getGame().getLevel(), difficulty,time,deathTypeStr));
		}
		isPropertyOutOfTime();
		snakeTailMotion();
	}
	
	private void playDeathSound(){
		Random rand = new Random();
		gameView.getSound().playSound(rand.nextInt(2)+8, 0);
	}
	
	private String setDeathType(int deathType){
		switch(deathType){
		case CollisionSolver.COL_WALL : return "Wallgrinding";
		case CollisionSolver.COL_BORDER : return "Hitting Edge";
		case CollisionSolver.COL_TAIL : return "Cannibalism";
		case NO_HEALTH : return "Tummy ache";
		default : return "unknown";
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
			MotionEvent action = event;
			switch(action.getAction()){
				case (MotionEvent.ACTION_DOWN) : 	snakeMovable = true;
													return true;
				case (MotionEvent.ACTION_UP) :		moveSnake((double)action.getX(),(double)action.getY());
													return false;
				case (MotionEvent.ACTION_MOVE) : 	snakeMovable = true;
													moveSnake((double)action.getX(),(double)action.getY());
													return true;
				case (MotionEvent.ACTION_CANCEL) : 	snakeMovable = false;
													return false;
			}
			if(action.getY()>grid.getHeight())
				snakeMovable = false;
			return false;
	}
	
	private void moveSnake(double x, double y){
		if(snakeMovable){
			snakeHead.setDirection(new Vector2(x, y));
			snakeMovable = false;
		}
	}
		
	private void snakeTailMotion(){
			for(int i = 0; i < snakeTails.size(); i++){
				Snake actualTail = snakeTails.get(i);
				if(i==0){
					actualTail.setPropertiesViaStep(snakeHead.getLastStep());
				}
				else{
					Snake previousTail = snakeTails.get(i-1);
					actualTail.setPropertiesViaStep(previousTail.getLastStep());
				}
			}
	}
		
	private void extendSnakeTail(int length){
		if(length>0){
			for(int i = 0; i<length; i++){
				int tailNumber;
				if(snakeTails.isEmpty())
					tailNumber = 1;
				else
					tailNumber = snakeTails.get(snakeTails.size()-1).getTailNumber()+1;
				snakeTails.add(new Snake(gameView,tailBitmap,tailNumber, grid, snakeHead));
			}
		}
		else{
			length = -length;
			if(snakeTails.size()>GamePlayGenerator.DEFAULT_TAILS+length){
				for(int i = 0; i<length; i++){
					snakeTails.remove(snakeTails.size()-1);
				}
			}
		}
		for(int i=1; i<snakeTails.size(); i++){
			snakeTails.get(i).changeSize(snakeTails.size(), snakeTails.get(i-1).getHeight());
		}
	}
		
	private void createSnake(int tailLength){
			snakeHead = new Snake(gameView, headBitmap, grid, gameMode==GamePlayGenerator.MODE_CLASSIC);
			extendSnakeTail(tailLength);
	}
		
	public void setCollisionSolver(CollisionSolver collisionSolver){
			this.collisionSolver = collisionSolver;
	}
		
	public void onDraw(Canvas canvas){
		for(Snake snake : snakeTails){
			snake.onDraw(canvas);
		}
		snakeHead.onDraw(canvas);
	}
		
	public Snake getSnakeHead(){
			return snakeHead;
	}
		
	public List<Snake> getSnakeTails(){
			return snakeTails;
	}

	public void setTimedSpeedBonus(){
		removeSpeedPropertyEffect();
		propertyTimer = 0;
		
		if(speedMultiplier<0){
			int speed;
			speed = (difficulty==0) ? 10 : (difficulty==1) ? 20 : 40;
			snakeHead.setSpeed(speed);
		}
		else
			snakeHead.setSpeed((int)speedMultiplier+snakeHead.getDefaultSpeed());
	}
	
	public boolean isPropertyOutOfTime(){
		if(propertyTimer<PROPERTY_TIMEOUT){
			propertyTimer++;
			return false;
		}
		else{
			removePropertyEffects();
			propertyTimer = 0;
			return true;
		}
	}
	
	private void removePropertyEffects(){
		removeSpeedPropertyEffect();
	}
	
	private void removeSpeedPropertyEffect(){
		snakeHead.setSpeed(snakeHead.getDefaultSpeed());
	}
	
	public void setItemEffects(ItemProperties itemProperties){
		score+=itemProperties.getScore();
		extendSnakeTail(itemProperties.getTailExtensionLength());
		if(itemProperties.getSpeedUp()!=0){
			speedMultiplier = itemProperties.getSpeedUp();
			setTimedSpeedBonus();
		}
		if(itemProperties.getHealthAddition()<0){
			Random rand = new Random();
			gameView.getSound().playSound(rand.nextInt(2)+15, 0);
		}
		health = health + itemProperties.getHealthAddition();
		if(health>MAX_HEALTH){
			health = MAX_HEALTH;
		}
		else if(health<0){
			health = 0;
		}
	}
	
	public int getHealth(){
		return health;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getTailLength(){
		return snakeTails.size();
	}
	
	public int getDifficulty(){
		return difficulty;
	}
	
	
	private void setBitmaps(){
		headBitmap = gameView.loadBitmap("/snake/head_s.png");
		tailBitmap = gameView.loadBitmap("/snake/tail_s_l.png");
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		setBitmaps();
		snakeHead.setGraphics(antiAlias, filtering, headBitmap);
		for(Snake tail : snakeTails){
			tail.setGraphics(antiAlias, filtering, tailBitmap);
		}
		grid.setGraphics(antiAlias, filtering);
	}
	
	public void popScoreWindow(int place){
		gameView.getMenuGenerator().popScoreWindow(score, place, gameView.getGame().getGameMode(), time);
	}
	
	public int[] getTime(){
		return time;
	}
	
	public void timeControl(){
		frames++;
		if(frames>=GameLoopThread.FPS){
			frames = 0;
			time[2]++;
			if(time[2]>=60){
				time[2] = 0;
				time[1]++;
				if(time[1]>=60){
					time[1] = 0;
					time[0]++;
				}
			}
		}
	}
	
	
	public int survivalTimeCheck(){
		if(gameMode == GamePlayGenerator.MODE_SURVIVAL){
			return time[1];
		}
		else{
			return -1;
		}
	}
}
