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
// Game context class
// This essentially includes variables that would perhaps normally
// be declared global. Essentially, this class is used to pass miscellaneous
// data between other classes the project uses.

class GameControl
{
	
	public static Planet planets[];

	public final static boolean	bDebug = true;
	public static boolean			bIE5mode = true;
	
	public final static float SHIP_RANGE	=	60000;
	
	public final static int SCREEN_WIDTH	=	640;
	public final static int SCREEN_HEIGHT	=	480;

	public final static int SCREEN_CEN_X	=	(SCREEN_WIDTH/2);
	public final static int SCREEN_CEN_Y	=	(SCREEN_HEIGHT/2);

	public final static int PERSPECTIVE_H	=	640;
  public final static int EXPANSION_FACTOR = 4; // Added this to make things look much bigger when you get closer
  public final static int Z_OFFSET = 800; // Added this to make sure objects remain visible until they are well behind the viewer
	public final static int NUM_3D_OBJS		=	32;
	public final static int NUM_Z_BUCKETS	=	1024;
	public final static int NUM_FREE_POLYS	=	4096;
	public final static int NUM_PLAYERS		=	1;
	public final static int NUM_STARS		=	100;
	public final static int NUM_STARS_REL	=	100;
	public final static int NUM_PLANETS		=	500;
	public final static int NUM_PARTS		=	2000;
	
	public final static int NUM_SHIPS		=	4;
	
	public final static int MAX_DYNAMIC_OBJS	=	NUM_SHIPS;


	public final static int NUM_PRODUCTS	=	17;
	
	public final static int MODE_TITLE		=	100;
	public final static int MODE_START		=	200;
	public final static int MODE_DOCKED		=	300;
	public final static int MODE_LAUNCH		=	400;
	public final static int MODE_SPACE		=	500;
	public final static int MODE_HYPER		=	600;
	public final static int MODE_DOCK		=	700;
	public final static int MODE_AUTODOCK1	=	800;
	public final static int MODE_AUTODOCK2	=	900;
	
	public final static int CobraIII			=	0;
	public final static int Missile			=	1;
	
	public static int			CURRENT_MODE	=	MODE_TITLE;
	public static int			OLD_MODE			=	MODE_TITLE;
	public static int			TIME_IN_MODE	=	0;
	
	public static boolean	bFilled		=	true;
	public static boolean	bPart			=	false; 
	public static boolean	bBounding	=	true;
	public static boolean	bDispHelp	=	false;
	
	public final static String VIEW[]	=	new String[11];
	
	public final static String UPGRADES[]	=	new String[32];
	public final static int		UPTECH[]		=	new int[32];
	public 		 static int		UPCOST[]		=	new int[32];
	
	public final static String ITEM[]		=	new String[32];
	public final static String POLITICS[]	=	new String[32];
	public final static String INDUSTRY[]	=	new String[32];
	public final static String SPECIES[]	=	new String[32];
	public final static String COLOURS[]	=	new String[32];
	public final static String POLICE[]		=	new String[32];
	public final static String MISC[]		=	new String[256];
  public final static int	MAX_MODELS		=	32;
	public final static ModelInfo ModInfo[]	=	new ModelInfo[MAX_MODELS];

	public final static int		LaserRate[]		=	new int[4];
	public final static int		LaserHeat[]		=	new int[4];
	public final static int		LaserDamage[]	=	new int[4];

