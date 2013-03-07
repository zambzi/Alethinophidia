package alethinophidia.utils;

import java.util.List;
import alethinophidia.game.GameView;
import alethinophidia.map.MapGrid;
import alethinophidia.pawns.Snake;
import alethinophidia.utils.Vector2;

/**
 * 
 * simple solver that checks if snake head
 * hits walls/items/tails in actual moment.
 * 
 * @author £ukasz Piotrowski
 */

public class CollisionSolver {
	private MapGrid grid;
	private Snake snakeHead;
	private Vector2 snakeCollisionPoint[];
	private List<Snake> snakeTails;
	private List<Vector2> map;
	
	public static final int COL_WALL = 0;
	public static final int COL_BORDER = 1;
	public static final int COL_TAIL = 2;
	public static final int NO_COLLISION = -1;
	
	public CollisionSolver(GameView gameView, MapGrid grid, Snake snakeHead, List<Snake> snakeTails){
		this.snakeTails = snakeTails;
		this.grid = grid;
		map = grid.getMapReader().getMap();
		this.snakeHead = snakeHead;
		snakeCollisionPoint = new Vector2[2];
	}
	
	public int testCollision(){
		updateSnakeCollisionPoints();
		if(snakeToWallCollision())
			return COL_WALL;
		else if(snakeToBorderCollision())
			return COL_BORDER;
		else if(snakeToSnakeCollision())
			return COL_TAIL;
		return NO_COLLISION;
	}
	
	public boolean testItemCollision(){
		return true;
	}
	
	private void updateSnakeCollisionPoints(){
		snakeCollisionPoint[0] = new Vector2(snakeHead.getX()+snakeHead.getWidth(), snakeHead.getY());
		snakeCollisionPoint[0].becomes(new Vector2(snakeCollisionPoint[0], snakeHead.getOrientation(), new Vector2(snakeHead.getX()+snakeHead.getWidth()/2, snakeHead.getY()+snakeHead.getHeight()/2)));
		snakeCollisionPoint[1] = new Vector2(snakeHead.getX()+snakeHead.getWidth(), snakeHead.getY()+snakeHead.getHeight());
		snakeCollisionPoint[1].becomes(new Vector2(snakeCollisionPoint[1], snakeHead.getOrientation(), new Vector2(snakeHead.getX()+snakeHead.getWidth()/2, snakeHead.getY()+snakeHead.getHeight()/2)));
	}
	
	private boolean snakeToSnakeCollision(){
		for(int i = 0; i<snakeTails.size(); i++){
			Vector2 vectorToPoint = new Vector2(snakeCollisionPoint[0].getX(),snakeCollisionPoint[0].getY());
			Vector2 vectorToTailCenter = new Vector2(snakeTails.get(i).getX()+snakeTails.get(i).getWidth()/2, snakeTails.get(i).getY()+snakeTails.get(i).getHeight()/2);
			Vector2 headToTailVector = new Vector2(vectorToPoint.substract(vectorToTailCenter));
			if(headToTailVector.getLength()<=snakeTails.get(i).getHeight()/2){
				return true;
			}
			
			vectorToPoint.becomes(new Vector2(snakeCollisionPoint[1].getX(),snakeCollisionPoint[1].getY()));
			headToTailVector.becomes(new Vector2(vectorToPoint.substract(vectorToTailCenter)));
			if(headToTailVector.getLength()<=snakeTails.get(i).getHeight()/2){
				return true;
			}
		}
		return false;
	}
	
	private boolean snakeToWallCollision(){
		for(int i = 0; i<map.size(); i++){
			if((snakeCollisionPoint[0].getX()>map.get(i).getX()*grid.getSquareWidth() && 
				snakeCollisionPoint[0].getY()>map.get(i).getY()*grid.getSquareHeight()) && 	
				(snakeCollisionPoint[0].getX()<map.get(i).getX()*grid.getSquareWidth()+grid.getSquareWidth() && 
				snakeCollisionPoint[0].getY()<map.get(i).getY()*grid.getSquareHeight()+grid.getSquareHeight())){
				return true;
			}
			if((snakeCollisionPoint[1].getX()>map.get(i).getX()*grid.getSquareWidth() && 
				snakeCollisionPoint[1].getY()>map.get(i).getY()*grid.getSquareHeight()) && 	
				(snakeCollisionPoint[1].getX()<map.get(i).getX()*grid.getSquareWidth()+grid.getSquareWidth() && 
				snakeCollisionPoint[1].getY()<map.get(i).getY()*grid.getSquareHeight()+grid.getSquareHeight())){
				return true;
			}
		}
		return false;
	}
	
	public boolean snakeToItemCollision(Vector2 itemLocation){
		if((snakeCollisionPoint[0].getX()>itemLocation.getX() && 
				snakeCollisionPoint[0].getY()>itemLocation.getY()) && 	
				(snakeCollisionPoint[0].getX()<itemLocation.getX()+grid.getSquareWidth() && 
				snakeCollisionPoint[0].getY()<itemLocation.getY()+grid.getSquareHeight())){
				return true;
			}
			if((snakeCollisionPoint[1].getX()>itemLocation.getX() && 
				snakeCollisionPoint[1].getY()>itemLocation.getY()) && 	
				(snakeCollisionPoint[1].getX()<itemLocation.getX()+grid.getSquareWidth() && 
				snakeCollisionPoint[1].getY()<itemLocation.getY()+grid.getSquareHeight())){
				return true;
			}
		return false;
	}
	
	private boolean snakeToBorderCollision(){
		if((snakeCollisionPoint[0].getX()<0 || snakeCollisionPoint[0].getY()<0) ||
			(snakeCollisionPoint[0].getX()>grid.getWidth() || snakeCollisionPoint[0].getY()>grid.getHeight())){
			return true;
		}
		if((snakeCollisionPoint[1].getX()<0 || snakeCollisionPoint[1].getY()<0) ||
			(snakeCollisionPoint[1].getX()>grid.getWidth() || snakeCollisionPoint[1].getY()>grid.getHeight())){
			return true;
		}
		return false;
	}
}
