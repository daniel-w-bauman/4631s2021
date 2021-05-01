package com.example.starvinartist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView responseText;
    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    Button signupPageButton;
    String name;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        responseText = (TextView) findViewById(R.id.login_name_response);
        emailInput = (EditText) findViewById(R.id.login_email_input);
        passwordInput = (EditText) findViewById(R.id.login_password_input);
        loginButton = (Button) findViewById(R.id.login_button);

        name = "";
        token = "";

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        signupPageButton = (Button) findViewById(R.id.signup_page_button);
        signupPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

    }

    public void makeToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public void login(){
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.0.227:3000/login";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        ServerRequest jsObjRequest = new ServerRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Volley success","Login success: "+response.toString());
                emailInput.getText().clear();
                passwordInput.getText().clear();
                try {
                    if(response.getString("status").equals("0")){
                        makeToast("success");
                        name = response.getJSONObject("user").getString("name");
                        token = response.getJSONObject("user").getString("token");
                        responseText.setText("Hello, "+name);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("name", name);
                        i.putExtra("token", token);
                        startActivity(i);
                    } else {
                        responseText.setText("Error: "+response.getString("error"));
                        makeToast(response.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error","Sign up error "+error.toString());
                makeToast(error.getMessage());
            }
        });
        queue.add(jsObjRequest);
    }
}