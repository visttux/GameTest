package com.example.gametest;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;


/** Al usar solo una escena de momento no lo separo en clases */
public class MainScene extends Scene {
	
	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;
	
	private Text mScoreText;
	private ResourcesManager mResourcesManager;
	private HUD mHud;
	private PhysicsWorld mPhysicsWorld;
	private PlayerCar mCar;
	
	private int mFuelPoints;
	
	public MainScene() {
		mResourcesManager = ResourcesManager.getInstance();
		mResourcesManager.engine.registerUpdateHandler(new FPSLogger());	
		
		/** Añado el hud con el texto (Fuel: 100%) */
		this.mScoreText = new Text(100, 0, mResourcesManager.mFont, "Fuel: 100%", "Fuel: XXX%".length(), mResourcesManager.vbom);
		mHud.attachChild(this.mScoreText);
		mResourcesManager.camera.setHUD(mHud);
		
		this.setBackground(new Background(Color.BLACK));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		this.mPhysicsWorld.setContactListener(contactListener());
		
		float centerXRoad = (CAMERA_WIDTH - mResourcesManager.mRoadTextureRegion.getWidth()) / 2;   
		float centerYRoad = (CAMERA_HEIGHT - mResourcesManager.mRoadTextureRegion.getHeight()) / 2;
		float centerXCar = (CAMERA_WIDTH - mResourcesManager.mCarTextureRegion.getWidth()) / 2;   
		//float centerYCar = (CAMERA_HEIGHT - mCarTextureRegion.getHeight()) / 2;
		
		this.addWalls();
		this.addRoad(centerXRoad, centerYRoad);	
		this.mCar = new PlayerCar(centerXCar, 620,mResourcesManager.camera, mPhysicsWorld);
		this.attachChild(mCar);
		
		/** Cada segundo miramos si generamos o no un objeto fuel (1/3 posibilidades) y restamos 1% del fuel acumulado*/
		this.registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {	
				if(mFuelPoints > 0) {
					mFuelPoints--;
					mScoreText.setText("Fuel: " + mFuelPoints + "%");
				}
				final int random = MathUtils.random(0, 2);
				if(random == 2) {
					/** Añadimos el fuel en una posicion aleatoria X (<->) entre 40 y 400 */
					attachChild(new Fuel(( MathUtils.random(40,400)), 0, mResourcesManager.camera, mPhysicsWorld));
				}							
			}
		}));
		
		this.registerUpdateHandler(this.mPhysicsWorld);
	}
	
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
				
				if(x1.getBody().getUserData().equals("fuel") || x2.getBody().getUserData().equals("fuel"))
				{
					//Seteamos ese fuel para eliminar
					Log.i("contacto", "fuel con otro objeto");
					
					if (x1.getBody().getUserData().equals("coche") || x2.getBody().getUserData().equals("coche"))
					{
						//sumamos puntos al contador
					}
				}
			}
		};
		return contactListener;
	}
	
	/** Añado la carretera animada de "Background" */
	private void addRoad(float pX, float pY) {	
		AnimatedSprite Road = new AnimatedSprite(pX, pY, ResourcesManager.getInstance().mRoadTextureRegion, ResourcesManager.getInstance().vbom);
		Road.animate(60); //dependera del coche (parado, mas tiempo corriendo)
		this.attachChild(Road);
	}

	/** Muros en los limites de la antalla */
	private void addWalls() {
		final VertexBufferObjectManager vertexBufferObjectManager = mResourcesManager.vbom;
		final Rectangle left = new Rectangle(40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 40, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle bottom = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 1, vertexBufferObjectManager);
		final Rectangle top = new Rectangle(0, -1, CAMERA_WIDTH, 1, vertexBufferObjectManager);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottom, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, top, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		this.attachChild(left);
		this.attachChild(right);
		this.attachChild(bottom);
		this.attachChild(top);
	}
	
}
