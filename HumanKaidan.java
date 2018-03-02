/*
Todo: プレイヤー切り替え
player controle
level image

Todo: AI
all clear flag
geme start input


 */


import java.util.ArrayList;
import java.util.Random;

import SnesInterface.*;
import GlobalFunc.Field;

public class HumanKaidan {
    public int scoreCalcNum = 0;
    public int tsumoListNum = 0;

    private int[] dodai = new int[Field.MAX_WIDTH];
    private int[] sikake = new int[Field.MAX_WIDTH];
    private int[] dodaiColor = new int[Field.MAX_WIDTH];
    //int cnt_scoreCalc = 0;

    public HumanKaidan() {
	System.out.println("using HumanKaidan algorithm.");
    }

    public static void main(String args[]) {
	if (args.length < 1) {
	    System.out.println("input player number: 1 or 2");
	    return;
	}
	int player = Character.getNumericValue(args[0].charAt(0));
	player--;
	if (player < 0 || 1 < player ) {
	    System.out.println("input player number: 1 or 2");
	    return;
	}

	int depth = 3;
	int threshold = 2100;

	while(true) {
	    HumanKaidan humanAi = new HumanKaidan();
	    DelPotentialKai ai = new DelPotentialKai();
	    SnesInterface ioface = new SnesInterface(player);
	    int moves = 6;
	    int cnt_moves = 0;
	/*robotの生成時、threadがアクティブ化（初回のみ？）
	  snes9xが停止するため要対策
	*/

	    try {
		Thread.sleep(1500);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }

	    if (ioface.waitSelectLevel()) {
		System.out.println("Ready");
		ioface.OneButton(ioface.snesKeyCode.ROT_R);
	    }
	    PlayerStat stat = new PlayerStat( new int[2], ioface.getNextPuyo()[player], ioface.getNxnxPuyo()[player], player);
	    
	    /*
	try {
	Thread.sleep(1500);
	} catch(InterruptedException e ) {
	throw new RuntimeException(e);
	}
	*/
	    
	    
	    int col[] = new int[2];
	    int row[] = new int[2];
	    int nxnx[] = new int[2];
	    boolean gameOver = false;
	    while(!gameOver) {
		cnt_moves++;
		int waitCounter = 0;//発火時アウト
		while(true) {
		    if ( ioface.waitPuyoGiven(stat.player, stat.thisTsumo, stat.nextTsumo, stat.nxnxTsumo) ) {
			stat.addColorVari();
			break;
		    }
		    //死亡判定
		    if (ioface.isFieldEmpty(stat.player) && !stat.allClear ||
			waitCounter > 10) {
			gameOver = true;
			System.out.println("Game Over!!");
			break;
		    }
		    //44waitCounter++;
		}
		if(gameOver) { break; }
		
		stat.setPuyo2Field(ioface);

		nxnx[0] = stat.nxnxTsumo[0];
		nxnx[1] = stat.nxnxTsumo[1];
		stat.nxnxTsumo[0] = PuyoColor.EMPTY;
		stat.nxnxTsumo[1] = PuyoColor.EMPTY;
		
		//long start = System.currentTimeMillis();
		float[] score;
		if (cnt_moves <= moves) {
		    score = humanAi.decidePutLocale(stat, col, row, depth);
		} else {
		    score = ai.decidePutLocale(stat, col, row, 1);
		    if (score[0] < threshold) {
			score = ai.decidePutLocale(stat, col, row, depth);
		    }
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("time: " + (stop - start));
		
		System.out.println("score: " + score[0] + " ");
		if (score[0] < 0) {
		    gameOver = true;
		    break;
		}
		System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
		
		ioface.putPuyo(col, row);
		stat.nxnxTsumo[0] = nxnx[0];
		stat.nxnxTsumo[1] = nxnx[1];
		
		stat.setPuyo2Field(col, row);
		Field.printData(stat.field);
		//おじゃま、消去
	    }
	    
	    try {
		Thread.sleep(1500);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	    //ioface.OneButton(SnesKeyCode.START);
	}
    }

    public float[] decidePutLocale(PlayerStat stat, int[] col, int[] row, int depth) {
	if (depth < 1) {
	    System.out.println("invalid depth:" + depth);
	    return null;
	}
	float[] ret = {.0F, .0F, 1.0F};

	int[] height = Field.getHeight(stat.field);
	int[][] tsumo = new int[3][];
	tsumo[0] = stat.thisTsumo;
	tsumo[1] = stat.nextTsumo;
	tsumo[2] = stat.nxnxTsumo;
	boolean[] zoro = {
	    tsumo[0][0] == tsumo[0][1],
	    tsumo[1][0] == tsumo[1][1],
	    tsumo[2][0] == tsumo[2][1],
	};
		
	if (zoro[0]) {
	    for (int i = 0; i < Field.MAX_WIDTH-1; i++ ) {
		//縦置き
		if (dodai[i] == 1 && dodaiColor[i] == tsumo[0][0] ||
		    dodai[i] == 0 && (i==0 || dodaiColor[i-1] != tsumo[0][0])) {
		    col[0] = col[1] = i;
		    row[0] = 1;
		    row[1] = 0;

		    dodai[i] += 2;
		    dodaiColor[i] = tsumo[0][0];
		    return ret;
		}
		//横置き
		if (dodaiColor[i] == tsumo[0][0] && dodai[i] < 3 &&
		    height[i+1] >= 3 && sikake[i+1] <= 1 ) {
		    col[0] = i;
		    col[1] = i+1;
		    row[0] = height[i];
		    row[1] = height[i+1];
		    dodai[i]++;
		    sikake[i+1]++;
		    return ret;
		}

	    }
	    //左端へ
	    col[0] = col[1] = 0;
	    row[0] = height[0]+1;
	    row[1] = height[0];
	    return ret;
	}
	//非ゾロ

	//土台横
	for (int i = 0; i < Field.MAX_WIDTH-1; i++) {
	    if (dodaiColor[i] == tsumo[0][0] && dodai[i]< 3 &&
		((dodaiColor[i+1] == tsumo[0][1] && dodai[i+1] < 3)
		 // || dodaiColor[i+1] == PuyoColor.EMPTY
		 )) {
		col[0] = i;
		col[1] = i+1;
		row[0] = height[i];
		row[1] = height[i+1];
		
		dodai[i]++;
		dodai[i+1]++;
		dodaiColor[i+1] = tsumo[0][1];
		return ret;
	    }
	    if (dodaiColor[i] == tsumo[0][1] && dodai[i] < 3 &&
		((dodaiColor[i+1] == tsumo[0][0] && dodai[i+1] < 3)
		 // || dodaiColor[i+1] == PuyoColor.EMPTY
		 )) {
		col[0] = i+1;
		col[1] = i;
		row[0] = height[i+1];
		row[1] = height[i];
		
		dodai[i+1]++;
		dodai[i]++;
		dodaiColor[i+1] = tsumo[0][0];
		return ret;
	    }
	}

	//左端土台
	if(dodai[0] == 2) {
	    if (dodaiColor[0] == tsumo[0][0]) {
		col[0] = col[1] = 0;
		row[0] = height[0];
		row[1] = height[0]+1;
		
		dodai[0]++;
		return ret;
	    }
	    if (dodaiColor[0] == tsumo[0][1]) {
		col[0] = col[1] = 0;
		row[0] = height[0]+1;
		row[1] = height[0];
		
		dodai[0]++;
		return ret;
	    }
	}

	for (int i = 1; i < Field.MAX_WIDTH-1; i++) {
	    //土台＋仕掛け
	    if (dodai[i] == 2 && dodaiColor[i] == tsumo[0][0] &&
		dodaiColor[i-1] == tsumo[0][1] ) {
		col[0] = col[1] = i;
		row[0] = height[0];
		row[1] = height[0]+1;
		
		dodai[i]++;
		sikake[i]++;
		return ret;
	    }
	    if (dodai[i] == 2 && dodaiColor[i] == tsumo[0][1] &&
		dodaiColor[i-1] == tsumo[0][0] ) {
		col[0] = col[1] = i;
		row[0] = height[0]+1;
		row[1] = height[0];
		
		dodai[i]++;
		sikake[i]++;
		return ret;
	    }

	    //仕掛け左
	    if (dodaiColor[i-1] == tsumo[0][0]
		&& height[i] >= 3 && sikake[i] == 0
		&& ((dodaiColor[i+1] == tsumo[0][1] && dodai[i+1] < 3)
		    || dodaiColor[i+1] == PuyoColor.EMPTY && dodaiColor[i] != tsumo[0][1])) {
		col[0] = i;
		col[1] = i+1;
		row[0] = height[i];
		row[1] = height[i+1];
		
		sikake[i]++;
		dodai[i+1]++;
		dodaiColor[i+1] = tsumo[0][1];
		return ret;
	    }
	    if (dodaiColor[i-1] == tsumo[0][1] 
		&& height[i] >= 3 && sikake[i] == 0
		&& ((dodaiColor[i+1] == tsumo[0][0] && dodai[i+1] < 3)
		    || dodaiColor[i+1] == PuyoColor.EMPTY && dodaiColor[i] != tsumo[0][0])) {
		col[0] = i+1;
		col[1] = i;
		row[0] = height[i+1];
		row[1] = height[i];
		
		sikake[i]++;
		dodai[i+1]++;
		dodaiColor[i+1] = tsumo[0][0];
		return ret;
	    }
	}
	for (int i = 1; i < Field.MAX_WIDTH-1; i++) {
	    //仕掛け縦
	    if (height[i] >= 3 && sikake[i] == 0 && dodaiColor[i-1] == tsumo[0][0]) {
		col[0] = col[1] = i;
		row[0] = height[i];
		row[1] = height[i] + 1;

		sikake[i]++;
		return ret;
	    }
	    if (height[i] >= 3 && sikake[i] == 0 && dodaiColor[i-1] == tsumo[0][1]) {
		col[0] = col[1] = i;
		row[0] = height[i] + 1;
		row[1] = height[i];

		sikake[i]++;
		return ret;
	    }	    

	}


	//土台横
	for (int i = 0; i < Field.MAX_WIDTH-1; i++) {
	    if (dodaiColor[i] == tsumo[0][0] && dodai[i]< 3 &&
		( dodaiColor[i+1] == PuyoColor.EMPTY)) {
		col[0] = i;
		col[1] = i+1;
		row[0] = height[i];
		row[1] = height[i+1];
		
		dodai[i]++;
		dodai[i+1]++;
		dodaiColor[i+1] = tsumo[0][1];
		return ret;
	    }
	    if (dodaiColor[i] == tsumo[0][1] && dodai[i] < 3 &&
		( dodaiColor[i+1] == PuyoColor.EMPTY)) {
		col[0] = i+1;
		col[1] = i;
		row[0] = height[i+1];
		row[1] = height[i];
		
		dodai[i+1]++;
		dodai[i]++;
		dodaiColor[i+1] = tsumo[0][0];
		return ret;
	    }
	}

	//仕掛けの上におくパターン

	//同色の隣接注意
	//他
	for (int i = 0; i < Field.MAX_WIDTH-1; i++ ) {
	    if ( dodaiColor[i] == PuyoColor.EMPTY && dodaiColor[i+1] == PuyoColor.EMPTY) {
		if ((i == 0 || tsumo[0][1] != dodaiColor[i-1]) && 
		    (
		     (zoro[1] && tsumo[0][1] == tsumo[1][0]) || //ABBB
		     //(isABAC(tsumo[0], tsumo[1]) && isSame(tsumo[0], tsumo[2]) ) || //ABACAB 
		     //(isABAC(tsumo[0], tsumo[1]) && zoro[2] && tsumo[0][1] == tsumo[2][0]) ||//ABACBB
		     isABAC(tsumo[0], tsumo[1]) ||
		     (zoro[1] && zoro[2] && !inColor(tsumo[0], tsumo[1][0]) && tsumo[0][1] == tsumo[2][0]) //ABCCBB

		     )) {
		    col[0] = i+1;
		    col[1] = i;
		    row[0] = height[i+1];
		    row[1] = height[i];
		    
		    dodai[i] = 1;
		    dodaiColor[i] = tsumo[0][1];
		    dodai[i+1] = 1;
		    dodaiColor[i+1] = tsumo[0][0];
		    return ret;
		}
		if ( i == 0 || tsumo[0][0] != dodaiColor[i-1]) {
		    col[0] = i;
		    col[1] = i+1;
		    row[0] = height[i];
		    row[1] = height[i+1];
		    
		    dodai[i] = 1;
		    dodai[i+1] = 1;
		    dodaiColor[i] = tsumo[0][0];
		    dodaiColor[i+1] = tsumo[0][1];
		    return ret;
		}
	    }
	}

	col[0] = col[1] = 0;
	row[0] = height[0];
	row[1] = height[0];

	/*
	for (int i = 0; i < Field.MAX_WIDTH; i++) {
	    if (thisTsumo[1] == dodaiColor) {
		
	    }
	}
	
	int[] colorList = new int[PuyoColor.MAX_NUM];
	for ( int i = 0; i < 2; i++ ) {
	    colorList[thisTsumo[i]]++;
	    colorList[nextTsumo[i]]++;
	    colorList[nxnxTsumo[i]]++;
	}
	*/

	return ret;

    }
    public boolean isSame(int[] tsumo1, int[] tsumo2) {
	return tsumo1[0] == tsumo2[0] && tsumo1[1] == tsumo2[1]
	    || tsumo1[1] == tsumo2[0] && tsumo1[0] == tsumo2[1];
    }
    public boolean inColor(int[] tsumo, int c) {
	return tsumo[0] == c || tsumo[1] == c;
    }
    public boolean isABAC(int[] tsumo1, int[] tsumo2) {
	if (tsumo1[0] == tsumo2[0]) {//ABAC
	    return tsumo1[0] != tsumo2[1] && tsumo1[1] != tsumo2[1];
	}
	if (tsumo1[0] == tsumo2[1]) {//ABCA
	    return tsumo1[0] != tsumo2[0] && tsumo1[1] != tsumo2[0];
	}
	return false;
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
