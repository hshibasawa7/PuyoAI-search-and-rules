import SnesInterface.*;

public class TsumoOut {

    public static void main(String args[]) {
       	SnesInterface ioface = new SnesInterface();

	try {
	    Thread.sleep(5000);
	} catch(InterruptedException e ) {
	    throw new RuntimeException(e);
	}

	int[] thisTsumo = new int[2];
	int[] nextTsumo = ioface.getNextPuyo()[1];
	int[] nxnxTsumo = ioface.getNxnxPuyo()[1];

	System.out.print("tsumo");
	while(true) {
	    while(true) {
		if ( ioface.waitPuyoGiven(1, thisTsumo, nextTsumo, nxnxTsumo) ) {
		    break;
		}
		try {
		    Thread.sleep(500);
		} catch(InterruptedException e ) {
		    throw new RuntimeException(e);
		}
	    }
	    System.out.print(" " + thisTsumo[0] + thisTsumo[1]);
	}

    }

}
