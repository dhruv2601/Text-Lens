package datapole.ocrtext;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGall;
    private FloatingActionButton fabDon;
    private static final int CAMERA_REQUEST = 2601; // field
    public static final int PICK_IMAGE = 1903;
    public static String DATA_PATH = Environment
            .getExternalStorageDirectory().toString();
    public static final String DATA_PATH_EXT_BUS = "/BusinessCardScanner/";
    public static Uri cropURI;
    public static SharedPreferences sref;
    private DownloadManager dm;
    private long enqueue;
    public static boolean hasEntered;
    public static String urlTrainedDataSet;
    private ProgressDialog pDialog;
    public static TextToSpeech t1;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.all_cards_list_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new allCardRecyclerViewAdapter(getDataSet(), this);


        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabDon = (FloatingActionButton) findViewById(R.id.fab_donate);
        fabGall = (FloatingActionButton) findViewById(R.id.fab_gall);

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        t1.setLanguage(Locale.forLanguageTag("values-hi-rIN"));
                    } else {
                        t1.setLanguage(Locale.ENGLISH);
                    }
                }
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT)
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        fabGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    Log.i("after select", "get image");
                    startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
                } catch (Exception e) {
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
            }
        });

        sref = MainActivity.this.getSharedPreferences("entered", 0);
        SharedPreferences.Editor editor = sref.edit();
        pDialog = new ProgressDialog(this);

        if (sref.getBoolean("entered", false) == false) {
            Log.d(TAG, "first Time installation");

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/BusinessCardScanner/tessdata");
            if (file.exists()) {
                Toast.makeText(this, "Data exists", Toast.LENGTH_SHORT).show();
                SharedPreferences pref2 = this.getSharedPreferences("engDataSet", 0);
                SharedPreferences.Editor edit = pref2.edit();
                edit.putString("dataSetUrl", "file:///storage/emulated/0/mounted/tessdata/eng.traineddata");
                edit.commit();

                // BC Scanner ka engDataSet hai phone mn, no need to download, save it in SharedPref(only once XD)
            } else {
                pDialog.setIcon(R.drawable.appicon);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.incrementProgressBy(5);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();

                Uri uri;
                uri = Uri.parse("https://www.dropbox.com/s/sdwvyelq68q012y/eng.traineddata?dl=1");
                // download vala code here
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Language = English")
                        .setDescription("To operate OCR on english language.")
                        .setDestinationInExternalPublicDir(Environment.getExternalStorageState(),
                                "tessdata/eng.traineddata");
                enqueue = dm.enqueue(request);
                hasEntered = false;
                editor.putBoolean("entered", true);
                editor.apply();
            }
//            Intent i = new Intent(MainActivity1.this, ActivityIntro.class);
//            startActivity(i);
        } else {
            hasEntered = true;
        }

        BroadcastReceiver receiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pDialog.dismiss();
                String action = intent.getAction();
                Log.d(TAG, "onRecieve for downloading");
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long download = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int coloumnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(coloumnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            urlTrainedDataSet = uriString;

                            SharedPreferences pref = context.getSharedPreferences("engDataSet", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("dataSetUrl", uriString);
                            edit.putString("downloaded", "1");
                            edit.commit();
                            Log.d(TAG, "uriString: " + uriString);
                            //  --------------->>>>>>>>>>>>>>>>       THIS IS THE DOWNLOADED AUDIO URI       <<<<<<<------
                        }
                    }
                }
            }
        };
        registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent newIntent = new Intent(MainActivity.this, OCRAct.class);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
            Uri imageUri = data.getData();
            Log.d(TAG, "imageURI: " + imageUri);
            CropImage.activity(imageUri)                    // starting a new crop image activity
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            CropImage.activity(selectedImageUri)                    // starting a new crop image activity
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {            // handling the result after cropping
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d(TAG, "resultURI: " + resultUri);
                cropURI = resultUri;
                newIntent.putExtra("uri", resultUri);
                startActivity(newIntent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "error: " + error.getMessage());
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}