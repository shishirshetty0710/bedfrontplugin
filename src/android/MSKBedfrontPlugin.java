package com.msk.bedfrontplugin;

import android.app.Application;
import android.util.Log;

import com.bedfont.icosdk.ble.v2.*;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;


public class MSKBedfrontPlugin extends CordovaPlugin {
	
	private static CallbackContext myAsyncCallbackContext = null;
	
	private static final int PERMISSION_REQUEST_LOCATION = 400;
    private static final int PERMISSION_REQUEST_BT = 401;
    private static final int REQUEST_PERMISSION_FINE_LOCATION = 9358;
    private static final int REQUEST_ENABLE_BT = 1;
	
	private static final String SUCCESS = "SUCCESS";
	private static final String SUCCESS_NEEDS_RECOVERY = "SUCCESS_NEEDS_RECOVERY";
	private static final String ZEROING = "ZEROING";
	private static final String ERROR_FAILED_TO_FINALIZE = "ERROR_FAILED_TO_FINALIZE";
	private static final String ERROR_FAILED_TO_CONNECT = "ERROR_FAILED_TO_CONNECT";
	private static final String ERROR_SCAN_FAILED = "ERROR_SCAN_FAILED";
	
	private static String CONNECT_STATUS = "EMPTY_STATUS";
	
	private SmokerlyzerBluetoothLeManager smokerlyzerBluetoothLeManager;

    static String TAG = "MSKBedfrontPlugin";
	
