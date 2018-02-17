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
// ...

class	Camera extends Object3D
{
	private	ShipSimulator	Target;
		
	Camera()
	{
	}

	public void target(ShipSimulator tar)
	{
		Target	=	tar;
	}
	
   public void pos(Vectr v)
   {
   	Movement.copy(v);
   	Movement.sub(Position);
   	Position.copy(v);
   }
   
   public void ang(Vectr v)
   {
   	Angle.copy(v);
   }
   
   public void copy(Camera cam)	// Just enough for rendering purposes
   {
   	Position.copy(cam.Position);
   	Angle.copy(cam.Angle);
   	Mat.copy(cam.Mat);
   }
   
   public void update(ShipSimulator s)
   {
   	Target	=	s;  
   	update();
   }
   
   public void update()
   {  
   	MatrixMath43	m	=	new MatrixMath43();
   	MatrixMath43	m2	=	new MatrixMath43();
   	
		pos(Target.Position);
		
   	if(Target.iView==2)
   	{
	   	Vectr	v	=	new Vectr(0f,1f,0f);
   		m.copy(Target.Mat);
			v.mul(m);
			m2.rotateAbout(v, (float)(Math.PI));
			m.mul(m2);
			Mat.affineInverse(m);
   	}
   	else if(Target.iView==3)
   	{
	   	Vectr	v	=	new Vectr(0f,1f,0f);
   		m.copy(Target.Mat);
			v.mul(m);
			m2.rotateAbout(v, (float)(-Math.PI/2));
			m.mul(m2);
			Mat.affineInverse(m);
   	}
   	else if(Target.iView==4)
   	{
	   	Vectr	v	=	new Vectr(0f,1f,0f);
   		m.copy(Target.Mat);
			v.mul(m);
			m2.rotateAbout(v, (float)(Math.PI/2));
			m.mul(m2);
			Mat.affineInverse(m);
   	}
   	else
			Mat.affineInverse(Target.Mat);
   }
}

