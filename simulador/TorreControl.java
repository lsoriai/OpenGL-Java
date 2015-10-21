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
import Utils.Matrix3f;
import Utils.Matrix4f;
import Utils.OpenGLHelper;
import Utils.PlaneModel;
import Utils.ShaderProgram;

public class TorreControl extends Dibujable {

	/**
	 * @auhor potter
	 */

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
    private int uniModel;
    private int uNMatrixAttribute;
    private float angle;
    private static final float angularVelocity = 20.0f;
    
	public TorreControl(float px, float py, float pz, OpenGLHelper openGLHelper){
		super(px, py, pz);
		
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
         * MODELO DEL CUBE--------------------------------------------------------------------------------------
         */
        cubeVao = glGenVertexArrays();
        glBindVertexArray(cubeVao);

	        vbo_v_cube = cubeModel.createVerticesBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_v_cube);
	        glEnableVertexAttribArray(posAttrib);
	        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);
	
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
	}

	    
	public void draw(OpenGLHelper openGLHelper) /**Paso los mismo parámetros que tiene la clase padre*/
		{
	    angle += angularVelocity * openGLHelper.getDeltaTime();
		draw_base(openGLHelper);
		draw_antenna(openGLHelper);
		}

	public void draw_base(OpenGLHelper openGLHelper){
		
		glBindVertexArray(cubeVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex1, 8);
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ ------> PLANTA BAJA
		 */
        Matrix4f model = Matrix4f.scale(4.0f, 1.0f, 4.0f);
        model = Matrix4f.translate(15, 4, -3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ ------> PLANTA MEDIA
		 */
        model = Matrix4f.scale(3.0f, 5.0f, 3.0f);
        model = Matrix4f.translate(15, 5, -3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ ------> PLANTA ALTA
		 */
        int uniTex2 = shaderProgram.getUniformLocation("Texture1");
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex2, 15);
        Matrix4f model1 = Matrix4f.scale(2.0f, 2.0f, 2.0f);
        model1 = Matrix4f.translate(15, 12, -3).multiply(model1);
        glUniformMatrix4(uniModel, false, model1.getBuffer());
        Matrix3f normalMatrix1 = model1.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix1.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix1.getBuffer());
        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}

    public void draw_antenna(OpenGLHelper openGLHelper){
		//base antena
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");
		glBindVertexArray(cubeVao);
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex1, 12);
		
		/**Iluminacion*/
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        Matrix4f model = Matrix4f.scale(0.1f, 1.0f, 0.1f);
        model = Matrix4f.translate(15, 14, -3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());

        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        
        //antena//
        int uniTex1_1 = shaderProgram.getUniformLocation("Texture1");
		glBindBuffer(GL_ARRAY_BUFFER, vbo_v_cube);
		glBindBuffer(GL_ARRAY_BUFFER, vbo_n_cube);
		glBindBuffer(GL_ARRAY_BUFFER, vbo_t_cube);
		glBindVertexArray(cubeVao);
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex1_1, 13);
		
		/**Iluminacion*/
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        Matrix4f model_1 = Matrix4f.scale(1.0f, 1.0f, 1.0f);
        model_1 = Matrix4f.rotate(angle, 0, 8f, 0);
        model_1 = Matrix4f.translate(15, 16, -3).multiply(model_1);
        glUniformMatrix4(uniModel, false, model_1.getBuffer());

        Matrix3f normalMatrix_1 = model_1.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix_1.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

        glDrawElements(GL_TRIANGLES, cubeModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
	}
}
