package com.example.gametest;

import org.andengine.entity.sprite.Sprite;

import com.example.gametest.SceneManager.SceneType;

public class SplashScene extends BaseScene {

	private Sprite mSplash;
	
	@Override
	public void createScene() {
		mSplash = new Sprite(0, 0, resourcesManager.splash_region, vbom);
		
		/** lo colocamos en medio de la pantalla */
		mSplash.setScale(1.5f);
		mSplash.setPosition(130, 240);
		attachChild(mSplash);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		mSplash.detachSelf();
	    mSplash.dispose();
	    this.detachSelf();
	    this.dispose();
	}

}