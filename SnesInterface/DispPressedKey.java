import java.awt.*;
import javax.swing.*;

import java.awt.event.KeyEvent;

public class DispPressedKey {
    public static void main(String[] args) {
	DispPressedKey dk = new DispPressedKey();
    }
    public DispPressedKey() {
	System.out.println(KeyEvent.VK_UP);
	System.out.println(KeyEvent.VK_LEFT);
	System.out.println(KeyEvent.VK_RIGHT);
	System.out.println(KeyEvent.VK_DOWN);

	JFrame frame = new JFrame();
	frame.setSize(250,250);
	frame.setTitle("Key_test2");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
	frame.addKeyListener(new MyKeyEvent());
    }

}
