package com.example.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import jdk.nashorn.internal.objects.Global;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Sound flapSound;
	Sound beep;
	Texture background;
	Texture[] birds;
	int flapState = 0;
	int gameState = 0;

	float birdY = 0;
	float birdX= 0 ;
	float velocity = 0;
	float gravity = 2;

	Texture topTube;
	Texture bottomTube;
	float gap = 480;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubex = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Circle birdCircle;
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	BitmapFont fontStart;
	BitmapFont unlock;
	BitmapFont Credits;

	Texture gameOver;
	Texture nightmode;

	@Override
	public void create () {
		//collision detection
		birdCircle = new Circle();
		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];

		//score
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameOver = new Texture("gameover.png");

		fontStart = new BitmapFont();
		fontStart.setColor(Color.YELLOW);
		fontStart.getData().setScale(5);

		unlock = new BitmapFont();
		unlock.setColor(Color.BLACK);
		unlock.getData().setScale(2);

		Credits = new BitmapFont();
		Credits.setColor(Color.WHITE);
		Credits.getData().setScale(2);

		// bg and birds
		flapSound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));
		beep = Gdx.audio.newSound(Gdx.files.internal("beep.mp3"));
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		nightmode = new Texture("bgn.png");
		birds = new Texture[2];
		birds[0] = new Texture("birdup.png");
		birds[1] = new Texture("Birdwingdown.png");

		//pipes
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

		startGame();


	}

	public void  startGame(){

		birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
		birdX= Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2;
		for (int i =0; i < numberOfTubes; i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap -180);
			tubex[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		if (gameState == 1){

			if (tubex[scoringTube] < Gdx.graphics.getWidth() / 2){
				flapSound.play();
				score++ ;

				if (scoringTube < numberOfTubes - 1){
					scoringTube++ ;

				}else{
					scoringTube = 0;
				}
			}if (score >= 0 && score < 15){
				batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}
			else{
				batch.draw(nightmode ,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}

			if (Gdx.input.isTouched()){
				velocity = -21;
			}
			for (int i = 0; i < numberOfTubes; i++){
				if (tubex[i] < - topTube.getWidth()){
					tubex[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else{
					tubex[i] = tubex[i] - tubeVelocity;
				}
				batch.draw(topTube, tubex[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubex[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangle[i] = new Rectangle(tubex[i], Gdx.graphics.getHeight() / 2 + gap /2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubex[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


			}
			if (birdY > 0 && birdY < Gdx.graphics.getHeight()){
				velocity = velocity + gravity;
				birdY -= velocity;
			}else{
				gameState = 2;
			}
		}else if(gameState == 0) {
			if (Gdx.input.isTouched()){
				gameState = 1;
			}
		}
		else if (gameState == 2){

			batch.draw(gameOver,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			fontStart.draw(batch, "Tap To Play again", Gdx.graphics.getWidth() / 2 - 250, Gdx.graphics.getHeight() / 2 - 150);
			unlock.draw(batch, "Reach 15 to unlock the Secret Location", Gdx.graphics.getWidth() / 2 - 330, Gdx.graphics.getHeight() / 2 - 400);
			Credits.draw(batch, "Game by AlphaCoder", Gdx.graphics.getWidth()  - 320, Gdx.graphics.getHeight() - 140);
			if (Gdx.input.justTouched()){
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY );
		font.draw(batch, String.valueOf(score), 100, 200);

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
		for (int i = 0; i < numberOfTubes; i++){
			if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])){
				gameState = 2 ;
			}
		}
		batch.end();
	}

}
