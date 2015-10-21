package simulador;

import Utils.OpenGLHelper;

public abstract class Dibujable { /**porque solamente tienes declaradas variables sin funcionalidad
									*hasta que lo empleen las hijas*/
	protected float x,y,z; /**solamente las pueden manipular las hijas
							**si fueran privadas solo lo maneja el padre*/
	
	public Dibujable(float px, float py, float pz){
		this.x = px;
		this.y = py;
		this.z = pz;
	}
	/**
	 * Clase que tiene el procedimiento draw que será sobreescrito por las clases hijas
	 * avion, torrecontrol, pista, aeropuerto
	 * 
	 * Sobre-escribir es modificar un método del padre, por tanto el hijo sabe hacer la
	 * labor del padre y sus propias implementaciones
	 */
	public void draw(OpenGLHelper openGLHelper){
		System.out.println("Error");
		throw new UnsupportedOperationException("Error");
	}

}
