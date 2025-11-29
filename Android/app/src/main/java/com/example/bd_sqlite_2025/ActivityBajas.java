package com.example.bd_sqlite_2025;


import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import db.EscuelaBD;
import entities.Alumno;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ActivityBajas extends Activity {

    EditText cajaNC, cajaNombre;
    Button btnBuscar, btnEliminar;
    RecyclerView recycler;
    AlumnoAdapter adapter;

    EscuelaBD bd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bajas);

        cajaNC = findViewById(R.id.caja_num_control_bajas);
        cajaNombre = findViewById(R.id.caja_nombre_bajas);
        btnBuscar = findViewById(R.id.btn_buscar_bajas);
        btnEliminar = findViewById(R.id.btn_eliminar_bajas);
        recycler = findViewById(R.id.recycler_bajas);

        // BD
        bd = EscuelaBD.getAppDatabase(getBaseContext());

        // Adapter
        adapter = new AlumnoAdapter(Collections.emptyList());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> buscarAlumno());
        btnEliminar.setOnClickListener(v -> eliminarAlumno());

        actualizarListaSimilar();

        cajaNC.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarListaSimilar();
            }
        });
    }

    // ----------------------- BUSCAR ------------------------
    private void buscarAlumno() {
        String nc = cajaNC.getText().toString().trim();

        if (nc.isEmpty()) {
            Toast.makeText(this, "Ingresa un número de control", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {

            // Para buscar exacto primero
            List<Alumno> lista = bd.alumnoDAO().buscarPorNumControlSimilar(nc);

            runOnUiThread(() -> {
                if (lista.isEmpty()) {
                    cajaNombre.setText("");
                    Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show();
                } else {

                    Alumno a = lista.get(0);

                    if (a.num_control.equals(nc)) {
                        cajaNombre.setText(a.nombre);
                    } else {
                        cajaNombre.setText("");
                        Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }).start();
    }

    // ----------------------- ELIMINAR ------------------------
    private void eliminarAlumno() {
        String nc = cajaNC.getText().toString().trim();

        if (nc.isEmpty()) {
            Toast.makeText(this, "Ingresa un número de control", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {

            bd.alumnoDAO().eliminarAlumnoPorNumControl(nc);

            runOnUiThread(() -> {
                Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show();
                cajaNC.setText("");
                cajaNombre.setText("");
                actualizarListaSimilar();
            });

        }).start();
    }

    // ----------------------- ACTUALIZAR LISTA ------------------------
    private void actualizarListaSimilar() {
        String pattern = cajaNC.getText().toString().trim() + "%";

        new Thread(() -> {

            List<Alumno> lista = bd.alumnoDAO().buscarPorNumControlSimilar(pattern);

            runOnUiThread(() -> adapter.updateData(lista));

        }).start();
    }
}


class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.ViewHolder> {

    private ArrayList<Alumno> localDataSet;

    // ------------------ ViewHolder ------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textviewAlumnos);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    // ------------------ Constructor ------------------
    public AlumnoAdapter(List<Alumno> dataset) {
        if (dataset == null)
            this.localDataSet = new ArrayList<>();
        else
            this.localDataSet = new ArrayList<>(dataset);
    }

    // ------------------ Crear vista ------------------
    @NonNull
    @Override
    public AlumnoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_recycleview, parent, false);

        return new ViewHolder(view);
    }

    // ------------------ Enlazar datos ------------------
    @Override
    public void onBindViewHolder(@NonNull AlumnoAdapter.ViewHolder holder, int position) {

        Alumno a = localDataSet.get(position);

        holder.getTextView().setText(a.num_control + " - " + a.nombre);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    // ------------------ ACTUALIZAR DATOS ------------------
    public void updateData(List<Alumno> nuevos) {
        localDataSet.clear();
        if (nuevos != null)
            localDataSet.addAll(nuevos);

        notifyDataSetChanged();
    }
}
