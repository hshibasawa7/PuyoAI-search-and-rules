import SnesInterface.*;
import GlobalFunc.Field;

public class PlayerStat implements Cloneable {
    public int player = 0;
    public int[][] field;
    //public int[] height;

    //todo: change into future tsumo list
    public int[] thisTsumo = new int[2];
    public int[] nextTsumo = new int[2];
    public int[] nxnxTsumo = new int[2];
    public boolean[] colorVariation = new boolean[PuyoColor.MAX_NUM];

    public boolean allClear = true;
    public int score;
    //public boolean deleting = false;


    public PlayerStat() {
	field = new int[Field.MAX_HEIGHT][];
	for (int i = 0; i < Field.MAX_HEIGHT; i++) {
	    field[i] = new int[Field.MAX_WIDTH];
	}
    }

    public PlayerStat(int player) {
	this();
	this.player = player;
    }

    public PlayerStat(int[] thisTsumo, int[] nextTsumo, int[] nxnxTsumo) {
	this();
	this.thisTsumo = thisTsumo;
	setColorVari(thisTsumo);
	this.nextTsumo = nextTsumo;
	setColorVari(nextTsumo);
	this.nxnxTsumo = nxnxTsumo;
	setColorVari(nxnxTsumo);
    }
    public PlayerStat(int[] thisTsumo, int[] nextTsumo, int[] nxnxTsumo, int player) {
	this(thisTsumo, nextTsumo, nxnxTsumo);
	this.player = player;
    }

    public PlayerStat clone() {
	PlayerStat newStat = null;
	try {
	    newStat = (PlayerStat)super.clone();
	} catch(CloneNotSupportedException e) {
	    System.err.println(e);
	    throw new RuntimeException();
	}
	newStat.field = Field.copyField(this.field);

	newStat.thisTsumo = new int[2];
	newStat.nextTsumo = new int[2];
	newStat.nxnxTsumo = new int[2];
	for(int i = 0; i < 2; i++) {
	    newStat.thisTsumo[i] = this.thisTsumo[i];
	    newStat.nextTsumo[i] = this.nextTsumo[i];
	    newStat.nxnxTsumo[i] = this.nxnxTsumo[i];
	}

	newStat.colorVariation = new boolean[PuyoColor.MAX_NUM];
	for (int i = 0; i < PuyoColor.MAX_NUM; i++) {
	    newStat.colorVariation[i] = this.colorVariation[i];
	}
	return newStat;
    }

    public void setColorVari(int[] tsumo) {
	colorVariation[tsumo[0]] = true;
	colorVariation[tsumo[1]] = true;
    }
    public void addColorVari() {
	colorVariation[nxnxTsumo[0]] = true;
	colorVariation[nxnxTsumo[1]] = true;
    }
    public int getNumColor() {
	int cnt = 0;
	if (colorVariation[PuyoColor.RED]) { cnt++; }
	if (colorVariation[PuyoColor.BLUE]) { cnt++; }
	if (colorVariation[PuyoColor.YELLOW]) { cnt++; }
	if (colorVariation[PuyoColor.GREEN]) { cnt++; }
	if (colorVariation[PuyoColor.PURPLE]) { cnt++; }
	return cnt;
    }
    public int[] getColorList() {
	int[] colorList = new int[this.getNumColor()];
	int idx = 0;
	if(colorVariation[PuyoColor.RED]) {
	    colorList[idx] = PuyoColor.RED;
	    idx++;
	}
	if(colorVariation[PuyoColor.BLUE]) {
	    colorList[idx] = PuyoColor.BLUE;
	    idx++;
	}
	if(colorVariation[PuyoColor.YELLOW]) {
	    colorList[idx] = PuyoColor.YELLOW;
	    idx++;
	}
	if(colorVariation[PuyoColor.GREEN]) {
	    colorList[idx] = PuyoColor.GREEN;
	    idx++;
	}
	if(colorVariation[PuyoColor.PURPLE]) {
	    colorList[idx] = PuyoColor.PURPLE;
	    idx++;
	}
	return colorList;
    }

    public void setPuyo2Field(int[] col, int[] row) {
	field[row[0]][col[0]] = thisTsumo[0];
	field[row[1]][col[1]] = thisTsumo[1];
    }

    /*
    public void setPuyo2Field() {
	field = new SnesInterface().getPuyoField()[];

    }
    */	
    public void setPuyo2Field(SnesInterface ioface) {
        int[][] data = ioface.getFieldPuyo()[player];
	boolean hole;
	for (int x = 0; x < Field.MAX_WIDTH; x++) {
	    hole = false;
	    for (int y = 0; y < Field.MAX_VISUAL_HEIGHT; y++) {
		if (hole) { 
		    field[y][x] = PuyoColor.EMPTY;
		} else if (data[y][x] == PuyoColor.EMPTY) {
		    field[y][x] = PuyoColor.EMPTY;
		    hole = true;
		} else if ( field[y][x] != data[y][x] ) {
		    if (data[y][x] >= 0) {
			field[y][x] = data[y][x];
		    }
		}
	    }
	}
	allClear = Field.isAllClear(field);
    }

}
