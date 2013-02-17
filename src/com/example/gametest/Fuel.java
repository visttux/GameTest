package com.example.gametest;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Fuel extends Sprite{
	
	// ===========================================================
	// FIELDS
	// ===========================================================
	
	private boolean mToDelete;
	
	// ===========================================================
	// CONSTRUCTORS
	// ===========================================================
	
	public Fuel(float pX, final float pY, Camera camera, PhysicsWorld physicsWorld) {
		
			super(pX, pY, ResourcesManager.getInstance().mFuelTextureRegion, ResourcesManager.getInstance().vbom);	
			
			setScale(1.5f);
			final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(0,0,0);
			final Body body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, objectFixtureDef);
	        body.setUserData("fuel");
	        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
	        final Vector2 velocity = Vector2Pool.obtain(0, 10);
	        body.setLinearVelocity(velocity);
	        Vector2Pool.recycle(velocity);
       	}
	
	// ===========================================================
	// GETTERS & SETTERS
	// ===========================================================
	
	public boolean isToDelete() {
		return mToDelete;
	}
	
	public void setToDelete(boolean ToDelete) {
		this.mToDelete = ToDelete;
	}	
}

	/*fuel.registerUpdateHandler(new IUpdateHandler() {
	@Override
	public void reset() {
		/* NOTHING 
	}
	@Override
	public void onUpdate(float pSecondsElapsed) {
		/** Miramos si colisiona con el coche (o choca y se mueve en X) para aumentar el combustible y hacerlo desaparecer 
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
});*/
