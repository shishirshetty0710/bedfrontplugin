package com.msk.bedfrontplugin;

import android.app.Application;
import android.util.Log;

import com.bedfont.icosdk.ble.v2.*;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

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
	
	private static final int PERMISSION_REQUEST_LOCATION = 400;
    private static final int PERMISSION_REQUEST_BT = 401;
    private static final int REQUEST_PERMISSION_FINE_LOCATION = 9358;
    private static final int REQUEST_ENABLE_BT = 1;
	
	private static String CONNECT_STATUS = "EMPTY_STATUS";
	
	private SmokerlyzerBluetoothLeManager smokerlyzerBluetoothLeManager;

    static String TAG = "MSKBedfrontPlugin";
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		PluginResult result = new PluginResult(PluginResult.Status.OK, "Success");
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
                this.connect(callbackContext);
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
			//displayToast("smokerlyzerBluetoothLeManager>>TRY"+">>"+smokerlyzerBluetoothLeManager);
	   }catch(Exception e){
		   displayToast("smokerlyzerBluetoothLeManager>>CATCH"+">>"+smokerlyzerBluetoothLeManager);
		   //displayToast(e.toString());
	   }
	  
	   //displayToast("Ended initAsync v1");
	   
	  // PluginResult result = new PluginResult(PluginResult.Status.OK, "Success");
       //result.setKeepCallback(true);
       //callbackContext.sendPluginResult(result);
	   
	  
	   
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
			displayToast(connectResult+"<<<connectResult");
            switch(connectResult) {
                case SUCCESS:
					CONNECT_STATUS = "Finalized connection. Device is READY";
                    sendPluginResult(callbackContext, "\n Finalized connection. Device is READY");
                    break;
                case SUCCESS_NEEDS_RECOVERY:
				CONNECT_STATUS = "Finalized connection. Recovery function needs to be run on the sensor";
                    sendPluginResult(callbackContext, "\n Finalized connection. Recovery function needs to be run on the sensor");
                    break;
                case ZEROING:
				CONNECT_STATUS = " Zeroing the sensor, please wait...";
                    sendPluginResult(callbackContext, "\n Zeroing the sensor, please wait...");
                    break;
                case ERROR_FAILED_TO_FINALIZE:
				CONNECT_STATUS = "Connection process failed to finalize.";
                    sendPluginResult(callbackContext, "\n Connection process failed to finalize.");
                    break;
                case ERROR_FAILED_TO_CONNECT:
				CONNECT_STATUS = "Failed to connect to device";
                    sendPluginResult(callbackContext, "\n Failed to connect to device");
                    break;
                case ERROR_SCAN_FAILED:
				CONNECT_STATUS = "Failed to find device";
                    sendPluginResult(callbackContext, "\n Failed to find device");
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
	
	private void displayToast(String message) {
		final android.widget.Toast toast = android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), message, android.widget.Toast.LENGTH_SHORT);
		  toast.show();
	}
	
    
}