package GlobalFunc;

import SnesInterface.PuyoColor;

public class Field {
    public static final int MAX_WIDTH = 6;
    public static final int MAX_HEIGHT = 13;
    public static final int MAX_VISUAL_HEIGHT = 12;

    public static void copyField(int[][] source, int[][] object) {
	for (int i = 0; i < source.length; i++) {
	    for (int j = 0; j < source[0].length; j++) {
		object[i][j] = source[i][j];
	    }
	}
    }
    public static int[][] copyField(int[][] source) {
	int[][] object = new int[source.length][];
	for (int i = 0; i < source.length; i++) {
	    object[i] = new int[source[0].length];
	}
	copyField(source, object);

	return object;
    }

    public static int[] getHeight(int[][] field) {
	int[] height = new int[MAX_WIDTH];
	int y;
	for (int p = 0; p < 2; p++) {
	    for (int x = 0; x < MAX_WIDTH; x++) {
		if(field[0][x] == PuyoColor.EMPTY) {
		    height[x] = 0;
		    continue;
		}
		for (y = 1; y < MAX_HEIGHT; y++ ) {
		    if(field[y][x] == PuyoColor.EMPTY) {
			break;
		    }
		}
		height[x] = y;
	    }
	}
	return height;
    }

    public static int[] copyHeight(int[] source) {
	int[] object = new int[source.length];
	for (int i = 0; i < source.length; i++) {
	    object[i] = source[i];
	}
	return object;
    }

    public static boolean overHeight(int[] height) {
	for (int i = 0; i < MAX_WIDTH; i++) {
	    if (height[i] >= MAX_HEIGHT) {
		return true;
	    }
	}
	return false;
    }


    public static void printData(int[][][] data) {
	
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
    }

    public static void printData(int[][] data) {
	for (int i = MAX_HEIGHT - 1; i >= 0 ; i-- ) {
	    for (int j = 0; j < MAX_WIDTH; j++) {
		if (data[i][j] < 0) {
		    System.out.print(" -");
		} else {
		    System.out.print(" " + data[i][j]);
		}
	    }
	    System.out.println();
	}
	System.out.println();
    }

    public static int getPuyoNum(int[][] data) {
	int num = 0;
	for (int i = 0; i < MAX_HEIGHT; i++) {
	    for (int j = 0; j < MAX_WIDTH; j++) {
		if ( data[i][j] != PuyoColor.EMPTY ) {
		    num++;
		}
	    }
	}
	return num;
    }

    public static boolean isAllClear(int[][] data) {
	for (int i = 0; i < MAX_WIDTH; i++) {
	    if (data[0][i] != PuyoColor.EMPTY) {
		return false;
	    }
	}
	return true;
    }

}
