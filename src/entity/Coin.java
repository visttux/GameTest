package entity;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Coin extends CellEntity {

	public Coin(int pCellX, int pCellY, int pWidth, int pHeight,
			ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		
		super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
	}

}
