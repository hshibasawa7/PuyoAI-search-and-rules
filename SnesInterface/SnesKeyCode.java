package SnesInterface;

import java.awt.event.KeyEvent;

public class SnesKeyCode {
    
//snes9x default 1P key
    public int UP = KeyEvent.VK_UP;
    public int DOWN = KeyEvent.VK_DOWN;
    public int LEFT = KeyEvent.VK_LEFT;
    public int RIGHT = KeyEvent.VK_RIGHT;
    public int ROT_L = KeyEvent.VK_ALT;
    public int ROT_R = KeyEvent.VK_META;
    public int START = KeyEvent.VK_ENTER;

    //snes9x default 2P key
    /*
    public static final int UP = KeyEvent.VK_NUMPAD8;
    public static final int DOWN = KeyEvent.VK_NUMPAD2;
    public static final int LEFT = KeyEvent.VK_NUMPAD4;
    public static final int RIGHT = KeyEvent.VK_NUMPAD6;
    public static final int ROT_L = KeyEvent.VK_PAGE_UP;
    public static final int ROT_R = KeyEvent.VK_HOME;
    public static final int START = KeyEvent.VK_COLON;
    */

    SnesKeyCode(int player) {
	if (player == 0) { //1P
	    UP = KeyEvent.VK_UP;
	    DOWN = KeyEvent.VK_DOWN;
	    LEFT = KeyEvent.VK_LEFT;
	    RIGHT = KeyEvent.VK_RIGHT;
	    ROT_L = KeyEvent.VK_ALT;
	    ROT_R = KeyEvent.VK_META;
	    START = KeyEvent.VK_ENTER;
	} else if (player == 1) { //2P
	    UP = KeyEvent.VK_NUMPAD8;
	    DOWN = KeyEvent.VK_NUMPAD2;
	    LEFT = KeyEvent.VK_NUMPAD4;
	    RIGHT = KeyEvent.VK_NUMPAD6;
	    ROT_L = KeyEvent.VK_PAGE_UP;
	    ROT_R = KeyEvent.VK_HOME;
	    START = KeyEvent.VK_COLON;
	}
    }



}

