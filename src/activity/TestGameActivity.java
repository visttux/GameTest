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
import org.andengine.input.touch.detector.SurfaceGestureDetectorAdapter;
import org.andengine.ui.activity.BaseGameActivity;

import resources.ResourcesManager;
import scene.GameScene;
import scene.SceneManager;
import scene.SceneManager.SceneType;
import android.os.Bundle;
import android.view.KeyEvent;
import entity.Canon;

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
	
	private Canon mCanon;
	
	private Camera mCamera;	
	protected PhysicsWorld mPhysicsWorld;
	public SurfaceGestureDetectorAdapter surfaceGestureDetector;

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
	protected void onCreate(Bundle pSavedInstanceState) {
		
		surfaceGestureDetector = new SurfaceGestureDetectorAdapter(this) {
			@Override
			protected boolean onSwipeUp() {
				((GameScene) SceneManager.getInstance().mGameScene).throwCoins();
				return false;
			}
			
			@Override
			protected boolean onSwipeRight() {
				mCanon.moveRight();
				return false;
			}
			
			@Override
			protected boolean onSwipeLeft() {
				mCanon.moveLeft();
				return false;
			}
			
			@Override
			protected boolean onSwipeDown() {
				((GameScene) SceneManager.getInstance().mGameScene).pickCoins();
				return false;
			}
			
			@Override
			protected boolean onSingleTap() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean onDoubleTap() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		surfaceGestureDetector.setEnabled(true);
		
		super.onCreate(pSavedInstanceState);
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
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
				//TODO elegir menu+levels o solo levels
				SceneManager.getInstance().createLevelsScene();
			}
		}));
		/** Saltar al ciclo de la activity de nuevo*/
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public void onResumeGame() {
		
		super.onResumeGame();
		if(SceneManager.getInstance().getCurrentSceneType().equals(SceneType.SCENE_GAME))
		{
			this.enableAccelerationSensor(mCanon)  ;
			SceneManager.getInstance().mGameScene.setOnSceneTouchListener(surfaceGestureDetector);
		}
		
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	public void setCanon(Canon pCanon)
	{
		mCanon = pCanon;
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
