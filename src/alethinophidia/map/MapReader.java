package alethinophidia.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import alethinophidia.utils.Vector2;
import android.content.Context;

/**
 * 
 * Basically it's a holder for the positions
 * of all walls and starting snake location.
 * 
 * map is read from file in the /assets/maps
 * folder inside apk file
 * 
 * it doesn't really matter if map have .ale
 * extension. Just added it for fun.
 * 
 * @author £ukasz Piotrowski
 */

public class MapReader {
	private List<Vector2> walls;
	private Vector2 snakePos;
	
	public MapReader(Context context, String mapName){
		walls = new ArrayList<Vector2>();
		readMapFromFile(context, mapName);
	}
	
	private void readMapFromFile(Context context, String mapName){
		try{
			InputStream input = context.getAssets().open("Maps/"+mapName);
			InputStreamReader inputReader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			
			try{
				for(int x = 0; x<MapGrid.ROWS; x++){
					for(int y = 0; y<=MapGrid.COLUMNS+1; y++){
						int token = bufferedReader.read();
						if(token == 'w'){
							walls.add(new Vector2(y,x));
						}
						else if(token == 's'){
							snakePos = new Vector2(y,x);
						}
						
						if(token != '\n' && y==MapGrid.COLUMNS+1){
							
							break; 
						}
					}
				}
				input.close();
			} catch (IOException e) {
			}
		} catch (android.content.res.Resources.NotFoundException e){
				walls.clear();
		} catch (IOException e) {
				walls.clear();
		}
	}

	public boolean checkIfEmpty(){
		return walls.isEmpty();
	}
	
	public List<Vector2> getMap(){
		return walls;
	}
	public Vector2 getSnakePos(){
		return snakePos;
	}
}
