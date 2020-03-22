package com.clases.rd.ui.cobros;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.clases.rd.R;
import com.clases.rd.modelo.OpenHelper;

import java.util.Calendar;
import java.util.Objects;

public class CobrosFragment extends Fragment {


    //BD
    private OpenHelper dbHelper;
    private SQLiteDatabase db;
    private CobrosViewModel objCobrosViewModel;
    private ImageButton btnBuscarPrestamo;
    private EditText txtNombreCliente;
    private EditText txtMontoAPagar;
    private EditText txtMontoPagado;
    private EditText txtmostrarFecha;
    private ImageButton btnMostrarDatePicker;
    private TextView txtIdPrestamo;
    private Button btnProcesarCobro;
    //Variables a enviar para la BD
    private double importePagado;
    private int idPrestamoObtenido;
    private int idClienteObtenido;
    //la fecha es lo ultimo que se enviara


    //Para lo de la fecha
    //Calendario para obtener fecha & hora
    private static final String CERO = "0";
    private static final String BARRA = "/";
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);






    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        objCobrosViewModel =
                ViewModelProviders.of(this).get(CobrosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pagos, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        objCobrosViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Cobros");
            }
        });
        //Se configura el acceso a la BD
        dbHelper = new OpenHelper(this.getActivity());
        db= dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //Cast Views
        //View
        txtIdPrestamo = root.findViewById(R.id.txtIdPrestamo);
        txtNombreCliente = root.findViewById(R.id.txtNombreClienteSearch);
        txtNombreCliente.setVisibility(View.INVISIBLE);
        txtMontoAPagar = root.findViewById(R.id.txtMontoPrestamoPorPagar);
        txtMontoAPagar.setEnabled(false);
        txtMontoPagado = root.findViewById(R.id.txtMontoPrestamoPagado);
        txtmostrarFecha = root.findViewById(R.id.txtFechaPago);
        btnBuscarPrestamo = root.findViewById(R.id.btnConsultarPrestamo);
        btnBuscarPrestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPrestamosOnClickListener();
            }
        });
        btnMostrarDatePicker = root.findViewById(R.id.btnMostrarFecha);
        btnMostrarDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerFecha();
            }
        });
        btnProcesarCobro = root.findViewById(R.id.btnProcesar);
        btnProcesarCobro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarPrestamoOnClickListener();
            }
        });

        return root;
    }//Fin del metodo onCreate

    @SuppressLint("SetTextI18n")
    private void buscarPrestamosOnClickListener()
    {
        String nombreCliente= "";
        Cursor cursor = db.rawQuery("Select c.idCliente, c.nombre, c.apellidos, p.idPrestamo," +
                "p.cuota  from Clientes as c \n" +
                "INNER JOIN Prestamos as p on p.IdClienteFK = c.idCliente;",null);
        if(cursor.moveToFirst())
        {
            do {
                idClienteObtenido = Integer.parseInt(cursor.getString(0));
                nombreCliente = cursor.getString(1) + " "+ cursor.getString(2);
                idPrestamoObtenido = Integer.parseInt(cursor.getString(3));
                txtNombreCliente.setVisibility(View.VISIBLE);
                txtNombreCliente.setText(nombreCliente);
                txtNombreCliente.setEnabled(false);
                txtNombreCliente.setTextColor(Color.RED);
                txtMontoAPagar.setVisibility(View.VISIBLE);
                txtMontoAPagar.setText("RD$ "+cursor.getString(4));
                txtMontoAPagar.setTextColor(Color.BLUE);
                txtMontoAPagar.setEnabled(false);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }//Fin del metodo  buuscarPrestamosOnClickListener


    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(Objects.requireNonNull
                (this.getActivity())
                , new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya
                // que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO +
                        String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO +
                        String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                txtmostrarFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

    private void agregarPrestamoOnClickListener()
    {
        if(txtIdPrestamo.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }

        if(txtMontoPagado.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }
        if(txtmostrarFecha.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(db != null)
            {
                ContentValues cv = new ContentValues();
                cv.put("importe_pago",Double.parseDouble(txtMontoPagado.getText().toString()));
                cv.put("idPrestamoFK",idPrestamoObtenido);
                cv.put("idClienteFK",idClienteObtenido);
                cv.put("fecha_pago",txtmostrarFecha.getText().toString());
                db.insert("Cobros",null,cv);
                Toast.makeText(this.getActivity(),"Cobro generado correctamente",
                        Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                    limpiarCodigo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }//Fin del metodo agregarPrestamoOnClickList

    private void limpiarCodigo()
    {
        txtIdPrestamo.setText("");
        txtNombreCliente.setText("");
        txtMontoAPagar.setText("");
        txtMontoPagado.setText("");
        txtmostrarFecha.setText("");
    }
}//Fin de la class CobrosFragemnent