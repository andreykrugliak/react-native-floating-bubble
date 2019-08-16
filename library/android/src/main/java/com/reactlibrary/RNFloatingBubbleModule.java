
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import android.os.Bundle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.util.Log;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


public class RNFloatingBubbleModule extends ReactContextBaseJavaModule {

  private BubblesManager bubblesManager;
  private final ReactApplicationContext reactContext;
  private BubbleLayout bubbleView;
  private BubbleLayout expandedView;

  public RNFloatingBubbleModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

    // try {
    //   initializeBubblesManager();
    // } catch (Exception e) {

    // }
  }

  public class ButtonData {
    public String title;
    public String value;
  }

  @Override
  public String getName() {
    return "RNFloatingBubble";
  }

  @ReactMethod // Notates a method that should be exposed to React
  public void showFloatingBubble(ReadableArray data, final Promise promise) {
    try {
      this.addNewBubble(data);
      promise.resolve("");
    } catch (Exception e) {
      promise.reject("");
    }
  }  

  @ReactMethod // Notates a method that should be exposed to React
  public void hideFloatingBubble(final Promise promise) {
    try {
      this.removeBubble();
      promise.resolve("");
    } catch (Exception e) {
      promise.reject("");
    }
  }  
  
  @ReactMethod // Notates a method that should be exposed to React
  public void requestPermission(final Promise promise) {
    try {
      this.requestPermissionAction(promise);
    } catch (Exception e) {
    }
  }  
  
  @ReactMethod // Notates a method that should be exposed to React
  public void checkPermission(final Promise promise) {
    try {
      promise.resolve(hasPermission());
    } catch (Exception e) {
      promise.reject("");
    }
  }  
  
  @ReactMethod // Notates a method that should be exposed to React
  public void initialize(final Promise promise) {
    try {
      this.initializeBubblesManager();
      promise.resolve("");
    } catch (Exception e) {
      promise.reject("");
    }
  }

  private void setClipboard(String text) {
    ClipData clipdata = ClipData.newPlainText(null, text);
    ClipboardManager clipboard = getClipboardService();
    clipboard.setPrimaryClip(clipdata);
  }

  private ClipboardManager getClipboardService() {
    return (ClipboardManager) reactContext.getSystemService(reactContext.CLIPBOARD_SERVICE);
  }

  private void addNewBubble(ReadableArray data) {
    this.removeBubble();
    bubbleView = (BubbleLayout) LayoutInflater.from(reactContext).inflate(R.layout.bubble_layout, null);
    final FloatingActionsMenu button = bubbleView.findViewById(R.id.multiple_actions);
    for (int i = 0; i <  data.size(); i++) {
      FloatingActionButton action = new FloatingActionButton(reactContext);
      final ReadableMap item = data.getMap(i);
      final String title = item.getString("title");
      final String value = item.getString("value");
      action.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          setClipboard(item.getString("value"));
        }
      });
      action.setIcon(R.drawable.copy_icon);
      action.setTitle(title + ": " + value);
      action.setStrokeVisible(false);
      button.addButton(action);

    }
    bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
      @Override
      public void onBubbleRemoved(BubbleLayout bubble) {
        bubbleView = null;

        sendEvent("floating-bubble-remove");
      }
    });
    bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

      @Override
      public void onBubbleClick(BubbleLayout bubble) {

        sendEvent("floating-bubble-press");
      }
    });
    bubbleView.setShouldStickToWall(true);
    bubblesManager.addBubble(bubbleView, 50, 50);
  }

  private boolean hasPermission(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.canDrawOverlays(reactContext);
    }
    return true;
  }

  private void removeBubble() {
    if(bubbleView != null){
      try{
        bubblesManager.removeBubble(bubbleView);
      } catch(Exception e){

      }
    }
  }


  public void requestPermissionAction(final Promise promise) {
    if (!hasPermission()) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + reactContext.getPackageName()));
      Bundle bundle = new Bundle();
      reactContext.startActivityForResult(intent, 0, bundle);
    } 
    if (hasPermission()) {
      this.initializeBubblesManager();
      promise.resolve("");
    } else {
      promise.reject("");
    }
  }

  private void initializeBubblesManager() {
    bubblesManager = new BubblesManager.Builder(reactContext).setTrashLayout(R.layout.bubble_trash_layout)
        .setInitializationCallback(new OnInitializedCallback() {
          @Override
          public void onInitialized() {
            // addNewBubble(80,80);
          }
        }).build();
    bubblesManager.initialize();
  }

  private void sendEvent(String eventName) {
    WritableMap params = Arguments.createMap();
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }
}