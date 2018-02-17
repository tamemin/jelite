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
//	Universe class
//
// Contains:
//		All drawable models
//		planets for current galaxy
//		ships in current system
//		planets in current system
//		space stations in current system

import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;


class Universe
{
	public	final	static	int	OBJ_NONE		=	0;
	public	final	static	int	OBJ_STATION	=	1;
	public	final	static	int	OBJ_PLANET	=	2;
	public	final	static	int	OBJ_SUN		=	3;
	public	final	static	int	OBJ_SHIP		=	4;

   // Character types
   public	final static	int	C_TRADER		=	0,
   										C_MERCENARY	=	1,
                                 C_PIRATE		=	2,
                                 C_POLICE		=	3,
                                 C_TRANSPORT	=	4,
                                 C_MINER		=	5,
                                 C_SCAVENGER	=	6,
                                 C_KILLER		= 	7;
                                 
   public	final static	int	CT_TRADER		=	1<<C_TRADER,
   										CT_MERCENARY	=	1<<C_MERCENARY,
                                 CT_PIRATE		=	1<<C_PIRATE,
                                 CT_POLICE		=	1<<C_POLICE,
                                 CT_TRANSPORT	=	1<<C_TRANSPORT,
                                 CT_MINER			=	1<<C_MINER,
                                 CT_SCAVENGER	=	1<<C_SCAVENGER,
                                 CT_KILLER		= 	1<<C_KILLER;
                                 
   public	final static String	CT_NAME[]	=	new String[16];
   
	public	int		CurrentGalaxy;

	public	ModelThreeD	Model[]			=	new ModelThreeD[GameControl.NUM_3D_OBJS];

// Lists of objects
	public	Objct		ShipFree			=	new Objct();			// Free ship pool.
	public	Objct		ShipUsed			=	new Objct();			// Used ship list.
	public	Objct		Bodies			=	new Objct();			//	List of stations/suns/planets in current system
	
	public	Part		PartFree			=	new Part();
	public	Part		PartUsed			=	new Part();
	
// Actual objects
	public	Camera	camera				=	new Camera();
	
	public	ShipSimulator	ShipPlayer[]	=	new ShipSimulator[GameControl.NUM_PLAYERS];
	public	Planet	planets[]		=	new Planet[GameControl.NUM_PLANETS];
	public	Part		Part[]			=	new Part[GameControl.NUM_PARTS];
	public	StationModel	Station			=	new StationModel();
	public	SunThreeD		Sun				=	new SunThreeD();

// Mouse/menu control variables - probably shouldn't be here, but what the hell.	
	int		MouseX=0,
				MouseY=0;
	boolean	MouseClick	=	false;

	int		EquipState,
				EquipItem;
   
// Event handling
	int		iTimeSinceLastEvent;
	
 	public void universe()
 	{
 		CurrentGalaxy	=	0;
 		iTimeSinceLastEvent	=	0;

   	CT_NAME[C_TRADER]		=	"Trader";
   	CT_NAME[C_MERCENARY]	=	"Mercernary";
    CT_NAME[C_PIRATE]		=	"Pirate";
   	CT_NAME[C_POLICE]		=	"Police";
   	CT_NAME[C_TRANSPORT]	=	"Transporter";
   	CT_NAME[C_MINER]		=	"Miner";
   	CT_NAME[C_SCAVENGER]	=	"Scavenger";
   	CT_NAME[C_KILLER]		=	"Killer";
 	}
 	
 	public void initStaticLists()
 	{
		for(int i=0; i!=GameControl.NUM_PLANETS; i++)
			planets[i]	=	new Planet();
			
		for(int i=0; i!=GameControl.NUM_3D_OBJS; i++)
			Model[i]	=	new ModelThreeD();
		
		for(int i=0; i!=GameControl.NUM_SHIPS; i++)
		{
			ShipSimulator	s	=	new ShipSimulator();
			s.linkTo(ShipFree);
		}
		
		for(int i=0; i!=GameControl.NUM_PARTS; i++)
		{
			Part	p	=	new Part();
			p.linkTo(PartFree);
		}
		
 	}
 	
 	
 	public void initialiseGalaxy()
 	{
 		int	CG	=	CurrentGalaxy*10000;
 		
		System.out.println("Creating Planets");
		for(int i=0; i!=GameControl.NUM_PLANETS; i++)
			planets[i].setup(i+CG);
		System.out.println("Finished Planets");
 	}
 	

	public void loadModels(URL codebase)
	{ 	
   	int	i;

		System.out.println("IDJ:"+codebase);

      i	=	0;
      while(GameControl.ModInfo[i].Name != null)
      {
        System.out.println("gfx"+java.io.File.separator+GameControl.ModInfo[i].Name+".dat");
      	//Model[i].load(codebase, "gfx"+java.io.File.separator+gamecon.ModInfo[i].Name+".dat");
        Model[i].load(codebase, "gfx/"+GameControl.ModInfo[i].Name+".dat");
         i++;
      }
	}
	
	public void clearLists(boolean ClearPlayer)
	{
		// Clear used ship list - except player ships
		Objct	s	=	ShipUsed.Next,
						sNext;
		
		while(s!=null)
		{
			sNext	=	s.Next;
			if((ClearPlayer	|| !((ShipSimulator)s).PlayerCraft)
			&&	s.type!=OBJ_STATION)
			{
				s.linkTo(ShipFree);
			}
			s	=	sNext;
		}
		
		s	=	Bodies.Next;
		while(s!=null)
		{
			sNext	=	s.Next;
			s.unlink();
			s	=	sNext;
		}
		
		s	=	PartUsed.Next;
		while(s!=null)
		{
			sNext	=	s.Next;
			s.linkTo(PartFree);
			s	=	sNext;
		}
		
		iTimeSinceLastEvent	=	0;
	}
	
	public ShipSimulator getShip()
	{
		return((ShipSimulator) ShipFree.Next);
	}

	public void event()
	{
		boolean	eventOccurred	=	false;
		
		if(iTimeSinceLastEvent>300)
		{
			ShipSimulator	s	=	getShip();
			if(s!=null)
			{  
				MatrixMath43	mat	=	new MatrixMath43(),
							mat2	=	new MatrixMath43();
				Vectr		off	=	new Vectr(0,0,40000);
				
				s.defaults(GameControl.CobraIII);

            int ship;
            do{
   				ship  =  (int)(Math.abs(Math.random()*GameControl.MAX_MODELS));
               }while(GameControl.ModInfo[ship].BaseOccupation==0);
                       
            System.out.println("Created ship model: "+GameControl.ModInfo[ship].Name);
            
				s.mod(Model[ship]);
				s.Position.copy(ShipPlayer[0].Position);
				mat2.rotY((float)(Math.random()*Math.PI));
				mat.rotZ((float)(Math.random()*Math.PI));
				mat.mul(mat2);
				off.mul(mat);
				s.Position.add(off);

				// Setup AI inf
				double	job	=	Math.abs(Math.random());
			
					//s.AIattack(ShipPlayer[0]); // MOD - Do not attack my ship
          //s.AIfollow(ShipPlayer[0]);
   


				s.linkTo(ShipUsed);
			}
			
			eventOccurred	=	true;
		}
		
		if(eventOccurred)
			iTimeSinceLastEvent	=	0;
		else
			iTimeSinceLastEvent++;
	}
}
 