package entity;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Canon extends CellEntity{

	private int mCellX;
	private int mCellY;
	
	public Canon(int pCellX, int pCellY, int pWidth, int pHeight, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld pPhysicsWorld) {
		
		super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		mCellX = pCellX;
		mCellY = pCellY;
	}
	
	public void moveLeft()
	{
		if(mCellX > 0)
		{
			mCellX--;
			this.setCell(mCellX, mCellY);
		}
			
	}
	
	public void moveRight()
	{
		if(mCellX < 9)
		{
			mCellX++;
			this.setCell(mCellX, mCellY);
		}
	}

}
