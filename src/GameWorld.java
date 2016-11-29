import java.awt.Color;
import java.util.Random;
import java.util.*;


//TODO: Add more pixelTypes. Update ELEMENT_COUNT

//Class responsible for managing game world objects
public class GameWorld
{
    //Map of all the pixels on-screen
    //Send this to user interface component to update user-display
    //2Darray indices represent (x,y) coordinates of each pixels
    ArrayList<Particle> existingParticles;
    final int PIXEL_MAP_HEIGHT = 800/4;
    final int PIXEL_MAP_WIDTH = 1240/4;
    Particle[][] pixelMap;//(0,0) coordinates are located at the top-left of the 2D array

    //General-use random variable
    Random randy = new Random();

    //Associates an element name with an array index.
    //Determines name of particle on pixelMap where
    //pixelMap's stored integer represents an index in the pixelType array
    final int ELEMENT_COUNT = 5;

    String[] pixelNameList = new String[ELEMENT_COUNT];

    //Boolean variables to keep track of mouse operations by user
    boolean mouseWasClicked;//Turns "true" when the visual component indicates the mouse was clicked
    int clickedX, clickedY;//Stores x,y coordinates of the last point the mouse was clicked
    boolean mouseWasDragged;//Turns "true" when the visual component indicates the mouse was dragged
    int draggedX1, draggedX2, draggedY1, draggedY2;//x,y coordinates of dragging mouse's the starting point and ending point

    //Variables to keep track of custom options
    String penElement;
    boolean penDragMode;
    boolean wallCollision;

    //Default constructor
    public GameWorld()
    {
        //Initializes pixelMap with "Nothing" in all indices
        pixelMap = new Particle[PIXEL_MAP_WIDTH  ][PIXEL_MAP_HEIGHT ];
        existingParticles = new ArrayList<Particle>();
        for(int iY = PIXEL_MAP_HEIGHT-1; iY >= 0; iY--){
            for(int iX = 0; iX < PIXEL_MAP_WIDTH; iX++){
                pixelMap[iX][iY] = new Particle("Nothing", Color.black, true, iX, iY);
            }
        }

        //Initializes pixelType with every element to be used in-game
        pixelNameList[0] = "Nothing";
        pixelNameList[1] = "Sand";//Completely unreactive non-static particle
        pixelNameList[2] = "Wall";//Completely unreactive static paticle
        pixelNameList[3] = "Water";//flowing particle that reacts with plant
        pixelNameList[4] = "Plant";//Seed that will drop then grow on contact to water

        //Initializes mouse variables
        mouseWasClicked = false;
        mouseWasDragged = false;

        //Initializes toggled option variables
        penElement = "Wall";
        penDragMode = false;
        wallCollision = false;
    }

