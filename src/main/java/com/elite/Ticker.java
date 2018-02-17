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
 // Provide some text display options
 // Just a simple one to start with

import	java.awt.*;
 
 class Ticker
 {
 	private	final static int	MAX = 20;
 	
 	private	static boolean		Active;
 	private	static String		str[]	=	new String[MAX];
 	private	static int			Speed,
 										TimeCur,
			 							Current,
			 							SX,
			 							SY,
			 							Num;
			 							
	private	static Color		Col;
 	
 	Ticker()
 	{
 		Active	=	false;
	}

   public void setupSimple(String str[], int first, int last, int speed, int sx, int sy, Color col)
   {
 		Active	=	false;
 		
 		Num	=	last-first+1;
 		
 		if(Num<1 || Num > MAX)
 			return;
 			
 		for(int i=0; i!=Num; i++)
 		{
 			this.str[i]	=	str[first+i];
 		}
 		
 		Speed	=	speed;
 		if(Speed<1)
 			return;
 		
 		SX		=	sx;
 		SY		=	sy;
 		Col	=	col;
 		
 		Current	=	0;
 		TimeCur	=	0;
 		Active	=	true;
   }
   
   public void update(Graphics g)
   {
   	if(!Active)	return;
   	
   	if(++TimeCur > Speed)
   	{
   		TimeCur	=	0;
   		if(++Current >= Num)
   			Current	=	0;
   	}
   	
   	g.setColor(Col);
   	
   	g.drawString(str[Current], SX,SY);
   }
 }