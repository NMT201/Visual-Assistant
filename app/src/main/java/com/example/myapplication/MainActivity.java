package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.DescribePost;
import com.example.myapplication.model.DescribeResponse;
import com.example.myapplication.model.StreamingPost;
import com.example.myapplication.model.StreamingResponse;
import com.google.gson.Gson;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CameraActivity {

    public final int SKIP_FRAME = 10;
    public final int MICROPHONE_REQUEST_CODE = 200;

    public final int FIRST_WARNING = 2;
    public final int DUPLICATE_WARNING = 100;

    //Camera preview
    private CameraBridgeViewBase mOpenCvCamera;

    //Log tag
    private static String LOGTAG = "OpenCV_Log";

    //Process param
    private int countWarnOnroad = 0;
    private int countWarnObstacle = 0;

    private String base64;
    public int count = 0;
    private Mat mRgba;
    private String question = new String();
    private TextToSpeech mTTS;
    private Boolean requestDes = false;

    private String logMessage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCamera = (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        mOpenCvCamera.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCamera.setCvCameraViewListener(cvCameraViewListener);

        HashMap<String, String> map = new HashMap<>();
        String a = map.get("key");

        mTTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    }
                } else {
                }
            }
        });
    }

    private BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS : {
//                    Log.v(LOGTAG, "OpenCV Loaded");
                    mOpenCvCamera.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    //Speech to text
    public void getSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if ((intent.resolveActivity(getPackageManager()) != null)) {
            startActivityForResult(intent, MICROPHONE_REQUEST_CODE);
        } else {
            Toast.makeText(MainActivity.this, "Microphone is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MICROPHONE_REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    question = result.get(0);

                    if (base64.length() > 0) {
                        DescribePost jsonDescribe = new DescribePost(base64, question);

                        long start = System.currentTimeMillis();
                        clickCallDescribe(jsonDescribe, start);
                    }
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    getSpeechInput();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    //Speak text
    private void speak(String text) {
        float pitch = 0.9f;
        float speed = 1.1f;
        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //Call api streaming function
    private void clickCallStreaming(StreamingPost jsonStreaming, long start) {
        ApiService.API_SERVICE.Streaming(jsonStreaming).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


                String res = response.body();
                Gson gson = new Gson();
                StreamingResponse streamingResponse = gson.fromJson(res, StreamingResponse.class);
                String onRoadWarning = streamingResponse.warnOnRoad();
                if (onRoadWarning.length() > 0 ) {
                    countWarnOnroad ++;
                    if (countWarnOnroad == FIRST_WARNING) {
                        speak(onRoadWarning);
                    }
                    if (countWarnOnroad == DUPLICATE_WARNING) {
                        countWarnOnroad = 0;
                    }

                } else {
                    countWarnOnroad = 0;

                }

                String obstacleWarning = streamingResponse.warnObstacle();
                if (obstacleWarning.length() > 0) {
                    countWarnObstacle++;
                    if (countWarnObstacle == FIRST_WARNING) {
                        speak(obstacleWarning);
                    }
                    if (countWarnObstacle == DUPLICATE_WARNING) {
                        countWarnObstacle = 0;
                    }
                } else {
                    countWarnObstacle = 0;
                }
                long end = System.currentTimeMillis();

//                write logging file
//                logMessage += res + " " + Float.toString((end - start)/1000f) + "\n";


//                Log.v("TimeStreaming", Float.toString((end - start)/1000f));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API fail", Toast.LENGTH_LONG).show();
                Log.d("API", "Failed");
            }

        });
    }

    //Call api describe function
    private void clickCallDescribe(DescribePost jsonDescribe, long start) {
        ApiService.API_SERVICE.Describe(jsonDescribe).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.e("Call Api", "Call Success");
                String res = response.body();

                if (res.length() > 10) {

                    Gson gson = new Gson();

                    DescribeResponse describeResponse = gson.fromJson(res, DescribeResponse.class);
//                    Log.d("ResponseDes", Float.toString(describeResponse.getTime_process()));

                    String message = describeResponse.toString();


                    speak(message);
                }
                else {
                    String message = new String();
                    switch (res.substring(2)) {
                        case "Left":
                            message = "Bên trái không có gì";
                            break;
                        case "Right":
                            message = "Bên phải không có gì";
                            break;
                        case "Front":
                            message = "Phía trước không có gì";
                            break;
                        case "Near":
                            message = "Ở gần không có gì";
                            break;
                        case "Far":
                            message = "Phía xa không có gì";
                            break;
                        case "Road":
                            message = "Dưới đường không có gì";
                            break;
                        case "Sidewalk":
                            message = "Trên lề không có gì";
                            break;
                        case "All":
                            message = "Không có gì cả";
                            break;
                    }
                    speak(message);


                }
                long end = System.currentTimeMillis();
//                write logging file
//                logMessage += res + " " + Float.toString((end - start)/1000f) + "\n";
//
//                Log.v("ResponseDes", Float.toString((end - start)/1000F));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    @Override
    protected List<?extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCamera);
    }

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mRgba = new Mat(height, width, CvType.CV_8UC4);
        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


            mRgba = inputFrame.rgba();

            Mat resizeimage = new Mat();

            mRgba.copyTo(resizeimage);
            count++;
            if (count % SKIP_FRAME == 0) { /*&& requestDes == false) {*/

                Size sz = new Size(960,960);
                Imgproc.resize( resizeimage, resizeimage, sz );

                Bitmap bitmap;
                bitmap = Bitmap.createBitmap(resizeimage.cols(), resizeimage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(resizeimage, bitmap);

                if (bitmap != null) {
                    base64 = bitmap2Base64(bitmap);

                    long start = System.currentTimeMillis();
                    StreamingPost jsonStreaming = new StreamingPost(base64);
                    clickCallStreaming(jsonStreaming, start);

                }
            }
            if(count > 100) count = 0;

            return mRgba;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCamera != null) {
            mOpenCvCamera.disableView();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
//          Write logging file
//        File root = android.os.Environment.getExternalStorageDirectory();
//        File dir = new File (root.getAbsolutePath() + "/download");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File file = new File(dir, "myData.txt");
//
//        try {
//            FileOutputStream f = new FileOutputStream(file);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println(logMessage);
//            pw.flush();
//            pw.close();
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.i("FILE", "******* File not found. Did you" +
//                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallBack);
        } else {
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCamera != null) {
            mOpenCvCamera.disableView();
        }

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    //convert Bitmap to Base64
    public String bitmap2Base64(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG , 95, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }
}