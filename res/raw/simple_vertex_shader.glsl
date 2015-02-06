attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main()
{
	v_Color = a_Color;
	
	//lowp float point_size = 10.0f;
	gl_PointSize = 10.0;
	gl_Position = a_Position;
}