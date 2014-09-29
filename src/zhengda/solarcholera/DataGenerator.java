package zhengda.solarcholera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import java.lang.Math;

public class DataGenerator {
	
	public static int[] YAxisLocation = new int[ValueSet.screenWidth+1];
	public static int[] Peaks = {0,0,0,0};
	Color mColor;
	public static int ControlGroup=3;
	public static int Bar = 0;
	private static final String Tag = "DataGenerator";
	private Bitmap mBitmap;
	
	public static int timeValue=0;
	public static float[] tempValues={0,0,0};
	public static float rpm=0.0f;
	public static boolean dataIsValid=true;
	
	public DataGenerator(Bitmap mBitmap){
		this.mBitmap=mBitmap;
	}
	
	public void getData(Bitmap CurGraph){
		
		mBitmap = CurGraph;
		
		int Width = CurGraph.getWidth();
		int Height = CurGraph.getHeight();
		int[] YAxisLocation = new int[Width];
				
		double[] Intensity = new double[Width];
		int DigitPoint;
		float[] HSV = new float[3];
		float brightness;
		
		for (int j=0;j<Width;j++){
			
			double TotalInten = 0.0d;
			
			for (int i=0;i<Height;i++){
				DigitPoint = CurGraph.getPixel(j, i);
				Color.colorToHSV(DigitPoint, HSV);
				if (HSV[2]>=1.0f)
					brightness = HSV[2]-HSV[1]+1;
				else brightness = (HSV[2]);//*(-Math.abs(HSV[0]-129.6f)/50.4f + 1.0f));
				TotalInten = TotalInten + brightness;
				
			}
			Intensity[j] = TotalInten;
		}
		
		double minIntensity=min(Intensity);
		
		for (int j=0;j<Width;j++){
			Intensity[j]=Intensity[j]-minIntensity;
		}
		
		double ChartHeight = max(Intensity) * 1.2d;
		
		for (int i=0;i<Width;i++)
			YAxisLocation[i] = (int)Math.round(ValueSet.screenWidth / 2 * Intensity[i] / ChartHeight);
		DataGenerator.YAxisLocation=YAxisLocation;
		setNewBar(ControlGroup);
	}
	
	private double max(double[] arr){
		double MaxValue = 0.0d;
		for (int i=0; i<arr.length; i++){
			if (arr[i]>MaxValue)
				MaxValue = arr[i];
		}
		return MaxValue;
	}
	
	private double min(double[] arr){
		double MinValue = Double.MAX_VALUE;
		for (int i=0; i<arr.length; i++){
			if (arr[i]<MinValue)
				MinValue = arr[i];
		}
		return MinValue;
	}
	
	public  static void setControlGroup(int x){
		ControlGroup = x;
		setNewBar(x);
	}
	
	public static void setNewBar(int x){
		Log.d(Tag,"Pre Bar is "+Bar);
		float step = (ValueSet.RightEdgeX - ValueSet.LeftEdgeX) / 4.0f;
	//	int start = (int)(ValueSet.LeftEdgeX+x*step);
	//	int end = (int)(ValueSet.LeftEdgeX+(x+1)*step);
	//	int ControlGroupMax = 0;
		for (int i=0; i<=3; i++){
			Peaks[i]=0;
		}
		int j;
		Log.d(Tag,""+ ValueSet.RightEdgeX);
		for (int i=ValueSet.LeftEdgeX;i<=ValueSet.RightEdgeX;i++){
			if (i-ValueSet.LeftEdgeX < step)
				j=0;
			else if (i-ValueSet.LeftEdgeX < step*2)
				j=1;
			else if (i-ValueSet.LeftEdgeX < step*3)
				j=2;
			else j=3;
			if (Peaks[j]<YAxisLocation[i]){
				Log.d(Tag, "Zone "+j+" Row "+i+" in "+ValueSet.screenWidth);
				Peaks[j]=YAxisLocation[i];
			}
		}
		
		
		Bar = 3*Peaks[ControlGroup];
		Log.d(Tag,"Bar is "+Bar);
	}
	
	static public void strToTemps(String str){
		dataIsValid = true;
		String strValue;
		strValue=str.substring(0,str.indexOf(" "));
		str=str.substring(str.indexOf(" "), str.length());
		try{
			timeValue=Integer.parseInt(strValue);
		}catch(NumberFormatException e){
			dataIsValid = false;
			return;
		}
		for(int i=0;i<3;i++){
			while (str.indexOf(" ")==0)
				str=str.substring(1);
			strValue=str.substring(0,str.indexOf(" "));
			String tempstr=str.substring(str.indexOf(" "), str.length());
			str = tempstr;
		//	strValue="NaN";
			try{
				tempValues[i]=Float.parseFloat(strValue);
			}catch(NumberFormatException e){
			//	tempValues[i]=30.0f;
			}
			if((tempValues[i]==Float.NaN) || (tempValues[i]>=120.0f) || (tempValues[i]<=10.0f)){
				dataIsValid = false;
			//	return;
			}
		}
		
		for(int i=0;i<3;i++){
			ValueSet.temperature[timeValue][i]=tempValues[i];
		}
		
		TemperatureTrend.inRange0.setImageResource(R.drawable.ic_blank);
		TemperatureTrend.inRange1.setImageResource(R.drawable.ic_blank);
		TemperatureTrend.inRange2.setImageResource(R.drawable.ic_blank);
		if (Math.abs(tempValues[0])>1000.0f)
			TemperatureTrend.mTemp0.setText("TC Disconnected");
		else if(tempValues[0]==0.0f)
			TemperatureTrend.mTemp0.setText("Amp Error");
		else {
			TemperatureTrend.mTemp0.setText(""+tempValues[0]+"`C");
			if (tempValues[0]<=ValueSet.DENATURATION_MAX && tempValues[0]>=ValueSet.DENATURATION_MIN){
				TemperatureTrend.inRange0.setImageResource(R.drawable.ic_check);
			}
		}

		if (Math.abs(tempValues[1])>1000.0f)
			TemperatureTrend.mTemp1.setText("TC Disconnected");
		else if(tempValues[1]==0.0f)
			TemperatureTrend.mTemp1.setText("Amp Error");
		else {TemperatureTrend.mTemp1.setText(""+tempValues[1]+"`C");
		if (tempValues[1]<=ValueSet.EXTENSION_MAX && tempValues[1]>=ValueSet.EXTENSION_MIN){
			TemperatureTrend.inRange1.setImageResource(R.drawable.ic_check);
		}
		}
		if (Math.abs(tempValues[2])>1000.0f)
			TemperatureTrend.mTemp2.setText("TC Disconnected");
		else if(tempValues[2]==0.0f)
			TemperatureTrend.mTemp2.setText("Amp Error");
		else {TemperatureTrend.mTemp2.setText(""+tempValues[2]+"`C");
		if (tempValues[2]<=ValueSet.ANEALING_MAX && tempValues[2]>=ValueSet.ANEALING_MIN){
			TemperatureTrend.inRange2.setImageResource(R.drawable.ic_check);
		}}
		ValueSet.numOfPoint=timeValue;
				
	}
	
	
	
}
