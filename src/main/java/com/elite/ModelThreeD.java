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
// Simple 3d model class

import java.awt.*;
import java.io.*;
import java.util.*;


class ModelThreeD {

	private	int	iNFaces, iNVecs;

	private	BufferedInputStream	bufferIn;

	private	ArrayList<Face>	faces	=	new ArrayList<Face>();
	private	ArrayList<Vector> vecs	=	new ArrayList<Vector>();

	// Bounds
	public	Vector	Max	=	new Vector();
	public	Vector	Min	=	new Vector();

// File loading state statics
	private final int	LOAD_NOTHING	=	0,
							LOAD_NVERTS		=	1,
							LOAD_NFACES		=	2,
							LOAD_FACES		=	3,
							LOAD_VERTS		=	4,
                     LOAD_RGB			=	5,
                     LOAD_NORMAL		=	6,
							LOAD_END			=	10;

   public ModelThreeD() {
		iNFaces	=	0;
		iNVecs	=	0;
   }

	public	void	addVec(float x, float y, float z)
	{
		vecs.add(new Vector(x,y,z));
		iNVecs++;
	}

	public	void	addFace(int v0, int v1, int v2, int v3)
	{
		faces.add(new Face(v0,v1,v2,v3));
		iNFaces++;
	}

   public void addFace(Color c, Vector normal, int i[])
	{
   	Face f	=	new Face(i);

      f.normal.copy(normal);
      f.c	=	c;
      
		faces.add(f);
		iNFaces++;
	}
    

	public void printInfo(Graphics g)
	{
		g.drawString("NVerts:"+iNVecs, 10, 40);
		g.drawString("NFaces:"+iNFaces, 10, 50);
	}

	public int nfaces()
	{
		return(iNFaces);
	}

	public Vector vec(int i)
	{
		return((Vector) vecs.get(i));
	}

	public Vector vec(int i, int j)

	{
		return((Vector) vecs.get(face(i).v[j]));
	}

	public Face	face(int i)
	{
		return((Face) faces.get(i));
	}

