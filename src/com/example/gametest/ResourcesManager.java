package com.example.gametest;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Color;

/** Singleton encargado de contener todos los recursos para los sprites */
public class ResourcesManager
{
    //---------------------------------------------
    // FIELDS
    //---------------------------------------------
    
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
    public Engine engine;
    public SimpleBaseGameActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
   
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------
    
    private BitmapTextureAtlas mRoadTextureAtlas;
	private BitmapTextureAtlas mCarTextureAtlas;
	private BitmapTextureAtlas mFuelTextureAtlas;
	
	public ITiledTextureRegion mRoadTextureRegion;
	public ITiledTextureRegion mCarTextureRegion;
	public ITiledTextureRegion mFuelTextureRegion;
	
	public Font mFont;
	
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

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
        
    }
    
    private void loadMenuAudio()
    {
        
    }

    private void loadGameGraphics()
    {
    	/** Paths en assets de los distintos recursos (imagenes, sonido, texto) */
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		/** Texturas Carretera */
		this.mRoadTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 800, 800, TextureOptions.BILINEAR);
		this.mRoadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mRoadTextureAtlas, activity,"carretera-480-800.png", 0, 0, 2, 1);
		this.mRoadTextureAtlas.load();
		
		/** Texturas Coche */
		this.mCarTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 12, 26, TextureOptions.BILINEAR);
		this.mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mCarTextureAtlas, activity, "coche.png", 0, 0, 1, 1);
		this.mCarTextureAtlas.load();
		
		/** Texturas Fuel */
		this.mFuelTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 16, 22);
		this.mFuelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mFuelTextureAtlas, activity, "fuel.png", 0, 0,1,1);
		this.mFuelTextureAtlas.load();
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
    
    public static void prepareManager(Engine engine, SimpleBaseGameActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
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