	GameControl()
	{
	// Some, but not all initialisations will be done here
		
		LaserRate[0]	=	5;
		LaserRate[1]	=	4;
		LaserRate[2]	=	8;
		LaserRate[3]	=	1;
		
		LaserHeat[0]	=	20;
		LaserHeat[1]	=	10;
		LaserHeat[2]	=	5;
		LaserHeat[3]	=	4;
		
		LaserDamage[0]	=	25;
		LaserDamage[1]	=	30;
		LaserDamage[2]	=	5;
		LaserDamage[3]	=	40;
	
		VIEW[1]	=	"Front view";
		VIEW[2]	=	"Rear view";
		VIEW[3]	=	"Left view";
		VIEW[4]	=	"Right view";
		
		VIEW[5]	=	"Galaxy Map";
		VIEW[6]	=	"Local Map";
		VIEW[7]	=	"Planet Data";
		VIEW[8]	=	"Market Prices";
	
		VIEW[9]	=	"Status";
		VIEW[0]	=	"Inventory";

		VIEW[10]	=	"Ship Equipment";


		UPGRADES[0]		=	"Pulse Laser";				//  3  		400
		UPGRADES[1]		=	"Beam Laser";				//  4  		1000
		UPGRADES[2]		=	"Mining Lasers";			//  10  		800
		UPGRADES[3]		=	"Military Lasers";		//  10  		6000

		UPGRADES[4]		=	"Fuel";						//  always  varies
		UPGRADES[5]		=	"Missile";					//  always  30

		UPGRADES[6]		=	"Large Cargo Bay";		//  always  400
		UPGRADES[7]		=	"ECM System";				//  2  		600
		UPGRADES[8]		=	"Fuel Scoops";				//  5  		525
		UPGRADES[9]		=	"Escape Capsule";			//  6  		1000
		UPGRADES[10]	=	"Energy Bomb";				//  7  		900
		UPGRADES[11]	=	"Extra Energy Unit";		//  8  		1500
		UPGRADES[12]	=	"Docking Computers";		//  9  		1500
		UPGRADES[13]	=	"Galactic Hyperdrive";	//  10  		5000

		UPCOST[0]		=	4000;
		UPCOST[1]		=	10000;
		UPCOST[2]		=	8000;
		UPCOST[3]		=	60000;
		                  
		UPCOST[4]		=	0;
		UPCOST[5]		=	300;
		
		UPCOST[6]		=	4000;
		UPCOST[7]		=	6000;
		UPCOST[8]		=	5250;
		UPCOST[9]		=	10000;
		UPCOST[10]		=	9000;
		UPCOST[11]		=	15000;
		UPCOST[12]		=	15000;
		UPCOST[13]		=	50000;
		
		UPTECH[0]		=	3;
		UPTECH[1]		=	4;
		UPTECH[2]		=	10;
		UPTECH[3]		=	10;
		
		UPTECH[4]		=	0;
		UPTECH[5]		=	0;
		
		UPTECH[6]		=	0;
		UPTECH[7]		=	2;
		UPTECH[8]		=	5;
		UPTECH[9]		=	6;
		UPTECH[10]		=	7;
		UPTECH[11]		=	8;
		UPTECH[12]		=	9;
		UPTECH[13]		=	10;

		ITEM[0]	=	"Food";								//  (Simple organic products, see below)  4.4 tonne  
		ITEM[1]	=	"Textiles";							//  (Unprocessed fabrics)  6.4 "  
		ITEM[2]	=	"Radioactives";					//  (Ores and by-products)  21.2 "  
		ITEM[3]	=	"Slaves";							//  (Usually humanoid)  8.0 "  
		ITEM[4]	=	"Liquor/Wines";					//  (Exotic spirits from unearthy flora)  25.2 "  
		ITEM[5]	=	"Luxuries";							//  (Perfumes, Spices, Coffee)  91.2 "  
		ITEM[6]	=	"Narcotics";						//  (Tobacco, Arcturan Megaweed)  114.8 "  
		ITEM[7]	=	"Computers";						//  (Intelligent machinery)  84.0 "  
		ITEM[8]	=	"Machinery";						//  (Factory and farm equipment)  56.4 "  
		ITEM[9]	=	"Alloys";							//  (Industrial Metals)  32.8 "  
		ITEM[10]	=	"Firearms";							//  (Small-scale artillery, sidearms, etc)  70.4 "  
		ITEM[11]	=	"Furs";								//  (Includes leathers, Millennium Wompom Pelts)  56.0 "  
		ITEM[12]	=	"Minerals";							//  (Unrefined rock containing trace elements)  8.0 kg  
		ITEM[13]	=	"Gold";								//     37.2 kg  
		ITEM[14]	=	"Platinum";							//     65.2 kg  
		ITEM[15]	=	"Gem-stones";						//  (Includes jewelry)  16.4 g  
		ITEM[16]	=	"Alien Items";						//  (Artifacts, Weapons, etc)  27.0 tonne  


		POLITICS[0]	=	"Corporate State";
		POLITICS[1]	=	"Democracy";
		POLITICS[2]	=	"Confederacy";
		POLITICS[3]	=	"Communist State";
		POLITICS[4]	=	"Dictatorship";
		POLITICS[5]	=	"Multi-Government";
		POLITICS[6]	=	"Feudal World";
		POLITICS[7]	=	"Anarchy";

		INDUSTRY[0]	=	"Agricultural (Heavy)";
		INDUSTRY[1]	=	"Agricultural (Medium)";
		INDUSTRY[2]	=	"Agricultural (Light)";
		INDUSTRY[3]	=	"Mixed";
		INDUSTRY[4]	=	"Industrial (Light)";
		INDUSTRY[5]	=	"Industrial (Medium)";
		INDUSTRY[6]	=	"Industrial (Heavy)";

		SPECIES[0]	=	"bird-forms";
		SPECIES[1]	=	"amphibioids";
		SPECIES[2]	=	"felines";
		SPECIES[3]	=	"insectoids";

		COLOURS[0]	=	"black";
		COLOURS[1]	=	"red";
		COLOURS[2]	=	"green";
		COLOURS[3]	=	"yellow";
		COLOURS[4]	=	"blue";
		COLOURS[5]	=	"magenta";
		COLOURS[6]	=	"cyan";
		COLOURS[7]	=	"white";

		MISC[0]		=	"Tech level:";
		MISC[1]		=	"Not somewhere you'd like to take your mum";
		MISC[2]		=	"Comment:";
		MISC[3]		=	"Politics:";
		MISC[4]		=	"Species:";
		MISC[5]		=	"Criminal status";
		MISC[6]		=	"Current planet:";
		MISC[7]		=	"Selected planet:";
		MISC[8]		=	"Core trade:";
		MISC[9]		=	"Planet";
		MISC[10]		=	"g";
		MISC[11]		=	"kg";
		MISC[12]		=	"tonne";
		MISC[13]		=	"Quantity";
		MISC[14]		=	"Item";
		MISC[15]		=	"Credits";
		MISC[16]		=	"Credits available";
		MISC[17]		=	"Cargo";
		MISC[18]		=	"Mouse click to buy. Press '-' to sell.";
		MISC[19]		=	"You currently have no cargo.";
		MISC[20]		=	"Front";
		MISC[21]		=	"Rear";
		MISC[22]		=	"Left";
		MISC[23]		=	"Right";
		MISC[24]		=	"None fitted";
		MISC[25]		=	"Weaponry";
		MISC[26]		=	"Extras";
		
		MISC[27]		=	"Press 1 to launch";
		MISC[28]		=	"Press 4 to equip ship";
		MISC[29]		=	"Press 5/6 for maps";
		MISC[30]		=	"Press 7 for planet info";
		MISC[31]		=	"Press 8 for market prices";
		MISC[32]		=	"Press 9 for your status";
		MISC[33]		=	"Press 0 for your inventory";

		POLICE[0]	=	"Clean";
		POLICE[1]	=	"Offender";
		POLICE[2]	=	"Fugitive";
		POLICE[3]	=	"Wanted (Dead or Alive)";

      for(int i=0; i!=MAX_MODELS; i++)
      {
      	ModInfo[i]	=	new ModelInfo();
      }
  		ModInfo[ 0].Name   			=	"adder";
      ModInfo[	0].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[ 1].Name   			=	"anaconda";
      ModInfo[	1].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[ 2].Name   			=	"asp";
      ModInfo[	2].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[ 3].Name   			=	"asteroid";
      
		ModInfo[ 4].Name   			=	"barrel";
      
		ModInfo[ 5].Name   			=	"boa";
      ModInfo[	5].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[ 6].Name   			=	"boulder";
      
		ModInfo[ 7].Name   			=	"capsule";
      
		ModInfo[ 8].Name   			=	"cobra";
      ModInfo[	8].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[ 9].Name   			=	"cobramk1";
      ModInfo[	9].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[10].Name   			=	"constrictor";
      ModInfo[10].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[11].Name   			=	"coriolis";
      
		ModInfo[12].Name   			=	"cougar";
      ModInfo[12].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[13].Name   			=	"dodo";
      
		ModInfo[14].Name   			=	"ferdelance";
      ModInfo[14].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[15].Name   			=	"gecko";
      ModInfo[15].BaseOccupation	=	Universe.CT_MINER;
      
		ModInfo[16].Name   			=	"krait";
      ModInfo[16].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[17].Name   			=	"mamba";
      ModInfo[17].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[18].Name   			=	"missile";
      
		ModInfo[19].Name   			=	"moray";
      ModInfo[19].BaseOccupation	=	Universe.CT_MERCENARY;
      
		ModInfo[20].Name   			=	"platelet";
      
		ModInfo[21].Name   			=	"python";
      ModInfo[21].BaseOccupation	=	Universe.CT_TRADER;
      
		ModInfo[22].Name   			=	"shuttle";
      ModInfo[22].BaseOccupation	=	Universe.CT_TRANSPORT;
      
		ModInfo[23].Name   			=	"sidewinder";
      ModInfo[23].BaseOccupation	=	Universe.CT_PIRATE;
      
		ModInfo[24].Name   			=	"splinter";
      
		ModInfo[25].Name   			=	"tharglet";
      ModInfo[25].BaseOccupation	=	Universe.CT_KILLER;
      
		ModInfo[26].Name   			=	"thargoid";
      ModInfo[26].BaseOccupation	=	Universe.CT_KILLER;
      
		ModInfo[27].Name   			=	"transporter";
      ModInfo[27].BaseOccupation	=	Universe.CT_TRANSPORT;
      
		ModInfo[28].Name   			=	"viper";
      ModInfo[28].BaseOccupation	=	Universe.CT_POLICE;
      
		ModInfo[29].Name   			=	"worm";
      ModInfo[29].BaseOccupation	=	Universe.CT_TRANSPORT;
	}           
}