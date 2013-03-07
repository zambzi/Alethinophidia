package alethinophidia.utils;

import alethinophidia.game.GameView;
import alethinophidia.userInterface.Field;

/**
 * 
 * Class created just to add a little
 * eye-candy to Fields.
 * So far allows to fade (in, out) and
 * blink Fields.
 * 
 * Perhaps in future it will allow to
 * create Interval Motion Transitions
 * (so far, the slideIn method was the 
 * first try, but the rest of the code,
 * ie. other classes, where not created
 * to accommodate such behavior)
 * 
 * @author £ukasz Piotrowski
 */

public class Transitions {
	private long blinkTimer;
	private int slideTimer;
	private Field item;
	private boolean fadeInThenOutEnd;
	private boolean itemReady;
	private boolean slideEnded;
	GameView gameView;
	
	public Transitions(Field field, GameView gameView){
		this.item = field;
		blinkTimer = 0;
		slideTimer = 1;
		itemReady = false;
		fadeInThenOutEnd = false;
		slideEnded = false;
		this.gameView = gameView;
	}
	
	public void update(){
		blinkTimer++;
	}
	
	public void fadeInThenOut(int time, int length, int betweenFadesLength, int minAlpha, int maxAlpha, boolean fadeIn, boolean fadeOut){
		if(blinkTimer>=time && blinkTimer<time+length){
			if(fadeIn){
				if(item.getAlpha()<=maxAlpha){
					item.addAlpha((int)Math.floor(maxAlpha/length));
				}
			}
			else{
				item.setAlpha(maxAlpha);
			}
		}
		else if(blinkTimer>=time+length+betweenFadesLength && blinkTimer<time+length*2+betweenFadesLength){
			if(fadeOut){
				if(item.getAlpha()>=minAlpha){
					item.removeAlpha((int)Math.floor(maxAlpha/length));
				}
			}
		}
		if(blinkTimer >= time+length*2+betweenFadesLength)
			fadeInThenOutEnd = true;
	}
	
	public void blink(int time, int length, int betweenFadesLength, int minAlpha, int maxAlpha){
		fadeInThenOut(time,length, betweenFadesLength, minAlpha, maxAlpha, true, true);
		if(fadeInThenOutEnd == true){
			blinkTimer = 0;
			fadeInThenOutEnd = false;
		}
	}
	
	public void slideIn(int time, int length, Vector2 startPos, Vector2 endPos){
		if(!itemReady){
			item.setPosition(startPos);
			slideTimer = 0;
			itemReady = true;
			slideEnded = false;
		}
		if(slideTimer>=time && slideTimer<time+length){
			Vector2 end = new Vector2(endPos.getX()*gameView.getWidth(), endPos.getY()*gameView.getHeight());
			Vector2 tmp = endPos.getVectorChunk(end.getLength()/length);
			item.setPosition2(new Vector2(tmp.getX()+item.getPosition().getX(),tmp.getY()+item.getPosition().getY()));
		}
		if(slideTimer < time+length){
			slideTimer++;
		}
		else
			slideEnded = true;
	}
	
	/*private double smoothOut(double max, double min, int steps, int time){
		double v = (double)time/steps;
		v = 1-(1-v)*(1-v);
		return (min*v)+(max*(1-v));
	}*/
	
	public boolean isFadeFinished(){
		return fadeInThenOutEnd;
	}
	
	public boolean isSlideOver(){
		return slideEnded;
	}
	
}
