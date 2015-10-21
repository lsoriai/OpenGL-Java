attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 uNMatrix;
 
uniform vec3 LightPosition;	// Light position in eye coords

uniform vec3 Ka;                // Ambient Reflectivity
uniform vec3 Kd;                // Diffuse Reflectivity
uniform vec3 Ks;                // Specular Reflectivity

uniform vec3 La;                // Ambient light intensity
uniform vec3 Ld;                // Diffuse light intensity
uniform vec3 Ls;                // Specular light intensity

uniform float Shininess;        // Specular shininess factor
 
varying vec4 vFinalColor;
 
void main(void) {
    mat4 modelview = view * model;

    // Convert normal and position to eye coords
    vec3 tnorm = normalize(uNMatrix * aVertexNormal);
    vec4 eyeCoords = modelview * vec4(aVertexPosition, 1.0);

    vec3 s = normalize(vec3(LightPosition - eyeCoords));
    vec3 v = normalize(- eyeCoords.xyz);
    vec3 r = reflect(-s, tnorm);
    vec3 ambient = La * Ka;
    float sDotN = max(dot(s, tnorm), 0.0);
    vec3 diffuse = Ld * Kd * sDotN;
    vec3 specular = vec3(0.0);
    if(sDotN > 0.0)
        specular = Ls * Ks * pow ( max (dot(r,v), 0.0), Shininess);

    // The diffuse shading equation
    vFinalColor = vec4(ambient + diffuse + specular, 1.0);
    
    //transformed vertex position
    gl_Position = projection * modelview * vec4(aVertexPosition, 1.0);
}