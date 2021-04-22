package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Integer artPhotos[] = {R.drawable.flower, R.drawable.black_trees, R.drawable.purple_mesh, R.drawable.skull};
    private int imageCounter = 0;

    ImageView artView;
    ImageButton leftArrow;
    ImageButton rightArrow;
    Button buyButton;
    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artView = (ImageView) findViewById(R.id.art_view);
        imageCounter = 0;

        setImage();

        leftArrow = (ImageButton) findViewById(R.id.left_arrow_button);
        rightArrow = (ImageButton) findViewById(R.id.right_arrow_button);
        buyButton = (Button) findViewById(R.id.buy_button);
        uploadButton = (Button) findViewById(R.id.upload_button);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageCounter > 0){
                    imageCounter--;
                    setImage();
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageCounter < artPhotos.length-1){
                    imageCounter++;
                    setImage();
                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BuyActivity.class);
                i.putExtra("id", artPhotos[imageCounter]);
                startActivity(i);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(i);
            }
        });
    }

    public void setImage(){
        if(artView != null){
            artView.setImageResource(artPhotos[imageCounter]);
        }
    }

}