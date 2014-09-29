package zhengda.solarcholera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class TempTrndGraphView extends View implements Runnable{

	public Context mContext;
	public static final String Tag = "TempTrndGraphView";
	private float tempRangeHigh = 107.5f;
	private float tempRangeLow = 102.5f;
	private Paint mPaint;
	public Bitmap mBitmap;
	
	static public float[] temperatures = new float[1300];

	public TempTrndGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint= new Paint();
        new Thread(this).start();
        this.mContext=context;
	}

	public TempTrndGraphView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mPaint= new Paint();
        new Thread(this).start();
        this.mContext=context;
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);
        
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(90, 255, 0, 0));
        mPaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawRect(60,660-tempRangeHigh*5,800,660-tempRangeLow*5,mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextSize(30);
        canvas.drawLine(60, 0, 60, 560, mPaint);
        canvas.drawText("'C", 15, 25, mPaint);
        canvas.drawLine(60, 560, 800, 560, mPaint);
        canvas.drawText("min", 750, 550, mPaint);
        mPaint.setTextSize(25);
        for (int i = 20;i<=120;i=i+10) {
        	canvas.drawLine(60, 660-i*5, 50, 660-i*5, mPaint);
        	canvas.drawText(""+i,5,670-i*5,mPaint);
        }
        for (int i = 0; i<=20 ; i=i+2){
        	canvas.drawLine(60+i*35, 560, 60+i*35, 570, mPaint);
        	canvas.drawText(""+i, 50+i*35, 600, mPaint);
        }
        mPaint.setColor(Color.RED);
        for (int i=1;i<1300;i++){
        	int lastPoint = i-1;
        	while (lastPoint >0 && (temperatures[lastPoint]>200.0 || temperatures[lastPoint]<10.0))
        		lastPoint -- ;
        	if (temperatures[lastPoint]<200.0 && temperatures[lastPoint]>10.0
        			&& temperatures[i]<200.0 && temperatures[i]>10.0){
        		canvas.drawLine(60+lastPoint*35/60,660-5*temperatures[lastPoint],60+(i)*35/60,660-5*temperatures[i],mPaint);
        		lastPoint=i;
        	}
        }
	}

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
