package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;
/**
** PT- Extends ApplicationAdapter: a classe ApplicationAdapter é uma classe abstrata que implementa a interface ApplicationListener. 
** ApplicationAdapter possui os métodos necessários para esta aplicação: create(), render(), resize().
**
** EN- Extends ApplicationAdapter: Abstract class that implement the interface ApplicationListener.
** ApplicationAdapter has the metods required for this application: create(), render(), resize().
**/
public class FlappyBird extends ApplicationAdapter {

	//PT- Classe responsável por desenhar os objetos na tela.
	//EN- Class responsible for drawing the objects on the screen.
	private SpriteBatch batch;

	//PT- Cria os atributos do tipo textura.
	//EN- Creates the atributtes on the screen.
	private Texture[] birds;
	private Texture background;
	private Texture downPipe;
	private Texture upperPipe;
	private Texture gameOver;
	private Texture getReady;

	//PT- Random é utilizado para randomificar a posição dos canos.
	//EN- Method Random is utilized for randomize the position of pipes.
	private Random randomNumber;
	
	//PT- Atributos do tipo Bitmap. 
	//EN- Atributtes of type Bitmap.
	private BitmapFont font;
	private BitmapFont message;

	
	//PT- Cria objetos ao redor das texturas para identificar colisões.
	//EN- Creates objects around the textures to identify collisions.
	private Circle circleBird;
	private Rectangle rectangleUpperPipe;
	private Rectangle rectangleDownPipe;

	//PT- Atributos de configuracao
	//EN- Configuration atributtes
	private float deviceWidth;
	private float deviceHeight;
	private int estateGame=0;// 0-> jogo não iniciado 1-> jogo iniciado 2-> Game Over
	private int pontuation=0;
	private float variation = 0;
	private float dropSpeed=0;
	private float verticalStartPosition;
	private float positionMovementHorizontalPipe;
	private float spaceBetweenPipes;
	private float deltaTime;
	private float heightBetweenRandomPipes;
	private boolean scoredPoints=false;

	//PT- Câmera
	//EN- Camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	//PT- Chamado na criação da aplicação. 
    //EN- Called when the Application is first created.
	@Override
	public void create () {

		batch = new SpriteBatch();
		randomNumber = new Random();
		circleBird = new Circle();
        
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);

		message = new BitmapFont();
		message.setColor(Color.WHITE);
		message.getData().setScale(3);



		birds = new Texture[3];
		birds[0] = new Texture("passaro1.png");
		birds[1] = new Texture("passaro2.png");
		birds[2] = new Texture("passaro3.png");

		background = new Texture("fundo.png");
		downPipe = new Texture("cano_baixo.png");
		upperPipe = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");
		getReady = new Texture("getready.png");

		/**********************************************
		 * PT- Configuração da câmera / EN- Camera configuration
		 * */
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		deviceWidth = VIRTUAL_WIDTH;
		deviceHeight  = VIRTUAL_HEIGHT;

