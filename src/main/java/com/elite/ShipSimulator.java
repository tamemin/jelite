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
// Space ship sim class
import java.awt.*;

class ShipSimulator extends Object3D
{
	public Vector		Rcur	=	new Vector();
	public Vector		Rmax	=	new Vector();
	public Vector		Rtar	=	new Vector();
	
	public Vector		Radjust	=	new Vector();
	public Vector		Rdampen	=	new Vector();
	
	public float	fSpeedCur;
	public float	fSpeedMax;
	public float	fSpeedTar;
	
	public boolean	PlayerCraft;
	public int		PlayerId;
	
	public boolean	bFire,
						bShoot,
						bMissileFire;
	public ShipSimulator	sBest;
	public boolean	bStation;
   
	public float	FrontShields,
						FrontShieldsMax,
						RearShields,
						RearShieldsMax,
						EnergyBank[] = new float[4],
						EnergyBankMax,
						EnergyRechargeRate,
						LaserTemp,
						CabinTemp,
						Fuel;

	public int		LaserType[]	=	new int[4],
						FiringRate;

	public int		NumMissiles,
						MissileState;
						
	public Object3D	MissileTarget;

	public boolean	Upgrade[] = new boolean[8];

	public int		iView;

	public int		iCurrentPlanet,
						iSelectedPlanet;
	
	public Planet	CurrentPlanet,
						SelectedPlanet;
	
	public int		Cargo[]	=	new int[GameControl.NUM_PRODUCTS];
	public int		CargoSize;

	public int		Credits;
	public int		PoliceStatus;


//	Auto-pilot/simple AI
	private int			iAutoState;
	private int			iAutoStateOld;
	private int			iTimeInState;
	
	private ShipSimulator	shipTar;
	private StationModel	stationTar;

	public static final	int	AI_DO_NOTHING	=	0;
	public static final	int	AI_FOLLOW_SHIP	=	0x100;
	public static final	int	AI_ATTACK_SHIP	=	0x200;
	public static final	int	AI_FLY_AWAY 	=	0x300;
	public static final	int	AI_DOCK			=	0x400;

	private	int	ShipType;

	ShipSimulator()
	{				  
		Next	=	Prev	=	null;
		defaults(0);
	}
	
	public ShipSimulator Next()
	{
		return((ShipSimulator)this.Next);
	}

