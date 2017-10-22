package com.example.android.photobyintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PhotoIntentActivity extends Activity {
    private TessBaseAPI mTess;
    String datapath = "";


    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;
    private Uri mVideoUri;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    //create listView

    ListView list;
   // LazyAdapter adapter;


    String[] web = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress",
            "Drupal"
    } ;
    Integer[] imageId = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7


    };

    // Temp save listItem position
    int position;

    int imageCount;
    String imageTempName;
    String[] imageFor;


    /* Photo album for this application */
    private String getAlbumName() {
        Log.d("SAIMBHI", "getAlbumName method executed");
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
        Log.d("SAIMBHI", "getAlbumDirectory method executed");
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
//			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        Log.d("SAIMBHI", "createImageFile method executed");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {
        Log.d("SAIMBHI", "setUpPhotoFile method executed");

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        Log.d("CREATION", " \"*********************** in handlebigcamera() ***********************\"" + f);
        return f;
    }

    private void setPic() {
        Log.d("SAIMBHI", "setPic method executed");

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
//		int targetW = mImageView.getWidth();
//		int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
//		if ((targetW > 0) || (targetH > 0)) {
//			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//		}

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

//		Matrix matrix = new Matrix();
//		matrix.postRotate(90);
//		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,photoW,photoH,true);
//		Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth() , scaledBitmap.getHeight(), matrix, true);

        String OCRresult = null;
        mTess.setImage(bitmap);
        OCRresult = mTess.getUTF8Text();
//		TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
//		OCRTextView.setText(OCRresult);

        Log.d("SAIMBHI", "The OCR Result is:" + OCRresult);


        Log.d("CREATION", " \"*********************** CREATING BITMAP() ***********************\"" + bitmap);

		/* Associate the Bitmap to the ImageView */
//		mImageView.setImageBitmap(bitmap);
//		mVideoUri = null;
//		mImageView.setVisibility(View.VISIBLE);
//		mVideoView.setVisibility(View.INVISIBLE);
    }

    private void galleryAddPic() {
        Log.d("SAIMBHI", "galleryAddPic method executed");
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Log.d("CREATION", " \"*********************** in galleryaddpic() ***********************\"" + f.toString());

    }

    private void dispatchTakePictureIntent(int actionCode) {
        Log.d("SAIMBHI", "dispatchTakePictureIntent method executed");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

//	private void dispatchTakeVideoIntent() {
//		Log.d("SAIMBHI", "dispatchTakeVideoContent method executed");
//		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
//	}

//	private void handleSmallCameraPhoto(Intent intent) {
//		Log.d("SAIMBHI", "handleSmallCamerapPhoto method executed");
//		Bundle extras = intent.getExtras();
//		mImageBitmap = (Bitmap) extras.get("data");
//		mImageView.setImageBitmap(mImageBitmap);
//		mVideoUri = null;
//		mImageView.setVisibility(View.VISIBLE);
//		mVideoView.setVisibility(View.INVISIBLE);
//	}

    private void handleBigCameraPhoto() {
        Log.d("SAIMBHI", "HANDLEBIGCAMERA method executed");

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

//	private void handleCameraVideo(Intent intent) {
//		Log.d("SAIMBHI", "handleCameraVideo method executed");
//		mVideoUri = intent.getData();
//		mVideoView.setVideoURI(mVideoUri);
//		mImageBitmap = null;
//		mVideoView.setVisibility(View.VISIBLE);
//		mImageView.setVisibility(View.INVISIBLE);
//	}

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };

//	Button.OnClickListener mTakePicSOnClickListener =
//		new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
//		}
//	};
//
//	Button.OnClickListener mTakeVidOnClickListener =
//		new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			dispatchTakeVideoIntent();
//		}
//	};

    /**     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("SAIMBHI", "onCreate method executed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // retrireving files from the storage starts

        File file = null;
        File[] listFile = null;
        String[] FilePathStrings = null;
        String[] FileNameStrings = null;

        file = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }
        }

        // Locate the GridView in gridview_main.xml
//        grid = (GridView) findViewById(R.id.list);
//        // Pass String arrays to LazyAdapter Class
//        adapter = new LazyAdapter(this, FilePathStrings, FileNameStrings);
//        // Set the LazyAdapter to the GridView
//        grid.setAdapter(adapter);

        //retrireving files from the storage ends

        // to create ListView
        CustomList adapter = new
                CustomList(PhotoIntentActivity.this, web, imageId);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(PhotoIntentActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

            }});

//		mImageView = (ImageView) findViewById(R.id.imageView1);
//
//		mVideoView = (VideoView) findViewById(R.id.videoView1);
        mImageBitmap = null;
        mVideoUri = null;

        Button picBtn = (Button) findViewById(R.id.btnIntend);
        Log.d("SAIMBHI", "Value of mTakePicOnClickListener:" + mTakePicOnClickListener.toString());
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

//		Button picSBtn = (Button) findViewById(R.id.btnIntendS);
//		setBtnListenerOrDisable(
//				picSBtn,
//				mTakePicSOnClickListener,
//				MediaStore.ACTION_IMAGE_CAPTURE
//		);

//		Button vidBtn = (Button) findViewById(R.id.btnIntendV);
//		setBtnListenerOrDisable(
//				vidBtn,
//				mTakeVidOnClickListener,
//				MediaStore.ACTION_VIDEO_CAPTURE
//		);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir() + "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));
        Log.d("SAIMBHI", "The value of mTess is:" + mTess);
        Log.d("SAIMBHI", "The value of datapath is:" + datapath);


        mTess.init(datapath, language);
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

//	public void processImage(View view){
//		String OCRresult = null;
//		mTess.setImage(image);
//		OCRresult = mTess.getUTF8Text();
//		TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
//		OCRTextView.setText(OCRresult);
//	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SAIMBHI", "onActivityResult method executed");
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

//		case ACTION_TAKE_PHOTO_S: {
//			if (resultCode == RESULT_OK) {
//				handleSmallCameraPhoto(data);
//			}
//			break;
//		} // ACTION_TAKE_PHOTO_S
//
//		case ACTION_TAKE_VIDEO: {
//			if (resultCode == RESULT_OK) {
//				handleCameraVideo(data);
//			}
//			break;
//		} // ACTION_TAKE_VIDEO
        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("SAIMBHI", "onSAVEInstanceState method executed");
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("SAIMBHI", "onRestoreInstanceResult method executed");
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setVisibility(
                savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        Log.d("SAIMBHI", "isIntentAvailable method executed");
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        Log.d("SAIMBHI", "setButtonListenerorDisable method executed");
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

}