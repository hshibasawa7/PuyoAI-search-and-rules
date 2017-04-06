package SnesInterface;

import java.util.Arrays;

public class FieldCoordinates {
    public static final int screen_w = 512;
    public static final int screen_h = 478 + 22;

    //32x32
    //(x1, y1) = (16, 66), (x2, y2) = (304, 66)
    //(w, h) = (192, 384)

    //next: (x1, y1) = (220, 129), (x2, y2) = (264, 129), (w, h) = (28, 32x2)
    //nxnx: (x1, y1) = (228, 208), (x2, y2) = (272, 208), (w, h) = (12, 32*2)
    public static final int[] x = { 16, 304 };
    public static final int y =  418;
    public static final int w = 32;
    public static final int h = 32;

    public static final int[] next_x = {220, 264};
    public static final int next_y = 129;
    public static final int next_w = 28;
    
    public static final int[] nxnx_x = {228, 272};
    public static final int nxnx_y = 208;
    public static final int nxnx_w = 12;

    public static final int[][] selectLevel = { 
	{128, 90, 80, 2}, {416, 90, 80, 2}, };

    public static int[] idx2field(int p, int idx_x, int idx_y) {
	int[] ret = new int[4];
	if(idx_x < 0 || 6 < idx_x ||
	   idx_y < 0 || 13 < idx_y) {
	    System.out.println("invalid index is inputed.");
	    Arrays.fill(ret, -1);
	    return ret;
	}
	ret[0] = x[p] + w * idx_x;
	ret[1] = y - h * idx_y;
	ret[2] = w;
	ret[3] = h;
	return ret;
    }
    public static int[][] getNext(int p) {
	int[][] ret = new int[2][];
	for (int i = 0; i < 2; i++) {
	    ret[i] = new int[4];
	}

	for(int i = 0; i < 2; i++) {
	    ret[i][0] = next_x[p];
	    ret[i][1] = next_y + h*i;
	    ret[i][2] = next_w;
	    ret[i][3] = h;
	}
	return ret;
    }

    public static int[][] getNxnx(int p) {
	int[][] ret = new int[2][];
	for (int i = 0; i < 2; i++) {
	    ret[i] = new int[4];
	}

	for(int i = 0; i < 2; i++) {
	    ret[i][0] = nxnx_x[p];
	    ret[i][1] = nxnx_y + h*i;
	    ret[i][2] = nxnx_w;
	    ret[i][3] = h;
	}
	return ret;
    }    

}
