//Particle Class
//Used as a structure object to store a sets of data for each in-game particle
public class Particle 
{
    public String name;
    public String color;
    
    //Pixel map coordinates
    public int x;
    public int y;
    
    //Determines whether the particle falls due to gravity (not static)
    //or whether the particle stays still (static)
    public boolean isStatic;
    
    public Particle()
    {
        name = "Nothing";
        color = "Black";
        x = 0;
        y = 0;
        isStatic = false;
    }
    
    public Particle(int xPos, int yPos)
    {
        name = "Nothing";
        color = "Black";
        x = xPos;
        y = yPos;
        isStatic = false;
    }
    
    public Particle(String particleName, String particleColor, boolean isItStatic)
    {
        name = particleName;
        color = particleColor;
        x = 0;
        y = 0;
        isStatic = isItStatic;
    }
    
    public Particle(String particleName, String particleColor, boolean isItStatic, int xPos, int yPos)
    {
        name = particleName;
        color = particleColor;
        x = xPos;
        y = yPos;
        isStatic = isItStatic;
    }
    
    
}
