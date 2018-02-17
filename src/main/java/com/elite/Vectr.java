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
// 3D vector

class Vectr
{
	public float	x,y,z;
	public int		sX,sY;

	Vectr()
	{
		x	=	0;
		y	=	0;
		z	=	0;	
	}

	Vectr(float x, float y, float z)
	{
		this.x	=	x;
		this.y	=	y;
		this.z	=	z;	
	}

	Vectr(Vectr v)
	{
		x	=	v.x;
		y	=	v.y;
		z	=	v.z;
	}

	public void zero()
	{
		x	=	0;
		y	=	0;
		z	=	0;
	}

	public void set(float x, float y, float z)
	{
		this.x	=	x;
		this.y	=	y;
		this.z	=	z;	
	}

	public void copy(Vectr v)
	{
		x	=	v.x;
		y	=	v.y;
		z	=	v.z;
	}

	public void normalise()
	{
		float len = x*x + y*y + z*z;
		
		len	= (float) Math.sqrt((double)len);
		
		x	/=	len;
		y	/=	len;
		z	/=	len;
	}
	 
	public float size()
	{
		return((float)(Math.sqrt(x*x+y*y+z*z)));
	}
	 
	public void add(Vectr v)
	{
		this.x	+=	v.x;
		this.y	+=	v.y;
		this.z	+=	v.z;
	}

	public void add(float x, float y, float z)
	{
		this.x	+=	x;
		this.y	+=	y;
		this.z	+=	z;
	}
	
	public void sub(Vectr v)
	{
		this.x	-=	v.x;
		this.y	-=	v.y;
		this.z	-=	v.z;
	}

	public void sub(Vectr v, Vectr v2)
	{
		this.x	=	v.x-v2.x;
		this.y	=	v.y-v2.y;
		this.z	=	v.z-v2.z;
	}
	
	public void neg()
	{
		x=-x;
		y=-y;
		z=-z;
	}

	// Scalar
	public void mul(float s)
	{
		x	*=	s;
		y	*=	s;
		z	*=	s;
	}

	public void mul(Vectr v)
	{
		float	x	=	this.x,
				y	=	this.y,
				z	=	this.z;
				
		this.x	=	y*v.z - z*v.y;
		this.y	=	z*v.x - x*v.z;
		this.z	=	x*v.y - y*v.x;
	}
	
	public float dot(Vectr v)
	{
		float dot;
		dot	=	x*v.x + y*v.y + z*v.z;
		return(dot);
	}

	public void	mul(MatrixMath43 m)
	{
		float	x	=	this.x,
				y	=	this.y,
				z	=	this.z;

		this.x	=	x*m.m[0][0] + y*m.m[1][0] + z*m.m[2][0] + m.m[3][0];
		this.y	=	x*m.m[0][1] + y*m.m[1][1] + z*m.m[2][1] + m.m[3][1];
		this.z	=	x*m.m[0][2] + y*m.m[1][2] + z*m.m[2][2] + m.m[3][2];
	}

	// Create perspectivised, 2d screen coord, return order table value for point
	public int pers()
	{
		int	i;

		if(z==0)
		{
			sX	=	0;
			sY	=	0;
		}
		else
		{
      // do scaling for vectors using perspective figure
			sX	=	(int) (x*((GameControl.PERSPECTIVE_H*GameControl.EXPANSION_FACTOR)/z)) + GameControl.SCREEN_CEN_X;
			sY	=	GameControl.SCREEN_CEN_Y - (int) (y*((GameControl.PERSPECTIVE_H*GameControl.EXPANSION_FACTOR)/z));	// Make +Y up
		}

		i	=	(int) (z/4);
		if(i<0)	i=0;
		if(i>=GameControl.NUM_Z_BUCKETS) i=GameControl.NUM_Z_BUCKETS-1;
		return(i);
	}
}
