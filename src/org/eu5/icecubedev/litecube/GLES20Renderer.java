package org.eu5.icecubedev.litecube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import static android.opengl.GLES20.*;

import org.eu5.icecubedev.litecube.util.LoggerConfig;
import org.eu5.icecubedev.litecube.util.ShaderHelper;
import org.eu5.icecubedev.litecube.util.TextResourceLoader;

public class GLES20Renderer implements Renderer {

	private static final String A_POSITION = "a_Position";
	private static final String A_COLOR = "a_Color";
	
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE =
			(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final String TAG = "GLES20Renderer";
	
	private int program;
	private int aPositionLocation;
	private int aColorLocation;
	
	private final Context context;
	private final FloatBuffer vertexData;
	
	
	public GLES20Renderer(Context context) {
		this.context = context;
		
		float[] tableVerticies = {
				// Order of coordinates: X, Y, R, G, B
				
				// Table
				 0.0f,  0.0f,  1.0f, 1.0f, 1.0f,
				-0.5f, -0.5f,  0.7f,0.7f,0.7f,
				 0.5f, -0.5f,  0.7f,0.7f,0.7f,
				 0.5f,  0.5f,  0.7f,0.7f,0.7f,
				-0.5f,  0.5f,  0.7f,0.7f,0.7f,
				-0.5f, -0.5f,  0.7f,0.7f,0.7f,
				
				// Middle line
				-0.5f, 0.0f,  1.0f,0.0f,0.0f,
				 0.5f, 0.0f,  1.0f,0.0f,0.0f,
				
				// Mallets
				0.0f, -0.25f,  0.0f,1.0f,0.0f,
				0.0f,  0.25f,  1.0f,0.0f,0.0f,
				
				// Borders
				-0.5f, -0.5f,  1.0f,0.0f,0.0f,
				-0.48f, 0.5f,  1.0f,0.0f,0.0f,
				-0.5f,  0.5f,  1.0f,0.0f,0.0f,
				-0.5f, -0.5f,  1.0f,0.0f,0.0f,
				-0.48f,-0.5f,  1.0f,0.0f,0.0f,
				-0.48f, 0.5f,  1.0f,0.0f,0.0f,
				
				-0.5f, -0.5f,   1.0f,0.0f,0.0f,
				 0.5f, -0.48f,  1.0f,0.0f,0.0f,
				 0.5f, -0.5f,   1.0f,0.0f,0.0f,
				-0.5f, -0.5f,   1.0f,0.0f,0.0f,
				-0.5f, -0.48f,  1.0f,0.0f,0.0f,
				 0.5f, -0.48f,  1.0f,0.0f,0.0f,
				 
				 0.5f,  -0.5f,  1.0f,0.0f,0.0f,
				 0.48f,  0.5f,  1.0f,0.0f,0.0f,
				 0.5f,   0.5f,  1.0f,0.0f,0.0f,
				 0.5f,  -0.5f,  1.0f,0.0f,0.0f,
				 0.48f, -0.5f,  1.0f,0.0f,0.0f,
				 0.48f,  0.5f,  1.0f,0.0f,0.0f,
				 
				  0.5f, 0.5f,   1.0f,0.0f,0.0f,
				 -0.5f, 0.5f,   1.0f,0.0f,0.0f,
				  0.5f, 0.48f,  1.0f,0.0f,0.0f,
				  0.5f, 0.48f,  1.0f,0.0f,0.0f,
				 -0.5f, 0.48f,  1.0f,0.0f,0.0f,
				 -0.5f, 0.5f,   1.0f,0.0f,0.0f,
				 
				 // Puck
				 0.0f, 0.0f,  0f,0f,0f
		};
		
		vertexData = ByteBuffer
				// First we allocate a block of native memory which will not be
				// managed by the garbage collector.
				.allocateDirect(tableVerticies.length * BYTES_PER_FLOAT)
				// Use the same byte order as the platform.
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		vertexData.put(tableVerticies);
		
		Log.d(TAG, "Invalid value: " + GL_INVALID_VALUE); 
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Draw the table
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		
		// Draw the dividing line
		glDrawArrays(GL_LINES, 6, 2);
		
		// Draw the two mallets
		glDrawArrays(GL_POINTS, 8, 1);
		glDrawArrays(GL_POINTS, 9, 1);
		
		// Draw the borders
		glDrawArrays(GL_TRIANGLES, 10, 24);
		
		// Draw the puck
		glDrawArrays(GL_POINTS, 34, 1);
		
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);

	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		glClearColor(0.0f, 162.0f/255.0f, 232.0f/255.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_GEQUAL);
		glClearDepthf(0.0f);
		
		// Get shader sources
		String vertexShaderSource = TextResourceLoader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceLoader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		
		// Compile the shaders
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		// Compile and link the program
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
		
		// Get the location of the uniforms and attributes
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		
		Log.d(TAG, "Position attribute location: " + aPositionLocation);
		Log.d(TAG, "Color attribute location: " + aColorLocation);
		
		vertexData.position(0);
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, 
				false, STRIDE, vertexData);	
		glEnableVertexAttribArray(aPositionLocation);
		
		vertexData.position(POSITION_COMPONENT_COUNT);
		glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
				false, STRIDE, vertexData);
		glEnableVertexAttribArray(aColorLocation);
		
		// Use the newly create program
		glUseProgram(program);
	}
}
