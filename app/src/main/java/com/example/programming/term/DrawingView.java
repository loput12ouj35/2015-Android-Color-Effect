package com.example.programming.term;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.Ragnarok.BitmapFilter;


public class DrawingView extends View {
    private Paint mPaint;       //Paint for masking lines
    private Paint Layer1Paint;      //Paint for Layer1
    private Paint Layer2Paint;       //Paint for Layer2
    private Paint maskPaint;        //Paint for masking

    private Bitmap mLayer1;     //Bitmap for background (Layer 1)
    private Bitmap mMaskLine;   //Bitmap for masking
    private Bitmap mMaskSRC;   //Source of layer2
    private Bitmap mLayer2;     //Result after masking (Layer 2)

    private Canvas maskinglineCanvas;      //Canvas for masking
    private Canvas resultCanvas;        //Result after masking

    private Path mPath;     //Path for drawing masking line
    private ArrayList<Pathset> mPathSet;       //Path set
    private ArrayList<Pathset> mPathSetRedo;        //Path set for redo

    private String filepath;        //path of source image

    private int currentBrushsize;       //current size of brush
    private int rotationAngle;

    private float scaleFactor = 1.f;    //current scale factor

    private boolean usingEraser = false;        //Note that toggling maskInvert affects eraser;
    private boolean maskInvert = false;         //Invert mode?
    private boolean undo = false;           //can undo?
    private boolean redo = false;       //can redo?
    private boolean grayscale1 = false;  //Layer1 is grayscale?
    private boolean grayscale2 = false;  //Layer2 is grayscale?
    private boolean initalized = false;
    private boolean movingMode = false; //When it is on, not drawing and moving this view

    private LayerRGB layer1RGB;
    private LayerRGB layer2RGB;

    public DrawingView(Context c) {
        this(c, null, 0);
    }

    public DrawingView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DrawingView(Context c, AttributeSet attrs, int defstyle) {
        super(c, attrs, defstyle);
        currentBrushsize = 18;
        Layer1Paint = new Paint(Paint.DITHER_FLAG);
        Layer2Paint = new Paint(Paint.DITHER_FLAG);
        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(currentBrushsize);

        mPath = new Path();
        mPathSet = new ArrayList<>();
        mPathSetRedo = new ArrayList<>();

        layer1RGB = new LayerRGB();
        layer2RGB = new LayerRGB();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(!initalized) {
            initalized = true;
            loadImage(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mLayer1, 0, 0, Layer1Paint);
        resultCanvas.drawBitmap(mMaskSRC, 0, 0, null);
        resultCanvas.drawBitmap(mMaskLine, 0, 0, maskPaint);
        canvas.drawBitmap(mLayer2, 0, 0, Layer2Paint);
    }
    private float mX, mY;

    private void drawing_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
    }
    private void drawing_move(float x, float y) {
        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        maskinglineCanvas.drawPath(mPath, mPaint);
    }
    private void drawing_up() {
        mPath.lineTo(mX, mY);
        mPathSet.add(new Pathset(new Path(mPath), usingEraser, currentBrushsize));
        maskinglineCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        if(movingMode) {
            return super.onTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mX = x;
                    mY = y;

                    clearRedoPathset();
                    drawing_start(x, y);

                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawing_move(x, y);
                    invalidate();

                    mX = x;
                    mY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    drawing_up();
                    invalidate();
                    break;
            }
        }

