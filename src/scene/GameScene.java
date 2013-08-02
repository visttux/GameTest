package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

import scene.SceneManager.SceneType;
import utils.Constants;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import entity.Canon;
import entity.CellEntity;
import entity.Coin;

public class GameScene extends BaseScene implements OnClickListener{

	private static final int COL_ROWS = 10;
	private HUD mHud;
	
	/** Posicion de la ultima moneda en cada columna */
	private int[] lastCoin;
		
	/** Referencias a los Coins */
	private Coin[][] CoinReferencesMatrix;
	
	private PhysicsWorld mPhysicsWorld;
	private Canon mCanon;
	
	/** tipo de la ultima moneda cojida*/
	private int lastType;
	
	//private Text mCoinNumberText;
	
	/** numero de monedas cojidas */
	private int mCoinNumber;
	//private Coin lastAlphaCoin;
	//private CellEntity mPointer;
	private int mTopDelimiter;
	public int mOffset;
	
	
	@Override
	public void createScene() {	
		
		mCoinNumber = 0;
		lastType = 0;
		mTopDelimiter = 0;
		mOffset = 0;
		createBackground();
		createHud();
		initializeReferencesMatrix();
		mPhysicsWorld =  new FixedStepPhysicsWorld(30, new Vector2(0,0), false, 8, 1);
		createCanon();
		createCoins();
		createWalls();
		
		registerUpdateHandler(new TimerHandler(Constants.TIME_GO_DOWN, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				DownAllCoins();	
					
			}
		}));
		

		registerUpdateHandler(this.mPhysicsWorld);
		
		//createButtons();
		//primera moneda donde aparece el cañon
		
		//lastAlphaCoin = CoinReferencesMatrix[5][4];
		/*registerUpdateHandler(new TimerHandler(0.1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				lastAlphaCoin.setAlpha(0.5f);
				lastAlphaCoin = CoinReferencesMatrix[(int) (mCanon.getX() / 46)][lastCoin[(int) (mCanon.getX() / 46)]];
				lastAlphaCoin.setAlpha(1.0f);
			}

		}));*/
		
		
	}

	private void initializeReferencesMatrix() {
		CoinReferencesMatrix = new Coin[Constants.CELLS_HORIZONTAL][Constants.CELLS_VERTICAL];
	}

	/** Ahora a manubrio pero habra que parsearlo de un XML */
	private void createCoins() {
				
		for(int i=0; i<5; i++) 
		{
			for(int j=0; j<5; j++) 
			{
				CoinReferencesMatrix[i][j] = new Coin(i, j, 48, 48, resourcesManager.game_coin1_region, vbom, Constants.Coin1);
			}
			
			CoinReferencesMatrix[i][i] = new Coin(i, i, 48, 48, resourcesManager.game_coin5_region, vbom, Constants.Coin5);			
		}
		
		for(int i=5; i<COL_ROWS; i++) 
		{
			for(int j=0; j<5; j++) 
			{
				CoinReferencesMatrix[i][j] = new Coin(i, j, 48, 48, resourcesManager.game_coin1_region, vbom, Constants.Coin1);
			}
			
			CoinReferencesMatrix[i][9-i] = new Coin(i, 9-i, 48, 48, resourcesManager.game_coin5_region, vbom, Constants.Coin5);			
		}
		
		for(int i=0; i<COL_ROWS; i++) {
			for(int j=0; j<COL_ROWS; j++) {
				if(CoinReferencesMatrix[i][j] != null)
				{
					attachChild(CoinReferencesMatrix[i][j]);
				}
			}
		}
		
		/** creamos una de 10 para cuadrar numeros hasta 100*/
		CoinReferencesMatrix[5][5] = new Coin(5, 5, 48, 48, resourcesManager.game_coin10_region, vbom, Constants.Coin10);	
		attachChild(CoinReferencesMatrix[5][5]);
		
		lastCoin = new int[Constants.CELLS_HORIZONTAL];
		for(int i=0; i<Constants.CELLS_HORIZONTAL; i++)
			lastCoin[i] = 4;
		lastCoin[5] = 5;
		
	}
	
	private void createCanon()
	{
		mCanon = new Canon(5, 0, 48, 800, resourcesManager.game_canon_region, vbom, mPhysicsWorld);
		attachChild(mCanon);
		activity.setCanon(mCanon);
		
	}
		
	
	@Override
	public void onBackKeyPressed() {
		/** Aqui en realidad sacaremos un pop-up (como un pause) y desde ahi podras volver al menu */
		SceneManager.getInstance().loadMenuScene();
		engine.start();
	}

	@Override
	public SceneType getSceneType() {
		
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(240, 400);	
	}
	
	private void createBackground() //poner background en el parser XML o pasarle como argumento
	{
		/*Sprite SpriteforBackground =  new Sprite(0, 0, resourcesManager.game_background_region, vbom);
		SpriteforBackground.setAlpha(0.5f);
		SpriteBackground background = new SpriteBackground(SpriteforBackground);
		setBackground(background);*/
		
		this.setBackground(new Background(Color.BLACK));
	}
	
	private void createHud()
	{
		mHud = new HUD();
		camera.setHUD(mHud);
		
		
		//aÃ±adir texto etc
		/*
		CellEntity square = new CellEntity(1,14,48,48,resourcesManager.game_hud_square_region,vbom) {};
		this.attachChild(square);
		
		mCoinNumberText = new Text(60, 630, resourcesManager.mFont, "0", "XXX".length() , vbom);
		mHud.attachChild(this.mCoinNumberText);
		*/
		
		
	}
	
	private void createWalls()
	{
		final Rectangle left = new Rectangle(-1,0,1,Constants.HEIGHT,vbom);
		final Rectangle right = new Rectangle(Constants.WIDTH, 0, 1, Constants.HEIGHT, vbom);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("muro");
		
		this.attachChild(left);
		this.attachChild(right);
		
		/** sprite muro que baja*/
		this.attachChild(new CellEntity(0,-1,480,48,resourcesManager.game_stone_wall_region,vbom) {} );
		
		/** ladrillos de madera del muro que baja */ 
		this.attachChild(new CellEntity(2,-2,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-2,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-3,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-3,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-4,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-4,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-5,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-5,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-6,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-6,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-7,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-7,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-8,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-8,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-9,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-9,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-10,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-10,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-11,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-11,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-12,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-12,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-13,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-13,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-14,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-14,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-15,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-15,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(2,-16,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
		this.attachChild(new CellEntity(8,-16,48,48,resourcesManager.game_wood_brick_region,vbom) {} );
	}
	
	/*private void createButtons() {
		
		ButtonSprite rightbutton = new ButtonSprite(380, 400, resourcesManager.game_triangle_button_region, vbom,new OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
				mCanon.moveRight();
			}
		});
		
		ButtonSprite leftbutton = new ButtonSprite(20, 400, resourcesManager.game_triangle_button_region, vbom,new OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
				mCanon.moveLeft();
			}
		});
		
		leftbutton.setRotation(180);
		mHud.attachChild(rightbutton);
		mHud.attachChild(leftbutton);
		mHud.registerTouchArea(rightbutton);
		mHud.registerTouchArea(leftbutton);
		mHud.setTouchAreaBindingOnActionDownEnabled(true);

}*/

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		
	}
	


	/** metodo que se encarga de cojer monedas y mostrarlo en la interfaz*/
	public void pickCoins()
    {	
    	
    	/** posicion de la ultima moneda en la columna */
		//int X = mCanon.getCellX();
		int X = (int) (mCanon.getX() / 46);
    	int Y = lastCoin[X];
    	
    	if(Y >= mTopDelimiter)
    	{
	    	Coin last = CoinReferencesMatrix[X][Y];
	    	
	    	if(lastType == last.getType() || lastType == 0)
	    	{
		    	/** cojemos todas las referencias de monedas del mismo tipo por encima */
		    	if(mCoinNumber < Constants.MAX_COINS)
		    	{
		    		lastType = last.getType();
			    	do 
			    	{
				    	last = CoinReferencesMatrix[X][Y];
				    	last.goDown();
				    	//CoinReferencesMatrix[X][Y] = null;
					    Y--;
					    lastCoin[X] = Y;
	
				    	mCoinNumber++;
				    	//mCoinNumberText.setText("" + mCoinNumber);
			    	} while(Y>=mTopDelimiter && mCoinNumber < Constants.MAX_COINS && last.getType() == CoinReferencesMatrix[X][Y].getType() );
		    	}
	    	}
	    		    	
	    	if(lastType == Constants.Coin100)
	    	{
	    		registerUpdateHandler(new TimerHandler(0.35f, new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						attachChild(new Text(camera.getCenterX() - 150, camera.getCenterY() - 100, resourcesManager.mFont, "CONGRATZ BRO U WIN!", vbom));
			    		engine.stop();			
					}
				}));
	    	}
	    		
	    	
	    	
	    	/** cambiamos el icono del tipo picked*/
	    	/*
	    	switch (lastType) {
			case Constants.Coin1:
				attachChild(new CellEntity(1,14,48,48,resourcesManager.game_coin1_region,vbom) {});
				break;
			case Constants.Coin5:
				attachChild(new CellEntity(1,14,48,48,resourcesManager.game_coin5_region,vbom) {});
				break;
			case Constants.Coin10:
				attachChild(new CellEntity(1,14,48,48,resourcesManager.game_coin10_region,vbom) {});
				break;
			case Constants.Coin50:
				attachChild(new CellEntity(1,14,48,48,resourcesManager.game_coin50_region,vbom) {});
				break;
			case Constants.Coin100:
				attachChild(new CellEntity(1,14,48,48,resourcesManager.game_coin100_region,vbom) {});
				break;
			default:
				break;
			}*/
	    	
    	}
    	
	}

	public void throwCoins()
	{
		
		if(lastType > 0)
		{
			//int x = mCanon.getCellX();
			final int x = (int) (mCanon.getX() / 46);
			int y = 16;
			ITextureRegion CoinTexture = null;
			
			
			switch (lastType)
			{
			case Constants.Coin1:
				CoinTexture = resourcesManager.game_coin1_region;
				break;
			case Constants.Coin5:
				CoinTexture = resourcesManager.game_coin5_region;
				break;
			case Constants.Coin10:
				CoinTexture = resourcesManager.game_coin10_region;
				break;
			case Constants.Coin50:
				CoinTexture = resourcesManager.game_coin50_region;
				break;
			case Constants.Coin100:
				CoinTexture = resourcesManager.game_coin100_region;
				break;
			default:
				break;
			}
			
			
			/** creamos un sprite por cada moneda */
			for(int i=0;i<mCoinNumber;i++)
			{
			    Coin auxCoin = new Coin(x, y-1, 48, 48, CoinTexture, vbom, lastType);
				this.attachChild(auxCoin);
				
				/** movemos la moneda hasta la ultima posicion en Y, teniendo en cuenta si hemos bajado el delimitador*/
				auxCoin.goUp(lastCoin[x] + mTopDelimiter);
				/** actualizamos la matriz de monedas */
				lastCoin[x] = lastCoin[x]+1;
				CoinReferencesMatrix[x][lastCoin[x]] = auxCoin;
			}
			
			mCoinNumber = 0;
			//mCoinNumberText.setText("" + mCoinNumber);
			lastType = 0;
		
			/** miramos si hay algun combo en la columna */
			registerUpdateHandler(new TimerHandler(0.35f, new ITimerCallback() {
				@Override
				public void onTimePassed(final TimerHandler pTimerHandler) {
					checkCombos(x);					
				}
			}));
			
			
		}
	}
	
	public void checkCombos(final int x)
	{
		int y = lastCoin[x];
		boolean recall = false;
		
		/** comprobamos si hay 5 monedas de 1 y si es asi creamos una de 5 y borramos las ultimas */
		if( lastCoin[x] >= 4 && CoinReferencesMatrix[x][y].getType() == Constants.Coin1 && CoinReferencesMatrix[x][y-1].getType() == Constants.Coin1 
				&& CoinReferencesMatrix[x][y-2].getType() == Constants.Coin1 && CoinReferencesMatrix[x][y-3].getType() == Constants.Coin1 && CoinReferencesMatrix[x][y-4].getType() == Constants.Coin1  )
		{
			
			/** borramos las monedas */
			CoinReferencesMatrix[x][y].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-1].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-2].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-3].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-4].setAlpha(0.0f);
			
			/** creamos la nueva moneda */
			CoinReferencesMatrix[x][y-4] = new Coin(x, y-4, 48, 48, resourcesManager.game_coin5_region, vbom, Constants.Coin5);
			attachChild(CoinReferencesMatrix[x][y-4]);
			
			/** actualizamos las matrices */
			CoinReferencesMatrix[x][y] = null;
			CoinReferencesMatrix[x][y-1] = null;
			CoinReferencesMatrix[x][y-2] = null;
			CoinReferencesMatrix[x][y-3] = null;
			lastCoin[x] = lastCoin[x] - 4;
			
			recall = true;
			
		} else if (lastCoin[x] >= 4 && CoinReferencesMatrix[x][y].getType() == Constants.Coin10 && CoinReferencesMatrix[x][y-1].getType() == Constants.Coin10 
				&& CoinReferencesMatrix[x][y-2].getType() == Constants.Coin10 && CoinReferencesMatrix[x][y-3].getType() == Constants.Coin10 && CoinReferencesMatrix[x][y-4].getType() == Constants.Coin10  ){
				
			/** borramos las monedas */
			CoinReferencesMatrix[x][y].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-1].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-2].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-3].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-4].setAlpha(0.0f);
			
			/** creamos la nueva moneda */
			CoinReferencesMatrix[x][y-4] = new Coin(x, y-4, 48, 48, resourcesManager.game_coin50_region, vbom, Constants.Coin50);
			attachChild(CoinReferencesMatrix[x][y-4]);
			
			/** actualizamos las matrices */
			CoinReferencesMatrix[x][y] = null;
			CoinReferencesMatrix[x][y-1] = null;
			CoinReferencesMatrix[x][y-2] = null;
			CoinReferencesMatrix[x][y-3] = null;
			lastCoin[x] = lastCoin[x] - 4;
			
			recall = true;
		} else if (lastCoin[x] >= 1 && CoinReferencesMatrix[x][y].getType() == Constants.Coin5 && CoinReferencesMatrix[x][y-1].getType() == Constants.Coin5  ) {
			/** borramos las monedas */
			CoinReferencesMatrix[x][y].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-1].setAlpha(0.0f);
			
			/** creamos la nueva moneda */
			CoinReferencesMatrix[x][y-1] = new Coin(x, y-1, 48, 48, resourcesManager.game_coin10_region, vbom, Constants.Coin10);
			attachChild(CoinReferencesMatrix[x][y-1]);
			
			/** actualizamos las matrices */
			CoinReferencesMatrix[x][y] = null;
			lastCoin[x] = lastCoin[x] - 1;
			
			recall = true;
		} else if(lastCoin[x] >= 1 && CoinReferencesMatrix[x][y].getType() == Constants.Coin50 && CoinReferencesMatrix[x][y-1].getType() == Constants.Coin50  ) {
			/** borramos las monedas */
			CoinReferencesMatrix[x][y].setAlpha(0.0f);
			CoinReferencesMatrix[x][y-1].setAlpha(0.0f);
			
			/** creamos la nueva moneda */
			CoinReferencesMatrix[x][y-1] = new Coin(x, y-1, 48, 48, resourcesManager.game_coin100_region, vbom, Constants.Coin100);
			attachChild(CoinReferencesMatrix[x][y-1]);
			
			/** actualizamos las matrices */
			CoinReferencesMatrix[x][y] = null;
			lastCoin[x] = lastCoin[x] - 1;
						
			//recall = true;
		}
				
		if(recall)
		{
			/** miramos si hay algun combo en la columna */
			registerUpdateHandler(new TimerHandler(0.25f, new ITimerCallback() {
				@Override
				public void onTimePassed(final TimerHandler pTimerHandler) {
					checkCombos(x);					
				}
			}));
		}	
	}
	
	
	/** bajamos todas las monedas, actualizamos posicion y bajamos el delimitador */
	public void DownAllCoins()
	{
		mOffset++;	
		camera.setCenter(240, 400 - mOffset*48);
		mCanon.goOneUp(mOffset);
		
		/** paramos el juego si perdemos al bajar */
		for(int i=0;i<Constants.CELLS_HORIZONTAL;i++)
			if(lastCoin[i] == 15-mOffset)
				{	
					this.attachChild(new Text(camera.getCenterX() - 50, camera.getCenterY() - 100, resourcesManager.mFont, "YOU LOSE", vbom));
					engine.stop();
				}
	}
	
}