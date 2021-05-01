package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    //private Integer artPhotos[] = {R.drawable.flower, R.drawable.black_trees, R.drawable.purple_mesh, R.drawable.skull};
    private int imageCounter = 0;

    ImageView artView;
    ImageButton leftArrow;
    ImageButton rightArrow;
    Button buyButton;
    Button uploadButton;
    TextView nameGreet;
    Button profileButton;
    Button logoutButton;
    ImageButton searchButton;
    EditText searchBox;

    String tag;
    boolean signedIn;
    String name;
    String token;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signedIn = false;
        name = "";
        token = "";
        tag = "";

        artView = (ImageView) findViewById(R.id.art_view);
        imageCounter = 0;

        queue = Volley.newRequestQueue(this);
        setImage();

        leftArrow = (ImageButton) findViewById(R.id.left_arrow_button);
        rightArrow = (ImageButton) findViewById(R.id.right_arrow_button);
        buyButton = (Button) findViewById(R.id.buy_button);
        uploadButton = (Button) findViewById(R.id.upload_button);
        nameGreet = (TextView) findViewById(R.id.name_greet);
        profileButton = (Button) findViewById(R.id.profile_button);
        logoutButton = (Button) findViewById(R.id.logout_button);
        searchBox = (EditText) findViewById(R.id.search_box);
        searchButton = (ImageButton) findViewById(R.id.search_button);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageCounter > 0){
                    imageCounter--;
                    if(tag.isEmpty()){
                        setImage();
                    } else {
                        setTag();
                    }
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCounter++;
                if(tag.isEmpty()){
                    setImage();
                } else {
                    setTag();
                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BuyActivity.class);
                i.putExtra("tag", tag);
                i.putExtra("counter", imageCounter);
                startActivity(i);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signedIn){
                    Intent i = new Intent(MainActivity.this, UploadActivity.class);
                    i.putExtra("token", token);
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

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tag = searchBox.getText().toString();
                if(tag.isEmpty()){
                    setImage();
                } else {
                    setTag();
                }
            }
        });
        getLogin();
    }

    public void setTag(){
        String url = "http://10.0.0.227:3000/tag/"+tag+"/"+imageCounter;
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d("Volley", "Success: Gotten tag image");
                        artView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley error", error.toString());
                        setImage();
                    }
                });
        queue.add(request);
    }

    public void setImage(){
        String url = "http://10.0.0.227:3000/photo/"+imageCounter;
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d("Volley", "Success: Gotten image");
                        artView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley error", error.toString());
                        artView.setImageResource(R.drawable.skull);
                    }
                });
        queue.add(request);
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