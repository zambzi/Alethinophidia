package alethinophidia.utils;


/**
 * 
 * Simple euclidean vector representation
 * Used by most classes to hold coordinates
 * and do geometric calculations.
 * 
 * @author £ukasz Piotrowski
 */

public class Vector2 {
	private double x;
	private double y;
	
	/*
	 * empty vector
	 */
	public Vector2(){
		x=0;
		y=0;
	}
	
	/*
	 * Vector equal to vector2
	 */
	public Vector2(Vector2 vector2){
		becomes(vector2);
	}
	
	/*
	 * Vector of given length and angle
	 */
	public Vector2(double angle,  double length, boolean none){
		if(angle>360)
			angle -= 360;
		if(angle<0)
			angle += 360;
		
		Vector2 tmpVector = new Vector2(length, 0);
		tmpVector.becomes(new Vector2(tmpVector, (double)angle));
		x=tmpVector.getX();
		y=tmpVector.getY();
	}
	
	/*
	 * Creates copy of vector2 rotated by given angle
	 */
	public Vector2(Vector2 vector2, double angle){
		if(angle>360)
			angle -= 360;
		if(angle<0)
			angle += 360;

		x = vector2.getX()*Math.cos(Math.toRadians(angle))-vector2.getY()*Math.sin(Math.toRadians(angle));
		y = vector2.getX()*Math.sin(Math.toRadians(angle))+vector2.getY()*Math.cos(Math.toRadians(angle));
	}
	
	
	/*
	 * creates copy of vector2 rotated by given angle
	 * with rotation centered around center vector
	 */
	public Vector2(Vector2 vector2, double angle, Vector2 center){
		if(angle>360)
			angle -= 360;
		if(angle<0)
			angle += 360;
		
		x = (vector2.getX()-center.getX())*Math.cos(Math.toRadians(angle))-(vector2.getY()-center.getY())*Math.sin(Math.toRadians(angle))+center.getX();
		y = (vector2.getX()-center.getX())*Math.sin(Math.toRadians(angle))+(vector2.getY()-center.getY())*Math.cos(Math.toRadians(angle))+center.getY();
	}
	
	/*
	 * Vector with _x and _y coordinates
	 */
	public Vector2(double _x, double _y){
		x=_x;
		y=_y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setX(double value){
		x=value;
	}
	
	public void setY(double value){
		y=value;
	}
	
	/*returns normalized Vector*/
	public Vector2 getNormalizedVector(){
		return new Vector2(x/getLength(), y/getLength());
	}
	
	/*returns length of a vector*/
	public double getLength(){
		return Math.sqrt((x*x)+(y*y));
	}
	
	/*returns angle of a vector*/
	public double getAngle(){
		Vector2 tmpVector = new Vector2(getNormalizedVector());
		double angle =  Math.atan2(tmpVector.getY(), tmpVector.getX());
		return (angle > 0 ? angle : (2*Math.PI+angle)) * 360/(2*Math.PI);
	}
	
	/*
	 * returns vector of the same orientation
	 * and given length
	 */
	public Vector2 getVectorChunk(double length){
		Vector2 tmpVector = new Vector2(getNormalizedVector());
		tmpVector.setX(tmpVector.getX()*length);
		tmpVector.setY(tmpVector.getY()*length);
		return tmpVector;
	}
	
	/*
	 * adds two vectors.
	 * returns Cross Product in form of 
	 * another vector
	 */
	public Vector2 add(Vector2 vector2){
		return new Vector2(x+vector2.getX(), y+vector2.getY());
	}
	
	public Vector2 substract(Vector2 vector2){
		return new Vector2(x-vector2.getX(), y-vector2.getY());
	}
	
	/*
	 * gives actual vector attributes from
	 * other vector2
	 */
	public void becomes(Vector2 vector2){
		x = vector2.getX();
		y = vector2.getY();
	}
}
