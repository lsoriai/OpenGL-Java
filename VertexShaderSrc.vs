#version 130

in vec3 aVertexPosition;
in vec3 aVertexColor;

out vec3 vColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vColor = aVertexColor;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(aVertexPosition, 1.0);
}