	public void defaults(int iShipType)
	{
		position.zero();
		angle.zero();
		renderPos.zero();
		Rtar.zero();
		Rcur.zero();
		matrix.unit();

		ShipType	=	iShipType;

		// General ship-specific setup
		switch(iShipType)
		{
			case	GameControl.Missile:
				fSpeedMax	=	200;

				Rmax.x	=	(float)(Math.PI/60);
				Rmax.y	=	(float)(Math.PI/60);
				Rmax.z	=	(float)(Math.PI/60);

				Radjust.x	=	(float)(Rmax.x/10);
				Radjust.y	=	(float)(Rmax.y/10);
				Radjust.z	=	(float)(Rmax.z/10);
				
				Rdampen.x	=	(float)(Rmax.x/20);
				Rdampen.y	=	(float)(Rmax.y/20);
				Rdampen.z	=	(float)(Rmax.z/20);
				break;

			case	GameControl.CobraIII:
				FrontShieldsMax		=	100;
				RearShieldsMax			=	100;
				EnergyRechargeRate	=	1;

				LaserType[0]			=	0;
				LaserType[1]			=	-1;
				LaserType[2]			=	-1;
				LaserType[3]			=	-1;
				fSpeedMax	=	160;

				Rmax.x	=	(float)(Math.PI/100);
				Rmax.y	=	(float)(Math.PI/100);
				Rmax.z	=	(float)(Math.PI/100);
				
				Radjust.x	=	(float)(Rmax.x/20);
				Radjust.y	=	(float)(Rmax.y/20);
				Radjust.z	=	(float)(Rmax.z/20);
				
				Rdampen.x	=	(float)(Rmax.x/5);
				Rdampen.y	=	(float)(Rmax.y/5);
				Rdampen.z	=	(float)(Rmax.z/5);
				break;

			default:
				FrontShieldsMax		=	100;
				RearShieldsMax			=	100;
				EnergyRechargeRate	=	1;

				LaserType[0]			=	0;
				LaserType[1]			=	-1;
				LaserType[2]			=	-1;
				LaserType[3]			=	-1;
				fSpeedMax	=	40;

				Rmax.x	=	(float)(Math.PI/100);
				Rmax.y	=	(float)(Math.PI/100);
				Rmax.z	=	(float)(Math.PI/100);
				
				Radjust.x	=	(float)(Rmax.x/10);
				Radjust.y	=	(float)(Rmax.y/10);
				Radjust.z	=	(float)(Rmax.z/10);
				
				Rdampen.x	=	(float)(Rmax.x/20);
				Rdampen.y	=	(float)(Rmax.y/20);
				Rdampen.z	=	(float)(Rmax.z/20);

				break;
		}

//
		iSelectedPlanet	=	60;
		if(iSelectedPlanet>GameControl.NUM_PLANETS)
			iSelectedPlanet = GameControl.NUM_PLANETS-1;
		iCurrentPlanet		=	iSelectedPlanet;

		Fuel					=	7;
		
		CargoSize		=	30 * 1000000;
		Credits			=	1000; // * 10000;
		PoliceStatus	=	0;

		for(int i=0; i!=GameControl.NUM_PRODUCTS; i++)
			Cargo[i]	=	0;

		FrontShields		=	FrontShieldsMax;
		RearShields			=	RearShieldsMax;
		EnergyBankMax		=	10; // ** MOD - Reduced energy
		EnergyBank[0]		=	EnergyBankMax;
		EnergyBank[1]		=	EnergyBankMax;
		EnergyBank[2]		=	EnergyBankMax;
		EnergyBank[3]		=	EnergyBankMax;

		LaserTemp			=	0;
		CabinTemp			=	0;
		FiringRate			=	0;
		
		fSpeedCur	=	0;
		fSpeedTar	=	0;
		
		bFire				=	false;
		bMissileFire	=	false;
		sBest				=	null;
		
		NumMissiles		=	3;
		MissileState	=	0;		

		for(int i=0; i!=8; i++)
			Upgrade[i]	=	false;
			
		//Upgrade[6]	=	true; ** MOD - Turn off docking computer **

		randomColour();


		iAutoState		=	AI_DO_NOTHING;
		iAutoStateOld	=	iAutoState;
		iTimeInState	=	0;
		shipTar			=	null;
		stationTar		=	null;
	}
	
	public void AIdock(StationModel s)
	{
      if(s==null)
      	return;
		iAutoStateOld	=	iAutoState;
		iAutoState		=	AI_DOCK;
		iTimeInState	=	0;
		stationTar			=	s;
	}

	public void AIfollow(ShipSimulator s)
	{
      if(s==null)
      	return;
      AIChangeState(AI_FOLLOW_SHIP, s);
	}

	public void AIattack(ShipSimulator s)
	{
      if(s==null)
      	return;
      AIChangeState(AI_ATTACK_SHIP, s);
	}

	public void AIChangeState(int NEW_STATE, ShipSimulator s)
	{
		iAutoStateOld	=	iAutoState;
		iAutoState		=	NEW_STATE;
		iTimeInState	=	0;
		shipTar			=	s;
	}
   
