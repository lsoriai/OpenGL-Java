
uniform vec4 LightPosition;   // light position
uniform vec3 LightIntensity;   // light intensity

uniform vec3 Ka;                // Ambient Reflectivity
uniform vec3 Kd;                // Diffuse Reflectivity
uniform vec3 Ks;                // Specular Reflectivity

uniform float Shininess;        // Specular shininess factor

varying vec3 Normal;
varying vec3 Position;

vec3 ads()
{
     vec3 n = normalize(Normal);
     vec3 s = normalize(vec3(LightPosition) - Position);
     vec3 v = normalize(vec3( - Position));
     vec3 r = reflect(-s, n);

     return LightIntensity * (Ka + Kd*max(dot(s,n), 0.0) + Ks * pow(max(dot(r,v), 0.0), Shininess));
}

void main(void)
{
     gl_FragColor = vec4(ads(), 1.0);
}