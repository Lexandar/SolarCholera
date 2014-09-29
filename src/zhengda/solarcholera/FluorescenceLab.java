package zhengda.solarcholera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FluorescenceLab extends Activity {
	
private static int RESULT_LOAD_IMAGE = 1;
	
	public static final String  Tag = "SolarPCReport";
	public String FilePath = null;
	public Bitmap mBitmap;
	private Button ButtonLoad, ButtonRotate;
	public FluoLabGraphView mFluoLabGraphView;
	public ImageView LeftEdge,RightEdge;
	public Context mContext;
	private int LeftLastX,RightLastX;
	private int RightLastY,LeftLastY;
	private Toast toast;
	private static int ControlGroup=3;
	public static ImageView ChannelCheck0;   
	public static ImageView ChannelCheck1; 
	public static ImageView ChannelCheck2; 
	public static ImageView ChannelCheck3; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fluor_lab);
		
		mFluoLabGraphView= (FluoLabGraphView) findViewById (R.id.graphView);
        DisplayMetrics dm = getResources().getDisplayMetrics();
		ValueSet.screenWidth = dm.widthPixels;
		ValueSet.screenHeight = dm.heightPixels;
		Log.d(Tag, "screen width =" + ValueSet.screenWidth + ",screen height=" + ValueSet.screenHeight);
