package alethinophidia.utils;

import java.io.Serializable;
import alethinophidia.game.GamePlayGenerator;

/**
 * 
 * Holds scores in form of four
 * instances of nested classes:
 * ScoreTailDifficulty and
 * TimeDifficultyDeaths,
 * for each game mode.
 * 
 * @author £ukasz Piotrowski
 */

public class HiScores implements Serializable{
	private static final long serialVersionUID = 1L;
	public ScoreTailDifficulty survival[];
	public ScoreTailDifficulty classic[];
	public ScoreTailDifficulty free[];
	public TimeDifficultyDeaths certainDeath[];
		
	public boolean saveable;
	
	public class ScoreTailDifficulty implements Serializable{
		private static final long serialVersionUID = 1L;
		public int score;
		public int tail;
		public int difficulty;
		public int level;
		
		
		public void emptyScore(){
			score = 0;
			tail = 0;
			difficulty = 0;
			level = 0;
			emptyScore();
		}
	}
	
	public class TimeDifficultyDeaths implements Serializable{
		private static final long serialVersionUID = 1L;
		public int time[];
		public int difficulty;
		public String death;
		
		public TimeDifficultyDeaths(){
			time = new int[3];
			emptyScore();
		}
		
		public void emptyScore(){
			time[0] = 0;
			time[1] = 0;
			time[2] = 0;
			difficulty = 0;
			death = "unknown";
		}
	}
	
	public HiScores(){
		survival = new ScoreTailDifficulty[5];
		classic = new ScoreTailDifficulty[5];
		free = new ScoreTailDifficulty[5];
		certainDeath = new TimeDifficultyDeaths[5];
		for(int i = 0; i<5; i++){
			survival[i] = new ScoreTailDifficulty();
			classic[i] = new ScoreTailDifficulty();
			free[i] = new ScoreTailDifficulty();
			certainDeath[i] = new TimeDifficultyDeaths();
		}
		saveable = true;
	}
	
	public int setNewScore(int gameMode, int score, int tail, int level, int difficulty, int time[], String death){
		 int place = -1;
		switch(gameMode){
		case GamePlayGenerator.MODE_SURVIVAL : place = getPlace(score, survival);
												if(place!=-1){
													moveScoresDown(place,survival);
													survival[place].score = score;
													survival[place].difficulty = difficulty;
													survival[place].tail = tail;
													survival[place].level = level;
												} break;
		case GamePlayGenerator.MODE_CLASSIC : place = getPlace(score, classic);
												if(place!=-1){
													moveScoresDown(place, classic);
													 classic[place].score = score;
													 classic[place].difficulty = difficulty;
													 classic[place].tail = tail;
												} break;
		case GamePlayGenerator.MODE_FREE : place = getPlace(score, free);
												if(place!=-1){
													moveScoresDown(place, free);
													free[place].score = score;
													free[place].difficulty = difficulty;
													free[place].tail = tail;
												} break;
		case GamePlayGenerator.MODE_CERTAIN_DEATH : place = getPlace(time, certainDeath);
													if(place!=-1){
														moveScoresDown(place, certainDeath);
														certainDeath[place].time = time;
														certainDeath[place].difficulty = difficulty;
														certainDeath[place].death = death;
													} break;
		}
		return place;
	}
	
	private int getPlace(int score, ScoreTailDifficulty scores[]){
		for(int i = 0; i<5; i++){
			if(scores[i].score<score)
				return i;
		}
		return -1;
	}
	
	private void moveScoresDown(int place, ScoreTailDifficulty scores[]){
		for(int i=4; i>=place+1; i--){
			scores[i].score=scores[i-1].score;
			scores[i].difficulty=scores[i-1].difficulty;
			scores[i].tail=scores[i-1].tail;
			scores[i].level=scores[i-1].level;
		}
	}
	
	private int getPlace(int time[], TimeDifficultyDeaths times[]){
		for(int i = 0; i<5; i++){
			if(times[i].time[0]<time[0])
				return i;
			else if(times[i].time[0]==time[0] && times[i].time[1]<time[1])
				return i;
			else if(times[i].time[0]==time[0] && times[i].time[1]==time[1] && times[i].time[2]<time[2])
				return i;
		}
		return -1;
	}
	
	private void moveScoresDown(int place, TimeDifficultyDeaths times[]){
		for(int i=4; i>=place+1; i--){
			times[i].time = times[i-1].time;
			times[i].difficulty = times[i-1].difficulty;
			times[i].death = times[i-1].death;
		}
	}
}