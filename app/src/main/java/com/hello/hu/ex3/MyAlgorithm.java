package com.hello.hu.ex3;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [Class Overview]
 * This class intends to solve some basic graphic operations in my own designed algorithms.
 * myEdgy() -  Do
 *              1) Laplacian transformation;
 *              2) Threshold;
 *              3) Mark all the edges with red on the color mat and return.
 *
 * myBestLines() - Do
 *              1) Laplacian transformation;
 *              2) Threshold;
 *              3) Hough line transformation and save all the points within +/- 30 degrees of vertical in pairs in a list.
 *              4) Draw the longest 10 lines saved in the list on the color mat and return.
 * The basic idea of laplacian transformation can go through http://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/laplace_operator/laplace_operator.html
 * @author Chengzhi Hu
 * Compiler used: Android Studio 2.3.1
 * Language used: Java SE 8
 * min Android SDK version: API level 21 (Android 5.0)
 * target Android SDK version: API level 25 (Android 7.1)
 * OpenCV version: 3.1.0
 */

public class MyAlgorithm {

    /**
     * A 3*3 laplacian kernel array.
     */
    private static final int[][] KERNELofLAPLACIAN = {{0,1,0},{1,-4,1},{0,1,0}};

    /**
     * Tan 30 degree. sqrt(3)/3. Approximately equals to 0.577.
     * This value will be used in drawHoughLines() to judge if the hough lines are within +/- 30 degrees of vertical.
     */
    private static final double TAN30degree = 0.577;


    /**
     * Use list to hold all the lines within +/- 30 degrees of vertical.
     * And then sort the list, and output the top 10 lines.
     */
    private List<LineInformation> lineInfoList;


    /**
     * Constructor to initialize the list to an array list.
     * The array list can be changed to any kinds of list.
     */
    public MyAlgorithm()
    {
        lineInfoList = new ArrayList<LineInformation>();
    }


    /**
     * Do
     *   1) Laplacian transformation;
     *   2) Threshold;
     *   3) Mark all the edges with red on the color mat and return.
     * This algorithm can be improved by seeing the instruction here:
     * http://algebra.sci.csueastbay.edu/~grewe/CS6825/Mat/OpenCV/speed/speed.html
     * Already finished in myEdgy2().
     * @param inputFrame frame captured be onCameraFrame
     * @param thres_value 0-255. May can write another algorithm to calculate the threshold value dynamically.
     * @return the post processed result.
     */
    public Mat myEdgy(CameraBridgeViewBase.CvCameraViewFrame inputFrame,int thres_value){
        long current = System.currentTimeMillis();
        Mat result = inputFrame.rgba();
        Mat gray = new Mat();
        Imgproc.cvtColor(result,gray,Imgproc.COLOR_RGBA2GRAY);
        for (int r = 1;r < gray.rows() - 1;r++){
            for (int c = 1; c < gray.cols() - 1;c++){
                redBorder(result, threshold(laplacian(gray,r,c), thres_value), r,c);
            }
        }
        Log.i("My Edgy Execute time",String.valueOf(System.currentTimeMillis() - current));
        return result;
    }

    /**
     * DOG - discrete approximation of LOG (Laplacian of Gaussian)
     * DOG represented by class array variable kenerlOfLaplacian
     *
     * @param grayMat entire image
     * @param row location where want to apply DOG
     * @param col location where want to apply DOG
     * @return value of DOG applied at location row,col
     */
    private double laplacian(Mat grayMat,int row, int col)
    {
        double grayTotal = 0.0;
        int half_kernel_size =  KERNELofLAPLACIAN.length/2;
        for (int x = -half_kernel_size; x <= half_kernel_size;x++)
            for (int y = -half_kernel_size;y <= half_kernel_size;y++)
            {
                grayTotal += grayMat.get(row + y,col + x)[0] * KERNELofLAPLACIAN[y + half_kernel_size ][x + half_kernel_size];
            }
        return grayTotal;
    }


