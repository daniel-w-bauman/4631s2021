package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class BuyActivity extends AppCompatActivity {

    ImageView buyArtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        buyArtView = (ImageView) findViewById(R.id.buy_art_view);

        setImage();
    }

    public void setImage(){
        Intent i = getIntent();
        Integer id = i.getIntExtra("id", 0);
        if((buyArtView != null) && (id != 0)){
            buyArtView.setImageResource(id);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImage();
    }

}