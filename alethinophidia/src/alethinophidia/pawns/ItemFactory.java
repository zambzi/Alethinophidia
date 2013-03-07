package alethinophidia.pawns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alethinophidia.game.GamePlayGenerator;
import alethinophidia.game.GameView;
import alethinophidia.game.R;
import alethinophidia.game.SoundGenerator;
import alethinophidia.map.MapGrid;
import alethinophidia.map.MapReader;
import alethinophidia.utils.CollisionSolver;
import alethinophidia.utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * 
 * Class that takes care of spawning
 * items around the map.
 * 
 * Uses CollisionSolver instance created
 * by GamePlayGenerator to check if snake
 * touched an item
 * 
 * @author £ukasz Piotrowski
 */

public class ItemFactory {
	private static final int APPLE_SPAWN_CHANCE = 56;
	private static final int ORANGE_SPAWN_CHANCE = 25 + APPLE_SPAWN_CHANCE;
	private static final int SHROOMS_SPAWN_CHANCE = 5 + ORANGE_SPAWN_CHANCE;
	private static final int ROTTEN_SPAWN_CHANCE = 5 + SHROOMS_SPAWN_CHANCE;
	private static final int MOUSE_SPAWN_CHANCE = 1 + ROTTEN_SPAWN_CHANCE;
	private static final int QUEBAB_SPAWN_CHANCE = 1 + MOUSE_SPAWN_CHANCE;
	private static final int TRAP_SPAWN_CHANCE = 5 + QUEBAB_SPAWN_CHANCE;
	private static final int DRINK_SPAWN_CHANCE = 2 + TRAP_SPAWN_CHANCE;
	
	private List<Item> items = new ArrayList<Item>();
	
	private int maxItemsOnScreen;
	private int spawnTimer;
	private int spawnTime;
	private int removeTime;
	GameView gameView;
	MapGrid grid;
	CollisionSolver collisionSolver;
	SnakeCareCenter snake;
	private int gameMode;
	
	public ItemFactory(int gameMode, GameView gameView, MapGrid grid, CollisionSolver collisionSolver, SnakeCareCenter snake){
		this.gameView = gameView;
		this.grid = grid;
		this.collisionSolver = collisionSolver;
		this.snake = snake;
		this.gameMode = gameMode;
		switch(gameMode){
			case GamePlayGenerator.MODE_SURVIVAL : maxItemsOnScreen = 5; spawnTime = 20; removeTime = 1000; break;
			case GamePlayGenerator.MODE_CLASSIC : maxItemsOnScreen = 1; spawnTime = 1; removeTime = 1000;break;
			case GamePlayGenerator.MODE_CERTAIN_DEATH : maxItemsOnScreen = 30; spawnTime = 10; removeTime = 500;break;
			case GamePlayGenerator.MODE_FREE : maxItemsOnScreen = 5; spawnTime = 20; removeTime = 1000;break;
		}
	}
	
	private void spawnItem(){
		String name = chooseItemName();
		if(spawnTimeCheck()){
			if(items.size()<=maxItemsOnScreen){
				Vector2 tmpVector = placeItem();
				if(tmpVector.getX()>=0 && tmpVector.getY()>=0){
					items.add(new Item(gameView, chooseBitmap(name), grid, name, collisionSolver, tmpVector, removeTime, true));
					gameView.getSound().playSound(SoundGenerator.SOUND_ITEM_CREATE, 0);
				}
				spawnTimer = 0;
			}
		}
	}
	
	
	private Vector2 placeItem(){
		Vector2 tmpVector = new Vector2(-1,-1);
		int i = 0;
		while((tmpVector.getX() < 0 && tmpVector.getY() < 0)){
			tmpVector = getEmptySpace();
			i++;
			if(i>10) break;
		}
		return new Vector2(tmpVector.getX()*grid.getSquareWidth(), tmpVector.getY()*grid.getSquareHeight());
	}
	
	private Vector2 getEmptySpace(){
		MapReader map = grid.getMapReader();
		Random rnd = new Random(System.currentTimeMillis());
		int tmpX, tmpY;
		tmpX = rnd.nextInt(MapGrid.COLUMNS);
		tmpY = rnd.nextInt(MapGrid.ROWS);
		boolean takenSpace = false;
		
		for(int i = 0; i<map.getMap().size(); i++){
			if(map.getMap().get(i).getX() == tmpX &&  map.getMap().get(i).getY() == tmpY){
				takenSpace = true;
				break;
			}
		}
		if(takenSpace){
			return new Vector2(-1,-1);
		}
		else{
			return new Vector2(tmpX,tmpY);
		}
	}
	
	public void onDraw(Canvas canvas){
		for(Item item : items){
			item.onDraw(canvas);
		}
	}
	
	public void update(){
		for(Item item : items){
			item.update();
		}
		spawnItem();
		removeItem();
	}
	
	private void removeItem(){
		for(int i = 0; i<items.size(); i++){
			if(items.get(i).isItemEaten()){
				snake.setItemEffects(items.get(i).getProperties());
				items.remove(i);
				Random rand = new Random();
				gameView.getSound().playSound(rand.nextInt(2)+5, 0);
			}
			else if(items.get(i).isItemRemoved())
			{
				items.remove(i);
				gameView.getSound().playSound(SoundGenerator.SOUND_ITEM_DESTROY, 0);
			}
		}
	}
	
	private boolean spawnTimeCheck(){
		if(spawnTimer == spawnTime)
			return true;
		spawnTimer++;
		return false;
	}
	
	
	private String chooseItemName(){
		Random rand = new Random();
		int choice = rand.nextInt(100);
		if(gameMode == GamePlayGenerator.MODE_CERTAIN_DEATH){
			return "trap";
		}
		else{
			if(choice< APPLE_SPAWN_CHANCE) return "apple";
			else if(choice< ORANGE_SPAWN_CHANCE) return "orange";
			else if(choice< SHROOMS_SPAWN_CHANCE) return "shrooms";
			else if(choice< ROTTEN_SPAWN_CHANCE) return "rottenFruit";
			else if(choice< MOUSE_SPAWN_CHANCE) return "mouse";
			else if(choice< QUEBAB_SPAWN_CHANCE) return "quebab";
			else if(choice< TRAP_SPAWN_CHANCE) return "trap";
			else if(choice< DRINK_SPAWN_CHANCE) return "energyDrink";
			//in case of emergency:
			return "apple";
		}
	}
	
	private Bitmap chooseBitmap(String name){
		if(name == "apple") return gameView.loadBitmap("/items/apple.png");
		if(name == "orange") return gameView.loadBitmap("/items/orange.png");
		if(name == "shrooms") return gameView.loadBitmap("/items/shroom.png");
		if(name == "rottenFruit") return gameView.loadBitmap("/items/rotten.png");
		if(name == "mouse") return gameView.loadBitmap("/items/mouse.png");
		if(name == "quebab") return gameView.loadBitmap("/items/quebab.png");
		if(name == "trap") return gameView.loadBitmap("/items/poison.png");
		if(name == "energyDrink") return gameView.loadBitmap("/items/drink.png");
		//in case of emergency"
		return BitmapFactory.decodeResource(gameView.getResources(), R.drawable.err);
	}
}
