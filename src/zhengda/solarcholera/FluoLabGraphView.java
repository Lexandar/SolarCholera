package zhengda.solarcholera;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class FluoLabGraphView extends View implements Runnable {
	
	public Context mContext;
	public static final String Tag = "FluoLabGraphView";
	
	public static String FilePath;
	public static int Rotates = 0;
	private  Paint mPaint;
	public Bitmap mBitmap;
	public static Bitmap SourceGraph;
	private DataGenerator mDG;
	private PathEffect effects;
	private int numOfChannels = 8;
//	Bitmap SourceGraph = BitmapFactory.decodeResource(getResources(), R.drawable.flou);
	
	
	public FluoLabGraphView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mPaint= new Paint();
        new  Thread(this).start();
        this.mContext=context;
    /**    DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		ValueSet.screenWidth = dm.widthPixels;
		ValueSet.screenHeight = dm.heightPixels;
		Log.d(Tag, "screen width =" + ValueSet.screenWidth + ",screen height=" + ValueSet.screenHeight);
	*/	effects = new DashPathEffect(new float[] {10, 10}, 0);
		mDG = new DataGenerator(mBitmap);
		
	}

	public FluoLabGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint= new Paint();
        new  Thread(this).start();
        this.mContext=context;
    /**    DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		ValueSet.screenWidth = dm.widthPixels;
		ValueSet.screenHeight = dm.heightPixels;
		Log.d(Tag, "screen width =" + ValueSet.screenWidth + ",screen height=" + ValueSet.screenHeight);
	*/	effects = new DashPathEffect(new float[] {10, 10}, 0);
		mDG = new DataGenerator(mBitmap);
	}

	public FluoLabGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mPaint= new Paint();
        new  Thread(this).start();
        this.mContext=context;
    /**    DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		ValueSet.screenWidth = dm.widthPixels;
		ValueSet.screenHeight = dm.heightPixels;
	*/	Log.d(Tag, "screen width =" + ValueSet.screenWidth + ",screen height=" + ValueSet.screenHeight);
		effects = new DashPathEffect(new float[] {10, 10}, 0);
		mDG = new DataGenerator(mBitmap);
	}

	@Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);
        
        mPaint.setAntiAlias(true);

        if (mBitmap != null){
        	canvas.drawBitmap(mBitmap, 0, 0, null);
        	
        
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        mPaint.setPathEffect(null);
        canvas.drawLine(0,ValueSet.screenWidth+3, ValueSet.screenWidth, ValueSet.screenWidth+3, mPaint);
   
        //Generate Date
        
        
        //Draw Curve
        mPaint.setColor(Color.RED);
        for (int i=2; i<ValueSet.screenWidth-3;i++){
        	canvas.drawLine(i,ValueSet.screenWidth-DataGenerator.YAxisLocation[i],i+1,ValueSet.screenWidth-DataGenerator.YAxisLocation[i+1],mPaint);
        }
                
        
        mPaint.setPathEffect(effects);
        mPaint.setColor(Color.BLACK);
  //      if(DataGenerator.Bar<=ValueSet.screenWidth/2)
   //     	canvas.drawLine(0, ValueSet.screenWidth-DataGenerator.Bar, ValueSet.screenWidth, ValueSet.screenWidth-DataGenerator.Bar, mPaint);
        //End Draw Curve
        
        float step = (ValueSet.RightEdgeX-ValueSet.LeftEdgeX)/numOfChannels;
        float Boundary[] = new float[numOfChannels+1];
        for (int i=0; i<=numOfChannels; i++)
        	Boundary[i] = ValueSet.LeftEdgeX + step * i;
        
  //      mPaint.setARGB(45, 255, 255, 0);
  //      canvas.drawRect(Boundary[0], 0, Boundary[1],ValueSet.screenWidth, mPaint);
        
  //      mPaint.setARGB(45, 0, 255, 0);
  //      canvas.drawRect(Boundary[1], 0, Boundary[2],ValueSet.screenWidth, mPaint);
        
  //      mPaint.setARGB(45, 0, 0, 255);
  //      canvas.drawRect(Boundary[2], 0, Boundary[3],ValueSet.screenWidth, mPaint);
        
  //      mPaint.setARGB(45, 255, 0, 255);
  //      canvas.drawRect(Boundary[3], 0, Boundary[4],ValueSet.screenWidth, mPaint);
        
        mPaint.setColor(Color.rgb(232, 232, 255));
        mPaint.setStrokeWidth(2);
        mPaint.setPathEffect(null);
        
        for (int i=0;i<=numOfChannels;i++){
        	mPaint.setColor(Color.rgb(232, 232, 255));
        	canvas.drawLine(Boundary[i], 0, Boundary[i], ValueSet.screenWidth/2, mPaint);
        	mPaint.setColor(Color.BLACK);
        	canvas.drawLine(Boundary[i], ValueSet.screenWidth/2, Boundary[i], ValueSet.screenWidth, mPaint);
        }
        }
	}
	
	private int toscreenX (double x) {
		return 0;
	}
	
	private int toscreenY (double y) {
		return 0;
	}
	
	public void setBitmap(Bitmap mBitmap){
	// 	Bitmap mBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.flou);
		
		SourceGraph = mBitmap;
		if (SourceGraph != null)
			fitBitmap();
		
	}
	
	public void fitBitmap(){ 
		if(null!=SourceGraph){
	//	SourceGraph= BitmapFactory.decodeResource(getResources(), R.drawable.flou);
		
		Bitmap tempBitmap;// = setRotates(SourceGraph);
				
		Matrix matrix = new Matrix();
        matrix.postRotate(Rotates);
        int Width = SourceGraph.getWidth();
        int Height = SourceGraph.getHeight();
        
        tempBitmap = Bitmap.createBitmap(SourceGraph, 0, 0, Width, Height, matrix, true);
        
        
        Width = tempBitmap.getWidth();
        Height = tempBitmap.getHeight();
        int newWidth = ValueSet.screenWidth;
        int newHeight = newWidth / 2;
        float scaleX = ((float) newWidth) / ((float)Width);
        float scaleY = ((float) newHeight) / ((float)Height);
        
        Matrix matrix1 = new Matrix();
        matrix1.postScale(scaleX, scaleY);
        
        mBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix1, true);
		
        mDG.getData(mBitmap);
        Log.d(Tag, ""+Width+" "+Height);
		Log.d(Tag, ""+scaleX+" "+scaleY);
		Log.d(Tag, "Height:"+tempBitmap.getHeight()+" Width:"+tempBitmap.getWidth());
		Log.d(Tag, "Height:"+mBitmap.getHeight()+" Width:"+mBitmap.getWidth());
		postInvalidate();
		}}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!Thread.currentThread().isInterrupted()) {
            try{
                  Thread.sleep(100);
            } catch(InterruptedException e) {
              // TODO: handle exception
              Thread.currentThread().interrupt();
            }
            postInvalidate(); 
       }
	}

}