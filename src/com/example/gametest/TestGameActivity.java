package com.example.gametest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2013 Victor Martinez
 * 
 * Minijuego sobre un coche con control analogico 
 * para ser agregado al (c) Canigotchi
 * 
 */

public class TestGameActivity extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mRoadTextureAtlas;
	private BitmapTextureAtlas mCarTextureAtlas;
	private BitmapTextureAtlas mOnScreenControlAtlas;
	
	private Camera mCamera;

	private Scene mScene;

	protected ITiledTextureRegion mRoadTextureRegion;
	protected ITiledTextureRegion mCarTextureRegion;
	protected ITextureRegion mOnScreenControlBaseTextureRegion;
	protected ITextureRegion mOnScreenControlKnobTextureRegion;
	
	private Body mCarBody;
	
	private PhysicsHandler mCarPhysicsHandler;
	
	protected PhysicsWorld mPhysicsWorld;
	
	private Sprite mCar;
	
	private Vector2 mGravity;
	
	private AccelerationData mAccelerationData;

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
		/* Toast.makeText(this, "Toca la pantalla para hacer saltar la bola",
				Toast.LENGTH_LONG).show(); */

		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED	,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/** Texturas Carretera */
		this.mRoadTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 406, 42, TextureOptions.BILINEAR);
		this.mRoadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mRoadTextureAtlas, this,"carretera-232px-bordes.png", 0, 0, 7, 1);
		this.mRoadTextureAtlas.load();
		
		/** Texturas Coche */
		this.mCarTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 12, 26, TextureOptions.BILINEAR);
		this.mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mCarTextureAtlas, this, "coche.png", 0, 0, 1, 1);
		this.mCarTextureAtlas.load();
		
		/** Texturas Control Analogico (de momento no lo uso)*/
		this.mOnScreenControlAtlas= new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlAtlas, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlAtlas, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlAtlas.load();
		
		

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(1,1,1));
		//this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		
		float centerXRoad = (CAMERA_WIDTH - mRoadTextureRegion.getWidth()) / 2;   
		float centerYRoad = (CAMERA_HEIGHT - mRoadTextureRegion.getHeight()) / 2;
	
		float centerXCar = (CAMERA_WIDTH - mCarTextureRegion.getWidth()) / 2;   
		float centerYCar = (CAMERA_HEIGHT - mCarTextureRegion.getHeight()) / 2;
		
		//float centerXScreenControl = (CAMERA_WIDTH - mOnScreenControlBaseTextureRegion.getWidth() * 1.75f) / 2; 
		
		this.addLimits();
		
		this.addRoad(centerXRoad, centerYRoad);
		this.addRoad(centerXRoad, centerYRoad - 42*4); //tamaño * escala
		this.addRoad(centerXRoad, centerYRoad + 42*4);
		this.addRoad(centerXRoad, centerYRoad + 2*(42*4));
		this.addRoad(centerXRoad, centerYRoad - 2*(42*4)); //rellenamos toda la pantalla con carretera
		
		this.addCar(centerXCar, centerYCar);
		
		//this.addAnalogScreenControl(centerXScreenControl, CAMERA_HEIGHT - mOnScreenControlBaseTextureRegion.getHeight());
		//mOnScreenControlBaseTextureRegion.getHeight() - CAMERA_HEIGHT
		
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		return this.mScene;
	}

	
	
	/** No lo uso de momento */
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		/*if (this.mPhysicsWorld != null) {
			//if (pSceneTouchEvent.isActionDown() ) {
			
				mGravity.set(mGravity.x, -3f);
				this.mPhysicsWorld.setGravity(mGravity);
				//Vector2Pool.recycle(mGravity);
				
				Debug.d("touch");
				
				return true;
			//}
		}*/
		return false;
	}

	/** No lo uso de momento */
	
	@Override
	public void onAccelerationAccuracyChanged(
			final AccelerationData pAccelerationData) {

	}

	
	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		
		/*mGravity = Vector2Pool.obtain(pAccelerationData.getX() * 5, 0);// no muevo en Y
		
		this.mPhysicsWorld.setGravity(mGravity);
		Vector2Pool.recycle(mGravity);*/
		
		/** Cuando cambia la aceleracion en el eje X movemos el sprite del coche con un impulso lineal */
		
		mAccelerationData = pAccelerationData;
		
		final Vector2 velocity = Vector2Pool.obtain(pAccelerationData.getX() * 2.5f, 0);
		mCarBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
		
		/*final float rotationInRad = (float) MathUtils.degToRad(pAccelerationData.getX()*5);
		mCarBody.setTransform(mCarBody.getWorldCenter(), rotationInRad);
		
		mCar.setRotation(pAccelerationData.getX() * 5);*/
		
		
		
		}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	
	// ===========================================================
	// Methods
	// ===========================================================
	
	
	private void addRoad(float pX, float pY) {
		
		AnimatedSprite Road = new AnimatedSprite(pX, pY, mRoadTextureRegion, getVertexBufferObjectManager());
		Road.setScale(4);
		Road.animate(10); //dependera del coche (parado, mas tiempo corriendo)
		
		this.mScene.attachChild(Road);
		Debug.d("" + mScene.getChildCount());
	
	}
	
	private void addCar(float pX, float pY) {
		
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.2f, 0.2f);
		
		mCar = new Sprite(pX, pY, mCarTextureRegion, getVertexBufferObjectManager()); // 12 x 26 px
		mCar.setScale(3);
		mCarBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mCar, BodyType.DynamicBody, objectFixtureDef);
		mCarBody.setLinearDamping(1.5f); //mas suavidad
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mCar, mCarBody, true, false));
		mCar.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				/** Rotamos el coche en proporcion a la velocidad de moviento en eje X <- -> 
				 * 	Si roto Body da problemas con los limites de la pantalla y el sprite pasa */
				
				/*final float rotationInRad = (float) MathUtils.degToRad(mAccelerationData.getX()*5);
				mCarBody.setTransform(mCarBody.getWorldCenter(), rotationInRad);*/
				
				mCar.setRotation(mAccelerationData.getX() * 5);
				
			}

			@Override
			public void reset() {
				
			}
			
		});
		
		
		this.mScene.attachChild(mCar);
		
		
		
	}
	
	/** No lo uso de momento */
	private void addAnalogScreenControl(float pX, float pY) {
		
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(pX, pY, this.mCamera, this.mOnScreenControlBaseTextureRegion, 
				this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			
					@Override
					public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
						
						final Body carBody = mCarBody;
						final Vector2 velocity = Vector2Pool.obtain(pValueX*5, pValueY*5);
						carBody.setLinearVelocity(velocity);
						Vector2Pool.recycle(velocity);
						
						final float rotationInRad = (float) Math.atan2(pValueX, -pValueY);
						carBody.setTransform(carBody.getWorldCenter(), rotationInRad);
						
						mCar.setRotation(MathUtils.radToDeg(rotationInRad));
						
					}
					
					@Override
					public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
						// TODO Auto-generated method stub
						
					}
				});
		
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.75f);
		analogOnScreenControl.getControlKnob().setScale(1.75f);
		analogOnScreenControl.refreshControlKnobPosition();
		
		mScene.setChildScene(analogOnScreenControl);
		
		
			
		}	
	
	
	private void addLimits() {
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2,	vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
