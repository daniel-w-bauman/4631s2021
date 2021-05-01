package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupButton = (Button) findViewById(R.id.signup_button);
        nameInput = (EditText) findViewById(R.id.signup_name_input);
        emailInput = (EditText) findViewById(R.id.signup_email_input);
        passwordInput = (EditText) findViewById(R.id.signup_password_input);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void makeToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public void signup(){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.0.227:3000/createUser";
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        ServerRequest jsObjRequest = new ServerRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Volley success","Signup success: "+response.toString());
                nameInput.getText().clear();
                emailInput.getText().clear();
                passwordInput.getText().clear();
                try {
                    if(response.getString("status").equals("0")){
                        makeToast(response.getString("result"));
                    } else {
                        makeToast(response.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error","Sign up error "+error.getMessage().toString());
                makeToast(error.getMessage());
            }
        });
        queue.add(jsObjRequest);
    }
}