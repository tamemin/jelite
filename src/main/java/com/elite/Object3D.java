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
import java.io.*;
import java.net.*;
import java.util.*;


class Object3D extends Objct
{
	public	Vectr		Position		= new Vectr();
	public	Vectr		Angle			= new Vectr();
	public	Vectr		RenderPos	= new Vectr();
	public	Vectr		Movement		= new Vectr();
  public	MatrixMath43	Mat =	new MatrixMath43();
	private	Vectr		Min	=	new Vectr();
	private	Vectr		Max	=	new Vectr();
	public	int		Col;
	public	Color		Colour;
	public	float		Size;	// For planets/suns
	private	ModelThreeD	mod;


	public void dupe(Object3D o)	// Duplicate basic 3d obj info (not links)
	{
		Position.copy(o.Position);
		Angle.copy(o.Angle);	// Shouldn't be used
		Mat.copy(o.Mat);
		Min.copy(o.Min);		// Just for bounding box display
		Max.copy(o.Max);		// ditto
		Col		=	o.Col;
		Colour	=	o.Colour;
		mod		=	o.mod;
	}
	

	Object3D()
	{
		mod	=	null;
	}
	
   public void mod(ModelThreeD mod)
   {
   	this.mod	=	mod;
   	if(mod==null)
   		return;
   		
		Min.copy(mod.Min);
		Max.copy(mod.Max);
   }

	public ModelThreeD mod()
	{
		return(this.mod);
	}
   
   public void pos(float x, float y, float z)
   {
   	Position.x	=	x;
   	Position.y	=	y;
   	Position.z	=	z;
   }
   
   public void ang(float x, float y, float z)
   {
   	Angle.x	=	x;
   	Angle.y	=	y;
   	Angle.z	=	z;
   }

  	public void randomColour()
	{
		Col	=	(int)(Math.random()*8);
		if(Col<1)	Col	=	1;
		if(Col>7)	Col	=	7;

		int	c=0;

		if((Col&1)!=0)	c	+=	255;
		if((Col&2)!=0)	c	+=	255*256;
		if((Col&4)!=0)	c	+=	255*65536;
		Colour	=	new Color(c);
	}
	

// Collision code follows - should create obj3dcoll class at some stage
// For laser read ray/vector
// For ship read object
	public int	collideWithVec(Vectr Pos, Vectr Dir)
	{
		Vectr	p = new Vectr(Pos),
				d = new Vectr(Dir),
				v = new Vectr();

		MatrixMath43	m = new MatrixMath43();

		float	t;

		boolean	bHit	=	false;

		m.affineInverse(this.Mat);
		p.sub(this.Position);

		// make rays source and direction relative to ship
		p.mul(m);
		d.mul(m);
	
		// any point on ray is p + t*d

		// Do z plane checks
		if(d.z != 0)
		{
			// find where ray intersects plane z = Min.z i.e. p.z+t*d.z = Min.z, t = (Min.z-p.z)/d.z
			t		=	(Min.z-p.z)/d.z;
			if(t>0)	// Laser has specific dir
			{
				v.x	=	p.x+t*d.x;
				v.y	=	p.y+t*d.y;

				if(v.x>Min.x && v.x<Max.x && v.y>Min.y && v.y<Max.y)
					bHit	=	true;
				else
				{
					// z = Max.z
					t		=	(Max.z-p.z)/d.z;
					if(t>0)	// Laser has specific dir
					{
						v.x	=	p.x+t*d.x;
						v.y	=	p.y+t*d.y;
						if(v.x>Min.x && v.x<Max.x && v.y>Min.y && v.y<Max.y)
							bHit	=	true;
					}
				}
			}
		}
		
		if(d.x != 0 && !bHit)
		{
			// x = Min.x
			t		=	(Min.x-p.x)/d.x;
			if(t>0)	// Laser has specific dir
			{
				v.y	=	p.y+t*d.y;
				v.z	=	p.z+t*d.z;

				if(v.z>Min.z && v.z<Max.z && v.y>Min.y && v.y<Max.y)
					bHit	=	true;
				else
				{
					// x = Max.x
					t		=	(Max.x-p.x)/d.x;
					if(t>0)	// Laser has specific dir
					{
						v.y	=	p.y+t*d.y;
						v.z	=	p.z+t*d.z;
						if(v.z>Min.z && v.z<Max.z && v.y>Min.y && v.y<Max.y)
							bHit	=	true;
					}
				}
			}
		}

		if(d.y != 0 && !bHit)
		{
			// y = Min.y
			t		=	(Min.y-p.y)/d.y;
			if(t>0)	// Laser has specific dir
			{
				v.x	=	p.x+t*d.x;
				v.z	=	p.z+t*d.z;

				if(v.z>Min.z && v.z<Max.z && v.x>Min.x && v.x<Max.x)
					bHit	=	true;
				else
				{
					// y = Max.y
					t		=	(Max.y-p.y)/d.y;
					if(t>0)	// Laser has specific dir
					{
						v.x	=	p.x+t*d.x;
						v.z	=	p.z+t*d.z;
						if(v.z>Min.z && v.z<Max.z && v.x>Min.x && v.x<Max.x)
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

