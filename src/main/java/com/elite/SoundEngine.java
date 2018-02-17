package com.elite;

import javax.sound.sampled.*;
import java.util.Vector;
import java.net.*;

public class SoundEngine 
{
  private String relativepath;
  public SoundEngine()
  {
  }

  public void setPath(String path)
  {
    relativepath = path;
  }
 private static Vector clips;
 
      public void init()
      {
        clips = new Vector(10);
    
        try
        {
          relativepath += "/snd/";
          loadSound(new URL(relativepath + "laser.wav"),clips,0);
          loadSound(new URL(relativepath + "explode.wav"),clips,1);
          loadSound(new URL(relativepath + "damage.wav"),clips,2);
        }
        catch (Exception e)
        {
      
        }
      }
      
    public static void playSound(int sound)
    {
      
     // ((Clip)clips.get(sound)).stop();
     if (!(((Clip)(clips.get(sound))).isRunning()))
     {
        ((Clip)clips.get(sound)).stop();
        ((Clip)clips.get(sound)).flush();
        ((Clip)clips.get(sound)).start();
        System.out.println("Fire");

     }

    }
    public boolean loadSound(Object object, Vector clips, int clipnum)
    {
        if (object instanceof URL) 
        {
          try
          {
                
                AudioInputStream audiostream = (AudioInputStream)AudioSystem.getAudioInputStream((URL) object);
                AudioFormat format = audiostream.getFormat();
                  DataLine.Info info = new DataLine.Info(
                  Clip.class, 
                  audiostream.getFormat(), 
                  ((int) audiostream.getFrameLength() *
                  format.getFrameSize()));
                Clip clip = (Clip)AudioSystem.getLine(info);
                clip.open(audiostream);
                clips.add(clipnum, clip);
                audiostream.close();               
          }
          catch (Exception e)
          {
             e.printStackTrace();
          }
        }
        return true;
    }
}