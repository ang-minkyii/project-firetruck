package saco.ProjectFireTruckV2.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.TCP.SendMessage;
import saco.ProjectFireTruckV2.etc_utilities.PermissionCheck;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;

public class PhotoActivity extends AppCompatActivity {

    //Definitions for activity requests
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int RESULT_LOAD_IMG = 200;

    //Debug tag variable
    public static final String TAG = "Camera";

    //Variables for buttons and imageviews
    private ImageView background;
    private Drawable firetruck;
    private ImageButton camera;
    private ImageButton gallery;
    private ImageButton clear;
    private static ImageButton send;
    private Camera phoneCamera; // camera object
    private TextView texty;
    private static String fileName;

    //Variable for file path
    private Uri fileUri=null;

    //Variable for bitmap image
    private Bitmap rotatedAndScaledBm;

    //Variable for toast message
    public Toast mToast;

    //Variable for socket and output stream
    public Socket socket;
    public OutputStream out;

    //Variables to for permission checking

    private boolean safeToTakePicture = false;

    public static boolean photoActivityState = false;
    public static Activity photoActivity;
    private SoundPool soundPool;


    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("Pictures");
        initialise();
        clicksListener();
        photoActivity = this;
    }


    @Override
    protected void onStop() {
        super.onStop();
        photoActivityState = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        photoActivityState = true;
    }

    /**
     * Function to initialise all variables needed
     */
    public void initialise(){
        mToast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        send = (ImageButton)findViewById(R.id.send_image_button);
        camera = (ImageButton)findViewById(R.id.imageButton);
        gallery = (ImageButton)findViewById(R.id.imageButton2);
        clear = (ImageButton)findViewById(R.id.imageButton3);
        clear.setVisibility(View.INVISIBLE);
        background = (ImageView)findViewById(R.id.imageView);
        firetruck = getResources().getDrawable(R.drawable.firetruck2);
        background.setImageDrawable(firetruck);
        background.setAlpha(20);
        changeSendButtonState("enabledFalse");
        textInitialise();
    }

    public void textInitialise(){
        texty = (TextView)findViewById(R.id.textView6);
        texty.setTextSize(20);
        texty.setGravity(Gravity.CENTER);
        texty.setText("Hold the camera button" + "\n" + "to Snap and Send");
        texty.setVisibility(View.VISIBLE);
    }

    /**
     * Function to listen for all button clicks
     */
    public void clicksListener(){

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking for permission before going into the default gallery_default, otherwise we request
                PermissionCheck.permissionCheckStorage(getApplicationContext(), photoActivity);
                //If permission is granted, we proceed with image capturing, otherwise exit without returning null
                if (PermissionCheck.getStoragePermisson()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE,"MyCameraApp");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        camera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeSendButtonState("enabledFalse");
                camera.setEnabled(false);
                gallery.setEnabled(false);
                camera.setImageResource(R.drawable.camera_clicked);
                soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
                //Checking for permission before going into the default gallery_default, otherwise we request
                PermissionCheck.permissionCheckCamera(getApplicationContext(), photoActivity);
                PermissionCheck.permissionCheckStorage(getApplicationContext(), photoActivity);

                //If permission is granted, we proceed with image capturing, otherwise exit without returning null
                if (PermissionCheck.getStoragePermisson() && PermissionCheck.getCameraPermisson()) {
                    takePicture();
                }
                return false;
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSendButtonState("enabledFalse");
                //Check if socket is present before sending to avoid crash
                if (TCPSocket.getSocket() != null) {
                    socket = TCPSocket.getSocket();
                    try {
                        ReadWrite.storeData(getApplicationContext(), fileName, "Image", "config.txt");
                        out = socket.getOutputStream();
                        //Creating output stream in bytes
                        preparePhotoToSend();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mToast.setText("Not Connected");
                    mToast.show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setImageDrawable(firetruck);
                background.setAlpha(20);
                texty.setVisibility(View.VISIBLE);
                clear.setVisibility(View.INVISIBLE);
                changeSendButtonState("enabledFalse");
            }
        });
    }

    public void preparePhotoToSend(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //Compress the image to JPEG type to be sent to baos
        rotatedAndScaledBm.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        //Converting the image into a byte array
        byte[] bitmap_byte = baos.toByteArray();
        //Send the byte array
        SendMessage.SendAsRequest(bitmap_byte, fileName+".jpg");
    }


    Camera.PictureCallback jpegCallBack=new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // set file destination and file name
            texty.setVisibility(View.INVISIBLE);
            changeCameraState("Restore");
            File imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE,"AutoSnapApp");
            if(imageFile == null){
                safeToTakePicture = true;
                return;
            }else {
                fileUri = Uri.fromFile(imageFile);
            }
            try {
                //Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap userImage = BitmapFactory.decodeByteArray(data,0,data.length,options);
                // set file out stream
                FileOutputStream out = new FileOutputStream(imageFile);
                // set compress format quality and stream
                userImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                userImage.recycle();
                phoneCamera.release();
                rotatedAndScaledBm = rotateAndScalePhoto(fileUri,true);
                //Set to image view
                adjustBackground(background, 1000, rotatedAndScaledBm);
                clear.setVisibility(View.VISIBLE);
                send.performClick();

                changeCameraState("Enable");
                gallery.setEnabled(true);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            safeToTakePicture = true;
        }
    };

    public void takePicture(){
        int shutterSound = soundPool.load(getApplicationContext(), R.raw.camera_click, 0);
        phoneCamera = Camera.open();
        SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView);
        try {
            phoneCamera.setPreviewDisplay(view.getHolder()); // feed dummy surface to surface
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        phoneCamera.startPreview();
        safeToTakePicture = true;
        Camera.Parameters params = phoneCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        phoneCamera.setParameters(params);
        if(safeToTakePicture) {
            phoneCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    phoneCamera.takePicture(null, null, jpegCallBack);
                }
            });
            safeToTakePicture = false;
        }
        soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);
    }


    /**
     * Function to convert the file path from type file to type Uri
     * @param type  Variable to determine if image or video(coming soon) is requested
     * @return Uri of the image to be saved
     */
    public static Uri getOutputMediaFileUri(int type,String folder){
        return Uri.fromFile(getOutputMediaFile(type,folder));
    }

    /**
     * Function to get a file Uri for image to be taken
     * @param type ariable to determine if image or video(coming soon) is requested
     * @return File path of image to be saved
     */
    public static File getOutputMediaFile(int type, String folder){
        //Getting directory from external storage
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),folder);

        if(!mediaStorageDir.exists()){
            debug("External Storage not available");
            if(!mediaStorageDir.mkdirs()){
                debug("Failed to create directory");
                return null;
            }
        }

        //Creating format of file path
        //yyyyMMdd_HHmmss stands for year, month, day, hour, minute, seconds
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        debug("Creating file path");

        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
            fileName = "IMG_" + String.valueOf(timeStamp);
        }else{
            return null;
        }
        return mediaFile;
    }

    /**
     * Function to ease debugging
     * @param message Message to be thrown in terminal/debug window
     */
    public static void debug(String message){
        Log.d(TAG, message);
    }

    /**
     * Function to listen for information retrieved from returning intents (camera,gallery_default)
     * @param requestCode Constant retrieved frm returning activity, which was sent when starting activity
     * @param resultCode    Result retrieved from returning activity, can be either yes or no (save or discard)
     * @param data  Data retrieved from returning activity, content depends on the returning activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If return from camera
        if((requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            //and user request to save
            try {
                //rotate and scale photo
                texty.setVisibility(View.INVISIBLE);
                rotatedAndScaledBm = rotateAndScalePhoto(fileUri, false);
                //Set to image view
                adjustBackground(background,1000,rotatedAndScaledBm);
                clear.setVisibility(View.VISIBLE);
                //Activate send_default button
                changeSendButtonState("enabledTrue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //If return from gallery_default
        if((requestCode == RESULT_LOAD_IMG) && (resultCode == RESULT_OK)){
            //user confirms selected image
            //get file uri from data
            texty.setVisibility(View.INVISIBLE);
            fileUri = data.getData();
            fileName = fileUri.getPath();
            String[] newFilename = fileName.split("/");
            fileName = newFilename[newFilename.length-1];
            fileName = "IMG_"+fileName;

            try {
                //rotate and scale photo
                rotatedAndScaledBm = rotateAndScalePhoto(fileUri, true);
                //Set to image view
                adjustBackground(background,1000,rotatedAndScaledBm);
                clear.setVisibility(View.VISIBLE);
                //Activate send_default button
                changeSendButtonState("enabledTrue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function to rotate and scale photo (only applies to camera activity, gallery_default activity might be different
     * @param fileUri   file path of image
     * @return  Image in bitmap form
     * @throws IOException
     */
    public Bitmap rotateAndScalePhoto(Uri fileUri, boolean rotateFromGallery) throws IOException {
        int rotate = 0;

        //Method to determine the orientation of the photo (landscape or portrait
        getContentResolver().notifyChange(fileUri, null);
        File imageFile = new File(fileUri.getPath());
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                break;
        }

        if (rotateFromGallery == true){rotate += 90;}

        //Rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

        //Scale image
        if (bm.getWidth()<bm.getHeight()) {
            bm = bm.createScaledBitmap(bm, 240, 320, true);}
        else {bm = bm.createScaledBitmap(bm, 320, 240, true);}

        System.gc();
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        return bm;
    }

    public void adjustBackground(ImageView imageView, int transparency, Bitmap resource){
        imageView.setAlpha(transparency);
        imageView.setImageBitmap(resource);
    }

    /**
     * Function to check and request for permission
     */

    /**
     * Function to wait for result returning from permission checking activity
     * @param requestCode Code to represent permission checking
     * @param permissions Requested permissons, Never null
     * @param grantResults results of permission (granted or not granted)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissions.equals(new String[]{Manifest.permission.CAMERA})){
                phoneCamera = Camera.open();
                SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView);
                try {
                    phoneCamera.setPreviewDisplay(view.getHolder()); // feed dummy surface to surface
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                phoneCamera.startPreview();
                phoneCamera.takePicture(null, null, null, jpegCallBack);
            }
            else {
                //Send intent again for stability purpose
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE,"MyCameraApp");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        } else {
            // Permission Denied
            mToast.setText("Permission denied");
            mToast.show();
        }
    }


    public static void changeSendButtonState (String state){
        switch (state){
            case "enabledTrue":
                send.setEnabled(true);
                break;
            case "enabledFalse":
                send.setEnabled(false);
                break;
            case "pressed":
                send.setPressed(true);
                break;
            case "released":
                send.setPressed(false);
                break;
            default:
                send.setEnabled(true);
                break;
        }
    }

    public void changeCameraState(String state){
        switch (state){
            case "Enable":
                camera.setEnabled(true);
                break;
            case "Clicked":
                camera.setImageResource(R.drawable.camera_clicked);
                break;
            case "Restore":
                camera.setImageResource(R.drawable.camera_default);
                break;
        }
    }
}