    //Assesses the situation and determines the next course of action for the game
    //We must find a way to call this repeatedly during gamepay
    public void update(){

        //Updates each pixel on-screen, bottom to top, in horizontal layers

        for(int iY = PIXEL_MAP_HEIGHT-1; iY >= 0; iY--){
            for(int iX = 0; iX < PIXEL_MAP_WIDTH; iX++){

                updatePixel(pixelMap[iX][iY]);

                //Removes any pixels at the screen edge if wall-collision is off
                if(!wallCollision && (iX == 0 || iY == 0 || iX == PIXEL_MAP_WIDTH-1 || iY == PIXEL_MAP_HEIGHT-1))
                {
                    pixelMap[iX][iY] = new Particle(iX,iY);
                }
            }
        }
        /*for(Particle p : existingParticles)
        {
            updatePixel(p);
            if(!wallCollision && (p.x == 0 || p.y == 0 || p.x == PIXEL_MAP_WIDTH-1 || p.y == PIXEL_MAP_HEIGHT-1))
            {
                pixelMap[p.x][p.y] = new Particle(p.x,p.y);
            }
        }*/

        //Handles response to a mouse-click
        if(mouseWasClicked)
        {
            switch(penElement)
            {
                case "Nothing":
                    //If there's something in the target spot, replace it with nothing
                    if(!pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p = new Particle(clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                       // existingParticles.add(p);
                    }
                    break;
                case "Sand":
                    //If there's nothing in the target spot, replace it with sand
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Sand", Color.ORANGE, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Wall":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Wall", Color.darkGray, true, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Water":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Water", Color.blue, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Plant":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Plant", Color.green, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
            }
            mouseWasClicked = false;

        }
    }

    //Updates the pixel space with the given particle
    //only considers pixel spaces below the given particle
    public void updatePixel(Particle thisPixel){
        if(thisPixel.y + 1 >= 200)//NOTE: Is the 200 the same as PIXEL_MAP_HEIGHT?
            return;
        //Change nothing if there's nothing in the pixel space
        if(thisPixel.name.equals("Nothing"))
        {
            return;
        }
        //Change nothing if particle doesn't fall
        else if(thisPixel.isStatic)
        {
            return;
        }
        
        //List all fluid particles here
        if(thisPixel.name.equals("Water"))
        {
            //Moves particle 1 pixel downward if there's nothing in the pixel space below and the particle is not at the bottom of the screen
            if(pixelMap[thisPixel.x][thisPixel.y + 1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1){
                movePixelDown(thisPixel);
            }
            else{
                //Checks if the particle below can interact with thisParticle
                if(canParticleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
                else//Since there's an unreactive particle directly below, thisPixel searches for the nearest open gap 1 pixel below its own height
                {
                    int gapPosX = findNearestReachableGap(thisPixel);
                    if(gapPosX != -1 && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1)//If there is a gap, the pixel is moved to it
                    {
                        movePixelAndCheckInteraction(thisPixel, gapPosX, thisPixel.y+1);
                    }
                    else
                    {
                        //If there isn't a gap, the pixel stays still
                        
                    }
                }
            }
            interactionsAroundParticle(thisPixel);
        }
        else
        {
            //Moves particle 1 pixel downward if there's nothing in the pixel space below and the particle is not at the bottom of the screen
            if(pixelMap[thisPixel.x][thisPixel.y + 1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1){
                movePixelDown(thisPixel);
            }
            else{
                //Checks if the particle below can interact with thisParticle
                if(canParticleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
                //Since there's an unreactive particle directly below, thisPixel randomly decides to try to slide left or slide right
                else if(randy.nextInt(2) == 0)
                {//Checks right side first
                    if(pixelMap[thisPixel.x+1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x+1 < PIXEL_MAP_WIDTH-1)
                    {
                        movePixelDownRight(thisPixel);
                    }
                    //Checks if the particle below+right can interact with thisParticle
                    else if(canParticleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                    {
                        particleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                    }
                    //Checking left side
                    else if(pixelMap[thisPixel.x-1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x-1 > 0)
                    {
                        movePixelDownLeft(thisPixel);
                    }
                    //Checks if the particle below+left can interect with thisParticle
                    else if(canParticleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                    {
                        particleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                    }
                }
                else
                {//Checks left side first
                    if(pixelMap[thisPixel.x-1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x-1 > 0)
                    {
                        movePixelDownLeft(thisPixel);
                    }
                    //Checks if the particle below+left can interect with thisParticle
                    else if(canParticleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                    {
                        particleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                    }
                    //Checking right side
                    else if(pixelMap[thisPixel.x+1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x+1 < PIXEL_MAP_WIDTH-1)
                    {
                        movePixelDownRight(thisPixel);
                    }
                    //Checks if the particle below+right can interect with thisParticle
                    else if(canParticleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                    {
                        particleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                    }
                }

                //If there's no empty space and no reactive elements underneath, no changes should be made to the particle in-question
            }
            interactionsAroundParticle(thisPixel);
        }


    }
    
    //Looks for the nearest reachable empty space a layer below 
    //Returns the "x" coordinate of the nearest gap.
    //Returns -1 if no gap was found
    public int findNearestReachableGap(Particle thisPixel)
    {
        boolean leftIsBlocked = false;
        boolean rightIsBlocked = false;
        int x = thisPixel.x;
        int y = thisPixel.y;
        for(int lookDistance = 1; lookDistance <= PIXEL_MAP_WIDTH/2; lookDistance++)
        {
            //Randomizes whether left or right be checked first
            if(randy.nextInt(2) == 0)
            {
                //Checks for any blockages
                if(x+lookDistance < PIXEL_MAP_WIDTH-1)
                {
                    if(!pixelMap[x+lookDistance][y].name.equals("Nothing"))
                    {
                        rightIsBlocked = true;
                    }
                }
                if(x-lookDistance > 0)
                {
                    if(!pixelMap[x-lookDistance][y].name.equals("Nothing"))
                    {
                        leftIsBlocked = true;
                    }
                }
                if(x+lookDistance < PIXEL_MAP_WIDTH-1)
                {
                    //Checks right side
                    if(pixelMap[x+lookDistance][y+1].name.equals("Nothing") && !rightIsBlocked)
                    {
                        return x+lookDistance;
                    }
                }
                if(x-lookDistance > 0)
                {
                    //Checks left side
                    if(pixelMap[x-lookDistance][y+1].name.equals("Nothing") && !leftIsBlocked)
                    {
                        return x-lookDistance;
                    }
                }
            }
            else
            {
                //Checks for any blockages
                if(x+lookDistance < PIXEL_MAP_WIDTH-1)
                {
                    if(!pixelMap[x+lookDistance][y].name.equals("Nothing"))
                    {
                        rightIsBlocked = true;
                    }
                }
                if(x-lookDistance > 0)
                {
                    if(!pixelMap[x-lookDistance][y].name.equals("Nothing"))
                    {
                        leftIsBlocked = true;
                    }
                }
                if(x-lookDistance > 0)
                {
                    //Checks left side
                    if(pixelMap[x-lookDistance][y+1].name.equals("Nothing") && !leftIsBlocked)
                    {
                        return x-lookDistance;
                    }
                }
                if(x+lookDistance < PIXEL_MAP_WIDTH-1)
                {
                    //Checks right side
                    if(pixelMap[x+lookDistance][y+1].name.equals("Nothing") && !rightIsBlocked)
                    {
                        return x+lookDistance;
                    }
                }
            }
        }
        return -1;
    }
    
    //Moves pixel to designated coordiantes. Overwrites whatever is in the destination
    public void movePixel(Particle thisPixel, int targetX, int targetY)
    {
        int x = thisPixel.x;
        int y = thisPixel.y;

        thisPixel.x = targetX;
        thisPixel.y = targetY;
        pixelMap[targetX][targetY] = thisPixel;
        pixelMap[x][y] = new Particle(x,y);

    }
    
    //Moves pixel to designated coordiantes. Attempts to interact with pixel of the destination. 
    //Overwrites whatever is in the destination if no interaction occurs
    public void movePixelAndCheckInteraction(Particle thisPixel, int targetX, int targetY)
    {
        int x = thisPixel.x;
        int y = thisPixel.y;
        if(canParticleInteract(pixelMap[targetX][targetY], pixelMap[x][y]))
        {
            particleInteract(pixelMap[targetX][targetY], pixelMap[x][y]);
        }
        else
        {
            thisPixel.x = targetX;
            thisPixel.y = targetY;
            pixelMap[targetX][targetY] = thisPixel;
            pixelMap[x][y] = new Particle(x,y);
        }
    }
    
    public void movePixelDown(Particle thisPixel){
        int x = thisPixel.x;
        int y = thisPixel.y;
        thisPixel.y+=1;
        pixelMap[x][y+1] = thisPixel;
        pixelMap[x][y] = new Particle(x,y);
    }
    
    public void movePixelDownLeft(Particle thisPixel){
        int x = thisPixel.x;
        int y = thisPixel.y;
        thisPixel.y+=1;
        thisPixel.x-=1;
        pixelMap[x-1][y+1] = thisPixel;
        pixelMap[x][y] = new Particle(x,y);
    }
    
    public void movePixelDownRight(Particle thisPixel){
        int x = thisPixel.x;
        int y = thisPixel.y;
        thisPixel.y+=1;
        thisPixel.x+=1;
        pixelMap[x+1][y+1] = thisPixel;
        pixelMap[x][y] = new Particle(x,y);
    }
    
    //Updates pixelMap after 2 particles interact with each other
    //Interaction occurs as if the targetParticle was applied the appliedParticle
    public void particleInteract(Particle targetParticle, Particle appliedParticle)
    {

    }

    //Returns true if particleA and particleB can interact with each other
    public boolean canParticleInteract(Particle particleA, Particle particleB)
    {
        if((particleA.name == "Plant" && particleB.name == "Water") || (particleA.name == "Water" && particleB.name == "1"))
        {
            return true;
        }

        return false;
    }
    
    //NOTE: lmk if you notice an outOfBoundException when water reaches the bottom of the screen
    public void interactionsAroundParticle(Particle p)
    {
        if(p.name == "Water" )
        {
            int posx = p.x;
            int posy = p.y;

            if(pixelMap[p.x + 1][p.y].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);

            }
            if(pixelMap[p.x + 1][p.y + 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x ][p.y + 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x - 1][p.y].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x - 1][p.y - 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x ][p.y - 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x + 1][p.y - 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);
            }
            if(pixelMap[p.x - 1][p.y + 1].name == "Plant")
            {
                Particle newP =  new Particle("Plant", Color.green, false, posx, posy);
                pixelMap[posx][posy] = newP;
                existingParticles.remove(p);
                existingParticles.add(newP);

            }

        }
    }

    //Called by Display when the mouse is clicked at a specific location on the pixel map
    //parameters accept the pixelMap coordinate at which the mouse was clicked
    public void mouseClicked(int x, int y){
        clickedX = x;
        clickedY = y;
        mouseWasClicked = true;
        update();
    }

    //Called by Display when the mouse was dragged at a specific location on the pixel map
    //parameters accept the pixelMap coordinate at which the mouse started dragging and stopped dragging
    public void mouseDragged(int x1, int y1, int x2, int y2){
        draggedX1 = x1;
        draggedY1 = y1;
        draggedX2 = x2;
        draggedY2 = y2;
        mouseWasDragged = true;
    }

    public void changePenType(String type)
    {
        penElement = type;
    }

    public void test()
    {

    }

}
