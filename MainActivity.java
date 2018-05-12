package com.example.i20035.loadimage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView i1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug())
        {
            Toast.makeText(this, "openCV loaded successfully", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "OpenCV failed", Toast.LENGTH_SHORT).show();
        }

        i1 = (ImageView)findViewById(R.id.imageView);

    }


    public void openGallary(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 101 && resultCode == RESULT_OK && data != null )
        {
            Uri imageUri = data.getData();
            String path = getpath(imageUri);
            loadImage(path);

            displayImage(sampledImg);

        }
    }

    private void displayImage(Mat mat)
    {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mat,bitmap);
        i1.setImageBitmap(bitmap);
    }

    Mat sampledImg;
    private void loadImage( String path )
    {
        Mat originalImage = Imgcodecs.imread(path); // Image will be BGR format
        Mat rgbImage = new Mat();
        sampledImg = new Mat();

        // convert to RGB image
        Imgproc.cvtColor(originalImage,rgbImage, Imgproc.COLOR_BGR2RGB);
        Display display = getWindowManager().getDefaultDisplay();

        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        int mobile_width = size.x;
        int mobile_height = size.y;



        double downSmapleRatio = calculateSubSampleSize( rgbImage, mobile_width, mobile_height );

        Imgproc.resize(rgbImage, sampledImg, new Size(), downSmapleRatio, downSmapleRatio, Imgproc.INTER_AREA );

        try
        {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            switch(orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    sampledImg = sampledImg.t();
                    Core.flip(sampledImg,sampledImg,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    sampledImg = sampledImg.t();
                    Core.flip(sampledImg,sampledImg,0);
                    break;


            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }



    }

    private double calculateSubSampleSize( Mat src, int mobile_width, int mobile_heigth )
    {
        final int width = src.width();
        final int height = src.height();
        double intSampleSize = 1;

        if( height > mobile_heigth || width > mobile_width )
        {
            final double heightRatio = (double)mobile_heigth/height;
            final double widthRatio = (double)mobile_width/width;

            intSampleSize = heightRatio < widthRatio ? height : width ;

        }

        return intSampleSize;
    }


    private String getpath( Uri uri )
    {
        if( uri == null )
        {
            return null;
        }
        else
        {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,projection,null,null,null);

            if( cursor != null )
            {
                int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                return cursor.getString(col_index);
            }
        }

        return uri.getPath();
    }
}
