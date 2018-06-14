package com.sososeen09.multidexbuild.utils;

import android.app.Application;
import android.content.Context;

public class MultiDexApplication extends Application {
	  @Override
	  protected void attachBaseContext(Context base) {
	    super.attachBaseContext(base);
	    MultiDex.install(this);
	  }
}
