/*
 * To change this license headerf, choose License Headers in Project Properties.
 * To change this template filef, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

/**
 * draws a sphere of the given	radius centered	around the origin. The sphere is
 * subdivided around the z axis into slices and along the z axis into stacks
 * (similar to lines of longitude and latitude).
 *
 * If the orientation is set to GLU.OUTSIDE (with glu.quadricOrientation), then
 * any normals generated point away from the center of the sphere. Otherwise,
 * they point toward the center of the sphere. * If texturing is turned on (with
 * glu.quadricTexture), then texture coordinates are generated so that t ranges
 * from 0.0 at z=-radius to 1.0 at z=radius (t increases linearly along
 * longitudinal lines), and s ranges from 0.0 at the +y axis, to 0.25 at the +x
 * axis, to 0.5 at the -y axis, to 0.75 at the -x axis, and back to 1.0 at the
 * +y axis.
 */
public class PlaneModel {

    float[] vertices = new float[]{
        1.0f, 0.0f, 1.0f, // 4 - Top Right Of The Quad (Top)
        -1.0f, 0.0f, 1.0f, // 5 - Top Left Of The Quad (Top)
        -1.0f, 0.0f, -1.0f, // 6 - Bottom Left Of The Quad (Top)
        1.0f, 0.0f, -1.0f, // 7 - Bottom Right Of The Quad (Top)
    };

    float[] colors = new float[]{
        1.0f, 1.0f, 1.0f, // Top Right Of The Quad (Top)
        1.0f, 1.0f, 1.0f, // Top Left Of The Quad (Top)
        1.0f, 1.0f, 1.0f, // Bottom Left Of The Quad (Top)
        1.0f, 1.0f, 1.0f, // Bottom Right Of The Quad (Top)
    };

    float[] textCoords = new float[]{
        1.0f, 1.0f, // Top Right Of The Quad (Top)
        0.0f, 1.0f, // Top Left Of The Quad (Top)
        0.0f, 0.0f, // Bottom Left Of The Quad (Top)
        1.0f, 0.0f, // Bottom Right Of The Quad (Top)
    };

    int[] indices = new int[]{
        0, 1, 2, // Top Right, Top Left, Bottom Left Of The Quad (Front)
        0, 2, 3, // Top Right, Bottom Left, Bottom Right Of The Quad (Front)
    };

    private float[] normals;

    private int vbo_vertices;
    private int vbo_colors;
    private int vbo_normals;
    private int vbo_textCoords;
    private int vbo_indices;

    public PlaneModel()
    {
        normals = calculateNormals(vertices, indices);
    }
    
    public PlaneModel(float rw, float rh) {
        textCoords = new float[]{
            rw, rh, // Top Right Of The Quad (Top)
            0.0f, rh, // Top Left Of The Quad (Top)
            0.0f, 0.0f, // Bottom Left Of The Quad (Top)
            rw, 0.0f, // Bottom Right Of The Quad (Top)
        };
        normals = calculateNormals(vertices, indices);
    }

