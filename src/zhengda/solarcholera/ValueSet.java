package zhengda.solarcholera;

public class ValueSet {
	
	static final public int MAX_NUM_OF_POINTS=18000;
	
	public static final float DENATURATION_MAX= 98f;
	public static final float DENATURATION_MIN= 93f;
	public static final float EXTENSION_MAX= 77f;
	public static final float EXTENSION_MIN= 72f;
	public static final float ANEALING_MAX= 65f;
	public static final float ANEALING_MIN= 55f;
	
	static public double temperature[][]= new double[MAX_NUM_OF_POINTS+1][3];
	static public int startPoint=0;
	static public int numOfPoint=0;
	static public String tag = "ValueSet";
	static public int screenWidth=800;
	static public int screenHeight=1280;
	
	static public int ChannelBoundary[] = {137,256,371,477,589};
	static public int NumberOfBoundary = 5;
	
	static public float HueRangeLow = 0.22f;
	static public float HueRangeHigh = 0.50f;
	static public float BriAverage = 0.36f;
	static public float BriRange = 0.14f;
	
	static public int EdgeIconSize = 20;
	static public int HeadHeight = 150;
	static public int LeftEdgeX = 20;
	static public int RightEdgeX = screenWidth-20;

	public ValueSet() {
		// TODO Auto-generated constructor stub
		ValueSet.temperature[0][0]=0;
		ValueSet.temperature[0][1]=0;
		ValueSet.temperature[0][2]=0;
	//	Timer timer = new Timer();
	//	timer.schedule(new TimeTask(), 1000, 1000);
	}

}
