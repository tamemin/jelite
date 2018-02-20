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
// Misc keyboard handling

class Keyboard
{
	public	int	iUp,
						iDown,
						iLeft,
						iRight,
						iFire,
						iAcc,
						iDec,
						iJump,
						iSell,
						iHyper,
						iDock,
						
						iTarget,
						iUnarm,
						iMissile;
						
	public	int	iView;

	Keyboard()
	{
		iUp		=	0;
		iDown		=	0;
		iLeft		=	0;
		iRight	=	0;
		iFire		=	0;
		iAcc		=	0;
		iDec     =	0;
		iJump    =	0;
		iSell    =	0;

		iDock		=	0;

		iTarget	=	0;
		iUnarm	=	0;
		iMissile	=	0;
						
		iView		=	1;
	}

	public void down(int key)
	{
		if(key>='0' && key<='9')
		{
			if(GameControl.CURRENT_MODE == GameControl.MODE_DOCKED)
			{
				if(key=='0' || key>='4')
					iView	=	key-'0';
				else if(key=='1')
					GameControl.CURRENT_MODE	=	GameControl.MODE_LAUNCH;
			}
			else
				iView	=	key-'0';
		}
	
		if(GameControl.CURRENT_MODE == GameControl.MODE_HYPER)
			return;

	   switch(key)
	   {
	      case	's':	iUp		|= 1;	break;
	      case	'x':	iDown		|= 1;	break;
	      case	',':	iLeft		|= 1;	break;
	      case	'.':	iRight	|=	1;	break;
			case	'a':	iFire		|=	1;	break;
			case	'/':	iDec		|=	1;	break;
			case	'-':	iSell		|=	1;	break;

			case	'j':	iJump		+=	1;	break;
			
			case	'c':	iDock		|=	1;	break;
			
			case	't':	iTarget	|=	1; break;
			case	'u':	iUnarm	|=	1; break;
			case	'm':	iMissile	|=	1; break;

			case	' ':
				iAcc		|=	1;
				break;
			
			case	'q':
				if(GameControl.CURRENT_MODE != GameControl.MODE_TITLE)
					GameControl.CURRENT_MODE	=	GameControl.MODE_AUTODOCK2;
				break;
				
			case	'h':
				if(GameControl.CURRENT_MODE == GameControl.MODE_TITLE)
				{
					if(GameControl.bDispHelp)
						GameControl.bDispHelp = false;
					else
						GameControl.bDispHelp = true;
				}
				else if(GameControl.CURRENT_MODE == GameControl.MODE_SPACE)
					iHyper	|=	1;
				break;
	
			case	'f':
				if(GameControl.bFilled)
					GameControl.bFilled = false;
				else
					GameControl.bFilled = true;
				break;
			case	'p':
				if(GameControl.bPart)
					GameControl.bPart = false;
				else
					GameControl.bPart = true;
				break;
			case	'b':
				if(GameControl.bBounding)
					GameControl.bBounding = false;
				else
					GameControl.bBounding = true;
				break;
		}
	}
	
	public void up(int key)
	{
	   switch(key)
	   {
	      case	's':	iUp		=	0;	break;
	      case	'x':	iDown		=	0;	break;
	      case	',':	iLeft		=	0;	break;
	      case	'.':	iRight	=	0;	break;
			case	'a':	iFire		=	0;	break;
			case	' ':	iAcc		=	0;	break;
			case	'/':	iDec		=	0;	break;
			case	'-':	iSell		=	0;	break;
			
			case	'c':	iDock		=	0;	break;
			
			case	't':	iTarget	=	0; break;
			case	'u':	iUnarm	=	0; break;
			case	'm':	iMissile	=	0; break;

			case	'j':	iJump		=	0;	break;
			case	'h':	iHyper	=	0;	break;
		}
	}
	
	public void processKeys(ShipSimulator s)
	{
		s.iView	=	this.iView;
		
		if(iDown!=0)
		{
			if(s.Rtar.x<0)
				s.Rtar.x	=	0;
			else
				s.Rtar.x	=	Utils.moveTo(s.Rtar.x, s.Rmax.x, s.Radjust.x);
		}
		else if(iUp!=0)
		{
			if(s.Rtar.x>0)
				s.Rtar.x	=	0;
			else
				s.Rtar.x	=	Utils.moveTo(s.Rtar.x, s.Rmax.x, -s.Radjust.x);
		}
		else
			s.Rtar.x	=	Utils.moveToTarget(s.Rtar.x, 0, s.Rdampen.x);

		if(iRight!=0)
		{
			if(s.Rtar.z<0)
				s.Rtar.z	=	0;
			else
				s.Rtar.z	=	Utils.moveTo(s.Rtar.z, s.Rmax.z, s.Radjust.z);
		}
		else if(iLeft!=0)
		{
			if(s.Rtar.z>0)
				s.Rtar.z	=	0;
			else
				s.Rtar.z	=	Utils.moveTo(s.Rtar.z, s.Rmax.z, -s.Radjust.z);
		}
		else
			s.Rtar.z	=	Utils.moveToTarget(s.Rtar.z, 0, s.Rdampen.z);

		if(iAcc != 0)
		{
			s.fSpeedTar	=	Utils.moveTo(s.fSpeedTar, s.fSpeedMax, 2);
		}
		else if(iDec != 0)
		{
			s.fSpeedTar	=	Utils.moveToTarget(s.fSpeedTar, 0, 2);
		}
		
		if(s.iView>0 && s.iView<5)
		{
			if(iFire != 0 && s.LaserType[s.iView-1]!=-1)
      {
				s.bFire	=	true;
      }
			else
				s.bFire	=	false;
		}
		
		if(iMissile!=0)
			s.bMissileFire	=	true;
		else
			s.bMissileFire	=	false;
		
			
		if(iHyper != 0)
		{
			if (GameControl.CURRENT_MODE == GameControl.MODE_SPACE)
			{
				if(s.iCurrentPlanet != s.iSelectedPlanet)
				{
					float Dist	=	s.SelectedPlanet.distanceFrom(s.CurrentPlanet);
					
					if(Dist<s.Fuel)
					{
						s.Fuel	-=	Dist;
						GameControl.CURRENT_MODE	=	GameControl.MODE_HYPER;
			         GameControl.TIME_IN_MODE	=	0;
			      }
				}
			}
		}
		
		if(s.NumMissiles != 0)
		{
			if(iTarget!=0)
			{
				if(s.MissileState==0)	s.MissileState	=	1;
			}
			
			if(iUnarm!=0)
			{
				if(s.MissileState!=0)	s.MissileState	=	0;
			}
		}
	}
}
