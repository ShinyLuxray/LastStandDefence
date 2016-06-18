import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class LSD
{
  protected static int money = 2000;
  protected static int score = 0;
  static GameBoard aBoard;
  static ArrayList<Tower> towers = new ArrayList(0);
  static ArrayList<Position> path = new ArrayList(0);
  static ArrayList<Enemy> wave;
  
  // Welcome to Kai's clump of boolean controls
  protected static boolean nextWaveClicked = false;
  
  
  public static void main(String args[])
  {
    
    System.out.println("LSD v.0.3 Codename 'Bubba'\npress return to add, enter any string then return to remove.");
    
    try
    {
      ArrayList<String> pathtemp = parse(parse(new String(Files.readAllBytes(Paths.get("paths.txt"))),'\n').get(0),'#');
      //ArrayList<String> pricetemp = parse(parse(new String(Files.readAllBytes(Paths.get("properties.txt"))),'\n').get(0),'#');
      for(int a = 0;a<pathtemp.size();a++)
      {
        path.add(new Position(pathtemp.get(a)));
      }
      
    }
    catch(IOException e)
    {
      System.out.println("Could not read file.");
    }
    
    aBoard = new GameBoard(path);
    aBoard.setVisible(true);
    
    Scanner little = new Scanner(System.in);
    
    while(true)
    {
      
      while(!nextWaveClicked){ System.out.print(""); }
      
      if(nextWaveClicked)
      {
        nextWaveClicked = false;
        newWave();
      }
      
    }
    
    /*
    while(true)
    {
      String inputs = little.nextLine();
      System.out.println("current position: "+aBoard.select);
      if(inputs .isEmpty())
        addTower(aBoard.select,0);
      else
        removeTower(aBoard.select);
    }
    */
    
  }
  
  public static void newWave()
  {
    
    wave = new ArrayList(4);
    
    for(int i = 0; i < 4; i++)
    {
      wave.add(new Enemy(20));
    }
    
    for(int i = 0; i < wave.size(); i++)
    {
      System.out.println(wave.get(i));
    }
    
    aBoard.spawnWave();
    
  }
  
  public static boolean canBuild(Position place)
  {
    for(int a = 0;a<path.size();a++)
    {
      if(path.get(a).distance(place)==0)//if place is on the path
      {
        System.out.println(place+" is part of the path. Can't build.");
        return false; //no building allowed
      }
    }
    
    for(int a = 0;a<towers.size();a++)
    {
      //System.out.println("Tower "+a+": "+towers.get(a).getPos()+towers.get(a).getPos().distance(place));
      if(towers.get(a).getPos().distance(place)==0)//tower is at that position
      {
        System.out.println("There is a tower at "+place+" Can't build.");
        return false; //no building allowed
      }
    }
    
    return true;
  }
  
  public static void addTower(Position place, int type)//a flimsy addTower class, makes a default Tower()
  {
    if(canBuild(place))
    {
      if(money>200)//change this to a proper requirement
      {
        towers.add(new Tower(type,new Position(place)));
        money-=200; //subtract funds
        Tower toAdd = new Tower(type,new Position(place));
        aBoard.addTower(toAdd);
        System.out.println("Added tower at "+place);
        //then update graphics as necessary
      }
      else
      {
        System.out.println("Insufficient funds.");
      }
    }
  }
  
  public static void removeTower(Position place)
  {
    for(int a = 0;a<=towers.size();a++)
    {
      if(a==towers.size())
      {
        System.out.println("There is no tower there.");
        break;
      }
      if(towers.get(a).getPos().distance(place)==0)
      {                
        Tower toRemove = new Tower(towers.get(a).getType(), new Position(place));
        towers.remove(a);
        aBoard.removeTower(toRemove);
        System.out.println("Removed tower "+a+" from "+towers.get(a).getPos());
        break;
        //then update graphics as necessary
      }
    }
  }
  
  public static ArrayList<String> parse(String parseThis,char delimiter)
  {
    int width = 0,elements = 0,temp = 0;
    
    for(int a = 0; a < parseThis.length();a++)//check the first row to see what the dimension should be
    {
      if(parseThis.charAt(a) == ',')
      {
        width++;
      }
    }
    
    ArrayList<String> toR = new ArrayList(0);
    
    if(width==0)
    {
      toR.add(parseThis);
      return toR;
    }
    
    for(int b = 0;elements<width;b++)
    {
      if(elements>=width-1)//if last element copy the rest
      {
        toR.add(parseThis.substring(temp));
        break;
      }
      
      if(b>=parseThis.length())//if we are at the end of the line before all the elements filled
      {
        while(elements<width)
        {
          toR.add("");//just fill it with blank then
          elements++;
        }
        
        break;
      }
      
      if(parseThis.charAt(b)==delimiter)//delimiter is the comma
      {
        toR.add(parseThis.substring(temp,b));//set the former stuff to the element
        temp = b+1;//shorten the string we have to work with
        elements++;//go to next element
      }
    }
    
    return toR;
  }

static class GameBoard extends JFrame
{
  
  protected MiscImage wavesImage =           new MiscImage(40 , 160, 500, 70, "resources/backings/waves.png");
  protected MiscImage killsImage =           new MiscImage(240, 160, 500, 70, "resources/backings/kills.png");
  protected MiscImage nextWaveImage =        new MiscImage(680, 160, 500, 70, "resources/backings/nextWave.png");
  protected MiscImage pauseImage =           new MiscImage(880, 160, 500, 70, "resources/backings/pause.png");
  protected MiscImage backgroundImage =      new MiscImage(0 , 1080, 0 , 600, "resources/overall/background.png");
  protected MiscImage boardBackgroundImage = new MiscImage(40, 1000, 40, 440, "resources/overall/boardBackground.png"); // This is actually not useless, it's used for a bit of checking, will clean up soon(tm)
  
  protected MiscImage baseImage =            new MiscImage(840, 160, 265, 70, "resources/entities/base.png"); // These coordinates can be changed depending on how we want to set up the map
  
  protected MiscImage currentTurretImage =   new MiscImage(524, 40, 518, 40, "resources/turrets/Gun_A_Idle.png"); // I'll deal with this one later
  protected int currentTurretID = 1; // Turret ID Number, between 1 and 4 to represent the four different types of turrets
  
  protected MiscImage buttonsImage =         new MiscImage(478, 125, 520, 30, "resources/controls/arrows.png"); // I'll deal with this one later
  
  // protected MiscImage path =                 new MiscImage(40, 880, 80, 360, "resources/entities/path.png"); Time to remove this and legitimatelly generate a path
  
  protected ArrayList<MiscImage> pathImages = new ArrayList<MiscImage>(); // Path will be an arrayList of position objects, I'd presume..?
  protected ArrayList<MiscImage> turrets = new ArrayList<MiscImage>();
  protected ArrayList<MiscImage> enemies = new ArrayList<MiscImage>();
  
  protected DrawArea main; // This is the main area where all of our components will be drawn onto.
  
  protected Position select = new Position (0,0); //if you want to access this in a static method, use Gameboard.aboard.select
  
  // Okay, for now, I'm defining each square to be 40px by 40px
  
  public GameBoard(ArrayList<Position> aPath)
  {
    
    msListen mouse = new msListen();
    
    main = new DrawArea(1080, 630);
    
    main.setLayout(null);
    main.addMouseListener(mouse);
    main.addMouseMotionListener(mouse);
    
    for(int i = 0; i < aPath.size(); i++)
    {
      int squareX = (aPath.get(i).getX() * 40) + 40;
      int squareY = (aPath.get(i).getY() * 40) + 40;
      pathImages.add(new MiscImage(squareX, 40, squareY, 40, "resources/entities/path.png"));
    }
    
    //spawnEnemies spawner = new spawnEnemies();
    //spawner.start();
    
    main.add(backgroundImage);
    main.add(wavesImage);
    main.add(killsImage);
    main.add(nextWaveImage);
    main.add(pauseImage);
    main.add(baseImage);
    main.add(buttonsImage);
    main.add(currentTurretImage);
    
    main.add(turrets);
    main.add(pathImages);
    main.add(enemies);
    
    setContentPane(main);                                // set the content pane to be whatever content pane contains all the others
    pack ();                                             // this is apparently required
    setTitle ("GUI Testing");                            // set the title of the window
    setSize (1080, 630);                                 // set the size of the window (in pixels)
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);     // set the close operation (just use EXIT_ON_CLOSE, we're not one of those dicks who minimizes windows when the user hits close)
    setLocationRelativeTo (null);                        // Center window.
    
  }
  
  public void revert()
  {
    main.revert();
    
    main.add(backgroundImage);
    main.add(wavesImage);
    main.add(killsImage);
    main.add(nextWaveImage);
    main.add(pauseImage);
    main.add(baseImage);
    main.add(buttonsImage);
    main.add(currentTurretImage);
    
    main.add(turrets);
    main.add(pathImages);
    main.add(enemies);    
  }
  
  public void addTower(Tower toAdd)
  {
    int towerX = toAdd.getPos().getX();
    int towerY = toAdd.getPos().getY();
    System.out.println("Tower is located at: (" + towerX + ", " + towerY + ")");
    
    int towerDrawX = (towerX * 40) + 40;
    int towerDrawY = (towerY * 40) + 40;
    
    turrets.add(new MiscImage(towerDrawX, 40, towerDrawY, 40, currentTurretImage.getImg()));
    
    revert();
    main.add(turrets);
    repaint();
    
  }
  
  public void removeTower(Tower toRemove)
  {
    int towerX = toRemove.getPos().getX();
    int towerY = toRemove.getPos().getY();
    System.out.println("Removing tower at: (" + towerX + ", " + towerY + ")");
    int towerDrawX = (towerX * 40) + 40;
    int towerDrawY = (towerY * 40) + 40;
    
    for(int i = 0; i < turrets.size(); i++)
    {
      if(turrets.get(i).getX() == towerDrawX && turrets.get(i).getY() == towerDrawY)
      {
        turrets.remove(i);
      }
    }
    
    revert();
    repaint();
    
  }
  
  public void spawnWave()
  {
    spawnEnemies spawner = new spawnEnemies(wave); // Creates a new spawnEnemies thread that will spawn one wave
    spawner.start();                                // Starts the spawner thread; spawner will run until it's finished spawning and moving that wave
  }
  
  class spawnEnemies extends Thread
  {
    
    ArrayList<Enemy> enems = new ArrayList<Enemy>(); // The list of enemies
    
    public spawnEnemies(ArrayList<Enemy> enems)      // Constructor, takes in a list of enemies
    {
      this.enems = enems;
    }
    
    public void run() // The thread that will be run
    {
      
      int leftToSpawn = wave.size(); // The number of enemies that still have to be spawned
      int leftInWave  = wave.size(); // The number of enemies that are still on the wave
      
      int progress = 0; // The number of times the thread will have executed; allows me to determine spawn start location
      
      int startSpawningAt = 0;
      
      while(leftInWave > 0)
      {
        
        if(leftToSpawn > 0)
        {
          //System.out.println("Spawned enemy index: " + (wave.size() - leftToSpawn));
          enemies.add(new MiscImage(wave.get(wave.size() - leftToSpawn).getPos(), "resources/enemies/default.png"));
          leftToSpawn --;
        }
        
        progress++;
        
        if(((progress-1)) >= path.size())
        {
          //System.out.println("???");
          startSpawningAt = path.size() - 1;
          leftInWave --;
          enemies.remove(0);
          
        }
        
        else
        {
          
          for(int i = 0; i < progress; i++)
          {
            startSpawningAt = (progress - 1);
          }
        
        }
        
        //System.out.println("Objects will start to be spawned at: " + startSpawningAt);
        
        for(int i = 0; i < leftInWave-leftToSpawn; i++)
        {
          wave.get(i).setPos(new Position(path.get(startSpawningAt - (i))));
          enemies.get(i).setPos(wave.get(i).getPos());
          revert();
          repaint();
          //System.out.println("Position for enemy " + (i+1) + " is: " + wave.get(i).getPos());
        }
        
        try
        {
          Thread.sleep(500);
        }
        catch(InterruptedException e)
        {
        }
          
        
      }
      
      //System.out.println("We're done spawning.");
      revert();
      repaint();
      
    }
    
  }
  
  class msListen implements MouseListener, MouseMotionListener
  {
    
    public void mouseEntered  (MouseEvent e){}
    
    public void mouseExited   (MouseEvent e){}
    public void mousePressed  (MouseEvent e){}
    public void mouseReleased (MouseEvent e){}
    public void mouseDragged  (MouseEvent e){}
    
    public void mouseClicked(MouseEvent e)
    {
      if(boardBackgroundImage.checkBounds(e.getX(), e.getY()))
      {
        
        int calcX = e.getX() - 40;
        int calcY = e.getY() - 40;
        
        select.setX(calcX / 40);
        select.setY(calcY / 40);
        System.out.println("Selected coordinates: "+select);
        
      }
      
      System.out.println("(MouseClick at" + e.getX() + ", " + e.getY() + ")");
      
      // For the control panel
      
      if(e.getX() < 508 && e.getX() > 478 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/left click.png");
        
        currentTurretID--;
        if(currentTurretID == 0)
        {
          currentTurretID = 4;
        }
        
      }
      else if(e.getX() > 575 && e.getX() < 603 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/right click.png");
        
        currentTurretID++;
        if(currentTurretID == 5)
        {
          currentTurretID = 1;
        }
        
      }
      
      else
      {
        buttonsImage.setImg("resources/controls/arrows.png");
      }
      
      if(currentTurretID == 1)
      {
        currentTurretImage.setImg("resources/turrets/Gun_A_Idle.png");
      }
      else if(currentTurretID == 2)
      {
        currentTurretImage.setImg("resources/turrets/Laser_A_Idle.png");
      }
      else if(currentTurretID == 3)
      {
        currentTurretImage.setImg("resources/turrets/Rocket_A.png");
      }
      else if(currentTurretID == 4)
      {
        currentTurretImage.setImg("resources/turrets/Taser_A_Idle_1.png");
      }
      
      // Next Wave Button
      
      if(nextWaveImage.checkBounds(e.getX(), e.getY()))
      {
        nextWaveImage.setImg("resources/backings/nextWaveClicked.png");
        nextWaveClicked = true;
      }
      else
      {
        nextWaveImage.setImg("resources/backings/nextWave.png");
      }
      
      // Pause Button
      
      if(pauseImage.checkBounds(e.getX(), e.getY()))
      {
        pauseImage.setImg("resources/backings/pauseClicked.png");
      }
      else
      {
        pauseImage.setImg("resources/backings/pause.png");
      }
      
      // Kills Button
      
      if(killsImage.checkBounds(e.getX(), e.getY()))
      {
        killsImage.setImg("resources/backings/killsClicked.png");
      }
      else
      {
        killsImage.setImg("resources/backings/kills.png");
      }
      
      // Waves Button
      
      if(wavesImage.checkBounds(e.getX(), e.getY()))
      {
        wavesImage.setImg("resources/backings/wavesClicked.png");
      }
      
      else
      {
        wavesImage.setImg("resources/backings/waves.png");
      }
      
      repaint();
      
    }
    
    public void mouseMoved(MouseEvent e)
    {
      
      // Next Wave Button
      
      if(nextWaveImage.checkBounds(e.getX(), e.getY()))
      {
        nextWaveImage.setImg("resources/backings/nextWaveSelected.png");
      }
      else
      {
        nextWaveImage.setImg("resources/backings/nextWave.png");
      }
      
      // Pause Button
      
      if(pauseImage.checkBounds(e.getX(), e.getY()))
      {
        pauseImage.setImg("resources/backings/pauseSelected.png");
      }
      else
      {
        pauseImage.setImg("resources/backings/pause.png");
      }
      
      // Kills Button
      
      if(killsImage.checkBounds(e.getX(), e.getY()))
      {
        killsImage.setImg("resources/backings/killsSelected.png");
      }
      else
      {
        killsImage.setImg("resources/backings/kills.png");
      }
      
      // Waves Button
      
      if(wavesImage.checkBounds(e.getX(), e.getY()))
      {
        wavesImage.setImg("resources/backings/wavesSelected.png");
      }
      
      else
      {
        wavesImage.setImg("resources/backings/waves.png");
      }
      
      repaint();
      
      if(e.getX() < 508 && e.getX() > 478 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/left hover.png");
      }
      else if(e.getX() > 575 && e.getX() < 603 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/right hover.png");
      }
      else
      {
        buttonsImage.setImg("resources/controls/arrows.png");
      }
      
      repaint();
      
    }
    
  }
  
}

  
}
