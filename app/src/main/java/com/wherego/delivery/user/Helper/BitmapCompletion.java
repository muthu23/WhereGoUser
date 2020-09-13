package com.wherego.delivery.user.Helper;

import android.graphics.Bitmap;

/**
 * To be used with bitmap task worker for creating short scaled images
 * @author Rajesh sharma
 *
 */
public interface BitmapCompletion {
	
	public void onBitmapScaleComplete(Bitmap bmp);

}
