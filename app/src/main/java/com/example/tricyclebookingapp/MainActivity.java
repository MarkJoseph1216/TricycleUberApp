package com.example.tricyclebookingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tricyclebookingapp.Activity.HomeActivity;
import com.example.tricyclebookingapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    Button btnRegisterAccount, btnLoginMain;

    TextInputEditText edtEmail, edtPassword, edtName, edtPhoneNumber;
    TextInputEditText edtEmailLogin, edtPasswordLogin;
    RelativeLayout relativeLayoutMain;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Dialog showDialogRegister, showDialogLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        showDialogRegister = new Dialog(MainActivity.this);
        showDialogLogin = new Dialog(MainActivity.this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRegister();
            }
        });
    }

    private void showDialogLogin(){
        showDialogLogin.setContentView(R.layout.layout_login);
        btnLoginMain = (Button) showDialogLogin.findViewById(R.id.btnLoginMain);
        edtEmailLogin = (TextInputEditText) showDialogLogin.findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = (TextInputEditText) showDialogLogin.findViewById(R.id.edtPasswordLogin);

        btnLoginMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmailLogin.getText().toString().equals("") || edtPasswordLogin.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Field's Are Empty", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog loadingDialog = new SpotsDialog(MainActivity.this);
                    loadingDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(edtEmailLogin.getText().toString(), edtPasswordLogin.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    loadingDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    showDialogLogin.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        showDialogLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showDialogLogin.show();
    }

    private void showDialogRegister(){
        showDialogRegister.setContentView(R.layout.layout_register);

        relativeLayoutMain = (RelativeLayout) showDialogRegister.findViewById(R.id.relativeLayoutMain);
        btnRegisterAccount = (Button) showDialogRegister.findViewById(R.id.btnRegisterAccount);

        edtEmail = (TextInputEditText) showDialogRegister.findViewById(R.id.edtEmail);
        edtPassword = (TextInputEditText) showDialogRegister.findViewById(R.id.edtPassword);
        edtName = (TextInputEditText) showDialogRegister.findViewById(R.id.edtName);
        edtPhoneNumber = (TextInputEditText) showDialogRegister.findViewById(R.id.edtPhone);

        btnRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().equals("") || edtPassword.getText().toString().equals("")
                        || edtName.getText().toString().equals("") || edtPhoneNumber.getText().toString().equals("")) {

                    Toast.makeText(MainActivity.this, "Field's Are Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog loadingDialog = new SpotsDialog(MainActivity.this);
                    loadingDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    User user = new User();
                                    user.setEmail(edtEmail.getText().toString());
                                    user.setName(edtName.getText().toString());
                                    user.setPhone(edtPhoneNumber.getText().toString());
                                    user.setPassword(edtPassword.getText().toString());

                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    loadingDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                                                    showDialogRegister.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(Exception e) {
                                                    loadingDialog.dismiss();
                                                    Snackbar.make(relativeLayoutMain, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    loadingDialog.dismiss();
                                    Snackbar.make(relativeLayoutMain, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        showDialogRegister.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showDialogRegister.show();
    }
}
