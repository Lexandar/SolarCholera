package zhengda.solarcholera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TemperatureTrend extends Activity {
	
	final private String Tag = "TemperatureTrend";
	final private String dm_Cel="'C";

	private Button mZoomIn,mZoomOut,mMoveLeft,mMoveRight;
	static public TextView mTemp0,mTemp1,mTemp2;
	static public ImageView inRange0,inRange1,inRange2;
//	private TempTrndGraphView mTempTrndGraphView;
	private ToggleButton mLocker;
	private ImageView mCheck0,mCheck1,mCheck2;
	private TempTrndGraphView graphView;
	private boolean firstIntent = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_trend);
		mTemp0 = (TextView) findViewById(R.id.temp0);
		mTemp1 = (TextView) findViewById(R.id.temp1);
		mTemp2 = (TextView) findViewById(R.id.temp2);
		
		mCheck0 = (ImageView) findViewById(R.id.in_range0);
		mCheck1 = (ImageView) findViewById(R.id.in_range1);
		mCheck2 = (ImageView) findViewById(R.id.in_range2);
		
		graphView = (TempTrndGraphView) findViewById(R.id.graphView);
		/**
		Intent fromMain;
		fromMain = this.getIntent();
		Log.d(Tag, "Temperature Trend onCreate");
		Bundle bundle=fromMain.getExtras();
		
		if (bundle!=null && bundle.containsKey("Time&Temp")){
			int t[] = bundle.getIntArray("Time&Temp");
			mTemp0.setText(""+t[1]);
			mTemp1.setText(""+t[2]);
			mTemp2.setText(""+t[3]);
			Log.d(Tag, "At "+t[0]+" get "+t[1]+" "+t[2]+" "+t[3]);
		}**/
		
	}	
	
	@Override
	protected void onNewIntent(Intent fromMain){
		super.onNewIntent(fromMain);
	//	Log.d(Tag, "OnNewIntent");
		setIntent(fromMain);
		Bundle bundle=fromMain.getExtras();
		if (bundle!=null && bundle.containsKey("Time") && bundle.containsKey("Temps")){
			int time = bundle.getInt("Time");
			if (firstIntent) { 
				firstIntent = false;
			}
			int rawTemps[]=bundle.getIntArray("Temps");
			float temps[]=new float[rawTemps.length];
			for (int i=0; i<temps.length;i++)
				temps[i]=rawTemps[i]/4.0f;
			if (temps[0]<0.25f)
				mTemp0.setText("Amp Error");
			else if (temps[0]>200f)
				mTemp0.setText("TC Error");
			else {
				if (temps[0]>=102.5f && temps[0]<=107.5f)
					mCheck0.setImageResource(R.drawable.ic_check);
				else mCheck0.setImageResource(R.drawable.blank);
				mTemp0.setText(String.format("%.2f", temps[0])+dm_Cel);
				if (time<1200)
					TempTrndGraphView.temperatures[time - 1] = temps[0];
				graphView.invalidate();
			}
			if (temps[1]<0.25f)
				mTemp1.setText("Amp Error");
			else if (temps[1]>200f)
				mTemp1.setText("TC Error");
			else mTemp1.setText(String.format("%.2f", temps[1])+dm_Cel);
			if (temps[2]<0.25f)
				mTemp2.setText("Amp Error");
			else if (temps[2]>200f)
				mTemp2.setText("TC Error");
			else mTemp2.setText(String.format("%.2f", temps[2])+dm_Cel);
			//If temps in range, change checks
		/**	if (temps[0]>=ValueSet.DENATURATION_MIN && temps[0]<=ValueSet.DENATURATION_MAX)
				mCheck0.setImageResource(R.drawable.ic_check);
			else mCheck0.setImageResource(R.drawable.blank);
			
			if (temps[1]>=ValueSet.EXTENSION_MIN && temps[1]<=ValueSet.EXTENSION_MAX)
				mCheck1.setImageResource(R.drawable.ic_check);
			else mCheck1.setImageResource(R.drawable.blank);
			
			if (temps[2]>=ValueSet.ANEALING_MIN && temps[2]<=ValueSet.ANEALING_MAX)
				mCheck2.setImageResource(R.drawable.ic_check);
			else mCheck2.setImageResource(R.drawable.blank);**/
		}
	}
}


