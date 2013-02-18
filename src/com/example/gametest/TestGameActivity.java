package com.example.gametest;

import java.util.Vector;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
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
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * (c) 2013 Victor Martinez
 * 
 * Minijuego sobre un coche contralado con el accelerometro
 * 
 */

public class TestGameActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;

	// ===========================================================
	// Fields
	// ===========================================================	
	
	private PlayerCar mCar;
	
	private Camera mCamera;
	private Scene mScene;
	
	private ResourcesManager mResourcesManager;
	
	protected PhysicsWorld mPhysicsWorld;
	
	private HUD hud = new HUD();
	
	private int mFuelPoints = 100;
	private Text mScoreText;
	
	Vector<Body> BodiesToRemove = new Vector<Body>();
	Vector<Fuel> FuelToRemove = new Vector<Fuel>();

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
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public void onCreateResources() {
		ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
		mResourcesManager = ResourcesManager.getInstance();
		mResourcesManager.loadGameResources();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		/** A�ado el hud con el texto (Fuel: 100%) */
		this.mScoreText = new Text(100, 0, mResourcesManager.mFont, "Fuel: 100%", "Fuel: XXX%".length(), this.getVertexBufferObjectManager());
		hud.attachChild(this.mScoreText);
		mCamera.setHUD(hud);
		
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0,0,0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		this.mPhysicsWorld.setContactListener(contactListener());
		
		final float centerXRoad = (CAMERA_WIDTH - mResourcesManager.mRoadTextureRegion.getWidth()) / 2;   
		final float centerYRoad = (CAMERA_HEIGHT - mResourcesManager.mRoadTextureRegion.getHeight()) / 2;
		final float centerXCar = (CAMERA_WIDTH - mResourcesManager.mCarTextureRegion.getWidth()) / 2;
		
		this.addWalls();
		this.addRoad(centerXRoad, centerYRoad);	
		this.mCar = new PlayerCar(centerXCar, 620, mCamera, mPhysicsWorld);
		this.mScene.attachChild(mCar);
		
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
					/** A�adimos el fuel en una posicion aleatoria X (<->) entre 40 y 400 */
					final Fuel fuel = new Fuel(( MathUtils.random(40,400)), 10, mCamera, mPhysicsWorld);
					mScene.attachChild(fuel);
				}							
			}
		}));
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		/** UpdateHandler para eliminar de forma segura los sprites y bodies */
		this.mScene.registerUpdateHandler(this.getCollisionUpdateHandler());
		return this.mScene;
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
		this.enableAccelerationSensor(mCar);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				//NOTHING
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {	
				//NOTHING
			}
			
			@Override
			public void endContact(Contact contact) {
				//NOTHING
			}
			
			@Override
			public void beginContact(Contact contact) {
				/** Cojemos la info de las 2 entidades en el contacto */
				Fixture x1 = contact.getFixtureA();
				Fixture x2 = contact.getFixtureB();
				
				if(x2.getBody().getType2().equals("fuel"))
				{
					final Fuel fuel = (Fuel) x2.getBody().getUserData();
					fuel.setInvisible();
					FuelToRemove.add(fuel);
					BodiesToRemove.add(x2.getBody());
				} else if (x1.getBody().getType2().equals("fuel")) 
				{
					final Fuel fuel = (Fuel) x1.getBody().getUserData();
					fuel.setInvisible();
					FuelToRemove.add(fuel);
					BodiesToRemove.add(x1.getBody());
				}
			}
		};
		return contactListener;
	}

	/** A�ado la carretera animada de "Background" */
	private void addRoad(float pX, float pY) {	
		AnimatedSprite Road = new AnimatedSprite(pX, pY, ResourcesManager.getInstance().mRoadTextureRegion, ResourcesManager.getInstance().vbom);
		Road.animate(60); //dependera del coche (parado, mas tiempo corriendo)
		this.mScene.attachChild(Road);
	}

	/** Muros en los limites de la antalla */
	private void addWalls() {
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle left = new Rectangle(40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle bottom = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 1, vertexBufferObjectManager);
		final Rectangle top = new Rectangle(0, -1, CAMERA_WIDTH, 1, vertexBufferObjectManager);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottom, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, top, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(bottom);
		this.mScene.attachChild(top);
	}
	
	/** UpdateHandler para eliminar de forma segura los sprites y bodies */
	public IUpdateHandler getCollisionUpdateHandler() { 
        return new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {

				/** loop en todos los sprites a eliminar */
				for(int i=0;i<BodiesToRemove.size();i++) 
				{
					mPhysicsWorld.destroyBody(BodiesToRemove.get(i));
					BodiesToRemove.remove(i);
				}
				for(int i=0;i<FuelToRemove.size();i++) 
				{
					mScene.detachChild(FuelToRemove.get(i));
					FuelToRemove.remove(i);
				}
				
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
        };
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
