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
// Face data class

import java.awt.*;


class	Face
{
   public final static int MAX_V	=	8;

   public int	num_v;
	public int	v[] = new int[MAX_V];
	Color	c;

	Vector	normal = new Vector();

	Face()
	{
		int i;

      num_v	=	0;
      
		for(i=0; i!=MAX_V; i++)
			v[i]	=	0;

		//i	=	(int)(0xffffff * Math.random());
		//c	=	new Color(i);
      c	=	Color.black;
	}

	Face(int v0, int v1, int v2, int v3)
	{
		v[0]	=	v0;
		v[1]	=	v1;
		v[2]	=	v2;
		v[3]	=	v3;
	}

   Face(int v2[])

   {
   	num_v	=	v2[0];
      if(num_v>MAX_V)	num_v	=	MAX_V;

      for(int i=0; i!=num_v; i++)
      {
      	v[i]	=	v2[i+1];
      }
   }
}

