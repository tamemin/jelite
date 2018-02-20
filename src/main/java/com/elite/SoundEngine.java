package com.elite;

import javax.sound.sampled.*;
import java.util.Vector;
import java.io.InputStream;
import java.net.*;

public class SoundEngine {


 private static Vector clips;
 
      public void init() {
        clips = new Vector(10);
    
        try {
          loadSound("/snd/laser.wav",clips,0);
          loadSound("/snd/explode.wav",clips,1);
          loadSound("/snd/damage.wav",clips,2);
        }  catch (Exception e) {
      
        }
      }
      
    public static void playSound(int sound) {
      
     if (!(((Clip)(clips.get(sound))).isRunning())) {
        ((Clip)clips.get(sound)).stop();
        ((Clip)clips.get(sound)).flush();
        ((Clip)clips.get(sound)).start();
        System.out.println("Fire");

     }

    }
    
    public boolean loadSound(String soundName, Vector clips, int clipnum) {
          try(InputStream is = this.getClass().getResourceAsStream(soundName);) {
                
                AudioInputStream audiostream = (AudioInputStream)AudioSystem.getAudioInputStream(is);
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
        return true;
    }
}