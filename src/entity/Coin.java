package entity;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Coin extends CellEntity {
	
	private int mCellY;
	private int mCellX;
	private int mType; 
	private TimerHandler th;
	
	public Coin(int pCellX, int pCellY, int pWidth, int pHeight,
			ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pType) {
		
		super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		mType = pType;
		mCellX = pCellX;
		mCellY = pCellY;
		//setAlpha(0.5f);
		
	}
	
	public int getType()
	{
		return mType;
	}
	
	/** send the coin to the cannon */
	public void goDown()
	{
		registerUpdateHandler(th = new TimerHandler(0.001f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				if(mCellY <= 16)
				{
					setCell(mCellX, mCellY++);
					th.reset();
				} else {
					setIgnoreUpdate(true);
					//clearEntityModifiers();
					//clearUpdateHandlers();
					setAlpha(0.0f);
				}
			}
		}));
	}
	
	/** metodo que mueve la moneda hacia arriba con una celda en la que desaparece*/
	public void goUp(final int breakCell)
	{
		registerUpdateHandler(th = new TimerHandler(0.001f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				if(mCellY > breakCell)
				{
					setCell(mCellX, mCellY--);
					th.reset();
				} else {
					/*setIgnoreUpdate(true);
					clearEntityModifiers();
					clearUpdateHandlers();
					setAlpha(0.0f);*/
				}
			}
		}));
	}

}
