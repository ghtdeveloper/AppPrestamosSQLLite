package com.clases.rd.ui.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.clases.rd.MainActivity;
import com.clases.rd.R;
import com.clases.rd.modelo.OpenHelper;
import com.clases.rd.ui.usuarios.UsuarioViewModel;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity
{
    Intent intentActivtiyLogin;
    private EditText txtUsuario; //Telefono sin guion
    private EditText txtPassword;//Password usuario
    //Base de datos
    private  SQLiteDatabase db;
    private UsuarioViewModel objUsuarioViewModel;

    public static final String REGEX_NUM_CELULAR = "^[0-10]{10}$";

     EditText txtNombreUsuario;
     EditText txtApellidoUsuario;
     EditText txtTelefonoUsuario;
     EditText txtpasswordUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        //Button btnInicarSesion = (Button) findViewById(R.id.boton_iniciar_sesion);
        OpenHelper dbHelper = new OpenHelper(this);
        db = dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //Se realiza el cast de la  vistas con el del layout
        txtUsuario = findViewById(R.id.txtTelefonoUsuarioAcc);
        txtPassword = findViewById(R.id.txtPasswordUsuarioAcc);
        txtUsuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10) {
        }}); //Validar que  este campo solo admita  10 digitos para el campo de telefono
        txtPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12) {
        }}); //Validar que  este campo solo admita  12 digitos para el campo de telefono
        //*****************************************************


    }//Fin del metodo onCreate

    /*
        Se crea el dialogo para la accion de recuperar
        el password en la pantalla de login
     */
    public void mostrarRecupararPassword(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.textDialogoRecuperarPassword);
        final EditText input = new EditText(this);
        input.setHint(R.string.correo);
        //input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setHintTextColor(Color.BLACK);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Se toma el valor del correo que el usuario ingrese
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No se realiza ninguna accion
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setView(input);
        dialog.show();
    }//Fin del

    @SuppressLint("InflateParams")
    public void registrarUsuarioFast(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
       // builder.setView(inflater.inflate(R.layout.dialog_usuarios_fast,null));
        View dialogView = inflater.inflate(R.layout.dialog_usuarios_fast,null);
        builder.setView(dialogView);
        //String capturarMensaje = txtNombreUsuario.getText().toString();
        //txtNombreUsuario = new EditText(this);
        final EditText txtNombreUsuario = dialogView.findViewById(R.id.txtUserNameFast);
        final EditText txtTelfonoUsuario = dialogView.findViewById(R.id.txtTelefonoUsuarioFast);
        final EditText txtApellidoUsuario = dialogView.findViewById(R.id.txtlastNameFast);
        final EditText txtPasswordUsuario = dialogView.findViewById(R.id.txtPasswordUsuarioFast);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(db != null)
                {
                    ContentValues cv = new ContentValues();
                    cv.put("nombre", String.valueOf(txtNombreUsuario.getText()));
                    cv.put("apellidos",String.valueOf(txtApellidoUsuario.getText()));
                    cv.put("telefono",String.valueOf(txtTelfonoUsuario.getText()));
                    cv.put("direccion","PA");
                    cv.put("correo","PA");
                    cv.put("rol","PA");
                    cv.put("estado","Activo");
                    cv.put("password",String.valueOf(txtPasswordUsuario.getText()));
                    db.insert("Usuarios",null,cv);
                    mensajeAddUserSucces();
                    try
                    {
                        Thread.sleep(1000);
                        txtNombreUsuario.setText(" ");
                        txtApellidoUsuario.setText(" ");
                        txtTelfonoUsuario.setText(" ");
                        txtPasswordUsuario.setText(" ");
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }//Fin del if
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No se realiza ninguna accion
            }
        });
        AlertDialog dialog =   builder.create();
        dialog.getLayoutInflater();
        dialog.show();

    }//Fin del mostrarRecupararPassword

    public void saludar(String personacool)
    {
        Toast.makeText(this,"Hola"+ personacool,
                Toast.LENGTH_LONG).show();
    }


    //metodo para controlar el acceso a la app y verificar el rol
    public void validarAcceso(View view)
    {


        String telefonoObtenido = "";
        String passwordObtenida= "";
        String rolObtenido="";
        String correoUsuarioObtenido="";
        String estadoObtenido="";

        Pattern patron = Pattern.compile(REGEX_NUM_CELULAR);
        String telefono = txtUsuario.getText().toString().trim();

        //String password = txtPassword.getText().toString();

        //Especifico que campos me interesan buscar en la BD
        String[] campos= new String[] {"telefono","password,rol,correo,estado"};
        //Especifico por que campo voy a filtrar
        String[] arg = new String[]{telefono.trim()};

        if(txtUsuario.getText().toString().isEmpty()
                && txtpasswordUsuario.getText().toString().isEmpty())
        {
            Toast.makeText(this,"No puede dejar campos vacios"
                    ,Toast.LENGTH_SHORT).show();

        }

        if(patron.matcher(telefono).matches())
        {
            Toast.makeText(this,"Validacion correcta",
                    Toast.LENGTH_SHORT).show();
        }


        else {
            Cursor fila = db.query("Usuarios",campos,"telefono=?",
                    arg,null,null,null);
            if(fila.moveToFirst())
            {
                do {
                    telefonoObtenido = fila.getString(0);
                    passwordObtenida = fila.getString(1);
                    rolObtenido = fila.getString(2);
                    correoUsuarioObtenido = fila.getString(3);
                    estadoObtenido = fila.getString(4);
                }while (fila.moveToNext());
            }
            if(estadoObtenido.equals("Inactivo"))
            {
                Toast.makeText(this,"NO TIENE ACCESO AL SISTEMA CONTACTE ADMIN SITEMA",
                        Toast.LENGTH_LONG).show();
            }
            else {
                if(txtUsuario.getText().toString().equals(telefonoObtenido) &&
                        txtPassword.getText().toString().equals(passwordObtenida))
                {
                    intentActivtiyLogin = new Intent(this,MainActivity.class);
                    intentActivtiyLogin.putExtra("rolUser",rolObtenido);
                    intentActivtiyLogin.putExtra("correoUser",correoUsuarioObtenido);
                    startActivity(intentActivtiyLogin);
                }
                else
                {
                    Toast.makeText(this, "VERIFIQUE SU USUARIO/ PASSWORD",
                            Toast.LENGTH_SHORT).show();
                }
            }
        fila.close();
        }//Fin del else parent
    }//Fin del metdo validarAcceso


    /*
    public void mostrarPantallaPrincipal(View View)
    {
        intentActivtiyLogin = new Intent(this,MainActivity.class);
        startActivity(intentActivtiyLogin);
    }

     */

    /*
    private void agregarUsuario()
    {

            if(db != null)
            {
                ContentValues cv = new ContentValues();
                cv.put("nombre", String.valueOf(txtNombreUsuario.getText()));
                cv.put("apellidos",String.valueOf(txtApellidoUsuario.getText()));
                cv.put("telefono",String.valueOf(txtTelefonoUsuario.getText()));
                cv.put("direccion","PA");
                cv.put("correo","PA");
                cv.put("rol","PA");
                cv.put("password",String.valueOf(txtpasswordUsuario.getText()));
                db.insert("Usuarios",null,cv);
                Toast.makeText(this, "Usuario Agregado"
                        , Toast.LENGTH_SHORT).show();
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }//Fin del if

        }//Fin del metodo agregarUsuario

     */

    public void mensajeAddUserSucces()
    {
        Toast.makeText(this,"USUARIO AGREGADO EXITOSAMENTE",
                Toast.LENGTH_LONG).show();
    }





}//Fin de la class Login
