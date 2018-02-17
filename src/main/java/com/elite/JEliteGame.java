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

//package JElite2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.image.MemoryImageSource;

public class JEliteGame extends java.applet.Applet implements Runnable
{
	boolean	gameInit			=	true, gameRunning		=	true;
// Double buffering variables
	Graphics		og;
	SceneRenderer	renderer	=	new SceneRenderer();	// This is going to do all the drawing
// Globals
   Thread   rungame;
	long	lCount		=	0;
	int		demoModel	=	0;
	int		demoMods[]	=	{8,17,23,28,21,16,14,15,9,2,0,1,-1};
	
	Universe		uni	=	new Universe();
	GameControl		gc		=	new GameControl();		// Need one instance of this to set up strings
	Keyboard	keys	=	new Keyboard();
	
// Misc
	int		OldView;

// Sound
  SoundEngine sound = new SoundEngine();

   // Once off initialisations
	public void init() {
		repaint();
		initialise();
	    sound.setPath((getCodeBase()).toExternalForm());
	    sound.init();
		renderer.initialise(this, uni);
		if(!GameControl.bIE5mode)
		{
			new Thread(renderer).start();
		}
		gameInit	=	false;
	}

   public void initialise() {
		int i;
		uni.initStaticLists();
		System.out.println("JI:"+uni);
		uni.loadModels(getCodeBase());
		GameControl.CURRENT_MODE	=	GameControl.MODE_TITLE;
		GameControl.OLD_MODE		=	GameControl.MODE_START;
		GameControl.TIME_IN_MODE	=	0;
		setBackground( new Color(0x102030) );
	}
	
	private	void initTitle() {
		uni.clearLists(true);
		uni.camera.Mat.unit();
		uni.camera.Mat.trans(0f,0f,0f);
		uni.camera.Position.set(0f,0f,0f);
		ShipSimulator	s	=	uni.getShip();
		
		if(s!=null) {
			s.defaults(GameControl.CobraIII);
			demoModel	=	0;
			s.PlayerCraft	=	false;
			s.pos(0,0,-2800);
			s.mod(uni.Model[demoMods[demoModel]]);
			s.linkTo(uni.ShipUsed);
			s.ang(0,0,0);
			s.Rcur.set(0f,0f,0f);
			s.Rtar.set((float)(Math.PI/70), (float)(Math.PI/120), (float)(Math.PI/82));
		}
		
		renderer.setupList(null);
	}
	
	private	void initGame() {
		
		uni.clearLists(true);
		uni.initialiseGalaxy();
		// Setup player ships
		for(int i=0; i!=GameControl.NUM_PLAYERS; i++) {
			ShipSimulator	s	=	uni.getShip();
			
			if(s!=null) {
				uni.ShipPlayer[i]	=	s;
				s.linkTo(uni.ShipUsed);
				s.CurrentPlanet	=	uni.planets[s.iCurrentPlanet];
				s.SelectedPlanet	=	uni.planets[s.iSelectedPlanet];
				s.PlayerCraft		=	true;
				s.PlayerId			=	i;
				// Zero rotations
				s.Rtar.zero();
				s.Rcur.zero();
				s.Mat.unit();
			} else {
				System.out.println("Couldn't allocate player ship");
				System.exit(1);
			}
		}
		renderer.setupList(uni.ShipPlayer[0]);
	}