    public void run(Universe Uni)
    {
      MatrixMath43	m	=	new MatrixMath43(), m2	=	new MatrixMath43();
      Vector	thrust	=	new Vector();
      
      if(GameControl.bPart && !PlayerCraft)
      {
        Part	p	=	(Part)Uni.partFree.Next;
        if(p != null)
        {
          p.linkTo(Uni.partUsed);
          p.setup(matrix, position);
        }
      } 	
      // Energy replenishment
      if(PlayerCraft) recharge(EnergyRechargeRate);
      if(PlayerCraft)
      {
        if(iAutoState!=0)
        {
          switch(iAutoState)
          {
            case	1:
              followShip();
	   				break;
            case	2:
              followStation();
	   				break;
          }
        }
      }
      else
      {
        switch(iAutoState)
          {
            case	AI_DO_NOTHING:
              break;
            case	AI_DOCK:
              followStation();
              break;
            case	AI_FOLLOW_SHIP:
              followShip();
              break;
            case	AI_ATTACK_SHIP:
              float Dist = followShip();
              if(Dist<GameControl.SHIP_RANGE/2)
              {
                if(iTimeInState>400 && Dist<GameControl.SHIP_RANGE/24)
                {
                  AIChangeState(AI_FLY_AWAY, shipTar);
                }
                else
                {
                  // Consider firing?
                  if((iTimeInState  & 0xf)==0)
                  {
                    bFire	=	true;
                  }
                }
              }
              break;
            case  AI_FLY_AWAY:
              if(iTimeInState<400)
              {
                Vector	vecTar	=	new Vector(position);
                vecTar.sub(shipTar.position);
                vecTar.add(position);
                fSpeedTar   =  fSpeedMax*0.8f;
            		followVec(vecTar);
              }
              else
              {
                AIChangeState(AI_ATTACK_SHIP, shipTar);
              }
                break;
          }
	   	iTimeInState++;
      } // end
      Rcur.x	=	Utils.moveToTarget(Rcur.x, Rtar.x, Rdampen.x);
      Rcur.y	=	Utils.moveToTarget(Rcur.y, Rtar.y, Rdampen.y);
      Rcur.z	=	Utils.moveToTarget(Rcur.z, Rtar.z, Rdampen.z);
		// Want z rotation first - due to autopilot/AI requirements
      if(Rcur.z!=0)
      {
        thrust.set(0f,0f,1f);
        thrust.mul(matrix);
        m.rotateAbout(thrust, -Rcur.z);
        matrix.mul(m);
      }
      if(Rcur.x!=0)
      {
        thrust.set(1f,0f,0f);
        thrust.mul(matrix);
        m.rotateAbout(thrust, -Rcur.x);
        matrix.mul(m);       
      }
      if(Rcur.y!=0)
      {
        thrust.set(0f,1f,0f);
        thrust.mul(matrix);
        m.rotateAbout(thrust, -Rcur.y);
        matrix.mul(m);
      }
      // Ensure minimum speed for ships
      if(fSpeedTar<4) fSpeedTar	=	4;
      fSpeedCur	=	Utils.moveToTarget(fSpeedCur, fSpeedTar, 2f);
      if(fSpeedCur != 0)
      {
        thrust.set(0f,0f,(float)fSpeedCur);
        thrust.mul(matrix);
        position.add(thrust);
        movement.copy(thrust);
      }
      else movement.set(0f,0f,0f);
      // ****************
      // Handle weaponary   		
      // ****************
      Vector	laserDir	=	new Vector();
      int	View;		
      if(!PlayerCraft)
      {
        laserDir.set(0f,0f,1f);
        View = 1;
      }
      else
      {
        View	=	iView;
        switch(iView)
        {
          case	1:	laserDir.set(0f,0f,1f);		break;
          case	2:	laserDir.set(0f,0f,-1f);	break;
          case	3:	laserDir.set(-1f,0f,0f);	break;
          case	4:	laserDir.set(1f,0f,0f);		break;
        }
      }
      bShoot	=	false;
     	// Handle laser firing
      if(bFire)
      {
      	if(FiringRate>0) FiringRate--;	
      	if(FiringRate==0)
      	{
          float	Heat	=	((float)GameControl.LaserHeat[LaserType[View-1]])/4;
      		if(LaserTemp<100-Heat)
      		{
            LaserTemp	=	Utils.moveToTarget(LaserTemp,100,Heat);
	    			FiringRate	=	GameControl.LaserRate[LaserType[View-1]];
	    			bShoot		=	true;
            SoundEngine.playSound(0);
		    	}
      	}
      }
      if(!bShoot) LaserTemp	=	Utils.moveToTarget(LaserTemp, 0, 0.1f);	// Cool down
      if(bShoot || MissileState == 1)
      {  
        // Let's see if we're pointing at another ship?
        Vector	dis	=	new Vector();
        float BestDist = Float.MAX_VALUE, Dist;
        int	BestColValue = 0;
        sBest =	null;
        laserDir.mul(matrix);
        ShipSimulator s	=	(ShipSimulator)Uni.shipUsed.Next;
        while(s!=null)
        {
          if(s!=this)
          {
            dis.copy(position);
            dis.sub(s.position);
            Dist	=	dis.size();
            if(Dist<BestDist)
            {
              int	ColValue	=	s.collideWithVec(position, laserDir);
              if(ColValue != 0)
              {
                BestDist			=	Dist;
                sBest				=	s;
                BestColValue	=	ColValue;
              }
            }
          }
          s	=	s.Next();
        }
        if(sBest != null)
        {
          // Target aquired (missile or laser)
          if(MissileState == 1)
          {
            MissileState	=	2;
            MissileTarget	=	sBest; //(obj3d) sBest;
          }	
          if(bShoot)
          {  
            if(!sBest.damage(BestColValue, GameControl.LaserDamage[LaserType[View-1]]))
            {
              if(sBest.PlayerCraft)
              {
                System.out.println("Player ship destroyed");
                GameControl.CURRENT_MODE	=	GameControl.MODE_TITLE;
              }
              else
              {
                sBest.linkTo(Uni.shipFree);	// Perhaps, have an exploding ship list - Mmm.
                System.out.println("Enemy ship destroyed");
                SoundEngine.playSound(1);
              }
            }
            else
            {
                // Inflict damage on target ship
                SoundEngine.playSound(2); // Hit ship sound
            }
          }
        }  
      }
      if(bMissileFire && MissileState==2)
      {
        // Launch missile at enemy
        ShipSimulator	s		=	(ShipSimulator) Uni.shipFree.Next;
        if(s!=null)
        {
          NumMissiles--;
          MissileState		=	0;
          s.defaults(GameControl.Missile);
          s.followShip((ShipSimulator)MissileTarget);
          s.position.copy(position);
          s.matrix.copy(matrix);
          s.setModel(Uni.models[2]);
          s.fSpeedCur	=	s.fSpeedMax;
          s.linkTo(Uni.shipUsed);
        }
      }
      // Are we near the space station? Then do bodged dock
      Vector	stat	=	new Vector(Uni.Station.position);
      stat.sub(position);
      bStation	=	stat.size() < Uni.Station.RANGE;
      if(stat.size() < 200)
      {
        if(PlayerCraft) GameControl.CURRENT_MODE	=	GameControl.MODE_DOCK;
        else linkTo(Uni.shipFree);
      }
    }
	public	boolean	damage(int ColValue, float Damage)
	{
		if(ColValue==1)
		{
			if(FrontShields>0)
			{
				FrontShields	-=	Damage;

				if(FrontShields<0)
				{
					Damage			=	-FrontShields;
					FrontShields	=	0;
				}
				else
					Damage	=	0;
			}
		}
		else
		{
			if(RearShields>0)
			{
				RearShields	-=	Damage;

				if(RearShields<0)
				{
					Damage		=	-RearShields;
					RearShields	=	0;
				}
				else
					Damage	=	0;
			}
		}

		if(Damage>0)
		{
			for(int Bank=3; Bank>=0; Bank--)
			{
				if(EnergyBank[Bank]>0)
				{
					EnergyBank[Bank]	-=	Damage;

					if(EnergyBank[Bank]<0)
					{
						Damage				=	-EnergyBank[Bank];
						EnergyBank[Bank]	=	0;
					}
					else
						Damage	=	0;
				}

				if(Damage==0)	break;
			}
		}
		
		boolean	Alive;
		if(EnergyBank[0]!=0)
			Alive=true;
		else
			Alive=false;

		return(Alive);
	}

