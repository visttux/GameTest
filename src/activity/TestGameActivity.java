package activity;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.ui.activity.BaseGameActivity;

import resources.ResourcesManager;
import scene.SceneManager;


import android.view.KeyEvent;

/**
 * (c) 2013 Victor Martinez
 * 
 * Minijuego sobre un coche contralado con el accelerometro
 * 
 */

public class TestGameActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;

	// ===========================================================
	// Fields
	// ===========================================================	
	
	
	private Camera mCamera;	
	protected PhysicsWorld mPhysicsWorld;


	// ===========================================================
	// Constructors
	// ===========================================================

	/** En el constructor le pasaremos parametros tipo color del coche, mejoras (velocidad,etc) */ 
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
	    return engineOptions; 
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
//		ResourcesManager.getInstance().loadGameResources();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {

		
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}
	
	/**  Los siguientes dos overrides son para manejar bien los eventos de pulsar BACK y destruir la aplicacion*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}
	
	@Override
	protected void onDestroy() {
		System.exit(0);
		super.onDestroy();
	}

	@Override
	public void onPopulateScene(Scene pScene,OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
		{
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		/** Saltar al ciclo de la activity de nuevo*/
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
//		this.enableAccelerationSensor(mCar);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
//		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
