package alethinophidia.pawns;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.RectF;
import alethinophidia.game.*;
import alethinophidia.map.MapGrid;
import alethinophidia.utils.CollisionSolver;
import alethinophidia.utils.Vector2;

/**
 * 
 * Just as name implies - it's an item.
 * Item class is used to draw and place
 * all that fancy stuff for our snake
 * to eat.
 * 
 * @author £ukasz Piotrowski
 */

public class Item {
	private static final int BMP_ROWS = 1;
	private static final int BMP_COLUMNS = 1;
	private Paint paint;
	private int x;
	private int y;
	private Bitmap bmp;
	private int width;
	private int height;
	private MapGrid grid;
	private boolean cantPlaceItem;
	private ItemProperties itemProperties;
	private CollisionSolver collisionSolver;
	private boolean itemEaten;
	private boolean itemRemoved;
	private int itemTimeout;
	private boolean itemTimed;
	private int timer;
	
	
	public void update(){
		eatItem();
		if(itemTimed){
			timeItem();
			if(timer>=itemTimeout)
				itemRemoved = true;
		}
	}
	
	public Item(GameView gameView, Bitmap bmp, MapGrid grid, String itemName, CollisionSolver collisionSolver, Vector2 location, int itemTimeout, boolean itemTimed){
		itemProperties = new ItemProperties(itemName);
		this.grid = grid;
		this.bmp = bmp;
		this.width = bmp.getWidth()/BMP_COLUMNS;
		this.height = bmp.getHeight()/BMP_ROWS;
		cantPlaceItem = false;
		itemEaten = false;
		this.collisionSolver = collisionSolver;
		x = (int) location.getX();
		y = (int) location.getY();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		this.itemTimeout = itemTimeout;
		this.itemTimed = itemTimed;
		timer = 0;
	}
	
	
	public void onDraw(Canvas canvas){
		if(!cantPlaceItem || !itemEaten){
			int srcX = 0;
			int srcY = 0;
			Rect src = new Rect(srcX, srcY, srcX+width, srcY+height);
			RectF dst = new RectF(x,y,x+grid.getSquareWidth(), y+grid.getSquareHeight());
			canvas.drawBitmap(bmp, src, dst, paint);
		}
	}
	
	private void eatItem(){
		if(collisionSolver.snakeToItemCollision(new Vector2(x,y))){
			itemEaten = true;
		}
	}
	
	private void timeItem(){
		timer++;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public boolean isItemEaten(){
		return itemEaten;
	}
	
	public boolean isItemRemoved(){
		return itemRemoved;
	}

	public ItemProperties getProperties(){
		return itemProperties;
	}
}
