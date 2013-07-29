package scene;


import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import resources.ResourcesManager;


public class SceneManager
{
    //---------------------------------------------
    // SCENES
    //---------------------------------------------
    
    private BaseScene mSplashScene;
    private BaseScene mMenuScene;
    public BaseScene mGameScene;
    private BaseScene mLoadingScene;
    
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;
    
    private BaseScene currentScene;
    
    private Engine engine = ResourcesManager.getInstance().engine;
    
    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
    }
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
    
    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(mMenuScene);
                break;
            case SCENE_GAME:
                setScene(mGameScene);
                break;
            case SCENE_SPLASH:
                setScene(mSplashScene);
                break;
            case SCENE_LOADING:
                setScene(mLoadingScene);
                break;
            default:
                break;
        }
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
    
    
    //---------------------------------------------
    // METHODS TO CREATE/DISPOSE EVERY SCENE
    //---------------------------------------------
    
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourcesManager.getInstance().loadSplashScreen();
        mSplashScene = new SplashScene();
        currentScene = mSplashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
    }
    
    private void disposeSplashScene()
    {
        ResourcesManager.getInstance().unloadSplashScreen();
        mSplashScene.disposeScene();
        mSplashScene = null;
    }
    
    public void createMenuScene()
    {
        ResourcesManager.getInstance().loadMenuResources();
        mMenuScene = new MainMenuScene();
        setScene(mMenuScene);
        disposeSplashScene();
    }
    
    /** si ya la hemos creado */
    public void loadMenuScene()
    {
    	mGameScene.disposeScene();
    	setScene(mMenuScene);    	
    }

    public void createGameScene(final Engine mEngine)
    {
        ResourcesManager.getInstance().loadGameResources();
        mGameScene = new GameScene();
        setScene(mGameScene);
        mGameScene.activity.onResumeGame();
    }
       
    
}