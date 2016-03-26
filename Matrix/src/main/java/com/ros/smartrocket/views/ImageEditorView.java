package com.ros.smartrocket.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.ros.smartrocket.utils.UIUtils;

public class ImageEditorView extends View {
    private Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Bitmap croppedBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    private Paint bitmapPaint = new Paint();
    private boolean isCroppedBitmapChanged = true;

    private Paint eraserPaint = new Paint();

    private final int MATRIX_POINT_AMOUNT = 9;
    private final int EVENT_POINT_AMOUNT = 4;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    float[] matrixValues = new float[MATRIX_POINT_AMOUNT];
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private PointF midPoint = new PointF();

    private float oldDist = 1.0f;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final double MN_10 = 10.0;

    private int mode = NONE;
    private float fangle;
    float[] lastEvent = null;
    private int viewWidth, viewHeight;
    private boolean isEmpty = true;
    private boolean canRotate = true;

    private OnImageChangeListener onImageChangeListener;

    public ImageEditorView(Context context) {
        super(context);

        setEraserSettings();
    }

    public ImageEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.isEmpty = false;
        this.isCroppedBitmapChanged = true;

        invalidate();
    }

    public void setOldBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.isEmpty = false;
        this.isCroppedBitmapChanged = false;

        invalidate();
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, matrix, bitmapPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (viewWidth == 0 || viewHeight == 0) {
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                lastEvent = null;
                mode = DRAG;

                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                endPoint.set(event.getX(), event.getY());
                oldDist = spacing(event);

                if (oldDist > MN_10) {
                    savedMatrix.set(matrix);
                    matrix.getValues(matrixValues);
                    setMidPoint(midPoint, event);
                    mode = ZOOM;
                }
                lastEvent = new float[EVENT_POINT_AMOUNT];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                fangle = rotation(event);
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                this.fangle = 0;
                lastEvent = null;

                isCroppedBitmapChanged = true;
                if (onImageChangeListener != null) {
                    onImageChangeListener.onImageChange();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                this.fangle = 0;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    translate(event.getX(), event.getY());
                } else if (mode == ZOOM) {

                    float newDist = spacing(event);
                    // float angle = rotation(event);

                    if (newDist > MN_10) {

                        matrix.set(savedMatrix);
                        matrix.getValues(matrixValues);

                        float scale = newDist / oldDist;

                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        matrix.getValues(matrixValues);

                    }

                    if (lastEvent != null) {
                        float newRot = rotation(event);
                        matrix.postRotate(newRot - fangle, midPoint.x, midPoint.y);
                    }
                }
                break;
        }

        invalidate();

        return true;

    }

    private float rotation(MotionEvent event) {
        float degrees = 0;
        if (canRotate) {
            double delta_x = (event.getX(0) - event.getX(1));
            double delta_y = (event.getY(0) - event.getY(1));
            double radians = Math.atan2(delta_y, delta_x);
            degrees = (float) Math.toDegrees(radians);
        }
        return degrees;
    }

    private float spacing(MotionEvent event) {
        // Spacing between fingers
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void setMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void translate(float x, float y) {
        matrix.getValues(matrixValues);

        float dx = x - startPoint.x;
        float dy = y - startPoint.y;

        matrix.postTranslate(dx, dy);

    }

    public Bitmap getSourceBitmap() {
        return bitmap;
    }

    public Bitmap getScaledCropedBitmap(int width, int height) {
        return Bitmap.createScaledBitmap(getCroppedBitmap(), width, height, false);
    }

    public Bitmap getCroppedBitmap() {
        if (isCroppedBitmapChanged) {
            Bitmap croppedImage = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(croppedImage);
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.drawCircle(0, 0, 1, eraserPaint);

            croppedBitmap = croppedImage;
            isCroppedBitmapChanged = false;
        }

        return croppedBitmap;
    }

    public float[] getMatrixValues() {
        matrix.getValues(matrixValues);
        return matrixValues;
    }

    public void setMatrixValues(float[] matrixValues) {
        if (matrixValues != null && matrixValues.length == MATRIX_POINT_AMOUNT) {
            matrix.setValues(matrixValues);
        }
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isCroppedBitmapChanged() {
        return isCroppedBitmapChanged;
    }

    public void setEraserSettings() {
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(UIUtils.getDpFromPx(getContext(), 1));
    }

    public static Bitmap getScaledBitmap(Bitmap source, double resultWidth, double resultHeight) {
        double coefficient;
        double sourceWidth = source.getWidth();
        double sourceHeight = source.getHeight();

        if (sourceHeight - resultHeight > sourceWidth - resultWidth) {
            coefficient = resultWidth / sourceWidth;
        } else {
            coefficient = resultHeight / sourceHeight;
        }

        sourceWidth = (coefficient * sourceWidth);
        sourceHeight = (coefficient * sourceHeight);

        return Bitmap.createScaledBitmap(source, (int) sourceWidth, (int) sourceHeight, false);
    }

    public void clearMatrix() {
        matrix = new Matrix();
    }

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    public void setOnImageChangeListener(OnImageChangeListener onImageChangeListener) {
        this.onImageChangeListener = onImageChangeListener;
    }

    public static interface OnImageChangeListener {
        public void onImageChange();
    }
}
