attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
 
varying vec4 vFinalColor;
 
void main(void) {
    vFinalColor = vec4(aVertexNormal, 1.0);

    //transformed vertex position
    gl_Position = projection * model * view * vec4(aVertexPosition, 1.0);
}