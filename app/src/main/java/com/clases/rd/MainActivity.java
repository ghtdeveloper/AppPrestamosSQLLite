package com.clases.rd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.clases.rd.modelo.OpenHelper;
import com.clases.rd.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    //Comentario

                    //BD
    private OpenHelper dbHelper;
    private SQLiteDatabase db;
    private AppBarConfiguration mAppBarConfiguration;
    ArrayList<String> listClientesData;
    Intent intentMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//activity_main
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //BD
        dbHelper = new OpenHelper(this);
        db= dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //ListView

        Menu navMenu = navigationView.getMenu();
        View hView =  navigationView.getHeaderView(0);
        //Editext Nav Header

        TextView textViewRolusuario = hView.findViewById(R.id.textViewRolUsuarioNavHeader);
        textViewRolusuario.setText(getRolUsuario());
        TextView textViewCorreoUsuario = hView.findViewById(R.id.textViewCorreoUserNavHeader);
        textViewCorreoUsuario.setText(getCorreoUsario());

        if(getRolUsuario().equals("Cobrador"))
        {
            navMenu.findItem(R.id.nav_usuarios).setVisible(false);
            navMenu.findItem(R.id.nav_configuracion).setVisible(false);
        }



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_clientes, R.id.nav_prestamos, R.id.nav_pagos,
                R.id.nav_usuarios, R.id.nav_configuracion,R.id.nav_cerrarSesion)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this
                , R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController
                , mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navMenu.findItem(R.id.nav_cerrarSesion).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //mostrarMensaje();
                mostrarMensajeCerrarSesion();
                return false;
            }
        });

    }//Fin del metodo onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void mostrarLogin()
    {
        intentMainActivity = new Intent(this, LoginActivity.class);
        startActivity(intentMainActivity);
    }

    public String getCorreoUsario()
    {
        Intent intent = getIntent();
        return intent.getStringExtra("correoUser");
    }

    public String getRolUsuario()
    { Intent intent = getIntent();
        return intent.getStringExtra("rolUser");
    }

    public void mostrarMensajeCerrarSesion()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea salir de la aplicación?");
        builder.setTitle("Cerrar Sesion");
        builder.setPositiveButton(R.string.btnAceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Si el usuario presiona el boton de aceptar regresa al login
                mostrarLogin();
            }
        });
        builder.setNegativeButton(R.string.btnCancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Si cancela no hace nada se queda donde esta
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }//Fin del metodo mostrarMensajeCerrarSesion


    public void listClientes()
    {
        listClientesData.clear();
        Cursor cursor = db.rawQuery("Select c.nombre, c.apellidos from Clientes as c"
                , null);
        if(cursor.moveToFirst())
        {
            do {
                listClientesData.add(cursor.getString(0));
                listClientesData.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        cursor.close();
    }


}//Fin de la class Main Activity
