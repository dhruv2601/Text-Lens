package datapole.ocrtext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OCRAct extends AppCompatActivity {

    public String dataSetUrl = "";
    private static final String TAG = "OCRText.java";
    private EditText editText;
    private String ocrStr = "";
    private FloatingActionButton listen;
    private AppCompatButton dropbox;
    private AppCompatButton txtFile;
    private AppCompatButton share;

    final static private String APP_KEY = "80obijhq074fhxn";
    final static private String APP_SECRET = "v22myjt9gq3f15i";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    DropboxAPI<AndroidAuthSession> mDBApi;
    private AndroidAuthSession session;

    private static final boolean USE_OAUTH1 = false;

    private boolean mLoggedIn;
    public int cmon = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ocr);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        initialize_session();

        listen = (FloatingActionButton) findViewById(R.id.fab_listen);
        editText = (EditText) findViewById(R.id.edt_txt);
        dropbox = (AppCompatButton) findViewById(R.id.dropbox);
        txtFile = (AppCompatButton) findViewById(R.id.txtfile);
        share = (AppCompatButton) findViewById(R.id.share);

        final SharedPreferences txtURI = this.getSharedPreferences("txtURI", 0);
        final int ind = txtURI.getInt("ind", 0);
        final SharedPreferences.Editor editor = txtURI.edit();
        final String[] bhaiName = {""};

        dropbox.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (cmon == 0) {

                                               cmon = 1;

                                               final String[] str_name = {""};
                                               final AlertDialog.Builder alert = new AlertDialog.Builder(OCRAct.this);
                                               alert.setTitle("Save File");
                                               alert.setMessage("Enter File Name");
                                               alert.setIcon(R.drawable.appicon);
                                               final EditText input = new EditText(OCRAct.this);
                                               alert.setView(input);
                                               final String[] fileName = {""};
                                               alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                   public void onClick(DialogInterface dialog, int whichButton) {
                                                       String srt = input.getText().toString();
                                                       str_name[0] = srt;
                                                       bhaiName[0] = srt;

                                                       mDBApi.getSession().startOAuth2Authentication(OCRAct.this);

                                                       AlertDialog.Builder alert1 = new AlertDialog.Builder(OCRAct.this);
                                                       alert1.setTitle("DROPBOX");
                                                       alert1.setMessage("Log in into your Dropbox Account.");
                                                       alert1.setIcon(R.drawable.dropbox);
                                                       alert1.setCancelable(false);

                                                       alert1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                           @Override
                                                           public void onClick(DialogInterface dialogInterface, int i) {
                                                               File file = new File("/storage/emulated/0/TextLens/" + bhaiName[0] + ".txt");

                                                               UpToDropbox upToDropbox = new UpToDropbox(OCRAct.this, mDBApi, "", file);
                                                               upToDropbox.execute();
                                                               Toast.makeText(OCRAct.this, "Saved in Dropbox and in phone.", Toast.LENGTH_SHORT).show();
                                                               txtFile.setVisibility(View.GONE);
                                                           }
                                                       });
                                                       alert1.show();

//                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                                                       try {
                                                           String path = "file:///storage/emulated/0/TextLens/" + str_name[0] + ".txt";
                                                           String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                                                           Log.d(TAG, "fileName: " + str_name[0]);
                                                           editor.putString("filename" + String.valueOf(ind), str_name[0]);
                                                           editor.putString("path" + String.valueOf(ind), path);         // first saved at 0
                                                           editor.putString("uri" + String.valueOf(ind), String.valueOf(MainActivity.cropURI));
                                                           Log.d(TAG, "cropUri: " + String.valueOf(MainActivity.cropURI));
                                                           editor.putInt("ind", ind + 1);
                                                           editor.putString("date" + ind, date);
                                                           editor.commit();

                                                           fileName[0] = str_name[0];
                                                           String content = editText.getText().toString();
                                                           try {
                                                               File root = new File(Environment.getExternalStorageDirectory(), "TextLens");
                                                               if (!root.exists()) {
                                                                   root.mkdirs();
                                                               }
                                                               File gpxfile = new File(root, str_name[0] + ".txt");
                                                               FileWriter writer = new FileWriter(gpxfile);
                                                               writer.append(editText.getText().toString());
                                                               writer.flush();
                                                               writer.close();
                                                               Toast.makeText(OCRAct.this, "Saved", Toast.LENGTH_SHORT).show();
                                                           } catch (IOException e) {
                                                               e.printStackTrace();
                                                           }

                                                           Toast.makeText(getBaseContext(),
                                                                   "File Saved",
                                                                   Toast.LENGTH_SHORT).show();
                                                       } catch (Exception e) {
                                                           Toast.makeText(getBaseContext(), e.getMessage(),
                                                                   Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                               });
                                               alert.show();

                                               // isse pehle jara file ka nam input kra lio and then use that in DropBoxFilePath
                                           }
