package simulador;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE11;
import static org.lwjgl.opengl.GL13.GL_TEXTURE12;
import static org.lwjgl.opengl.GL13.GL_TEXTURE13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import Utils.Drawable;
import Utils.FPCameraController;
import Utils.OpenGLHelper;
import Utils.ShaderProgram;
import Utils.Texture;

public class Simulador implements Drawable {
	
	private Aeropuerto aeropuerto = null;
	
	
	private int uKdAttribute;
	private int uKsAttribute;
    	private int uKaAttribute;
    	private ShaderProgram shaderProgram;
    	private int uniTex1, uniTex2, uniTex3;
    	private Texture fieldstoneTexture, stoneTexture, nicegrasstexture, roadTexture;
    	private final OpenGLHelper openGLHelper = new OpenGLHelper("Simulador", new FPCameraController(23, -16, -30));
    
		
	public static void main(String[] args){
		Simulador simulador = new Simulador();
		simulador.run();
	}
	
	public void run(){
		openGLHelper.initGL("VS_ADS_Texture.vs", "FS_ADS_Texture4.fs");
		prepareModels();
        initLights();

		aeropuerto = new Aeropuerto(0,0,0, openGLHelper);
		aeropuerto.addObject(new Pista(0,0,0, openGLHelper));
		aeropuerto.addObject(new Avion(0,0,0, openGLHelper));
		aeropuerto.addObject(new TorreControl(0,0,0, openGLHelper));
		
		openGLHelper.run(this);
	}
	
	private void initLights() {
		/*
		 * EXTRAEMOS TODA LA INFORMACION REFERENTE AL SHADER PARA SU MANEJO
		 */
        int uLightPositionAttribute = shaderProgram.getUniformLocation("LightPosition");
        int uLightIntensityAttribute = shaderProgram.getUniformLocation("LightIntensity");
        int uShininessAttribute = shaderProgram.getUniformLocation("Shininess");
        uKaAttribute = shaderProgram.getUniformLocation("Ka");
        uKdAttribute = shaderProgram.getUniformLocation("Kd");
        uKsAttribute = shaderProgram.getUniformLocation("Ks");
        
        
        /*
         * INICIALIZAMOS LAS MATRICES DE ILUMINACION
         */
        glUniform4f(uLightPositionAttribute, 2.0f, 4.0f, 2.0f, 1.0f);
        glUniform3f(uLightIntensityAttribute, 0.7f, 0.7f, 0.7f);
        glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        glUniform3f(uKsAttribute, 0.2f, 0.2f, 0.2f);
        glUniform1f(uShininessAttribute, 18.0f);
    }

	private void prepareModels() {
		shaderProgram = openGLHelper.getShaderProgram();
		glActiveTexture(GL_TEXTURE0);
	    nicegrasstexture = Texture.loadTexture("cesped.jpg");
	    uniTex1 = shaderProgram.getUniformLocation("Texture1");
	    shaderProgram.setUniform(uniTex1, 0);
	
	    glActiveTexture(GL_TEXTURE1);
	    Texture texture = Texture.loadTexture("tierra.jpg");
	    uniTex2 = shaderProgram.getUniformLocation("Texture2");
	    shaderProgram.setUniform(uniTex2, 1);
	
	    glActiveTexture(GL_TEXTURE2);
	    texture = Texture.loadTexture("darkrockalpha.png");
	    uniTex3 = shaderProgram.getUniformLocation("Texture3");
	    shaderProgram.setUniform(uniTex3, 2);
	
	    glBindVertexArray(0);
	    
	    glActiveTexture(GL_TEXTURE3);
	    fieldstoneTexture = Texture.loadTexture("cielosin.jpg");
	    
	    glActiveTexture(GL_TEXTURE5);
	    roadTexture = Texture.loadTexture("pista.jpg");
	    
	    glActiveTexture(GL_TEXTURE6);
        roadTexture = Texture.loadTexture("angar.jpg");
        
        glActiveTexture(GL_TEXTURE7);
        roadTexture = Texture.loadTexture("metal1.jpg");

        glActiveTexture(GL_TEXTURE8);
        roadTexture = Texture.loadTexture("prueba1.jpg");

        glActiveTexture(GL_TEXTURE9);
        roadTexture = Texture.loadTexture("antena.jpg");

        glActiveTexture(GL_TEXTURE10);
        roadTexture = Texture.loadTexture("edificio.jpg");

        glActiveTexture(GL_TEXTURE11);
        roadTexture = Texture.loadTexture("antena.jpg");

        glActiveTexture(GL_TEXTURE12);
        roadTexture = Texture.loadTexture("negro.jpg");

        glActiveTexture(GL_TEXTURE13);
        roadTexture = Texture.loadTexture("antena.jpg");
        
        glActiveTexture(GL_TEXTURE14);
        roadTexture = Texture.loadTexture("Batwing_D.png");
        
        glActiveTexture(GL_TEXTURE15);
        roadTexture = Texture.loadTexture("glass2.jpg");
	}
	
	@Override
	public void draw(){
		aeropuerto.draw(openGLHelper);
	}

}
