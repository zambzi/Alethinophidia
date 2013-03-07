package alethinophidia.map;


import java.util.List;

import alethinophidia.game.GameView;
import alethinophidia.utils.Vector2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * 
 * Class used for creating small maps
 * in MapChoiceMenu.
 * MapThumb uses Rects to draw mini map
 * on screen at the given position.
 * 
 * @author £ukasz Piotrowski
 */

public class MapThumb extends MapGrid{
	
	private Vector2 position;
	private Rect background;
	private String mapName;
	
	
	public MapThumb(GameView gameView, Context context, Vector2 position, String mapName){
		super(gameView, null, null, context, mapName);
		super.squareHeight = (float)(0.011*gameView.getWidth());
		super.squareWidth = super.squareHeight;
		super.width = super.squareWidth*COLUMNS;
		super.height = super.squareHeight*ROWS;
		this.mapName = mapName;
		this.position = new Vector2(position.getX()*gameView.getWidth(), position.getY()*gameView.getHeight());
		background = new Rect((int)this.position.getX(), (int)this.position.getY(), (int)(this.position.getX()+width), (int)(this.position.getY()+height));
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.paint.setColor(Color.parseColor("#eba112"));
		super.paint.setAlpha(200);
		canvas.drawRect(background, paint);
		super.paint.setARGB(200, 0, 0, 0);
		drawWalls(canvas);
	}
	
	@Override
	protected void drawWalls(Canvas canvas){
		List<Vector2> walls = super.map.getMap();
		if(!walls.isEmpty()){
			for(int i =0; i< walls.size(); i++){
				Vector2 segment = walls.get(i);
				Rect wall = new Rect((int)(segment.getX()*squareWidth)+(int)position.getX(),
										(int)(segment.getY()*squareHeight)+(int)position.getY(),
										(int)(segment.getX()*squareWidth+squareWidth)+(int)position.getX(),
										(int)(segment.getY()*squareHeight+squareHeight)+(int)position.getY());
				canvas.drawRect(wall, paint);
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if((event.getX()>=position.getX() && event.getX()<=position.getX()+width) && (event.getY()>=position.getY() && event.getY()<=position.getY()+height)){
			return true;
		}
		return false;
	}
	
	public void setPosition(Vector2 position){
		this.position = position;
		background = new Rect((int)this.position.getX(), (int)this.position.getY(), (int)(this.position.getX()+width), (int)(this.position.getY()+height));
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void move(float moveTo, float catchPoint){
		position.setY(position.getY()+moveTo-catchPoint);
		background = new Rect((int)this.position.getX(), (int)this.position.getY(), (int)(this.position.getX()+width), (int)(this.position.getY()+height));
	}
	
	public String getMapName(){
		return mapName;
	}
}
