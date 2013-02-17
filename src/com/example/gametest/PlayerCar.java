package com.example.gametest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PlayerCar extends Sprite implements IAccelerationListener {

	// ===========================================================
	// FIELDS
	// ===========================================================
	
	private Body mCarBody;
	private float mAccelerationDataX;
	private boolean mToDelete = false;
	
	// ===========================================================
	// CONSTRUCTORS
	// ===========================================================
	
	public PlayerCar(float pX, final float pY, Camera camera, PhysicsWorld physicsWorld) {
		
		super(pX, pY, ResourcesManager.getInstance().mCarTextureRegion, ResourcesManager.getInstance().vbom);
		setScale(3);
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(0,0,0);
		mCarBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, objectFixtureDef);
        mCarBody.setLinearDamping(1.5f); //mas suavidad
        mCarBody.setUserData("coche");
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, mCarBody, true, true));
        registerUpdateHandler(new IUpdateHandler() {
                @Override
                public void onUpdate(float pSecondsElapsed) {
                        /** Rotamos el coche en proporcion a la velocidad de moviento en eje X <- -> 
                         *  Si roto Body da problemas con los limites de la pantalla y el sprite pasa por eso comento las 2 lineas */
                        
                		/*final float rotationInRad = (float) MathUtils.degToRad(mAccelerationDataX*5);
                        mCarBody.setTransform(mCarBody.getWorldCenter(), rotationInRad);*/
                       
                        if(!(-0.5f < mAccelerationDataX && mAccelerationDataX < 0.5f)) {
                                setRotation(mAccelerationDataX * 5);
                        } else {
                                setRotation(0);
                        }
                        
                        /** si al chocar se mueve el coche en altura lo devolvemos a la posicion inicial */
                        if(getY() != pY) {
                                setY(pY);
                        }
                }
                @Override
                public void reset() {
                        /* NOTHING */
                }
        });
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

	// ===========================================================
	// METHODS FROM/FOR SUPERCLASS/INTERFACE 
	// ===========================================================
	
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		mAccelerationDataX = pAccelerationData.getX();
		final Vector2 velocity = Vector2Pool.obtain(pAccelerationData.getX() * 2.5f, 0);
		mCarBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}
	
}
