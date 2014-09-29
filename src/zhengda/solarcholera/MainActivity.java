package zhengda.solarcholera;

/**Copyright Erickson Lab**
 * 
 */

import java.util.ArrayList;
import java.util.Timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ActivityGroup;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActivityGroup {
	
	private String Tag_M="MainActivity";
	
	// Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
	Timer timer;
	
	/** Temperature data**/
	//Intt temperature data
	private int mTemps[]=new int[3];
	
	/** Tab Effect**/
	//Layout Views
	private PagerAdapter mPagerAdapter;
	private View TempTrendView;
	private View FlowControlView;
	private View FluoLabView;
	private MyViewPager mViewPager;
	private TextView t1,t2,t3;
	//ViewPager default settings
	private int offset = 0;
	private int currIndex = 0;
	//Cursor to show active pager & cursor width to fit screen
	private int bmpW; 
	private ImageView cursor;//
	//Views container
	private ArrayList<View> views;
	//Field members
	private Window window;
	
	
	/**Bluetooth serial chat**/
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    
    // Layout Views
    private TextView mTitle;
    /** Enable to start test
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;**/

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    /** Enable to start test
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;**/
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        //Ensure menu will show on screen
        window=getWindow();
        try {
        	window.addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
        }catch (NoSuchFieldException e) {
            // Ignore since this field won't exist in most versions of Android
        }
        catch (IllegalAccessException e) {
        	Log.w(TAG, "Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()", e);
        }
		/**Tab Effect**/
		//Prepare Views container
		views = new ArrayList<View>();
		mViewPager = (MyViewPager)findViewById(R.id.main_viewpager);
		//Obtain views of each activity
	    initView();
	    //Add views to view container ArrayList<View>
		addViews();
		//Initialize tab labels and set onclick listeners
		initTextView();
		//Initialize cursor to show active tab
		initImageView();
		//Set pager adapter
		mPagerAdapter = new PagerAdapter() {
			@Override
	        public int getItemPosition(Object object) {
	            return POSITION_NONE;
	        }
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		//Set view pager;
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		/**Outstream transmitter**/
		Intent intentToMain=this.getIntent();
		Bundle bundle=intentToMain.getExtras();
		if (bundle!=null && bundle.containsKey("rpmValue")){
			Log.d(TAG, "Intent on");
			//Get outstream from other activity
			String message = bundle.getString("rpmValue");
			//Send outstream to Arduino
			sendMessage(message);
		}
		
		/** Bluetooth serial chat between Android and Arduino**/
		// Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		
			
	}
	
	/****************************************************************************************
	 * Tab Effect Starts
	 */
	private void initTextView() {
		//Setup Tab labels with window layout
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		//Setup clicklistener on labels
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
	}
	
	private void initImageView() {
		//Set up cursor with window layout
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller).getWidth();
		//Obtain screen information
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//Fit Image to screen
		int screenW = dm.widthPixels;// 
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public void initView(){
		//Obtain view of each activity
		TempTrendView=getViews(TemperatureTrend.class,"TempTrend");
		FlowControlView=getViews(FlowController.class,"FlowControl");
		FluoLabView=getViews(FluorescenceLab.class,"FluoLab");
	}

	public void addViews(){
		//Add views to container ArrayList<View>
		views.add(TempTrendView);
		views.add(FlowControlView);
		views.add(FluoLabView);
	}

	public View getViews(Class<?> cls,String pageid){
		//Start respective activity and return them as View
		//Send received data from Bluetooth to other activities
		Intent intent = new Intent(MainActivity.this,cls);
		intent = intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		//Choose different data to send
		return getLocalActivityManager().startActivity(pageid, intent).getDecorView();
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		//Override OnPageChangeListener for animation effect
		int one = offset * 2 + bmpW;// 
		int two = one * 2;//

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				
            //     ActivitySecond.this.finish();
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);	
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			Log.d(Tag_M, "onPageSelected "+currIndex+" to "+arg0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		//Set up Onclicklistener for tab label
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	};
	/**
	 * Tab Effect Ends
	 **************************************************************************************************/
	
	
	/***************************************************************************************************
	 * Sending Outstream Starts
	 */
	@Override  
	protected void onNewIntent(Intent intentToMain){
		//For singleTask, receive intent with this part
		Bundle bundle=intentToMain.getExtras();
		if (bundle!=null && bundle.containsKey("rpmValue")){
			//Get outstream from other activity
			String message = bundle.getString("rpmValue");
			//Send outstream to Arduino
			Log.d(TAG, "Intent on, rpm = "+message);
			sendMessage(message);
		}
		
	}
	/**
	 * Sending Outstream Ends
	 **************************************************************************************************/
	
	/***************************************************************************************************
	 * Bluetooth Serial Chat Starts
	 */
	@Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        /** Enable to start test mode
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });**/

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

/**    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }**/

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            /** Enable to start test mode
            mOutEditText.setText(mOutStringBuffer);**/
        }
    }

    /** The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };**/

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    /** Enable to start test mode
                    mConversationArrayAdapter.clear();**/
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
            	/** Enable to start test mode
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);**/
                break;
            case MESSAGE_READ:
//              byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                int[] t=msg.getData().getIntArray("Time&Data");
            //    Log.d(TAG, "Total index "+t.length);
                mTemps[0]=t[1];mTemps[1]=t[2];mTemps[2]=t[3];  
 			//    String readMessage = msg.getData().getString("Time&Data");
            //    mConversationArrayAdapter.add(mConnectedDeviceName+": " + t[0]+" "+t[1]+" "+t[2]+" "+t[3]);
            //    byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
             //   String readMessage = new String(readBuf, 0, msg.arg1);
                Intent toTempTrend=new Intent();
                toTempTrend.setClass(MainActivity.this, TemperatureTrend.class);
                toTempTrend.putExtra("Time", t[0]);
                toTempTrend.putExtra("Temps", mTemps);
                int index=views.indexOf(TempTrendView);
                TempTrendView=getLocalActivityManager().startActivity("TempTrend", toTempTrend).getDecorView();
                views.set(index, TempTrendView);
                
                /** Enable to start test mode
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);**/
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    
	/**
	 * Bluetooth Serial Chat Ends
	 **************************************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            Log.d(TAG, "Request connect device secure");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
    /**    	Enable to allow different connection type
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            Log.d(TAG, "Request connect device insecure");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;**/
    /**		Enable to allow discoverable in app
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;**/
        case R.id.action_settings:
        	//To do:
        	//Pop up dialog to select mode;
        	return true;
        }
        return false;
    }

}
