package com.clases.rd.ui.usuarios;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.clases.rd.R;
import com.clases.rd.modelo.OpenHelper;

import java.util.Objects;

public class UsuarioFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText txtTelefonoUser;
    private EditText txtNombreUsuario;
    private EditText txtApellidoUsuario;
    private EditText txtTelefono;
    private EditText txtDireccion;
    private EditText txtCorreo;
    private EditText txtPassword;
    private Spinner spinnerRolUser;
    private Spinner spinnerEstadoUser;
    private UsuarioViewModel objUsuarioViewModel;
    private  SQLiteDatabase db;
    private OpenHelper dbHelper;
    private ImageView btnConsultarUsuario;
    private ImageView btnAgregarUsuario;
    private ImageView btnModificarUsuario;
    private Button btnProcesar;
    String telefono;
    private String definirAccion;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        objUsuarioViewModel =
                ViewModelProviders.of(this).get(UsuarioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_usuario, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        objUsuarioViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Usuarios");
            }
        });
        //Spinner Rol User
        spinnerRolUser = root.findViewById(R.id.spinnerRolUsuario);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                Objects.requireNonNull(this.getActivity()),
                R.array.rolUsuario,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        spinnerRolUser.setAdapter(adapter);

        //Spiner Estado User
        spinnerEstadoUser = root.findViewById(R.id.spinnerEstadoUsuario);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                Objects.requireNonNull(this.getActivity()),
                R.array.estadoUsuario,R.layout.spinner_style);
        adapter2.setDropDownViewResource(R.layout.spinner_style);
        spinnerEstadoUser.setAdapter(adapter2);

        //Se realiza el cast con las vistas del layout
        txtNombreUsuario = root.findViewById(R.id.txtNombreUsuario);
        txtApellidoUsuario = root.findViewById(R.id.txtApellidoUsuario);
        txtTelefono = root.findViewById(R.id.txtTelefonoUsuario);
        txtDireccion = root.findViewById(R.id.txtDireccionUsuario);
        txtCorreo  = root.findViewById(R.id.txtCorreoUsuario);
        txtPassword = root.findViewById(R.id.txtPasswordUsuario);
        //Button btnProcesarAddUsuario = root.findViewById(R.id.btnProcesar);
        txtTelefonoUser = root.findViewById(R.id.txtTelefonoUser);
        btnProcesar = root.findViewById(R.id.btnProcesar);

        btnConsultarUsuario = root.findViewById(R.id.btnConsultarUsuario);
        btnAgregarUsuario = root.findViewById(R.id.btnAgregarUsuario);
        btnModificarUsuario = root.findViewById(R.id.btnActualizarUsuario);
        //**********************************************************
                        //Acceso a la base de datos
        dbHelper = new OpenHelper(this.getActivity());
        db= dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //******************************************************
                    //Se asigna el listener al spinner
        spinnerRolUser.setOnItemSelectedListener(this);
        spinnerEstadoUser.setOnItemSelectedListener(this);
        //*******************************************************

        btnConsultarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busquedaClienteOnClickListener();
            }
        });

        btnAgregarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
                activarCampos();
                txtTelefonoUser.setEnabled(false);
                definirAccion ="Agregar";
            }
        });


        btnProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(definirAccion.equals("Agregar"))
                {
                    agregarUsuario();
                }

                if(definirAccion.equals("Modificar"))
                {
                    actualizarRegistroOnClickListener();
                }

            }
        });
        desactivarCampos();

        btnModificarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activarCamposXModificar();
                txtTelefonoUser.setEnabled(false);
                definirAccion ="Modificar";
            }
        });

        return root;
    }//Fin del metoodo onCreate


    private void agregarUsuario()
    {
       if(validarCamposVacios() > 0 )
       {
           Toast.makeText(this.getActivity(),"No puede dejar ningun campo vacio",
                   Toast.LENGTH_SHORT).show();
       }
       else
       {
            objUsuarioViewModel.setNombre(txtNombreUsuario.getText().toString());
            objUsuarioViewModel.setApellido(txtApellidoUsuario.getText().toString());
            objUsuarioViewModel.setTelefono(txtTelefono.getText().toString());
            objUsuarioViewModel.setDireccion(txtDireccion.getText().toString());
            objUsuarioViewModel.setCorreo(txtCorreo.getText().toString());
            objUsuarioViewModel.setPassword(txtPassword.getText().toString());
            if(db != null)
            {
                ContentValues cv = new ContentValues();
                cv.put("nombre",objUsuarioViewModel.getNombre());
                cv.put("apellidos",objUsuarioViewModel.getApellido());
                cv.put("telefono",objUsuarioViewModel.getTelefono());
                cv.put("direccion",objUsuarioViewModel.getDireccion());
                cv.put("correo",objUsuarioViewModel.getCorreo());
                cv.put("rol",objUsuarioViewModel.getRol());
                cv.put("password",objUsuarioViewModel.getPassword());
                db.insert("Usuarios",null,cv);
                Toast.makeText(this.getActivity(), "Usuario Agregado"
                        , Toast.LENGTH_SHORT).show();
                try
                {
                    Thread.sleep(1000);
                    limpiarCampos();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }//Fin del if

       }//Fin del else

    }//Fin del metodo agregarUsuario

    //Se utiliza el evente onItemSelected para saber que valor utilizar de lista
    @Override
    public void onItemSelected(AdapterView<?> parent,View view, int posicion,long id)
    {
        int posit;
        posit = posicion;

        if(parent.getId()==R.id.spinnerRolUsuario)
        {
            String valorSpinnerUsuario = parent.getItemAtPosition(posit).toString();
            objUsuarioViewModel.setRol(valorSpinnerUsuario);
        }

        if(parent.getId() == R.id.spinnerEstadoUsuario)
        {
            String valorEstadoUsuario = parent.getItemAtPosition(posit).toString();
            objUsuarioViewModel.setEstadoUser(valorEstadoUsuario);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //no se ha seleccionado nada
    }


    //Metodos para validar si algun campo esta vacio
    private int validarCamposVacios()
    {
        int contador = 0;
        if(txtNombreUsuario.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtApellidoUsuario.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtTelefono.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtDireccion.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtCorreo.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtPassword.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }
        return contador;
    }//Fin del metodo validarCamposVacios


    private void limpiarCampos()
    {
        txtNombreUsuario.setText("");
        txtApellidoUsuario.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtCorreo.setText("");
        txtPassword.setText("");

    }//Fin del metodo limpiar Campos


    private void desactivarCampos()
    {
         txtNombreUsuario.setEnabled(false);
         txtApellidoUsuario.setEnabled(false);
         txtTelefono.setEnabled(false);
         txtDireccion.setEnabled(false);
         txtCorreo.setEnabled(false);
         txtPassword.setEnabled(false);
         spinnerRolUser.setEnabled(false);
         spinnerEstadoUser.setEnabled(false);

    }

    private void activarCampos()
    {
        txtNombreUsuario.setEnabled(true);
        txtApellidoUsuario.setEnabled(true);
        txtTelefono.setEnabled(true);
        txtDireccion.setEnabled(true);
        txtCorreo.setEnabled(true);
        txtPassword.setEnabled(true);
        spinnerRolUser.setEnabled(true);
        spinnerEstadoUser.setEnabled(true);
    }



    private void activarCamposXModificar()
    {
        txtNombreUsuario.setEnabled(true);
        txtNombreUsuario.setBackgroundColor(Color.YELLOW);
        txtApellidoUsuario.setEnabled(true);
        txtApellidoUsuario.setBackgroundColor(Color.YELLOW);
        txtTelefono.setEnabled(true);
        txtTelefono.setBackgroundColor(Color.YELLOW);
        txtDireccion.setEnabled(true);
        txtDireccion.setBackgroundColor(Color.YELLOW);
        txtCorreo.setEnabled(true);
        txtCorreo.setBackgroundColor(Color.YELLOW);
        txtPassword.setEnabled(true);
        txtPassword.setBackgroundColor(Color.YELLOW);
        spinnerRolUser.setEnabled(true);
        spinnerEstadoUser.setEnabled(true);
    }

    private void consultarValoresSpinnerRol(String rol)
    {
        for(int i=0; i< spinnerRolUser.getAdapter().getCount();i++)
        {
           if(spinnerRolUser.getAdapter().getItem(i).equals(rol))
           {
               spinnerRolUser.setSelection(i);
           }
        }
    }


    private void consultarValoresSpinnerEstadoUser(String estado)
    {
        for(int i=0; i< spinnerEstadoUser.getAdapter().getCount();i++)
        {
            if(spinnerEstadoUser.getAdapter().getItem(i).equals(estado))
            {
                spinnerEstadoUser.setSelection(i);
            }
        }
    }


    private void busquedaClienteOnClickListener()
    {
        String mande="busqueda";
        telefono = txtTelefonoUser.getText().toString();
        String[] campos = new String[]{"nombre,apellidos,telefono,direccion,password,correo,rol" +
                ",estado"};
        String[] argFiltro = new String[] {telefono.trim()};
        if(txtTelefonoUser.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"DEBE INGRESAR UN TELEFONO",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Cursor fila = db.query("Usuarios",campos,"telefono=?",
                    argFiltro,null,null,null);
            if(fila.moveToFirst())
            {
                do {

                    txtNombreUsuario.setText(fila.getString(0));
                    txtApellidoUsuario.setText(fila.getString(1));
                    txtTelefono.setText(fila.getString(2));
                    txtDireccion.setText(fila.getString(3));
                    txtPassword.setText(fila.getString(4));
                    txtCorreo.setText(fila.getString(5));
                    String rolObtenido = fila.getString(6);
                    String estadoObtenido = fila.getString(7);
                    consultarValoresSpinnerRol(rolObtenido);
                    consultarValoresSpinnerEstadoUser(estadoObtenido);
                }while (fila.moveToNext());

            }
            else
            {
                Toast.makeText(this.getActivity(),
                        "NO SE HA ENCONTRADO NINGUN REGISTRO",Toast.LENGTH_LONG).show();
            }
            fila.close();
        }

    }//Fin del metodo busquedaClienteOnClickListener

    private void actualizarRegistroOnClickListener()
    {
        String mande="actualizar";
        telefono = txtTelefonoUser.getText().toString();
        String[] argFiltro = new String[] {telefono.trim()};
        if(db!= null)
        {
            ContentValues values = new ContentValues();
            values.put("nombre",txtNombreUsuario.getText().toString());
            values.put("apellidos",txtNombreUsuario.getText().toString());
            values.put("telefono",txtTelefono.getText().toString());
            values.put("direccion",txtDireccion.getText().toString());
            values.put("password",txtPassword.getText().toString());
            values.put("correo",txtCorreo.getText().toString());
            values.put("rol",objUsuarioViewModel.getRol());
            values.put("estado",objUsuarioViewModel.getEstadoUser());
            //db.update("usuario",values,"telefono=",argFiltro);
            db.update("Usuarios",values,"telefono=?",argFiltro);
            Toast.makeText(this.getActivity(), "USUARIO MODIFICADO" +
                    "EXITOSAMENTE", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(1000);
                limpiarCampos();
                desactivarCampos();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //db.close();

    }//Fin del metodo actualizarRegistroOnClickListener



}//Fin de la class UsuarioFragment