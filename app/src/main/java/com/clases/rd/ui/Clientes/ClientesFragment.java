package com.clases.rd.ui.Clientes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.clases.rd.R;
import com.clases.rd.modelo.OpenHelper;

public class ClientesFragment extends Fragment {

    private OpenHelper dbHelper;
    private SQLiteDatabase db;
    //Variables de la vistas
    private EditText txtNombreCliente;
    private EditText txtApellido;
    private EditText txtCedula;
    private EditText txtTelefono;
    private RadioButton rdbSexMasculino;
    private RadioButton rdbSexFemenino;
    private EditText txtDireccion;
    private ClientesViewModel objClientesViewModel;
    private Button btnProcesarCliente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         objClientesViewModel =
                ViewModelProviders.of(this).get(ClientesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_clientes, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        objClientesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("CLIENTES");
            }
        });
            //Se configura el acceso a la BD
        dbHelper = new OpenHelper(this.getActivity());
        db= dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //**********************************************

        //Se realiza el cast de las vistas con los objetos del layout
        txtNombreCliente = root.findViewById(R.id.txtNombreCliente);
        txtApellido = root.findViewById(R.id.txtapellidoCliente);
        txtCedula = root.findViewById(R.id.txtcedulaCliente);
        txtTelefono = root.findViewById(R.id.txtTelefonoCliente);
        rdbSexMasculino = root.findViewById(R.id.rdbYear);
        rdbSexFemenino = root.findViewById(R.id.rdbFemenino);
        txtDireccion = root.findViewById(R.id.txtDireccionCliente);
        btnProcesarCliente = root.findViewById(R.id.btnProcesarAddCLiente);
        btnProcesarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarClienteOnClickListener();
            }
        });

        return root;
    }//Fin del metodo onCreate Fragment

    /*
        Metodo definido para agregar los clientes a la base de datos
     */

        private void agregarClienteOnClickListener()
        {
            //Si hay un campo vacio se procede a lanzar una alerta
           if(validarCamposVacios()> 0)
           {
              Toast.makeText(this.getActivity(),"No puede dejar ningun campo vacio",
                      Toast.LENGTH_SHORT).show();

           }
           //De lo contrario se asignan los valores a los controles
           else
           {
               objClientesViewModel.setNombre(txtNombreCliente.getText().toString());
               objClientesViewModel.setApellido(txtApellido.getText().toString());
               objClientesViewModel.setTelefono(txtTelefono.getText().toString());
               objClientesViewModel.setCedula(txtCedula.getText().toString());
               objClientesViewModel.setDireccion(txtDireccion.getText().toString());

               //Se verifica que la base de datos exista antes de ingresar datos
               if(db != null)
               {
                   ContentValues cv = new ContentValues();
                   cv.put("nombre",objClientesViewModel.getNombre());
                   cv.put("apellidos",objClientesViewModel.getApellido());
                   cv.put("sexo",getSexoCliente());
                   cv.put("cedula",objClientesViewModel.getCedula());
                   cv.put("telefono",objClientesViewModel.getTelefono());
                   cv.put("direccion",objClientesViewModel.getDireccion());
                   db.insert("Clientes",null,cv);
                   Toast.makeText(this.getActivity(),"Cliente agregado",
                           Toast.LENGTH_SHORT).show();
                   try {
                       Thread.sleep(1000);
                       limpiarCampos();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }

        }//Fin del metodo agregarCLiente

    //Metodos para validar si algun campo esta vacio
    private int validarCamposVacios()
    {
        int contador = 0;
        if(txtNombreCliente.getText().toString().isEmpty())
        {
           contador = contador + 1;
        }

        if(txtApellido.getText().toString().isEmpty())
        {
            contador = contador + 1;
        }

        if(txtCedula.getText().toString().isEmpty())
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
        return contador;
    }//Fin del metodo validarCamposVacios

    private String getSexoCliente()
    {
       String sexo = "";

       if(rdbSexMasculino.isChecked())
       {
           sexo="Masculino";
       }
       if(rdbSexFemenino.isChecked())
       {
           sexo = "Femenino";
       }
       return sexo;
    }

    private void limpiarCampos()
    {
        txtNombreCliente.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        rdbSexMasculino.setChecked(false);
        rdbSexFemenino.setChecked(false);

    }//Fin del metodo limpiar Campos


}//Fin de la class ClientesFragmen