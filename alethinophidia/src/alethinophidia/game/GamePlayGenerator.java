package alethinophidia.game;


import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import alethinophidia.map.MapGrid;
import alethinophidia.pawns.ItemFactory;
import alethinophidia.pawns.SnakeCareCenter;
import alethinophidia.userInterface.InGameInterface;
import alethinophidia.utils.CollisionSolver;
import alethinophidia.utils.SurvivalMapListManager;

/**
 * Main game play manager, that creates and controls
 * game play elements such as 
 * SnakeCareCenter, ItemFactory, MapGrid and
 * CollisionSolver.
 * 
 * also holds ongoing game info: used theme name,
 * game mode, actual level
 * 
 * Every time new game starts, GamePlayGenerator
 * is recreated.
 * 
 * @author £ukasz Piotrowski
 */

public class GamePlayGenerator{
	
		public static final int DEFAULT_TAILS = 10;
		public static final int MODE_SURVIVAL = 0;
		public static final int MODE_CLASSIC = 1;
		public static final int MODE_CERTAIN_DEATH = 2;
		public static final int MODE_FREE = 3;
		private static final int SURVIVAL_TIMEOUT_MINUTES = 2;
		private int gameMode;
		private boolean pause;
		private boolean touchLocked;
		private boolean controllingSnake;
		private boolean gameLost;
		private int level;
		private SurvivalMapListManager mapList;
		private String theme;
		private int musicTimer=0;
	
		private GameView view;
		private InGameInterface gameMenu;
		
		//MAP
		private MapGrid mapGrid;
		Bitmap background;
		Bitmap wall;
		
		//COLLISION
		private CollisionSolver collisionSolver;
		
		//ITEMS
		private ItemFactory items;
		
		//SNAKE
		private SnakeCareCenter snake;
		
		public GamePlayGenerator(GameView view, int gameMode, String map, String theme){
			Random rand = new Random();
			gameLost = false;
			this.theme = theme;
			setPause(true);
			lockTouchEvent(true);
			controllingSnake = false;
			this.gameMode = gameMode;
			this.view = view;
			generateMap(map);
			createSnake(0,DEFAULT_TAILS ,view.getSettings().difficulty);
			generateCollisionSolver();
			createItems();
			createInGameInterface();
			if(gameMode == MODE_SURVIVAL){
				mapList = new SurvivalMapListManager(view);
			}
			level = 1;
			setBitmaps();
			view.getMusic().play(rand.nextInt(3), false);
		}
		
		private void musicController(){
			if(!view.getMusic().isPlayerPlaying()){
				musicTimer++;
				Random rand = new Random();
				if(musicTimer>=100+rand.nextInt(100)){
					view.getMusic().stop();
					view.getMusic().play(rand.nextInt(3), false);
					musicTimer = 0;
				}
			}
		}
		
		public void restartGame(){
			gameLost = false;
			setPause(true);
			lockTouchEvent(true);
			controllingSnake = false;
			createSnake(0,DEFAULT_TAILS ,view.getSettings().difficulty);
			generateCollisionSolver();
			createItems();
			createInGameInterface();
			gameMenu.setGreyOut(true);
			level = 1;
			setBitmaps();
		}
		
		public void update(){
			if(!pause){
				snake.update();
				items.update();
				snake.timeControl();
				mapChange();
				musicController();
			}
		}
		
		protected void onDraw(Canvas canvas){
			mapGrid.onDraw(canvas);
			items.onDraw(canvas);
			snake.onDraw(canvas);
			gameMenu.onDraw(canvas);
		}
		
		private void generateCollisionSolver(){
			collisionSolver = new CollisionSolver(view,mapGrid,snake.getSnakeHead(),snake.getSnakeTails());
			snake.setCollisionSolver(collisionSolver);
		}
		
		private void createSnake(int score, int tailLength, int difficulty){
			snake = new SnakeCareCenter(view, mapGrid, gameMode, score, tailLength, difficulty);
		}
		
		private void createItems(){
			items = new ItemFactory(gameMode, view, mapGrid, collisionSolver, snake);
		}
		
		private void createInGameInterface(){
			gameMenu = new InGameInterface(snake,mapGrid,view,gameMode);
		}
		
		private void generateMap(String map){
			Bitmap background = view.loadBitmap("/themes/"+theme+"bg.png");
			Bitmap wall = view.loadBitmap("/themes/"+theme+"wall.png");
			mapGrid = new MapGrid(view, background, wall, view.getViewContext(), map);
		}
		
		public void onTouchEvent(MotionEvent event){
			if(!pause){
				if(!controllingSnake){
					if(gameMenu.onTouchEvent(event))
						lockTouchEvent(true);
				}
				if(!touchLocked){
					controllingSnake = snake.onTouchEvent(event);
				}
				if(event.getAction()==MotionEvent.ACTION_UP){
					lockTouchEvent(false);
				}
				else if(event.getAction()==MotionEvent.ACTION_CANCEL){
					lockTouchEvent(false);
				}
			}
			gameMenu.onStartTouchEvent(event);
		}
		
		public void onBackDown() {
				view.getMenuGenerator().onBackDown();
		}
		
		public void lockTouchEvent(boolean lock){
			touchLocked = lock;
		}
		
		public void resetGraphics(){
			snake.setGraphics(view.getSettings().antiAlias, view.getSettings().filtering);
			gameMenu.setGraphics(view.getSettings().antiAlias, view.getSettings().filtering);
			setBitmaps();
		}
		
		public void setBitmaps(){
			background = view.loadBitmap("/themes/bg_wip.png");
			wall = view.loadBitmap("/themes/wall.png");
		}
		
		public void setPause(boolean pause){
			this.pause = pause;
		}
		
		public int getGameMode(){
			return gameMode;
		}
		
		public void setGameLost(boolean gameLost){
			this.gameLost = gameLost;
		}
		
		public boolean isGameLost(){
			return gameLost;
		}
		
		private boolean survivalTimeCheck(){
			return (snake.survivalTimeCheck()>=SURVIVAL_TIMEOUT_MINUTES);
		}
		
		private void mapChange(){
			if(survivalTimeCheck()){
				level++;
				int score = snake.getScore()+1000;
				int tailLength = snake.getTailLength();
				int difficulty = snake.getDifficulty();
				snake = null;
				mapGrid = null;
				items = null;
				collisionSolver = null;
				setPause(true);
				lockTouchEvent(true);
				generateMap(mapList.getRandomMap());
				createSnake(score, tailLength, difficulty);
				generateCollisionSolver();
				createItems();
				createInGameInterface();
				setBitmaps();
			}
		}
		
		public int getLevel(){
			return level;
		}
}
