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
// Basic object class
// provides simple linked listing

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;


class Objct
{
	int	type;
	
	Objct	Next;
	Objct	Prev;
	
	Objct()
	{
		type	=	0;
		Next	=	null;
		Prev	=	null;
	}
	
	
   public void linkTo(Objct p)
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
}

