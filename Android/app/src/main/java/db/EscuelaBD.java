package db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import controlers.AlumnoDAO;
import entities.Alumno;

@Database(entities = {Alumno.class}, version = 1)
public abstract class EscuelaBD extends RoomDatabase {

    private static EscuelaBD INSTANCE;

    public abstract AlumnoDAO alumnoDAO();

    public static EscuelaBD getAppDatabase(Context context){

        if(INSTANCE==null){

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EscuelaBD.class, "BD_Escuela").build();

        }

        return INSTANCE;

    }

    public static void desstroyInstance(){INSTANCE=null;}

}
