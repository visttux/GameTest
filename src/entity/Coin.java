package entity;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Coin extends CellEntity {

	private int mType; 
	
	public Coin(int pCellX, int pCellY, int pWidth, int pHeight,
			ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pType) {
		
		super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		mType = pType;
		setAlpha(0.5f);
		
	}
	
	public int getType()
	{
		return mType;
	}
	

}
