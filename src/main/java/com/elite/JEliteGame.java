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

public class JEliteGame implements Runnable {
	
	boolean	gameInit = true;
	boolean gameRunning	= true;
	SceneRenderer renderer = new SceneRenderer();	// This is going to do all the drawing

	Thread   rungame;
	long	lCount		=	0;
	int		demoModel	=	0;
	int		demoMods[]	=	{8,17,23,28,21,16,14,15,9,2,0,1,-1};
	
	Universe		uni	=	new Universe();
	GameControl		gc		=	new GameControl();		// Need one instance of this to set up strings
	Keyboard	keyboard	=	new Keyboard();
	int		OldView;
	SoundEngine sound = new SoundEngine();
  

   // Once off initialisations
	public void init() {
		
		Frame frame = new Frame();
	    
	    frame.addWindowListener(new WindowAdapter()
	      {
	        public void windowClosing(WindowEvent e)
	        {
	          System.exit(0);
	        }
	      });
	    

	    frame.setTitle("Java Elite");
	    frame.setSize(800, 600);
	    frame.setLayout(new BorderLayout());
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = frame.getSize();
	    frame.setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
	    frame.setVisible(true);
	    frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				keyboard.up(e.getKeyChar());
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				keyboard.down(e.getKeyChar());			
			}
		});
	    
	    frame.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				uni.MouseClick = false;
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				uni.MouseClick = true;
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				uni.mouseX		=	e.getX();
				uni.mouseY		=	e.getY();				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    frame.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				uni.mouseX		=	e.getX();
				uni.mouseY		=	e.getY();
				uni.MouseClick	=	false;		
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				uni.mouseX		=	e.getX();
				uni.mouseY		=	e.getY();
				uni.MouseClick	=	true;		
			}
		});
	     
		   
		uni.init();
		GameControl.CURRENT_MODE	=	GameControl.MODE_TITLE;
		GameControl.OLD_MODE		=	GameControl.MODE_START;
		GameControl.TIME_IN_MODE	=	0;
			
	    sound.init();   
		renderer.initialise(frame.getGraphics(), uni);
		new Thread(renderer).start();
		gameInit	=	false;
	}

	
	private	void initTitle() {
		
		uni.clearLists(true);
		uni.camera.matrix.unit();
		uni.camera.matrix.trans(0f,0f,0f);
		uni.camera.position.set(0f,0f,0f);
		ShipSimulator	s	=	uni.getShip();
		
		if(s!=null) {
			s.defaults(GameControl.CobraIII);
			demoModel	=	0;
			s.PlayerCraft	=	false;
			s.setPosition(0,0,-2800);
			s.setModel(uni.models[demoMods[demoModel]]);
			s.linkTo(uni.shipUsed);
			s.ang(0,0,0);
			s.Rcur.set(0f,0f,0f);
			s.Rtar.set((float)(Math.PI/70), (float)(Math.PI/120), (float)(Math.PI/82));
		}
		
		renderer.setupList(null);
	}
	
	private	void initGame() {
		
		//may need 
		// initStaticLists();
		//initialiseGalaxy();
		
		uni.clearLists(true);

		// Setup player ships
		for(int i=0; i!=GameControl.NUM_PLAYERS; i++) {
			ShipSimulator	s	=	uni.getShip();
			
			if(s!=null) {
				uni.ShipPlayer[i]	=	s;
				s.linkTo(uni.shipUsed);
				s.CurrentPlanet	=	uni.planets[s.iCurrentPlanet];
				s.SelectedPlanet	=	uni.planets[s.iSelectedPlanet];
				s.PlayerCraft		=	true;
				s.PlayerId			=	i;
				// Zero rotations
				s.Rtar.zero();
				s.Rcur.zero();
				s.matrix.unit();
			} else {
				System.out.println("Couldn't allocate player ship");
				System.exit(1);
			}
		}
		renderer.setupList(uni.ShipPlayer[0]);
	}

	private	void initSpace(int type) {
		ShipSimulator	s	=	uni.ShipPlayer[0];
		Vector		off;
		uni.clearLists(false);
		s.CurrentPlanet	=	uni.planets[s.iCurrentPlanet];
		Planet	Planet	=	s.CurrentPlanet;
		SunThreeD		Sun		=	uni.Sun;
		StationModel	Station	=	uni.Station;
		Station.setup(Planet);
		Station.setModel(uni.models[11]);
		Sun.setup(s.iCurrentPlanet);

		if(type==0) {
			off	=	new	Vector(uni.Station.position);
			off.z+=4000;
		} else {
			
			off	=	new	Vector(0,0,0);
		}
		
		s.setModel(uni.models[0]);
		s.position.copy(off);
		s.matrix.unit();
		s.fSpeedTar	=	10;
		s.fSpeedCur	=	s.fSpeedMax;
		s.position.z	-=	3000;
		s.PlayerCraft	=	true;
		s.PlayerId		=	0;
		s.MissileState	=	0;
		s.iView			=	1;
		keyboard.iView		=	1;
		
		Planet.linkTo(uni.bodies);
		Station.linkTo(uni.bodies);
		Sun.linkTo(uni.bodies);

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
		
		ShipSimulator ship;
		
		do	{
			
			try {Thread.currentThread().sleep(20); } catch (InterruptedException e) { }
         
			if(GameControl.CURRENT_MODE != GameControl.OLD_MODE) {
				GameControl.OLD_MODE		=	GameControl.CURRENT_MODE;
				GameControl.TIME_IN_MODE	=	0;
         	} else {
         		GameControl.TIME_IN_MODE++;
         	}
			
			switch(GameControl.CURRENT_MODE) {
				
			case	GameControl.MODE_TITLE:
					if(GameControl.TIME_IN_MODE == 0) {
						initTitle();
					} else {
						uni.camera.position.z	+=	100;
						ship=(ShipSimulator) uni.shipUsed.Next;
						
						if(ship!=null) {
							
							int t = GameControl.TIME_IN_MODE % 700;
							float off;
							
							ship.run(uni);
                     
							if(t==0) {
								ship.randomColour();
							}
								
							
							if(t<100)
								off	=	(100-t)*150;
							else if(t>600)
								off	=	(t-600)*150;
							else
								off	=	0;
	
							if(t==0) {
								demoModel =	demoModel+1;
								if(demoMods[demoModel]==-1) {
									demoModel	=	0;
								}
                        	
								ship.setModel(uni.models[demoMods[demoModel]]);
								System.out.println("Displaying ship model "+demoMods[demoModel]);
							}
							
							ship.position.copy(uni.camera.position);
							ship.position.z=uni.camera.position.z+500+off*2;
						}
                  
						if(keyboard.iAcc != 0) {
							keyboard.iAcc	=	0;
							GameControl.CURRENT_MODE	=	GameControl.MODE_START;
						}
					}
					break;
					
				case	GameControl.MODE_START:
					initGame();
					GameControl.CURRENT_MODE	=	GameControl.MODE_DOCKED;
					break;
					
				case	GameControl.MODE_DOCKED:
		         keyboard.processKeys(uni.ShipPlayer[0]);
		         if(GameControl.TIME_IN_MODE	==	0)
					{
						uni.ShipPlayer[0].iView	=	9;
						keyboard.iView				=	9;
						OldView	=	keyboard.iView;
					}
					else
					{
						if(OldView != keyboard.iView)
						{
							if(keyboard.iView==4)
							{
								uni.EquipState	=	0;
								uni.EquipItem	=	-1;
							}
						}
						
						OldView	=	keyboard.iView;
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
		         keyboard.processKeys(uni.ShipPlayer[0]);

					// Run ship handlers
					ship		=	(ShipSimulator) uni.shipUsed.Next;
					while(ship!=null)
					{
                  ShipSimulator sNext = ship.Next();
						ship.run(uni);
						ship	=	sNext;
					}

					
					if(keyboard.iDock!=0)
					{
						keyboard.iDock	=	0;
						
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
            
           		if(keyboard.iJump==1 && !uni.ShipPlayer[0].bStation)
           		{
           			uni.ShipPlayer[0].jump((Object3D) uni.shipUsed.Next);
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
				renderer.rePaint(uni.camera, uni.shipUsed);
				
			
				
		} while(gameRunning);
   }
	
	
	public static void main(String[] args) {	
	    JEliteGame game = new JEliteGame();
	    game.init();
	    game.start();
  }

}