	private	void initSpace(int type) {
		ShipSimulator	s	=	uni.ShipPlayer[0];
		Vectr		off;
		uni.clearLists(false);
		s.CurrentPlanet	=	uni.planets[s.iCurrentPlanet];
		Planet	Planet	=	s.CurrentPlanet;
		SunThreeD		Sun		=	uni.Sun;
		StationModel	Station	=	uni.Station;
		Station.setup(Planet);
		Station.mod(uni.Model[11]);
		Sun.setup(s.iCurrentPlanet);

		if(type==0) {
			off	=	new	Vectr(uni.Station.Position);
			off.z+=4000;
		} else {
			
			off	=	new	Vectr(0,0,0);
		}
		
		s.mod(uni.Model[0]);
		s.Position.copy(off);
		s.Mat.unit();
		s.fSpeedTar	=	10;
		s.fSpeedCur	=	s.fSpeedMax;
		s.Position.z	-=	3000;
		s.PlayerCraft	=	true;
		s.PlayerId		=	0;
		s.MissileState	=	0;
		s.iView			=	1;
		keys.iView		=	1;
		
		Planet.linkTo(uni.Bodies);
		Station.linkTo(uni.Bodies);
		Sun.linkTo(uni.Bodies);

		uni.camera.update(s);
		renderer.setupList(s);
	}
	
	
	public void start() {
	   rungame = new Thread(this);
	   rungame.start();
	}
	
	public void stop() {
		gameRunning	=	false;
	}

	public void run() {
		
		ShipSimulator s;
		
		do	{
			
			try {Thread.currentThread().sleep(20); } catch (InterruptedException e) { }
         
			if(GameControl.CURRENT_MODE != GameControl.OLD_MODE) {
				GameControl.OLD_MODE		=	GameControl.CURRENT_MODE;
				GameControl.TIME_IN_MODE	=	0;
         	} else {
         		GameControl.TIME_IN_MODE++;
         	}
			
			switch(GameControl.CURRENT_MODE)
			{
				case	GameControl.MODE_TITLE:
					if(GameControl.TIME_IN_MODE == 0)
					{
						initTitle();
					}
					else
					{
						uni.camera.Position.z	+=	100;
						s=(ShipSimulator) uni.ShipUsed.Next;
						if(s!=null)
						{
							int t = GameControl.TIME_IN_MODE % 700;
							float off;
							
							s.run(uni);
                     
							if(t==0)
								s.randomColour();
							
							if(t<100)
								off	=	(100-t)*150;
							else if(t>600)
								off	=	(t-600)*150;
							else
								off	=	0;
	
							if(t==0)
							{
								demoModel	=	demoModel+1;
								if(demoMods[demoModel]==-1)
                        	demoModel	=	0;
								s.mod(uni.Model[demoMods[demoModel]]);
                        System.out.println("Displaying ship model "+demoMods[demoModel]);
							}
							
							s.Position.copy(uni.camera.Position);
							s.Position.z=uni.camera.Position.z+500+off*2;
						}
                  
						if(keys.iAcc != 0)
						{
							keys.iAcc	=	0;
							GameControl.CURRENT_MODE	=	GameControl.MODE_START;
						}
					}
					break;
					
				case	GameControl.MODE_START:
					initGame();
					GameControl.CURRENT_MODE	=	GameControl.MODE_DOCKED;
					break;
					
				case	GameControl.MODE_DOCKED:
		         keys.processKeys(uni.ShipPlayer[0]);
		         if(GameControl.TIME_IN_MODE	==	0)
					{
						uni.ShipPlayer[0].iView	=	9;
						keys.iView				=	9;
						OldView	=	keys.iView;
					}
					else
					{
						if(OldView != keys.iView)
						{
							if(keys.iView==4)
							{
								uni.EquipState	=	0;
								uni.EquipItem	=	-1;
							}
						}
						
						OldView	=	keys.iView;
					}
					break;
					
				case	GameControl.MODE_LAUNCH:
					if(((int)(GameControl.TIME_IN_MODE/50)) > 0)
					{
						initSpace(0);
						GameControl.CURRENT_MODE	= GameControl.MODE_SPACE;
					}
					break;
					
				case	GameControl.MODE_SPACE:
		         keys.processKeys(uni.ShipPlayer[0]);

					// Run ship handlers
					s		=	(ShipSimulator) uni.ShipUsed.Next;
					while(s!=null)
					{
                  ShipSimulator sNext = s.Next();
						s.run(uni);
						s	=	sNext;
					}

					
					if(keys.iDock!=0)
					{
						keys.iDock	=	0;
						
						if(uni.ShipPlayer[0].autoEngaged())
						{
							uni.ShipPlayer[0].autoStop();
						}
						else
						{
							if(uni.ShipPlayer[0].Upgrade[6] && uni.ShipPlayer[0].bStation)
							{
								uni.ShipPlayer[0].followStation(uni.Station);
								// uni.ShipPlayer[0].followShip((shipsim)uni.ShipUsed.Next.Next);
							}
						}
					}
            
           		if(keys.iJump==1 && !uni.ShipPlayer[0].bStation)
           		{
           			uni.ShipPlayer[0].jump((Object3D) uni.ShipUsed.Next);
           		}

					uni.event();
					
					uni.camera.update();
		         break;
							
				case	GameControl.MODE_HYPER:
					if(((int)(GameControl.TIME_IN_MODE/50)) > 0)
					{
						GameControl.CURRENT_MODE	=	GameControl.MODE_SPACE;
						
						// Set new system
						uni.ShipPlayer[0].iCurrentPlanet	=	uni.ShipPlayer[0].iSelectedPlanet;
						initSpace(1);
					}
					break;
					
				case	GameControl.MODE_DOCK:
					if(GameControl.TIME_IN_MODE>100)
					{
						uni.ShipPlayer[0].autoStop();
						uni.ShipPlayer[0].MissileState		=	0;
						GameControl.CURRENT_MODE	=	GameControl.MODE_DOCKED;
					}
					break;
					
				case	GameControl.MODE_AUTODOCK1:
					break;
					
				case	GameControl.MODE_AUTODOCK2:
					GameControl.CURRENT_MODE	= GameControl.MODE_TITLE;
		         GameControl.TIME_IN_MODE	=	0;
					break;
			}
				
				
			if(gameRunning)
				renderer.rePaint(uni.camera, uni.ShipUsed);
				
			
				
		} while(gameRunning);
   }


