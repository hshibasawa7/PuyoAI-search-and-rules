import java.util.ArrayList;
import java.util.Random;

import SnesInterface.*;
import globalFunc.Field;

public class Potential {
    int cnt_scoreCalc = 0;
    /*
    public final int MAX_WIDTH = 6;
    public final int MAX_HEIGHT = 13;
    */
    public static void main(String args[]) {
	PlayerStat stat = new PlayerStat();
	Potential ai = new Potential();
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

	stat = new PlayerStat( new int[2], ioface.getNextPuyo()[1], ioface.getNxnxPuyo()[1]);

	int col[] = new int[2];
	int row[] = new int[2];
	int depth = 3;
	int iMax = 30;
	for (int i = 0; i < iMax; i++) {
	    while(true) {
		if ( ioface.waitPuyoGiven(1, stat.thisTsumo, stat.nextTsumo, stat.nxnxTsumo) ) {
		    stat.addColorVari();
		    break;
		}
		try {
		    Thread.sleep(500);
		} catch(InterruptedException e ) {
		    throw new RuntimeException(e);
		}
	    }
	    
	    long start = System.currentTimeMillis();
	    int score = -1;
	    if (iMax - i < depth) {
		score = ai.decidePutLocale(stat, col, row, iMax - i);
	    } else {
		score = ai.decidePutLocale(stat, col, row, depth);
	    }
	    long stop = System.currentTimeMillis();
	    System.out.println("time: " + (stop - start));

	    System.out.println("score: " + score + " ");
	    if (score < 0) {
		return;
	    }
	    System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
	    
	    ioface.putPuyo(col, row);
	    stat.setPuyo2Field(col, row);
	    Field.printData(stat.field);
	    //おじゃま、消去
	}

    }

    //ret = max(score)
    public int simulation(int[][] ex_field, int[] ex_height, int[][][] tsumoList, int depth, int[] col, int[] row) {
	/*
	if (depth > 3) {
	    System.out.println("depth is too lerge(" + depth + ")");
	    return -1;
	}
	*/
	if (depth == 0) {
	    cnt_scoreCalc++;
	    return  Score.getPoint(ex_field);
	}

	int[][] field;
	int[] height;
	int ret;
	int max = -1;

	//todo: ツモの組み合わせ
	    ArrayList<Integer> idxList = new ArrayList<Integer>();
	for (int j = 0; j < tsumoList[depth-1].length; j++) {
	    int numCombi = PutCombination.getNumCombi(tsumoList[depth-1][j]);
	    for (int i = 0; i < numCombi; i++ ) {
		field = Field.copyField(ex_field);
		height = Field.copyHeight(ex_height);		    
		int putType = PutCombination.setSimField(field, height, tsumoList[depth-1][j], i);
		if (putType < 0) { ret = -1; }
		else {
		    if (depth != 1 && putType > 0) { 
			ret = -1;//消してはならない
		    } else {
			ret = simulation(field, height, tsumoList, depth-1, col, row);
		    }
		}

		if (max < ret) {
		    max = ret;
		    idxList.clear();
		    idxList.add(i);
		} else if (max == ret) {
		    idxList.add(i);
		}
	    }
	    //maxの平均化、ミニマックス	    
	}
	//一手目
	if (depth == tsumoList.length) {
	    int size = idxList.size();
	    System.out.println("decided tsumoList size: " + size);
	    if (size == 0) {
		System.out.println("it was not able to find putting locale.");
		//size = numCombi;
		return -1;
	    }
	    int i = idxList.get(new Random().nextInt(size));
	    PutCombination.setColRow(ex_height, col, row, i);
	}
	
	return max;

    }

    public int decidePutLocale(PlayerStat stat, int[] col, int[] row, int depth) {
	if (depth < 1) {
	    System.out.println("invalid depth:" + depth);
	    return -1;
	}
	//int[][][], create fullTsumo
	

	int[][][] tsumoList = new int[depth][][];
	tsumoList[depth-1] = new int[1][];
	tsumoList[depth-1][0] = stat.thisTsumo;
	if (2 <= depth) {
	    tsumoList[depth-2] = new int[1][];
	    tsumoList[depth-2][0] = stat.nextTsumo;
	    if (3<= depth) {
		tsumoList[depth-3] = new int[1][];
		tsumoList[depth-3][0] = stat.nxnxTsumo;
		if (3 < depth) {
		    int[][] fullTsumo = createFullTsumo(stat);
		    for (int i = depth-4; i >= 0; i--) {
			tsumoList[i] = fullTsumo;
		    }
		}
	    }
	}
	int[] height = Field.getHeight(stat.field);
	for(int i = 0; i < 6; i++){ System.out.print(height[i]);}
	System.out.println();
	cnt_scoreCalc = 0;
	int score = simulation(stat.field, height, tsumoList, depth, col, row);
	if ( score < 0) {
	    System.out.println("Warning: this game is not playable.");
	}
	System.out.println("calculation times: " + cnt_scoreCalc);
	return score;

    }

    public int[][] createFullTsumo(PlayerStat stat) {
	ArrayList<Integer> list = new ArrayList<Integer>();
	if (stat.colorVariation[PuyoColor.RED]) {
	    list.add(PuyoColor.RED);
	}
	if (stat.colorVariation[PuyoColor.BLUE]) {
	    list.add(PuyoColor.BLUE);
	}
	if (stat.colorVariation[PuyoColor.GREEN]) {
	    list.add(PuyoColor.GREEN);
	}
	if (stat.colorVariation[PuyoColor.PURPLE]) {
	    list.add(PuyoColor.PURPLE);
	}
	if (stat.colorVariation[PuyoColor.YELLOW]) {
	    list.add(PuyoColor.YELLOW);
	}
	int len = list.size();
	int tsumoCombi = -1;
	switch(len) {
	case 1:
	    tsumoCombi = 1;
	    break;
	case 2:
	    tsumoCombi = 3;
	    break;
	case 3:
	    tsumoCombi = 6;
	    break;
	case 4:
	    tsumoCombi = 10;
	    break;
	case 5:
	    tsumoCombi = 15;
	    break;
	}

	int[][] tsumo = new int[tsumoCombi][];
	int idx = 0;
	for (int i = 0; i < len; i++) {
	    for (int j = i; j < len; j++) {
		tsumo[idx] = new int[2];
		tsumo[idx][0] = list.get(i);
		tsumo[idx][1] = list.get(j);
		idx++;
	    }
	}
	return tsumo;
    }

}
