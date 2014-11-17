package org.roc.bounceprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.util.Property;

/**
 * @author Rocko
 * @date 2014年11月17日 下午7:36:49
 */
public class BounceProgressBar extends View {

	private static final String TAG = BounceProgressBar.class.getSimpleName();
	/**
	 * the drawable(bitmap) bounce speed,the bounce animation one-way duration
	 */
	private int speed;
	/**
	 * single bitmap or drawable size
	 */
	private int size;
	/**
	 * shape
	 */
	private int shape;
	/**
	 * resource drawable
	 */
	private Drawable src;
	/**
	 * canvas's paint in onDraw() method
	 */
	private Paint mPaint;
	/**
	 * BounceProgressBar Widget's width
	 */
	private int mWidth;
	/**
	 * BounceProgressBar Widget's height
	 */
	private int mHeight;

	/**
	 * 第一个图像的top位置
	 */
	private int firstBitmapTop = Integer.MIN_VALUE;
	private int secondBitmapTop = Integer.MIN_VALUE;
	private int thirdBitmapTop = Integer.MIN_VALUE;
	// loading...
	// private float firstBitmapMatrixSY = 1f;
	// private float secondBitmapMatrixSY = 1f;
	// private float thirdBitmapMatrixSY = 1f;
	private Matrix firstBitmapMatrix;
	private Matrix secondBitmapMatrix;
	private Matrix thirdBitmapMatrix;
	/**
	 * BounceProgressBar hold three bitmap
	 */
	private Bitmap firstBitmap;
	private Bitmap secondBitmap;
	private Bitmap thirdBitmap;
	/**
	 * bitmap(or drawable) count of BounceProgressBar
	 */
	private static final int count = 3;
	/**
	 * The first bitmap's location of the horizontal position
	 */
	private float firstDX;
	/**
	 * The second bitmap's location of the horizontal position
	 */
	private float secondDX;
	/**
	 * The third bitmap's location of the horizontal position
	 */
	private float thirdDX;

	public static interface Shape {
		static final int original = 0;
		static final int circle = 1;
		static final int pentagon = 2;
		static final int rhombus = 3;
		static final int heart = 4;
	}

	public BounceProgressBar(Context context) {
		this(context, null, 0);
	}

