//import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;

import SnesInterface.PuyoColor;
import globalFunc.Field;

public class Score {
    public static void main(String args[]) {
	int[][] input = {
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,0,0,0,0,0},
	    {0,1,1,0,0,0},
	    {0,3,3,0,0,0},
	    {0,3,3,0,0,0},
	    {0,1,1,0,0,0},
	    {1,2,2,2,1,0},
	    {1,1,2,1,1,0}
	};
	int[][] data = new int[input.length][];
	for (int i = 0; i < data.length; i++) {
	    data[i] = input[data.length-i-1];
	}
	int score = Score.getPoint(data);
	System.out.println("Score:" + score);
    }

    public static int ojamaRate = 70;
    public static final int allClear = 2100;

    //todo: rename and integration
    private static final int x_max = 6;
    private static final int y_max = 13;
    //private static final int header = 1;

    public static int getChainBonus(int n) {
	if(n == 1) { return 0; }
	if(n <= 3) { return 8*(n - 1); }
	if(n <= 19) { return 32*(n - 3); }
	return -1;
    }

    public static int getConnectBonus(int n) {
	if(n == 4) { return 0; }
	if(n <= 10) { return n - 3; }
	if(n > 10) { return 10; }
	return -1;
    }
    public static int getColorBonus(int n) {
	if(n == 1) { return 0; }
	if(n <= 5) { return 3*(int)Math.pow(2, n - 2); }
	return -1;
    }


    public static int[][] initMask() {
	int[][] mask = new int[y_max][];
	for (int i = 0; i < y_max; i++) {
	    mask[i] = new int[x_max];
	}
	return mask;
    }
    public static int setConnectMask(int[][] field, int[][]mask, int x_idx, int y_idx) {
	if(y_idx < 0 || y_max <= y_idx) { return 0; }
	if(x_idx < 0 || x_max <= x_idx) { return 0; }
	
	int color = field[y_idx][x_idx];
	if(color == PuyoColor.EMPTY) { return 0; }
	
	if(mask[y_idx][x_idx] > 0) { return 0; }
	mask[y_idx][x_idx] = 1;
	
	int c = 1;
	c += setConnectMask(field, mask, x_idx - 1, y_idx, color);
	c += setConnectMask(field, mask, x_idx + 1, y_idx, color);
	c += setConnectMask(field, mask, x_idx, y_idx - 1, color);
	c += setConnectMask(field, mask, x_idx, y_idx + 1, color);
	return c;
    }

    public static int setConnectMask(int[][] field, int[][]mask, int x_idx, int y_idx, int color ) {
	if(y_idx < 0 || y_max <= y_idx) { return 0; }
	if(x_idx < 0 || x_max <= x_idx) { return 0; }

	if(color != field[y_idx][x_idx]) { return 0; }

	if(mask[y_idx][x_idx] > 0) { return 0; }
	mask[y_idx][x_idx] = 1;

	int c = 1;
	c += setConnectMask(field, mask, x_idx - 1, y_idx, color);
	c += setConnectMask(field, mask, x_idx + 1, y_idx, color);
	c += setConnectMask(field, mask, x_idx, y_idx - 1, color);
	c += setConnectMask(field, mask, x_idx, y_idx + 1, color);
	return c;
    }

    public static int getMaskCount(int[][] mask) {
	int count = 0;
	for(int i = 0; i < y_max; i++) {
	    for(int j = 0; j < x_max; j++) {
		count += mask[i][j];
	    }
	}
	return count;
    }
    public static int[][] initConnect() {
	int[][] connect = new int[y_max][];
	for (int i = 0; i < y_max; i++) {
	    connect[i] = new int[x_max];
	}
	return connect;
    }
    /*
    public int getConnect(int x_idx, int y_idx) {
	return this.puyoConnect[y_idx][x_idx];
    }
    */
    public static void setConnect(int[][] field, int[][] connect, int[][] delCount, int x_idx, int y_idx) {
	int[][] mask = initMask();

	int c = setConnectMask(field, mask, x_idx, y_idx);
	if(c == 0) {
	    connect[y_idx][x_idx] = 0;
	} else if(c == 1) {
	    connect[y_idx][x_idx] = c;
	} else {
	    for (int i = 0; i < y_max; i++) {
		for(int j = 0; j < x_max; j++) {
		    if(mask[i][j] == 1) {connect[i][j] = c;}
		}
	    }
	    if (c >= 4) {
		setDelCount(delCount, field[y_idx][x_idx], c);
	    }
	}
    }
    public static void setConnectAll(int[][] field, int[][] connect, int[][] delCount) {
	for (int i = 0; i < y_max; i++) {
	    for (int j = 0; j < x_max; j++) {
		if (connect[i][j] > 0) { continue; }
		setConnect(field, connect, delCount, j, i);
	    }
	}
    }

    public static int[][] initDelCount() {
	int[][] delCount = new int[PuyoColor.MAX_NUM][];
	for (int i = 0; i < PuyoColor.MAX_NUM; i++) {
	    delCount[i] = new int[0];
	}
	return delCount;
    }
    public static void setDelCount(int[][] delCount, int color, int numConnect) {
	/*
	ArrayList<Integer> list = new ArrayList<>( Arrays.asList(delCount[color]) );
	list.add(numConnect);
	delCount[color] = (int[])list.toArray();
	*/
	int l = delCount[color].length;
	delCount[color] = Arrays.copyOf(delCount[color], l + 1);
	delCount[color][l] = numConnect;
    }

    /*
	public void incDelCount(int colorName, int numConnect) {

	    
		if(this.delCount[colorName] == undefined) {
			this.delCount[colorName] = {};
		}
		if(this.delCount[colorName][numConnect] == undefined) {
			this.delCount[colorName][numConnect] = 1;
		} else {
			this.delCount[colorName][numConnect]++;
		}
	    
	}
    */
    public static int delPuyo(int[][] field, int[][] connect) {
	int cnt = 0;
	//delCount = initDelCount();
	for(int i = 0; i < y_max; i++) {
	    for(int j = 0; j < x_max; j++) {
		if(connect[i][j] >= 4) {
		    //this.incDelCount(this.getColorName(j, i), this.getConnect(j, i));
		    cnt += 1;
		    field[i][j] = PuyoColor.EMPTY;
		    connect[i][j] = 0;
		}
	    }
	}
	//this.drawPuyo();
	/*
		if(delcnt > 0) {
			chain++;
			//var ele = document.getElementById("score");
			//ele.value = this.calcPoint();
			//this.setNumOjama();
			//console.log(this.numOjama);
		} else { this.chain = 0; }
	*/
	return cnt;
    }
    
    public static boolean fallPuyo(int[][] field) {
	int hole;
	boolean isFall = false;
	//todo: use getHeight(field)
	for(int i = 0; i < x_max; i++) {
	    hole = 0;
	    for(int j = 0; j < y_max; j++) {
		if(field[j][i] == PuyoColor.EMPTY) {
		    hole++;
		} else if (hole > 0) {
		    field[j-hole][i] = field[j][i];
		    field[j][i] = PuyoColor.EMPTY;
		    //this.setColor(i, j - hole, this.getColorNum(i, j));
		    //this.setColor(i, j, 0);
		    isFall = true;
		}
	    }
	}
	//setConnectAll(field, delCount);
	//this.drawPuyo();
	
	//var ele = document.getElementById("score");
	//ele.value = this.point;
    
	return isFall;
    }
    public static int calcPoint(int[][] field, int chain) {
	int totalPuyo = 0;
	int[][] connect = initConnect();
	int[][] delCount = initDelCount();
	setConnectAll(field, connect, delCount);
	if (delPuyo(field, connect) < 1) { return 0; }
        fallPuyo(field);
	//now, connect and delCount do not correspond with field data.

	int chainBonus = getChainBonus(chain);
	if(chainBonus < 0) {
	    System.out.println("Chain Bonus errror.");
	    return -1; 
	}

	int numColor= 0;
	for (int i = 0; i < PuyoColor.MAX_NUM; i++) {
	    if (delCount[i].length > 0) { numColor++; }
	}
	int colorBonus = getColorBonus(numColor);
	if (colorBonus < 0) {
	    System.out.println("Color Bonus errror.");
	     return -1; }

	int connectBonus = 0;
	for(int i = 0; i < delCount.length; i++) {
	    for (int j = 0; j < delCount[i].length; j++) {
		int buf = getConnectBonus(delCount[i][j]);
		if(buf < 0) {
		    System.out.println("Connect Bonus errror.");
		    return -1;
		}
		connectBonus += buf;
		totalPuyo += delCount[i][j];
	    }
	}
	int allBonus = chainBonus + colorBonus + connectBonus;
	if( allBonus == 0) { allBonus = 1; }

        int point = calcPoint(field, chain+1);
	if (point < 0) { return -1; }
	return totalPuyo*10*allBonus + point;
    }
    public static int getPoint(int[][] ex_field) {
	int[][] field = Field.copyField(ex_field);
	return calcPoint(field, 1);
    }


}
