package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import scene.SceneManager.SceneType;



public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene mMenuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            /** Como es un degradado para que se vea mejor */
	            pGLState.enableDither();
	        }
	    });
	}
	
	private void createMenuChildScene()
	{
	    mMenuChildScene = new MenuScene(camera);
	    mMenuChildScene.setPosition(0, 0);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
	    final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.options_region, vbom), 1.2f, 1);
	    
	    mMenuChildScene.addMenuItem(playMenuItem);
	    mMenuChildScene.addMenuItem(optionsMenuItem);
	    
	    mMenuChildScene.buildAnimations();
	    mMenuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(40,200);
	    optionsMenuItem.setPosition(40,340);
	    
	    mMenuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(mMenuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
        case MENU_PLAY:
        	//cargamos el juego
            SceneManager.getInstance().loadGameScene(engine);
        case MENU_OPTIONS:
            return true;
        default:
            return false;
        }
	}
	
		

}
