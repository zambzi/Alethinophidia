package alethinophidia.userInterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import alethinophidia.game.GameView;
import alethinophidia.utils.Transitions;
import alethinophidia.utils.Vector2;

/**
 * 
 * It's an universal class for creating
 * interface elements. Buttons, fields,
 * bitmaps and such. Also holds instance
 * of Transitions class witch allows for
 * simple transition animations like fading
 * or blinking.
 * 
 * note: coordinates, width and height arguments
 * in constructor are float values in scope from 0 
 * to 1.0, where 0 means either left or upper edge
 * of the screen, while 1.0 means right or lower
 * edge.
 * 
 * @author £ukasz Piotrowski
 */

public class Field {
	private int animationRows = 1;
	private int animationColumns = 1;
	private Vector2 position;
	private float width;
	private float height;
	private Bitmap background;
	private Paint paint;
	private int currentFrame;
	private int animationRectWidth;
	private int animationRectHeight;
	private int alpha = 255;
	private Transitions transitions;
	private int orientation;
	private boolean triggered;
	GameView gameView;
	
	public Field(Vector2 position, float width, float height, Bitmap background, GameView gameView){
		initField(position, background, gameView);
		this.width = (width*gameView.getWidth());
		this.height = (height*gameView.getHeight());
	}
	
	public Field(Vector2 position, float size, Bitmap background, GameView gameView){
		initField(position, background, gameView);
		this.width = (size*gameView.getWidth());
		this.height = width;
	}
	
	private void initField(Vector2 position, Bitmap background, GameView gameView){
		this.gameView = gameView;
		this.position = new Vector2(position.getX()*gameView.getWidth(), position.getY()*gameView.getHeight());
		this.background = background;
		triggered = false;
		paint = new Paint();
		paint.setAntiAlias(gameView.getSettings().antiAlias);
		paint.setFilterBitmap(gameView.getSettings().filtering);
		currentFrame = 0;
		orientation = 0;
		if(background!=null){
			animationRectWidth = background.getWidth()/animationColumns;
			animationRectHeight = background.getHeight()/animationRows;
		}
		transitions = new Transitions(this, gameView);
	}
	
	/**position in [0,1] scope*/
	public void setPosition(Vector2 position){
		this.position.becomes(new Vector2(position.getX()*gameView.getWidth(), position.getY()*gameView.getHeight()));
	}
	
	/**normal coordinates*/
	public void setPosition2(Vector2 position){
		this.position.becomes(position);
	}

	public void setSize(float multiplier, boolean preserveCenterPosition){
		if(multiplier>0){
			float tmpW = width;
			float tmpH = height;
			width *= multiplier;
			height *= multiplier;
			if(preserveCenterPosition){
				position.setX(position.getX()-((width-tmpW)/2));
				position.setY(position.getY()-((height-tmpH)/2));
			}
		}
	}
	
	public void setSize(float width, float height, boolean preserveCenterPosition){
		float tmpW = width;
		float tmpH = height;
		if(width>0)
			this.width = width;
		if(height>0)
			this.height = height;
		if(preserveCenterPosition){
			position.setX(position.getX()-((width-tmpW)/2));
			position.setY(position.getY()-((height-tmpH)/2));
		}
	}
	
	
	public void setAnimationRowsAndColumns(int rows, int columns){
		if(rows>0)
			this.animationRows = rows;
		if(columns>0){
			this.animationColumns = columns;
		animationRectWidth = background.getWidth()/animationColumns;
		animationRectHeight = background.getHeight()/animationRows;
		}
	}
	
	public void setFrame(int frame){
		currentFrame = frame;
	}
	
	public void update(){
		
	}
	
	public void onDraw(Canvas canvas){
		if(background!=null){
			transitions.update();
			paint.setAlpha(alpha);
			int srcX = 0;//currentFrame*animationRectWidth;
			int srcY = currentFrame*animationRectHeight;
			canvas.save();
			canvas.rotate((float)orientation,(float)position.getX()+width/2, (float)position.getY()+height/2);
			Rect src = new Rect(srcX, srcY, srcX+animationRectWidth, srcY+animationRectHeight);
			RectF dst = new RectF(	(int)position.getX(),
									(int)position.getY(),
									(int)position.getX()+width, 
									(int)position.getY()+height	);
			canvas.drawBitmap(background, src, dst, paint);
			canvas.restore();
		}
	}
	
	public void setBackground(Bitmap background){
		this.background = background;
		paint.setDither(true);
		
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getPosition2(){
		return new Vector2(position.getX()/gameView.getWidth(), position.getY()/gameView.getHeight());
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public void setAlpha(int value){
		alpha = value;
	}
	
	public void addAlpha(int multiplier){
		if(multiplier<=0)
			multiplier = 1;
		alpha = alpha+multiplier;
		if(alpha>255)
			alpha = 255;
	}
	
	public void removeAlpha(int multiplier){
		if(multiplier <= 0)
			multiplier = 1;
		alpha = alpha - multiplier;
		if(alpha<0)
			alpha = 0;
	}
	
	public int getAlpha(){
		return alpha;
	}
	
	public Transitions getTransitions(){
		return transitions;
	}
	
	public void addOrientation(int value){
		orientation+=value;
		if(orientation>=360)
			orientation=360-value;
		else if(orientation<=0)
			orientation = value+360;
	}
	
	public void setOrientation(int value){
		orientation = value;
		if(orientation>360)
			orientation=360;
		else if(orientation<0)
			orientation = 0;
	}
	
	public boolean isPointOnField(float x, float y){
		if((x>=position.getX() && x<=position.getX()+width) && (y>=position.getY() && y<=position.getY()+height)){
			triggered = (triggered==true) ? false : true;
			return true;
		}
		else
			return false;
	}
	
	public int getFrame(){
		return currentFrame;
	}
	
	public boolean isTriggered(){
		return triggered;
	}
	
	public void setTriggeredStatus(boolean value){
		triggered = value;
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering){
		paint.setAntiAlias(antiAlias);
		paint.setFilterBitmap(filtering);
	}
}