//                                           else {
//                                               File file = new File("/storage/emulated/0/TextLens/" + bhaiName[0] + ".txt");
//
//                                               UpToDropbox upToDropbox = new UpToDropbox(OCRAct.this, mDBApi, "", file);
//                                               upToDropbox.execute();
//                                               Toast.makeText(OCRAct.this, "Saved in Dropbox and in phone.", Toast.LENGTH_SHORT).show();
//                                               txtFile.setVisibility(View.GONE);
//                                           }
                                       }
                                   }
        );

        share.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View view) {
                                         Intent shareIntent = new Intent(Intent.ACTION_SEND);

                                         shareIntent.setType("text/html");
                                         shareIntent.setType("text/plain");
                                         shareIntent.putExtra(Intent.EXTRA_SUBJECT, ("https://play.google.com/store/apps/details?id=datapole.ocrtext" + "\n"));   // instead send the description here

                                         shareIntent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());
                                         OCRAct.this.startActivity(Intent.createChooser(shareIntent, "Share scanned text"));
                                     }
                                 }

        );

        txtFile.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {

                                           final String[] str_name = {""};
                                           AlertDialog.Builder alert = new AlertDialog.Builder(OCRAct.this);
                                           alert.setTitle("Save File");
                                           alert.setMessage("Enter File Name");
                                           final EditText input = new EditText(OCRAct.this);
                                           alert.setView(input);
                                           alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int whichButton) {
                                                   String srt = input.getText().toString();
                                                   str_name[0] = srt;

//                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                                                   try {
                                                       String path = "file:///storage/emulated/0/TextLens/" + str_name[0] + ".txt";
                                                       String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                                                       Log.d(TAG, "fileName: " + str_name[0]);
                                                       editor.putString("filename" + String.valueOf(ind), str_name[0]);
                                                       editor.putString("path" + String.valueOf(ind), path);         // first saved at 0
                                                       editor.putString("uri" + String.valueOf(ind), String.valueOf(MainActivity.cropURI));
                                                       Log.d(TAG, "cropUri: " + String.valueOf(MainActivity.cropURI));
                                                       editor.putInt("ind", ind + 1);
                                                       editor.putString("date" + ind, date);
                                                       editor.commit();

                                                       String fileName = str_name[0];
                                                       String content = editText.getText().toString();
                                                       try {
                                                           File root = new File(Environment.getExternalStorageDirectory(), "TextLens");
                                                           if (!root.exists()) {
                                                               root.mkdirs();
                                                           }
                                                           File gpxfile = new File(root, str_name[0] + ".txt");
                                                           FileWriter writer = new FileWriter(gpxfile);
                                                           writer.append(editText.getText().toString());
                                                           writer.flush();
                                                           writer.close();
                                                           Toast.makeText(OCRAct.this, "Saved", Toast.LENGTH_SHORT).show();
                                                       } catch (IOException e) {
                                                           e.printStackTrace();
                                                       }

                                                       Toast.makeText(getBaseContext(),
                                                               "File Saved",
                                                               Toast.LENGTH_SHORT).show();
                                                   } catch (Exception e) {
                                                       Toast.makeText(getBaseContext(), e.getMessage(),
                                                               Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                                           alert.show();
                                       }
                                   }

        );

        SharedPreferences pref = this.getSharedPreferences("engDataSet", 0);
        dataSetUrl = pref.getString("dataSetUrl", "");
        if (dataSetUrl.equals(""))

        {
            new AlertDialog.Builder(OCRAct.this).setTitle("Language Data Not Found").setMessage("Go to language settings and download the language data")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Intent for languages vala act
                        }
                    }).show();
        }

        final int[] flag = {0};
        listen.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View view) {
                                          if (flag[0] == 1) {
                                              MainActivity.t1.stop();

                                          } else {
                                              flag[0] = 1;
                                              if (ocrStr == "") {
                                                  MainActivity.t1.speak("No text present", TextToSpeech.QUEUE_ADD, null);
                                              } else {
                                                  String temp = ocrStr.replaceAll("[^a-zA-Z0-9]+", " ");
                                                  MainActivity.t1.speak(temp, TextToSpeech.QUEUE_ADD, null);
                                              }
                                          }
                                      }
                                  }

        );
        new

                extractOCR()

                .

                        execute();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void logOut() {
        // Remove credentials from the session
        mDBApi.getSession().unlink();
    }


    private void loadAuth(AndroidAuthSession session) {
        String key = APP_KEY;
        String secret = APP_SECRET;
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    protected void initialize_session() {
        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mDBApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
//        if (loggedIn) {
//            mSubmit.setText("Unlink from Dropbox");
//            mDisplay.setVisibility(View.VISIBLE);
//        } else {
//            mSubmit.setText("Link with Dropbox");
//            mDisplay.setVisibility(View.GONE);
//            mImage.setImageDrawable(null);
//        }
    }

    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }


//    protected void onResume() {
//        super.onResume();
//
//        if (mDBApi.getSession().authenticationSuccessful()) {
//            try {
//
//                // Required to complete auth, sets the access token on the session
//                mDBApi.getSession().finishAuthentication();
//
//                // retrieve access token
//                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
//
//            } catch (IllegalStateException e) {
//                Log.i(TAG, "Error authenticating:: " + e);
//            }
//        }
//    }

    private Bitmap GetBinaryBitmap(Bitmap bitmap_src) {
        Bitmap bitmap_new = bitmap_src.copy(bitmap_src.getConfig(), true);

        for (int x = 0; x < bitmap_new.getWidth(); x++) {
            for (int y = 0; y < bitmap_new.getHeight(); y++) {
                int color = bitmap_new.getPixel(x, y);
                color = GetNewColor(color);
                bitmap_new.setPixel(x, y, color);
            }
        }
        return bitmap_new;
    }

    private double GetColorDistance(int c1, int c2) {
        int db = Color.blue(c1) - Color.blue(c2);
        int dg = Color.green(c1) - Color.green(c2);
        int dr = Color.red(c1) - Color.red(c2);

        double d = Math.sqrt(Math.pow(db, 2) + Math.pow(dg, 2) + Math.pow(dr, 2));
        return d;
    }

    private int GetNewColor(int c) {
        double dwhite = GetColorDistance(c, Color.WHITE);
        double dblack = GetColorDistance(c, Color.BLACK);

        if (dwhite <= dblack) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public class extractOCR extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;
        ProgressDialog pDial = new ProgressDialog(OCRAct.this);

        @Override
        protected void onPreExecute() {
            pDial.setIcon(R.drawable.appicon);
            pDial.setMessage("Extracting Text");
            pDial.setCancelable(false);
            pDial.setTitle("Scanning Image");
            pDial.show();

//            Intent i = getIntent();
//            Uri uri = Uri.parse(i.getStringExtra("uri"));
            Uri uri = MainActivity.cropURI;
            Log.d(TAG, "uriOCR: " + uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap binarizedBitmap = GetBinaryBitmap(bitmap);
            TessBaseAPI baseAPI = new TessBaseAPI();
            baseAPI.setDebug(true);
            String tempUri = "";
            Log.d(TAG, "dataSetUrl: " + dataSetUrl);
            for (int i = 0; i < dataSetUrl.length(); i++) {
                if (i >= 7) {
                    if (dataSetUrl.charAt(i) == 't' && dataSetUrl.charAt(i + 1) == 'e' && dataSetUrl.charAt(i + 2) == 's' && dataSetUrl.charAt(i + 3) == 's') {
                        break;
                    } else {
                        tempUri += dataSetUrl.charAt(i);
                    }
                }
            }

            Log.d(TAG, "tempURI:: " + tempUri);
            Log.d(TAG, "uri: " + tempUri);
            baseAPI.init(tempUri, "eng");            //content://downloads/my_downloads/1620
            baseAPI.setImage(binarizedBitmap);
            String binaryText = baseAPI.getUTF8Text();
            baseAPI.end();

            Log.d(TAG, "OCR text: " + binaryText);      // the text after binarized bitmap
            ocrStr = binaryText;

//            if (MainActivity.lang.equalsIgnoreCase("eng"))
//            {
//                String temp = binaryText.replaceAll("[^a-zA-Z0-9]+", " ");
//                ocrStr = temp;
//                Log.d(TAG, "tempStr: " + temp);
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDial.dismiss();
            editText.setText(ocrStr);
            View view = OCRAct.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            super.onPostExecute(aVoid);
        }
    }
}