	public void destroy()
	{
	}

   public void paint(Graphics g)
   {
  		if(renderer!=null)
  		{
  			Image	RendBuff;
   			
   		if(renderer.offscreen!=null)
   		{
		   	g.drawImage(renderer.offscreen, 0, 0, this);
		   }
		}
   }

//*********************************************************************************************
	// Overloading update to allow easy double buffering
	public void update(Graphics g)
	{
		paint(g);
	}
//*********************************************************************************************

	// Implement applet io methods
	public boolean mouseEnter(Event ev, int x, int y)
	{
		uni.MouseX		=	x;
		uni.MouseY		=	y;
      return true;
	}

	public boolean mouseMove(Event ev, int x, int y)
	{
		uni.MouseX		=	x;
		uni.MouseY		=	y;
      return true;
	}

	public boolean mouseDrag(Event ev, int x, int y)
	{
		uni.MouseX		=	x;
		uni.MouseY		=	y;
		uni.MouseClick	=	true;
      return true;
	}

	public boolean mouseDown(Event ev, int x, int y)
	{
		uni.MouseClick	=	true;
      return true;
	}
	
	public boolean mouseUp(Event ev, int x, int y)
	{
		uni.MouseClick	=	false;
      return true;
	}

	public boolean keyDown(Event ev, int key)
	{
		keys.Down(key);
		return true;
	}
	
	public boolean keyUp(Event ev, int key)
	{
		keys.Up(key);
		return true;
	}
public static void main(String[] args)
  {
    JEliteGame applet = new JEliteGame();
    Frame frame = new Frame();
    frame.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          System.exit(0);
        }
      });
    frame.add(applet, BorderLayout.CENTER);
    frame.setTitle("Java Elite");
    applet.init();
    applet.start();
    //frame.setSize(640, 480);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    frame.setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
    frame.setVisible(true);
  }
//*********************************************************************************************
}


