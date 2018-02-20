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
// Self contained render class
//
// This class will handle all screen output
// and will run on it's own thread

import java.awt.*;
import java.awt.image.BufferedImage;



class SceneRenderer implements Runnable {
		
	// Pure render variables
	
	Graphics onscreen;
	Image offscreen;
	Graphics graphics;	
	boolean paintRequested;
	boolean Running;

	// Static render value
	public final static int SCREEN_WIDTH	=	800;
	public final static int SCREEN_HEIGHT	=	600;
	public final static int SCREEN_CEN_X	=	(SCREEN_WIDTH/2);
	public final static int SCREEN_CEN_Y	=	(SCREEN_HEIGHT/2);

	// List control
	static	LinkableObject			pobjList;
	static	Object3D			objList[]	=	new Object3D[2];
	static	Object3D			objFree		=	new Object3D();
	static	Object3D			objects[]	=	new Object3D[2 * GameControl.MAX_DYNAMIC_OBJS];
	
	Camera	camera	=	new Camera();

	Vector		Light	=	new Vector(0.7f, 0.7f, 0);

	Polygon		poygonArray[]		=	new Polygon[GameControl.NUM_Z_BUCKETS];
	Polygon		polygon		=	new Polygon();
	
	Vector		star[]		=	new Vector[GameControl.NUM_STARS];
	Vector		star_rel[]	=	new Vector[GameControl.NUM_STARS_REL];
	
	// Mr Universe & friend
	Universe	Uni;
	ShipSimulator	Ship;
	
	// Misc
	Ticker	Mess1	=	new Ticker();
	int		t	=	0;
				
	SceneRenderer() {
		paintRequested	=	false;
		Running			=	true;
	}
	
	public void initialise(Graphics gcd, Universe uni) {
		
		this.onscreen = gcd;
		int	i;
		
		Uni	=	uni;
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		
		offscreen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT,BufferedImage.TYPE_INT_RGB);

		
		if(graphics!=null) graphics.dispose();
		
		graphics = offscreen.getGraphics();

		for(i=0; i!=GameControl.NUM_Z_BUCKETS; i++)
			poygonArray[i]	=	new Polygon();

		for(i=0; i!=GameControl.NUM_FREE_POLYS; i++)
		{
			Polygon	p	=	new Polygon();
			p.linkTo(polygon);
		}

		
		for(i=0; i!=GameControl.NUM_STARS; i++)
		{
			star[i]	=	new Vector(	(float)((Math.random()-0.5)*10000),
										(float)((Math.random()-0.5)*10000),
										(float)((Math.random()-0.5)*10000));
		}
		
		for(i=0; i!=GameControl.NUM_STARS_REL; i++)
		{
			star_rel[i]	=	new Vector(	(float)(Math.random()*32000)-16000,
											(float)(Math.random()*32000)-16000,
											(float)(Math.random()*32000)-16000);
		}
		
		
		for(i=0; i!=2; i++)
			objList[i]	=	new Object3D();
		
		for(i=0; i!=2*GameControl.MAX_DYNAMIC_OBJS; i++)
		{
			objects[i]	=	new Object3D();
			objects[i].linkTo(objFree);
		}
		
		Mess1.setupSimple(GameControl.MISC, 27,33, 100, 500,SCREEN_HEIGHT-10, Color.white);
	}


	public void rePaint(Camera cam, LinkableObject pobjList) {
		if(paintRequested || cam==null)
			return;

		camera.copy(cam);
		this.pobjList	=	pobjList;
		paintWorld();

	
		/*			
			Objct	p;
		
			p	=	objList[0].Next;
			while(p!=null) {
				Objct pN	=	p.Next;
				p.linkTo(objFree);
				p	=	pN;
			}
		
			if(pobjList!=null) {
				p	=	pobjList.Next;
				
				while(p != null) {
					Object3D	o	=	(Object3D)p,
							d	=	(Object3D)objFree.Next;
					
					if(d!=null) {
						d.dupe(o);
						d.linkTo(objList[0]);
					}
					
					p	=	p.Next;
				}
			}		
			this.pobjList	=	objList[0];	
			paintRequested	=	true;
		}
		*/
		
		
	}


	public void stop()
	{
		Running	=	false;
	}

	public void setupList(ShipSimulator Ship)
	{
		this.Ship	=	Ship;
	}

