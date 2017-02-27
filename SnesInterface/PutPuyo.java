
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import java.lang.InterruptedException;


public class PutPuyo {

    public static void main(String[] args) {



    }

    public static ControllPuyo(int[] col) {
	/* col[0]:軸ぷよ（下）
	   col[1]:子ぷよ（上） */
	int move = col[0] - 3;//軸ぷよを基準に横移動
	int rot = col[0] - col[1];
	if(move < 0) {
	    for (int i = move; i < 0; i++) {
		OneButton(SnesKeyCode.LEFT);
	    }
	} else if ( 0 < move ) {
	    for (int i  = 0; i < move; i++) {
		OneButton(SnesKeyCode.RIGHT);
	    }
	}

	if(rot < 0) {
	    OneButton(SnesKeyCode.ROT_L);
	} else if(0 < rot) {
	    OneButton(SnesKeyCode.ROT_R);
	}
	

    }

    public static OneButton(int key) {
	try {
	    Robot robot = new Robot();
	} catch(AWTException e) {
	    throw new RuntimeException(e);
	}
	try {
	    robot.keyPress(key);
	    robot.delay(50);
	    robot.keyRelease(key);
	    robot.delay(50);
	} catch(IllegalArgumentException e) {
	    System.out.println("invalid key code: " + key);
	    return;
	}
    }


}
