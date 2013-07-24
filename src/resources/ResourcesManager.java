package resources;


import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import activity.TestGameActivity;
import android.graphics.Color;

/** Singleton encargado de contener todos los recursos para los sprites */
public class ResourcesManager
{
    //---------------------------------------------
    // FIELDS
    //---------------------------------------------
    
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
    public Engine engine;
    public TestGameActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
   
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------

	private BitmapTextureAtlas splashTextureAtlas;
	private BitmapTextureAtlas gameBackgroundTexturaAtlas;
	private BitmapTextureAtlas gameCanonTextureAtlas;
	private BitmapTextureAtlas gameCoinsTextureAtlas;
	private BitmapTextureAtlas gameButtonsTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	
	/** menu */
	public ITextureRegion splash_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;
	public ITextureRegion menu_background_region;
	
	/** game */
	public ITextureRegion game_background_region;
	public ITextureRegion game_coin1_region;
	public ITextureRegion game_coin5_region;
	public ITextureRegion game_canon_region;
	public ITextureRegion game_pointer_region;
	public ITextureRegion game_triangle_button_region;
	
	public Font mFont;
	
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
	public void loadSplashScreen()
    {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		/** Logo del Splash screen */
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "v_robotics.png", 0, 0);
		splashTextureAtlas.load();
    }
	    
    public void unloadSplashScreen()
    {
    	/** hacemos un release del atlas del logo y hacemos que el recolector borre la textura */
    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
    }
    
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }
    
    private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	
    	/** Texturas de los botones de menu */
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play_button.png");
    	options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "option_button.png");
    	       
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
     }
    
    private void loadMenuAudio()
    {
        
    }

    private void loadGameGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
    	
    	/** Texturas de los background */
    	gameBackgroundTexturaAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 480, 800, TextureOptions.BILINEAR);
    	game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTexturaAtlas, activity, "background/background1.png", 0, 0);
    	gameBackgroundTexturaAtlas.load();
    	
    	gameCoinsTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
    	gameCanonTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 48, 800, TextureOptions.BILINEAR);
    	gameButtonsTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
    	
    	game_coin1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCoinsTextureAtlas, activity, "coins/coin1_48px.png", 0, 0);
    	game_coin5_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCoinsTextureAtlas, activity, "coins/coin5_48px.png", 48, 48);
    	game_canon_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCanonTextureAtlas, activity, "canon2.png", 0, 0);
    	game_triangle_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameButtonsTextureAtlas, activity, "triangle_button_64px.png", 0, 0);
    	
    	gameCoinsTextureAtlas.load();
    	gameCanonTextureAtlas.load();
    	gameButtonsTextureAtlas.load();
    	
    }
    
    private void loadGameFonts()
    {
    	FontFactory.setAssetBasePath("font/");
        
    	/** Fuente Fuel*/ 
		this.mFont = FontFactory.createFromAsset(activity.getFontManager(), activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR, activity.getAssets(), "Plok.ttf", 32, true, Color.WHITE);
		this.mFont.load();
    }
    
    private void loadGameAudio()
    {
        
    }
    
    public static void prepareManager(Engine engine, TestGameActivity testGameActivity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = testGameActivity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}
