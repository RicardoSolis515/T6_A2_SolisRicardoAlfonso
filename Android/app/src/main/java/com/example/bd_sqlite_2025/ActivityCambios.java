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

import java.util.ArrayList;
import java.util.List;

import controlers.AlumnoDAO;
import db.EscuelaBD;
import entities.Alumno;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ActivityCambios extends Activity {

    // UI
    EditText txtNumControl, txtNombre;
    Button btnBuscar, btnGuardar, btnCancelar;
    RecyclerView recycler;

    // Adapter
    CustomAdapterCambios adapter;
    ArrayList<Alumno> listaAlumnos = new ArrayList<>();

    // Base de datos
    AlumnoDAO alumnoDAO;

    // Alumno encontrado (para edición)
    Alumno alumnoSeleccionado = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambios);

        // Inicializar BD
        alumnoDAO = EscuelaBD.getAppDatabase(this).alumnoDAO();

        // Referencias UI
        txtNumControl = findViewById(R.id.txtNumControl);
        txtNombre = findViewById(R.id.txtNombre);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        recycler = findViewById(R.id.recyclerAlumnos);

        // Configurar RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapterCambios(listaAlumnos);
        recycler.setAdapter(adapter);

        // Eventos
        configurarEventos();
        cargarTodos();
    }

    // -----------------------------
    //   Cargar todos al iniciar
    // -----------------------------
    private void cargarTodos() {
        new Thread(() -> {
            List<Alumno> datos = alumnoDAO.mostrarTodos();
            listaAlumnos.clear();
            listaAlumnos.addAll(datos);

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    // -----------------------------
    //      Configurar eventos
    // -----------------------------
    private void configurarEventos() {

        // Buscar alumno exacto
        btnBuscar.setOnClickListener(v -> {
            String nc = txtNumControl.getText().toString().trim();

            if (nc.isEmpty()) {
                Toast.makeText(this, "Ingrese un número de control", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                List<Alumno> encontrados = alumnoDAO.mostrarPorNumControl(nc);

                runOnUiThread(() -> {
                    if (encontrados.size() > 0) {
                        alumnoSeleccionado = encontrados.get(0);

                        txtNombre.setText(alumnoSeleccionado.getNombre());
                        txtNumControl.setEnabled(false);  // Bloquear NC

                        Toast.makeText(this, "Alumno encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            if (alumnoSeleccionado == null) {
                Toast.makeText(this, "No hay alumno seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            String nuevoNombre = txtNombre.getText().toString().trim();

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "Ingrese un nombre válido", Toast.LENGTH_SHORT).show();
                return;
            }

            alumnoSeleccionado.setNombre(nuevoNombre);

            new Thread(() -> {
                alumnoDAO.actualizarAlumno(alumnoSeleccionado);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Alumno actualizado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarTodos();
                });
            }).start();
        });

        // Cancelar selección
        btnCancelar.setOnClickListener(v -> {
            limpiarCampos();
        });

        // Filtrado mientras se escribe
        txtNumControl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtNumControl.isEnabled()) {
                    filtrar(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // -----------------------------
    //          Filtrar LIKE
    // -----------------------------
    private void filtrar(String texto) {
        new Thread(() -> {
            List<Alumno> filtro = alumnoDAO.buscarPorCoincidencia(texto);

            listaAlumnos.clear();
            listaAlumnos.addAll(filtro);

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    // -----------------------------
    //      Limpiar y resetear
    // -----------------------------
    private void limpiarCampos() {
        alumnoSeleccionado = null;
        txtNumControl.setEnabled(true);
        txtNumControl.setText("");
        txtNombre.setText("");
        cargarTodos();
    }
}


class CustomAdapterCambios extends RecyclerView.Adapter<CustomAdapterCambios.ViewHolder> {

    private ArrayList<Alumno> lista;

    public CustomAdapterCambios(ArrayList<Alumno> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.textviewAlumnos);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_recycleview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alumno a = lista.get(position);
        holder.txtItem.setText(a.num_control + " - " + a.nombre);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
