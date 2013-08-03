package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.util.color.Color;

import scene.SceneManager.SceneType;

public class LevelMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene mLevelsChildScene;
	
	

	@Override
	public void createScene() {
		createBackground();
		createLevelsChildScene();
		
	}

	private void createBackground() {
		this.setBackground(new Background(Color.BLACK));		
	}

	private void createLevelsChildScene() {
		mLevelsChildScene = new MenuScene(camera);
		mLevelsChildScene.setPosition(0, 0);
		
		//TODO cambiar las texturas 
		final IMenuItem firstLevelItem = new ScaleMenuItemDecorator(new SpriteMenuItem(1, resourcesManager.game_coin1_region, vbom), 1.2f, 1.5f);
		final IMenuItem secondLevellItem = new ScaleMenuItemDecorator(new SpriteMenuItem(2, resourcesManager.game_coin5_region, vbom), 1.2f, 1.5f);
		
		mLevelsChildScene.addMenuItem(firstLevelItem);
		mLevelsChildScene.addMenuItem(secondLevellItem);
		mLevelsChildScene.buildAnimations();
		
		firstLevelItem.setPosition(48,48);
		secondLevellItem.setPosition(144,48);
		
		mLevelsChildScene.setOnMenuItemClickListener(this);
		setChildScene(mLevelsChildScene);
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVELS;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		
		switch (pMenuItem.getID())
		{
			case 1: 
				//cargar primer nivel
				SceneManager.getInstance().createGameScene(engine,1);
				break;
			case 2:
				SceneManager.getInstance().createGameScene(engine,2);
				break;
		}
		
		return false;
	}

}
