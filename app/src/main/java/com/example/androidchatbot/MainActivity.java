package com.example.androidchatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidchatbot.Activities.MessageActivity;
import com.example.androidchatbot.utils.MemoryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.example.androidchatbot.Activities.RegisterActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Reference Database Firebase
    private DatabaseReference mDatabase;
    // Button´s
    Button btnRegister, btnLogin;
    // EditText´s
    EditText edPhone, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edPhone     = findViewById(R.id.edPhone);
        edPassword  = findViewById(R.id.edPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this); // Crear una nueva instancia de ProgressDialog
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Cargando...");

                mDatabase = FirebaseDatabase.getInstance().getReference();
                onClickLogin(progressDialog);
            }
        });
    }

    public void onCreateRegister()
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void onClickLogin(ProgressDialog progressDialog)
    {
        final String phone    = edPhone.getText().toString();
        final String password = edPassword.getText().toString();

        progressDialog.show();

        if(password.isEmpty() || phone.isEmpty()){
            Toast.makeText(MainActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }else{
            mDatabase.child("users").addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    if (snapshot.hasChild(phone)) {
                        String storedPassword = snapshot.child(phone).child("password").getValue(String.class);
                        String storedUsername = snapshot.child(phone).child("username").getValue(String.class);
                        if (storedPassword.equals(password)) {
                            Toast.makeText(MainActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_LONG).show();

                            // Save mobile to memory
                            MemoryData.saveData(phone, MainActivity.this);

                            // Save username to memory
                            assert storedUsername != null;
                            MemoryData.saveUserName(storedUsername, MainActivity.this);

                            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("password", password);
                            intent.putExtra("username", storedUsername);
                            startActivity(intent);
                            finish();
                        } else {
                            // La clave no coincide
                            Toast.makeText(MainActivity.this, "Clave incorrecta", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // No existe ningun usuario con el numero de celular dado
                        Toast.makeText(MainActivity.this, "Usuario no existe en el sistema", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        }
    }
}