attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;
attribute vec2 aVertexTexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 uNMatrix;

varying vec3 Normal;
varying vec3 Position;
varying vec2 TexCoord;
 
void main(void) {
    mat4 uMVMatrix = view * model;
     
    //Transformed normal position
    Normal = normalize(uNMatrix *  aVertexNormal);
 
    //Vector Eye
    Position = vec3(uMVMatrix * vec4(aVertexPosition, 1.0));
     
    TexCoord  = aVertexTexCoord;

     //Final vertex position
     gl_Position = projection * uMVMatrix * vec4(aVertexPosition, 1.0);
}