import java.awt.*;
import java.util.Stack;

public class glovar {
    public static Font titleFont=new Font("SF Pro Display",Font.BOLD,48);
    public static Font normalFont = new Font("SF Pro Display",Font.PLAIN,32);
    public static boolean optionsActive=false;
    public static class coord {
        public int x,y;
    }
    public static Stack<coord> chorded;
    public static Stack<coord> uncovr;
    public static int szx,szy,minecnt;
    public static boolean shiftDown,chord;
    public static boolean virgin;
    public static int heicap=22,widcap=44;
}
