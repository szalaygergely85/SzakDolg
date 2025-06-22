package com.zen_vy.chat.activity.image;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener {

   private final ScaleGestureDetector scaleDetector;
   private final Matrix baseMatrix = new Matrix();
   private final Matrix drawMatrix = new Matrix();
   private float scaleFactor = 1.0f;

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
   public void setImageDrawable(Drawable drawable) {
      super.setImageDrawable(drawable);
      post(this::fitImageToView); // Refit after image is set
   }

   @Override
   public boolean onScale(ScaleGestureDetector detector) {
      float scale = detector.getScaleFactor();

      float newScale = scaleFactor * scale;
      newScale = Math.max(0.5f, Math.min(newScale, 3.0f)); // Clamp zoom range

      float factor = newScale / scaleFactor;
      scaleFactor = newScale;

      drawMatrix.postScale(factor, factor, detector.getFocusX(), detector.getFocusY());
      setImageMatrix(drawMatrix);
      return true;
   }

   @Override
   public boolean onScaleBegin(ScaleGestureDetector detector) {
      return true;
   }

   @Override
   public void onScaleEnd(ScaleGestureDetector detector) {
      // No action needed
   }

   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      post(this::fitImageToView);  // Defer until layout is ready
   }

   private void fitImageToView() {
      Drawable drawable = getDrawable();
      if (drawable == null) return;

      float viewWidth = getWidth();
      float viewHeight = getHeight();
      float drawableWidth = drawable.getIntrinsicWidth();
      float drawableHeight = drawable.getIntrinsicHeight();

      if (drawableWidth == 0 || drawableHeight == 0 || viewWidth == 0 || viewHeight == 0) return;

      float scale = Math.min(viewWidth / drawableWidth, viewHeight / drawableHeight);
      float dx = (viewWidth - drawableWidth * scale) / 2f;
      float dy = (viewHeight - drawableHeight * scale) / 2f;

      baseMatrix.setScale(scale, scale);
      baseMatrix.postTranslate(dx, dy);

      drawMatrix.set(baseMatrix);
      setImageMatrix(drawMatrix);

      scaleFactor = scale; // Save initial scale
   }
}
