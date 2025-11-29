package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Alumno;



@Dao
public interface AlumnoDAO {



    //----------------------------------- ALTAS ------------------------------

    @Insert
    public void agregarAlumno(Alumno alumno);

    //----------------------------------- BAJAS ------------------------------

    @Delete
    public void eliminarAlumnos(Alumno alumno);

    @Query("DELETE FROM alumno WHERE num_control=:nc")
    public void eliminarAlumnoPorNumControl(String nc);

    //----------------------------------- CAMBIOS ------------------------------

    @Update
    public void actualizarAlumno(Alumno alumno);

    @Query("UPDATE alumno SET nombre =:n WHERE num_control=:nc") //En set van todos lo campos a cambiar
    public void actualizarAlumnoPorNumControl(String n, String nc);


    //----------------------------------- CONSULTAS ------------------------------

    @Query("SELECT * FROM alumno")
    public List<Alumno> mostrarTodos();

    @Query("SELECT * FROM alumno WHERE nombre=:n")
    public List<Alumno> mostrarPorNombre(String n);

    @Query("SELECT * FROM alumno WHERE num_control LIKE :pattern")
    public List<Alumno> buscarPorNumControlSimilar(String pattern);

    @Query("SELECT * FROM alumno WHERE num_control LIKE :nc || '%'")
    public List<Alumno> mostrarPorNumControl(String nc);

    @Query("SELECT * FROM alumno WHERE num_control LIKE '%' || :filtro || '%'")
    public List<Alumno> buscarPorCoincidencia(String filtro);


}
