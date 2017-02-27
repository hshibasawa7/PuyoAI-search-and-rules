import GlobalFunc.Field;

public class PutCombination {
    //todo: rename
    private static int[][] col = {
	{0, 0}, {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5},
	{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}
    };
    public final static int NUM_ZORO = col.length;
    public final static int NUM_ALL = NUM_ZORO * 2;
 
    //todo: 返り値が美しく無い
    //ret: 置けない場合-1, 置ける場合0, 置いて消える時1
    public static int setSimField(int[][] field, int[] height, int[] tsumo, int i) {
        int isChain = 0;
	int[] col = new int[2];
	int[] row = new int[2];
	int[][] mask = Score.initMask();
	setColRow(height, col, row, i);
	//todo: Magic Number
	if (row[0] >= Field.MAX_HEIGHT || row[1] >= Field.MAX_HEIGHT) {//13 out
	    return -1;
	}

	field[row[0]][col[0]] = tsumo[0];
	if(Score.setConnectMask(field, mask, col[0], row[0]) >= 4 ) {
	    //System.out.println("idx"+i+"("+col[0]+","+row[0]+")");
	    //globalFunc.Field.printData(field);
	    isChain = 1;
	}
	height[col[0]]++;

	field[row[1]][col[1]] = tsumo[1];
	mask = Score.initMask();
	if(Score.setConnectMask(field, mask, col[1], row[1]) >= 4 ) {
	    //System.out.println("idx"+i+"("+col[0]+","+row[0]+")");
	    //globalFunc.Field.printData(field);
	    isChain = 1;
	}
	height[col[1]]++;

	if (isChain != 1) {
	    if(col[0] == 2 && row[0] >= Field.MAX_VISUAL_HEIGHT-1) { return -1; }
	    if(col[1] == 2 && row[1] >= Field.MAX_VISUAL_HEIGHT-1) { return -1; }
	}
	return isChain;
    }
    public static void setColRow(int[] height, int[] col, int[] row, int i) {
	int x0 = -1;
	int x1 = -1;
	if (i < NUM_ZORO) {
	    //todo: remove class name
	    col[1] = PutCombination.col[i][0];
	    row[1] = height[col[1]];
	    col[0] = PutCombination.col[i][1];
	    if (col[0] == col[1]) {
		row[0] = row[1] + 1;
	    } else {
		row[0] = height[col[0]];
	    }
	} else if (i < NUM_ALL) {
	    i %= NUM_ZORO;
	    //todo: remove class name
	    col[0] = PutCombination.col[i][0];
	    row[0] = height[col[0]];
	    col[1] = PutCombination.col[i][1];
	    if (col[0] == col[1]) {
		row[1] = row[0] + 1;
	    } else {
		row[1] = height[col[1]];
	    }
	}

    }
    public static int getNumCombi(int[] tsumo) {
	if (tsumo[0] == tsumo[1]) {
	    return NUM_ZORO;
	}
	return NUM_ALL;
    }
}
