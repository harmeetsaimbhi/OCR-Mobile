package com.example.android.photobyintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//google drive dependencies
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;



public class PhotoIntentActivity extends Activity {
    private TessBaseAPI mTess;
    String datapath = "";

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
    LazyAdapter adapter;
    Activity activity;
    File file = null;
    File[] listFile = null;
    String[] FilePathStrings = null;
    String[] FileNameStrings = null;
    private static final int ACTION_TAKE_PHOTO_B = 1;

    // filter declarations
    EditText editsearch;
    ListView filterList;
    String[] rank;
    String[] country;
    ArrayList<Combination> arraylist = new ArrayList<Combination>();
    Combination combo ;
    TextView status ;

    //GoogleDrive variables
    //GoogleDrive variables
    private static final String TAG = "SAIMBHI";

    /**
     * Request code for google sign-in
     */
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CREATOR = 2;

    /**
     * Request code for the Drive picker
     */
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;

    /**
     * Handles high-level drive functions like sync
     */
    private DriveClient mDriveClient;

    /**
     * Handle access to Drive resources/files.
     */
    private DriveResourceClient mDriveResourceClient;


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
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
        Log.d("SAIMBHI", "The images are getting stored in:" + storageDir);
        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private String setPic() {

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;


		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Save to Google Drive
        saveFileToDrive(bitmap);


//		Matrix matrix = new Matrix();
//		matrix.postRotate(90);
//		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,photoW,photoH,true);
//		Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth() , scaledBitmap.getHeight(), matrix, true);

        String OCRresult = null;
        mTess.setImage(bitmap);
        OCRresult = mTess.getUTF8Text();
        Log.d("SAIMBHI", "The OCR Result is:" + OCRresult);
        return OCRresult;

    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        Log.d("FILECREATION", "THE FILE CREATED IS: " + sFileName);
        try {
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            File root = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES) + "/CameraSample");
            File root = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS) + "/CameraSample");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d("SAIMBHI", "Error in creating a name file");
            e.printStackTrace();
        }
    }

    private void galleryAddPic(String text) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        String textFileName = (f.getName().substring(0, f.getName().length() - 4).concat(".txt"));
        generateNoteOnSD(this, textFileName, text);
        finish();
        startActivity(getIntent());
    }


    private void dispatchTakePictureIntent(int actionCode) {

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

    private void handleBigCameraPhoto() {
        Log.d("SAIMBHI", "HANDLEBIGCAMERA method executed");

        if (mCurrentPhotoPath != null) {
            String exctractedText = setPic();
            galleryAddPic(exctractedText);
            mCurrentPhotoPath = null;
        }

    }


    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("SAIMBHI", "onCreate method executed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Log.d("TESTING", "inside onCreate before sign in");
        signIn();

        // retrireving files from the storage starts
//        file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES) + "/CameraSample");
        file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + "/CameraSample");
        Log.d("SAIMBHI", "The size is:" + file.length());
        if(file.length() != 0) {
        if (file.isDirectory()) {
            listFile = file.listFiles();
            Log.d("SAIMBHI", "The size is:" + listFile.length);

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
//        Log.d("SAIMBHI", "The FileNameStrings is:" + FileNameStrings.length);
//        Log.d("SAIMBHI", "The FilePathStrings is:" + FilePathStrings.length);

        // passing combination class stuff as an arraylist to LazyAdapter
        for (int i = 0; i < FileNameStrings.length; i++)
        {
             combo = new Combination(FileNameStrings[i], FilePathStrings[i], null);
            // Binds all strings into an array
            arraylist.add(combo);
        }

        // to create ListView
        adapter = new
                LazyAdapter(this,PhotoIntentActivity.this, this.arraylist);
        Log.d("FILTER", "The size is:" + arraylist.size());
        list = (ListView) findViewById(R.id.list);

        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.search); // ********to be added**********

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() { //********to be added**********

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                //String text = combo.getFilterText();
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                try {
                    adapter.filter(text); //important to active
                    status = (TextView) findViewById(R.id.status);
                    status.setText(text.isEmpty() ? "" : "Found in: " + adapter.getCount());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Code for image loader

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String searchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                Intent textIntent = new Intent(PhotoIntentActivity.this, TextContentActivity.class);
                File selectedFile = new File(adapter.getItem(pos).getFilePath());
                String thumbnailPath = adapter.getItem(pos).getImagePath();
                String fileName = "File Name: " + adapter.getItem(pos).getFileName().replace(".txt",".jpg");

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                    Bitmap bitmap = BitmapFactory.decodeFile(thumbnailPath, bmOptions);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] imageByteArray = stream.toByteArray();


                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                        String line;
                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    } catch (IOException e) {
                        throw new Error("Error reading file", e);
                    }

                byte[] textBytes = text.toString().getBytes();
                Bundle extras = new Bundle();
                extras.putString("fileName",fileName);
                extras.putByteArray("image",imageByteArray);
                extras.putByteArray("name", textBytes);
                extras.putString("search", searchText);
                textIntent.putExtras(extras);
                startActivity(textIntent);

            }
        });

        } else {
            Toast.makeText(PhotoIntentActivity.this, "There is no data", Toast.LENGTH_LONG).show();

            Log.d("SAIMBHI", "NOTHING IN THE DIRECTORY");
        }

        mImageBitmap = null;
        mVideoUri = null;

        Button picBtn = (Button) findViewById(R.id.btnIntend);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ACTION_TAKE_PHOTO_B: {
                Log.i("SAIMBHI", "CAMERA ACTIVITY progress"+Activity.RESULT_OK + resultCode);
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                    return;
                }
                Log.d(TAG,"inside ActivityResult");
                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                    Log.d(TAG,"the account is"+getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                }
                break;


        }
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
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    //GOOGLE DRIVE METHODS FROM HERE
    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    protected void signIn() {
        Log.d(TAG,"inside signIn()");
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
            Log.d(TAG,"initialized");
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            Log.d(TAG,"creating GoogleSignInClient");
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        Log.d(TAG,"Signed In");
    }

    /** Create a new file and save it to Drive. */
    private void saveFileToDrive(Bitmap mBitmapToSave) {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Log.d(TAG, "inside saveFileToDrive");
        final Bitmap image = mBitmapToSave;

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                Log.d(TAG,"Calling the last method");
                                return createFileIntentSender(task.getResult(), image);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents, Bitmap image) {
        Log.i(TAG, "New contents created.");
        Log.d("TESTING", "inside createFileIntentSender");
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        // Write the bitmap data from it.
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
        try {
            outputStream.write(bitmapStream.toByteArray());
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg")
                        .setTitle("Photo.jpeg")
                        .build();

        Log.i(TAG, "New contents created 2");
        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        Log.i(TAG, "New contents create 3");

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                Log.i(TAG, "New contents created 4");
                                Toast.makeText(getApplicationContext(),"Filed Uploaded Succesfully",Toast.LENGTH_LONG);
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                                return null;
                            }
                        });
    }

}