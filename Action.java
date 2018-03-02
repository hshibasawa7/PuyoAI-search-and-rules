import java.util.Random;
import SnesInterface.PuyoColor;
import GlobalFunc.Field;

public class Action {
    public int[] x;
    public int[] y;
    public int[] color;

    public Action() {
	this.x = new int[2];
	this.y = new int[2];
	this.color = new int[2];
    }
    public Action(int[] color) {

	this();
	this.color[0] = color[0];
	this.color[1] = color[1];
    }
    public Action(int[] x, int[] y, int[] color) {
	this(color);
	this.x[0] = x[0];
	this.x[1] = x[1];
	this.y[0] = y[0];
	this.y[1] = y[1];
    }

    static public Action[] getActionFullList(PlayerStat stat) {
	int[] color = stat.thisTsumo;
	int[] height = Field.getHeight(stat.field);

	boolean is_zoro = false;
	int actionNum = Field.MAX_WIDTH * 2 - 1;
	if (color[0] != color[1]) {
	    actionNum *= 2;
	    is_zoro = false;
	} else {
	    is_zoro = true;
	}
	Action[] actionFullList = new Action[actionNum];
	int[] x = new int[2];
	int[] y = new int[2];
	int idx = 0;
	for (int i = 0; i < Field.MAX_WIDTH; i++) {
	    x[0] = i;
	    x[1] = i;
	    y[0] = height[i];
	    y[1] = height[i] + 1;
	    actionFullList[idx] = new Action(x, y, color);
	    idx++;
	}
	for (int i = 1; i < Field.MAX_WIDTH; i++) {
	    x[0] = i - 1;
	    x[1] = i;
	    y[0] = height[i-1];
	    y[1] = height[i];
	    actionFullList[idx] = new Action(x, y, color);
	    idx++;
	}

	if (is_zoro) {
	    return actionFullList;
	}

	for (int i = 0; i < Field.MAX_WIDTH; i++) {
	    x[1] = i;
	    x[0] = i;
	    y[1] = height[i];
	    y[0] = height[i] + 1;
	    actionFullList[idx] = new Action(x, y, color);
	    idx++;
	}
	for (int i = 1; i < Field.MAX_WIDTH; i++) {
	    x[1] = i - 1;
	    x[0] = i;
	    y[1] = height[i-1];
	    y[0] = height[i];
	    actionFullList[idx] = new Action(x, y, color);
	    idx++;
	}
	return actionFullList;
    }
    static public Action[] getMaxList(Action[] actionList, float[] eval) {
	if (eval.length != actionList.length) {
	    return null;
	}
	float max = 0.0F;
	int num_max = 0;
	for (int i = 0; i < eval.length; i++) {
	    if (max < eval[i]) {
		max = eval[i];
		num_max = 1;
	    } else if (max == eval[i]) {
		num_max++;
	    }
	}
	Action[] actionMaxList = new Action[num_max];
	int idx = 0;
	for (int i = 0; i < actionList.length; i++) {
	    if (max == eval[i]) {
		actionMaxList[idx] = actionList[i];
		idx++;
	    }
	}
	return actionMaxList;
    }
    static public Action getRandomOne(Action[] actionList) {
	int len = actionList.length;
	Random rand = new Random();
	int idx = rand.nextInt(len);
	return actionList[idx];
    }

    public void put(int[][] field) {
	field[this.y[0]][this.x[0]] = this.color[0];
	field[this.y[1]][this.x[1]] = this.color[1];
    }

}
