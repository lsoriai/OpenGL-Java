
uniform vec4 LightPosition;   // light position
uniform vec3 LightIntensity;   // light intensity

uniform vec3 Ka;                // Ambient Reflectivity
uniform vec3 Kd;                // Diffuse Reflectivity
uniform vec3 Ks;                // Specular Reflectivity

uniform float Shininess;        // Specular shininess factor

uniform sampler2D Texture1;
uniform sampler2D Texture2;

varying vec3 Normal;
varying vec3 Position;
varying vec2 TexCoord;

void phongModel(vec3 pos, vec3 norm, out vec3 ambAndDiff, out vec3 specular)
{
    vec3 n = normalize(norm);
    vec3 s = normalize(vec3(LightPosition) - pos);
    vec3 v = normalize(vec3( - pos));
    vec3 r = reflect(-s, n);

    ambAndDiff = LightIntensity * (Ka + Kd*max(dot(s,n), 0.0));
    specular = LightIntensity * (Ks * pow(max(dot(r,v), 0.0), Shininess));
}

void main(void)
{
    vec3 ambAndDiff, specular;
    vec4 texColor1 = texture(Texture1, TexCoord);
    vec4 texColor2 = texture(Texture2, TexCoord);
    vec4 texColor = mix(texColor1, texColor2, texColor2.a);
    phongModel(Position, Normal, ambAndDiff, specular);
    gl_FragColor = vec4(ambAndDiff, 1.0) * texColor + vec4(specular, 1.0);
}