    public int createVerticesBuffer() {
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        vbo_vertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_vertices);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        return vbo_vertices;
    }

    public int createColorsBuffer() {
        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        vbo_colors = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_colors);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);

        return vbo_colors;
    }

    public int createTextCoordsBuffer() {
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
        textCoordsBuffer.put(textCoords).flip();

        vbo_textCoords = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_textCoords);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);

        return vbo_textCoords;
    }

    public int createIndicesBuffer() {
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        vbo_indices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        return vbo_indices;
    }

    public int createNormalsBuffer() {
        FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.length);
        normalsBuffer.put(normals).flip();

        vbo_normals = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_normals);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);

        return vbo_normals;
    }

    public int getIndicesLength() {
        return indices.length;
    }

    public int getVerticesBufferId() {
        return vbo_vertices;
    }
    
    public int getNormalsBufferId() {
        return vbo_normals;
    }
    
    public int getColorsBufferId() {
        return vbo_colors;
    }
    
    public int getTextCoordsBufferId() {
        return vbo_textCoords;
    }
    
    public int getIndicesBufferId() {
        return vbo_indices;
    }

   

    //indices have to be completely defined NO TRIANGLE_STRIP only TRIANGLES
    private static float[] calculateNormals(float[] vs, int[] ind) {
        int x = 0;
        int y = 1;
        int z = 2;

        float[] ns = new float[vs.length];
        for (int i = 0; i < vs.length; i++) { //for each vertex, initialize normal x, normal y, normal z
            ns[i] = 0.0f;
        }

        for (int i = 0; i < ind.length; i = i + 3) { //we work on triads of vertices to calculate normals so i = i+3 (i = indices index)
            float[] v1 = new float[vs.length];
            float[] v2 = new float[vs.length];
            float[] normal = new float[vs.length];
            //p1 - p0
            v1[x] = vs[3 * ind[i + 1] + x] - vs[3 * ind[i] + x];
            v1[y] = vs[3 * ind[i + 1] + y] - vs[3 * ind[i] + y];
            v1[z] = vs[3 * ind[i + 1] + z] - vs[3 * ind[i] + z];
            // p0 - p1
            v2[x] = vs[3 * ind[i + 2] + x] - vs[3 * ind[i + 1] + x];
            v2[y] = vs[3 * ind[i + 2] + y] - vs[3 * ind[i + 1] + y];
            v2[z] = vs[3 * ind[i + 2] + z] - vs[3 * ind[i + 1] + z];
            //p2 - p1
            // v1[x] = vs[3*ind[i+2]+x] - vs[3*ind[i+1]+x];
            // v1[y] = vs[3*ind[i+2]+y] - vs[3*ind[i+1]+y];
            // v1[z] = vs[3*ind[i+2]+z] - vs[3*ind[i+1]+z];
            // p0 - p1
            // v2[x] = vs[3*ind[i]+x] - vs[3*ind[i+1]+x];
            // v2[y] = vs[3*ind[i]+y] - vs[3*ind[i+1]+y];
            // v2[z] = vs[3*ind[i]+z] - vs[3*ind[i+1]+z];
            //cross product by Sarrus Rule
            normal[x] = v1[y] * v2[z] - v1[z] * v2[y];
            normal[y] = v1[z] * v2[x] - v1[x] * v2[z];
            normal[z] = v1[x] * v2[y] - v1[y] * v2[x];

            // ns[3*ind[i]+x] += normal[x];
            // ns[3*ind[i]+y] += normal[y];
            // ns[3*ind[i]+z] += normal[z];
            for (int j = 0; j < 3; j++) { //update the normals of that triangle: sum of vectors
                ns[3 * ind[i + j] + x] = ns[3 * ind[i + j] + x] + normal[x];
                ns[3 * ind[i + j] + y] = ns[3 * ind[i + j] + y] + normal[y];
                ns[3 * ind[i + j] + z] = ns[3 * ind[i + j] + z] + normal[z];
            }
        }
        //normalize the result
        for (int i = 0; i < vs.length; i = i + 3) { //the increment here is because each vertex occurs with an offset of 3 in the array (due to x, y, z contiguous values)

            float[] nn = new float[vs.length];
            nn[x] = ns[i + x];
            nn[y] = ns[i + y];
            nn[z] = ns[i + z];

            float len = (float) Math.sqrt((nn[x] * nn[x]) + (nn[y] * nn[y]) + (nn[z] * nn[z]));
            if (len == 0) {
                len = 0.00001f;
            }

            nn[x] = nn[x] / len;
            nn[y] = nn[y] / len;
            nn[z] = nn[z] / len;

            ns[i + x] = nn[x];
            ns[i + y] = nn[y];
            ns[i + z] = nn[z];
        }

        return ns;
    }

    private static float[] calculateVertices(int sphereDiv) {

        double ai, si, ci;
        double aj, sj, cj;

        float[] positions = new float[(sphereDiv + 1) * (sphereDiv + 1) * 3];

        int cnt = 0;
        // Generate coordinates
        for (int j = 0; j <= sphereDiv; j++) {
            aj = j * Math.PI / sphereDiv;
            sj = Math.sin(aj);
            cj = Math.cos(aj);
            for (int i = 0; i <= sphereDiv; i++) {
                ai = i * 2 * Math.PI / sphereDiv;
                si = Math.sin(ai);
                ci = Math.cos(ai);

                positions[cnt++] = (float) (si * sj);  // X
                positions[cnt++] = (float) (cj);       // Y
                positions[cnt++] = (float) (ci * sj);  // Z
            }

        }
        return positions;
    }

    private static int[] calculateIndices(int sphereDiv) {
        int p1, p2;
        int[] indices = new int[sphereDiv * sphereDiv * 6];
        int cnt = 0;
        // Generate indices
        for (int j = 0; j < sphereDiv; j++) {
            for (int i = 0; i < sphereDiv; i++) {
                p1 = j * (sphereDiv + 1) + i;
                p2 = p1 + (sphereDiv + 1);

                indices[cnt++] = (p1);
                indices[cnt++] = (p2);
                indices[cnt++] = (p1 + 1);

                indices[cnt++] = (p1 + 1);
                indices[cnt++] = (p2);
                indices[cnt++] = (p2 + 1);
            }
        }
        return indices;
    }
}
