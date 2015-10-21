package simulador;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import Utils.CubeModel;
import Utils.Matrix3f;
import Utils.Matrix4f;
import Utils.OpenGLHelper;
import Utils.PlaneModel;
import Utils.ShaderProgram;
import Utils.SphereModel;

public class Avion extends Dibujable {

	/**
	 * @author potter
	 */
	
	private final SphereModel sphereModel = new SphereModel();
	private ShaderProgram shaderProgram;
	private int uKdAttribute;
        private int uKaAttribute;
        private int sphereVao;
        private int avionVao;
        private int vbo_v_sphere;
        private int vbo_n_sphere;
        private int ebo_sphere;
        private int uniModel;
        private int useTextures;
        private int uNMatrixAttribute;
        private float angle;
        
        private int vbo_avion_vertices;
        private int vbo_avion_textures;
        private int vbo_avion_lights;
        
        private final ModelLoader avion = new ModelLoader("src/Batwing.obj");
        private static final float angularVelocity = 10.f;

	public Avion(float px, float py, float pz, OpenGLHelper openGLHelper){
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
                 * MODELO DEL AVIÓN OBJ--------------------------------------------------------------------------------
                 * 
                 * EXTRAEMOS LOS DATOS DE LA VARIABLE AVION. ESTOS DATOS PROCEDEN DE UN ARCHIVO .OBJ
                 * PARA EXTRAER ESTOS DATOS EMPLEAMOS LA FUNCION LOADELEMENTS DE FINIDA EN EL PAQUETE MODELLOADER
                 * CREAMOS LA MATRIZ DE VERTICES DEL AVION CON LA FUNCION GLGENVERTEXARRAY Y LO ALMACENAMOS EN UN BUFFER
                 */
                avion.loadElements();
                avionVao = glGenVertexArrays();
                glBindVertexArray(avionVao);
                
	                vbo_avion_vertices = glGenBuffers();                                                                 //GENERAMOS LA MATRIZ
	                glBindBuffer(GL_ARRAY_BUFFER, vbo_avion_vertices);                                   //LO ALMACENAMOS EN EL BUFFER
	                glBufferData(GL_ARRAY_BUFFER, avion.verticesBuffer, GL_STATIC_DRAW); //INTRODUCIMOS ESTE BUFFER EN UN NUEVO BUFFER DE DATOS
	                glEnableVertexAttribArray(posAttrib);                                                                //HABILITAMOS EL BUFFER
	                glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);                  //LE ASIGNAMOS UN IDENTIFICADOR
	                
