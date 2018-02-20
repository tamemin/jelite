package com.elite;
/***************************************************************************
                          Java Elite style game
                             -------------------
    begin                : 10th May 1999
    copyright            : (C) 1999 by Simon Lacey (aka BootLace)
    email                : bootlace@btinternet.com
    web					 : http://bootlace.co.uk
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
// 4*3 Matrix - 3*3 rotation matrix, 1*3 translation

class	MatrixMath43
{
	float	m[][] = new float[4][3];

	MatrixMath43()
	{
		this.unit();
	}

	MatrixMath43(MatrixMath43 m)
	{
		this.copy(m);
	}
	
	public void unit()	// identity
	{
		for(int r=0; r!=4; r++)
			for(int c=0; c!=3; c++)
				m[r][c]	=	0;
		
		m[0][0]	=	1;
		m[1][1]	=	1;
		m[2][2]	=	1;
	}
	
	public void copy(MatrixMath43 m)
	{
		for(int r=0; r!=4; r++)
			for(int c=0; c!=3; c++)
				this.m[r][c] = m.m[r][c];
	}

	public void rotX(float a)
	{
		m[0][0]	=	1;
		m[0][1]	=	0;
		m[0][2]	=	0;

		m[1][0]	=	0;
		m[1][1]	=	(float)Math.cos((double)a);
		m[1][2]	=	-(float)Math.sin((double)a);

		m[2][0]	=	0;
		m[2][1]	=	(float)Math.sin((double)a);
		m[2][2]	=	(float)Math.cos((double)a);
	}

	public void rotY(float a)
	{
		m[0][0]	=	(float)Math.cos((double)a);
		m[0][1]	=	0;
		m[0][2]	=	-(float)Math.sin((double)a);

		m[1][0]	=	0;
		m[1][1]	=	1;
		m[1][2]	=	0;

		m[2][0]	=	(float)Math.sin((double)a);
		m[2][1]	=	0;
		m[2][2]	=	(float)Math.cos((double)a);
	}

	public void rotZ(float a)
	{
		m[0][0]	=	(float)Math.cos((double)a);
		m[0][1]	=	-(float)Math.sin((double)a);
		m[0][2]	=	0;

		m[1][0]	=	(float)Math.sin((double)a);
		m[1][1]	=	(float)Math.cos((double)a);
		m[1][2]	=	0;

		m[2][0]	=	0;
		m[2][1]	=	0;
		m[2][2]	=	1;
	}

	public void trans(float x, float y, float z)
	{
		m[3][0]	=	x;
		m[3][1]	=	y;
		m[3][2]	=	z;
	}
	
	public void affineInverse()
	{
		MatrixMath43 m = new MatrixMath43();
		m.copy(this);
		
		this.m[0][0]	=	m.m[0][0];
		this.m[0][1]	=	m.m[1][0];
		this.m[0][2]	=	m.m[2][0];
		
		this.m[1][0]	=	m.m[0][1];
		this.m[1][1]	=	m.m[1][1];
		this.m[1][2]	=	m.m[2][1];
		
		this.m[2][0]	=	m.m[0][2];
		this.m[2][1]	=	m.m[1][2];
		this.m[2][2]	=	m.m[2][2];
	}
	
	public void affineInverse(MatrixMath43 m)
	{
		this.m[0][0]	=	m.m[0][0];
		this.m[0][1]	=	m.m[1][0];
		this.m[0][2]	=	m.m[2][0];
		
		this.m[1][0]	=	m.m[0][1];
		this.m[1][1]	=	m.m[1][1];
		this.m[1][2]	=	m.m[2][1];
		
		this.m[2][0]	=	m.m[0][2];
		this.m[2][1]	=	m.m[1][2];
		this.m[2][2]	=	m.m[2][2];
	}
	 
	// Rotation about a general vector (axis of rotation)
	public void	rotateAbout(Vector l, float a)
	{
		MatrixMath43	mI		=	new MatrixMath43(),
					mL		=	new MatrixMath43(),
					mL2	=	new MatrixMath43();
		
		float	d	=	(float) Math.sqrt(l.x*l.x + l.y*l.y + l.z*l.z);
		
		mL.m[0][0] = 0;		mL.m[0][1] = l.z; 	mL.m[0][2] = -l.y;
		mL.m[1][0] = -l.z;	mL.m[1][1] = 0;		mL.m[1][2] = l.x;
		mL.m[2][0] = l.y;		mL.m[2][1] = -l.x;	mL.m[2][2] = 0;
		
		mL2.copy(mL);
		mL2.mul(mL);
		
		mL.mul((float)(Math.sin(a)/d));
		mL2.mul((float)((1-Math.cos(a))/(d*d)));
		
		mI.add(mL);
		mI.add(mL2);
		this.copy(mI);
	}
	
	// Mat + Mat
	public void add(MatrixMath43 mat)
	{
		for(int r=0; r!=3; r++)
			for(int c=0; c!=3; c++)
				this.m[r][c]	+=	mat.m[r][c];
	}
	 
	// Scalar * Mat
	public void mul(float f)
	{
		for(int r=0; r!=3; r++)
			for(int c=0; c!=3; c++)
				m[r][c]	*=	f;
	}
	
	// Mat * Mat
	public void mul(MatrixMath43 mat)
	{
		MatrixMath43 m = new MatrixMath43();
		Vector		v = new Vector();
		
		m.copy(this);
		
		v.x	=	this.m[3][0];
		v.y	=	this.m[3][1];
		v.z	=	this.m[3][2];
		
		v.mul(mat);
		
		this.m[0][0]	=	m.m[0][0]*mat.m[0][0] + m.m[0][1]*mat.m[1][0] + m.m[0][2]*mat.m[2][0];
		this.m[0][1]	=	m.m[0][0]*mat.m[0][1] + m.m[0][1]*mat.m[1][1] + m.m[0][2]*mat.m[2][1];
		this.m[0][2]	=	m.m[0][0]*mat.m[0][2] + m.m[0][1]*mat.m[1][2] + m.m[0][2]*mat.m[2][2];
		
		this.m[1][0]	=	m.m[1][0]*mat.m[0][0] + m.m[1][1]*mat.m[1][0] + m.m[1][2]*mat.m[2][0];
		this.m[1][1]	=	m.m[1][0]*mat.m[0][1] + m.m[1][1]*mat.m[1][1] + m.m[1][2]*mat.m[2][1];
		this.m[1][2]	=	m.m[1][0]*mat.m[0][2] + m.m[1][1]*mat.m[1][2] + m.m[1][2]*mat.m[2][2];
		                                                                       
		this.m[2][0]	=	m.m[2][0]*mat.m[0][0] + m.m[2][1]*mat.m[1][0] + m.m[2][2]*mat.m[2][0];
		this.m[2][1]	=	m.m[2][0]*mat.m[0][1] + m.m[2][1]*mat.m[1][1] + m.m[2][2]*mat.m[2][1];
		this.m[2][2]	=	m.m[2][0]*mat.m[0][2] + m.m[2][1]*mat.m[1][2] + m.m[2][2]*mat.m[2][2];
		
		this.m[3][0]	=	v.x + mat.m[3][0];
		this.m[3][1]	=	v.y + mat.m[3][1];
		this.m[3][2]	=	v.z + mat.m[3][2];
	}
}

