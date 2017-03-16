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

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class OCRAct extends AppCompatActivity {

    public static final String PACKAGE_NAME = "datapole.ocrtext";
    public static String DATA_PATH = Environment
            .getExternalStorageDirectory().toString();
    public String dataSetUrl = "";
    public static final String DATA_PATH_EXT_OCR = "/OCRText/";
    public static final String DATA_PATH_EXT_BUS = "/BusinessCardScanner/";
    public static final String lang = "eng";
    private static final String TAG = "OCRText.java";
    private EditText editText;
    private String ocrStr = "";
    private FloatingActionButton listen;
    private AppCompatButton dropbox;
    private AppCompatButton txtFile;
    private AppCompatButton share;

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

        listen = (FloatingActionButton) findViewById(R.id.fab_listen);
        editText = (EditText) findViewById(R.id.edt_txt);
        dropbox = (AppCompatButton) findViewById(R.id.dropbox);
        txtFile = (AppCompatButton) findViewById(R.id.txtfile);
        share = (AppCompatButton) findViewById(R.id.share);

        SharedPreferences txtURI = this.getSharedPreferences("txtURI", 0);
        final int ind = txtURI.getInt("ind", 0);
        final SharedPreferences.Editor editor = txtURI.edit();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.setType("text/html");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, ("https://play.google.com/store/apps/details?id=datapole.ocrtext" + "\n"));   // instead send the description here

                shareIntent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());
                OCRAct.this.startActivity(Intent.createChooser(shareIntent, "Share scanned text"));
            }
        });

        txtFile.setOnClickListener(new View.OnClickListener() {
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
                            editor.putString("uri" + String.valueOf(ind), String.valueOf(MainActivity.simpleURI));
                            Log.d(TAG,"cropUri: "+String.valueOf(MainActivity.simpleURI));
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


//                            FileOutputStream outputStream = null;
//                            try {
//                                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
//                                outputStream.write(content.getBytes());
//                                outputStream.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }


//                    File myFile = new File(path);                                   //  check here
//                    myFile.createNewFile();
//                    FileOutputStream fOut = new FileOutputStream(myFile);
//                    OutputStreamWriter myOutWriter =
//                            new OutputStreamWriter(fOut);
//                    myOutWriter.append(editText.getText().toString());
//                    myOutWriter.close();
//                    fOut.close();
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
        });

        SharedPreferences pref = this.getSharedPreferences("engDataSet", 0);
        dataSetUrl = pref.getString("dataSetUrl", "");
        if (dataSetUrl.equals("")) {
            new AlertDialog.Builder(OCRAct.this).setTitle("Language Data Not Found").setMessage("Go to language settings and download the language data")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Intent for languages vala act
                        }
                    }).show();
        }

        final int[] flag = {0};
        listen.setOnClickListener(new View.OnClickListener() {
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
        });
        new extractOCR().execute();
    }

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
