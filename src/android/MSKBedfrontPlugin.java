package com.msk.connexientplugin;

import android.app.Application;
import android.util.Log;

import com.bedfont.icosdk.ble.v2.*;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

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
	
	private SmokerlyzerBluetoothLeManager smokerlyzerBluetoothLeManager;

    static String TAG = "MSKBedfrontPlugin";
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action){
           
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

    private void initAsync(CallbackContext callbackContext) {
        //Application currApp = this.cordova.getActivity().getApplication();
       
	   smokerlyzerBluetoothLeManager = SmokerlyzerBluetoothLeManager.build(this.cordova.getContext());
	   
    }
	
	
	private void disconnect(CallbackContext callbackContext){
		// Cleanup subscriptions
		if(smokerlyzerBluetoothLeManager!=null)
			smokerlyzerBluetoothLeManager.disconnect();
    }
	
	private void connect(CallbackContext callbackContext) {
		
		smokerlyzerBluetoothLeManager.scanAndConnect(new String[]{"iCOquit"}, connectResult -> {
            switch(connectResult) {
                case SUCCESS:
                    sendPluginResult(callbackContext, "\n Finalized connection. Device is READY");
                    break;
                case SUCCESS_NEEDS_RECOVERY:
                    sendPluginResult(callbackContext, "\n Finalized connection. Recovery function needs to be run on the sensor");
                    break;
                case ZEROING:
                    sendPluginResult(callbackContext, "\n Zeroing the sensor, please wait...");
                    break;
                case ERROR_FAILED_TO_FINALIZE:
                    sendPluginResult(callbackContext, "\n Connection process failed to finalize.");
                    break;
                case ERROR_FAILED_TO_CONNECT:
                    sendPluginResult(callbackContext, "\n Failed to connect to device");
                    break;
                case ERROR_SCAN_FAILED:
                    sendPluginResult(callbackContext, "\n Failed to find device");
                    break;
            }
        });
		
	}
	
	private void getSerialNumber(CallbackContext callbackContext) {
		smokerlyzerBluetoothLeManager.getIsConnected((r2) -> {
                if (r2) {
                    smokerlyzerBluetoothLeManager.getSerialNumber((r) -> {
						sendPluginResult(callbackContext, "\n serial number = " + r);
                    });
                } else {
                    sendPluginResult(callbackContext, "\n device not connected");
                }

            });
	}
	
	
	private void sendPluginResult(CallbackContext callbackContext, String message){
		
			try{
				PluginResult result = new PluginResult(PluginResult.Status.OK, message);
				callbackContext.sendPluginResult(result);
			} 
			catch (Exception e){
				PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
				callbackContext.sendPluginResult(result);
			}
		
		
	}
	
    
}