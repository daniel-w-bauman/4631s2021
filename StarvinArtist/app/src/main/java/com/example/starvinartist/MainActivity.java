package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Integer artPhotos[] = {R.drawable.flower, R.drawable.black_trees, R.drawable.purple_mesh, R.drawable.skull};
    private int imageCounter = 0;

    ImageView artView;
    ImageButton leftArrow;
    ImageButton rightArrow;
    Button buyButton;
    Button uploadButton;
    TextView nameGreet;
    Button profileButton;
    Button logoutButton;

    boolean signedIn;
    String name;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signedIn = false;
        name = "";
        token = "";

        artView = (ImageView) findViewById(R.id.art_view);
        imageCounter = 0;

        setImage();

        leftArrow = (ImageButton) findViewById(R.id.left_arrow_button);
        rightArrow = (ImageButton) findViewById(R.id.right_arrow_button);
        buyButton = (Button) findViewById(R.id.buy_button);
        uploadButton = (Button) findViewById(R.id.upload_button);
        nameGreet = (TextView) findViewById(R.id.name_greet);
        profileButton = (Button) findViewById(R.id.profile_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

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
                if(signedIn){
                    Intent i = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "Clicked on profile button");
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "";
                token = "";
                signedIn = false;
                nameGreet.setText("");
                profileButton.setVisibility(View.INVISIBLE);
                logoutButton.setVisibility(View.INVISIBLE);
            }
        });

        getLogin();
    }

    public void setImage(){
        if(artView != null){
            artView.setImageResource(artPhotos[imageCounter]);
        }
    }

    public void getLogin() {
        Intent i = getIntent();
        if(name.isEmpty() || token.isEmpty()){
            if(i != null){
                name = i.getStringExtra("name");
                token = i.getStringExtra("token");
                signedIn = true;
                if(name == null){
                    name = "";
                    signedIn = false;
                }
                if(token == null){
                    token = "";
                    signedIn = false;
                }
            }
            if(signedIn){
                nameGreet.setText("Hello, "+name);
                profileButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getLogin();
    }
}