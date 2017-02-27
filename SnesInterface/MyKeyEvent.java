import java.awt.event.KeyEvent;
import java.awt.event.*;

public class MyKeyEvent implements KeyListener {
    public void keyPressed(KeyEvent e){
	int keycode = e.getKeyCode();
	System.out.println(keycode);
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
