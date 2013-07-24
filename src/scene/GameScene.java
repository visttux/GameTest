package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import scene.SceneManager.SceneType;
import utils.Constants;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import entity.Canon;
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
	//private Coin lastAlphaCoin;
	//private CellEntity mPointer;
	
	
	@Override
	public void createScene() {
		createBackground();
		createHud();
		initializeReferencesMatrix();
		mPhysicsWorld =  new FixedStepPhysicsWorld(30, new Vector2(0,0), false, 8, 1);
		createCanon();
		createCoins();
		createWalls();
		createButtons();
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
		
		
		registerUpdateHandler(this.mPhysicsWorld);
	}

	private void initializeReferencesMatrix() {
		CoinReferencesMatrix = new Coin[COL_ROWS][COL_ROWS];
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
		
		lastCoin = new int[COL_ROWS];
		for(int i=0; i<COL_ROWS; i++)
			lastCoin[i] = 4;		
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
		Sprite SpriteforBackground =  new Sprite(0, 0, resourcesManager.game_background_region, vbom);
		SpriteforBackground.setAlpha(0.5f);
		//SpriteforBackground.setRotation(180);
		SpriteBackground background = new SpriteBackground(SpriteforBackground);
		setBackground(background);
	}
	
	private void createHud()
	{
		mHud = new HUD();
		camera.setHUD(mHud);
		
		//aÃ±adir texto etc
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
	}
	
	private void createButtons() {
		HUD hud = new HUD();
		
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
		hud.attachChild(rightbutton);
		hud.attachChild(leftbutton);
		hud.registerTouchArea(rightbutton);
		hud.registerTouchArea(leftbutton);
		hud.setTouchAreaBindingOnActionDownEnabled(true);
		camera.setHUD(hud);

}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		
	}
}