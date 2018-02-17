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
// General purpose polygon primative class

import java.awt.*;

class	Polygon
{
	public Polygon	Prev;
	public Polygon	Next;

	public int	x[] = new int[Face.MAX_V];
	public int	y[] = new int[Face.MAX_V];
	
	public int	iNSides;

	public Color	c;

	Polygon()
	{
		iNSides	=	0;
		Prev		=	null;
		Next		=	null;
	}

   public void linkTo(Polygon p)
   {
      if(this.Next!=null || this.Prev!=null)    this.unlink();
      if(p == null)   return;

      this.Next   =  p.Next;
      this.Prev   =  p;
      p.Next		=  this;

      if(this.Next != null)
         this.Next.Prev =  this;
   }

   public void unlink()
   {
      if(this.Prev != null)
      {
         this.Prev.Next =  this.Next;
      }

      if(this.Next != null)
      {
         this.Next.Prev =  this.Prev;
      }

      this.Next   =  null;
      this.Prev   =  null;
   }
   
	public static void drawOrderTable(Graphics g, Polygon OrderTable[], Polygon PolyFree)
	{
   	if(GameControl.bFilled)
		{
			for(int i=GameControl.NUM_Z_BUCKETS-1; i>0; i--)
			{
				Polygon	p = OrderTable[i].Next,
						pNext;
	
				while(p != null)
				{
					pNext	=	p.Next;
	
					g.setColor(p.c);
						
					switch(p.iNSides)
					{
						case	1:
							g.fillOval(p.x[0]-p.x[1]/2, p.y[0]-p.y[1]/2, p.x[1],p.y[1]);
							break;
							
						case	3:
						case	4:
                  default:
							if(p.x[1]<3 & p.y[1]<3)
								g.fillRect(p.x[0]-p.x[1]/2, p.y[0]-p.y[1]/2, p.x[1],p.y[1]);
							else
                     {
								g.fillPolygon(p.x, p.y, p.iNSides);
                        
								g.setColor(Color.black);
								g.drawPolygon(p.x, p.y, p.iNSides);
                     }
							break;
					}
	
					p.linkTo(PolyFree);
					p	=	pNext;
				}
			}
		}
		else
		{
			for(int i=GameControl.NUM_Z_BUCKETS-1; i>0; i--)
			{
				Polygon	p = OrderTable[i].Next,
						pNext;
	
				while(p != null)
				{
					pNext	=	p.Next;
	
					g.setColor(p.c);
					
					switch(p.iNSides)
					{
						case	1:
							if(p.x[1]<3 & p.y[1]<3)
								g.drawRect(p.x[0]-p.x[1]/2, p.y[0]-p.y[1]/2, p.x[1],p.y[1]);
							else
								g.drawOval(p.x[0]-p.x[1]/2, p.y[0]-p.y[1]/2, p.x[1],p.y[1]);
							break;
							
						case	3:
						case	4:
                  default:
							g.drawPolygon(p.x, p.y, p.iNSides);
							break;
					}
	
					p.linkTo(PolyFree);
					p	=	pNext;
				}
			}
		}
	}
 
 	public static void drawOrderTable(SceneRenderer r, Polygon OrderTable[], Polygon PolyFree)
	{
		
	}

}

