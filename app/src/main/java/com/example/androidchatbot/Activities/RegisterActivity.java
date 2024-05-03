package com.example.androidchatbot.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidchatbot.MainActivity;
import com.example.androidchatbot.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    // Reference Database Firebase
    private DatabaseReference mDatabase;
    // Button´s
    Button btnNewRegister;
    // EditText´s
    EditText edRegisterBirthdate, edRegisterUserName, edRegisterEmail, edRegisterPassword, edRegisterPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edRegisterUserName  = findViewById(R.id.edRegisterUserName);
        edRegisterEmail     = findViewById(R.id.edRegisterEmail);
        edRegisterPhone     = findViewById(R.id.edRegisterPhone);
        edRegisterPassword  = findViewById(R.id.edRegisterPassword);
        edRegisterBirthdate = findViewById(R.id.edRegisterBirthdate);

        btnNewRegister      = findViewById(R.id.btnNewRegister);

        edRegisterBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnNewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                onClickNewRegister();
            }
        });
    }
    //Agregar seleccion de fecha
    private void showDatePickerDialog(){
        Calendar calendario     = Calendar.getInstance();
        int year                = calendario.get(Calendar.YEAR);
        int month               = calendario.get(Calendar.MONTH);
        int dayOfMonth          = calendario.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edRegisterBirthdate.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                edRegisterBirthdate.setError(null);
            }
        },year, month, dayOfMonth);
        dialog.show();
    }
    // Registrar usuario
    private void onClickNewRegister()
    {
        final String username  = edRegisterUserName.getText().toString();
        final String email     = edRegisterEmail.getText().toString();
        final String phone     = edRegisterPhone.getText().toString();
        final String password  = edRegisterPassword.getText().toString();
        final String birthdate = edRegisterBirthdate.getText().toString();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || birthdate.isEmpty() || phone.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
        }else{
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("users").hasChild(phone)){
                        Toast.makeText(RegisterActivity.this, "El usuario se encuentra registrado", Toast.LENGTH_LONG).show();
                    }else{
                        mDatabase.child("users").child(phone).child("email").setValue(email);
                        mDatabase.child("users").child(phone).child("username").setValue(username);
                        mDatabase.child("users").child(phone).child("password").setValue(password);
                        mDatabase.child("users").child(phone).child("profile_picture").setValue("");
                        mDatabase.child("users").child(phone).child("birthdate").setValue(new Date(birthdate));

                        Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}