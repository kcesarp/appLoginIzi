package com.example.apploginizi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Clase Administrador que extiende de SQLiteOpenHelper para gestionar la base de datos
public class Administrador extends SQLiteOpenHelper {

    // Nombre de la base de datos y versión
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas de la base de datos
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_VERIFICATION_CODE = "verification_code";

    // Constructor de la clase que llama al constructor de la superclase SQLiteOpenHelper
    public Administrador(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método onCreate que se ejecuta al crear la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_VERIFICATION_CODE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    // Método onUpgrade que se ejecuta cuando se necesita actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Método para agregar un nuevo usuario a la base de datos
    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Método para verificar si un usuario existe con un email y contraseña específicos
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Método para verificar si un usuario existe solo con un email específico
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL},
                COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Método para guardar un código de verificación para un usuario específico
    public void guardarCodigoVerificacion(String email, String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VERIFICATION_CODE, codigo);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }

    // Método para verificar si un código de verificación es correcto para un usuario específico
    public boolean verificarCodigo(String email, String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL},
                COLUMN_EMAIL + "=? AND " + COLUMN_VERIFICATION_CODE + "=?",
                new String[]{email, codigo}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Método para actualizar la contraseña de un usuario y borrar el código de verificación
    public void actualizarContrasena(String email, String nuevaContrasena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, nuevaContrasena);
        values.putNull(COLUMN_VERIFICATION_CODE);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }
}
