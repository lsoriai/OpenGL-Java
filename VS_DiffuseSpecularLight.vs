attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 uNMatrix;
 
uniform float uShininess;   //shininness
uniform vec3 uLightDirection;  //light direction
 
uniform vec4 uLightAmbient;      //light ambient property
uniform vec4 uLightDiffuse;      //light diffuse property 
uniform vec4 uLightSpecular;     //light specular property
 
uniform vec4 uMaterialAmbient;  //object ambient property
uniform vec4 uMaterialDiffuse;   //object diffuse property
uniform vec4 uMaterialSpecular;  //object specular property
 
varying vec4 vFinalColor;
 
void main(void) {
    mat4 modelview = view * model;

    //Transformed vertex position
    vec4 vertex = modelview * vec4(aVertexPosition, 1.0);
    
    //Transformed normal position
    vec3 N = vec3(uNMatrix * vec4(aVertexNormal, 1.0));
    
    //Normalize light to calculate lambertTerm
    vec3 L = normalize(uLightDirection); 
    
    //Lambert's cosine law
    float lambertTerm = dot(N,-L);
    
    //Ambient Term
    vec4 Ia = uLightAmbient * uMaterialAmbient;
    
    //Diffuse Term
    vec4 Id = vec4(0.0,0.0,0.0,1.0);
    
    //Specular Term
    vec4 Is = vec4(0.0,0.0,0.0,1.0);
    
    if(lambertTerm > 0.0) //only if lambertTerm is positive
    {
        Id = uLightDiffuse* uMaterialDiffuse * lambertTerm; //add diffuse term
        
        vec3 eyeVec = -vec3(vertex.xyz);
        vec3 E = normalize(eyeVec);
        vec3 R = reflect(L, N);
        float specular = pow(max(dot(R, E), 0.0), uShininess );
        
        Is = uLightSpecular * uMaterialSpecular * specular; //add specular term
    }

    //Final color
    vFinalColor = Ia + Id + Is;
    vFinalColor.a = 1.0;

    // Uncomment these lines for debug.
    // vFinalColor = Ia + Is; //uNMatrix * vec4(aVertexNormal, 1.0);

    //transformed vertex position
    gl_Position = projection * vertex;
}