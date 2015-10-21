package simulador;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonOffset;
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

public class Pista extends Dibujable {

	/**
	 * @author potter
	 */

	private final PlaneModel planeModel = new PlaneModel(5,5);
	private final PlaneModel roadModel = new PlaneModel(5, 1);
	private ShaderProgram shaderProgram;
	private int uKdAttribute;
    	private int uKaAttribute;
	private int useTextures;
	private int roadVao;
	private int vbo_v_road;
	private int vbo_n_road;
	private int vbo_t_road;
	private int ebo_road;
	private int uniModel;
	private int uNMatrixAttribute;
    
    
	public Pista(float px, float py, float pz, OpenGLHelper openGLHelper){
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
         * MODELO DEL ROAD--------------------------------------------------------------------------------------
         */
        roadVao = glGenVertexArrays();
        glBindVertexArray(roadVao);

	        vbo_v_road = roadModel.createVerticesBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_v_road);
	        glEnableVertexAttribArray(posAttrib);
	        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);
	
	        vbo_n_road = roadModel.createNormalsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_n_road);
	        glEnableVertexAttribArray(vertexNormalAttribute);
	        glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);
	
	        vbo_t_road = roadModel.createTextCoordsBuffer();
	        glBindBuffer(GL_ARRAY_BUFFER, vbo_t_road);
	        glEnableVertexAttribArray(texCoordsAttribute);
	        glVertexAttribPointer(texCoordsAttribute, 2, GL_FLOAT, false, 0, 0);
	
	        ebo_road = roadModel.createIndicesBuffer();
        glBindVertexArray(0);
	}

	public void draw(OpenGLHelper openGLHelper) /**Paso los mismo parámetros que tiene la clase padre*/
		{
		System.out.println("Coordenadas: " + x + " " + y + " " + z + " ");
		draw_pista(openGLHelper);
		draw_hangar(openGLHelper);
		}

	public void draw_pista(OpenGLHelper openGLHelper){
		
		glBindVertexArray(roadVao);
		
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex1, 5);
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(6.0f, 5.0f, 30.0f);
        model = Matrix4f.translate(0, 4, 3).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        // Enable polygon offset to resolve depth-fighting isuses
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(-2.0f, 4.0f);
        glDrawElements(GL_TRIANGLES, roadModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        glDisable(GL_POLYGON_OFFSET_FILL);
	}
	
	public void draw_hangar(OpenGLHelper openGLHelper){
		
		glBindVertexArray(roadVao);

		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");
		glUniform1i(useTextures, 1);
		shaderProgram.setUniform(uniTex1, 6);
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
        glUniform3f(uKdAttribute, 0.8f, 0.8f, 0.8f);
        
        /*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
        Matrix4f model = Matrix4f.scale(26.0f, 26.0f, 6.0f);
        model = Matrix4f.translate(-1, 4, -33).multiply(model);
        glUniformMatrix4(uniModel, false, model.getBuffer());
        Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
        normalMatrix.transpose();
        glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
        // Enable polygon offset to resolve depth-fighting isuses
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(-2.0f, 4.0f);
        glDrawElements(GL_TRIANGLES, roadModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        glDisable(GL_POLYGON_OFFSET_FILL);
	}
}
