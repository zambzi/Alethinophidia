package alethinophidia.pawns;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.RectF;
import alethinophidia.game.*;
import alethinophidia.map.MapGrid;
import alethinophidia.utils.Vector2;
import alethinophidia.utils.Step;

/**
 * 
 * Our main protagonist. This class
 * controls position, movement vectors,
 * orientation etc. of the snake
 * (Whether we're talking about snake
 * head or snake tail segment)
 * 
 * This class may be a little cluttered
 * so in future I'll think about changing it
 * to three classes: Snake and deriving from
 * it subclasses: SnakeHead and SnakeTail
 * 
 * Snake tails use list of instances of class
 * Step to 'remember' all the positions of
 * tail/head before it.
 * 
 * @author £ukasz Piotrowski
 */

public class Snake{
	

	private int maxSteps = 4;

	private static final int AOA_CONSTRAINT_UP = 315+35;
	private static final int AOA_CONSTRAINT_DOWN = 45-35;
	private int defaultSpeed = 20;
	private Vector2 targetVector;
	private Vector2 fullTargetVector;
	private Paint paint;
	private List<Step> steps;

	private double x;
	private double y;
	private double orientation = 0;
	private Bitmap bmp;
	private int width;
	private float height;
	private int animationRectWidth;
	private int animationRectHeight;
	private int speed;
	private boolean isTail;
	private int tailNumber; 
	private boolean classicControls;
	private GameView gameView;
	
	/*
	 * Constructor for the snakeHead
	 */
	public Snake(GameView gameView, Bitmap bmp, MapGrid grid, boolean classicControls){
		this.gameView = gameView;
		this.classicControls = classicControls;
		defaultSpeed += gameView.getSettings().difficulty*20;
		speed = defaultSpeed;
		maxSteps = (gameView.getSettings().difficulty==0) ? 10 : ((gameView.getSettings().difficulty==1) ? 5 : 4);
		initSnake(gameView, bmp, grid);
		this.isTail = false;
		x = grid.getMapReader().getSnakePos().getX()*grid.getSquareWidth();
		y = grid.getMapReader().getSnakePos().getY()*grid.getSquareHeight();
		width = (int)grid.getSquareWidth();
		height = (int)grid.getSquareHeight();
	}
	
	/*
	 * constructor for the snakeTail
	 */
	public Snake(GameView gameView, Bitmap bmp, int tailNumber, MapGrid grid, Snake head){
		initSnake(gameView, bmp, grid);
		maxSteps = head.getMaxSteps();
		this.isTail = true;
		this.tailNumber = tailNumber;
		x = -50;
		y = -50;
		width = (int)grid.getSquareWidth();
		height = (int)grid.getSquareHeight();
	}
	
	private void initSnake(GameView gameView, Bitmap bmp, MapGrid grid){
		this.bmp = bmp;
		this.animationRectWidth = bmp.getWidth();
		this.animationRectHeight = bmp.getHeight();
		targetVector = new Vector2(1,0);
		fullTargetVector = new Vector2(1,0);
		paint = new Paint();
		paint.setAntiAlias(gameView.getSettings().antiAlias);
		paint.setFilterBitmap(gameView.getSettings().filtering);
		steps = new ArrayList<Step>();
		steps.add(new Step(-100,-100,orientation));
	}
	
	private void setFullTargetVector(Vector2 dirVector){
			if(dirVector.getLength()>50){
				fullTargetVector.setX((double)(dirVector.getX()-(x+width/2)));
				fullTargetVector.setY((double)(dirVector.getY()-(y+height/2)));
			}
	}
	
	private void addStep(){
		steps.add(new Step(x,y,orientation));
		
	}
	
	private void removeStep(){
		if(steps.size()>maxSteps){
			steps.remove(0);
		}
	}
	
	public void setPropertiesViaStep(Step step){
		this.x = step.x;
		this.y = step.y;
		this.orientation = step.orientation;
		addStep();
		removeStep();
	}
	
	public void update(){
		if(!isTail){
			if(!classicControls){
				followTarget();
				orientation = targetVector.getAngle();
			}
			x = x+targetVector.getX();
			y = y+targetVector.getY();
			addStep();
			removeStep();
		}
	}
	