	public void recharge(float Energy)
	{
		if(Energy>0)
		{
			for(int Bank=0; Bank<=3; Bank++)
			{
				if(EnergyBank[Bank]<EnergyBankMax)
				{
					EnergyBank[Bank]	+=	Energy;

					if(EnergyBank[Bank]>EnergyBankMax)
					{
						Energy				=	EnergyBank[Bank]-EnergyBankMax;
						EnergyBank[Bank]	=	EnergyBankMax;
					}
					else
						Energy	=	0;
				}

				if(Energy==0)	break;
			}
		}

		if(Energy!=0)
		{
			if(FrontShields<RearShields)
			{
				FrontShields	+=	Energy;
				if(FrontShields>FrontShieldsMax)
					FrontShields	=	FrontShieldsMax;
			}
			else
			{
				RearShields	+=	Energy;
				if(RearShields>RearShieldsMax)
					RearShields	=	RearShieldsMax;
			}
		}
	}

	public	void	jump(Object3D s)
	{
		boolean	jump	=	true;

		Vector	v		=	new Vector();
		
		while(s != null)
		{
			if(s!=this)
			{
				v.sub(s.position, this.position);
	         
	         if(v.size() <= GameControl.SHIP_RANGE)
	         {
	         	jump	=	false;
	         	break;
	         }
	      }
	      
			s	=	(Object3D) s.Next;
		}
		
		if(jump)
		{
			Vector thrust = new Vector(0f,0f, 5000f);
	  		thrust.mul(matrix);
	  		position.add(thrust);
	  	}
	}
	
