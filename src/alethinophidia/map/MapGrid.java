package alethinophidia.map;

import alethinophidia.game.*;
import alethinophidia.utils.Vector2;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Bitmap;

import java.util.List;
import android.content.Context;

/**
 * 
 * Responsible of drawing map on the screen,
 * along with background image
 * Also creates and holds MapReader instance
 * 
 * @author £ukasz Piotrowski
 */

public class MapGrid{
	public static final int ROWS = 29;
	public static final int COLUMNS = 20;
	protected float width;
	protected float height;
	protected float squareWidth;
	protected float squareHeight;
	private Bitmap background;
	private Bitmap wallTexture;
	protected Paint paint;
	protected MapReader map;
	
	public MapGrid(GameView gameView, Bitmap background, Bitmap wallTexture, Context context, String mapName){
		map = new MapReader(context, mapName);
		this.background = background;
		this.wallTexture = wallTexture;
		width = gameView.getWidth();
		height = gameView.getHeight()-(3*gameView.getHeight()/ROWS);
		squareWidth = width/COLUMNS;
		squareHeight = height/ROWS;
		paint = new Paint();
		paint.setAntiAlias(gameView.getSettings().antiAlias);
		paint.setFilterBitmap(gameView.getSettings().filtering);
		paint.setDither(true);
	}
	
	public Vector2 getSquare(int row, int column){
		Vector2 tempVector = new Vector2(squareWidth*column, squareHeight*row);
		return tempVector;
	}
	
	
	public float getSquareWidth(){
		return squareWidth;
	}
	
	public float getSquareHeight(){
		return squareHeight;
	}
	
	
	private void drawBackground(Canvas canvas){
		Rect rect = new Rect(0,0,background.getWidth(), background.getHeight());
		RectF rectF = new RectF(0,0,width,height);
		canvas.drawBitmap(background, rect, rectF, paint);
	}
	
	protected void drawWalls(Canvas canvas){
		List<Vector2> walls = map.getMap();
		if(!walls.isEmpty()){
			for(int i =0; i< walls.size(); i++){
				Vector2 segment = walls.get(i);
				Rect rect = new Rect(0,0,wallTexture.getWidth(), wallTexture.getHeight());
				RectF rectF = new RectF((int)segment.getX()*squareWidth,
										(int)segment.getY()*squareHeight,
										(int)segment.getX()*squareWidth+squareWidth,
										(int)segment.getY()*squareHeight+squareHeight);
				canvas.drawBitmap(wallTexture, rect, rectF, paint);
			}
		}
	}
	
	public void onDraw(Canvas canvas){
		drawBackground(canvas);
		drawWalls(canvas);
	}
	
	public MapReader getMapReader(){
		return map;
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		paint.setAntiAlias(antiAlias);
		paint.setFilterBitmap(filtering);
	}
	
}
