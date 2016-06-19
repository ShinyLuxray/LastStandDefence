import java.lang.Math;
public class Tower
{
  int id; 
  int period; 
  int damage;
  int range;
  int upperiod = 0,updamage = 0,uprange = 0;
  final Position pos; 
  public Tower (int initializeid, Position p)
  {
    this.pos = p; 
    this.id = initializeid;
    
    System.out.println("This tower has ID: " + id);
    
    //hardcode shit
    //if (id == 0)
    //{
    // damage = ...; ...    
    //}
    
    if(id == 1) // The cannon; slow-firing with low range, but relatively nice damage
    {
      period = 1000;
      damage = 20;
      range = 5;
    }
    
    else if(id == 2) // The laser; this piece of shit doesn't really have any redeeming qualities
    {
      period = 750;
      damage = 10;
      range = 7;
    }
    
    else if(id == 3) // The rocket; this thing is OP as fuck because of its long range
    {
      period = 1000;
      damage = 15;
      range = 15;
    }
    
    else if(id == 4) // The taser; basically just RoF
    {
      period = 200;
      damage = 10;
      range = 3;
    }
    
    Thread atkseq = new Thread(new Runnable()
                                 {
      public void run()
      {
        //System.out.println("Well, we're in the thread...");
        int atkindex = 0; 
        int maxPathID; 
        
        while (true)
        {
          //System.out.println("true is always true...");
          System.out.println("LSD.waveDone: " + LSD.waveDone);
          while (!LSD.waveDone)
          {
            System.out.println("so I guess the wave's not done..?");
            while(LSD.wave.size() > 0)
            {
              System.out.println("And the size is greater than zero..?");
              atkindex = 0; 
              maxPathID = 0;
              
              for (int i = 0; i < LSD.wave.size(); i++)
              {
                if (LSD.wave.get(i).inRange(Tower.this))
                {
                  if (LSD.wave.get(i).getPathId() > maxPathID)
                  {
                    maxPathID = LSD.wave.get(i).getPathId(); 
                    atkindex = i; 
                    System.out.println ("Checking"); 
                  }
                }
              }
              
              if(LSD.wave.get(atkindex).hit(damage))
              {
                LSD.wave.remove(atkindex); 
                LSD.kills++; 
                LSD.waveDone = true;
              }
              System.out.println ("Attacking"); 
              try
              {
                Thread.sleep (period); //period is specified in milliseconds
                System.out.println ("Waiting"); 
              }
              catch(InterruptedException e)
              {}
              
            }
          }
        }
      }
    });
    
    atkseq.start();
    
  }
  
  public Position getPos()
  {
    return pos;
  }
  
  public int getRange()
  {
    return range;
  }
  
  public int getPower()
  {
    return damage;
  }
  
  public int getPeriod()
  {
    return period;
  }
  
  public int getType()
  {
    return id;
  }
  
  public int getValue()
  {
    int moneyz = 100+id*100,price = 100;
    
    for(int a = 0;a<5;a++)
    {
      price*=2;
      
      if(uprange>a)
        moneyz += price;
      
      if(updamage>a)
        moneyz += price;
      
      if(upperiod>a)
        moneyz += price;
    }
    
    return moneyz;
  }
  
  public boolean upgrade (String statToUpgrade)
  {
    if(statToUpgrade.toLowerCase().equals("period")||statToUpgrade.toLowerCase().equals("rate"))
    {
      if(LSD.money<(int)Math.pow(2,upperiod+1))
        return false;
      LSD.money-=(int)Math.pow(2,upperiod+1);
      upperiod++;
      period=(period*3)/4;
      return true;
    }
    else if (statToUpgrade.toLowerCase().equals("range"))
    {
      if(LSD.money<(int)Math.pow(2,uprange+1))
        return false;
      LSD.money-=(int)Math.pow(2,uprange+1);
      uprange++;
      range++;
      return true;
    }
    else if(statToUpgrade.toLowerCase().equals("damage"))
    {
      if(LSD.money<(int)Math.pow(2,updamage+1))
        return false;
      LSD.money-=(int)Math.pow(2,updamage+1);
      updamage++;
      damage++;
      return true;
    }
    else
      return false;
  }
}