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


class ModelInfo {

   public String name;
   public int	type;
   public int	baseOccupation;
   public int	actualOccupation;
   public int	alignment;
   
	public ModelInfo() {
	   	name = null;
		type  =	0;
		baseOccupation = 0;
		actualOccupation = 0;
		alignment =	0;
	}
}

