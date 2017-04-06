package SnesInterface;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.Timer;
import java.util.TimerTask;

/*
todo: create FieldData Super Class.

instance:
MAX_WIDTH
MAX_HEIGHT
COLOR


*/

public class ScanField {
    //public int[][][] field;

    public static void main(String[] args) {
        SnesInterface ioface = new SnesInterface();	
        ScanTask task = new ScanTask(ioface);
	Timer timer = new Timer();
	timer.schedule(task, 0, 500);
	
    }

    public static void printField(int[][][] data) {
	int MAX_HEIGHT = 13;
	int MAX_WIDTH = 6;

	System.out.println();
	for (int i = MAX_HEIGHT - 1; i >= 0 ; i-- ) {
	    for (int j = 0; j < MAX_WIDTH; j++) {
		if (data[0][i][j] < 0) {
		    System.out.print(" -");
		} else {
		    System.out.print(" " + data[0][i][j]);
		}
	    }
	    System.out.print("       ");
	    for (int j = 0; j < MAX_WIDTH; j++) {
		if (data[1][i][j] < 0) {
		    System.out.print(" -");
		} else {
		    System.out.print(" " + data[1][i][j]);
		}
	    }
	    System.out.println();
	}
    }
}


class ScanTask extends TimerTask {
    private SnesInterface ioface;

    public ScanTask(SnesInterface ioface) {
	this.ioface = ioface;
    }

    public void run() {
	int[][][] field = ioface.getFieldPuyo();
	ScanField.printField(field);
    }
}

