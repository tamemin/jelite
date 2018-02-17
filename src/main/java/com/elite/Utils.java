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
// Misc useful methods that require no class dependencies
// Cough, except, cough, standard, cough, API, c c c cough.
import	java.awt.*;

class Utils
{
	Utils()
	{
	}

	public static float moveTo(float Cur, float Max, float Delta)
	{
		if(Delta!=0)
		{
			if(Delta>0)
			{
				Cur	+=	Delta;
				if(Cur>Max)
					Cur	=	Max;
			}
			else
			{
				Cur	+=	Delta;
				if(Cur<-Max)
					Cur	=	-Max;
			}
		}

		return(Cur);
	}

	public static float moveToTarget(float Cur, float Tar, float Speed)
	{
		if(Cur!=Tar)
		{
			if(Cur<Tar)
			{
				Cur	+=	Speed;
				if(Cur>Tar)
					Cur	=	Tar;
			}
			else
			{
				Cur	-=	Speed;
				if(Cur<Tar)
					Cur	=	Tar;
			}
		}
		
		return(Cur);
	}
	
	public static void drawStringCentre(Graphics g, String s, int y)
	{
		FontMetrics	f =	g.getFontMetrics();
		g.drawString(s, GameControl.SCREEN_CEN_X-f.stringWidth(s)/2, y);
	}

	public static void drawStringRight(Graphics g, String s, int x, int y)
	{
		FontMetrics	f =	g.getFontMetrics();
		g.drawString(s, x-f.stringWidth(s), y);
	}
	
	public static int	SQR(int x)
	{
		return(x*x);
	}
	
	public static float	SQR(float x)
	{
		return(x*x);
	}
	
	public static float atan(float opp, float adj)
	{
		float ang;
		
		if(adj==0)
		{
			if(opp==0)
				ang	=	0;
			else if(opp>=0)
				ang	=	(float) Math.PI/2;
			else
				ang	=	(float) -Math.PI/2;
		}
		else
		{
			if(adj>0)
			{
				ang	=	(float) Math.atan(opp/adj);
			}
			else
			{
				ang	=	(float) (Math.PI - Math.atan(-opp/adj));
				if(ang>(float)Math.PI)
					ang	-=	(float)(Math.PI*2);
			}
		}
	
		return(ang);
	}
}