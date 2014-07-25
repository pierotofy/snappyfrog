package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import tv.ouya.console.api.OuyaIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OuyaServices extends BroadcastReceiver implements PlatformServices, ControllerListener {
	private Array<Controller> controllers = new Array<Controller>();
	private boolean gamePadDown = false;
	
	public OuyaServices(){

	}
	
	@Override
	public void initGamePadControllers() {
		for(Controller controller: Controllers.getControllers()) {
			   controllers.add(controller);
		}	
		
		Controllers.addListener(this);
	}
	
	@Override
	public void connected(Controller arg0) {
		controllers.add(arg0);
	}
	
	@Override
	public void disconnected(Controller arg0) {
//		int index = -1;
//		for (int i = 0; i < controllers.size; i++){
//			if (controllers.get(i).getName().equals(arg0.getName())){
//				index = i;
//				break;
//			}
//		}
//		
//		if (index != -1){
//			controllers.removeIndex(index);
//		}
		
		controllers.removeValue(arg0, true);
		
		if (controllers.size == 0){
			// Pause game or notify disconnect
			LevelScreen.paused = true;
		}
	}
	
	@Override
	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller arg0, int arg1, float arg2) {
		return false;
	}
	
	@Override
	public boolean buttonDown(Controller arg0, int arg1) {
		// Don't let multiple people interfer with the gameplay
		if (controllers.size > 0 && controllers.get(0) == arg0){
			gamePadDown = true;
		}
		return true;
	}
	
	@Override
	public boolean buttonUp(Controller arg0, int arg1) {
		if (controllers.size > 0 && controllers.get(0) == arg0){
			gamePadDown = false;
		}
		return true;
	}
	
	@Override
	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2) {
		return false;
	}
	
	@Override
	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) {
		return false;
	}
	
	@Override
	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) {
		return false;
	}
	
	@Override
	public boolean isGamePadButtonPressed() {
		return gamePadDown;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(OuyaIntent.ACTION_MENUAPPEARING)) {
			Game.getSingleton().pause();
        }
	}
	
	@Override
	public boolean isSharingAvailable() {
		return false;
	}

	@Override
	public boolean shareCurrentScreen() {
		return false;
	}
	
	@Override
	public boolean willResumeAfterShare() {
		return true;
	}
	
	@Override
	public boolean delayHint() {
		return true;
	}
	
	@Override
	public boolean supportsFreetype() {
		return true;
	}

}
