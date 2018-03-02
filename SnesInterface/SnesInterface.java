package SnesInterface;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.util.Random;

import java.lang.InterruptedException;
import java.lang.Thread;

public class SnesInterface {
    public static final int MAX_WIDTH = 6;
    public static final int MAX_HEIGHT = 12;//todo: change to 12
    private CaptureScreen captureScreen;
    private Robot robot;//for inputting key
    private int player = 1;
    public SnesKeyCode snesKeyCode = new SnesKeyCode(player);
    //todo: インターフェイスの実体は一つのみか？
    //キー操作は一つのロボットで統一すべきか？

    public SnesInterface() {
	captureScreen = new CaptureScreen(FieldCoordinates.screen_w, FieldCoordinates.screen_h);
	try {
	    robot = new Robot();
	} catch(AWTException e) {
	    throw new RuntimeException(e);
	}
    }
    public SnesInterface(CaptureScreen cs) {
	this.captureScreen = cs;
	try {
	    robot = new Robot();
	} catch(AWTException e) {
	    throw new RuntimeException(e);
	}
    }

    public SnesInterface(int player) {
	this();
	this.player = player;
	snesKeyCode = new SnesKeyCode(player);
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
	printData(data);

	/*
	JFrame frame = new JFrame("sub image");
	frame.setBounds(1200, 700, 32, 54);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JTable table = new JTable(data[0]);
	frame.getContentPane().add(table);
	frame.setVisible(true);
	*/


    }