        return true;
    }

    public void moveDV(float dx, float dy) {
        this.setX(this.getX() + dx);
        this.setY(this.getY() + dy);
    }

    public void setBrushsize(int size) {
        if(size < 0)
            size = 1;
        mPaint.setStrokeWidth(size);
        currentBrushsize = size;
    }

    public int getCurrentBrushsize() {
        return currentBrushsize;
    }

    public boolean toggleErase() {
        usingEraser = !usingEraser;
        mPaint.setXfermode(new PorterDuffXfermode
                (usingEraser ? PorterDuff.Mode.CLEAR : PorterDuff.Mode.OVERLAY));
        return usingEraser;
    }

    public boolean toggleMovingMode() {
        movingMode = !movingMode;

        return movingMode;
    }

    public void clearBrush() {
        mMaskLine.eraseColor(Color.TRANSPARENT);
        clearPathset();
        invalidate();
    }

    public void loadImage(Bitmap bitmap) {
        scaleFactor = 1f;
        clearPathset();
        this.setX(0);
        this.setY(0);
        this.setScaleX(1f);
        this.setScaleY(1f);
        rotationAngle = 0;
        this.setRotation(0);
        Layer1Paint = new Paint(Paint.DITHER_FLAG);
        layer1RGB = new LayerRGB();
        layer2RGB = new LayerRGB();

        resize(bitmap.getWidth(), bitmap.getHeight());

        mMaskSRC = bitmap.copy(bitmap.getConfig(), false);
        mLayer1 = bitmap.copy(bitmap.getConfig(), false);
        mMaskLine = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        maskinglineCanvas = new Canvas(mMaskLine);
        mLayer2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        resultCanvas = new Canvas(mLayer2);
        invalidate();
        System.gc();
    }

    public Bitmap getResultforSave() {
        Bitmap mSave = Bitmap.createBitmap(mLayer1.getWidth(), mLayer1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tmpCanvas = new Canvas(mSave);

        tmpCanvas.drawBitmap(mLayer1, 0, 0, Layer1Paint);
        tmpCanvas.drawBitmap(mLayer2, 0, 0, Layer2Paint);

        if(rotationAngle == 0)
            return mSave;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        return Bitmap.createBitmap(mSave, 0, 0, mSave.getWidth(),
                mSave.getHeight(), matrix, true);
    }

    public void changeHSV(int H, int S, int V, boolean Layer1) {
        ColorMatrix cm = ColorFilterGenerator.adjustColor(H, S, V);
        cm.postConcat(makelayerColorMatrix(Layer1? layer1RGB : layer2RGB, Layer1? grayscale1 : grayscale2));
        ColorFilter hsvFilter = new ColorMatrixColorFilter(cm);
        if(Layer1)
            Layer1Paint.setColorFilter(hsvFilter);
        else
            Layer2Paint.setColorFilter(hsvFilter);
        invalidate();
    }

    public void saveHSVchange (int H, int S, int V, boolean Layer1) {
        Bitmap tmp = Bitmap.createBitmap(mLayer1.getWidth(), mLayer1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tmp);
        ColorMatrix cm = ColorFilterGenerator.adjustColor(H, S, V);
        ColorFilter hsvFilter = new ColorMatrixColorFilter(cm);

        if(Layer1) {
            Layer1Paint.setColorFilter(hsvFilter);
            c.drawBitmap(mLayer1, 0, 0, Layer1Paint);
            mLayer1 = tmp;
            Layer1Paint = new Paint(Paint.DITHER_FLAG);
            Layer1Paint.setColorFilter(new ColorMatrixColorFilter(makelayerColorMatrix(layer1RGB, grayscale1)));
        } else {
            Layer2Paint.setColorFilter(hsvFilter);
            c.drawBitmap(mMaskSRC, 0, 0, Layer2Paint);
            mMaskSRC = tmp;
            Layer2Paint = new Paint(Paint.DITHER_FLAG);
            Layer2Paint.setColorFilter(new ColorMatrixColorFilter(makelayerColorMatrix(layer2RGB, grayscale2)));
        }
    }

    public void invert() {
        maskInvert = !maskInvert;
        maskPaint.setXfermode(new PorterDuffXfermode
                (maskInvert? PorterDuff.Mode.DST_OUT : PorterDuff.Mode.DST_IN));
        invalidate();
    }

    public boolean isMaskInvert() {
        return maskInvert;
    }

    public void resetLayer(boolean isLayer1) {
        if(isLayer1)
            mLayer1 = BitmapFactory.decodeFile(filepath);
        else
            mMaskSRC = BitmapFactory.decodeFile(filepath);
        invalidate();
    }

    public void undoPath() {    //Delete all of the lines and draw them again except the last one.
        int size = mPathSet.size();

        if(size != 0) {
            mPathSetRedo.add(mPathSet.get(size - 1));
            mPathSet.remove(size - 1);
            undo = !(size - 1 == 0);    //after pop, if size is 0, undo cannot be done;
            drawAgain();
        }
        redo = true;        //after undo, redo can be done;
    }

    public void redoPath() {
        int size = mPathSetRedo.size();

        if(size != 0){
            mPathSet.add(mPathSetRedo.get(size - 1));
            mPathSetRedo.remove(size - 1);
            redo = !(size - 1 == 0);    //after pop, if size is 0, redo cannot be done;
            drawAgain();
        }
        undo = true;        //after redo, undo can be done;
    }

    public void drawAgain() {
        mMaskLine.eraseColor(Color.TRANSPARENT);

        for(int i=0; i < mPathSet.size(); ++i){
            mPaint.setXfermode(new PorterDuffXfermode
                    (mPathSet.get(i).isEraser? PorterDuff.Mode.CLEAR : PorterDuff.Mode.OVERLAY));
            mPaint.setStrokeWidth(mPathSet.get(i).size);
            maskinglineCanvas.drawPath(mPathSet.get(i).path, mPaint);
        }
        invalidate();
        mPaint.setXfermode(new PorterDuffXfermode
                (usingEraser ? PorterDuff.Mode.CLEAR : PorterDuff.Mode.OVERLAY));
        mPaint.setStrokeWidth(currentBrushsize);

    }

    public void clearRedoPathset() {
        undo = true;
        redo = false;
        mPathSetRedo.clear();
    }

    public void clearPathset() {
        mPathSet.clear();
        mPathSetRedo.clear();

        undo = false;
        redo = false;
    }

    public boolean isUndo() {
        return undo;
    }

    public boolean isRedo() {
        return  redo;
    }

    public void layerColorFilter(LayerRGB input, boolean grayscale, boolean Layer1) {
        ColorFilter rgbFilter = new ColorMatrixColorFilter(makelayerColorMatrix(input, grayscale));

        if(Layer1)
            Layer1Paint.setColorFilter(rgbFilter);
        else
            Layer2Paint.setColorFilter(rgbFilter);
        invalidate();
    }

    public ColorMatrix makelayerColorMatrix(LayerRGB input, boolean grayscale) {
        ColorMatrix cm = new ColorMatrix();
        ColorMatrix cm2 = new ColorMatrix();

        if(grayscale)
            cm.setSaturation(0);

        cm2.setScale(input.R, input.G, input.B, 1);
        cm.postConcat(cm2);

        return cm;
    }

    public LayerRGB getLayerRGB(boolean isLayer1) {
        return isLayer1? layer1RGB : layer2RGB;
    }

    public void setLayerRGB(LayerRGB input, boolean isLayer1) {
        if(isLayer1)
            layer1RGB = input;
        else
            layer2RGB = input;
    }

    public boolean isGrayscale(boolean Layer1) {
        return  Layer1? grayscale1 : grayscale2;
    }

    public void setGrayscale(boolean input, boolean Layer1) {
        if(Layer1)
            grayscale1 = input;
        else
            grayscale2 = input;
    }

    public void resetScale() {
        scaleFactor = 1f;
        rotationAngle = 0;
        this.setScaleX(1f);
        this.setScaleY(1f);
        this.setX(0);
        this.setY(0);
        this.setRotation(0);
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float input) {
        scaleFactor = input;
        this.setScaleX(scaleFactor);
        this.setScaleY(scaleFactor);
    }

    public void screenScaleFactor(int screenX, int screenY) {
        int viewW = getWidth();
        int viewH = getHeight();
        float dstX = -viewW / 2;
        float dstY = -viewH / 2;

        if(rotationAngle == 90 || rotationAngle == 270) {
            viewW = getHeight();
            viewH = getWidth();
        }

        setScaleFactor(Math.min((float) screenX / viewW, (float) screenY / viewH));

        dstX += screenX / 2;
        dstY += screenY / 2;
        this.setX(dstX);
        this.setY(dstY);
    }

    public void rotateDV() {
        rotationAngle += 90;
        this.setRotation(rotationAngle);
        if(rotationAngle >= 360)
            rotationAngle -= 360;
    }

    public void resize(int w, int h) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.getLayoutParams();
        params.width = w;
        params.height = h;
        this.setLayoutParams(params);
    }

    public void applyFilter(boolean Layer1, int style) {
        if(Layer1)
            mLayer1 = BitmapFilter.changeStyle(mLayer1, style);
        else
            mMaskSRC = BitmapFilter.changeStyle(mMaskSRC, style);
        invalidate();
    }

    public void setFilepath(String input) {
        filepath = input;
    }

}
