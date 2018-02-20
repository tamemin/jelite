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
// Simple particle class

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;


class Part extends LinkableObject {
	public	Vector		position = new Vector();
	public	Vector		angle = new Vector();
	public	Vector		renderPos = new Vector();
	public	Vector		movement = new Vector();
	public	MatrixMath43 matrix = new MatrixMath43();
	public	int	col;
	public	Color colour;	
	public	Vector move = new Vector();
	public	int	life;
	
	Part() {
		life =	0;
		col	=	3;
	}

	public void setup(MatrixMath43 m, Vector Pos) {
		matrix.copy(m);
		move.set(0f,10f,-100f);
		move.mul(matrix);				
		position.copy(Pos);	
		life	=	16;
	}
	
	// Draw object into order table
	public void render(Universe uni, Polygon OrderTable[], Polygon PolyFree, Vector light, MatrixMath43 m) {
		MatrixMath43	m2 = new MatrixMath43();
		Vector		v	= new Vector();

		life--;
		
		if(life==0) {
			linkTo(uni.partFree);
			return;
		}

		position.add(move);
	
	// Can now use m2 for lighting purposes
		m2.copy(m);
		m2.trans(0,0,0);

		int	x[] = new int[4];
		int y[] = new int[4];
	
		Polygon	p = PolyFree.Next;
	
		if(p == null)
			return;
			
		boolean	drawFlag	=	true;
		
		v.copy(position);
		v.sub(uni.camera.position);
		v.mul(m);
				
		if(v.z < (GameControl.PERSPECTIVE_H/2)) {
			// -ve z check; i.e. should clip with z=0 (or similar)
			//drawFlag	=	false;
			return;
		}
		
		if(SceneRenderer.fovClip(v)) {
			return;
		}
				
		int	iOT	= v.pers();
		
	
		if(drawFlag) {
			p.x[0]	=	v.sX;
			p.y[0]	=	v.sY;
			
			p.x[1]	=	(int)(10*GameControl.PERSPECTIVE_H/v.z)+1;
			p.y[1]	=	p.x[1];
					
			//int	Intensity = (int)(Math.acos(v.dot(light)) * (255/Math.PI)),
			//		colour	=	0;

			int	colour		=	0,
					Intensity	=	(life*16);
					
			if(Intensity<0)	Intensity	=	0;
			if(Intensity>255)	Intensity	=	255;
		
			//if((col&1)!=0)
			//	colour	+=	Intensity;
			//if((col&2)!=0)
				colour	+=	Intensity*256;
			//if((col&4)!=0)
				colour	+=	(128+Intensity/2)*65536;
		
			p.c			=	new Color(colour);
			p.iNSides	=	1;
			p.linkTo(OrderTable[iOT]);
		}
	}

}
