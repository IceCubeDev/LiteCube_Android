package org.eu5.icecubedev.litecube.util;

import static android.opengl.GLES20.*;
import android.opengl.GLU;
import android.util.Log;

public class ShaderHelper {

	private static final String TAG = "ShaderHelper";
	
	public static int compileVertexShader(String shaderCode) {
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}
	
	public static int compileFragmentShader(String shaderCode) {
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
	}
	
	private static int compileShader(int type, String shaderCode) {
		// We create an OpenGL Shader Object
		final int shaderObjectId = glCreateShader(type);
		
		// Check for errors
		if (shaderObjectId == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "Could not create new shader.");
			}
			
			return 0;
		}
		
		// Upload shader source to GPU
		glShaderSource(shaderObjectId, shaderCode);
		// Compile the shader
		glCompileShader(shaderObjectId);
		
		// Check for compilation errors
		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		
		if (LoggerConfig.ON) {
			//Print the shader info log to the Android log output.
			Log.v(TAG,"Results of compiling source: " + 
					"\n" + shaderCode + "\n:"
					+ glGetShaderInfoLog(shaderObjectId));
		}
		
		// Verify the compile status
		if (compileStatus[0] == 0) {
			// If it failed, delete the shader object.
			glDeleteShader(shaderObjectId);
			
			if (LoggerConfig.ON) {
				Log.w(TAG, "Compilation of shader failed.");
			}
			
			return 0;
		}
		
		return shaderObjectId;
	}
	
	public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
		// Create a new Shader Program Object
		final int programObjectId = glCreateProgram();
		checkGLError("glCreteProgram");
		
		// Check for errors
		if (programObjectId == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "Could not create new program");
			}
			
			return 0;
		}
		
		// Attach the shaders to the program
		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);
		checkGLError("glAttachShader");
		
		// Link the program
		glLinkProgram(programObjectId);
		checkGLError("glLinkProgram");
		
		final int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
		
		if (LoggerConfig.ON) {
			// Print the program info log to the Android log output.
			Log.v(TAG, "Results of linking program:\n"
					+ glGetProgramInfoLog(programObjectId));
		}
		
		// Verify link status
		if (linkStatus[0] == 0) {
			// If it failed, delete the program object
			glDeleteProgram(programObjectId);
			
			if (LoggerConfig.ON) {
				Log.w(TAG, "Linking of program failed.");
			}
			
			return 0;
		}
		
		return programObjectId;
	}
	
	public static boolean validateProgram(int programObjectId) {
		glValidateProgram(programObjectId);
		checkGLError("glValidateProgram");
		
		final int[] validateStatus = new int[1];
		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
		Log.v(TAG, "Results of validating program: " + validateStatus[0] + 
				"\nLog:" + glGetProgramInfoLog(programObjectId));
		
		return validateStatus[0] != 0;
	}
	
	private static void checkGLError(String op) {
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error +
            		"\n:" + GLU.gluErrorString(error));
        }
    }
}
