attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 uNMatrix;
 
uniform vec3 LightPosition;	// Light position in eye coords
uniform vec3 Kd;                // Diffuse Reflectivity
uniform vec3 Ld;                // Light source intensity
 
varying vec4 vFinalColor;
 
void main(void) {
    mat4 modelview = view * model;

    // Convert normal and position to eye coords
    vec3 tnorm = normalize(uNMatrix * aVertexNormal);
    vec4 eyeCoords = modelview * vec4(aVertexPosition, 1.0);

    vec3 s = normalize(vec3(LightPosition - eyeCoords));

    // The diffuse shading equation
    vFinalColor = vec4(Ld * Kd * max(dot(s, tnorm), 0.0), 1.0);
    
    //transformed vertex position
    gl_Position = projection * modelview * vec4(aVertexPosition, 1.0);
}