package com.example.programming.term;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import cn.Ragnarok.BitmapFilter;


public class MainActivity extends Activity {
    private static final int RESULT_LOAD_IMG = 1;
    private static final int CAMERA_REQUEST = 1888;

    private boolean movingMode = false;
    private ScaleGestureDetector scaleDetector;

    String imgDecodableString;
    DrawingView dv;
    GradientGenerator gg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dv = (DrawingView) findViewById(R.id.drawingView);
        gg = new GradientGenerator();
        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
    }
    @Override
    public void onBackPressed() {
    }

    public void onClickbrush(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();

        popup.getMenuInflater().inflate(R.menu.menu_brush, menu);
        menu.findItem(R.id.invert).setChecked(dv.isMaskInvert());
        menu.findItem(R.id.undo).setEnabled(dv.isUndo());
        menu.findItem(R.id.redo).setEnabled(dv.isRedo());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.brushSize:
                        showBrushsizeDialog();
                        return true;
                    case R.id.clear:
                        clearDialog();
                        return true;
                    case R.id.invert:
                        dv.invert();
                        toggleEraser();
                        return true;
                    case R.id.undo:
                        dv.undoPath();
                        return true;
                    case R.id.redo:
                        dv.redoPath();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }
    public void onClickLayer(View v) {
        final boolean Layer1 = v.getId() == R.id.layer1;
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_layer2, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.cf:
                        colorFilterDialog(Layer1);
                        return true;
                    case R.id.hsv:
                        changeHSVDialog(Layer1);
                        return true;
                    case R.id.reset:
                        resetLayerDialog(Layer1);
                        return true;
                    case R.id.relief:
                        dv.applyFilter(Layer1, BitmapFilter.RELIEF_STYLE);
                        return true;
                    case R.id.blur:
                        dv.applyFilter(Layer1, BitmapFilter.AVERAGE_BLUR_STYLE);
                        return true;
                    case R.id.oil:
                        dv.applyFilter(Layer1, BitmapFilter.OIL_STYLE);
                        return true;
                    case R.id.neon:
                        dv.applyFilter(Layer1, BitmapFilter.NEON_STYLE);
                        return true;
                    case R.id.pixelate:
                        dv.applyFilter(Layer1, BitmapFilter.PIXELATE_STYLE);
                        return true;
                    case R.id.oldtv:
                        dv.applyFilter(Layer1, BitmapFilter.TV_STYLE);
                        return true;
                    case R.id.invertcolor:
                        dv.applyFilter(Layer1, BitmapFilter.INVERT_STYLE);
                        return true;
                    case R.id.block:
                        dv.applyFilter(Layer1, BitmapFilter.BLOCK_STYLE);
                        return true;
                    case R.id.oldphoto:
                        dv.applyFilter(Layer1, BitmapFilter.OLD_STYLE);
                        return true;
                    case R.id.sharpen:
                        dv.applyFilter(Layer1, BitmapFilter.SHARPEN_STYLE);
                        return true;
                    case R.id.light:
                        dv.applyFilter(Layer1, BitmapFilter.LIGHT_STYLE);
                        return true;
                    case R.id.lomo:
                        dv.applyFilter(Layer1, BitmapFilter.LOMO_STYLE);
                        return true;
                    case R.id.HDR:
                        dv.applyFilter(Layer1, BitmapFilter.HDR_STYLE);
                        return true;
                    case R.id.gaussianblur:
                        dv.applyFilter(Layer1, BitmapFilter.GAUSSIAN_BLUR_STYLE);
                        return true;
                    case R.id.softglow:
                        dv.applyFilter(Layer1, BitmapFilter.SOFT_GLOW_STYLE);
                        return true;
                    case R.id.sketch:
                        dv.applyFilter(Layer1, BitmapFilter.SKETCH_STYLE);
                        return true;
                    case R.id.motionblur:
                        dv.applyFilter(Layer1, BitmapFilter.MOTION_BLUR_STYLE);
                        return true;
                    case R.id.gotham:
                        dv.applyFilter(Layer1, BitmapFilter.GOTHAM_STYLE);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void onClickmenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_main, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.camera:
                        takePicture();
                        return true;
                    case R.id.load:
                        selectPicture();
                        return true;
                    case R.id.save:
                        saveDialog();
                        return true;
                    case R.id.exit:
                        exitDialog();
                        return true;
                    case R.id.gc:
                        System.gc();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void onClickZoom(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_zoom, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                float scaleFactor = 1f;
                switch (item.getItemId()){
                    case R.id.zoom25:
                        scaleFactor = 0.25f;
                        break;
                    case R.id.zoom33:
                        scaleFactor = 0.33f;
                        break;
                    case R.id.zoom50:
                        scaleFactor = 0.5f;
                        break;
                    case R.id.zoom66:
                        scaleFactor = 0.66f;
                        break;
                    case R.id.zoom75:
                        scaleFactor = 0.75f;
                        break;
                    case R.id.zoom100:
                        scaleFactor = 1f;
                        break;
                    case R.id.zoom125:
                        scaleFactor = 1.25f;
                        break;
                    case R.id.zoom150:
                        scaleFactor = 1.5f;
                        break;
                    case R.id.zoom200:
                        scaleFactor = 2f;
                        break;
                    case R.id.zoom300:
                        scaleFactor = 3f;
                        break;
                    case R.id.zoom400:
                        scaleFactor = 4f;
                        break;
                    case R.id.zoom500:
                        scaleFactor = 5f;
                        break;
                    default:
                        return false;
                }
                dv.setScaleFactor(scaleFactor);
                showScaleFactor(scaleFactor);
                return true;
            }
        });
        popup.show();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.erase:
                toggleEraser();
                break;
            case R.id.movingMode:
                toggleMovingMode();
                break;
            case R.id.zoomIn:
                dv.setScaleFactor(Math.min(dv.getScaleFactor() * 2f, 5f));
                showScaleFactor(dv.getScaleFactor());
                break;
            case R.id.zoomOut:
                dv.setScaleFactor(Math.max(dv.getScaleFactor() * 0.5f, 0.25f));
                showScaleFactor(dv.getScaleFactor());
                break;
            case R.id.zoomScreen:
                zoomScreenSize();
                showScaleFactor(dv.getScaleFactor());
                break;
            case R.id.scaleReset:
                dv.resetScale();
                showScaleFactor(dv.getScaleFactor());
                break;
            case R.id.rotate:
                dv.rotateDV();
                break;
        }
    }

    private float mX, mY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        if(movingMode) {
            if(ev.getPointerCount() == 1) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX = x;
                        mY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dv.moveDV(x -mX, y -mY);
                        mX = x;
                        mY = y;
                        break;
                }
            } else
                scaleDetector.onTouchEvent(ev);
        }
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {     //Scale Listener

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = dv.getScaleFactor() * detector.getScaleFactor();

            if(scaleFactor < 0.25)
                scaleFactor = 0.25f;
            else if(scaleFactor > 5)
                scaleFactor = 5;

            dv.setScaleFactor(scaleFactor);
            showScaleFactor(dv.getScaleFactor());
            return true;
        }
    }

    public void showBrushsizeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_brush,
                (ViewGroup) findViewById(R.id.layout_brush_dialog));
        final TextView brushTxt = (TextView) Viewlayout.findViewById(R.id.brush_txt);
        final int prev_size = dv.getCurrentBrushsize();

        brushTxt.setText("Size : " + dv.getCurrentBrushsize());

        builder.setTitle("Brush size").setView(Viewlayout);

        final SeekBar brushSeek = (SeekBar) Viewlayout.findViewById(R.id.brush_seek);

        brushSeek.setMax(200);
        brushSeek.setProgress(prev_size);
        brushSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {         //Minimum of brushSize is 1;
                    brushSeek.setProgress(1);
                    progress = 1;
                }

                dv.setBrushsize(progress);
                brushTxt.setText("Size : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dv.setBrushsize(prev_size);
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    public void selectPicture() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File storagePath = new File(Environment.getExternalStorageDirectory() + "/TermProject/TermProjectSavedImages/");
        storagePath.mkdirs();
        File imageFile = new File(storagePath, "image.jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile((imageFile)));
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            File file = new File((Environment.getExternalStorageDirectory() + "/TermProject/TermProjectSavedImages/image.jpg"));
            dv.loadImage(BitmapFactory.decodeFile(file.getAbsolutePath()));
            dv.setFilepath(file.getAbsolutePath());
            showScaleFactor(dv.getScaleFactor());
        }  else {
            try {
                if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                            null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    dv.loadImage(BitmapFactory.decodeFile(imgDecodableString));
                    dv.setFilepath(imgDecodableString);
                    showScaleFactor(dv.getScaleFactor());
                } else {
                    Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void toggleEraser() {
        /* Note that red color does not mean 'usingEraser' is true at all time.
         * Consider 'maskinvert'.
         */
        boolean on = dv.toggleErase() ^ dv.isMaskInvert();  //note that there is a XOR operation

        Button eraserButton = (Button) findViewById(R.id.erase);
        eraserButton.setTextColor(on? Color.RED : Color.BLACK);

        final Toast toast = Toast.makeText(this, "Eraser " + (on? "on" : "off"), Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void toggleMovingMode() {
        movingMode = dv.toggleMovingMode();
        Button eraserButton = (Button) findViewById(R.id.movingMode);
        eraserButton.setTextColor(movingMode ? Color.RED : Color.BLACK);

        final Toast toast = Toast.makeText(this, "MovingMode " + (movingMode? "on" : "off"), Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void clearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Brush Clear");
        builder.setMessage("Are you sure? (This cannot be undone)").setCancelable(true).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dv.clearBrush();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure? (Please save all before EXIT)").setCancelable(true).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    public void save(boolean png, boolean compress90, boolean compress80) {
        String imageName = System.currentTimeMillis() + (png? ".png" : ".jpg");

        File storagePath = new File(Environment.getExternalStorageDirectory() + "/TermProject/TermProjectSavedImages/");
        storagePath.mkdirs();

        FileOutputStream out;
        File imageFile = new File(storagePath, imageName);
        try {
            out = new FileOutputStream(imageFile);
            dv.getResultforSave().compress(
                    png ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                    compress90 ? 90 : (compress80? 80 : 100), out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("error", "Unable to write the image to gallery", e);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.TITLE, imageName);
        values.put(MediaStore.Images.Media.MIME_TYPE, png? "image/png" : "image/jpeg");
        values.put("_data", imageFile.getAbsolutePath());

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(this, imageName + " was saved", Toast.LENGTH_SHORT).show();
    }

    public class TmpInput {
        int H = 0;
        int S = 0;
        int V = 0;
    }

    public void changeHSVDialog(boolean isLayer1) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_hsv,
                (ViewGroup) findViewById(R.id.layout_hsv_dialog));

        final TextView hTxt = (TextView) Viewlayout.findViewById(R.id.h_txt);
        final TextView sTxt = (TextView) Viewlayout.findViewById(R.id.s_txt);
        final TextView vTxt = (TextView) Viewlayout.findViewById(R.id.v_txt);

        final SeekBar hSeek = (SeekBar) Viewlayout.findViewById(R.id.h_seek);
        final SeekBar sSeek = (SeekBar) Viewlayout.findViewById(R.id.s_seek);
        final SeekBar vSeek = (SeekBar) Viewlayout.findViewById(R.id.v_seek);

        final TmpInput tmpInput = new TmpInput();

        final boolean Layer1 = isLayer1;

        hSeek.setProgressDrawable(gg.hBar);
        sSeek.setProgressDrawable(gg.sBar);
        vSeek.setProgressDrawable(gg.vBar);

        hTxt.setText("Hue : 0");
        sTxt.setText("Saturation : 0");
        vTxt.setText("Brightness : 0");

        hSeek.setMax(360);      //-180~180
        hSeek.setProgress(180);
        sSeek.setMax(200);      //-100~100
        sSeek.setProgress(100);
        vSeek.setMax(200);      //-100~100
        vSeek.setProgress(100);

        hSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hTxt.setText("Hue : " + (progress - 180));
                tmpInput.H = (progress - 180);
                dv.changeHSV(tmpInput.H, tmpInput.S, tmpInput.V, Layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sTxt.setText("Saturation : " + (progress - 100));
                tmpInput.S = (progress - 100);
                dv.changeHSV(tmpInput.H, tmpInput.S, tmpInput.V, Layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vTxt.setText("Brightness : " + (progress - 100));
                tmpInput.V = progress - 100;
                dv.changeHSV(tmpInput.H, tmpInput.S, tmpInput.V, Layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setTitle("Layer " + (Layer1? "1" : "2") + " - HSV").setView(Viewlayout).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dv.changeHSV(tmpInput.H, tmpInput.S, tmpInput.V, Layer1);
                        dv.saveHSVchange(tmpInput.H, tmpInput.S, tmpInput.V, Layer1);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dv.changeHSV(0, 0, 0, Layer1);
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    public void resetLayerDialog(boolean isLayer1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final boolean Layer1 = isLayer1;
        builder.setTitle("Reset - Layer " + (isLayer1? "1" : "2"));
        builder.setMessage("Are you sure?").setCancelable(true).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dv.resetLayer(Layer1);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    public void colorFilterDialog(boolean isLayer1) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_custom,
                (ViewGroup) findViewById(R.id.layout_custom_dialog));

        final TextView rTxt = (TextView) Viewlayout.findViewById(R.id.r_txt);
        final TextView gTxt = (TextView) Viewlayout.findViewById(R.id.g_txt);
        final TextView bTxt = (TextView) Viewlayout.findViewById(R.id.b_txt);

        final SeekBar rSeek = (SeekBar) Viewlayout.findViewById(R.id.r_seek);
        final SeekBar gSeek = (SeekBar) Viewlayout.findViewById(R.id.g_seek);
        final SeekBar bSeek = (SeekBar) Viewlayout.findViewById(R.id.b_seek);

        final Switch gSwitch = (Switch) Viewlayout.findViewById(R.id.switch1);

        final View rec = Viewlayout.findViewById(R.id.rec);

        final boolean Layer1 = isLayer1;

        final LayerRGB tmp = dv.getLayerRGB(Layer1);

        rTxt.setText("R : " + tmp.R);
        gTxt.setText("G : " + tmp.G);
        bTxt.setText("B : " + tmp.B);

        rSeek.setProgressDrawable(gg.rBar);
        gSeek.setProgressDrawable(gg.gBar);
        bSeek.setProgressDrawable(gg.bBar);

        rSeek.setMax(100);
        gSeek.setMax(100);
        bSeek.setMax(100);
        rSeek.setProgress((int) (tmp.R * 100));
        gSeek.setProgress((int) (tmp.G * 100));
        bSeek.setProgress((int) (tmp.B * 100));

        rec.setBackgroundColor(tmp.toColor());

        gSwitch.setChecked(dv.isGrayscale(Layer1));

        gSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dv.layerColorFilter(tmp, gSwitch.isChecked(), Layer1);
            }
        });

        rSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rTxt.setText("R : " + ((float) progress / 100));
                tmp.R = (float) progress / 100;
                dv.layerColorFilter(tmp, gSwitch.isChecked(), Layer1);
                rec.setBackgroundColor(tmp.toColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        gSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gTxt.setText("G : " + ((float) progress / 100));
                tmp.G = (float) progress / 100;
                dv.layerColorFilter(tmp, gSwitch.isChecked(), Layer1);
                rec.setBackgroundColor(tmp.toColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        bSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bTxt.setText("B : " + ((float) progress / 100));
                tmp.B = (float) progress / 100;
                dv.layerColorFilter(tmp, gSwitch.isChecked(), Layer1);
                rec.setBackgroundColor(tmp.toColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        builder.setTitle("Layer " + (Layer1? "1" : "2") + " - ColorFilter").setView(Viewlayout).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dv.layerColorFilter(tmp, gSwitch.isChecked(), Layer1);
                        dv.setLayerRGB(tmp, Layer1);
                        dv.setGrayscale(gSwitch.isChecked(), Layer1);
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] array = {"PNG", "JPG (100%)", "JPG (90%)", "JPG (80%)"};

        builder.setTitle("Select Format")
                .setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                save(true, false, false);
                                break;
                            case 1:
                                save(false, false, false);
                                break;
                            case 2:
                                save(false, true, false);
                                break;
                            case 3:
                                save(false, false, true);
                                break;
                        }

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    public void zoomScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        int menuSize = findViewById(R.id.menulayout1).getHeight()
                + findViewById(R.id.menulayout2).getHeight()
                + rect.top;     //height of status bar
        dv.screenScaleFactor(size.x, size.y - menuSize);
    }

    public void showScaleFactor(float scaleFactor){
        Button txt = (Button) super.findViewById(R.id.zoomcustom);
        txt.setText((int) (scaleFactor * 100) + "%");
    }
}

