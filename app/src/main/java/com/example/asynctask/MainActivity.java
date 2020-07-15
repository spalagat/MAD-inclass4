package com.example.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ExecutorService threadpool;
    Handler handler;
    private static String THREAD_IMAGE_KEY="Image_Key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Display Image");
        final  ProgressBar progressBar=findViewById(R.id.image_progress);
        final ImageView imageView=findViewById(R.id.intial_image);
        Button asyncButton=findViewById(R.id.async_button);
        Button threadButton=findViewById(R.id.thread_button);
        final String Image_URL1="https://cdn.pixabay.com/photo/2017/12/31/06/16/boats-3051610_960_720.jpg";
        final String IMAGE_URL2 =  "https://cdn.pixabay.com/photo/2014/12/16/22/25/youth-570881_960_720.jpg";

        threadpool= Executors.newFixedThreadPool(5);
        handler =new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                Bitmap bitmap=msg.getData().getParcelable(THREAD_IMAGE_KEY);
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        });

         threadButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE);
                 imageView.setVisibility(View.INVISIBLE);
                 threadpool.execute(new setImageByThread(Image_URL1));
             }
         });


        asyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                new setImageByAsyncTask().execute(IMAGE_URL2);
            }
        });



    }
    Bitmap getImageBitmap(String... strings) {
        try {
            Log.d("check",""+strings);
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    class setImageByThread implements Runnable{


        private final String image_url;


        public setImageByThread(String image_url1) {
            this.image_url=image_url1;
        }


        @Override
        public void run() {

            Bitmap bitmap=getImageBitmap(image_url);
            Message msg=new Message();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.THREAD_IMAGE_KEY,bitmap);
            msg.setData(bundle);
            handler.sendMessage(msg);


        }
    }

    class setImageByAsyncTask extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... strings) {

            return getImageBitmap(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            ImageView myImageView;
            ProgressBar myProgress;
            myImageView =findViewById(R.id.intial_image);
            myProgress=findViewById(R.id.image_progress);
            myImageView.setImageBitmap(bitmap);
            myImageView.setVisibility(View.VISIBLE);
            myProgress.setVisibility(View.INVISIBLE);

    }
    }
}