	public	boolean	load(InputStream is)
	{
		boolean					flag;

		flag	=	true;
	
		bufferIn = new BufferedInputStream(is);
	

		if(flag)
		{
			int		iC,
						iState	=	LOAD_NOTHING,
						iNumsReq	=	0,
						iNumsCur	=	0;

			int		i[]	=	new int[16];
         float		f[]	=	new float[16];

			char		c;

         int	r=0,g=0,b=0;
         Vector	normal	=	new Vector();
         
			String	str = new String();

			try{
				boolean flag2 = true;
				do
				{
					str	=	"";

					while(true)
					{
						iC	=	bufferIn.read();
						if(iC==-1)	break;

						if(iC!=10 && iC !=13)
						{
							//if(iC>=' ')
							//{
								c	=	(char)iC;
								str	+=	c;
							//}
						}
						else
						{
							if(iC==10)	break;
						}
					}

//					System.out.println("-*-" + str + "-*-");

					StringTokenizer st = new StringTokenizer(str, ", \t");

					while (st.hasMoreTokens())
					{
						String	strCom	=	st.nextToken();

						if(strCom.charAt(0) >= '0' && strCom.charAt(0) <='9' || strCom.charAt(0)=='-')
						{
                     Float    Flt   =  new Float(strCom);
							// Integer	Int	=	new Integer(strCom);
                     f[iNumsCur]	=	Flt.floatValue();
							i[iNumsCur]	=	Flt.intValue();
                     
                  	if(iNumsCur==0 && iState==LOAD_FACES)
                     {
                     	iNumsReq	=	i[0]+1;
                     }
                  
							// Numerical value
							iNumsCur++;

							if(iNumsCur == iNumsReq)
							{
								switch(iState)
								{
									case	LOAD_NVERTS:
										//iNVecs	=	i[0];
										break;
									case	LOAD_NFACES:
										//iNFaces	=	i[0];
										break;
									case	LOAD_VERTS:
										//addVec((float)i[0], (float)-i[1], (float)-i[2]);
										//addVec((float)i[0], (float)i[1], (float)-i[2]);
                              
										addVec((float)i[0], (float)i[1], (float)i[2]); // Elite ships
										break;       

                           case	LOAD_RGB:
                           	r	=	i[0];
                              g	=	i[1];
                              b	=	i[2];
                              
										iNumsReq	=	3;
                              iState	=	LOAD_NORMAL;

                           	break;       
                              
                           case	LOAD_NORMAL:
                              normal.x	=	f[0];
                              normal.y	=	f[1];
                              normal.z	=	f[2];
                              
										iNumsReq	=	4;
                              iState	=	LOAD_FACES;
                           	break;                 
                              
									case	LOAD_FACES:
										// addFace(i[0], i[1], i[2], i[3]);
                              // System.out.println(i[0]);
                              Color col	=	new Color(r,g,b);
                             
                              addFace(col, normal, i);

										iNumsReq	=	3;
                              iState	=	LOAD_RGB;

                              /*
                              if(i[0]==3)
											addFace(i[1], i[2], i[3], 255);
                              else
											addFace(i[1], i[2], i[3], i[4]);
                              */
                                 
										break;
								}

								iNumsCur	=	0;
							}

							//System.out.println(strCom + "==" + i[0]);
						}
						else
						{
							// New command
							if(strCom.compareTo("NVERTS")==0)
							{
								iState	=	LOAD_NVERTS;
								iNumsReq	=	1;
							}
							else if(strCom.compareTo("NFACES")==0)
							{
								iState	=	LOAD_NFACES;
								iNumsReq	=	1;
							}
							else if(strCom.compareTo("VERTEX")==0)
							{
								iState	=	LOAD_VERTS;
								iNumsReq	=	3;
							}
							else if(strCom.compareTo("FACES")==0)
							{
								iState	=	LOAD_RGB;
								iNumsReq	=	3;
							}
							else if(strCom.compareTo("END")==0)
							{
								iState	=	LOAD_END;
								iNumsReq	=	0;
							}
							else
								iState	=	LOAD_NOTHING;

							iNumsCur	=	0;

//							System.out.println(strCom + iState);
						}
					}

					if(iC==-1)
						flag2	=	false;
					else	if(str.length() != 0)
					{
						if(str == "END")
							flag2 = false;
					}
				}while(flag2);
			}
			catch(IOException e)
			{
				System.out.println("IOException:"+e);
			}
			
			// Create bounding box
			for(int iV=0; iV!=iNVecs; iV++)
			{
				if(iV==0)
				{
					Min.x	=	Max.x	=	vec(iV).x;
					Min.y	=	Max.y	=	vec(iV).y;
					Min.z	=	Max.z	=	vec(iV).z;
				}
				else
				{
					if(vec(iV).x < Min.x)	Min.x = vec(iV).x;
					if(vec(iV).y < Min.y)	Min.y = vec(iV).y;
					if(vec(iV).z < Min.z)	Min.z = vec(iV).z;

					if(vec(iV).x > Max.x)	Max.x = vec(iV).x;
					if(vec(iV).y > Max.y)	Max.y = vec(iV).y;
					if(vec(iV).z > Max.z)	Max.z = vec(iV).z;
				}
			}

			//	Might as well create some face normals
			Vector v[]	= new Vector[2];
			
			for(int iF=0; iF!=iNFaces; iF++)
			{
				v[0]	=	new Vector( vec(iF,1).x - vec(iF,0).x,
										vec(iF,1).y - vec(iF,0).y,
										vec(iF,1).z - vec(iF,0).z);
				
				v[1]	=	new Vector( vec(iF,2).x - vec(iF,0).x,
										vec(iF,2).y - vec(iF,0).y,
										vec(iF,2).z - vec(iF,0).z);
				
				v[0].normalise();
				v[1].normalise();
				v[0].mul(v[1]);
				
				v[0].normalise();	// Shouldn't be needed - but due to rounding errors, makes the absolute world of difference

            /*
            System.out.println(v[0].x + ","+v[0].y + ","+v[0].z+"===="
            							+ face(iF).normal.x+","
            							+ face(iF).normal.y+","
            							+ face(iF).normal.z+",");
            */
			}
		}

		return(flag);
	}
	
	
	// Draw object into order table
	public void render(Polygon OrderTable[], Polygon PolyFree, Vector light, MatrixMath43 m, int col)
	{
		MatrixMath43	m2 = new MatrixMath43();
		Vector		v	= new Vector(),
      			v2	= new Vector();
		int		iot = 0;

	// Can now use m2 for lighting purposes
		m2.copy(m);
		m2.trans(0,0,0);

      v2.set(m.m[3][0], m.m[3][1], m.m[3][2]);
		iot	=	(int)(v2.size()/10);
  		if(iot<1)	iot=1;
		if(iot>=GameControl.NUM_Z_BUCKETS) iot=GameControl.NUM_Z_BUCKETS-1;
    
		for(int count=0; count!=this.iNFaces; count++)
		{
      	int	i	=	this.iNFaces-count-1;
      
			int	x[] = new int[Face.MAX_V];
			int	y[] = new int[Face.MAX_V];
               
			Polygon	p	=	PolyFree.Next;
		
			if(p == null)
				break;
			else
			{
				boolean	drawFlag = true;
				int		iNSides;
		
				iNSides	=	this.face(i).num_v;

            // Normal clip*********************
            // transform normal
            v2.copy(this.face(i).normal);
            v2.mul(m2);
            // transform point on face
				v.copy(this.vec(i,0));
            v.mul(m);
            v.normalise();

            float d	=	v.dot(v2);
            // d	=	-1;
            
            if(d<0 && iNSides>2)
            {
					int		iOT = 0;
               
  				   for(int j=0; j!=iNSides; j++)
				   {
				   	v.copy(this.vec(i,j));
            
				   	v.mul(m);
				   	
				   	if(v.z < ((GameControl.PERSPECTIVE_H/2)-GameControl.Z_OFFSET))
				   	{
				   		// -ve z check; i.e. should clip with z=0 (or similar)
				   		drawFlag	=	false;
				   		break;
				   	}
				   	
				   	iOT	+=	v.pers();
				   	
				   	p.x[j]	=	v.sX;
				   	p.y[j]	=	v.sY;
				   }
		
				   if(drawFlag)
				   {
						iOT	/=	iNSides;
                  
			   		v.copy(this.face(i).normal);
			   		v.mul(m2);
                     
				   	int	Intensity = ((int)(Math.acos(v.dot(light)) * (224f/Math.PI)))+31,
                    		r,g,b;

                  if(this.face(i).c.getRed()==0
                  &&	this.face(i).c.getGreen()==0
                  &&	this.face(i).c.getBlue()==0)
                  {
				   	   p.c	=	new Color(Intensity, Intensity, Intensity);
                  }           
                  else
                  {
                     r	=	(this.face(i).c.getRed() * Intensity)/255;
                     g	=	(this.face(i).c.getGreen() * Intensity)/255;
                     b	=	(this.face(i).c.getBlue() * Intensity)/255;
                     p.c	=	new Color(r,g,b);
                  }                                     
				   	p.iNSides	=	iNSides;
      				iOT	=	iot;
		
				   	p.linkTo(OrderTable[iOT]);
				   }
            }
			}
		}
	}