// Do the biz
	public void run() {
		do	{
			while(!paintRequested && Running)
			{
		      try
		      {
		         Thread.currentThread().sleep(20);
	   	   }
	      	catch (InterruptedException e) { }
			}
			
			if(Running)
			{
				paintWorld();
			}
			
			paintRequested	=	false;
			}while(Running);
	}


	public void paintWorld() {
		
		t++;
	   	graphics.setColor(Color.black);
		graphics.fillRect(0,0, SCREEN_WIDTH,SCREEN_HEIGHT);
	   	doMain(Ship);
		graphics.setColor(new Color(128,128,0));
		graphics.drawRect(0,0, SCREEN_WIDTH-1,SCREEN_HEIGHT-1);
		onscreen.drawImage(offscreen, 0, 0, null);
	}
	



	public void doMain(ShipSimulator s)
	{
		switch(GameControl.CURRENT_MODE)
		{
			case	GameControl.MODE_TITLE:
				if(GameControl.TIME_IN_MODE != 0)
				{
					drawStars();
					
					if(pobjList != null)
					{
						drawShips((Object3D) pobjList.Next);
						Polygon.drawOrderTable(graphics, poygonArray, polygon);
					}
					drawHelp();
				}
				break;
				
			case	GameControl.MODE_DOCKED:
				if(s==null)
					return;
					
				switch(s.iView)
				{
					case	4:	drawEquip(s);			break;
					case	5:	drawGalaxy(s);			break;
					case	6:	drawLocal(s);			break;
					case	7:	drawPlanetData(s);	break;
					case	8:	drawMarketPrices(s, Uni.Station);	break;	// Naughty
					case	9:	drawShipStatus(s);	break;
					case	0:	drawShipCargo(s);		break;
				}

				graphics.setColor(Color.white);

				graphics.drawRect(32,32, GameControl.SCREEN_WIDTH-64, GameControl.SCREEN_HEIGHT-64);

				if(s.iView!=4)
					Utils.drawStringCentre(graphics, GameControl.VIEW[s.iView], 20);
				else
					Utils.drawStringCentre(graphics, GameControl.VIEW[10], 20);
					
				Mess1.update(graphics);
				break;
				
			case	GameControl.MODE_SPACE:
				if(s.iView>0 && s.iView<5)
				{
					drawStars();
					
					drawBodies(Uni.bodies);
					
					drawPart();
					
					if(pobjList != null)
					{
						drawShips((Object3D) pobjList.Next);
					}

					Polygon.drawOrderTable(graphics, poygonArray, polygon);
					
					if(s.bShoot)
						drawLasers(s);
					drawHUD(s);
				}
				else
				{
					graphics.setColor(Color.white);
					graphics.drawRect(32,32, GameControl.SCREEN_WIDTH-64, GameControl.SCREEN_HEIGHT-64);
					
					switch(s.iView)
					{
						case	5:	drawGalaxy(s);			break;
						case	6:	drawLocal(s);			break;
						case	7:	drawPlanetData(s);	break;
						case	8:	drawMarketPrices(s, Uni.Station);	break;	// Naughty
						case	9:	drawShipStatus(s);	break;
						case	0:	drawShipCargo(s);		break;
					}
				}
				
				graphics.setColor(Color.white);
				Utils.drawStringCentre(graphics, GameControl.VIEW[s.iView], 20);
		      break;
				
			case	GameControl.MODE_DOCK:
			case	GameControl.MODE_LAUNCH:
				int	t	=	GameControl.TIME_IN_MODE%200;
				int	t2	=	t%25;
				
				graphics.setColor(Color.white);

				t2	*=	8;
				for(int i=t2-4; i<t2+4; i++)
				{
					if(i>=0)
						graphics.drawOval(SCREEN_CEN_X - (int)(i*4), SCREEN_CEN_Y - (int)(i*4), i*8,i*8);
				}
				break;
				
			case	GameControl.MODE_HYPER:
				s.iView	=	1;
				
				drawStars();
				drawBodies(Uni.bodies);
					
				if(pobjList != null)
				{
					drawShips((Object3D) pobjList.Next);
					Polygon.drawOrderTable(graphics, poygonArray, polygon);
				}
				
				t	=	GameControl.TIME_IN_MODE%200;
				t2	=	t%25;
				
				graphics.setColor(Color.white);

				t2	*=	8;
				for(int i=t2-4; i<t2+4; i++)
				{
					if(i>=0)
						graphics.drawOval(SCREEN_CEN_X - (int)(i*4), SCREEN_CEN_Y - (int)(i*4), i*8,i*8);
				}

				drawHUD(s);
				break;
				
			case	GameControl.MODE_AUTODOCK1:
				break;
				
			case	GameControl.MODE_AUTODOCK2:
				break;
		}
	}


	private	void	drawStars()
	{
		Vector	v	=	new Vector();
		
		graphics.setColor(Color.white);
		for(int i=0; i!=GameControl.NUM_STARS; i++)
		{
			v.copy(star[i]);
			v.mul(camera.matrix);
			v.pers();
			
			graphics.fillRect(v.sX,v.sY, 1,1);
		}
		
		// Stars relative to cameras motion
//		graphics.setColor(Color.green);
		for(int i=0; i!=GameControl.NUM_STARS_REL; i++)
		{
			int x,y,z;
			
			v.copy(star_rel[i]);
			
			v.sub(camera.position);
			
			x	=	(int)v.x;
			y	=	(int)v.y;
			z	=	(int)v.z;
         
         if(x>=0)	x	=	x&0x7fff;
         else		x	=	0x7fff - ((-x) & 0x7fff);
         
         if(y>=0)	y	=	y&0x7fff;
         else		y	=	0x7fff - ((-y) & 0x7fff);
         
         if(z>=0)	z	=	z&0x7fff;
         else		z	=	0x7fff - ((-z) & 0x7fff);
         
         x	-=	0x4000;
         y	-=	0x4000;
         z	-=	0x4000;
         
			v.x	=	x;
			v.y	=	y;
			v.z	=	z;
			
			v.mul(camera.matrix);
			
			if(v.z<GameControl.PERSPECTIVE_H/2)
			{
			}
			else
			{
				v.pers();
				if(v.z>5000)
					graphics.fillRect(v.sX,v.sY, 1,1);
				else
					graphics.fillRect(v.sX,v.sY, 2,2);
			}
		}
	}


	private	void	drawPart()
	{
    
    return;
    /*
		part	p	=	(part)Uni.PartUsed.Next,
				pNext;	// Particles may kill themselves

		
		int	i	=	0;	
		while(p != null)
		{
			pNext	=	(part)p.Next;
			p.render(Uni, Poly, PolyFree, Light, Cam.Mat);
			p	=	pNext;
			i++;
		}
*/
//		graphics.drawString("Parts:"+i, 10,50);
	}	

	private	void	drawBodies(LinkableObject	Bodies)
	{
		LinkableObject	p	=	Bodies.Next;
		
		while(p!=null) {
			
			if (p.type==Uni.OBJ_STATION) {
				
				drawStation((StationModel) p);
				
			} else{
				
				if (p.type==Uni.OBJ_SUN || (p.type==Uni.OBJ_STATION)) {
					
					Polygon	Poly = polygon.Next;
					
					if(Poly != null)
					{
						Vector	v	=	new Vector(((Object3D)p).position);
						v.sub(camera.position);
						v.mul(camera.matrix);
						if(v.z>GameControl.PERSPECTIVE_H/2)
						{
							int	Size	=	(int)(((Object3D)p).size * GameControl.PERSPECTIVE_H/v.z)/2;
							int	iOT	=	v.pers();
							
							if(Size>1000)
								Size	=	1000;
							
							Poly.x[0]	=	v.sX;
							Poly.y[0]	=	v.sY;
							
							Poly.x[1]	=	Poly.y[1]	=	Size;
							
							Poly.c			=	((Object3D)p).colour;
							Poly.iNSides	=	1;
							Poly.linkTo(this.poygonArray[iOT]);
						}
						
					}
					
				}
			}
				
			p	=	p.Next;
		}
	}
	
	private	void	drawStation(StationModel s)
	{
		MatrixMath43	m	=	new MatrixMath43();

		s.run();

		m.copy(s.matrix);
		m.trans(	s.position.x-camera.position.x,
					s.position.y-camera.position.y,
					s.position.z-camera.position.z);
		
		m.mul(camera.matrix);
		
		// Calculate pos relative to cam for radar AND occlusion purposes
		s.renderPos.set(0f,0f,0f);
		s.renderPos.mul(m);
		
		boolean occlude	=	fovClip(s.renderPos);

		if(!occlude)
		{
			ModelThreeD mod	=	s.getModel();

			mod.render(poygonArray, polygon, Light, m, s.col);
				
			if(GameControl.bBounding)
				mod.renderBounds(graphics, m);
		}
	}
                               
	private	void	drawShips(Object3D s)
	{
		MatrixMath43	m	=	new MatrixMath43();
		int		iCount = 0;
		
		
		while(s!=null)
		{
			if(GameControl.bDebug)
			{
				//graphics.drawString("Ship "+iCount+":"+s+":@ "+s.Position.x+","+s.Position.y+","+s.Position.z, 10,iCount*15+120);
        graphics.drawString("Ship "+iCount+" ZPOS"+s.position.z,10,iCount*15+120);
				iCount++;
			}
								
			m.copy(s.matrix);
			m.trans(	s.position.x-camera.position.x,
						s.position.y-camera.position.y,
						s.position.z-camera.position.z);
			
			m.mul(camera.matrix);
			
			// Calculate pos relative to cam for radar AND occlusion purposes
			s.renderPos.set(0f,0f,0f);
			s.renderPos.mul(m);
			
			
			boolean occlude;
			
			if(s == Uni.ShipPlayer[0])		occlude	=	true;
			else
				occlude	=	fovClip(s.renderPos);
			if(!occlude) // Objects are not in field of vision (i.e. behind you)
			{
				ModelThreeD mod	=	s.getModel();

				s.renderPos.pers();
					
				if(s.renderPos.z > 20000)
				{
					// So far away, just draw a dot
					graphics.setColor(s.colour);
					graphics.fillRect(s.renderPos.sX,s.renderPos.sY, 2,2);
				}
				else
				{
					mod.render(poygonArray, polygon, Light, m, s.col);
					
					if(GameControl.bBounding)
						mod.renderBounds(graphics, m);
				}
				
        // Render laser fire
				ShipSimulator s2	=	(ShipSimulator) s;
				if(!s2.PlayerCraft && s2.bShoot && s2.sBest!=null)
				{
					graphics.setColor(s.colour);
					graphics.drawLine(s.renderPos.sX,s.renderPos.sY,
											SCREEN_CEN_X+(int)(SCREEN_CEN_X*Math.random()),
											SCREEN_CEN_Y+(int)(SCREEN_CEN_Y*Math.random()));
				}
			}
	
			s	=	(Object3D)s.Next;
		}
	}

 
 	private	void	drawHelp()
	{
		graphics.setColor(Color.white);
		
		Utils.drawStringCentre(graphics, "Jelly Tae by Simon Lacey", 20);
		
		graphics.setColor(Color.yellow);
		
		if(GameControl.bDispHelp)
		{
			if(GameControl.bDebug)
			{
				//Model[0].printInfo(graphics);
			}
			
			int	y=40;
			graphics.drawString("S,X - Climb,dive", 10,y);
			y	+=	15;
			
			graphics.drawString("<,> - Roll", 10,y);
			y	+=	15;
			
			graphics.drawString("Space,/ - Accelerate,decelerate", 10,y);
			y	+=	15;
			graphics.drawString("A - Fire", 10,y);
			y	+=	20;
			
			graphics.drawString("F - toggle filled/outlined ships", 10,y);
			y	+=	15;
			graphics.drawString("B - toggle bounding boxes on/off", 10,y);
			y	+=	15;
			graphics.drawString("P - toggle ship trails on/off", 10,y);
			y	+=	20;
			
			graphics.drawString("Click Mouse in applet to gain input focus", 10,y);
			y	+=	15;
			graphics.drawString("Please make sure Caps Lock is off", 10,y);
			y	+=	15;
		}
		else
		{
			Utils.drawStringCentre(graphics, "Press SPACE to START", SCREEN_HEIGHT-40);
			Utils.drawStringCentre(graphics, "Press H for HELP", SCREEN_HEIGHT-20);
		}
	}


	private	void	drawGalaxy(ShipSimulator s)
	{
		int	BestPlanet		=	-1,
				BestDistance	=	1000000,
				d;

		// Fuel range
		int	rad	=	(int) ((s.Fuel/7.00)*20);
		graphics.setColor(Color.lightGray);
		drawO((int)s.CurrentPlanet.sPos.x,(int)s.CurrentPlanet.sPos.z,	rad*2,rad*2);
		
		graphics.setColor(Color.yellow);
		graphics.drawLine((int)s.CurrentPlanet.sPos.x,		(int)s.CurrentPlanet.sPos.z-10,
								(int)s.CurrentPlanet.sPos.x,		(int)s.CurrentPlanet.sPos.z+10);
		graphics.drawLine((int)s.CurrentPlanet.sPos.x-10,	(int)s.CurrentPlanet.sPos.z,
								(int)s.CurrentPlanet.sPos.x+10,	(int)s.CurrentPlanet.sPos.z);

		graphics.setColor(Color.red);
		graphics.drawLine((int)s.SelectedPlanet.sPos.x,	(int)s.SelectedPlanet.sPos.z-10,
								(int)s.SelectedPlanet.sPos.x,	(int)s.SelectedPlanet.sPos.z+10);
		graphics.drawLine((int)s.SelectedPlanet.sPos.x-10,(int)s.SelectedPlanet.sPos.z,
								(int)s.SelectedPlanet.sPos.x+10,(int)s.SelectedPlanet.sPos.z);
		
		graphics.setColor(Color.white);
		for(int i=0; i!=GameControl.NUM_PLANETS; i++)
		{
			graphics.fillRect((int)Uni.planets[i].sPos.x, (int)Uni.planets[i].sPos.z, 2,2);
			
			d	=	Utils.SQR((int)Uni.planets[i].sPos.x - Uni.mouseX) + Utils.SQR((int)Uni.planets[i].sPos.z-Uni.mouseY);
			if(d<BestDistance)
			{
				BestPlanet		=	i;
				BestDistance	=	d;
			}

		}

		if(Uni.MouseClick && BestPlanet != -1)
		{
			s.iSelectedPlanet	=	BestPlanet;
			s.SelectedPlanet	=	Uni.planets[BestPlanet];
		}

		graphics.setColor(Color.red);
		graphics.drawOval((int)Uni.planets[BestPlanet].sPos.x-7, (int)Uni.planets[BestPlanet].sPos.z-7, 14,14);

		graphics.setColor(Color.white);
		if(s.iSelectedPlanet != s.iCurrentPlanet)
		{
			graphics.drawString("Distance:"+ s.SelectedPlanet.distanceFrom(s.CurrentPlanet), 32,20);
		}
		graphics.drawString(GameControl.MISC[6] +" "+ s.CurrentPlanet.name, 32,GameControl.SCREEN_HEIGHT-10);
		Utils.drawStringCentre(graphics, GameControl.MISC[7] +" "+ s.SelectedPlanet.name, GameControl.SCREEN_HEIGHT-10);
	}

	private	void	drawLocal(ShipSimulator s)
	{
		int	BestPlanet		=	-1,
				BestDistance	=	1000000,
				d,
				cx	=	((int)s.CurrentPlanet.sPos.x),
				cy	=	((int)s.CurrentPlanet.sPos.z),
				x,
				y;

		// Fuel range
		int	rad	=	(int) ((s.Fuel/7.00)*20);
		graphics.setColor(Color.lightGray);
		drawO(GameControl.SCREEN_CEN_X, GameControl.SCREEN_CEN_Y, rad*20,rad*20);

		graphics.setColor(Color.yellow);
		graphics.drawLine(	GameControl.SCREEN_CEN_X, GameControl.SCREEN_CEN_Y-10, GameControl.SCREEN_CEN_X, GameControl.SCREEN_CEN_Y+10);
		graphics.drawLine(	GameControl.SCREEN_CEN_X-10, GameControl.SCREEN_CEN_Y, GameControl.SCREEN_CEN_X+10, GameControl.SCREEN_CEN_Y);

		x	=	(((int)s.SelectedPlanet.sPos.x)-cx)*10;
		y	=	(((int)s.SelectedPlanet.sPos.z)-cy)*10;
		if(Math.abs(x)<(GameControl.SCREEN_WIDTH-64)/2 && Math.abs(y)<(GameControl.SCREEN_HEIGHT-64)/2)
		{
			graphics.setColor(Color.red);

		 	x	+=	GameControl.SCREEN_CEN_X;
		 	y	+=	GameControl.SCREEN_CEN_Y;

		 	graphics.drawLine(	x, y-10,	x, y+10);
			graphics.drawLine(	x-10, y,	x+10, y);
		}


		graphics.setColor(Color.white);
		for(int i=0; i!=GameControl.NUM_PLANETS; i++)
		{
			x	=	(((int)Uni.planets[i].sPos.x)-cx)*10;
			y	=	(((int)Uni.planets[i].sPos.z)-cy)*10;

			if(Math.abs(x)<(GameControl.SCREEN_WIDTH-64)/2 && Math.abs(y)<(GameControl.SCREEN_HEIGHT-64)/2)
			{
				x	+=	GameControl.SCREEN_CEN_X;
				y	+=	GameControl.SCREEN_CEN_Y;

				graphics.fillRect(x,y, 2,2);
				
				d	=	Utils.SQR(x-Uni.mouseX) + Utils.SQR(y-Uni.mouseY);
				if(d<BestDistance)
				{
					BestPlanet		=	i;
					BestDistance	=	d;
				}
			}
		}

		if(Uni.MouseClick && BestPlanet != -1)
		{
			s.iSelectedPlanet	=	BestPlanet;
			s.SelectedPlanet	=	Uni.planets[BestPlanet];
		}

		x	=	(((int)Uni.planets[BestPlanet].sPos.x)-cx)*10;
		y	=	(((int)Uni.planets[BestPlanet].sPos.z)-cy)*10;
		if(Math.abs(x)<(GameControl.SCREEN_WIDTH-64)/2 && Math.abs(y)<(GameControl.SCREEN_HEIGHT-64)/2)
		{
			graphics.setColor(Color.red);

		 	x	+=	GameControl.SCREEN_CEN_X;
		 	y	+=	GameControl.SCREEN_CEN_Y;

			graphics.drawOval(x-7,y-7, 14,14);
		}

		graphics.setColor(Color.white);
		if(s.iSelectedPlanet != s.iCurrentPlanet)
		{
			graphics.drawString("Distance:"+ s.SelectedPlanet.distanceFrom(s.CurrentPlanet), 32,20);
		}
		graphics.drawString(GameControl.MISC[6] +" "+ s.CurrentPlanet.name, 32,GameControl.SCREEN_HEIGHT-10);
		Utils.drawStringCentre(graphics, GameControl.MISC[7] +" "+ s.SelectedPlanet.name, GameControl.SCREEN_HEIGHT-10);
	}

	private	void	drawPlanetData(ShipSimulator s)
	{
		int		y;
		Planet	p	=	s.SelectedPlanet;

		graphics.setColor(Color.white);
		graphics.drawString(GameControl.MISC[9]+" "+p.name+"  ("+GameControl.COLOURS[p.iPlanetC]+")", 100,60);

		graphics.setColor(Color.lightGray);
		y	=	92;
		graphics.drawString(GameControl.MISC[8], 100,y);
		graphics.drawString(GameControl.INDUSTRY[p.iIndustry] +"  ("+GameControl.MISC[0]+p.iTechLevel+")", 200,y);
		y	+=	16;
		graphics.drawString(GameControl.MISC[3], 100,y);
		graphics.drawString(GameControl.POLITICS[p.iPolitics], 200,y);
		y	+=	16;
		graphics.drawString(GameControl.MISC[4], 100,y);
		graphics.drawString(GameControl.COLOURS[p.iSpeciesC] +" "+ GameControl.SPECIES[p.iSpecies], 200,y);
		y	+=	16;

		y	+=	32;
		graphics.drawString(GameControl.MISC[2], 100,y);
		y	+=	16;
		graphics.drawString(GameControl.MISC[1], 120,y);
		y	+=	16;
	}

	private	void	drawMarketPrices(ShipSimulator s, StationModel Station)
	{
		boolean	bDocked	=	(GameControl.CURRENT_MODE==GameControl.MODE_DOCKED);

		int	Sel	=	-1;

		graphics.setColor(Color.white);
		graphics.drawString(GameControl.MISC[13], 100,60);
		graphics.drawString(GameControl.MISC[14], 150,60);
		graphics.drawString(GameControl.MISC[15], 400,60);

		if(bDocked)
			graphics.drawString(GameControl.MISC[17], 500,60);

		int	y	=	80;
		for(int i=0; i!=GameControl.NUM_PRODUCTS; i++)
		{
			if(bDocked)
			{
				if(Station.numAvailable[i] == 0)
					graphics.setColor(Color.darkGray);
				else if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
				{
					graphics.setColor(Color.red);
				}
				else
					graphics.setColor(Color.lightGray);
			}
			else
			 	graphics.setColor(Color.lightGray);
			 	
			if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
         	Sel	=	i;
			
			graphics.drawString(""+Station.numAvailable[i], 100,y);
			graphics.drawString(GameControl.ITEM[i], 150,y);
			graphics.drawString(""+((float)Station.price[i])/10, 400,y);

			if(bDocked)
			{
				if(s.Cargo[i] == 0)
					graphics.setColor(Color.darkGray);
				else if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
					graphics.setColor(Color.red);
				else
					graphics.setColor(Color.lightGray);

				graphics.drawString(""+s.Cargo[i], 500,y);
			}

			y+=16;
		}

		if(bDocked)
		{
			y+=32;
			graphics.setColor(Color.white);
			graphics.drawString(GameControl.MISC[16]+": "+((float)s.Credits)/10, 100,y);

			y+=64;

			Utils.drawStringCentre(graphics, GameControl.MISC[18], GameControl.SCREEN_HEIGHT-10);

			if(Uni.MouseClick)
			{
				Uni.MouseClick	=	false;
				if(Sel>=0)
				{
					if(Station.numAvailable[Sel]>0 && Station.price[Sel]<=s.Credits)
					{
						Station.numAvailable[Sel]--;
						s.Cargo[Sel]++;
						s.Credits	-=	Station.price[Sel];
					}
				}
			}

/*
			if(keys.iSell != 0)
			{
				keys.iSell	=	0;
				if(Sel>=0)
				{
					if(s.Cargo[Sel]>0)
					{
						Station.numAvailable[Sel]++;
						s.Cargo[Sel]--;
						s.Credits	+=	Station.price[Sel];
					}
				}
			}
*/
		}
	}

	private	void	drawShipStatus(ShipSimulator s)
	{
		int	y;

		graphics.setColor(Color.lightGray);
		y	=	60;
		graphics.drawString(GameControl.MISC[5] +":", 100,y);
		graphics.drawString(GameControl.MISC[15] +":", 100,y+16);
		graphics.drawString(GameControl.UPGRADES[4] +":", 100,y+32);

		graphics.drawString(GameControl.POLICE[s.PoliceStatus], 200,y);
		graphics.drawString(""+((float)s.Credits)/10, 200,y+16);
		graphics.drawString(""+s.Fuel, 200,y+32);

		y	+=	64;

		graphics.drawString(GameControl.MISC[25]+":", 100,y);
		y	+=	16;

		for(int i=0; i!=4; i++)
		{
			graphics.drawString(GameControl.MISC[20+i] +":", 116,y);

			if(s.LaserType[i] == -1)
				graphics.drawString(GameControl.MISC[24], 150,y);
			else
				graphics.drawString(GameControl.UPGRADES[s.LaserType[i]], 150,y);

			y	+=	16;
		}

		y	+=	16;
		graphics.drawString(GameControl.MISC[26]+":", 100,y);
		y	+=	16;
		int	oy	=	y;
		for(int i=0; i!=8; i++)
		{
			if(s.Upgrade[i])
			{
				graphics.drawString(GameControl.UPGRADES[6+i], 116,y);
				y	+=	16;
			}
		}
		if(oy==y)
			graphics.drawString(GameControl.MISC[24], 116,y);
	}

	private	void	drawShipCargo(ShipSimulator s)
	{
		boolean	flag	=	false;

		int	y	=	80;
		for(int i=0; i!=GameControl.NUM_PRODUCTS; i++)
		{
			if(s.Cargo[i]!=0)
			{
		 		graphics.setColor(Color.lightGray);

				graphics.drawString(""+s.Cargo[i], 100,y);
				graphics.drawString(GameControl.ITEM[i], 150,y);

				flag	=	true;

				y	+=	16;
			}
		}

		graphics.setColor(Color.white);
		if(flag)
		{
			graphics.drawString(GameControl.MISC[13], 100,60);
			graphics.drawString(GameControl.MISC[14], 150,60);
		}
		else
			Utils.drawStringCentre(graphics, GameControl.MISC[19],60);
	}


	private	void	drawEquip(ShipSimulator s)
	{
		int	y,
				Item=-1,
				Tech	=	s.CurrentPlanet.iTechLevel;
				
		graphics.setColor(Color.white);
		graphics.drawString(GameControl.MISC[14], 100,60);
		graphics.drawString(GameControl.MISC[15], GameControl.SCREEN_CEN_X,60);
		
		y	=	80;
		for(int i=4; i!=14; i++)
		{
			if(GameControl.UPTECH[i] <= Tech)
			{
				if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
				{
					graphics.setColor(Color.red);
					Item	=	i;
				}
				else
					graphics.setColor(Color.lightGray);

				graphics.drawString(GameControl.UPGRADES[i], 100,y);
				graphics.drawString(""+((float)GameControl.UPCOST[i])/10, GameControl.SCREEN_CEN_X,y);
				y	+=	16;
			}
		}
		
		for(int i=0; i!=4; i++)
		{
			if(GameControl.UPTECH[i] <= Tech)
			{
				if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
				{
					graphics.setColor(Color.red);
					Item	=	i;
				}
				else
					graphics.setColor(Color.lightGray);
					
				graphics.drawString(GameControl.UPGRADES[i], 100,y);
				graphics.drawString(""+((float)GameControl.UPCOST[i])/10, GameControl.SCREEN_CEN_X,y);
				y	+=	16;
			}
		}
		
		y	+=	32;
		graphics.setColor(Color.white);
		graphics.drawString(GameControl.MISC[16]+": "+((float)s.Credits)/10, 100,y);
		y	+=	20;
		
		if(Uni.MouseClick)
		{
			switch(Uni.EquipState)
			{
				case	0:
					if(Item!=-1)
					{
						Uni.EquipItem	=	Item;
						Uni.EquipState	=	1;
						Uni.MouseClick	=	false;
					}
					break;
						
				case	1:
					if(Item!=-1)
					{
						Uni.EquipItem	=	Item;
						Uni.MouseClick	=	false;
					}
					break;
			}
		}
		
		switch(Uni.EquipState)
		{
			case	0:
				break;
					
			case	1:
				graphics.setColor(Color.white);
				
				if(Uni.EquipItem>3)
				{
					if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
					{
						graphics.setColor(Color.red);
						if(Uni.MouseClick)
						{
							Uni.MouseClick	=	false;
							if(s.Credits>=GameControl.UPCOST[Uni.EquipItem])
							{
								switch(Uni.EquipItem)
								{
									case	4:	//	Fuel
										if(s.Fuel<7)
										{
											s.Credits	-=	GameControl.UPCOST[Uni.EquipItem];
											s.Fuel	=	7;
										}
										break;
										
									case	5:	// Missile
										if(s.NumMissiles<4)
										{
											s.Credits	-=	GameControl.UPCOST[Uni.EquipItem];
											s.NumMissiles++;
										}
										break;
										
									default:
										if(Uni.EquipItem>5)
										{
											if(s.Upgrade[Uni.EquipItem-6]==false)
											{
												s.Credits	-=	GameControl.UPCOST[Uni.EquipItem];
												s.Upgrade[Uni.EquipItem-6]	=	true;
											}
										}
										break;
								}
							}
						}
					}
					else
						graphics.setColor(Color.white);
	
					graphics.drawString("Buy: "+GameControl.UPGRADES[Uni.EquipItem], 100,y);
					y	+=	16;
				}
				else
				{
					// Do laser purchase
					graphics.setColor(Color.white);
					graphics.drawString("Buy: "+GameControl.UPGRADES[Uni.EquipItem], 100,y);
					y	+=	16;
					
					for(int i=0; i!=4; i++)
					{
						if(Uni.mouseY>= y-10 && Uni.mouseY<=y)
						{
							graphics.setColor(Color.red);
							if(Uni.MouseClick)
							{
								Uni.MouseClick	=	false;
								
								if(s.LaserType[i]==-1)
								{
									// New laser for port
									if(s.Credits>=GameControl.UPCOST[Uni.EquipItem])
									{
										s.Credits		-=	GameControl.UPCOST[Uni.EquipItem];
										s.LaserType[i]	=	Uni.EquipItem;
									}
								}
								else if(s.LaserType[i]!=Uni.EquipItem)
								{
									// Upgrade/downgrade
									int	Cred = s.Credits;
									Cred	+=	GameControl.UPCOST[s.LaserType[i]]/2;
									
									if(Cred>=GameControl.UPCOST[Uni.EquipItem])
									{
										s.Credits		=	Cred - GameControl.UPCOST[Uni.EquipItem];
										s.LaserType[i]	=	Uni.EquipItem;
									}
								}
							}
						}
						else
							graphics.setColor(Color.white);
						
						graphics.drawString("--->"+GameControl.MISC[20+i], 100,y);
						y	+=	16;
					}
				}
				
				break;
		}
	}

	private	void	drawLasers(ShipSimulator s)
	{
		int	iTX	=	SCREEN_CEN_X + (int)((Math.random()-0.5)*6),
				iTY	=	SCREEN_CEN_Y + (int)((Math.random()-0.5)*6);
		
		int	Type	=	s.LaserType[s.iView-1];
		
		switch(Type)
		{
			case	0:		
				graphics.setColor(Color.red);
				break;
			case	1:		
				graphics.setColor(Color.green);
				break;
			case	2:		
				graphics.setColor(Color.yellow);
				break;
			case	3:		
				graphics.setColor(Color.blue);
				break;
		}
		
		
		graphics.drawLine(0,SCREEN_HEIGHT-8, iTX,iTY);
		graphics.drawLine(8,SCREEN_HEIGHT, iTX,iTY);

		
		graphics.drawLine(SCREEN_WIDTH,SCREEN_HEIGHT-8, iTX,iTY);
		graphics.drawLine(SCREEN_WIDTH-8,SCREEN_HEIGHT, iTX,iTY);
		
		if(Type==3)
		{
			graphics.drawLine(0,SCREEN_CEN_Y-8, iTX,iTY);
			graphics.drawLine(0,SCREEN_CEN_Y-16, iTX,iTY);
			
			graphics.drawLine(SCREEN_WIDTH, SCREEN_CEN_Y-8, iTX,iTY);
			graphics.drawLine(SCREEN_WIDTH, SCREEN_CEN_Y-16, iTX,iTY);
		}
	}

	private	void	drawHUD(ShipSimulator s)
	{
		// Quick debug bit
		//ShipPlayer[0].debugInfo(graphics, Station.Position, 0,200);
		
		int	x,y;

		Vector	v	=	new Vector(s.CurrentPlanet.position);
		v.sub(camera.position);
		
		x=8;y=SCREEN_HEIGHT-106;
		drawStatBox(x, y, s.FrontShields, s.FrontShieldsMax);
		drawStatBox(x, y+16, s.RearShields, s.RearShieldsMax);
		drawStatBox(x, y+32, s.Fuel, 7);
		drawStatBox(x, y+48, s.CabinTemp, 100);
		drawStatBox(x, y+64, s.LaserTemp, 100);
		drawStatBox(x, y+80, v.size(), 10000);
		drawStatBox(x, y+96, 0, 1);
		
		if(s.NumMissiles!=0)
		{
			for(int i=0; i<s.NumMissiles; i++)
			{
				if(i==(s.NumMissiles-1))
				{
					switch(s.MissileState)
					{
						case	1:	graphics.setColor(Color.red);		break;
						case	2:	graphics.setColor(Color.green);	break;
					}
				}
				graphics.drawString("#", x+i*16, y+5+96-1);
			}
		}
		
		
		int x2=x+70;
		graphics.setColor(Color.yellow);
		graphics.drawString("F.Shield",	x2, y+5);
		graphics.drawString("R.Shield",	x2, y+5+16);
		graphics.drawString("Fuel",			x2, y+5+32);
		graphics.drawString("C.Temp",		x2, y+5+48);
		graphics.drawString("L.Temp",		x2, y+5+64);
		graphics.drawString("Altitude",	x2, y+5+80);
		
		switch(s.MissileState)
		{
			case	0:
				break;
			case	1:
				graphics.setColor(Color.red);
				graphics.drawString("Targeting",	x2, y+5+96);
				break;
			case	2:
				graphics.setColor(Color.green);
				graphics.drawString("Locked",	x2, y+5+96);
				break;
		}
		
		x=SCREEN_WIDTH-70;y=SCREEN_HEIGHT-106;
		drawStatBox(x, y, s.fSpeedTar, s.fSpeedMax);
		drawStatBox(x, y+16, s.Rcur.z, s.Rtar.z, s.Rmax.z);
		drawStatBox(x, y+32, -s.Rcur.x, -s.Rtar.x, s.Rmax.x);
		drawStatBox(x, y+48, s.EnergyBank[3], s.EnergyBankMax);
		drawStatBox(x, y+64, s.EnergyBank[2], s.EnergyBankMax);
		drawStatBox(x, y+80, s.EnergyBank[1], s.EnergyBankMax);
		drawStatBox(x, y+96, s.EnergyBank[0], s.EnergyBankMax);

		x2=x-8;
		graphics.setColor(Color.yellow);
		Utils.drawStringRight(graphics, "Speed",	x2, y+5);
		Utils.drawStringRight(graphics, "Roll",		x2, y+5+32);
		Utils.drawStringRight(graphics, "Pitch",	x2, y+5+16);
		Utils.drawStringRight(graphics, "Bank3",	x2, y+5+48);
		Utils.drawStringRight(graphics, "Bank2",	x2, y+5+64);
		Utils.drawStringRight(graphics, "Bank1",	x2, y+5+80);
		Utils.drawStringRight(graphics, "Bank0",	x2, y+5+96);

		drawRadar(s, SCREEN_WIDTH/2, SCREEN_HEIGHT-52);
		
		y	=	SCREEN_HEIGHT-120;
		graphics.setColor(Color.yellow);
		graphics.drawLine(0,y, SCREEN_WIDTH/5,y);
		graphics.drawLine(SCREEN_WIDTH/5,y, 			SCREEN_WIDTH/5+16,y+16);
		graphics.drawLine(SCREEN_WIDTH/5+16,y+16,	SCREEN_WIDTH*4/5-16,y+16);
		graphics.drawLine(SCREEN_WIDTH*4/5-16,y+16,	SCREEN_WIDTH*4/5,y);
		graphics.drawLine(SCREEN_WIDTH*4/5,y,			SCREEN_WIDTH-1,y);
		
		drawScanner(s, SCREEN_WIDTH*4/5-16-32, y+16);
		
		
		if(s.bStation)
		{
			graphics.setColor(Color.white);
			
			x	=	SCREEN_WIDTH*4/5-16-32;
			y	=	SCREEN_HEIGHT-40;

			graphics.fillRect(x,y, 32,4);
			graphics.fillRect(x,y+16, 32,4);
			graphics.fillRect(x,y+32, 32,4);

			graphics.fillRect(x,y, 4,16);
			graphics.fillRect(x+28,y+16, 4,16);
		}
		

		// Laser sights
		if(s.iView>0 && s.iView<5)
		{
			int	cx	=	SCREEN_CEN_X,
					cy	=	SCREEN_CEN_Y;
					
			
			switch(s.LaserType[s.iView-1])
			{
				case	0:
					graphics.setColor(Color.white);
					graphics.drawLine(cx,cy+4, cx,cy+12);
					graphics.drawLine(cx,cy-4, cx,cy-12);
					graphics.drawLine(cx+4,cy, cx+12,cy);
					graphics.drawLine(cx-4,cy, cx-12,cy);
					break;
					
				case	1:
					graphics.setColor(Color.white);
					graphics.drawLine(cx+4,cy+4, cx+12,cy+12);
					graphics.drawLine(cx+4,cy-4, cx+12,cy-12);
					graphics.drawLine(cx-4,cy+4, cx-12,cy+12);
					graphics.drawLine(cx-4,cy-4, cx-12,cy-12);
					break;
					
				case	2:
					graphics.setColor(Color.white);
					graphics.drawLine(cx-12,cy-12, cx-12+4,cy-12);
					graphics.drawLine(cx-12,cy-12, cx-12,cy-12+4);
					
					graphics.drawLine(cx+12,cy-12, cx+12-4,cy-12);
					graphics.drawLine(cx+12,cy-12, cx+12,cy-12+4);
					
					graphics.drawLine(cx-12,cy+12, cx-12+4,cy+12);
					graphics.drawLine(cx-12,cy+12, cx-12,cy+12-4);
					
					graphics.drawLine(cx+12,cy+12, cx+12-4,cy+12);
					graphics.drawLine(cx+12,cy+12, cx+12,cy+12-4);
					break;
					
				case	3:
					graphics.setColor(Color.white);
					graphics.drawLine(cx-12,cy-12, cx-12+4,cy-12);
					graphics.drawLine(cx-12,cy-12, cx-12,cy-12+4);
					
					graphics.drawLine(cx+12,cy-12, cx+12-4,cy-12);
					graphics.drawLine(cx+12,cy-12, cx+12,cy-12+4);
					
					graphics.drawLine(cx-12,cy+12, cx-12+4,cy+12);
					graphics.drawLine(cx-12,cy+12, cx-12,cy+12-4);
					
					graphics.drawLine(cx+12,cy+12, cx+12-4,cy+12);
					graphics.drawLine(cx+12,cy+12, cx+12,cy+12-4);
					
					graphics.drawLine(cx,cy+4, cx,cy+12);
					graphics.drawLine(cx,cy-4, cx,cy-12);
					graphics.drawLine(cx+4,cy, cx+12,cy);
					graphics.drawLine(cx-4,cy, cx-12,cy);
					break;
			}
		}
	}
 
	private	void	drawStatBox(int x, int y, float cur, float max)
	{
		int	Width			=	64,
				Height		=	10;

		if(max != 0)
		{
			int ScaledValC	=	(int)(cur*(Width-3)/max);
			if(ScaledValC != 0)
			{
				if(ScaledValC > (Width-3))
					ScaledValC	=	Width-3;

				if(ScaledValC > Width/4)
					graphics.setColor(Color.green);
				else if(ScaledValC > Width/8)
					graphics.setColor(Color.yellow);
				else
					graphics.setColor(Color.red);

				graphics.fillRect(x, y-Height/2+2, ScaledValC, Height-4);
			}
		}

		graphics.setColor(Color.yellow);
		graphics.drawRect(x-2,y-Height/2, Width,Height-1);
	}

	private	void	drawStatBox(int x, int y, float cur, float tar, float max)
	{
		int	Width			=	64,
				Height		=	10;

		x	+=	(Width/2)-2;

		graphics.setColor(Color.yellow);
		graphics.drawRect(x-Width/2,y-Height/2, Width,Height-1);
		graphics.drawLine(x, y-Height/2-2, x, y+Height/2+1);

		if(max==0)
			return;
			
		int ScaledValC	=	(int)(cur*(Width/2-2)/max);
		if(ScaledValC != 0)
		{
			graphics.setColor(Color.green);
			if(ScaledValC > 0)
				graphics.fillRect(x, y-Height/2+2, ScaledValC, Height-4);
			else
				graphics.fillRect(x+ScaledValC, y-Height/2+2, -ScaledValC, Height-4);
		}
		
		int ScaledValT	=	(int)(tar*(Width/2-2)/max);
		graphics.setColor(Color.red);
		graphics.drawLine(x+ScaledValT,y-Height/2-2, x+ScaledValT,y+Height/2+1);
	}
 
	private	void	drawRadar(ShipSimulator ShipPlayer, int x,int y)
	{
		int	Width		=	160,
				Height	=	Width/2,
				Scale		=	500, //100,
				lim		=	Scale*Width / 2;


		graphics.setColor(Color.yellow);
		
		drawO(x,y, Width, Height);
		drawO(x,y, (int)(Width*.75), (int)(Height*.75));
		drawO(x,y, (int)(Width*.5), (int)(Height*.5));
		drawO(x,y, (int)(Width*.25), (int)(Height*.25));

		// Do radar relative to ship, not view
		MatrixMath43	mat	=	new MatrixMath43(ShipPlayer.matrix);
		Vector		v		=	new Vector();
		mat.affineInverse();
		
		if(pobjList != null)
		{
			Object3D	s	=	(Object3D) pobjList.Next;
			while(s!=null)
			{
				v.copy(s.position);
				v.sub(ShipPlayer.position);
	
				if(v.size()<lim)
				{
					v.mul(mat);
					
					int	x2	=	(int)(x+v.x/Scale),
							y2	=	(int)(y-v.z/(Scale*2));
				
					graphics.setColor(s.colour);
		
					graphics.drawLine(x2,y2, x2,(int)(y2-v.y/(Scale*2)));
					graphics.fillRect(x2,(int)(y2-v.y/(Scale*2)), 3,3);
				}
	
				s	=	(Object3D) s.Next;
			}
		}

		LinkableObject	p	=	Uni.bodies.Next;
		while(p!=null)
		{
			if(p.type == Uni.OBJ_STATION)
			{
				StationModel Station	=	(StationModel) p;
				
				v.copy(Station.position);
				v.sub(ShipPlayer.position);
				if(v.size()<lim)
				{
					v.mul(mat);
					
					int	x2	=	(int)(x+v.x/Scale),
							y2	=	(int)(y-v.z/(Scale*2));
				
					graphics.setColor(Station.colour);
		
					graphics.drawLine(x2,y2, x2,(int)(y2-v.y/(Scale*2)));
					graphics.fillRect(x2,(int)(y2-v.y/(Scale*2)), 3,3);
				}
			}
			p	=	p.Next;
		}
	}
	
	private	void	drawO(int x, int y, int w, int h)
	{
		graphics.drawOval(x-w/2,y-h/2, w,h);
	}
 
	private	void	drawScanner(ShipSimulator ShipPlayer, int x,int y)
	{
		int	Size	=	32;

		graphics.setColor(Color.yellow);
		graphics.drawRect(x,y, Size,Size);
		graphics.drawOval(x,y, Size,Size);
		
		graphics.fillRect(x+Size/2,y+Size/2, 1,1);
		graphics.fillRect(x+Size/4,y+Size/2, 1,1);
		graphics.fillRect(x+3*Size/4,y+Size/2, 1,1);
		graphics.fillRect(x+Size/2,y+Size/4, 1,1);
		graphics.fillRect(x+Size/2,y+3*Size/4, 1,1);
		
		
		MatrixMath43	mat	=	new MatrixMath43(ShipPlayer.matrix);
		mat.affineInverse();
		
		
		Vector	v	=	new Vector();
		
		if(ShipPlayer.bStation)
		{
			v.copy(Uni.Station.position);		//Naughty
			v.sub(ShipPlayer.position);
			v.mul(mat);
	      
	      if(v.z<0)
	      {
				graphics.setColor(Color.lightGray);
				v.z	=	-v.z;
			}
			else
				graphics.setColor(Color.white);
		}
		else
		{
			v.copy(ShipPlayer.CurrentPlanet.position);
			v.sub(ShipPlayer.position);
			v.mul(mat);
	      
	      if(v.z<0)
	      {
				graphics.setColor(Color.red);
				v.copy(Uni.Sun.position);			//Naughty
				v.sub(ShipPlayer.position);
				v.mul(mat);
			}
			else
				graphics.setColor(Color.green);
		}

		float	x2	=	Utils.atan(v.x, v.z),
				y2	=	Utils.atan(-v.y, v.z);
	
		x2	=	(float)(x2/Math.PI*Size);
		y2	=	(float)(y2/Math.PI*Size);

		graphics.fillRect(x+Size/2+((int)x2)-1,y+Size/2+((int)y2)-1, 3,3);
	}
 
// Field of vision, determine whether object is visible to the camera 	
	public	static	boolean	fovClip(Vector Pos)
	{ 	
 		boolean occlude	=	false;
		if(Pos.z < GameControl.PERSPECTIVE_H/2)		occlude = true;
		else if(Pos.z > 30000)		occlude = true;
		else if(Pos.x<-1.5*Pos.z)	occlude = true;
		else if(Pos.x>1.5*Pos.z)	occlude = true;
		else if(Pos.y<-1.2*Pos.z)	occlude = true;
		else if(Pos.y>1.2*Pos.z)	occlude = true;
		return(occlude);
	}

}