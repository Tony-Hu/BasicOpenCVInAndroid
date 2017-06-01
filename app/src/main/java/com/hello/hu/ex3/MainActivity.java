package com.hello.hu.ex3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2,OnItemSelectedListener{

    CameraBridgeViewBase mOpenCvCameraView;// will point to our View widget for our image
    Spinner spinner_menu;

    String[] menu_items;

    String menu_item_selected;

    MyAlgorithm myAlgorithm;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback() {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i("OPENCV","OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAlgorithm = new MyAlgorithm();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menu_items = getResources().getStringArray(R.array.spinner_menu);
        menu_item_selected = menu_items[0];//initialize to first item in arry
        Log.i("SPINNER", "menu item is " + menu_item_selected);
        spinner_menu = (Spinner)findViewById(R.id.spinner_menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_menu,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_menu.setAdapter(adapter);
        spinner_menu.setSelection(0);
        spinner_menu.setOnItemSelectedListener(this);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,mLoaderCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        menu_item_selected = parent.getItemAtPosition(position).toString();
        Log.i("SPINNER","choice is "+this.menu_item_selected);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        menu_item_selected = menu_items[0];
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        switch (menu_item_selected)
        {
            case "Threshold":
                return thresholdMat(inputFrame);
            case "Mean Blur":
                return meanBlurMat(inputFrame);
            case "Gaussian Blur":
                return gaussianBlurMat(inputFrame);
            case "Dialation":
                return dilationMat(inputFrame);
            case "Erosion":
                return erosionMat(inputFrame);
            case "Adaptive Thresholding":
                return adptThresholdMat(inputFrame);
            case "Difference of Gaussian":
                return differenceofGaussianMat(inputFrame);
            case "Canny Edge":
                return cannyEdgeMat(inputFrame);
            case "Sobel Edge":
                return sobelMat(inputFrame);
            case "Corner Detection":
                return cornerMat(inputFrame);
            case "Hough Line Transform":
                return houghMat(inputFrame);
            case "Edgy":
                //return myAlgorithm.myEdgy(inputFrame,100);
                return myAlgorithm.myEdgy2(inputFrame,100);
            case "Best Lines":
                return myAlgorithm.myBestLines2(inputFrame,100);
            case "Random":
            default:
                return randomMat(inputFrame);
        }
    }

    private Mat thresholdMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing thresholding");
        Mat mat = inputFrame.gray();
        Imgproc.threshold(mat,mat,50.0,255.0,Imgproc.THRESH_BINARY);
        return mat;
    }

    private Mat meanBlurMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing mean blur");
        Mat mat = inputFrame.rgba();
        Imgproc.blur(mat,mat,new Size(3,3));
        return mat;
    }

    private Mat gaussianBlurMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing gaussian blur");
        Mat mat = inputFrame.rgba();
        Imgproc.GaussianBlur(mat,mat,new Size(3,3),0);
        return mat;
    }

    private Mat dilationMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing dilation");
        Mat mat = thresholdMat(inputFrame);

        Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(7,7));
        Imgproc.dilate(mat,mat,kernelDilate);
        return mat;
    }


    private Mat erosionMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing erosion");
        Mat mat = thresholdMat(inputFrame);
        Mat kernelErosion = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(7,7));
        Imgproc.erode(mat,mat,kernelErosion);
        return mat;
    }

    private Mat adptThresholdMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing adaptive thresholding");
        Mat mat = inputFrame.gray();
        Imgproc.adaptiveThreshold(mat,mat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,3,0);
        return mat;
    }


    private Mat differenceofGaussianMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing Difference of Gaussian");
        Mat mat = inputFrame.gray();
        Mat gaussianMat1 = new Mat();
        Mat gaussianMat2 = new Mat();
        Imgproc.GaussianBlur(mat,gaussianMat1,new Size(15,15),5);
        Imgproc.GaussianBlur(mat,gaussianMat2,new Size(21,21),5);
        Mat doG = new Mat();
        Core.absdiff(gaussianMat1,gaussianMat2,doG);
        Core.multiply(doG,new Scalar(100),doG);
        Imgproc.threshold(doG,doG,50,255,Imgproc.THRESH_BINARY_INV);
        return doG;
    }

    private Mat cannyEdgeMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing Canny Edge");
        Mat mat = inputFrame.gray();
        Imgproc.Canny(mat,mat,10,100);
        return mat;
    }


    private Mat sobelMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing sobel");
        Mat mat = inputFrame.gray();
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(mat,gradX, CvType.CV_16S,1,0,3,1,0);
        Imgproc.Sobel(mat,gradY, CvType.CV_16S,0,1,3,1,0);
        Core.convertScaleAbs(gradX,gradX);
        Core.convertScaleAbs(gradY,gradY);
        Core.addWeighted(gradX,0.5,gradY,0.5,1,mat);
        return mat;
    }

    private Mat cornerMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing Harris Corner");
        Mat mat = inputFrame.gray();
        Mat corners = new Mat();
        Mat tempDst = new Mat();
        Mat tempDstNorm = new Mat();
        Imgproc.cornerHarris(mat,tempDst,2,3,0.04);
        Core.normalize(tempDst,tempDstNorm,0,255,Core.NORM_MINMAX);

        Core.convertScaleAbs(tempDstNorm,corners);
        Random random = new Random();
        long previous = System.currentTimeMillis();
        for (int i = 0; i < tempDstNorm.cols();i++){
            for (int j = 0; j < tempDstNorm.rows();j++){
                double[] value = tempDstNorm.get(j,i);
                if (value[0] > 150)
                    Imgproc.circle(corners,new Point(i,j),5,new Scalar(random.nextInt(255)),2);
            }
        }
        Log.i("Corner","One frame's run-time "+ (System.currentTimeMillis() - previous)+" ms");
        return corners;
    }

    private Mat houghMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Log.i("SPINNER","performing Hough transformations");
        Mat imageMat = inputFrame.rgba();
        Mat greyMat = new Mat();
        Imgproc.cvtColor(imageMat,greyMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(greyMat,greyMat,10,100);

        int threshold_line = Math.min(greyMat.rows(),greyMat.cols()) / 2;
        Mat lines = new Mat();
        Imgproc.HoughLines(greyMat,lines,1,Math.PI / 180,threshold_line);
        for (int i = 0;i < lines.rows();i++){
            double[] points = lines.get(i,0);
            double rho = points[0];
            double theta = points[1];
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a*rho;
            double y0 = b*rho;

            double x1,x2,y1,y2;

            x1 = x0 + 1000 * (-b);
            y1 = y0 + 1000 * a;
            x2 = x0 - 1000 * (-b);
            y2 = y0 - 1000* a;

            Point pt1 = new Point(x1,y1);
            Point pt2 = new Point(x2,y2);

            Imgproc.line(imageMat,pt1,pt2,new Scalar(0,0,255),1);
        }
        return imageMat;
    }

    private Mat randomMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        long previous = System.currentTimeMillis();
        Random rand = new Random(previous);
        Mat imageMat = new Mat();
        if (rand.nextGaussian() < 0.5)
        {
            Log.d("SPINNER","return color");
            imageMat = inputFrame.rgba();
        }
        else
        {
            Log.d("SPINNER","return greyscale");
            imageMat = inputFrame.gray();
        }
        Log.i("Random","One frame's run-time "+ (System.currentTimeMillis() - previous)+" ms");
        return imageMat;
    }
}
