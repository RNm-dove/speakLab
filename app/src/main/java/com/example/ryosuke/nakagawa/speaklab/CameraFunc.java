package com.example.ryosuke.nakagawa.speaklab;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by ryosuke on 2017/01/28.
 */
public class CameraFunc implements SurfaceHolder.Callback {

                Camera camera;

                @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                int cameraId = 1;
                camera = Camera.open(cameraId);
                // ディスプレイの向き設定
                setCameraDisplayOrientation(cameraId);
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // TODO Auto-generated method stub
                camera.stopPreview();
                // プレビュー画面のサイズ設定
                Camera.Parameters params = camera.getParameters();
                params.setPreviewSize(width, height);
                List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
                Camera.Size size = previewSizes.get(0);
                params.setPreviewSize(size.width, size.height);
                camera.setParameters(params);
                // プレビュー開始
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                camera.stopPreview();
                camera.release();
                camera = null;
            }

            // ディスプレイの向き設定
            public void setCameraDisplayOrientation(int cameraId) {
                // カメラの情報取得
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                // ディスプレイの向き取得
                int rotation =0;
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }
                // プレビューの向き計算
                int result;
                if (cameraInfo.facing == cameraInfo.CAMERA_FACING_FRONT) {
                    result = (cameraInfo.orientation + degrees) % 360;
                    result = (360 - result) % 360; // compensate the mirror
                } else {// back-facing
                    result = (cameraInfo.orientation - degrees + 360) % 360;
                }
                // ディスプレイの向き設定
                camera.setDisplayOrientation(result);
            }



}

