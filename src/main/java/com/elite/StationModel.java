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
// Space station

import	java.awt.*;

class StationModel extends Object3D
{
	public int	price[]			=	new int[GameControl.NUM_PRODUCTS];
	public int	numAvailable[]	=	new int[GameControl.NUM_PRODUCTS];

	public final static int AGRI		=	1<<0;
	public final static int INDU		=	1<<1;
	public final static int GOOD		=	1<<2;
	public final static int BAD		=	1<<3;
	public final static int LOTECH	=	1<<4;
	public final static int HITECH	=	1<<5;
	
	public final static int	RANGE		=	100000;

	public final static int	avPrice[]	=	new int[GameControl.NUM_PRODUCTS];
	public final static int	perWeight[]	=	new int[GameControl.NUM_PRODUCTS];
	public final static int	priceModifiers[]	=	new int[GameControl.NUM_PRODUCTS];
	
	StationModel()
	{
		type	=	Universe.OBJ_STATION;

		colour	=	Color.lightGray;
		col		=	7;

		priceModifiers[0]		=	AGRI;		//	Food
		priceModifiers[1]		=	AGRI;    //	Textiles
		priceModifiers[2]		=	AGRI+HITECH;		//	Radioactives
		priceModifiers[3]		=	AGRI+BAD;     //	Slaves
		priceModifiers[4]		=	AGRI;    //	Liquor/Wines
		priceModifiers[5]		=	AGRI;		//	Luxuries
		priceModifiers[6]		=	AGRI+BAD;        //	Narcotics
		priceModifiers[7]		=	INDU+HITECH;        //	Computers
		priceModifiers[8]		=	INDU;        //	Machinery
		priceModifiers[9]		=	AGRI+HITECH;        //	Alloys
		priceModifiers[10]	=	BAD;        //	Firearms
		priceModifiers[11]	=	AGRI+GOOD;        //	Furs
		priceModifiers[12]	=	AGRI;        //	Minerals
		priceModifiers[13]	=	GOOD;        //	Gold
		priceModifiers[14]	=	GOOD;        //	Platinum
		priceModifiers[15]	=	GOOD;       //	Gem-stones
		priceModifiers[16]	=	BAD;        //	Alien Items
		
		avPrice[0]	=	44;
		avPrice[1]	=	64;
		avPrice[2]	=	212;
		avPrice[3]	=	80;
		avPrice[4]	=	252;
		avPrice[5]	=	912;
		avPrice[6]	=	1148;
		avPrice[7]	=	840;
		avPrice[8]	=	564;
		avPrice[9]	=	328;
		avPrice[10]	=	704;
		avPrice[11]	=	560;
		avPrice[12]	=	80;
		avPrice[13]	=	372;
		avPrice[14]	=	652;
		avPrice[15]	=	164;
		avPrice[16]	=	270;

		perWeight[0]	=	1000000;
		perWeight[1]	=	1000000;
		perWeight[2]	=	1000000;
		perWeight[3]	=	1000000;
		perWeight[4]	=	1000000;
		perWeight[5]	=	1000000;
		perWeight[6]	=	1000000;
		perWeight[7]	=	1000000;
		perWeight[8]	=	1000000;
		perWeight[9]	=	1000000;
		perWeight[10]	=	1000000;
		perWeight[11]	=	1000000;
		perWeight[12]	=	1000;
		perWeight[13]	=	1000;
		perWeight[14]	=	1000;
		perWeight[15]	=	1;
		perWeight[16]	=	1000000;
	}

	public void setup(Planet p)
	{
		float	M,
				Mod;
		
		position.copy(p.position);
		position.z	-=	p.size + 10000;

		for(int i=0; i!=GameControl.NUM_PRODUCTS; i++)
		{
			numAvailable[i]	=	(int)(Math.random()*100);
			
			price[i]				=	(int)(avPrice[i] * (1+ ((Math.random()-.5)/10)));
			
			M	=	0;
			
			if((priceModifiers[i] & (AGRI+INDU)) != 0)
			{
				Mod	=	p.iIndustry - 3;
				Mod	*=	(float)(Math.random()*10);
				
				if((priceModifiers[i] & AGRI) != 0)
					M	+=	Mod;
				else
					M	-=	Mod;
			}

			if((priceModifiers[i] & (GOOD+BAD)) != 0)
			{
				Mod	=	(p.iPolitics - 4)*0.7f;
				Mod	*=	(float)(Math.random()*10);
				
				if((priceModifiers[i] & GOOD) != 0)
					M	+=	Mod;
				else
					M	-=	Mod;
			}
			
			if((priceModifiers[i] & (LOTECH+HITECH)) != 0)
			{
				Mod	=	(p.iTechLevel - 5)*0.6f;
				Mod	*=	(float)(Math.random()*10);
				
				if((priceModifiers[i] & LOTECH) != 0)
					M	+=	Mod;
				else
					M	-=	Mod;
			}
			
			price[i]	+=	(int)((price[i]*M)/100);
		}
	}	

   public void run()
   {
   	MatrixMath43	m	=	new MatrixMath43();
   					
   	Vector		thrust	=	new Vector();

		thrust.set(0f,0f,1f);
		thrust.mul(matrix);
		m.rotateAbout(thrust, (float)(-Math.PI/270));
		matrix.mul(m);
   }
}