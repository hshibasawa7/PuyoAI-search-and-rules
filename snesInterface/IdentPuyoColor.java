//package snesInterface;


import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class IdentPuyoColor {
    //private BufferedImage image;
    private static int histDim = 4;
    public int idx = -1;
    public float dist = -1.0F;

    //image + idx => 1マス
    //1マス=> hist => colorID


    public static void main(String[] args) {
	if(args.length < 1) {
	    System.out.println("input file name.");
	    return;
	}
	if(args.length < 3) {
	    System.out.println("input idx number. (x, y)");
	    return;
	}
	BufferedImage image, subImage;
	String filename = args[0];
	File f = new File("./", filename);
	IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	try {
	    image = ImageIO.read(f);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	int x_idx = Integer.parseInt(args[1]);
	int y_idx = Integer.parseInt(args[2]);
	int[] c = FieldCoordinates.idx2field(0, x_idx, y_idx);
	//subImage = image.getSubimage(32*x_idx - 16, 450 - 32*y_idx, 32, 32);
	subImage = image.getSubimage(c[0], c[1], c[2], c[3]);
        identPuyoColor.getPuyoColor(subImage);
	int min_idx = identPuyoColor.idx;
	float min_val = identPuyoColor.dist;

	System.out.println(PuyoColor.getColorString(min_idx) + "(" + min_idx + "): " + min_val);

	JFrame frame = new JFrame("sub image");
	frame.setBounds(1200, 700, 32, 54);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JLabel label = new JLabel(new ImageIcon(subImage));
	frame.getContentPane().add(label);
	frame.setVisible(true);


	/*
	float[][] hist = identPuyoColor.getHist(subImage);
	//identPuyoColor.dispHistAry(hist);

	float dist = 
	    IdentPuyoColor.getSquareDist(
					 //PuyoColor.trainedHist[PuyoColor.EMPTY], 
	            hist,
		    PuyoColor.trainedHist[PuyoColor.OJAMA]);
	System.out.println(dist);
	*/

    }

    /*
    public void setImage(File f) throws IOException {
        try {
	    this.image = ImageIO.read(f);
	} catch(IOException e) {
	    throw new IOException(e);
	}	

    }
    */

    public static float[][] getHist(BufferedImage image) {
	int w = image.getWidth();
	int h = image.getHeight();
	int dots = 0;
	int[] arrayRGB;
	float[][] hist = new float[3][];
	for (int i = 0; i < 3; i++) {
	    hist[i] = new float[histDim];//hist[i][j]=0.0
	}
	float step = (float)256.0 / histDim;

	//ヒストグラムへの投票
	for (int y = 0; y < h; y++) {
	    for (int x = 0; x < w; x++) {
		arrayRGB = RGB2Ary( image.getRGB(x, y) );
		for(int i = 0; i < 3; i++) {
		    for (int j = 0; j < histDim; j++) {
			if(step*j <= arrayRGB[i] && arrayRGB[i] < step*(j+1) ) {
			    hist[i][j] += 1.0;
			    dots++;
			}
		    }
		}
	    }
	}
	//正規化
	for(int i = 0; i < 3; i++) {
	    for (int j = 0; j < histDim; j++) {
		hist[i][j] /= dots;
	    }
	}

	return hist;
    }
    public static int[] RGB2Ary(int rgb) {
	int[] arrayRGB = new int[3];
	arrayRGB[0] = (rgb >> 16) & 0xFF;
	arrayRGB[1] = (rgb >>  8) & 0xFF;
	arrayRGB[2] = (rgb      ) & 0xFF;
	return arrayRGB;
    }

    public static void dispHistAry(float[][] hist) {
	System.out.print("{");
	for (int i = 0; i < hist.length; i++) {
	    System.out.print("{" + hist[i][0]);
	    for (int j = 1; j < hist[0].length; j++) {
		System.out.print( ", " + hist[i][j] );
	    }
	    if (i < hist.length-1)
		System.out.println("},");
	}
	System.out.println("}}");
    }

    public static float getSquareDist(float[][] hist1, float[][] hist2) {
	float dist = .0F;
	if( hist1.length != hist2.length ||
	    hist1[0].length != hist2[0].length) {
	    System.out.println("invalid vectors: they have defferent dimension.");
	    return -1.0F;
	}

	for (int i = 0; i < hist1.length; i++) {
	    for (int j = 0; j < hist1[0].length; j++) {
		dist += (hist1[i][j] - hist2[i][j])*(hist1[i][j] - hist2[i][j]);
	    }
	}
	return dist;
    }

    public int getPuyoColor(BufferedImage image) {
	float[][] hist = getHist(image);
	float[] dist = new float[PuyoColor.MAX_NUM];

	int min_idx = -1;
	float min_val = 777.7F;

	for (int i = 0; i < dist.length; i++) {
	    dist[i] = getSquareDist(hist, PuyoColor.trainedHist[i]);
	    if(dist[i] < min_val) {
		min_idx = i;
		min_val = dist[i];
	    }
	}
	this.idx = min_idx;
	this.dist = min_val;
	/*
	if (min_val > 0.003) {
	    min_idx = -1;
	}
	*/

	return min_idx;
    }

}
