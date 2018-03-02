/* Todo:
 * 埋まった時の消去
 * ランダム -> 埋め回避
 * 非3土台、仕掛け2以上
 * 仕掛けを巻き込まないと消せない
 */

import java.util.ArrayList;
import java.util.Random;

import SnesInterface.*;
import GlobalFunc.Field;
//import Action;

public class Nobashi {
    public int scoreCalcNum = 0;
    public int tsumoListNum = 0;

    private boolean[][] chainEnd = new boolean[Field.MAX_HEIGHT][Field.MAX_WIDTH];
    private int numChainEnd = 0;
    private int colorChainEnd = 0;

    private int[][] chainNum = new int[Field.MAX_HEIGHT][Field.MAX_WIDTH];
    private int maxChainNum = 0;

    private boolean[][] chainFire = new boolean[Field.MAX_HEIGHT][Field.MAX_WIDTH];
    private boolean isSetDevice = false;
    private int device_x = -1;
    private int device_y = -1;

    private int[] dodai = new int[Field.MAX_WIDTH];
    private int[] sikake = new int[Field.MAX_WIDTH];
    private int[] dodaiColor = new int[Field.MAX_WIDTH];
    //int cnt_scoreCalc = 0;

    public int[][] connectField = new int[Field.MAX_HEIGHT][Field.MAX_WIDTH];
	
    public Nobashi() {
	System.out.println("using Nobashi-hardcoding algorithm.");
    }

