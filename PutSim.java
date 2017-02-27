
import SnesInterface.*;
import globalFunc.Field;

public class PutSim {
    public static void main(String[] args) {
	if (args.length < 1) {
	    System.out.println("input the bariety of tsumo.");
	    return;
	}

	String[] outData = new String[8];
	outData[0] = "tsumo";
	outData[1] = "tsumoList";
	outData[2] = "calcTimes";
	outData[3] = "time[ms]";
	outData[4] = "score";
	outData[5] = "chain";
	outData[6] = "totalPuyo";
	outData[7] = "delPuyo";

	PlayerStat stat = new PlayerStat();
	Potential ai = new Potential();
	//DelPotential ai = new DelPotential();
	SnesInterface ioface = new SnesInterface();

	int[] thisTsumo = new int[2];
	//thisTsumo[0] = Character.getNumericValue(args[0].charAt(0));
	//thisTsumo[1] = Character.getNumericValue(args[0].charAt(1));

	int[] nextTsumo = new int[2];
	nextTsumo[0] = Character.getNumericValue(args[0].charAt(0));
	nextTsumo[1] = Character.getNumericValue(args[0].charAt(1));

	int[] nxnxTsumo = new int[2];
	nxnxTsumo[0] = Character.getNumericValue(args[0].charAt(2));
	nxnxTsumo[1] = Character.getNumericValue(args[0].charAt(3));

	stat = new PlayerStat(thisTsumo, nextTsumo, nxnxTsumo);

	int col[] = new int[2];
	int row[] = new int[2];
	int depth = 5;
	int iMax = args[0].length();
	double timeAvg = 0.0;
	System.out.println("depth: " + depth);
	for (int i = 1; i < iMax; i += 2) {
	    stat.thisTsumo[0] = stat.nextTsumo[0];
	    stat.thisTsumo[1] = stat.nextTsumo[1];
	    stat.nextTsumo[0] = stat.nxnxTsumo[0];
	    stat.nextTsumo[1] = stat.nxnxTsumo[1];

	    long start = System.currentTimeMillis();
	    Score score = new Score();
	    if ((iMax - i) > 4) {
		stat.nxnxTsumo[0] = Character.getNumericValue(args[0].charAt(i+3));
		stat.nxnxTsumo[1] = Character.getNumericValue(args[0].charAt(i+4));
		stat.addColorVari();
	    }
	    if ((iMax - i)/2+1 < depth) {
		score = ai.decidePutLocale(stat, col, row, (iMax - i)/2+1);
	    } else {
		score = ai.decidePutLocale(stat, col, row, depth);
	    }
	    long time = System.currentTimeMillis() - start;
	    System.out.println("time: " + time);
	    timeAvg += time;

	    System.out.println("score: " + score.score);
	    if (score.score < 0) {
		return;
	    }
	    System.out.println("chain: " + score.chain );
	    System.out.println("total del: " + score.totalDel);

	    System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
	    
	    stat.setPuyo2Field(col, row);
	    Field.printData(stat.field);

	    outData[0] += " " + stat.thisTsumo[0] + stat.thisTsumo[1];
	    outData[1] += " " + ai.tsumoListNum;//tsumoList size
	    outData[2] += " " + ai.scoreCalcNum;//calc times
	    outData[3] += " " + time;
	    outData[4] += " " + score.score;
	    outData[5] += " " + score.chain;
	    outData[6] += " " + Field.getPuyoNum(stat.field);
	    outData[7] += " " + score.totalDel;

	    score.setPoint(stat.field);
	    stat.field = score.getField();

	}
	timeAvg /= iMax/2 - 2;
	System.out.println("Average Time: " + timeAvg);
	for (int i = 0; i < outData.length; i++) {
	    System.out.println(outData[i]);
	}
    }

}
