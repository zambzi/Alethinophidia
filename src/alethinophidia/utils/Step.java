package alethinophidia.utils;

/**
 * 
 * Simple class used to hold snake tail
 * informations.
 * 
 * @author £ukasz Piotrowski
 */

public class Step {
	public double x;
	public double y;
	public double orientation;
	
	public Step(double x, double y, double orientation){
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}
}
