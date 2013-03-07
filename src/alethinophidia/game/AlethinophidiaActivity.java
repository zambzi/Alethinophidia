package alethinophidia.game;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;

/**
 * 
 * 
 * Main game class
 * 
 * @author £ukasz Piotrowski
 */

public class AlethinophidiaActivity extends Activity {
	
	private GameView gameView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(gameView == null){
        	gameView = new GameView(this);
        }
        setContentView(gameView);
       }

    @Override
	public void onBackPressed() {
    	gameView.onBackDown();
    }
    
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		gameView.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		gameView.onPause();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		gameView.onDestroy();
		this.finish();
	}
    
}