//        mFluoLabGraphView.setBottom(ValueSet.ScreenWidth);
//        mFluoLabGraphView.setBitmapPath(FilePath);
  //      mFluoLabGraphView.setBottom(ValueSet.ScreenWidth);
       
        LeftEdge = (ImageView) findViewById (R.id.leftEdge);
        RightEdge = (ImageView) findViewById(R.id.rightEdge);
                 
        ButtonLoad = (Button) findViewById(R.id.buttonLoad);
        ButtonLoad.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);

			    refreshResult();
			    LeftEdge.setImageResource(R.drawable.left_edgeicon);
			    RightEdge.setImageResource(R.drawable.right_edgeicon);
			//    ChannelCheck0.setImageResource(R.drawable.control);
			    
			}
        	
        });
        
        ButtonRotate = (Button) findViewById(R.id.buttonRotate);
        ButtonRotate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FluoLabGraphView.Rotates = FluoLabGraphView.Rotates + 90;
				Log.d(Tag,"Rotates"+FluoLabGraphView.Rotates);
				mFluoLabGraphView.fitBitmap();
				refreshResult();
				
			}
		});
        
        
        LeftEdge.setOnTouchListener(new View.OnTouchListener() {
        	
        	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						LeftLastX = (int) event.getRawX();
						LeftLastY = (int) event.getRawY();
						Log.d(Tag, "Clicked");
						break;
					case MotionEvent.ACTION_MOVE:
						Log.d(Tag, "Moved");
						int dx = (int) event.getRawX() - LeftLastX;
						int dy = 0;//(int) event.getRawY() - LeftLastY;
						Log.d(Tag, "move dx=" + dx + ",  dy=" + dy);
						int left = v.getLeft() + dx;
						int top = v.getTop() + dy;
						int right = v.getRight() + dx;
						int bottom = v.getBottom() + dy;
						Log.d(Tag, "view  left=" + left + ", top=" + top + ", right=" + right + ",bottom=" + bottom);
						if ((left<=0) || (right>=ValueSet.screenWidth/2)){
							left = left - dx;
							right = right - dx;
						}
						v.layout(left, top, right, bottom);
						ValueSet.LeftEdgeX=left+10;
						LeftLastX = (int) event.getRawX();
						LeftLastY = (int) event.getRawY();
				//		Toast.makeText(getApplicationContext(), ValueSet.LeftEdgeX, Toast.LENGTH_SHORT).show();
						DataGenerator.setNewBar(ControlGroup);
						refreshResult();
					//	ChannelCheck2.setImageResource(R.drawable.negative);
						break;
				}
				return true;
			}
		});
        
        
        RightEdge.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						RightLastX = (int) event.getRawX();
						RightLastY = (int) event.getRawY();
						Log.d(Tag, "Clicked");
						break;
					case MotionEvent.ACTION_MOVE:
						Log.d(Tag, "Moved");
						int dx = (int) event.getRawX() - RightLastX;
						int dy = 0;//(int) event.getRawY() - LeftLastY;
						Log.d(Tag, "move dx=" + dx + ",  dy=" + dy);
						int left = v.getLeft() + dx;
						int top = v.getTop() + dy;
						int right = v.getRight() + dx;
						int bottom = v.getBottom() + dy;
						
						Log.d(Tag, "view  left=" + left + ", top=" + top + ", right=" + right + ",bottom=" + bottom);
						if ((left<=0) || (right>=ValueSet.screenWidth/2)){
							left = left - dx;
							right = right - dx;
						}
						v.layout(left, top, right, bottom);
						ValueSet.RightEdgeX=right-10+ValueSet.screenWidth/2;
						RightLastX = (int) event.getRawX();
						RightLastY = (int) event.getRawY();
						DataGenerator.setNewBar(ControlGroup);
						refreshResult();
					//	ChannelCheck1.setImageResource(R.drawable.positive);
						break; 
					}
				return true;
			}
		});
        
        ChannelCheck0 = (ImageView)findViewById(R.id.channelcheck0); 
        
        ChannelCheck0.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Long click to change negative control channel", Toast.LENGTH_SHORT).show();
			}
		});
        
        ChannelCheck0.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Negative Control set to 1st", Toast.LENGTH_SHORT).show();
				DataGenerator.ControlGroup=0;
				DataGenerator.setControlGroup(0);
				refreshResult();
				return false;
			}
		});
        
    	ChannelCheck1 = (ImageView)findViewById(R.id.channelcheck1);
    	
    	ChannelCheck1.setOnClickListener(new View.OnClickListener() {
			
    		@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
    			Toast.makeText(getApplicationContext(), "Long click to change negative control channel", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	ChannelCheck1.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Negative Control set to 2nd", Toast.LENGTH_SHORT).show();
				DataGenerator.ControlGroup=1;
				DataGenerator.setControlGroup(1);
				refreshResult();
				return false;
			}
		});
    	
    	
    	ChannelCheck2 = (ImageView)findViewById(R.id.channelcheck2); 
    	
    	ChannelCheck2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Long click to change negative control channel", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	ChannelCheck2.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Negative Control set to 3rd", Toast.LENGTH_SHORT).show();
				DataGenerator.ControlGroup=2;
				DataGenerator.setControlGroup(2);
				refreshResult();
				return false;
			}
		});
    	
    	ChannelCheck3 = (ImageView)findViewById(R.id.channelcheck3); 
    	
    	ChannelCheck3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Long click to change negative control channel", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	ChannelCheck3.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Negative Control set to 4th", Toast.LENGTH_SHORT).show();
				DataGenerator.ControlGroup=3;
				DataGenerator.setControlGroup(3);
				refreshResult();
				return false;
			}
		});
        
    }
    
   public void refreshResult(){
    	int result[] = {0, 0, 0, 0};
    	DataGenerator.setNewBar(ControlGroup);
    	for (int i=0; i<=3; i++){
    		if (DataGenerator.Peaks[i]>DataGenerator.Bar)
    			result[i]=R.drawable.positive;
    		else result[i]=R.drawable.negative;
    	}
    	result[DataGenerator.ControlGroup]=R.drawable.control;
    	ChannelCheck0.setImageResource(result[0]);
    	ChannelCheck1.setImageResource(result[1]);
    	ChannelCheck2.setImageResource(result[2]);
    	ChannelCheck3.setImageResource(result[3]);
    }
    
    

       
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
    		Bundle extras = data.getExtras();
    		Uri selectedImage = data.getData();
    		if(null!=extras){
    			Log.i("bb","isNull:"+(null==extras));
    			mBitmap = (Bitmap) extras.get("data");
    		}else if (selectedImage!=null){
    			
    			String[] filePathColumn = { MediaColumns.DATA };
    			Cursor cursor = getContentResolver().query(selectedImage,
    					filePathColumn, null, null, null);
    			cursor.moveToFirst();
    			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    			FilePath = cursor.getString(columnIndex);
    			cursor.close();
    			if (FilePath!=null){
    				mBitmap=BitmapFactory.decodeFile(FilePath);
    //		mBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.flou);
    				mFluoLabGraphView.setBitmap(mBitmap);
    //		imageView.setImageBitmap(BitmapFactory.decodeFile(FilePath));
    			}else{
    				new AlertDialog.Builder(this)  
    						.setTitle("Error:")
    						.setMessage("Fail to get media source.")
    						.setPositiveButton("OK", null)
    						.show();
    			}
    			}
    		}
    }
	
	
	

}