	public static int LATEST_READING = -1;
	
	
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		
		 myAsyncCallbackContext = null;
		 
		 
	}
	
	
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		//PluginResult result = new PluginResult(PluginResult.Status.OK, "Success");
        //result.setKeepCallback(true);
       //callbackContext.sendPluginResult(result);
	   
	   PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
	   result.setKeepCallback(true);
       callbackContext.sendPluginResult(result);
	   
        switch (action){
           
		   
			 case "showToast": {
                this.showToast(callbackContext);
                return true;
            }
		   
			 case "initAsync": {
                this.initAsync(callbackContext);
                return true;
            }
			case "connect": {
				myAsyncCallbackContext = callbackContext;
                this.connect(callbackContext);
                return true;
            }
			
			case "retry": {
				myAsyncCallbackContext = callbackContext;
                this.retry(callbackContext);
                return true;
            }
			
			case "disconnect": {
                this.disconnect(callbackContext);
                return true;
            }
			case "getSerialNumber" : {
				this.getSerialNumber(callbackContext);
                return true;
			}
			
			case "takeReading" : {
				LATEST_READING = -1;
				this.takeReading(callbackContext);
                return true;
			}
			
			case "getReading" : {
				this.getReading(callbackContext);
                return true;
			}
            
        }
		

        return false;
    }
	
	private void showToast(CallbackContext callbackContext) {
		  displayToast("Reached showToast v1 "+ callbackContext);
		  //sendPluginResult(callbackContext, "This is a custom message");
		  //JSONObject status = new JSONObject();
		  //try{
			//  status.put("status",  "This is a custom message");
		  //}catch(Exception e) {
			
		  //}
		  callbackContext.success(99999);
		  
		 // PluginResult result = new PluginResult(PluginResult.Status.OK, "This is a custom message");
       //result.setKeepCallback(true);
       //callbackContext.sendPluginResult(result);
	   
	   
	   
	}

    private void initAsync(CallbackContext callbackContext) {
        //Application currApp = this.cordova.getActivity().getApplication();
	   displayToast("Reached initAsync v2");
	   try{
		    smokerlyzerBluetoothLeManager = SmokerlyzerBluetoothLeManager.build(this.cordova.getContext());
	   }catch(Exception e){
		   displayToast("smokerlyzerBluetoothLeManager>>CATCH"+">>"+smokerlyzerBluetoothLeManager);
	   }
	   
	   callbackContext.success("Object is not null");
	   
	  // PluginResult result = new PluginResult(PluginResult.Status.OK, "Success");
       //result.setKeepCallback(true);
       //callbackContext.sendPluginResult(result);
	   
	  
	   
    }
	
	private void retry(CallbackContext callbackContext){
		
		connect(callbackContext);
    }
	
	
	private void disconnect(CallbackContext callbackContext){
		// Cleanup subscriptions
		if(smokerlyzerBluetoothLeManager!=null)
			smokerlyzerBluetoothLeManager.disconnect();
		
		PluginResult result = new PluginResult(PluginResult.Status.OK, "Success");
        callbackContext.sendPluginResult(result);
    }
	
	private void connect(CallbackContext callbackContext) {
		//displayToast(CONNECT_STATUS+">>"+smokerlyzerBluetoothLeManager);
		
		if(smokerlyzerBluetoothLeManager !=null){
			
		smokerlyzerBluetoothLeManager.scanAndConnect(new String[]{"iCOquit"}, connectResult -> {
			//displayToast(connectResult+"<<<connectResult");
            switch(connectResult) {
                case SUCCESS:
					CONNECT_STATUS = "Finalized connection. Device is READY";
                    //sendPluginResult(callbackContext, "\n Finalized connection. Device is READY");
					//JSONObject payload = new JSONObject();
					//try {
						//payload.put("status",CONNECT_STATUS);
					//} catch(Exception e) {
						
					//}

					sendEventMessage(CONNECT_STATUS);
					
					//respondWithEvent(SUCCESS, payload, null, callbackContext, true, true);
                    break;
                case SUCCESS_NEEDS_RECOVERY:
				CONNECT_STATUS = "Finalized connection. Recovery function needs to be run on the sensor";
                    //sendPluginResult(callbackContext, "\n Finalized connection. Recovery function needs to be run on the sensor");
					sendEventMessage(CONNECT_STATUS);
                    break;
                case ZEROING:
				CONNECT_STATUS = " Zeroing the sensor, please wait...";
                    //sendPluginResult(callbackContext, "\n Zeroing the sensor, please wait...");
					sendEventMessage(CONNECT_STATUS);
                    break;
                case ERROR_FAILED_TO_FINALIZE:
				CONNECT_STATUS = "Connection process failed to finalize.";
                    //sendPluginResult(callbackContext, "\n Connection process failed to finalize.");
					sendEventMessage(CONNECT_STATUS);
                    break;
                case ERROR_FAILED_TO_CONNECT:
				CONNECT_STATUS = "Failed to connect to device";
                    //sendPluginResult(callbackContext, "\n Failed to connect to device");
					sendEventMessage(CONNECT_STATUS);
                    break;
                case ERROR_SCAN_FAILED:
				CONNECT_STATUS = "Failed to find device";
                    //sendPluginResult(callbackContext, "\n Failed to find device");
					sendEventMessage(CONNECT_STATUS);
                    break;
            }
        });
		} else {
			displayToast("smokerlyzerBluetoothLeManager is NULL");
		}
		
		
		
	}
	
	private void getSerialNumber(CallbackContext callbackContext) {
		if(smokerlyzerBluetoothLeManager!=null) {
			
		
		smokerlyzerBluetoothLeManager.getIsConnected((r2) -> {
			displayToast("Value of R2= " + r2);
                if (r2) {
                    smokerlyzerBluetoothLeManager.getSerialNumber((r) -> {
						//sendPluginResult(callbackContext, "\n serial number = " + r);
						callbackContext.success("\n serial number = " + r);
						displayToast("\n serial number = " + r);
                    });
                } else {
                    //sendPluginResult(callbackContext, "\n device not connected");
					callbackContext.success("\n device not connected");
					displayToast("\n device not connected");
                }

            });
		} else {
			displayToast("smokerlyzerBluetoothLeManager is null");
		}
	}
	
	
	private void sendPluginResult(CallbackContext callbackContext, String message){
		
				//JSONObject status = new JSONObject();
			try{
				//status.put("status", message);
				PluginResult result = new PluginResult(PluginResult.Status.OK, message);
				result.setKeepCallback(true);
				callbackContext.sendPluginResult(result);
			} 
			catch (Exception e){
				PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
				result.setKeepCallback(true);
				callbackContext.sendPluginResult(result);
			}
		
		
	}
	
	
	private void takeReading(CallbackContext callbackContext) {
		if(smokerlyzerBluetoothLeManager!=null) {
			smokerlyzerBluetoothLeManager.startBreathTest(new ReadingComplete(){
				
				@Override
				public void OnComplete​(java.lang.Boolean success, int reading, SmokerlyzerBluetoothLeManager.StatusCodeConstants status) {
					
					LATEST_READING = reading;
				}
			});
		} else{
			callbackContext.error("\n device not connected");
		}
	}
	
	private void getReading(CallbackContext callbackContext) {
		callbackContext.success("LATEST_READING: "+LATEST_READING);
	}
	
	private void displayToast(String message) {
		final android.widget.Toast toast = android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), message, android.widget.Toast.LENGTH_SHORT);
		  toast.show();
	}
	
	
	
	
	
	private void sendEventMessage(String message) {
		
		PluginResult result = new PluginResult(PluginResult.Status.OK,
                        message);
		result.setKeepCallback(false);
        if (myAsyncCallbackContext != null) {
              myAsyncCallbackContext.sendPluginResult(result);
              myAsyncCallbackContext = null;
       
		//respondWithEvent(eventName, payload, null, callbackContext, true, true);
	   }
    
	}
}