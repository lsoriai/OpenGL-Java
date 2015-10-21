package simulador;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

import Utils.CubeModel;
import Utils.FPCameraController;
import Utils.Matrix3f;
import Utils.Matrix4f;
import Utils.OpenGLHelper;
import Utils.PlaneModel;
import Utils.ShaderProgram;

public class Aeropuerto extends Dibujable {

	/**
	 * @author potter
	 */

	ArrayList<Dibujable> ObjetosDibujables = null;
	
	private final PlaneModel planeModel = new PlaneModel(5,5);
	private final CubeModel cubeModel = new CubeModel();
	private ShaderProgram shaderProgram;
	private int uKdAttribute;
    	private int uKaAttribute;
	private int useTextures;
	private int cubeVao;
	private int vbo_v_cube;
	private int vbo_n_cube;
	private int vbo_t_cube;
	private int ebo_cube;
	private int planeVao;
	private int vbo_v_plane;
	private int vbo_n_plane;
	private int vbo_t_plane;
	private int ebo_plane;
	private int uniModel;
	private int uNMatrixAttribute;
    

	public Aeropuerto(float px, float py, float pz, OpenGLHelper openGLHelper){
		super(px, py, pz);
		
		ObjetosDibujables = new ArrayList<Dibujable>();
		
		/*
		 * EN EL CONSTRUCTOR DE ESTA CLASE DECLARAMOS LAS VARIABLES CON LAS QUE DEBE TRABJAR
		 * AMBOS PROGRAMAS SHADER (VERTEXSHADER Y FRAGMENTSHADER)
		 */
		shaderProgram = openGLHelper.getShaderProgram();
        int posAttrib = shaderProgram.getAttributeLocation("aVertexPosition");
        int vertexNormalAttribute = shaderProgram.getAttributeLocation("aVertexNormal");
        int texCoordsAttribute = shaderProgram.getAttributeLocation("aVertexTexCoord");
        uniModel = shaderProgram.getUniformLocation("model");
        uNMatrixAttribute = shaderProgram.getUniformLocation("uNMatrix");
        useTextures = shaderProgram.getUniformLocation("useTextures");
        
        
        /*
         * MODELO DEL CUBO--------------------------------------------------------------------------------------
         *
         * EN ESTE CASO YA TENEMOS LOS DATOS DEL OBJETO EN EL ARCHIVO CUBEMODEL.JAVA ASI QUE DIRECTAMENTE
         * CREAMOS LAS MATRICICES Y LO ALMACENAMOS EN EL BUFFER
         */
        cubeVao = glGenVertexArrays();
        glBindVertexArray(cubeVao);
        
	        vbo_v_cube = cubeModel.createVerticesBuffer();              //GENERAMOS LA MATRIZ
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_v_cube);                  //ALMACENAMOS LOS DATOS
	        glEnableVertexAttribArray(posAttrib);                       //HABILITAMOS LA MATRIZ
	        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0); //CREAMOS UN INDICADOR
	
	        vbo_n_cube = cubeModel.createNormalsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_n_cube);
	        glEnableVertexAttribArray(vertexNormalAttribute);
	        glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);
	
	        vbo_t_cube = cubeModel.createTextCoordsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_t_cube);
	        glEnableVertexAttribArray(texCoordsAttribute);
	        glVertexAttribPointer(texCoordsAttribute, 2, GL_FLOAT, false, 0, 0);
	
	        ebo_cube = cubeModel.createIndicesBuffer();
        glBindVertexArray(0);
        
        
        /*
         * MODELO DEL PLANO--------------------------------------------------------------------------------------
         *
         * EN ESTE CASO YA TENEMOS LOS DATOS DEL OBJETO EN EL ARCHIVO CUBEMODEL.JAVA ASI QUE DIRECTAMENTE
         * CREAMOS LAS MATRICICES Y LO ALMACENAMOS EN EL BUFFER
         */
        planeVao = glGenVertexArrays();
        glBindVertexArray(planeVao);
        
	        vbo_v_plane = planeModel.createVerticesBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_v_plane);
	        glEnableVertexAttribArray(posAttrib);
	        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);
	
	        vbo_n_plane = planeModel.createNormalsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_n_plane);
	        glEnableVertexAttribArray(vertexNormalAttribute);
	        glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);
	
	        vbo_t_plane = planeModel.createTextCoordsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_t_plane);
	        glEnableVertexAttribArray(texCoordsAttribute);
	        glVertexAttribPointer(texCoordsAttribute, 2, GL_FLOAT, false, 0, 0);
	
	        ebo_plane = planeModel.createIndicesBuffer();
        glBindVertexArray(0);
	}

	public void addObject(Dibujable newobject){
		ObjetosDibujables.add(newobject);
	}
	
	public void draw(OpenGLHelper openGLHelper) /**Paso los mismo parámetros que tiene la clase padre*/
	{
		draw_skybox(openGLHelper);
		draw_floor(openGLHelper);
		draw_fence(openGLHelper);
		body_building(openGLHelper);
		int x = 1;
		for (int i=1; i<7; i++){
			building_column(openGLHelper, x);
			x = x + 10;
		}
		
		for (Dibujable obj:ObjetosDibujables){
			obj.draw(openGLHelper);
		}
	}
	
	public void draw_skybox(OpenGLHelper openGLHelper){
		
		glBindVertexArray(cubeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");  //HABILITAMOS LA VARIABLE CON LA QUE TRABAJA EL SHADER
		glUniform1i(useTextures, 1);                                 //INDICO QUE SOLO UTILIZO UNA TEXTURA
		shaderProgram.setUniform(uniTex1, 3);                        //INDICO CUAL ES LA TEXTURA CONCRETA
		

		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        

		/*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(26.0f, 26.0f, 36.0f);
        model = Matrix4f.translate(-1, 4, -3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
	
	public void draw_floor(OpenGLHelper openGLHelper){
		
		glBindVertexArray(planeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");  //HABILITAMOS LA VARIABLE CON LA QUE TRABAJA EL SHADER
		glUniform1i(useTextures, 3);                                 //INDICO QUE SOLO UTILIZO UNA TEXTURA
		shaderProgram.setUniform(uniTex1, 0);                        //INDICO CUAL ES LA TEXTURA CONCRETA
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(26.0f, 26.0f, 36.0f);
        model = Matrix4f.translate(-1, 4, -3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
	
	public void draw_fence(OpenGLHelper openGLHelper){
		
		glBindVertexArray(cubeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");  //HABILITAMOS LA VARIABLE CON LA QUE TRABAJA EL SHADER
		glUniform1i(useTextures, 1);                                 //INDICO QUE SOLO UTILIZO UNA TEXTURA
		shaderProgram.setUniform(uniTex1, 7);                        //INDICO CUAL ES LA TEXTURA CONCRETA
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ ------> VALLA 1
		 */
        Matrix4f model = Matrix4f.scale(10.0f, 2.0f, 0.25f);
        model = Matrix4f.translate(16, 4, -27).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ ------> VALLA 2
		 */
        model = Matrix4f.scale(11f, 2.0f, 0.25f);
        model = Matrix4f.translate(-17, 4, -27).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
	
	public void building_column (OpenGLHelper openGLHelper, int x){
		
		glBindVertexArray(cubeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");  //HABILITAMOS LA VARIABLE CON LA QUE TRABAJA EL SHADER
		glUniform1i(useTextures, 1);                                 //INDICO QUE SOLO UTILIZO UNA TEXTURA
		shaderProgram.setUniform(uniTex1, 10);                       //INDICO CUAL ES LA TEXTURA CONCRETA
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(0.5f, 4.0f, 2.0f);
        model = Matrix4f.translate(-26+x, 4, -37).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
	
	public void body_building(OpenGLHelper openGLHelper){
		
		glBindVertexArray(cubeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");  //HABILITAMOS LA VARIABLE CON LA QUE TRABAJA EL SHADER
		glUniform1i(useTextures, 1);                                 //INDICO QUE SOLO UTILIZO UNA TEXTURA
		shaderProgram.setUniform(uniTex1, 10);                       //INDICO CUAL ES LA TEXTURA CONCRETA
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(26.0f, 7.0f, 2.0f);
        model = Matrix4f.translate(-1, 15, -37).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
	
}
