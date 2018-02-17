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
// Class for model infomation

import java.awt.*;
import java.util.*;

class ModelInfo
{
	// Model info/data
	public String	Name;

   public int	Type;
   public int	BaseOccupation;
   public int	ActualOccupation;
   public int	Alignment;
   
   // 
   
	ModelInfo()
	{
   	Name	=	null;

      Type				   =	0;
      BaseOccupation	   =	0;
      ActualOccupation	=	0;
      Alignment			=	0;
	}
}

