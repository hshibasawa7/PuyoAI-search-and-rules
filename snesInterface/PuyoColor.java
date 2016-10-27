public class PuyoColor {
    public static final int MAX_NUM = 7;
    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int BLUE = 2;
    public static final int GREEN = 3;
    public static final int PURPLE = 4;
    public static final int YELLOW = 5;
    public static final int OJAMA = 6;

    public static float[][][] trainedHist = {
	//Empty
	{{0.33333334F, 0.0F, 0.0F, 0.0F},
	 {0.33333334F, 0.0F, 0.0F, 0.0F},
	 {0.33333334F, 0.0F, 0.0F, 0.0F}},
	//Red
	{{0.18391927F, 0.04264323F, 0.052408855F, 0.05436198F},
	 {0.27115884F, 0.032552082F, 0.02734375F, 0.0022786458F},
	 {0.2705078F, 0.033203125F, 0.02734375F, 0.0022786458F}},
	//Blue
	{{0.29231772F, 0.021809896F, 0.017252604F, 0.001953125F},
	 {0.23502605F, 0.055664062F, 0.034505207F, 0.0081380205F},
	 {0.19303386F, 0.05078125F, 0.07486979F, 0.0146484375F}},
	//Green
	{{0.26627603F, 0.04264323F, 0.022135416F, 0.0022786458F},
	 {0.22070312F, 0.053710938F, 0.049153645F, 0.009765625F},
	 {0.29654947F, 0.020833334F, 0.0152994795F, 6.510417E-4F}},
	//Purple
	{{0.23079427F, 0.06282552F, 0.035807293F, 0.00390625F},
	 {0.28873697F, 0.028320312F, 0.0152994795F, 9.765625E-4F},
	 {0.2220052F, 0.044921875F, 0.044270832F, 0.022135416F}},
	//Yellow
	{{0.19303386F, 0.034179688F, 0.055989582F, 0.050130207F},
	 {0.21712239F, 0.04296875F, 0.049479168F, 0.023763021F},
	 {0.29361978F, 0.026692709F, 0.013020833F, 0.0F}},
	//Ojama
	{{0.24804688F, 0.052408855F, 0.032877605F, 0.0F},
	 {0.24772136F, 0.03548177F, 0.047526043F, 0.0026041667F},
	 {0.24316406F, 0.036132812F, 0.051432293F, 0.0026041667F}}
    };
 
    public static String getColorString(int num) {
	if(num == EMPTY) { return "EMPTY"; }
	if(num == RED) { return "RED"; }
	if(num == BLUE) { return "BLUE"; }
	if(num == GREEN) { return "GREEN"; }
	if(num == YELLOW) { return "YELLOW"; }
	if(num == PURPLE) { return "PURPLE"; }
	if(num == OJAMA) { return "OJAMA"; }
	return "INVALID NUMBER";
    }
}
