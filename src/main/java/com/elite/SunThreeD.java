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

class SunThreeD extends Object3D
{
	// Local random number gen variables for planet creation
	Random Rand	=	new Random();

	SunThreeD()
	{
		type	=	Universe.OBJ_SUN;
	}

	SunThreeD(int iSeed)
	{
		type	=	Universe.OBJ_SUN;
		setup(iSeed);
	}

	public	void	setup(int iSeed)
	{
		Rand.setSeed(iSeed);
		Position.z	=	-200000;	//	 This is really hyperspace dependant
		Size			=	10000 + rand(10) * 10000;
		this.Colour	=	Color.yellow;
	}

	private	int rand(int iMax)
	{
		int	iR	=	(Math.abs(Rand.nextInt()))%(iMax);
		return(iR);
	}
}


