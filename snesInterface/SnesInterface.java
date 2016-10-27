import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.Jtable;

public class SnesInterface {
    public static final int MAX_WIDTH = 6;
    public static final int MAX_HEIGHT = 13;
    private CaptureScreen captureScreen;

    public SnesInterface() {
	captureScreen = new CaptureScreen(FieldCoordinates.screen_w, FieldCoordinates.screen_h);

    }
    public SnesInterface(CaptureScreen cs) {
	this.captureScreen = cs;
    }

    public static void main(String[] args ) {
	int[][][] data;
	if(args.length > 0) {
	    BufferedImage image;
	    String filename = args[0];
	    File f = new File("./", filename);
	    try {
		image = ImageIO.read(f);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	    data = getFieldPuyo(image);
	} else {
	    SnesInterface ioface = new SnesInterface();
	    data = ioface.getFieldPuyo();
	}

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
	System.out.println();



	/*
	JFrame frame = new JFrame("sub image");
	frame.setBounds(1200, 700, 32, 54);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JTable table = new JTable(data[0]);
	frame.getContentPane().add(table);
	frame.setVisible(true);
	*/


    }

    public static int[][][] getFieldPuyo(BufferedImage image) {
	BufferedImage subImage;
	int[][][] ret = new int[2][][];
	IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	for (int i = 0; i < 2; i++) {
	    ret[i] = new int[MAX_HEIGHT][];
	    for (int j = 0; j < MAX_HEIGHT; j++) {
		ret[i][j] = new int[MAX_WIDTH];
	    }
	}

	for (int p = 0; p < 2; p++) {
	    for (int y = 0; y < MAX_HEIGHT - 1; y++) {//to 12th
		for (int x = 0; x < MAX_WIDTH; x++ ) {
		    int[] c = FieldCoordinates.idx2field(p, x, y);
		    subImage = image.getSubimage(c[0], c[1], c[2], c[3]);
		    identPuyoColor.getPuyoColor(subImage);
		    if(identPuyoColor.dist > 0.0045) {//todo: magic number
			ret[p][y][x] = -1;
		    } else {
			ret[p][y][x] = identPuyoColor.idx;
		    }

		}
	    }	
	}
	return ret;
    }

    public int[][][] getFieldPuyo() {
	captureScreen.getCaptureImage();
	captureScreen.display();//todo: delete this line
	BufferedImage subImage;
	IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	int[][][] ret = new int[2][][];
	for (int i = 0; i < 2; i++) {
	    ret[i] = new int[MAX_HEIGHT][];
	    for (int j = 0; j < MAX_HEIGHT; j++) {
		ret[i][j] = new int[MAX_WIDTH];
	    }
	}

	for (int p = 0; p < 2; p++) {
	    for (int y = 0; y < MAX_HEIGHT - 1; y++) {//to 12th
		for (int x = 0; x < MAX_WIDTH; x++ ) {
		    int[] c = FieldCoordinates.idx2field(p, x, y);
		    subImage = captureScreen.getImage(c);
		    identPuyoColor.getPuyoColor(subImage);
		    if(identPuyoColor.dist > 0.0045) {//todo: magic number
			ret[p][y][x] = -1;
		    } else {
			ret[p][y][x] = identPuyoColor.idx;
		    }

		}
	    }	
	}
	return ret;
    }

}


