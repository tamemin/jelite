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
// Class for most planet handling - including planet types, galactic position, etc.

import java.awt.*;
import java.util.*;

class Planet extends Object3D
{
	// Local random number gen variables for planet creation
	Random Rand	=	new Random();

	// Planet info/data
	public String	Name;
	public int		iPolitics;
	public int		iIndustry;
	public int		iSpecies;
	public int		iSpeciesC;
	public int		iTechLevel;
	public int		iPlanetC;

	public Vectr		sPos		=	new Vectr();

	Planet()
	{
		type	=	Universe.OBJ_PLANET;
	}

	Planet(int iSeed)
	{
		type	=	Universe.OBJ_PLANET;
		setup(iSeed);
	}

	public	void	setup(int iSeed)
	{
		Rand.setSeed(iSeed);

		Name			=	randName();
		Size			=	100000 + rand(10) * 10000;
		sPos.x		=	rand(GameControl.SCREEN_WIDTH-100)+50+(rand(100)/100);
		sPos.y		=	rand(640)+(rand(100)/100);
		sPos.z		=	rand(GameControl.SCREEN_HEIGHT-100)+50+(rand(100)/100);

		iPolitics	=	rand(8);
		iIndustry	=	rand(7);
		iSpecies		=	rand(4);
		iSpeciesC	=	rand(8);
		iTechLevel	=	rand(13);

		Position.z	=	1000000 + rand(100)*30000;	//	 This is really hyperspace dependant

		iPlanetC		=	rand(8);
		if(iPlanetC==0)	iPlanetC=4;
		if(iPlanetC==3)	iPlanetC=2;
		
		int	c	=	0;
		if((iPlanetC&4) != 0)	c+=255;
		if((iPlanetC&2) != 0)	c+=255*256;
		if((iPlanetC&1) != 0)	c+=255*65536;
		this.Colour	=	new Color(c);
	}

	private	int rand(int iMax)
	{
		int	iR	=	(Math.abs(Rand.nextInt()))%(iMax);
		return(iR);
	}

	private	String randName()
	{
		String	s	=	new String();

		int		iL	=	rand(6) + 5;
		int		iCount	=	0,
					iLast		=	0,
					iCur;


		for(int i=0; i!=iL; i++)
		{
			if(i==0)
			{
				s	+=	(char)('A'+rand(26));
				switch(s.charAt(i))
				{
					case	'A':
					case	'E':
					case	'I':
					case	'O':
					case	'U':
					case	'Y':
						iLast		=	0;
						break;
					default:
						iLast		=	1;
						break;
				}
				iCount	=	1;
			}
			else
			{
				int	c;

				do{
					c	=	'a'+rand(26);

					switch((char)c)
					{
						case	'a':
						case	'e':
						case	'i':
						case	'o':
						case	'u':
						case	'y':
							iCur	=	0;
							break;

						default:
							iCur	=	1;
							break;
					}
					
					if(iCur==iLast)
					{
						iCount++;
					}
					else
					{
						iCount	=	0;
					}
				}	while(iCount>1 && iCount<50);

				iLast	=	iCur;
				s		+=	(char)c;
			}
		}

		return(s);
	}
	
	
	public float	distanceFrom(Planet p)
	{
		float Dist;
		
		Dist	=	Utils.SQR(p.sPos.x - sPos.x);
		Dist	+=	Utils.SQR(p.sPos.z - sPos.z);
					
		Dist	=	(float)(Math.sqrt(Dist)/20*7);
		
		int round	=	(int)(Dist*10);
		Dist	=	((float)round)/10;
		return(Dist);
	}
	
}


