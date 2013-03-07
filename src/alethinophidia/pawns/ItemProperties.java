package alethinophidia.pawns;

/**
 * 
 * It's basically a structure to hold item
 * specific properties such as:
 * length of tail to be extended by eating
 * an item;
 * health addition or loss;
 * score bonus added after eating item;
 * speed up value for items like mushroom,
 * mouse or energy Drink;
 * 
 * Whenever snake eats item, game asks ItemProperties
 * what properties it will bestow upon poor creature.
 * 
 * @author £ukasz Piotrowski
 */

public class ItemProperties {
	private int tailExtensionLength;
	private int healthAddition;
	private String itemName;
	private int speedUp;
	private int scoreBonus;
	
	public ItemProperties(String itemName){
		this.itemName = itemName;
		tailExtensionLength = 0;
		healthAddition = 0;
		speedUp = 0;
		
		if(itemName == "apple"){
			tailExtensionLength = 1;
			healthAddition = 1;
			scoreBonus = 10;
		}
		else if(itemName == "orange"){
			tailExtensionLength = 2;
			healthAddition = 1;
			scoreBonus = 20;
		}
		else if(itemName == "shrooms"){
			speedUp = 20;
			tailExtensionLength = 2;
			healthAddition = 2;
			scoreBonus = 50;
		}
		else if(itemName == "rottenFruit"){
			tailExtensionLength = 1;
			healthAddition = -1;
			scoreBonus = 20;
		}
		else if(itemName == "mouse"){
			speedUp = -10;
			tailExtensionLength = 3;
			healthAddition = 2;
			scoreBonus = 70;
		}
		else if(itemName == "quebab"){
			tailExtensionLength = 4;
			healthAddition = 4;
			scoreBonus = 100;
		}
		else if(itemName == "trap"){
			tailExtensionLength = -7;
			healthAddition = -2;
			scoreBonus = 100;
		}
		else if(itemName == "energyDrink"){
			tailExtensionLength = 1;
			speedUp = 40;
			scoreBonus = 5;
		}
	}
	
	public int getTailExtensionLength(){
		return tailExtensionLength;
	}
	
	public int getSpeedUp(){
		return speedUp;
	}
	
	public int getScore (){
		return scoreBonus;
	}
	
	public int getHealthAddition(){
		return healthAddition;
	}
	
	public String getItemName(){
		return itemName;
	}
}
