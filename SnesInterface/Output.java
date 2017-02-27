package SnesInterface;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import java.lang.InterruptedException;
import java.lang.Thread;

public class Output {

    public static void main(String[] args) {
	SnesInterface ioface = new SnesInterface();
	/*robotの生成時、threadがアクティブ化（初回のみ？）
	  snes9xが停止するため要対策
	 */

	try {
	    Thread.sleep(5000);
	} catch(InterruptedException e ) {
	    throw new RuntimeException(e);
	}
	ioface.OneButton(SnesKeyCode.ROT_R);

	int[] thisPuyo = new int[2];
	int[] nextPuyo = ioface.getNextPuyo()[1];
	int[] nxnxPuyo = ioface.getNxnxPuyo()[1];

	int col[] = new int[2];
	int row[] = new int[2];

	col[0] = 5;
	col[1] = 5;
	row[0] = 1;
	row[1] = 0;

	for (int i = 0; i < 6; i++) {
	    while(true) {
		if ( ioface.waitPuyoGiven(1, thisPuyo, nextPuyo, nxnxPuyo) ) {
		    break;
		}
		try {
		    Thread.sleep(500);
		} catch(InterruptedException e ) {
		    throw new RuntimeException(e);
		}
	    }

	    System.out.println(thisPuyo[0] + ", " + thisPuyo[1]);
	    System.out.println(nextPuyo[0] + ", " + nextPuyo[1]);
	    System.out.println(nxnxPuyo[0] + ", " + nxnxPuyo[1]);

	    
	    col[1] = new Random().nextInt(6);
	    col[0] = col[1] + new Random().nextInt(3) - 1;
	    row[0] = 0;
	    row[1] = new Random().nextInt(2);
	    System.out.println("("+col[0]+","+col[1]+"), "+row[0]);
	    
	    ioface.putPuyo(col, row);

	    //おじゃま、消去
	}

    }


}


/*
class OutTask extends TimerTask {
    private SnesInterface ioface;
    public OutTask() {
	ioface = new SnesInterface();
    }

    public void run() {
	int col[] = new int[2];
	col[0] = new Random().nextInt(6);
	col[1] = col[0] + new Random().nextInt(3) - 1;
	ioface.putPuyo(col);
	
    }
}
*/