    /**
     * Change gray mat to binary . Can only operate with gray mat.
     * Call this method in iterating through pixels.
     * @param gray the gray mat's value in specified position.
     * @param thres_value 0-255. May can write another algorithm to calculate the threshold value dynamically.
     * @return The result can only be either 0 or 255.
     */
    private int threshold(double gray, int thres_value)
    {
        if (gray < thres_value)
            return 0;
        return 255;
    }


    /**
     * Turn the specified point to red if the treshold value = 255.
     * Can only call this method when it's a binary mat already.
     * @param dst The destination mat.
     * @param thresValue Current points threshold value.
     * @param row position on mat.
     * @param col position on mat.
     */
    private void redBorder(Mat dst,int thresValue,int row, int col)
    {
        if (thresValue != 255)
            return;
        double rgbaValue[] = new double[4];
        rgbaValue[0] = 255;
        dst.put(row,col,rgbaValue);
    }


    /**
     *Do
     *  1) Laplacian transformation;
     *  2) Threshold;
     *  3) Hough line transformation and save all the points within +/- 30 degrees of vertical in pairs in a list.
     *  4) Draw the longest 10 lines saved in the list on the color mat and return
     * This algorithm can be improved by seeing the instruction here:
     * http://algebra.sci.csueastbay.edu/~grewe/CS6825/Mat/OpenCV/speed/speed.html
     * Already finished in myBestLines2().
     * @param inputFrame frame captured be onCameraFrame
     * @param thres_value 0-255. May can write another algorithm to calculate the threshold value dynamically.
     * @return the post processed result.
     */
    public Mat myBestLines(CameraBridgeViewBase.CvCameraViewFrame inputFrame,int thres_value)
    {
        long current = System.currentTimeMillis();
        Mat result = inputFrame.rgba();
        Mat gray = new Mat();
        Mat laplacianMat = new Mat();
        laplacianMat.create(result.size(),CvType.CV_8U);
        Imgproc.cvtColor(result,gray,Imgproc.COLOR_RGBA2GRAY);

        //Perform laplacian,then store in laplacianMat.
        for (int i = 1;i < gray.rows() - 1;i++) {
            for (int j = 1; j < gray.cols() - 1; j++) {
                laplacianMat.put(i,j,threshold(laplacian(gray, i, j),thres_value));
            }
        }

        drawHoughLines(laplacianMat,result);
        drawBest10Lines(result);
        Log.i("My Best Lines Exec.time",String.valueOf(System.currentTimeMillis() - current));
        return result;
    }


    /**
     * Detect and draw blue hough lines and save the lines within +/- 30 degrees of vertical into a list.
     * @param src Input mat. Must be a binary mat.
     * @param dst Output mat. The hough lines will be drawn in here.
     */
    private void drawHoughLines(Mat src,Mat dst)
    {
        int threshold_line = Math.min(src.rows(),src.cols()) / 4;
        Mat houghLines = new Mat();
        Imgproc.HoughLines(src,houghLines,1,Math.PI / 180,threshold_line);

        for (int i = 0; i < houghLines.rows();i++)
        {
                double[] points = houghLines.get(i,0);
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

                //Calculate slope, if -30 <= degrees <= 30, put into list.
                double slope = (x2 - x1) / (y2 - y1);
                if (Math.abs(slope) <= TAN30degree)
                {
                    LineInformation newInfo = new LineInformation(pt1,pt2);
                    lineInfoList.add(newInfo);
                }
                Imgproc.line(dst,pt1,pt2,new Scalar(0,0,255),1);
        }
    }

    /**
     * Sort the list in descending first, then draw in red, draw at most 10 lines.
     * @param dst Output mat. The red lines will be drawn there.
     */
    private void drawBest10Lines(Mat dst)
    {
        Collections.sort(lineInfoList);
        int maxDrawnLine = Math.min(10,lineInfoList.size());
        for (int i = 0; i < maxDrawnLine;i++)
        {
            Imgproc.line(dst,lineInfoList.get(i).getPt1(),lineInfoList.get(i).getPt2(),new Scalar(255),1);
        }
        lineInfoList.clear();
    }


