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
// Simple 3d object class

import java.awt.*;


class Object3D extends LinkableObject
{
	public	Vector		position		= new Vector();
	public	Vector		angle			= new Vector();
	public	Vector		renderPos		= new Vector();
	public	Vector		movement		= new Vector();
	public	MatrixMath43 matrix =	new MatrixMath43();
	private	Vector		min	=	new Vector();
	private	Vector		max	=	new Vector();
	public	int			col;
	public	Color		colour;
	public	float		size;	// For planets/suns
	private	ModelThreeD	model;


	/**
	 * Duplicate basic 3d obj info (not links)
	 * 
	 * @param o
	 */
	public void duplicate(Object3D o) {
		position.copy(o.position);
		angle.copy(o.angle);	// Shouldn't be used
		matrix.copy(o.matrix);
		min.copy(o.min);		// Just for bounding box display
		max.copy(o.max);		// ditto
		col		=	o.col;
		colour	=	o.colour;
		model		=	o.model;
	}
	

	Object3D()
	{
		model	=	null;
	}
	
   public void setModel(ModelThreeD mod) {
   	this.model	=	mod;
   	if(mod==null)
   		return;
   		
		min.copy(mod.Min);
		max.copy(mod.Max);
   }

	public ModelThreeD getModel() {
		return(this.model);
	}
   
   public void setPosition(float x, float y, float z) {
   	position.x	=	x;
   	position.y	=	y;
   	position.z	=	z;
   }
   
   public void ang(float x, float y, float z)
   {
   	angle.x	=	x;
   	angle.y	=	y;
   	angle.z	=	z;
   }

  	public void randomColour()
	{
		col	=	(int)(Math.random()*8);
		if(col<1)	col	=	1;
		if(col>7)	col	=	7;

		int	c=0;

		if((col&1)!=0)	c	+=	255;
		if((col&2)!=0)	c	+=	255*256;
		if((col&4)!=0)	c	+=	255*65536;
		colour	=	new Color(c);
	}
	

// Collision code follows - should create obj3dcoll class at some stage
// For laser read ray/vector
// For ship read object
	public int	collideWithVec(Vector Pos, Vector Dir)
	{
		Vector	p = new Vector(Pos),
				d = new Vector(Dir),
				v = new Vector();

		MatrixMath43	m = new MatrixMath43();

		float	t;

		boolean	bHit	=	false;

		m.affineInverse(this.matrix);
		p.sub(this.position);

		// make rays source and direction relative to ship
		p.mul(m);
		d.mul(m);
	
		// any point on ray is p + t*d

		// Do z plane checks
		if(d.z != 0)
		{
			// find where ray intersects plane z = Min.z i.e. p.z+t*d.z = Min.z, t = (Min.z-p.z)/d.z
			t		=	(min.z-p.z)/d.z;
			if(t>0)	// Laser has specific dir
			{
				v.x	=	p.x+t*d.x;
				v.y	=	p.y+t*d.y;

				if(v.x>min.x && v.x<max.x && v.y>min.y && v.y<max.y)
					bHit	=	true;
				else
				{
					// z = Max.z
					t		=	(max.z-p.z)/d.z;
					if(t>0)	// Laser has specific dir
					{
						v.x	=	p.x+t*d.x;
						v.y	=	p.y+t*d.y;
						if(v.x>min.x && v.x<max.x && v.y>min.y && v.y<max.y)
							bHit	=	true;
					}
				}
			}
		}
		
		if(d.x != 0 && !bHit)
		{
			// x = Min.x
			t		=	(min.x-p.x)/d.x;
			if(t>0)	// Laser has specific dir
			{
				v.y	=	p.y+t*d.y;
				v.z	=	p.z+t*d.z;

				if(v.z>min.z && v.z<max.z && v.y>min.y && v.y<max.y)
					bHit	=	true;
				else
				{
					// x = Max.x
					t		=	(max.x-p.x)/d.x;
					if(t>0)	// Laser has specific dir
					{
						v.y	=	p.y+t*d.y;
						v.z	=	p.z+t*d.z;
						if(v.z>min.z && v.z<max.z && v.y>min.y && v.y<max.y)
							bHit	=	true;
					}
				}
			}
		}

		if(d.y != 0 && !bHit)
		{
			// y = Min.y
			t		=	(min.y-p.y)/d.y;
			if(t>0)	// Laser has specific dir
			{
				v.x	=	p.x+t*d.x;
				v.z	=	p.z+t*d.z;

				if(v.z>min.z && v.z<max.z && v.x>min.x && v.x<max.x)
					bHit	=	true;
				else
				{
					// y = Max.y
					t		=	(max.y-p.y)/d.y;
					if(t>0)	// Laser has specific dir
					{
						v.x	=	p.x+t*d.x;
						v.z	=	p.z+t*d.z;
						if(v.z>min.z && v.z<max.z && v.x>min.x && v.x<max.x)
							bHit	=	true;
					}
				}
			}
		}

		// Hit front or rear shields?
		int	ColValue=0;
		if(bHit)
		{
			if(v.z>=0)
				ColValue	=	1;
			else
				ColValue	=	2;
		}

		return(ColValue);
	}
}

