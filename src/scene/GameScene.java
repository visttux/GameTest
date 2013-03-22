package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.Color;

import scene.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private HUD mHud;
	
	@Override
	public void createScene() {
		createBackground();
		createHud();
	}

	@Override
	public void onBackKeyPressed() {
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
	    setBackground(new Background(Color.BLUE));
	}
	
	private void createHud()
	{
		mHud = new HUD();
		camera.setHUD(mHud);
		
		//añadir texto etc
	}

}