    public static void main(String args[]) {
	if (args.length < 1) {
	    System.out.println("input player number: 1 or 2");
	    return;
	}
	int player = Character.getNumericValue(args[0].charAt(0));
	player--;//1 to 0, 2 to 1
	if (player < 0 || 1 < player ) {
	    System.out.println("input player number: 1 or 2");
	    return;
	}

	int depth = 3;
	int threshold = 2100;

	while(true) {
	    Nobashi ai = new Nobashi();
	    DelPotentialKai potential = new DelPotentialKai();
	    SnesInterface ioface = new SnesInterface(player);

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

		//field settings
		stat.setPuyo2Field(ioface);
		
		nxnx[0] = stat.nxnxTsumo[0];
		nxnx[1] = stat.nxnxTsumo[1];
		stat.nxnxTsumo[0] = PuyoColor.EMPTY;
		stat.nxnxTsumo[1] = PuyoColor.EMPTY;
		
		//long start = System.currentTimeMillis();
		float[] score;
		score = potential.decidePutLocale(stat, col, row, 1);
		if (score[0] < threshold) {
		    score = ai.decidePutLocale(stat, col, row, 2);
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
		
		//stat.setPuyo2Field(col, row);
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
    /**
     * Calculate evalutions on each action, return put locale and score.
     * @param stat Player's status
     * @param col pair of colmuns of put locations
     * @param row pair of rows of put locations
     * @param depth search depth (invalid)
     * @return score of decided action: float 3 dimension array:
     *   [score, number of max chain, total number of deletion]
     *   Null is returned when error occured.
     */
    public float[] decidePutLocale(PlayerStat stat, int[] col, int[] row, int depth) {

	if (depth < 1) {
	    System.out.println("invalid depth:" + depth);
	    return null;
	}

	float[] ret = {.0F, .0F, 1.0F};

	int[][] delCount = Score.initDelCount();
	Score.setConnectAll(stat.field, this.connectField, delCount);
	//Field.printData(ai.connectField);
	
	/*
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
	*/

	Action[] actionList = Action.getActionFullList(stat);
	float[] eval = new float[actionList.length];

	if (this.numChainEnd < 1) {
	    Action act = Action.getRandomOne(actionList);
	    act.put(stat.field);
	    this.chainEnd[act.y[0]][act.x[0]] = true;
	    this.numChainEnd++;
	    this.colorChainEnd = act.color[0];
	    
	    if(act.color[0] == act.color[1]) {
		this.chainEnd[act.y[1]][act.x[1]] = true;
		this.numChainEnd++;
	    }

	    col[0] = act.x[0];
	    col[1] = act.x[1];
	    row[0] = act.y[0];
	    row[1] = act.y[1];

	    return ret;
	}
	
	evalNotConnectToChain(stat, actionList, eval);
	actionList = Action.getMaxList(actionList, eval);
	eval = new float[actionList.length];

	evalNotDeletion(stat, actionList, eval);
	actionList = Action.getMaxList(actionList, eval);
	eval = new float[actionList.length];

	
	/*
	//remove extra connection
	evalChainExtra(stat, actionList, eval);
	actionList = Action.getMaxList(actionList, eval);
	eval = new float[actionList.length];
	*/
	
	evalChainEnd(stat, actionList, eval);
	evalChainFire(stat, actionList, eval);
	evalChainDevice(stat, actionList, eval);
	actionList = Action.getMaxList(actionList, eval);
	
	Action act = Action.getRandomOne(actionList);
	act.put(stat.field);
	int ex_numChainEnd = this.numChainEnd;
	addChainEnd(stat, act);
	//printField(this.chainFire);
	setIsSetDevice(stat, act);
	System.out.println("chainEnd: c" + this.colorChainEnd + ", n" + this.numChainEnd);
	System.out.println("setDevice: " + isSetDevice);
	if ( this.numChainEnd == 3) {
	    if (ex_numChainEnd != this.numChainEnd) {
		setChainFire(stat, act);
	    } else if (this.isSetDevice) {
		increaseChain(stat);
		/*
		 * 1 1
		 * 11233
		 *
		 */
		
	    }
	    
	}

	col[0] = act.x[0];
	col[1] = act.x[1];
	row[0] = act.y[0];
	row[1] = act.y[1];

	return ret;

    }
    public boolean isSame(int[] tsumo1, int[] tsumo2) {
	return tsumo1[0] == tsumo2[0] && tsumo1[1] == tsumo2[1]
	    || tsumo1[1] == tsumo2[0] && tsumo1[0] == tsumo2[1];
    }
    public boolean inColor(int[] tsumo, int c) {
	return tsumo[0] == c || tsumo[1] == c;
    }
    /*
    public boolean isABAC(int[] tsumo1, int[] tsumo2) {
	if (tsumo1[0] == tsumo2[0]) {//ABAC
	    return tsumo1[0] != tsumo2[1] && tsumo1[1] != tsumo2[1];
	}
	if (tsumo1[0] == tsumo2[1]) {//ABCA
	    return tsumo1[0] != tsumo2[0] && tsumo1[1] != tsumo2[0];
	}
	return false;
    }
    */
    public void resetField(boolean[][] field) {
	for (int i = 0; i < Field.MAX_HEIGHT; i++) {
	    for (int j = 0; j < Field.MAX_WIDTH; j++) {
		field[i][j] = false;
	    }
	}
    }
    public boolean isTrue(boolean[][] field, int y, int x) {
	if ( y < 0 || field.length <= y ) { return false; }
	if ( x < 0 || field[y].length <= x ) { return false; }
	return field[y][x];
    }
    public boolean isColor(int[][] field, int y, int x, int color) {
	if ( y < 0 || field.length <= y ) { return false; }
	if ( x < 0 || field[y].length <= x ) { return false; }
	return field[y][x] == color;
    }
    public int getConnect(int[][] field, int y, int x, int color) {
	int connect = 1;
	if (isColor(field, y  , x-1, color)) {
	    connect += connectField[ y ][x-1];
	}
	if (isColor(field, y  , x+1, color)) {
	    connect += connectField[ y ][x+1];
	}
	if (isColor(field, y-1, x  , color)) {
	    connect += connectField[y-1][ x ];
	}
	return connect;	
    }
    public int getConnect(int[][] field, Action action, int i) {
	if (i < 0 || 1 < i) { return -1; }
	boolean zoro = action.color[0] == action.color[1];
	int c = getConnect(field, action.y[i], action.x[i], action.color[i]);
	if (zoro) { c++; }
	return c;
    }
    public int countAdjoinEmpty(int[][] field, int y, int x) {
	int count = 0;
	if (isColor(field, y+1, x,   PuyoColor.EMPTY)) { count++; }
	if (isColor(field, y,   x-1, PuyoColor.EMPTY)) { count++; }
	if (isColor(field, y,   x+1, PuyoColor.EMPTY)) { count++; }
	if (isColor(field, y-1, x,   PuyoColor.EMPTY)) { count++; }
	return count;
    }
    public boolean canFire(int[][] field, boolean[][] checked, int y, int x) {
	if (y < 0 || Field.MAX_HEIGHT <= y || x < 0 || Field.MAX_WIDTH <= x) {
	    return false;
	}
	if (checked[y][x]) { return false; }
	checked[y][x] = true;
	if ( 0 < countAdjoinEmpty(field, y, x) ) { return true; }
	
	int c = field[y][x];	
	return
	    isColor(field, y+1, x,   c) && canFire(field, checked, y+1, x  ) ||
	    isColor(field, y,   x-1, c) && canFire(field, checked, y,   x-1) ||
	    isColor(field, y,   x+1, c) && canFire(field, checked, y,   x+1) ||
	    isColor(field, y-1, x,   c) && canFire(field, checked, y-1, x  );
    }
    
    public void evalChainExtra(PlayerStat stat, Action[] actionList, float[] eval) {
	if (this.maxChainNum < 1) {
	    return;
	}
	for (int i = 0; i < actionList.length; i++) {
	    int val = 0;
	    for (int j = 0; j < 2; j++) {
		int y = actionList[i].y[j];
		int x = actionList[i].x[j];
		int c = actionList[i].color[j];
		if ((isColor(this.chainNum, y  , x-1, 0) || !isColor(stat.field, y  , x-1, c)) &&
		    (isColor(this.chainNum, y  , x+1, 0) || !isColor(stat.field, y  , x+1, c)) &&
		    (isColor(this.chainNum, y-1, x  , 0) || !isColor(stat.field, y-1, x  , c))) {
		    val++;
		}
	    }
	    eval[i] += (float)val;
	}
    }

    public void evalNotDeletion(PlayerStat stat, Action[] actionList, float[] eval) {
	
	for (int i = 0; i < actionList.length; i++) {
	    boolean del = false;
	    for (int j = 0; j < 2; j++) {
		int y = actionList[i].y[j];
		int x = actionList[i].x[j];
		int color = actionList[i].color[j];
		int[][] mask = new int[Field.MAX_HEIGHT][];
		for (int k = 0; k < Field.MAX_HEIGHT; k++) {
		    mask[k] = new int[Field.MAX_WIDTH];
		}
		if ( 4 <= getConnect(stat.field, y, x, color) ) {
		    del = true;
		}
	    }
	    if (!del) {
		eval[i] += 1.0;
	    }
	}

    }
    public void evalNotConnectToChain(PlayerStat stat, Action[] actionList, float[] eval) {
	for(int i = 0; i < actionList.length; i++) {
	    boolean connect = false;
	    for(int j = 0; j < 2; j++) {
		int y = actionList[i].y[j];
		int x = actionList[i].x[j];
		int color = actionList[i].color[j];
		if(isColor(stat.field, y-1, x, color) && 0 < chainNum[y-1][x]) {
		    connect = true;
		} else if(isColor(stat.field, y, x-1, color) && 0 < chainNum[y][x-1]) {
		    connect = true;
		} else if(isColor(stat.field, y, x+1, color) && 0 < chainNum[y][x+1]) {
		    connect = true;
		}
	    }
	    if(!connect) {
		eval[i] += 1.0;
	    }
	}
    }

    public void evalChainEnd(PlayerStat stat, Action[] actionList, float[] eval) {
	int endColor = this.colorChainEnd;
	int endNum = this.numChainEnd;
	boolean zoro = actionList[0].color[0] == actionList[0].color[1];

	if (endNum < 1 || 3 < endNum) { return; }
	if (actionList[0].color[0] != endColor &&
	    actionList[0].color[1] != endColor) {
	    return;
	}

	for (int i = 0; i < actionList.length; i++) {
	    int val = 0;
	    for (int j = 0; j < 2; j++) {
		int color = actionList[0].color[j];
		if (color == endColor) {
		    int y = actionList[i].y[j];
		    int x = actionList[i].x[j];
		    if (isTrue(this.chainEnd, y  , x-1) ||
			isTrue(this.chainEnd, y  , x+1) ||
			isTrue(this.chainEnd, y-1, x  ) ) {
			val = getConnect(stat.field, actionList[i], j);
		    }
		}
	    }
	    if (0 < val && val < 4) {
		eval[i] += (float) (val - this.numChainEnd);
	    }
	}

    }

    public void evalChainFire(PlayerStat stat, Action[] actionList, float[] eval) {
	//numChainFire == 0
	if (actionList[0].color[0] == this.colorChainEnd &&
	    actionList[0].color[1] == this.colorChainEnd) {
	    return;
	}
	for (int i = 0; i < actionList.length; i++) {
	    int val = 0;
	    for (int j = 0; j < 2; j++) {
		int y = actionList[i].y[j];
		int x = actionList[i].x[j];
		if (isTrue(this.chainFire, y, x) &&
		    actionList[i].color[j] != this.colorChainEnd ) {
		    val++;
		}
	    }
	    eval[i] += (float)val;
	}
    }

    public void evalChainDevice(PlayerStat stat, Action[] actionList, float[] eval) {
	//Todo: next fire point won't connect more
	if (this.numChainEnd < 1) { return; }
	if (actionList[0].color[0] != this.colorChainEnd &&
	    actionList[0].color[1] != this.colorChainEnd) {
	    return;
	}
	for (int i = 0; i < actionList.length; i++) {
	    int val = 0;
	    int[][] field = Field.copyField(stat.field);
	    boolean[][] mask = new boolean[Field.MAX_HEIGHT][Field.MAX_WIDTH];
	    actionList[i].put(field);
	    for (int j = 0; j < 2; j++) {
		int y = actionList[i].y[j];
		int x = actionList[i].x[j];
		if (actionList[i].color[j] == this.colorChainEnd &&
		    isTrue(this.chainFire, y-1, x) &&
		    !isTrue(this.chainFire, y , x) &&
		    canFire(field, mask, y-1, x) ) {
			val++;
			System.out.println("device: (" + x + "," + y + "): " + val);
		}
	    }
	    eval[i] += (float)val;
	}
    }

    /*
    public int getMaxChainNum() {
	int max = 0;
	for (int i = 0; i < Field.MAX_HEIGHT; i++) {
	    for (int j = 0; j < Field.MAX_WIDTH; j++) {
		if (max < chainNum[i][j]) {
		    max = chainNum[i][j];
		}
	    }
	}
	return max;
    }
    */
    
    public void addChainEnd(PlayerStat stat, Action action) {
	int endColor = this.colorChainEnd;
	if (3 < this.numChainEnd) { return; }

	for (int j = 0; j < 2; j++) {
	    if (action.color[j] == this.colorChainEnd) {
		int y = action.y[j];
		int x = action.x[j];
		if ( //isTrue(this.chainEnd, y+1, x  ) ||
		    isTrue(this.chainEnd, y  , x-1) ||
		    isTrue(this.chainEnd, y  , x+1) ||
		    isTrue(this.chainEnd, y-1, x  ) ) {
		    this.chainEnd[action.y[j]][action.x[j]] = true;
		    this.numChainEnd++;

		}
	    }
	}
    }

    /**
     * Change chainFire AFTER set chainEnd.
     *
     */
    public void addChainFire(PlayerStat stat, Action action) {
	for (int j = 0; j < 2; j++) {
	    int x = action.x[j];
	    int y = action.y[j];

	    if (isTrue(this.chainEnd, y, x) ) {
		this.chainFire[y][x] = false;
		if ( this.chainNum[y][x-1] == 0 ) {
		    this.chainFire[y][x] = true;
		}
		if ( this.chainNum[y][x+1] == 0 ) {
		    this.chainFire[y][x] = true;
		}
		if ( this.chainNum[y-1][x] == 0 ) {
		    this.chainFire[y][x] = true;
		}
	    }
	}
    }

    public void setChainFire(PlayerStat stat, Action action) {
	for(int i = 0; i < Field.MAX_HEIGHT; i++) {
	    for (int j = 0; j < Field.MAX_WIDTH; j++) {
		this.chainFire[i][j] = false;
	    }
	}

	for(int i = 0; i < Field.MAX_HEIGHT; i++) {
	    for (int j = 0; j < Field.MAX_WIDTH; j++) {
		if (isTrue(this.chainEnd, i, j) ) {
		    if (isColor(this.chainNum, i, j-1, 0) &&
			!isTrue(this.chainEnd, i, j-1) ) {
			this.chainFire[i  ][j-1] = true;
		    }
		    if (isColor(this.chainNum, i, j+1, 0) &&
			!isTrue(this.chainEnd, i, j+1) ) {
			this.chainFire[i  ][j+1] = true;
		    }
		    if (isColor(this.chainNum, i+1, j, 0) &&
			 !isTrue(this.chainEnd, i+1, j) ) {
			this.chainFire[i+1][j  ] = true;
		    }
		}
	    }
	}
    }

    public void setIsSetDevice(PlayerStat stat, Action action) {
	if (isSetDevice) { return; }

	boolean[][] mask = new boolean[Field.MAX_HEIGHT][Field.MAX_WIDTH];
	for (int j = 0; j < 2; j++) {
	    int x = action.x[j];
	    int y = action.y[j];
	    if (action.color[j] == this.colorChainEnd &&
		isTrue(this.chainFire, y-1, x) &&
		canFire(stat.field, mask, y-1, x)) {
		this.isSetDevice = true;
		device_x = x;
		device_y = y;
	    }
	}
    }
    
    public void increaseChain(PlayerStat stat) {
	
	//setting chainNum
	this.maxChainNum++;
	for (int i = 0; i < Field.MAX_HEIGHT; i++) {
	    for (int j = 0; j < Field.MAX_WIDTH; j++) {
		if (this.chainEnd[i][j]) {
		    this.chainNum[i][j] = this.maxChainNum;
		}
		if (stat.field[i][j] == this.colorChainEnd &&
		    isTrue(this.chainFire, i-1, j)) {
		    this.chainNum[i][j] = this.maxChainNum;
		    //device_x = j;
		    //device_y = i;
		}
	    }
	}
	this.chainNum[device_y][device_x] = this.maxChainNum;

	//setting new chainEnd
	int end_y = -1;//device_y - cnt
	int end_x = device_x;
	for(int i = 0; i < Field.MAX_HEIGHT; i++) {
	    if(chainFire[i][device_x] ) {
		end_y = i;
		break;
	    }
	}
	if (end_x < 0 || end_y < 0) {
	    System.err.println("increaseChain: invalid axis of device.");
	    System.exit(-1);
	}
	resetField(this.chainEnd);	
	this.colorChainEnd = stat.field[end_y][end_x];
	this.numChainEnd = Score.setConnectMask(stat.field, chainEnd, end_x, end_y);

	//reset chainFire
	resetField(this.chainFire);	

	this.isSetDevice = false;
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

    static public void printField(boolean field[][]) {
	for (int i = field.length-1; i >= 0; i--) {
	    for (int j = 0; j < field[0].length; j++) {
		if (field[i][j]) {
		    System.out.print(" T");
		} else {
		    System.out.print(" F");
		}
	    }
	    System.out.println();
	}
		
    }
}
