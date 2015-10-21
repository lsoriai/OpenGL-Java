/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author Agustin
 */
public class FPCameraController {

    private float mouseSensitivity = 0.05f;

    //3d vector to store the camera's position in
    private Vector3f position = null;
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;

    public FPCameraController(float x, float y, float z) {
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
    }
    //increment the camera's current yaw rotation

    public void yaw(float amount) {
        //increment the yaw by the amount param
        yaw += amount;
    }

    //increment the camera's current yaw rotation
    public void pitch(float amount) {
        //increment the pitch by the amount param
        pitch += amount;
    }

    //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance) {
        position.x -= distance * (float) Math.sin(Math.toRadians(yaw));
        position.z += distance * (float) Math.cos(Math.toRadians(yaw));
    }

    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw));
    }

    //strafes the camera left relitive to its current rotation (yaw)
    public void strafeLeft(float distance) {
        position.x -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
        position.z += distance * (float) Math.cos(Math.toRadians(yaw - 90));
    }

    //strafes the camera right relitive to its current rotation (yaw)
    public void strafeRight(float distance) {
        position.x -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
        position.z += distance * (float) Math.cos(Math.toRadians(yaw + 90));
    }

    //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public Matrix4f lookThrough() {
        //rotate the pitch around the X axis
        Matrix4f matrix = Matrix4f.rotate(pitch, 1.0f, 0.0f, 0.0f);

        //rotate the yaw around the Y axis
        matrix = matrix.multiply(Matrix4f.rotate(yaw, 0.0f, 1.0f, 0.0f));

        //translate to the position vector's location
        matrix = matrix.multiply(Matrix4f.translate(position.x, position.y, position.z));

        return matrix;
    }

    public void ProcessMouseMovement(float dx, float dy) {
        //controll camera yaw from x movement fromt the mouse
        this.yaw(dx * getMouseSensitivity());
        //controll camera pitch from y movement fromt the mouse
        this.pitch(dy * getMouseSensitivity());
    }

    /**
     * @return the mouseSensitivity
     */
    public float getMouseSensitivity() {
        return mouseSensitivity;
    }

    /**
     * @param mouseSensitivity the mouseSensitivity to set
     */
    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }
}
