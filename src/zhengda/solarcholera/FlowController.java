package zhengda.solarcholera;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import zhengda.solarcholera.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;


public class FlowController extends Activity {
	
	final private String tag = "FlowController";
	final private String dim_uL="¦ÌL";
	final private String dim_RPM="RPM";

	static public boolean newRPMawait=false; 
	static public float RPMtoSend=0;
	static private float RPMTemp=0;
	static private float flowRate=0;
	static private int dir=1;
	static private int workTime=0;
	static public float volToMove=0;
	static public boolean newVolToMove=false;
	private SeekBar mSeekBar,mSeekBarVol;
	private Button mInject,mExtract,mReset;
	private TextView mFlowRate, mRPM, mVol;
	private ToggleButton mFlowForward, mFlowBackward;
	private ImageView mFlowStatus;
	Timer timer = new Timer(true);
    private static Handler handler;
    
    private RadioGroup mSpeed;
    private RadioButton mS0,mS1,mS2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_control);
		
		mFlowRate=(TextView)findViewById(R.id.flow_rate);
		mRPM=(TextView)findViewById(R.id.rpm);
		mVol=(TextView)findViewById(R.id.flow_vol);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBarVol = (SeekBar) findViewById(R.id.seekBarVol);
		mFlowForward = (ToggleButton) findViewById(R.id.flow_forward);
		mFlowBackward = (ToggleButton) findViewById(R.id.flow_backward);
		mInject=(Button)findViewById(R.id.inject);
		mReset=(Button)findViewById(R.id.reset_rate);
		mExtract = (Button)findViewById(R.id.extract);
		
		mSpeed = (RadioGroup) findViewById(R.id.Speed);
		mS0=(RadioButton)findViewById(R.id.s0);
		mS1=(RadioButton)findViewById(R.id.s1);
		mS2=(RadioButton)findViewById(R.id.s2);
		
		
		handler =new Handler(){
	         @Override
			public void handleMessage(Message msg){   
	             switch(msg.what){
	             case 223:
	            	 Log.d(tag, "handler runned");
	            	 mFlowForward.setEnabled(true);
	            	 mFlowBackward.setEnabled(true);
	            	 mInject.setEnabled(true);
	            	 mExtract.setEnabled(true);
	            	 mFlowForward.setChecked(false);
	            	 mFlowBackward.setChecked(false);
	            	 mSeekBar.setEnabled(true);
	 	        	 mSeekBarVol.setEnabled(true);
	            	 flowStop();
	            	 break;
	             }
	             super.handleMessage(msg);
	            }
	        };
	    
	    
	    mInject.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	if (timer != null)
			//		timer.cancel();
				TimerTask task = new TimerTask(){
			    	@Override
			    	public void run() {
			        	// TODO Auto-generated method stub\
			    		Log.d(tag, "timer runned");
			        	Message msg = new Message();
			        	msg.what=223;
			        	handler.sendMessage(msg);
			          }  
			    };
				if (volToMove<0.05)
					return;
				int spd;
				if (mS0.isChecked()){
					spd=1;workTime=(int) (volToMove*12000);}
				else if (mS1.isChecked()){
					spd=2;workTime=(int) (volToMove*6000);}
				else if (mS2.isChecked()){
					spd=4;workTime=(int) (volToMove*3000);}
				else return;
				
			//	RPMtoSend = 6.1417f*spd;
				newRPMawait = true;
				sendFloatToDevice(6.1417f*spd);
				mFlowForward.setChecked(false);
				mFlowBackward.setChecked(false);
				mFlowForward.setEnabled(false);
	        	mFlowBackward.setEnabled(false);
	        	mInject.setEnabled(false);
	        	mExtract.setEnabled(false);
	        	mSeekBar.setEnabled(false);
	        	mSeekBarVol.setEnabled(false);
	        	Log.d(tag, "Time="+workTime+" RPM="+RPMtoSend);
	        	timer.schedule(task, workTime);
			}
		});
	    
	    mExtract.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	if (timer != null)
			//		timer.cancel();
				TimerTask task = new TimerTask(){
			    	@Override
			    	public void run() {
			        	// TODO Auto-generated method stub\
			    		Log.d(tag, "timer runned");
			        	Message msg = new Message();
			        	msg.what=223;
			        	handler.sendMessage(msg);
			          }  
			    };
				if (volToMove<0.05)
					return;
				int spd;
				if (mS0.isChecked()){
					spd=1;workTime=(int) (volToMove*12000);}
				else if (mS1.isChecked()){
					spd=2;workTime=(int) (volToMove*6000);}
				else if (mS2.isChecked()){
					spd=4;workTime=(int) (volToMove*3000);}
				else return;
				
			//	RPMtoSend = -6.1417f*spd;
				newRPMawait = true;
				sendFloatToDevice(-6.1417f*spd);
				mFlowForward.setChecked(false);
				mFlowBackward.setChecked(false);
				mFlowForward.setEnabled(false);
	        	mFlowBackward.setEnabled(false);
	        	mInject.setEnabled(false);
	        	mExtract.setEnabled(false);
	        	mSeekBar.setEnabled(false);
	        	mSeekBarVol.setEnabled(false);
	        	Log.d(tag, "Time="+workTime+" RPM="+RPMtoSend);
	        	timer.schedule(task, workTime);
			}
		});
		
		
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(mFlowForward.isChecked() || mFlowBackward.isChecked())
					flowStart();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				float ul = progressToRPM(progress)/10.0f;
				float RPM=ul/0.8141f;
				BigDecimal b = new BigDecimal(RPM);  
				RPM = b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				b = new BigDecimal(ul);  
				ul = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
				
				mFlowRate.setText(""+ul+"¦ÌL/min");
				mRPM.setText(""+RPM+"RPM");
				RPMTemp = RPM;
				
				
				
			}
		});
		
		mFlowForward.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mFlowBackward.setChecked(false);
					dir=1;
					flowStart();
					}else{
					flowStop();
					}
			}
			
		});
		
		mFlowBackward.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mFlowForward.setChecked(false);
					dir=-1;
					flowStart();
					}else{
					flowStop();
					}
			}
			
		});
		
		mReset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				// TODO Auto-generated method stub
				Log.d(tag, "OnClick Reset");
				flowStop();
				mFlowForward.setChecked(false);
				mFlowBackward.setChecked(false);
				mFlowForward.setEnabled(true);
	        	mFlowBackward.setEnabled(true);
	        	mInject.setEnabled(true);
	        	mExtract.setEnabled(true);
	        	mSeekBar.setEnabled(true);
	        	mSeekBarVol.setEnabled(true);
				dir=1;
				mSeekBar.setProgress(0);
			}
		});
		
		mSeekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				volToMove=progress/1.0f;
				BigDecimal b = new BigDecimal(volToMove);  
				volToMove = b.setScale(0,BigDecimal.ROUND_HALF_UP).floatValue();
				int spd=0;
				if (mS0.isChecked()){
					spd=1;workTime=(int) (volToMove*12000);}
				else if (mS1.isChecked()){
					spd=2;workTime=(int) (volToMove*6000);}
				else if (mS2.isChecked()){
					spd=4;workTime=(int) (volToMove*3000);}
				mVol.setText(""+volToMove+"¦ÌL in "+(int)volToMove * 12 / spd+"s");
			}
		});
		
		mS0.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mS0.isChecked()){
					mVol.setText(""+volToMove+"¦ÌL in "+(int)volToMove * 12 +"s");
					}
			}
		});
		mS1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mS1.isChecked()){
					mVol.setText(""+volToMove+"¦ÌL in "+(int)volToMove * 6 +"s");
					}
			}
		});
		mS2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mS2.isChecked()){
					mVol.setText(""+volToMove+"¦ÌL in "+(int)volToMove * 3 +"s");
					}
			}
		});
		
		
		
		
		
		
			
	}
		
		
		
		
	
	
	private float progressToRPM(int progress){
		return progress/10.0f;
	}
	
	private int flowStart(){
		sendFloatToDevice(RPMTemp*dir);
		return 1;
	}
	
	private int flowStop(){
		sendFloatToDevice(0);
		return 1;
	}
	
	public int sendFloatToDevice(float rpm){
		if (Math.abs(rpm-RPMtoSend)>0.05f){
			RPMtoSend=rpm;
			BigDecimal b = new BigDecimal(rpm);  
			rpm = b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
			Intent rpmValue=new Intent(FlowController.this, MainActivity.class);
			rpmValue.putExtra("rpmValue", ""+rpm);
			startActivity(rpmValue);
		}
	/**	
			RPMtoSend=rpm;
			newRPMawait=true;
		}**/
		return 1;
	}
	
}
