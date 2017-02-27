import java.util.ArrayList;
import java.util.Random;

import SnesInterface.*;
import GlobalFunc.Field;

public class PotentialFullKai {
    public int scoreCalcNum = 0;
    public int tsumoListNum = 0;

    //int cnt_scoreCalc = 0;

    public PotentialFullKai() {
	System.out.println("using PotentialFullKai algorithm.");
    }

    public static void main(String args[]) {
	PlayerStat stat = new PlayerStat();
	PotentialFullKai ai = new PotentialFullKai();
	SnesInterface ioface = new SnesInterface(stat.player);
	/*robotの生成時、threadがアクティブ化（初回のみ？）
	  snes9xが停止するため要対策
	 */

	try {
	    Thread.sleep(3000);
	} catch(InterruptedException e ) {
	    throw new RuntimeException(e);
	}
	if (ioface.waitSelectLevel()) {
	    System.out.println("Ready");
	    ioface.OneButton(SnesKeyCode.ROT_R);
	}
	/*
	try {
	    Thread.sleep(1500);
	} catch(InterruptedException e ) {
	    throw new RuntimeException(e);
	}
	*/

	stat = new PlayerStat( new int[2], ioface.getNextPuyo()[stat.player], ioface.getNxnxPuyo()[stat.player]);

	int col[] = new int[2];
	int row[] = new int[2];
	int depth = 3;
	int iMax = 30;
	for (int i = 0; i < iMax; i++) {
	    while(true) {
		if ( ioface.waitPuyoGiven(stat.player, stat.thisTsumo, stat.nextTsumo, stat.nxnxTsumo) ) {
		    stat.addColorVari();
		    break;
		}
		//死亡判定
		if (ioface.isFieldEmpty(stat.player) && !stat.allClear) {
		    System.out.println("Game Over!!");
		    return;
		}
		/*
		try {
		    Thread.sleep(200);
		} catch(InterruptedException e ) {
		    throw new RuntimeException(e);
		}
		*/
	    }
	    //stat.Field更新
	    stat.setPuyo2Field(ioface);

	    long start = System.currentTimeMillis();
	    float[] score;
	    if (iMax - i < depth) {
		score = ai.decidePutLocale(stat, col, row, iMax - i);
	    } else {
		score = ai.decidePutLocale(stat, col, row, depth);
	    }
	    long stop = System.currentTimeMillis();
	    System.out.println("time: " + (stop - start));

	    System.out.println("score: " + score[0] + " ");
	    if (score[0] < 0) {
		return;
	    }
	    System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
	    
	    ioface.putPuyo(col, row);
	    stat.setPuyo2Field(col, row);
	    stat.allClear = Field.isAllClear(stat.field);
	    Field.printData(stat.field);
	    //おじゃま、消去
	}

    }

    //ret = max(score)
    public float[] simulation(int[][] ex_field, int[] ex_height, int[][][] tsumoList, final int max_depth, int depth, int[] col, int[] row) {

	if (depth == 0) {
	    float[] ret = new float[3];
	    ret[0] = .0F;//score
	    ret[1] = .0F;//chain
	    ret[2] = .0F;//delPuyo
	
	    return ret;
	    /*
	    scoreCalcNum++;
	    Score score = new Score();
	    score.setPoint(ex_field);
	    return score;
	    */
	}

	int[][] field;
	int[] height;
	int scoreNum = -1;
	float max = -1.0F;

	//todo: ツモの組み合わせ
	ArrayList<Integer> idxList = new ArrayList<Integer>();
	ArrayList<float[]> scoreList = new ArrayList<float[]>();
	for (int j = 0; j < tsumoList[depth-1].length; j++) {
	    int numCombi = PutCombination.getNumCombi(tsumoList[depth-1][j]);
	    for (int i = 0; i < numCombi; i++ ) {
		field = Field.copyField(ex_field);
		height = Field.copyHeight(ex_height);		    
		int putType = PutCombination.setSimField(field, height, tsumoList[depth-1][j], i);
		Score score = new Score();
		float[] ret = new float[3];

		if (putType < 0) { ret[0] = -1.0F; }
		else {
		    if (putType > 0) { 
			if (depth == max_depth && 1 < max_depth) {
			    ret[0] = -1.0F;//初手で消してはならない
			} else {
			    score.setPoint(field);//発火時点で評価
			    scoreCalcNum++;

			    ret[0] = score.score;
			    ret[1] = score.chain;
			    ret[2] = score.totalDel;
			}

		    } else {
			//depth == 1でも、どうしても消せない場合あり
			ret = simulation(field, height, tsumoList, max_depth, depth-1, col, row);
		    }
		}

		if (max < ret[0]) {
		    max = ret[0];
		    idxList.clear();
		    idxList.add(i);
		    scoreList.clear();
		    scoreList.add(ret);
		} else if (max == ret[0]) {
		    idxList.add(i);
		    scoreList.add(ret);
		}
	    }
	    //maxの平均化、ミニマックス	    
	}
	//一手目
	int size = idxList.size();
	if (size == 0) {
	    System.out.println("it was not able to find putting locale.");
	    //size = numCombi;
	    return null;
	}
	int i = new Random().nextInt(size);
	int idx = idxList.get(i);
        float[] ret = scoreList.get(i);
	if (depth == tsumoList.length) {
	    //System.out.println("decided tsumoList size: " + size);
	    tsumoListNum = size;
	    PutCombination.setColRow(ex_height, col, row, idx);
	}
	return ret;

    }

    public float[] decidePutLocale(PlayerStat stat, int[] col, int[] row, int depth) {
	if (depth < 1) {
	    System.out.println("invalid depth:" + depth);
	    return null;
	}
	//int[][][], create fullTsumo
	

	int[][][] tsumoList = new int[depth][][];
	int[][] fullTsumo = createFullTsumo(stat);
	if (stat.thisTsumo[0] == PuyoColor.EMPTY) {
	    tsumoList[depth-1] = fullTsumo;
	} else {
	    tsumoList[depth-1] = new int[1][];
	    tsumoList[depth-1][0] = stat.thisTsumo;
	}
	if (2 <= depth) {
	    if (stat.nextTsumo[0] == PuyoColor.EMPTY) {
		tsumoList[depth-2] = fullTsumo;
	    } else {
		tsumoList[depth-2] = new int[1][];
		tsumoList[depth-2][0] = stat.nextTsumo;
	    }
	    if (3<= depth) {
		if (stat.nxnxTsumo[0] == PuyoColor.EMPTY) {
		    tsumoList[depth-3] = fullTsumo;
		} else {
		    tsumoList[depth-3] = new int[1][];
		    tsumoList[depth-3][0] = stat.nxnxTsumo;
		}
		if (3 < depth) {
		    for (int i = depth-4; i >= 0; i--) {
			tsumoList[i] = fullTsumo;
		    }
		}
	    }
	}
	int[] height = Field.getHeight(stat.field);
	//for(int i = 0; i < 6; i++){ System.out.print(height[i]);}
	//System.out.println();
	scoreCalcNum = 0;
	float[] score = simulation(stat.field, height, tsumoList, depth, depth, col, row);
	if ( score[0] < 0) {
	    System.out.println("Warning: this game is not playable.");
	}
	//System.out.println("calculation times: " + scoreCalcNum);
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