		verticalStartPosition = deviceHeight / 2;
		positionMovementHorizontalPipe = deviceWidth;
		spaceBetweenPipes = 300;
	}

	@Override
	public void render () {

		camera.update();

		//PT- Limpar frames anteriores.
		//EN- Clean previous frames.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//PT- DeltaTime é um método para variar o tempo.
		//EN- DeltaTime is a method for variation the time.
		deltaTime = Gdx.graphics.getDeltaTime();
		variation += deltaTime * 10;
		if (variation > 2) variation = 0;

		if( estateGame == 0 ){//PT- Não iniciado / EN- Not Started

			//PT- Método JustTouched verifica se a tela foi tocada.
			//EN- JustTouched method verify if the screen was touched.
			if( Gdx.input.justTouched() ){
				estateGame = 1;
			}
		}else {//PT- Iniciado / EN- Started
			dropSpeed++;
			if (verticalStartPosition > 0 || dropSpeed < 0)
				verticalStartPosition = verticalStartPosition - dropSpeed;
			if( estateGame == 1 ){//PT- Iniciado / EN- Started
				positionMovementHorizontalPipe -= deltaTime * 200;
				if (Gdx.input.justTouched()) {
					dropSpeed = -15;
				}

				//PT- Verifica se o cano saiu inteiramente da tela.
				//EN- Check if the barrel has completely gone out of the screen.
				if (positionMovementHorizontalPipe < -upperPipe.getWidth()) {
					positionMovementHorizontalPipe = deviceWidth;
					heightBetweenRandomPipes = randomNumber.nextInt(400) - 200;
					scoredPoints = false;
				}

				//PT- Verifica pontuação.
				//EN- Checks the score.
				if(positionMovementHorizontalPipe < 120 ){
					if( !scoredPoints ){
						pontuation++;
						scoredPoints = true;
					}
				}
			}else{// Game Over
				//PT- Zerar os valores padrões.
				//EN- Reset default values.
				if( Gdx.input.justTouched() ){
					estateGame = 0;
					dropSpeed = 0;
					pontuation = 0;
					positionMovementHorizontalPipe = deviceWidth;
					verticalStartPosition = deviceHeight / 2;
				}
			}
		}

		//PT- Configura os dados de projeção da câmera.
		//EN- Configure the datas of projection of camera.
		batch.setProjectionMatrix( camera.combined );

		batch.begin();

		//PT- Método draw() é responsável por desenhar os elementos na tela.
		//EN- draw() method is responsable for drawing the elements on screen.
		batch.draw(background, 0, 0, deviceWidth, deviceHeight);
		batch.draw(upperPipe, positionMovementHorizontalPipe, deviceHeight / 2 + spaceBetweenPipes / 2 + heightBetweenRandomPipes);
		batch.draw(downPipe, positionMovementHorizontalPipe, deviceHeight / 2 - downPipe.getHeight() - spaceBetweenPipes / 2 + heightBetweenRandomPipes);
		batch.draw(birds[(int) variation], 120, verticalStartPosition);
		font.draw(batch, String.valueOf(pontuation), deviceWidth / 2, deviceHeight - 50);
		if (estateGame == 0){
			message.draw(batch, "Toque para iniciar!", deviceWidth / 2 - 175, deviceHeight / 2 - getReady.getHeight());
			batch.draw(getReady, deviceWidth / 2 - getReady.getWidth() / 2, deviceHeight / 2);
		}else if( estateGame == 2 ) {
			message.draw(batch, "Toque para reiniciar!", deviceWidth / 2 - 230, deviceHeight / 2 - gameOver.getHeight());
			batch.draw(gameOver, deviceWidth / 2 - gameOver.getWidth() / 2, deviceHeight / 2);
		}
		batch.end();

		//PT- Seta formas(círculos e retângulos) ao redor dos objetos pássaros e canos.
		//EN- Put shapes(circle and rectangules) around of objects birds and pipes.
		circleBird.set(120 + birds[0].getWidth() / 2, verticalStartPosition + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
		rectangleDownPipe = new Rectangle(
				positionMovementHorizontalPipe, deviceHeight / 2 - downPipe.getHeight() - spaceBetweenPipes / 2 + heightBetweenRandomPipes,
				downPipe.getWidth(), downPipe.getHeight()
		);
		rectangleUpperPipe = new Rectangle(
				positionMovementHorizontalPipe, deviceHeight / 2 + spaceBetweenPipes / 2 + heightBetweenRandomPipes,
				upperPipe.getWidth(), upperPipe.getHeight()
		);

		
		//PT- Teste de colisão.
		//EN- Collision test.
		if( Intersector.overlaps( circleBird, rectangleDownPipe ) || Intersector.overlaps(circleBird, rectangleUpperPipe)
				|| verticalStartPosition <= 0 || verticalStartPosition >= deviceHeight ){
				estateGame = 2;
		}
	}
	
	//PT- Chamado quando a aplicação é redimensionada.
	//EN- Called when the application is resized.
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