	public BounceProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BounceProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (null == attrs) {
			return;
		}
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BounceProgressBar);
		speed = a.getInt(R.styleable.BounceProgressBar_speed, 250);
		size = a.getDimensionPixelSize(R.styleable.BounceProgressBar_singleSrcSize, 50);
		shape = a.getInt(R.styleable.BounceProgressBar_shape, 0);
		src = a.getDrawable(R.styleable.BounceProgressBar_src);
		a.recycle();

		if (null == src) {
			throw new NullPointerException("BounceProgressBar did not define src Attribute.");
		}
		Bitmap srcBitmap = drawable2Bitmap(src);
		firstBitmap = initShapeBitmap(shape, srcBitmap, 255);
		secondBitmap = initShapeBitmap(shape, srcBitmap, 205);
		thirdBitmap = initShapeBitmap(shape, srcBitmap, 175);
		srcBitmap.recycle();

		mHeight = 4 * size;
		mWidth = 5 * size;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		firstBitmapMatrix = new Matrix();
		secondBitmapMatrix = new Matrix();
		thirdBitmapMatrix = new Matrix();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? mWidth = sizeWidth : mWidth,
				(modeHeight == MeasureSpec.EXACTLY) ? mHeight = sizeHeight : mHeight);

		firstDX = mWidth / 4 - size / 2;
		secondDX = mWidth / 2 - size / 2;
		thirdDX = 3 * mWidth / 4 - size / 2;
	}

	private AnimatorSet bouncer;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (bouncer == null || !bouncer.isRunning()) {
			ObjectAnimator firstAnimator = initDrawableAnimator(firstBitmapTopProperty, speed, size / 2,
					mHeight - size);
			ObjectAnimator secondAnimator = initDrawableAnimator(secondBitmapTopProperty, speed, size / 2,
					mHeight - size);
			secondAnimator.setStartDelay(100);
			ObjectAnimator thirdAnimator = initDrawableAnimator(thirdBitmapTopProperty, speed, size / 2,
					mHeight - size);
			thirdAnimator.setStartDelay(200);
			bouncer = new AnimatorSet();
			bouncer.playTogether(firstAnimator, secondAnimator, thirdAnimator);
			bouncer.start();
		}
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		if (bouncer != null) {
			bouncer.cancel();
		}
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		/* draw three bitmap */
		firstBitmapMatrix.reset();
		firstBitmapMatrix.postTranslate(firstDX, firstBitmapTop);
		// firstBitmapMatrix.postScale(1f, firstBitmapMatrixSY, 1f, 1f);
		secondBitmapMatrix.reset();
		secondBitmapMatrix.setTranslate(secondDX, secondBitmapTop);
		// secondBitmapMatrix.postScale(1f, secondBitmapMatrixSY, 1f, 1f);

		thirdBitmapMatrix.reset();
		thirdBitmapMatrix.setTranslate(thirdDX, thirdBitmapTop);
		// thirdBitmapMatrix.postScale(1f, thirdBitmapMatrixSY, 1f, 1f);

		canvas.drawBitmap(firstBitmap, firstBitmapMatrix, mPaint);
		canvas.drawBitmap(secondBitmap, secondBitmapMatrix, mPaint);
		canvas.drawBitmap(thirdBitmap, thirdBitmapMatrix, mPaint);
	}

	private ObjectAnimator initDrawableAnimator(Property<BounceProgressBar, Integer> property, int duration,
			int startValue, int endValue) {
		ObjectAnimator animator = ObjectAnimator.ofInt(this, property, startValue, endValue);
		animator.setDuration(duration);
		animator.setRepeatCount(Animation.INFINITE);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.setInterpolator(new AccelerateInterpolator());
		return animator;
	}

	/**
	 * get shape bitmap
	 * 
	 * @param shape
	 * @param srcBitmap
	 * @param alpha
	 * @return
	 */
	private Bitmap initShapeBitmap(int shape, Bitmap srcBitmap, int alpha) {
		BitmapShader bitmapShader = new BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Path path;
		ShapeDrawable shapeDrawable = new ShapeDrawable();
		shapeDrawable.getPaint().setAntiAlias(true);
		shapeDrawable.getPaint().setShader(bitmapShader);
		shapeDrawable.setBounds(0, 0, size, size);
		shapeDrawable.setAlpha(alpha);
		switch (shape) {
		case Shape.circle:
			shapeDrawable.setShape(new OvalShape());
			break;
		case Shape.pentagon:
			path = new Path();
			// The Angle of the pentagram
			float radian = (float) (Math.PI * 36 / 180);
			float radius = size / 2;
			// In the middle of the radius of the pentagon
			float radius_in = (float) (radius * Math.sin(radian / 2) / Math.cos(radian));
			// The starting point of the polygon
			path.moveTo((float) (radius * Math.cos(radian / 2)), 0);
			path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.sin(radian)),
					(float) (radius - radius * Math.sin(radian / 2)));
			path.lineTo((float) (radius * Math.cos(radian / 2) * 2),
					(float) (radius - radius * Math.sin(radian / 2)));
			path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.cos(radian / 2)),
					(float) (radius + radius_in * Math.sin(radian / 2)));
			path.lineTo((float) (radius * Math.cos(radian / 2) + radius * Math.sin(radian)),
					(float) (radius + radius * Math.cos(radian)));
			path.lineTo((float) (radius * Math.cos(radian / 2)), (float) (radius + radius_in));
			path.lineTo((float) (radius * Math.cos(radian / 2) - radius * Math.sin(radian)),
					(float) (radius + radius * Math.cos(radian)));
			path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.cos(radian / 2)),
					(float) (radius + radius_in * Math.sin(radian / 2)));
			path.lineTo(0, (float) (radius - radius * Math.sin(radian / 2)));
			path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.sin(radian)),
					(float) (radius - radius * Math.sin(radian / 2)));
			path.close();// Make these points closed polygons
			shapeDrawable.setShape(new PathShape(path, size, size));
			break;
		case Shape.rhombus:
			path = new Path();
			path.moveTo(size / 2, 0);
			path.lineTo(0, size / 2);
			path.lineTo(size / 2, size);
			path.lineTo(size, size / 2);
			path.close();
			shapeDrawable.setShape(new PathShape(path, size, size));
			break;
		case Shape.heart:
			path = new Path();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			Matrix matrix = new Matrix();
			Region region = new Region();
			RectF ovalRect = new RectF(size / 4, 0, size - (size / 4), size);
			path.addOval(ovalRect, Path.Direction.CW);
			matrix.postRotate(42, size / 2, size / 2);
			path.transform(matrix, path);
			region.setPath(path, new Region((int) size / 2, 0, (int) size, (int) size));
			canvas.drawPath(region.getBoundaryPath(), paint);

			matrix.reset();
			path.reset();
			path.addOval(ovalRect, Path.Direction.CW);
			matrix.postRotate(-42, size / 2, size / 2);
			path.transform(matrix, path);
			region.setPath(path, new Region(0, 0, (int) size / 2, (int) size));
			canvas.drawPath(region.getBoundaryPath(), paint);
			return bitmap;// return <--
		case Shape.original:
			break;
		default:
			break;
		}
		shapeDrawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Drawable → Bitmap(the size is "size")
	 */
	private Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, size, size);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * firstBitmapTop's Property. The change of the height through canvas is
	 * onDraw() method.
	 */
	private Property<BounceProgressBar, Integer> firstBitmapTopProperty = new Property<BounceProgressBar, Integer>(
			Integer.class, "firstDrawableTop") {
		@Override
		public Integer get(BounceProgressBar obj) {
			return obj.firstBitmapTop;
		}

		public void set(BounceProgressBar obj, Integer value) {
			obj.firstBitmapTop = value;
			invalidate();
		};
	};
	/**
	 * secondBitmapTop's Property. The change of the height through canvas is
	 * onDraw() method.
	 */
	private Property<BounceProgressBar, Integer> secondBitmapTopProperty = new Property<BounceProgressBar, Integer>(
			Integer.class, "secondDrawableTop") {
		@Override
		public Integer get(BounceProgressBar obj) {
			return obj.secondBitmapTop;
		}

		public void set(BounceProgressBar obj, Integer value) {
			obj.secondBitmapTop = value;
			invalidate();
		};
	};
	/**
	 * thirdBitmapTop's Property. The change of the height through canvas is
	 * onDraw() method.
	 */
	private Property<BounceProgressBar, Integer> thirdBitmapTopProperty = new Property<BounceProgressBar, Integer>(
			Integer.class, "thirdDrawableTop") {
		@Override
		public Integer get(BounceProgressBar obj) {
			return obj.thirdBitmapTop;
		}

		public void set(BounceProgressBar obj, Integer value) {
			obj.thirdBitmapTop = value;
			invalidate();
		};
	};

	public void setDrawable(Drawable drawable) {
		setDrawable(drawable, shape);
	}

	public void setDrawable(Drawable drawable, int shape) {
		setBitmap(drawable2Bitmap(drawable), shape);
	}

	public void setBitmap(Bitmap bitmap) {
		setBitmap(bitmap, shape);
	}

	public void setBitmap(Bitmap bitmap, int shape) {
		firstBitmap = initShapeBitmap(shape, bitmap, 255);
		secondBitmap = initShapeBitmap(shape, bitmap, 205);
		thirdBitmap = initShapeBitmap(shape, bitmap, 175);
		invalidate();
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		requestLayout();
	}

}