	public void autoStop()
	{
		iAutoState	=	0;
	}
	
	public boolean autoEngaged()
	{
		return(iAutoState!=0);
	}
	
	public void	followShip(ShipSimulator s)
	{
		if(s==null)	return;
		iAutoState	=	1;
		shipTar		=	s;
	}
	
	public void followStation(StationModel s)
	{
		if(s==null)	return;
		iAutoState	=	2;
		stationTar	=	s;
	}
	
	
	private float followShip()
	{
		float	Dist;
		Vector	vecTar	=	new Vector(shipTar.position);
		
		Dist	=	followVec(vecTar);
		return(Dist);
	}
	
	private void followStation()
	{
		Vector	vecTar	=	new Vector(stationTar.position);
		followVec(vecTar);
	}
	
	private float followVec(Vector vecTar)
	{
		MatrixMath43	mat	=	new MatrixMath43(matrix);
		
		mat.affineInverse();
		vecTar.sub(this.position);
		
		float	Dist	=	vecTar.size();
		
		vecTar.mul(mat);
		
		float ang;
		
		if(vecTar.y>=0)
			ang	=	Utils.atan(vecTar.x, vecTar.y);
		else
			ang	=	-Utils.atan(vecTar.x, -vecTar.y);
		
		Rtar.z	=	ang/4;
		
		if(Rtar.z>Rmax.z)
			Rtar.z	=	Rmax.z;
		else	if(Rtar.z<-Rmax.z)
			Rtar.z	=	-Rmax.z;
			
//		if(Math.abs(Rtar.z) < Math.PI/180)
//			Rtar.z	=	0;
			
		// Should really rotate to create new vecTar.y for following calc
		// This may be a tad excessive
		//vecTar.y	=	(float)(-vecTar.x*Math.sin(Rtar.z) + vecTar.y*Math.cos(Rtar.z));
		
		if(Math.abs(ang)<Math.PI/4 || Math.abs(ang)>Math.PI*3/4)
		{
			float ang2	=	Utils.atan(vecTar.y, vecTar.z);
			
			Rtar.x	=	ang2/6;
			
			if(Rtar.x>Rmax.x)
			{
				Rtar.x	=	Rmax.x;
			}
			else	if(Rtar.x<-Rmax.x)
			{
				Rtar.x	=	-Rmax.x;
			}
		}
			
//		if(Math.abs(Rtar.x) < Math.PI/180)
//			Rtar.x	=	0;
			
		// Negate damping as a factor
		/*
		if(!PlayerCraft)
		{
			Rtar.x	*=	0.7f;
			Rtar.z	*=	0.7f;
		}
		*/
		
		if(Dist<4000)
			fSpeedTar	=	fSpeedMax*0.25f;
		else
			fSpeedTar	=	fSpeedMax*0.6f;

		return(Dist);
	}

	public void debugInfo(Graphics g, Vector vecTarO, int x, int y)
	{
		Vector		vecTar	=	new Vector(vecTarO);
		MatrixMath43	mat		=	new MatrixMath43(matrix);
		
		mat.affineInverse();
		vecTar.sub(this.position);
		vecTar.mul(mat);
		
		float ang2	=	Utils.atan(vecTar.y, vecTar.z);
		
		g.setColor(Color.green);
		g.drawString("X:"+vecTar.x, x, y);
		g.drawString("Y:"+vecTar.y, x, y+16);
		g.drawString("Z:"+vecTar.z, x, y+32);
		g.drawString("Ang2:"+ang2, x, y+48);
	}

}

