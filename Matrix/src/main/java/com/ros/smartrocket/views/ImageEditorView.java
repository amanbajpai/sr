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
    // private static final String TAG = ImageEditorView.class.getSimpleName();
    private int layerId = 0;
    //private BitmapProcessingManager bitmapProcessingManager = BitmapProcessingManager.getInstance();
    private Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Bitmap cropedBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    private Paint bitmapPaint = new Paint();
    private boolean isSelectedLayer = true;
    private boolean isCropedBitmapChanged = true;

    private Paint eraserPaint = new Paint();

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    float[] matrixValues = new float[9];
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private PointF midPoint = new PointF();

    private float oldDist = 1.0f;
    private final int NONE = 0;
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private int mode = NONE;
    private float maxZoom = 3.0f;
    private float minZoom = 0.2f;
    private float angle, fangle;
    float[] lastEvent = null;
    private int viewWidth, viewHeight;
    private boolean isEmpty = true;
    private boolean canRotate = true;

    private OnImageChangeListener onImageChangeListener;

    public ImageEditorView(Context context, int layerId) {
        super(context);
        this.setLayerId(layerId);

        setEraserSettings();
    }

    public ImageEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.isEmpty = false;
        this.isCropedBitmapChanged = true;

        // bitmapPaint.setAlpha(50);
        // selectedLayerPaint.setAlpha(100);
        invalidate();
        // requestLayout();
    }

    public void setOldBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.isEmpty = false;
        this.isCropedBitmapChanged = false;

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

                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    matrix.getValues(matrixValues);
                    midPoint(midPoint, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
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

                isCropedBitmapChanged = true;
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

                    if (newDist > 10f) {

                        matrix.set(savedMatrix);
                        matrix.getValues(matrixValues);
                        // float currentScale = matrixValues[Matrix.MSCALE_X];

                        float scale = newDist / oldDist;

                        // TODO limit zoom
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        matrix.getValues(matrixValues);

                    }

                    if (lastEvent != null) {
                        float newRot = rotation(event);
                        this.angle = (newRot - fangle);
                        matrix.postRotate(this.angle, midPoint.x, midPoint.y);
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
    private void midPoint(PointF point, MotionEvent event) {
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
        return Bitmap.createScaledBitmap(getCropedBitmap(), width, height, false);
    }

    public Bitmap getCropedBitmap() {
        if (isCropedBitmapChanged) {
            Bitmap croppedImage = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(croppedImage);
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.drawCircle(0, 0, 1, eraserPaint);

            cropedBitmap = croppedImage;
            isCropedBitmapChanged = false;
        }

        return cropedBitmap;
    }

    public float[] getMatrixValues() {
        matrix.getValues(matrixValues);
        return matrixValues;
    }

    public void setMatrixValues(float[] matrixValues) {
        if (matrixValues != null && matrixValues.length == 9) {
            matrix.setValues(matrixValues);
        }
    }

    public void selectedLayer(boolean isSelected) {
        isSelectedLayer = isSelected;
        invalidate();
    }

    public void clearLayer() {
        this.bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        this.isEmpty = true;
        this.isCropedBitmapChanged = true;
        invalidate();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isCropedBitmapChanged() {
        return isCropedBitmapChanged;
    }

    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int layerId) {
        this.layerId = layerId;
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