    /**
     * Implement in Java calling O(1) JNI calls rather than calling a lot of(O(width * height)) JNI calls
     * Do
     *   1) Laplacian transformation;
     *   2) Threshold;
     *   3) Mark all the edges with red on the color mat and return.

     * @param inputFrame
     * @param thres_value
     * @return
     */
    public Mat myEdgy2(CameraBridgeViewBase.CvCameraViewFrame inputFrame,int thres_value){
        long current = System.currentTimeMillis();
        Mat result = inputFrame.rgba();
        Mat gray = new Mat();

        Imgproc.cvtColor(result,gray,Imgproc.COLOR_RGBA2GRAY);
        gray.convertTo(gray,CvType.CV_16UC1);
        result.convertTo(result,CvType.CV_16UC1);
        short grayArray[] = new short[(int)gray.total() * gray.channels()];
        short colorArray[] = new short[(int)result.total() * result.channels()];
        gray.get(0,0,grayArray);
        result.get(0,0,colorArray);
        int width = gray.width();
        int height = gray.height();
        int channels = result.channels();
        for (int r = 1;r < height - 1;r++){
            for (int c = 1; c < width - 1;c++){
              if (laplacian2(grayArray,r,c,width,height) > thres_value)
              {
                  colorArray[r * (width * channels) + c * channels] = 255;//R
                  colorArray[r * (width * channels) + c * channels + 1] = 0;//G
                  colorArray[r * (width * channels) + c * channels + 2] = 0;//B
                  colorArray[r * (width * channels) + c * channels + 3] = 0;//A
              }
            }
        }
        Log.i("My Edgy Execute time",String.valueOf(System.currentTimeMillis() - current));
        result.put(0,0,colorArray);
        result.convertTo(result,CvType.CV_8U);
        return result;
    }

    private double laplacian2(short[] grayArray,int row, int col,int width,int height)
    {
        double grayTotal = 0.0;
        int half_kernel_size =  KERNELofLAPLACIAN.length/2;
        for (int x = -half_kernel_size; x <= half_kernel_size;x++)
            for (int y = -half_kernel_size;y <= half_kernel_size;y++)
            {
                grayTotal += grayArray[(row + y) * width + col + x] * KERNELofLAPLACIAN[y + half_kernel_size ][x + half_kernel_size];
                //grayTotal += grayMat.get(row + y,col + x)[0]
            }
        return grayTotal;
    }


    /**
     *Do
     *  1) Laplacian transformation;
     *  2) Threshold;
     *  3) Hough line transformation and save all the points within +/- 30 degrees of vertical in pairs in a list.
     *  4) Draw the longest 10 lines saved in the list on the color mat and return
     * Implement in Java calling O(1) JNI calls rather than calling a lot of(O(width * height)) JNI calls
     * @param thres_value 0-255. May can write another algorithm to calculate the threshold value dynamically.
     * @return the post processed result.
     */
    public Mat myBestLines2(CameraBridgeViewBase.CvCameraViewFrame inputFrame,int thres_value)
    {
        long current = System.currentTimeMillis();

        Mat result = inputFrame.rgba();
        Mat gray = new Mat();
        Mat laplacianMat = new Mat();

        Imgproc.cvtColor(result,gray,Imgproc.COLOR_RGBA2GRAY);
        gray.convertTo(gray,CvType.CV_16UC1);
        int width = gray.width();
        int height = gray.height();
        laplacianMat.create(height,width,CvType.CV_16UC1);
        short grayArray[] = new short[(int)gray.total() * gray.channels()];
        short laplacianArray[] = new short[(int)gray.total() * gray.channels()];
        gray.get(0,0,grayArray);

        for (int r = 1;r < height - 1;r++){
            for (int c = 1; c < width - 1;c++){
                laplacianArray[r *width+c] =laplacian2(grayArray,r,c,width,height) > thres_value ? (short) 255 : 0;
            }
        }
        laplacianMat.put(0,0,laplacianArray);
        laplacianMat.convertTo(laplacianMat,CvType.CV_8U);
        drawHoughLines(laplacianMat,result);
        drawBest10Lines(result);
        Log.i("My Best Lines Exec.time",String.valueOf(System.currentTimeMillis() - current));
        return result;
    }
}
