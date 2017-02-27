
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import java.lang.InterruptedException;
import java.lang.Thread;

public class RandomOutput {

    public static void main(String[] args) {
	SnesInterface ioface = new SnesInterface();
	/*robotの生成時、threadがアクティブ化（初回のみ？）
	  snes9xが停止するため要対策
	 */

	int rand;
	while(true) {
	    switch( new Random().nextInt(6) ) {
	    case 0:
		ioface.OneButton(SnesKeyCode.ROT_R);
		break;
	    case 1:
		ioface.OneButton(SnesKeyCode.ROT_L);
		break;
	    case 2:
		ioface.OneButton(SnesKeyCode.UP);
		break;
	    case 3:
		ioface.OneButton(SnesKeyCode.DOWN);
		break;
	    case 4:
		ioface.OneButton(SnesKeyCode.LEFT);
		break;
	    case 5:
		ioface.OneButton(SnesKeyCode.RIGHT);
		break;
	    }
	    try {
		Thread.sleep(350);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	}

    }


}