	                vbo_avion_lights = glGenBuffers();
	                glBindBuffer(GL_ARRAY_BUFFER, vbo_avion_lights);
	                glBufferData(GL_ARRAY_BUFFER, avion.ligthsBuffer, GL_STATIC_DRAW);
	                glEnableVertexAttribArray(vertexNormalAttribute);
	                glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);
	                
	                vbo_avion_textures = glGenBuffers();
	                glBindBuffer(GL_ARRAY_BUFFER, vbo_avion_textures);
	                glBufferData(GL_ARRAY_BUFFER, avion.texelBuffer, GL_STATIC_DRAW);
	                glEnableVertexAttribArray(texCoordsAttribute);
	                glVertexAttribPointer(texCoordsAttribute, 2, GL_FLOAT, false, 0, 0);
	                uniModel = shaderProgram.getUniformLocation("model");
                glBindVertexArray(0);
                
                
                /*
                 * MODELO DE LA ESFERA PARA HACER UN AVIÓN PROPIO------------------------------------------------------
                 */
		sphereVao = glGenVertexArrays();                                                           //GENERAMOS LA MATRIZ
                glBindVertexArray(sphereVao);                                                                  //LO ALMACENAMOS EN EL BUFFER
	                vbo_v_sphere = sphereModel.createVerticesBuffer();                 //
	                glBindBuffer(GL_ARRAY_BUFFER, vbo_v_sphere);                           //INTRODUCIMOS ESTE BUFFER EN UN NUEVO BUFFER DE DATOS
	                glEnableVertexAttribArray(posAttrib);                                          //HABILITAMOS EL BUFFER
	                glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);//LE ASIGNAMOS UN IDENTIFICADOR
	
	                vbo_n_sphere = sphereModel.createNormalsBuffer();
	                glBindBuffer(GL_ARRAY_BUFFER, vbo_n_sphere);
	                glEnableVertexAttribArray(vertexNormalAttribute);
	                glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);
	
	                ebo_sphere = sphereModel.createIndicesBuffer();
                glBindVertexArray(0);
	}
	
	
	public void draw(OpenGLHelper openGLHelper) /**Paso los mismo parámetros que tiene la clase padre*/
	{
		angle += angularVelocity * openGLHelper.getDeltaTime();
		draw_airplane_moving(openGLHelper);
		draw_OBJ(openGLHelper);
		int x = 0;
		for (int i = 1; i<6; i++){
			if (x != 20){
				draw_airplanes(openGLHelper, -20+x, 5, -33);
			}
			x = x + 10;
		}
	}

	private void draw_OBJ(OpenGLHelper openGLHelper){

        	glBindVertexArray(avionVao);
        	
		/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 */
		int uniTex1 = shaderProgram.getUniformLocation("Texture1");
		shaderProgram.setUniform(uniTex1, 14);
		glUniform1i(useTextures, 1);
		
		/*
		 * VARIABLES DE ILUMINACION
		 */
		glUniform3f(uKaAttribute, 1.0f, 1.0f, 1.0f);
                glUniform3f(uKdAttribute, 1.0f, 1.0f, 1.0f);
                
		/*
		 * MANEJO DE LAS MATRICES DE TRANSFORMACION DEL OBJETO OBJ
		 */
                Matrix4f model = Matrix4f.rotate(180, 0, 1, 1);
                model = Matrix4f.scale(0.015f, 0.015f, 0.015f).multiply(model);
                model = Matrix4f.translate(0, 5, -33).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
                glDrawArrays(GL11.GL_TRIANGLES, 0, avion.getNumVertices()*3);
	}
	
        private void draw_airplane_moving(OpenGLHelper openGLHelper) {
        	
        	glBindVertexArray(sphereVao);
        	
        	/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 * EN LA ESFERA NO ESTA DEFINIDA NINGUNA TEXTURA ASI QUE INDICMAOS QUE ES 0
		 */
        	glUniform1i(useTextures, 0);
        	
        	
        	/*
		 * VARIABLES DE ILUMINACION
		 */
                glUniform3f(uKaAttribute, 0.0f, 0.8f, 1.0f);
                glUniform3f(uKdAttribute, 0.8f, 0.2f, 0.5f);
                
                /*
                 * CUERPO DEL AVION
                 */
                Matrix4f model = Matrix4f.scale(1.0f, 1.0f, 6.0f);
                model = Matrix4f.translate(0, 6, 5.0f*(1.2f + (float)Math.cos(angle/10))).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());
                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                
                /*
                 * ALETAS LATERALES 
                 */
                model = Matrix4f.scale(6.0f, 0.5f, 0.5f);
                model = Matrix4f.translate(0, 6, 5.0f*(1.2f + (float)Math.cos(angle/10))).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                
                /*
                 * ALETAS LATERALES TRASERAS 
                 */
                model = Matrix4f.scale(3.0f, 0.5f, 0.5f);
                model = Matrix4f.translate(0, 6, 5.0f*(1.6f + (float)Math.cos(angle/10))).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                
                /*
                 * ALETA COLA
                 */
                model = Matrix4f.scale(0.5f, 1.0f, 0.5f);
                model = Matrix4f.translate(0, 6, 5.0f*(1.7f + (float)Math.cos(angle/10))).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        }
	
        private void draw_airplanes(OpenGLHelper openGLHelper, int x, int y, int z) {
glBindVertexArray(sphereVao);
        	
        	/*
		 * VARIABLES RELACIONADAS CON LAS TEXTURAS
		 * EN LA ESFERA NO ESTA DEFINIDA NINGUNA TEXTURA ASI QUE INDICMAOS QUE ES 0
		 */
        	glUniform1i(useTextures, 0);
        	
        	
        	/*
		 * VARIABLES DE ILUMINACION
		 */
                glUniform3f(uKaAttribute, 0.0f, 0.8f, 1.0f);
                glUniform3f(uKdAttribute, 0.8f, 0.2f, 0.5f);
                
                /*
                 * CUERPO DEL AVION
                 */
                Matrix4f model = Matrix4f.scale(1.0f, 1.0f, 5.75f);
                model = Matrix4f.translate(x, y, z).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                Matrix3f normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                

                /*
                 * ALETAS LATERALES 
                 */
                model = Matrix4f.scale(6.0f, 0.5f, 0.5f);
                model = Matrix4f.translate(x, y, z).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                
                /*
                 * ALETAS LATERALES TRASERAS
                 */
                model = Matrix4f.scale(2.5f, 0.5f, 0.5f);
                model = Matrix4f.translate(x, y, z+2).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
                
                /*
                 * ALETA COLA
                 */
                model = Matrix4f.scale(0.5f, 1.5f, 0.5f);
                model = Matrix4f.translate(x, y, z+2).multiply(model);
                glUniformMatrix4(uniModel, false, model.getBuffer());

                normalMatrix = model.multiply(openGLHelper.getViewMatrix()).toMatrix3f().invert();
                normalMatrix.transpose();
                glUniformMatrix3(uNMatrixAttribute, false, normalMatrix.getBuffer());

                glDrawElements(GL_TRIANGLES, sphereModel.getIndicesLength(), GL_UNSIGNED_INT, 0);
        }
}
