#version 130

in vec3 aVertexPosition;
in vec3 aVertexColor;
in vec2 texcoord;

out vec3 vertexColor;
out vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = aVertexColor;
    textureCoord = texcoord;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(aVertexPosition, 1.0);
}