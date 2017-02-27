
import SnesInterface.*;
import GlobalFunc.Field;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;

public class PutSimFileKai {
    public static void main(String[] args) {

	if (args.length < 2) {
	    System.out.println("java PutSimFileKai tsumo.csv N32D3");
	    return;
	}
	try {
	    File tsumoFile = new File(args[0]);
	    
	    BufferedReader tsumoBR = new BufferedReader(new FileReader(tsumoFile));
	    String tsumoLine = tsumoBR.readLine();
	    while(tsumoLine != null) {
		String[] tsumo = tsumoLine.split(",");
		String tsumoList = "";
		String calcTimes = "";
		String str_time = "";
		String str_score = "";
		String chain = "";
		String totalPuyo = "";
		String delPuyo = "";

		PlayerStat stat = new PlayerStat();
		//PotentialFullKai ai = new PotentialFullKai();
		HumanKaidan humanAi = new HumanKaidan();
		DelPotentialKai ai = new DelPotentialKai();
		//PotentialLayered ai = new PotentialLayered();
		//SnesInterface ioface = new SnesInterface();

		int[] thisTsumo = new int[2];

		int[] nextTsumo = new int[2];
		nextTsumo[0] = Character.getNumericValue(tsumo[0].charAt(0));
		nextTsumo[1] = Character.getNumericValue(tsumo[0].charAt(1));

		int[] nxnxTsumo = new int[2];
		nxnxTsumo[0] = Character.getNumericValue(tsumo[1].charAt(0));
		nxnxTsumo[1] = Character.getNumericValue(tsumo[1].charAt(1));

		stat = new PlayerStat(thisTsumo, nextTsumo, nxnxTsumo);

		int col[] = new int[2];
		int row[] = new int[2];
		int depth = 3;
		int iMax = 32;
		int max_moves = 6;
		if(tsumo.length < iMax+2) {
		    System.out.println("Tsumo data are not enough: " + tsumo.length);
		    return;
		}
		double timeAvg = 0.0;
		System.out.println("depth: " + depth);
		for (int i = 0; i < iMax; i++) {
		    /* normal width search
		    stat.thisTsumo[0] = stat.nextTsumo[0];
		    stat.thisTsumo[1] = stat.nextTsumo[1];
		    stat.nextTsumo[0] = stat.nxnxTsumo[0];
		    stat.nextTsumo[1] = stat.nxnxTsumo[1];
		    stat.nxnxTsumo[0] = Character.getNumericValue(tsumo[i+2].charAt(0));
		    stat.nxnxTsumo[1] = Character.getNumericValue(tsumo[i+2].charAt(1));
		    /* */

		    /* Full width search */
		    stat.thisTsumo[0] = stat.nextTsumo[0];
		    stat.thisTsumo[1] = stat.nextTsumo[1];
		    stat.nextTsumo[0] = Character.getNumericValue(tsumo[i+1].charAt(0));
		    stat.nextTsumo[1] = Character.getNumericValue(tsumo[i+2].charAt(1));
		    stat.nxnxTsumo[0] = PuyoColor.EMPTY;
		    stat.nxnxTsumo[1] = PuyoColor.EMPTY;
		    /* */
		    stat.addColorVari();
		    

		    long start = System.currentTimeMillis();
		    float[] ret;
		    if (i < max_moves) {
			ret = humanAi.decidePutLocale(stat, col, row, depth);
		    } else {
			ret = ai.decidePutLocale(stat, col, row, depth);
		    }
		    if (ret[0] < 0) {
			break;
		    }
		    long time = System.currentTimeMillis() - start;

		    int[] niseCol = new int[2];
		    int[] niseRow = new int[2];
		    //ret = ai.decidePutLocale(stat, niseCol, niseRow, 1);
		    stat.setPuyo2Field(col, row);

		    /*
		    timeAvg += time;
		    
		    System.out.println("time: " + time);
		    System.out.println("score: " +ret[0]);
		    System.out.println("chain: " + ret[1] );
		    System.out.println("total del: " + ret[2]);
		    System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
		    */
		    //Field.printData(stat.field);
		    
		    tsumoList += ai.tsumoListNum + ",";
		    calcTimes += ai.scoreCalcNum + ",";
		    str_time += Long.toString(time) + ",";
		    str_score += ret[0]  + ",";
		    chain += ret[1]  + ",";
		    totalPuyo += Field.getPuyoNum(stat.field) + ",";
		    delPuyo += ret[2] + ",";
		    
		    //設置後の消去
		    Score score = new Score();
		    score.setPoint(stat.field);
		    stat.field = score.getField();

		} 
		    
	    
		//発火
		stat.thisTsumo[0] = PuyoColor.EMPTY;
		stat.thisTsumo[1] = PuyoColor.EMPTY;
		long start = System.currentTimeMillis();
		//Score score = new Score();
		float[] ret = ai.decidePutLocale(stat, col, row, 1);
		if (ret[0] < 0) {
		    return;
		}
		long time = System.currentTimeMillis() - start;
		stat.setPuyo2Field(col, row);
		Field.printData(stat.field);

		System.out.println("time: " + time);
		System.out.println("score: " + ret[0]);
		System.out.println("chain: " + ret[1] );
		System.out.println("total del: " + ret[2]);
		System.out.println("("+col[0]+","+row[0]+"), ("+col[1]+","+row[1]+")");
		
		tsumoList += ai.tsumoListNum + "\n";
		calcTimes += ai.scoreCalcNum + "\n";
		str_time += Long.toString(time) + "\n";
		str_score += ret[0] + "\n";
		chain += ret[1] + "\n";
		totalPuyo += Field.getPuyoNum(stat.field) + "\n";
		delPuyo += ret[2] + "\n";
		
		try {
		    File file = new File("./", args[1]+"_tsumoList.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(tsumoList);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_calcTimes.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(calcTimes);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_time.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(str_time);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_score.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(str_score);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_chain.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(chain);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_totalPuyo.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(totalPuyo);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		try {
		    File file = new File("./", args[1]+"_delPuyo.csv");
		    FileWriter filewriter = new FileWriter(file, true);
		    filewriter.write(delPuyo);
		    filewriter.close();
		} catch(IOException e){
		    System.out.println(e);
		    return;
		}
		tsumoLine = tsumoBR.readLine();
	    }
	    tsumoBR.close();
	} catch(IOException e){
	    System.out.println(e);
	    return;
	}
    }
	/*
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
	//SnesInterface ioface = new SnesInterface();

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
	*/

}
