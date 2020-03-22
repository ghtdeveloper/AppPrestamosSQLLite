package com.clases.rd.ui.prestamos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.DecimalFormat;

public class PrestamosFragment extends Fragment {

    //BD
    private OpenHelper dbHelper;
    private SQLiteDatabase db;
    private PrestamosViewModel objtPrestamosViewModel;
    //Views
    private EditText txtIdCliente;
    private EditText txtNombreCliente;
    private ImageButton btnBuscarCliente;
    private EditText txtMontoPrestamo;
    private EditText txtTipoInteres;//Tasa
    private EditText txtPlazoArmotizacion;//Plazo
    private RadioButton rdbMeses;//Meses
    private RadioButton rdbYear;//Year
    private EditText txtCantCuotas;
    private EditText txtTotalInteres;
    private EditText txtmontoFinal;//Valor final Prestamo
    private Button btnProcesarPrestamo;
    private Button btnCalcularPrestamo;
    private int idClienteObtenido;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        objtPrestamosViewModel =
                ViewModelProviders.of(this).get(PrestamosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prestamos, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        objtPrestamosViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("PRESTAMOS");
            }
        });
        //Se configura el acceso a la BD
        dbHelper = new OpenHelper(this.getActivity());
        db= dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        /*spinnerTipoPrestamos = root.findViewById(R.id.spinnerTipoPrestamo);
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
               Objects.requireNonNull(this.getActivity()),
                R.array.tipoDePrestamos,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        spinnerTipoPrestamos.setAdapter(adapter);
        */
        //Cast View
        txtNombreCliente = root.findViewById(R.id.txtNombreCliente);
        txtIdCliente = root.findViewById(R.id.txtidCliente);
        txtNombreCliente.setVisibility(View.INVISIBLE);
        btnBuscarCliente = root.findViewById(R.id.btnConsulartIDCliente);
        txtMontoPrestamo = root.findViewById(R.id.txtMontoPrestamoPorPagar);
        txtTipoInteres = root.findViewById(R.id.txtInteresPrestamo);
        txtPlazoArmotizacion = root.findViewById(R.id.txtPlazoArmotizacion);
        rdbMeses = root.findViewById(R.id.rdbMeses);
        rdbYear = root.findViewById(R.id.rdbYear);
        txtTotalInteres = root.findViewById(R.id.txtTotalintereses);
        txtmontoFinal = root.findViewById(R.id.txtMontoFinal);
        txtCantCuotas= root.findViewById(R.id.txtCantCuotas);
        btnCalcularPrestamo= root.findViewById(R.id.btnCalcularPrestamo);
        txtCantCuotas.setEnabled(false);
        txtTotalInteres.setEnabled(false);
        txtmontoFinal.setEnabled(false);
        btnProcesarPrestamo = root.findViewById(R.id.btnProcesarPrestamo);

       btnBuscarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaClienteOnClickListener();
            }
        });

       btnCalcularPrestamo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               calcularCuotasPrestamos();
           }
       });

        btnProcesarPrestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarPrestamoOnClickListener();
            }
        });

        return root;
    }//Fin del metodo OnCreateView

    @SuppressLint("SetTextI18n")
    private void busquedaClienteOnClickListener()
    {
        int idCliente = Integer.parseInt(txtIdCliente.getText().toString());
        String[] campos = new String[]{"idCliente,nombre,apellidos"};
        String[] arg = new String[]{String.valueOf(idCliente)};
        if(txtIdCliente.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"DEBE INGRESAR EL ID DEL CLIENTE",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Cursor fila = db.query("Clientes",campos,"idCliente=?",
                    arg,null,null,null);
            if(fila.moveToFirst())
            {
                do {
                    idClienteObtenido = Integer.parseInt(fila.getString(0));
                    txtNombreCliente.setVisibility(View.VISIBLE);
                    txtNombreCliente.setText(fila.getString(1) +" "+fila.getString(2));
                    txtNombreCliente.setEnabled(false);
                    txtNombreCliente.setTextColor(Color.RED);

                }while (fila.moveToNext());
            }

        }
    }//Fin del metodo busquedaClienteOnClickListener

    @SuppressLint("SetTextI18n")
    private void calcularCuotasPrestamos()
    {
        double S = Double.parseDouble(txtMontoPrestamo.getText().toString());
        int n = (int) Double.parseDouble(txtPlazoArmotizacion.getText().toString());
        double J = Double.parseDouble(txtTipoInteres.getText().toString())/100;
        double m = 12;
        double i = J/m;
        double R;
        DecimalFormat df = new DecimalFormat("#.##");

        R = S * i /1-((Math.pow(1+i,-n*m)));//Cuotas
        //Se envia el valor de la cuota
       objtPrestamosViewModel.setCuota(Double.parseDouble(df.format(R)));
        txtCantCuotas.setTextColor(Color.BLUE);
        txtCantCuotas.setText("Cuotas RD$ "+df.format(R));
        objtPrestamosViewModel.setMonto(Double.parseDouble(df.format(S)));//Bien
        objtPrestamosViewModel.setTasaInteres(Double.parseDouble(df.format(J)));//Bien
        objtPrestamosViewModel.setPlazoArmotizacion(n);

        //Se procede a calcular los intereses ganados
        double monto = S*((Math.pow(1+i,n)));
        txtTotalInteres.setTextColor(Color.BLUE);

        txtTotalInteres.setText("Total Intereses RD$"+df.format(monto-S));
        objtPrestamosViewModel.setTotalInteres(Double.valueOf(df.format(monto-S)));
        objtPrestamosViewModel.setMontoFinal(Double.valueOf(df.format(monto)));
        txtmontoFinal.setTextColor(Color.RED);
        txtmontoFinal.setText("Monto final RD$"+df.format(monto));
    }//Fin del metodo calcularCuotasPrestamos

    public void agregarPrestamoOnClickListener()
    {
        if(txtMontoPrestamo.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }
        if(txtTipoInteres.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }
        if(txtPlazoArmotizacion.getText().toString().isEmpty())
        {
            Toast.makeText(this.getActivity(),"NO PUEDE DEJAR CAMPOS VACIOS",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(db!= null)
            {
                ContentValues cv = new ContentValues();
                cv.put("monto",objtPrestamosViewModel.getMonto());
                cv.put("tasaInteres",objtPrestamosViewModel.getTasaInteres());
                cv.put("plazaArmotizacion",objtPrestamosViewModel.getPlazoArmotizacion());
                cv.put("cuota",objtPrestamosViewModel.getCuota());
                cv.put("totalInteres",objtPrestamosViewModel.getTotalInteres());
                cv.put("montoFinal",objtPrestamosViewModel.getMontoFinal());
                cv.put("idClienteFK",idClienteObtenido);
                db.insert("Prestamos",null,cv);
                Toast.makeText(this.getActivity(),"Prestamo generado correctamente",
                        Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                    limpiarCampos();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }//Fin del metodo agregarPrestamoOnClickListener

    public void limpiarCampos()
    {
        txtNombreCliente.setText("");
        txtPlazoArmotizacion.setText("");
        txtTipoInteres.setText("");
        txtMontoPrestamo.setText("");
        txtCantCuotas.setText("");
        txtTotalInteres.setText("");
        rdbYear.setChecked(false);
        rdbYear.setChecked(false);

    }

}//Fin de la class PrestamosFragment