package com.example.gametest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.math.MathUtils;

import android.graphics.Color;

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
		IAccelerationListener {
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
	private BitmapTextureAtlas mFuelTextureAtlas;
	
	private Camera mCamera;
	private Scene mScene;

	protected ITiledTextureRegion mRoadTextureRegion;
	protected ITiledTextureRegion mCarTextureRegion;
	protected ITiledTextureRegion mFuelTextureRegion;
	
	private Sprite mCar;
	private Body mCarBody;
	
	protected PhysicsWorld mPhysicsWorld;
	private AccelerationData mAccelerationData;
	
	private HUD hud = new HUD();
	private Font mFont;
	
	private int mFuelPoints = 100;
	private Text mScoreText;

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
		
		/** Paths en assets de los distintos recursos (imagenes, sonido, texto) */
		FontFactory.setAssetBasePath("font/");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		/** Fuente Fuel*/ 
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, TextureOptions.BILINEAR, this.getAssets(), "Plok.ttf", 32, true, Color.WHITE);
		this.mFont.load();
	
		/** Texturas Carretera */
		this.mRoadTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 800, TextureOptions.BILINEAR);
		this.mRoadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mRoadTextureAtlas, this,"carretera-480-800.png", 0, 0, 2, 1);
		this.mRoadTextureAtlas.load();
		
		/** Texturas Coche */
		this.mCarTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 12, 26, TextureOptions.BILINEAR);
		this.mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mCarTextureAtlas, this, "coche.png", 0, 0, 1, 1);
		this.mCarTextureAtlas.load();
		
		/** Texturas Fuel */
		this.mFuelTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 16, 22);
		this.mFuelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mFuelTextureAtlas, this, "fuel.png", 0, 0,1,1);
		this.mFuelTextureAtlas.load();
		
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		/** Añado el hud con el texto (Fuel: 100%) */
		this.mScoreText = new Text(100, 0, this.mFont, "Fuel: 100%", "Fuel: XXX%".length(), this.getVertexBufferObjectManager());
		hud.attachChild(this.mScoreText);
		mCamera.setHUD(hud);
		
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0,0,0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		
		float centerXRoad = (CAMERA_WIDTH - mRoadTextureRegion.getWidth()) / 2;   
		float centerYRoad = (CAMERA_HEIGHT - mRoadTextureRegion.getHeight()) / 2;
		float centerXCar = (CAMERA_WIDTH - mCarTextureRegion.getWidth()) / 2;   
		float centerYCar = (CAMERA_HEIGHT - mCarTextureRegion.getHeight()) / 2;
		
		this.addLimits();
		this.addRoad(centerXRoad, centerYRoad);	
		this.addCar(centerXCar, centerYCar);
		
		/** Cada segundo miramos si generamos o no un objeto fuel (1/3 posibilidades) y restamos 1% del fuel acumulado*/
		this.mScene.registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {	
				if(mFuelPoints > 0) {
					mFuelPoints--;
					mScoreText.setText("Fuel: " + mFuelPoints + "%");
				}
				final int random = MathUtils.random(0, 2);
				if(random == 2) {
					/** Añadimos el fuel en una posicion aleatoria X (<->) entre 40 y 400 */
					addFuel( MathUtils.random(40,400));
				}							
			}
		}));
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		return this.mScene;
	}

	/** No lo uso */
	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {
		/* NOTHING */
	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		
		/** Cuando cambia la aceleracion en el eje X movemos el "body" del coche con un impulso lineal */
		mAccelerationData = pAccelerationData;
		final Vector2 velocity = Vector2Pool.obtain(pAccelerationData.getX() * 2.5f, 0);
		mCarBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
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
		Road.animate(60); //dependera del coche (parado, mas tiempo corriendo)
		this.mScene.attachChild(Road);
	}
	
	private void addCar(float pX, final float pY) {
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		mCar = new Sprite(pX, pY, mCarTextureRegion, getVertexBufferObjectManager()) ; // 12 x 26 px
		mCar.setScale(3);
		mCarBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mCar, BodyType.DynamicBody, objectFixtureDef);
		mCarBody.setLinearDamping(1.5f); //mas suavidad
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mCar, mCarBody, true, true));
		
		mCar.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				/** Rotamos el coche en proporcion a la velocidad de moviento en eje X <- -> 
				 * 	Si roto Body da problemas con los limites de la pantalla y el sprite pasa */
				/*final float rotationInRad = (float) MathUtils.degToRad(mAccelerationData.getX()*5);
				mCarBody.setTransform(mCarBody.getWorldCenter(), rotationInRad);*/
				if(!(-0.5f < mAccelerationData.getX() && mAccelerationData.getX() < 0.5f)) {
					mCar.setRotation(mAccelerationData.getX() * 5);
				} else {
					mCar.setRotation(0);
				}
				
				/** si al chocar se mueve el coche en altura lo devolvemos a la posicion inicial */
				if(mCar.getY() != pY) {
					mCar.setY(pY);
				}
			}
			@Override
			public void reset() {
				/* NOTHING */
			}
		});
		
		this.mScene.attachChild(mCar);	
	}
	
	private void addFuel(final int positionX) {
		final Vector2 velocity;
		final Sprite fuel;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(0, 0f, 0f);
		
		fuel = new Sprite(positionX, 0, mFuelTextureRegion, getVertexBufferObjectManager());
		velocity = Vector2Pool.obtain(0, 10);
		fuel.setScale(1.5f);
		
		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, fuel, BodyType.DynamicBody, objectFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(fuel,body,true,false));
		body.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
		
		fuel.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
				/* NOTHING */
			}
			@Override
			public void onUpdate(float pSecondsElapsed) {
				/** Miramos si colisiona con el coche (o choca y se mueve en X) para aumentar el combustible y hacerlo desaparecer */
				if(fuel.collidesWith(mCar) ||  fuel.getX() != positionX) {
					if(mFuelPoints < 95) {
						mFuelPoints += 5;
					} else {
						mFuelPoints = 100;
					}
					mScoreText.setText("Fuel: " + mFuelPoints + "%");
					body.setActive(false);
					fuel.setVisible(false);
					fuel.setIgnoreUpdate(true);
					fuel.clearEntityModifiers();
					fuel.clearUpdateHandlers();
				}
			}
		});
		
		this.mScene.attachChild(fuel);
	}
	
	/** No ponemos suelo/techo para permitir salir los barriles/coches 
	 *  Como el coche es kinematic (masa 0) no choca contra los lados */
	private void addLimits() {
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle left = new Rectangle(40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
