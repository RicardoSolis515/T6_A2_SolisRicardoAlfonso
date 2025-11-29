package com.example.bd_sqlite_2025;


import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import db.EscuelaBD;
import entities.Alumno;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;




import java.util.List;



public class ActivityConsultas extends Activity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ArrayList<Alumno> listaAlumnos = new ArrayList<>();

    private EditText cajaFiltro;

    EscuelaBD bd;   // ← SOLO una declaración, dentro del onCreate se inicializa

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        // ==========================
        // Inicializar BD correctamente
        // ==========================
        bd = EscuelaBD.getAppDatabase(this);

        // ==========================
        // Inicializar UI
        // ==========================
        cajaFiltro = findViewById(R.id.cajaBuscar);
        recyclerView = findViewById(R.id.recycleAlumnos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter inicial
        adapter = new CustomAdapter(listaAlumnos);
        recyclerView.setAdapter(adapter);

        // Cargar datos al iniciar
        cargarTodos();

        // Evento de filtrado
        configurarFiltro();
    }

    private void cargarTodos() {
        new Thread(() -> {
            List<Alumno> datos = bd.alumnoDAO().mostrarTodos();
            listaAlumnos.clear();
            listaAlumnos.addAll(datos);

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    private void configurarFiltro() {

        cajaFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String filtro = s.toString().trim();

                new Thread(() -> {
                    List<Alumno> filtrados;

                    if (filtro.isEmpty()) {
                        filtrados = bd.alumnoDAO().mostrarTodos();
                    } else {
                        filtrados = bd.alumnoDAO().buscarPorCoincidencia(filtro);
                    }

                    listaAlumnos.clear();
                    listaAlumnos.addAll(filtrados);

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();

                        if (filtrados.isEmpty() && !filtro.isEmpty()) {
                            Toast.makeText(ActivityConsultas.this, "Sin coincidencias", Toast.LENGTH_SHORT).show();
                        }
                    });

                }).start();

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}


class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Alumno> lista;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textviewAlumnos);
        }
    }

    public CustomAdapter(ArrayList<Alumno> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alumno a = lista.get(position);
        holder.textView.setText(a.num_control + " - " + a.nombre);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}



