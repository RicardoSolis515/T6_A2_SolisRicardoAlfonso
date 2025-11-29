package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alumno {


    @PrimaryKey
    @NonNull
    public String num_control;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    public Alumno(@NonNull String num_control, @NonNull String nombre) {
        this.num_control = num_control;
        this.nombre = nombre;
    }

    @NonNull
    public String getNum_control() {
        return num_control;
    }

    public void setNum_control(@NonNull String num_control) {
        this.num_control = num_control;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "num_control='" + num_control + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }


}
