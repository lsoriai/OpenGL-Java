package simulador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class ModelLoader {

	String filename;
	
    private int nvertices;
    private int npositions;
    private int ntexels;
    private int nnormals;
    private int nfaces;
	
    private float [][]positions = null;
    private float [][]texels = null;
    private float [][]normals = null;
    private int [][]faces = null; 
   
    
    public FloatBuffer verticesBuffer = null;
    public FloatBuffer ligthsBuffer = null;
    public FloatBuffer texelBuffer = null;
    
	public ModelLoader(String file) {
		
		filename = new String(file);
		
		
		nvertices = 0;
		npositions = 0;
		ntexels = 0;
		nnormals = 0;
		nfaces = 0;
		
		getNumElements();

		
	}

	public void loadElements() {
		
		positions = new float[npositions][3];
		texels = new float[ntexels][2];      
		normals = new float[nnormals][3];      
		faces = new int[nfaces][9];  
		
		int p, t, n, f;
		
		p=t=n=f=0;
		
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filename));
 
			while ((sCurrentLine = br.readLine()) != null) {
				
				//System.err.println(sCurrentLine);
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("v "))
				{
					String [] coords = sCurrentLine.substring(2).split(" ");
					positions[p][0] = new Float(coords[coords.length-3]);
					positions[p][1] = new Float(coords[coords.length-2]);
					positions[p][2] = new Float(coords[coords.length-1]);
					p++;
				}
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("vn "))
				{
					String [] coords = sCurrentLine.substring(3).split(" ", 3);
					normals[n][0] = new Float(coords[0]);
					normals[n][1] = new Float(coords[1]);
					normals[n][2] = new Float(coords[2]);
					n++;
				}
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("vt "))
				{
					String [] coords = sCurrentLine.substring(3).split(" ", 3);
					texels[t][0] = new Float(coords[0]);
					texels[t][1] = new Float(coords[1]);
					t++;
				}
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("f "))
				{
					String [] coords = sCurrentLine.substring(2).split(" ");
					for(int i=0; i<3; i++) {
						String [] idx = coords[i].split("/", 3);
						faces[f][i*3+0] = new Integer(idx[0]);
						faces[f][i*3+1] = new Integer(idx[1]);
						faces[f][i*3+2] = new Integer(idx[2]);
					}
					
					f++;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		verticesBuffer = BufferUtils.createFloatBuffer(nfaces * 9);
		
		 for(int i=0; i<nfaces; i++)
		    {
		        // 3
		        int vA = faces[i][0] - 1;
		        int vB = faces[i][3] - 1;
		        int vC = faces[i][6] - 1;
		 
		        verticesBuffer.put(positions[vA][0]).put(positions[vA][1]).put(positions[vA][2]);
		        verticesBuffer.put(positions[vB][0]).put(positions[vB][1]).put(positions[vB][2]);
		        verticesBuffer.put(positions[vC][0]).put(positions[vC][1]).put(positions[vC][2]);
		    }

		 verticesBuffer.flip();
		
		 ligthsBuffer = BufferUtils.createFloatBuffer(nfaces * 9);
			
		 for(int i=0; i<nfaces; i++)
		    {
		        // 3
		        int vA = faces[i][2] - 1;
		        int vB = faces[i][5] - 1;
		        int vC = faces[i][8] - 1;
		 
		        ligthsBuffer.put(normals[vA][0]).put(normals[vA][1]).put(normals[vA][2]);
		        ligthsBuffer.put(normals[vB][0]).put(normals[vB][1]).put(normals[vB][2]);
		        ligthsBuffer.put(normals[vC][0]).put(normals[vC][1]).put(normals[vC][2]);
		    }

		 ligthsBuffer.flip();
		 
		 texelBuffer = BufferUtils.createFloatBuffer(nfaces * 9);
			
		 for(int i=0; i<nfaces; i++)
		    {
		        // 3
		        int vA = faces[i][1] - 1;
		        int vB = faces[i][4] - 1;
		        int vC = faces[i][7] - 1;
		 
		        texelBuffer.put(texels[vA][0]).put(texels[vA][1]);
		        texelBuffer.put(texels[vB][0]).put(texels[vB][1]);
		        texelBuffer.put(texels[vC][0]).put(texels[vC][1]);
		    }

		 texelBuffer.flip();
	}
	

	private void getNumElements() {
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filename));
 
			while ((sCurrentLine = br.readLine()) != null) {
				
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("v "))
					npositions++;
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("vn "))
					nnormals++;
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("vt "))
					ntexels++;
				if((sCurrentLine.length()>1) && sCurrentLine.startsWith("f "))
					nfaces++;
				
			}
			nvertices = nfaces*3;
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
			
	}

	public FloatBuffer getVertices() {
		
		return verticesBuffer;
	}
	
	public FloatBuffer getTextures() {
		
		return texelBuffer;
	}
	
	public FloatBuffer getligths() {
		
		return ligthsBuffer;
	}

	public int getNumVertices() {
		
		return nfaces*3;
	}

}