	public void onDraw(Canvas canvas){
		canvas.save();
		canvas.rotate((float)(orientation+180)%360,(float)x+width/2, (float)y+height/2);
		Rect src = new Rect(0, 0, animationRectWidth, animationRectHeight);
		RectF dst = new RectF((int)x,(int)y,(int)x+width, (int)y+height);
		canvas.drawBitmap(bmp, src, dst, paint);
		canvas.restore();
	}
	
	public void changeSize(int tailLength,float previousHeight){
		if(isTail){
			float multiplier = (float)20/(float)tailLength;
			height = previousHeight-multiplier;
		}
	}
	
	
	public void setDirection(Vector2 dirVector){
		setFullTargetVector(dirVector);
		if(!isTail){
			if(classicControls)classicControls(dirVector);
			else followTarget();
		}
	}
	
	private void classicControls(Vector2 dirVector){
		if(!isTail){
			int direction = (int)(fullTargetVector.getAngle());
			if((direction<=45 || direction>315) && orientation != 180 && orientation!=0){
				targetVector.becomes(new Vector2(setTargetVectorLength(),0)); 
				orientation = 0;
			}
			else if(direction>45 && direction<=135 && orientation != 270 && orientation!=90){
				targetVector.becomes(new Vector2(0,setTargetVectorLength())); 
				orientation = 90;
			}
			else if(direction>135 && direction<=225 && orientation != 0 && orientation!=180){
				targetVector.becomes(new Vector2(-setTargetVectorLength(),0)); 
				orientation = 180;
			}
			else if(direction>225 && direction<=315 && orientation != 90 && orientation!=270){
				targetVector.becomes(new Vector2(0,-setTargetVectorLength())); 
				orientation = 270;
			}			
		}
	}
	
	private void followTarget(){
		if(!isTail){
			if(targetVector.getAngle() != fullTargetVector.getAngle()){
				targetVector.becomes(fullTargetVector.getVectorChunk(setTargetVectorLength()));
				restrictAngleOfAttack(targetVector);
			}
		}
	}
	
	private double setTargetVectorLength(){
		return ((float)speed/500.0f*gameView.getWidth())/10;
	}
	
	private void restrictAngleOfAttack(Vector2 vector){
		int addedConstraint;
		addedConstraint = (speed == 20) ?  -3 : (addedConstraint = (speed == 40) ? 2 : (speed==10) ? -6 : 8);
		double relativeOrientation = vector.getAngle()-orientation;
		if(relativeOrientation<0)
			relativeOrientation+=360;
		if(relativeOrientation<AOA_CONSTRAINT_UP-addedConstraint  && relativeOrientation>=180){
			targetVector.becomes(new Vector2((orientation+AOA_CONSTRAINT_UP-addedConstraint)%360, targetVector.getLength(),false));
		}
		else if(relativeOrientation>AOA_CONSTRAINT_DOWN+addedConstraint && relativeOrientation<180){
			targetVector.becomes(new Vector2((orientation+AOA_CONSTRAINT_DOWN+addedConstraint)%360, targetVector.getLength(),false));
		}
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public int getSpeed(){
		return speed;
	}
	
	public int getMaxSteps(){
		return maxSteps;
	}
	
	public int getDefaultSpeed(){
		return defaultSpeed;
	}
	
	public void setSpeed(int speed){
		for(int i =0; i<speed/this.speed; i++){
			removeStep();
		}
		this.speed = speed;
		targetVector.becomes(targetVector.getVectorChunk(((double)this.speed/(double)500*(double)gameView.getWidth())/(double)10));
	}
	
	public double getOrientation(){
		return orientation;
	}
	
	public int getTailNumber(){
			return tailNumber;
	}
	
	public Step getLastStep(){
		return steps.get(0);
	}
	
	public void setGraphics(boolean antiAlias, boolean filtering, Bitmap bmp){
		paint.setAntiAlias(antiAlias);
		paint.setFilterBitmap(filtering);
		this.bmp = bmp;
	}
}
