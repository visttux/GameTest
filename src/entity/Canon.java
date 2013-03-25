package entity;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Canon extends CellEntity implements IAccelerationListener{

	private Body mCanonBody;
	
	public Canon(int pCellX, int pCellY, int pWidth, int pHeight, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld pPhysicsWorld) {
		
		super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		mCanonBody = PhysicsFactory.createBoxBody(pPhysicsWorld, this, BodyType.DynamicBody, fixtureDef);
		mCanonBody.setLinearDamping(1.5f);
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, mCanonBody, true, false));
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		
		final Vector2 velocity = Vector2Pool.obtain(pAccelerationData.getX() * 2.0f , 0);
		mCanonBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}

}
