package com.example.ryosuke.nakagawa.speaklab;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int PERMISSIONS_REQUEST_CODE2 = 200;
    SpeechRecognizer sr;
    SpeechListener speechListener;

    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    Button mButton;

    //ダイアログの表示
    final CharSequence[] items = { "あ行", "か行", "さ行","た行","な行","は行","ま行","や行","ら行","わ行" };

    CharSequence[] checkedItems;
    String selectLine;              //文字列の保持
    AlertDialog.Builder builder1;
    AlertDialog.Builder builder2;

    Camera camera;
    final int cameraId = 1;

    private ViewPager mPager;
    ImageView newImageView;
    private boolean flag = false;
    FrameLayout layout;

    AssetManager mAsset;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //パーミッションの許可
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 系以下の場合
        } else {
            getContentsInfo();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            case PERMISSIONS_REQUEST_CODE2:
                Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
                sr.startListening(intent);
                break;
            default:
                break;
        }
    }

    //ここからが中身
    private void getContentsInfo(){
        //初期化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        speechListener = new SpeechListener(MainActivity.this);
        setSupportActionBar(toolbar);


        //surfaceViewを設定
        FrameLayout framelayout = (FrameLayout)findViewById(R.id.frameLayout);
        SurfaceView surfaceView = new SurfaceView(this);
        framelayout.addView(surfaceView, 0, new ViewGroup.LayoutParams(MP, MP));
        SurfaceHolder holder = surfaceView.getHolder();

        camera = Camera.open(cameraId);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new CameraFunc(camera));







        //ダイアログを表示
        builder1 = new AlertDialog.Builder(MainActivity.this);
        builder2 = new AlertDialog.Builder(MainActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                sr.setRecognitionListener(speechListener);
                Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());

                //パーミッションの許可
                // Android 6.0以降の場合
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // パーミッションの許可状態を確認する
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        // 許可されている
                        sr.startListening(intent);
                    } else {
                        // 許可されていないので許可ダイアログを表示する
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CODE2);
                    }
                    // Android 5系以下の場合
                } else {
                    sr.startListening(intent);
                }

            }
        });

        //viewPagerの実装
        selectLine = "a";
        CustomPageAdapter adapter = new CustomPageAdapter(this,selectLine);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setAdapter(adapter);

        layout = (FrameLayout)findViewById(R.id.layout);

        //Buttonの実装
        mButton =(Button)findViewById(R.id.button);
        mButton.setText("横の画像を表示する");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == false) {
                    String image = "line_"+selectLine+"/" + String.valueOf(mPager.getCurrentItem()) + "_side.jpg";
                    newImageView = new ImageView(MainActivity.this);
                    try {
                        InputStream is = getResources().getAssets().open(image);
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        newImageView.setImageBitmap(bm);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    layout.addView(newImageView);
                    flag = true;

                    mButton.setText("正面の画像を表示する");
                }else{layout.removeView(newImageView);
                    flag = false;
                    mButton.setText("横の画像を表示する");
                }
            }

        });

    }

    //メニューバーの設定
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {



            builder1.setTitle("文字の選択").setSingleChoiceItems(items, -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if(flag ==true){
                                layout.removeView(newImageView);
                            }

                            switch (i){
                                case 0://あ行
                                    selectLine = "a";
                                    break;
                                case 1:
                                    selectLine = "ka";
                                    break;//か行
                                case 2:
                                    selectLine = "sa";
                                    break;//さ行
                                case 3:
                                    selectLine = "ta";
                                    break;//た行
                                case 4:
                                    selectLine = "na";
                                    break;//な行
                                case 5:
                                    selectLine = "ha";
                                    break;//は行
                                case 6:
                                    selectLine = "ma";
                                    break;//ま行
                                case 7:
                                    selectLine = "ya";
                                    break;//や行
                                case 8:
                                    selectLine = "ra";
                                    break;//ら行
                                case 9:
                                    selectLine = "wa";
                                    break;//わ行

                            }
                            showImage(selectLine);

                        }
                    })
                    .setPositiveButton("決定", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showImage(String selectLine){

        CustomPageAdapter adapter = (CustomPageAdapter) mPager.getAdapter();
        adapter.set(selectLine);
        adapter.notifyDataSetChanged();

        flag = false;
        mButton.setText("横の画像を表示する");
    }


}
