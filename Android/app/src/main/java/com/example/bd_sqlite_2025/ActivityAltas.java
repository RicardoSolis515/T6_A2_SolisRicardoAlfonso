package com.example.bd_sqlite_2025;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import db.EscuelaBD;
import entities.Alumno;

public class ActivityAltas extends Activity {

    EditText cajaNumControl, cajaNombre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altas);

        cajaNumControl = findViewById(R.id.caja_num_control);
        cajaNombre = findViewById(R.id.caja_nombre);
    }

    public void agregarAlumno(View v){

        String nc = cajaNumControl.getText().toString();
        String n = cajaNombre.getText().toString();

        Alumno alumno = new Alumno(nc, n);

        EscuelaBD bd = EscuelaBD.getAppDatabase(getBaseContext());

        new Thread(new Runnable() {
            @Override
            public void run() {

                bd.alumnoDAO().agregarAlumno(alumno);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Alumno agregado", Toast.LENGTH_SHORT).show();

                        // 🔥 Limpiar campos
                        cajaNumControl.setText("");
                        cajaNombre.setText("");
                        cajaNumControl.requestFocus();
                    }
                });

            }
        }).start();
    }
    public void restablecerCampos(View v){
        cajaNumControl.setText("");
        cajaNombre.setText("");
        cajaNumControl.requestFocus();
    }




}