	public void renderBounds(Graphics g, MatrixMath43 m)
	{
		Vector	av[] = new Vector[8];
		int	x[] = new int[4],
				y[] = new int[4];

		av[0]	=	new Vector(Min.x, Min.y, Min.z);
		av[1]	=	new Vector(Max.x, Min.y, Min.z);
		av[2]	=	new Vector(Min.x, Min.y, Max.z);
		av[3]	=	new Vector(Max.x, Min.y, Max.z);

		av[4]	=	new Vector(Min.x, Max.y, Min.z);
		av[5]	=	new Vector(Max.x, Max.y, Min.z);
		av[6]	=	new Vector(Min.x, Max.y, Max.z);
		av[7]	=	new Vector(Max.x, Max.y, Max.z);

		av[0].mul(m);	av[1].mul(m);	av[2].mul(m);	av[3].mul(m);
		av[4].mul(m);	av[5].mul(m);	av[6].mul(m);	av[7].mul(m);

		av[0].pers();	av[1].pers();	av[2].pers();	av[3].pers();
		av[4].pers();	av[5].pers();	av[6].pers();	av[7].pers();

		g.setColor(Color.cyan);

		x[0] = av[0].sX; y[0] = av[0].sY;
		x[1] = av[1].sX; y[1] = av[1].sY;
		x[2] = av[3].sX; y[2] = av[3].sY;
		x[3] = av[2].sX; y[3] = av[2].sY;
		g.drawPolygon(x, y, 4);

		x[0] = av[4].sX; y[0] = av[4].sY;
		x[1] = av[5].sX; y[1] = av[5].sY;
		x[2] = av[7].sX; y[2] = av[7].sY;
		x[3] = av[6].sX; y[3] = av[6].sY;
		g.drawPolygon(x, y, 4);

		g.drawLine(av[0].sX,av[0].sY, av[4].sX,av[4].sY);
		g.drawLine(av[1].sX,av[1].sY, av[5].sX,av[5].sY);
		g.drawLine(av[3].sX,av[3].sY, av[7].sX,av[7].sY);
		g.drawLine(av[2].sX,av[2].sY, av[6].sX,av[6].sY);
	}
}

