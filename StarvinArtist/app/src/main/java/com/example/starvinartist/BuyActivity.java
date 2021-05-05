package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuyActivity extends AppCompatActivity {

    ImageView buyArtView;
    TextView nameText;
    TextView priceText;
    TextView contactText;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        queue = Volley.newRequestQueue(this);

        buyArtView = (ImageView) findViewById(R.id.buy_art_view);
        nameText = (TextView) findViewById(R.id.buy_name_text);
        priceText = (TextView) findViewById(R.id.buy_price_text);
        contactText = (TextView) findViewById(R.id.buy_contact_text);

        getImage();
    }

    public void getImage(){
        Intent i = getIntent();
        int counter = i.getIntExtra("counter", 0);
        String tag = i.getStringExtra("tag");
        if(tag.isEmpty()){
            setImage(counter);
            getImageInfo(counter);
        } else {
            setTag(counter, tag);
            getTagInfo(counter, tag);
        }
    }

    public void setImage(int counter){
        String url = "https://starvinartist.herokuapp.com/photo/"+counter;
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d("Volley", "Success: Gotten image");
                        buyArtView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley error", error.toString());
                        buyArtView.setImageResource(R.drawable.skull);
                    }
                });
        queue.add(request);
    }

    public void setTag(int counter, String tag){
        String url = "https://starvinartist.herokuapp.com/tag/"+tag+"/"+counter;
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d("Volley", "Success: Gotten tag image");
                        buyArtView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley error", error.toString());
                        setImage(counter);
                    }
                });
        queue.add(request);
    }

    public void getImageInfo(int counter){
        String url = "https://starvinartist.herokuapp.com/photo/info/"+counter;
        Map<String, String> params = new HashMap<>();
        ServerRequest jsObjRequest = new ServerRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Volley success","get image info success: "+response.toString());
                try {
                    String name = response.getString("name");
                    String contact = response.getString("contact");
                    String price = response.getString("price");
                    nameText.setText(name);
                    contactText.setText("Contact: "+contact);
                    priceText.setText("Price: "+price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error","Get image info error "+error.toString());
            }
        });
        queue.add(jsObjRequest);
    }

    public void getTagInfo(int counter, String tag){
        String url = "https://starvinartist.herokuapp.com/tag/info/"+tag+"/"+counter;
        Map<String, String> params = new HashMap<>();
        ServerRequest jsObjRequest = new ServerRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Volley success","get image info success: "+response.toString());
                try {
                    String name = response.getString("name");
                    String contact = response.getString("contact");
                    String price = response.getString("price");
                    nameText.setText(name);
                    contactText.setText("Contact: "+contact);
                    priceText.setText("Price: "+price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error","Get image info error "+error.toString());
            }
        });
        queue.add(jsObjRequest);
    }

}