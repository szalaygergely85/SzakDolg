package com.zen_vy.chat.activity.image;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ZoomableImageView
   extends androidx.appcompat.widget.AppCompatImageView
   implements ScaleGestureDetector.OnScaleGestureListener {

   private float scaleFactor = 1.0f;
   private ScaleGestureDetector scaleDetector;

   public ZoomableImageView(Context context, AttributeSet attrs) {
      super(context, attrs);
      scaleDetector = new ScaleGestureDetector(context, this);
      setScaleType(ScaleType.MATRIX);
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      scaleDetector.onTouchEvent(event);
      return true;
   }

   @Override
   public boolean onScale(ScaleGestureDetector detector) {
      scaleFactor *= detector.getScaleFactor();
      scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f));

      Matrix matrix = new Matrix();
      matrix.setScale(
         scaleFactor,
         scaleFactor,
         detector.getFocusX(),
         detector.getFocusY()
      );
      setImageMatrix(matrix);

      return true;
   }

   @Override
   public boolean onScaleBegin(ScaleGestureDetector detector) {
      return true;
   }

   @Override
   public void onScaleEnd(ScaleGestureDetector detector) {}
}
