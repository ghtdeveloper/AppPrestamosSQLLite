package com.clases.rd.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    Esta clase esta destinada para la creacion de la base de datos
    aqui se crean las estructuras de las tablas
    by Edison Martinez 20-11-2019
 */
public class OpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "FPaymentt.db";
    private static final int DB_VERSION = 1;

    private static final String CLIENTES_TABLE_CREATE = "CREATE TABLE Clientes" +
            "(idCliente INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,apellidos TEXT" +
            ",sexo TEXT" + ",cedula TEXT,telefono TEXT,direccion TEXT)";

    private static final String PRESTAMOS_TABLE_CREATE = "CREATE TABLE Prestamos" +
            "(idPrestamo INTEGER PRIMARY KEY AUTOINCREMENT,monto REAL" +
            ",tasaInteres REAL" + ",plazaArmotizacion INTEGER,cuota REAL," +
            "totalInteres REAL,montoFinal REAL " +
            ",IdClienteFK INTEGER)";

    private static final String EMPRESA_TABLE_CREATE = "CREATE TABLE Empresa" +
            "(idEmpresa INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,Direccion TEXT" +
            ",rnc TEXT" + ",email TEXT,telefono TEXT" +
            ")";

    private static final String COBROS_TABLE_CREATE = "CREATE TABLE Cobros" +
            "(idCobro INTEGER PRIMARY KEY AUTOINCREMENT,importe_pago REAL,idPrestamoFK INTEGER," +
            "IdClienteFK INTEGER" + ",fecha_pago DATE"+
            ")";

    private static final String USUARIOS_TABLE_CREATE = "CREATE TABLE Usuarios" +
            "(idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,apellidos TEXT," +
            "telefono TEXT" + ",direccion TEXT"+",password TEXT,correo TEXT,rol TEXT,estado TEXT"+
            ")";

    private static final String CONFIGURACION_TABLE_CREATE = "CREATE TABLE Configuracion" +
            "(idConfiguracion INTEGER PRIMARY KEY AUTOINCREMENT,cantidad_usuario INTEGER" +
            ",password TEXT" +
            ")";

    public OpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CLIENTES_TABLE_CREATE);
        db.execSQL(PRESTAMOS_TABLE_CREATE);
        db.execSQL(EMPRESA_TABLE_CREATE);
        db.execSQL(COBROS_TABLE_CREATE);
        db.execSQL(USUARIOS_TABLE_CREATE);
        db.execSQL(CONFIGURACION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

}//Fin de la class OpenHelper