    public static void printData(int[][][] data) {
	
	for (int i = MAX_HEIGHT; i >= 0 ; i-- ) {
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
	
	for (int i = MAX_HEIGHT; i >= 0 ; i-- ) {
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
	    for (int y = 0; y < MAX_HEIGHT; y++) {
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
	    for (int y = 0; y < MAX_HEIGHT; y++) {
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

    public int[][] getNextPuyo() {
	captureScreen.getCaptureImage();
	BufferedImage subImage;
	IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	int[][] ret = new int[2][];
	for (int p = 0; p < 2; p++) {
	    ret[p] = new int[2];
       
	    int[][] c = FieldCoordinates.getNext(p);
	    for (int i = 0; i < 2; i++) {
		subImage = captureScreen.getImage(c[i]);
		identPuyoColor.getPuyoColor(subImage);
		ret[p][i] = identPuyoColor.idx;

		//identPuyoColor.dist
	    }
	}
	return ret;
    }


    public int[][] getNxnxPuyo() {
	captureScreen.getCaptureImage();
	BufferedImage subImage;
	IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	int[][] ret = new int[2][];
	for (int p = 0; p < 2; p++) {
	    ret[p] = new int[2];
	    
	    int[][] c = FieldCoordinates.getNxnx(p);
	    for (int i = 0; i < 2; i++) {
		subImage = captureScreen.getImage(c[i]);
		identPuyoColor.getPuyoColor(subImage);
		ret[p][i] = identPuyoColor.idx;

		//identPuyoColor.dist
	    }
	}
	return ret;
    }

    public boolean isFieldEmpty(int p) {
	int[][][] field = getFieldPuyo();
	for (int y = 0; y < MAX_HEIGHT; y++) {
	    for (int x = 0; x < MAX_WIDTH; x++) {
		if ( field[p][y][x] != PuyoColor.EMPTY ) {
		    //System.out.println(x + ", " + y);
		    return false;
		}
	    }
	}
	return true;
    }

    //配ぷよを待ち、色をセット
    //リターンはスレッドの同期待ち用
    public boolean waitPuyoGiven(int p, int[] thisPuyo, int[] nextPuyo, int[] nxnxPuyo) {
	int cnt = 0;
	/****  animation flow *****
	  1. next <= EMPTY : 15 frame
	     nxnx == nxnx
	  2. next <= nxnx
	     nxnx <= given
	****************************/

	cnt = 0;
	while(getNextPuyo()[p][0] != PuyoColor.EMPTY) {
	    cnt++;
	    
	    try {
		Thread.sleep(50);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	    if (cnt > 10) { return false; }

	}
	//System.out.println("Empty OK(" + cnt + "):" + nextPuyo[0] + nextPuyo[1]);
	int[] buf = getNextPuyo()[p];
	cnt = 0;
	while (nxnxPuyo[0] != buf[0] ||
	       nxnxPuyo[1] != buf[1] ) {
	    buf = getNextPuyo()[p];
	    cnt++;
	    
	    try {
		Thread.sleep(50);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	    
	}
	//System.out.println("Next OK(" + cnt + ")");
	cnt = 0;
	do { 
	    buf = getNxnxPuyo()[p];
	    cnt++;
	} while(buf[0] == PuyoColor.EMPTY);
	//System.out.println("Nxnx OK(" + cnt + "):" + buf[0] + buf[1]);
	for (int i = 0; i < 2; i++) {
	    thisPuyo[i] = nextPuyo[i];
	    nextPuyo[i] = nxnxPuyo[i];
	    nxnxPuyo[i] = buf[i];
	}
	return true;
    }

    public int[] getHeight(int[][] data) {
	int[] height = new int[MAX_WIDTH];

	int y;
	for (int p = 0; p < 2; p++) {
	    for (int x = 0; x < MAX_WIDTH; x++) {
		if(data[0][x] == PuyoColor.EMPTY) {
		    height[x] = 0;
		    continue;
		}
		for (y = 1; y < MAX_HEIGHT; y++ ) {
		    if(data[y][x] == PuyoColor.EMPTY) {
			y++;
			break;
		    }
		}
		height[x] = y;
	    }
	}
	return height;
    }

    public int[][] getHeight(int[][][] data) {
	int[][] height = new int[2][];
	height[0] = new int[MAX_WIDTH];
	height[1] = new int[MAX_WIDTH];

	int y;
	for (int p = 0; p < 2; p++) {
	    for (int x = 0; x < MAX_WIDTH; x++) {
		if(data[p][0][x] == PuyoColor.EMPTY ||
		   data[p][0][x] == -1
		   ) {
		    height[p][x] = 0;
		    continue;
		}
		for (y = 1; y < MAX_HEIGHT; y++ ) {
		    if(data[p][y][x] == PuyoColor.EMPTY ||
		   data[p][0][x] == -1) {
			break;
		    }
		}
		height[p][x] = y;
	    }
	}
	return height;
    }

    public int[][] getDifferenceField(int[][] field1, int[][] field2) {
	int[][] difference = new int[MAX_HEIGHT][];
	for (int i = 0; i < MAX_HEIGHT; i++) {
	    difference[i] = new int[MAX_WIDTH];
	}

	for (int y = 0; y < MAX_HEIGHT; y++) {
	    for (int x = 0; x < MAX_WIDTH; x++) {
		if (field1[y][x] != field2[y][x] 
		    //&& field2[y][x] >= 0
		    ) {
		    difference[y][x] = 1;
		}
	    }
	}
	return difference;
    }

    public int[][][] getDifferenceField(int[][][] field1, int[][][] field2) {
	int[][][] difference = new int[2][][];
	difference[0] = getDifferenceField(field1[0], field2[0]);
	difference[1] = getDifferenceField(field1[1], field2[1]);
	return difference;
    }

    public boolean isFlownPuyo(int[][] field, int[] height, int x) {
	for (int y = height[x]; y < MAX_HEIGHT; y++) {
	    if (field[y][x] != PuyoColor.EMPTY) {
		return true;
	    }
	}
	return false;
    }

    //落下中ぷよの行数を取得
    //todo: 全消し時
    public int getFlownPuyoCol(int p, int[] height) {
	int[][] field = getFieldPuyo()[p];
        int[] exist = new int[MAX_WIDTH];
	for (int x = 0; x < MAX_WIDTH; x++) {
	    for (int y = height[x]; y < MAX_HEIGHT; y++) {
		//todo: use function "isFlownPuyo()"
		if (field[y][x] != PuyoColor.EMPTY) {
		    exist[x] = 1;
		    break;
		}
	    }
	}
	int sum = 0;
	int col = -1;//初期値：存在しない
	for (int x = 0; x < MAX_WIDTH; x++) {
	    if (exist[x] > 0) {
		sum += exist[x];
		col = x;
	    }
	}
	if (sum > 1) { col = -1; }//複数行
	return col;
    }

   private void horizontalMove(int[] height, int col) {
	//軸ぷよを基準に横移動
	int move;
	int x = 2;//3行目
	int cnt = 0;
        boolean flag = false;
	move = col - x;
	if ( 0 < move ) {
	    for (int i = 0; i < move; i++) {
		OneButton(snesKeyCode.RIGHT);
	    }
	} else {
	    for (int i = 0; i > move; i--) {
		OneButton(snesKeyCode.LEFT);
	    }
	}
   }

    //todo: boolean
    private void horizontalMoveFeedback(int[] height, int col) {

	//軸ぷよを基準に横移動
	int move;
	int x = 2;//3行目
	int cnt = 0;
        boolean flag = false;
	while (x != col) {
	    move = col - x;
	    if ( 0 < move ) {
		for (int i = 0; i < move; i++) {
		    OneButton(snesKeyCode.RIGHT);
		}
	    } else {
		for (int i = 0; i > move; i--) {
		    OneButton(snesKeyCode.LEFT);
		}
	    }
	    //todo: 置けなかった場合の無限ループ
	    //ルート探索で完全に予知可能か？
	    do {
		//描画待ち
		try {
		    Thread.sleep(30);
		} catch(InterruptedException e ) {
		    throw new RuntimeException(e);
		}

		x = getFlownPuyoCol(1, height);
		cnt++;
	    } while (x < 0);
	    //System.out.println("col " + x);
	}
	//System.out.println("done:" + cnt);
    }
    public void rotationMove(int[] height, int[] col, int[] row) {
	//todo: 横が壁の場合、クイックターン
	int cnt = 0;

	//rotation
	int rot = 0;//+rot_r / -rot_l
	if (col[0] < col[1]) { rot = -1; }
	else if (col[0] > col[1]) { rot = 1; }
	else if (row[0] < row[1]) {
	    if (col[0] == 0) { rot = 2; }
	    else { rot = -2; }
	}

	while(true) {
	    if (rot > 0) {
		OneButton(snesKeyCode.ROT_R);
		rot--;
	    } else if (rot < 0) {
		OneButton(snesKeyCode.ROT_L);
		rot++;
	    } else {
		break;
	    }
	}
    }

    public void rotationMoveFeedback(int[] height, int[] col, int[] row) {
	//todo: 横が壁の場合、クイックターン
	int cnt = 0;

	//rotation
	int rot = 0;//+rot_r / -rot_l
	if (col[0] < col[1]) { rot = -1; }
	else if (col[0] > col[1]) { rot = 1; }
	else if (row[0] < row[1]) {
	    if (col[0] == 0) { rot = 2; }
	    else { rot = -2; }
	}

	//0回転
	if(rot == 0) { return; }//todo: 下キー

	//1回転
	int checkCol = -1;
	cnt = 0;
	while (true) {
	    cnt++;
	    if (rot > 0) {
		OneButton(snesKeyCode.ROT_R);
		checkCol = col[1] + 1;
	    } else {
		OneButton(snesKeyCode.ROT_L);
		checkCol = col[1] - 1;
	    }
	    //描画待ち(8 frame)：回転中、一部でも行内にあれば良いパターン
	    try {
		Thread.sleep(100);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	    if ( isFlownPuyo(getFieldPuyo()[player], height, checkCol) ) {
		break;
	    }
	}
	//System.out.println("rot1 OK:" + cnt);
	if(rot == 1 || rot == -1) { return; }

	//2回転
	cnt = 0;
	while (true) {
	    cnt++;
	    if (rot > 0) {
		OneButton(snesKeyCode.ROT_R);
	    } else {
		OneButton(snesKeyCode.ROT_L);
	    }
	    //描画待ち：回転終了後、ぷよがあってはならないパターン
	    try {
		Thread.sleep(150);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }

	    if ( !isFlownPuyo(getFieldPuyo()[player], height, checkCol) ) {
		break;
	    }
	}
	//System.out.println("rot2 OK:" + cnt);
    }

    public boolean putPuyo(int[] col, int[] row) {
	/* col[1]:軸ぷよ（下）
	   col[0]:子ぷよ（上） */

	int cnt = 0;
	//todo: ぷよの上下
	//高さ取得
	int[][] height;
	int[][][] ex_data;
	int[] ex_height;
	int [][][] difference;
	ex_data = getFieldPuyo();
	ex_height = getHeight(ex_data)[player];

	//todo: search route and return false

	/*
	printData(ex_data);
	for (int i = 0; i < MAX_WIDTH; i++) {
	    System.out.print(" " + ex_height[i]);
	}
	System.out.println();
	*/

	horizontalMove(ex_height, col[1]);
	rotationMove(ex_height, col, row);

	DownStart();
	try {
	    Thread.sleep(500);
	} catch(InterruptedException e ) {
	    throw new RuntimeException(e);
	}
	DownStop();

	/*
	//高さが増加するまで下キー入れ
	System.out.println("下入れ");
	try {
	    robot.keyPress(snesKeyCode.DOWN);
	} catch(IllegalArgumentException e) {
	    System.out.println("invalid key code.");
	}
	do {
	    robot.delay(100);
	    height = getHeight(getFieldPuyo());
	    for (int i = 0; i < 6; i++) {
		System.out.print(" " + height[1][i]);
	    }
	    System.out.println();
	} while(ex_height[0] == height[1][col[0]] ||
		ex_height[1] == height[1][col[1]] ||
		height[1][2] < 4
		);
	try {
	    robot.keyRelease(snesKeyCode.DOWN);
	} catch(IllegalArgumentException e) {
	    System.out.println("invalid key code.");
	}
	System.out.println("下リリース");
	*/
	return true;
    }

    //Todo: capsule
    public boolean OneButton(int key) {
	//移動は2フレーム
	try {
	    robot.keyPress(key);
	    robot.delay(20);
	    robot.keyRelease(key);
	    robot.delay(20);
	} catch(IllegalArgumentException e) {
	    System.out.println("invalid key code: " + key);
	}
	return true;
    }

    public void DownStart() {
	try {
	    robot.keyPress(snesKeyCode.DOWN);
	} catch(IllegalArgumentException e) {
	    System.out.println(e);
	}
    }
    public void DownStop() {
	try {
	    robot.keyRelease(snesKeyCode.DOWN);
	} catch(IllegalArgumentException e) {
	    System.out.println(e);
	}
    }
    
    public boolean waitSelectLevel() {
	boolean isReady = false;
	while(!isReady) {
	    captureScreen.getCaptureImage();
	    BufferedImage subImage;
	    IdentPuyoColor identPuyoColor = new IdentPuyoColor();
	    int[] c = FieldCoordinates.selectLevel[player];
	    subImage = captureScreen.getImage(c);
	    int w = c[2];
	    int h = c[3];
	    int[][] r = new int[h][];
	    int dist;
	    int idx_RGB = (player == 0) ? 0 : 2;
	    for (int y = 0; y < h; y++) {
		r[y] = new int[w];
		for (int x = 0; x < w; x++) {
		    r[y][x] = identPuyoColor.RGB2Ary(subImage.getRGB(x, y))[idx_RGB];
		    //System.out.print(" " + r[y][x]);
		}
		//System.out.println();
	    }
	    dist = identPuyoColor.getSquareDist(r, PuyoColor.levelR);
	    //System.out.println(" " + dist);
	    if(dist < 200000) {
		break;
	    }
	
	    try {
		Thread.sleep(100);
	    } catch(InterruptedException e ) {
		throw new RuntimeException(e);
	    }
	}

	return true;	
    }
}
    
    



