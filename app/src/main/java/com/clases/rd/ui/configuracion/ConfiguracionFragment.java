package com.clases.rd.ui.configuracion;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import static android.widget.Toast.LENGTH_SHORT;

public class ConfiguracionFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private EditText txtCantidadUsuario;
    private EditText txtPasswordAdmin;
    private Spinner spinnerTipoReporte;
    private Button btnProcesarCambiosConfig;
    //Base de datos
    private OpenHelper dbHelper;
    private SQLiteDatabase db;
    //**************************************
    String dataObtenida="";
    ArrayList<String> listaDatos;
    private ConfiguracionViewModel objConfiguracionViewModel;
    private Calendar calendar;
    private String tituloPDF;
    String valorObtenidoSpinnerTipoReporte;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        objConfiguracionViewModel =
                ViewModelProviders.of(this).get(ConfiguracionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        objConfiguracionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Configuracion");
            }
        });
        //Se configura el acceso a la bd
        dbHelper = new OpenHelper(this.getActivity());
        db = dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        //Se realiza el cast a las vistas
        txtCantidadUsuario = root.findViewById(R.id.txtCantUsuario);
        txtPasswordAdmin = root.findViewById(R.id.txtPasswordUsuarioAcc);
        //Se crea el adaptador para el spinner de los reportes
        spinnerTipoReporte = root.findViewById(R.id.spinnerTipoReporte);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (Objects.requireNonNull(this.getActivity()),
                R.array.listReportes, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        spinnerTipoReporte.setAdapter(adapter);
        spinnerTipoReporte.setOnItemSelectedListener(this);
        //****************************************************************
        //Se asigna el listener al boton de procesar los cambios de la configuracion
        btnProcesarCambiosConfig = root.findViewById(R.id.btnProcesar);
        btnProcesarCambiosConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarConfiguracion();
            }
        });
        listaDatos = new ArrayList<>();
        calendar = Calendar.getInstance();
        ImageButton btnGenerarReporte = root.findViewById(R.id.btnGenerarReporte);
        btnGenerarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pruebaBotonDescargar();
                descargarReporte();
            }
        });
        return root;
    }


    private void agregarConfiguracion()
    {
        if(camposVacios() > 0)
        {
            Toast.makeText(this.getActivity(), "No puede dejar campos vacios"
                    , Toast.LENGTH_SHORT).show();
        }
        else
        {
            objConfiguracionViewModel.setCantidadUsuario
                    (Integer.parseInt(txtCantidadUsuario.getText().toString()));
            objConfiguracionViewModel.setPasswordAdministrativa
                    (txtPasswordAdmin.getText().toString());

            if(db != null)
            {
                ContentValues cv = new ContentValues();
                cv.put("cantidad_usuario",
                        String.valueOf(objConfiguracionViewModel.getCantidadUsuario()));
                cv.put("password",objConfiguracionViewModel.getPasswordAdministrativa());
                db.insert("Configuracion",null,cv);
                Toast.makeText(this.getActivity(),"Configuracion establecida",
                        Toast.LENGTH_SHORT).show();
                try
                {
                    Thread.sleep(1000);
                    limpiarCampos();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }//Fin del if
        }//Fin del else
    }//Fin del metodo agregarConfiguracion


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
        int posit;
        posit = posicion;

        if (parent.getId() == R.id.spinnerTipoReporte) {
            valorObtenidoSpinnerTipoReporte = parent.getItemAtPosition(posit).toString();
            setTituloPDF(valorObtenidoSpinnerTipoReporte);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int camposVacios()
    {
        int contador = 0;

        if(txtCantidadUsuario.getText().toString().isEmpty())
        {
            contador =contador + 1;
        }

        if(txtPasswordAdmin.getText().toString().isEmpty())
        {
            contador = contador +1;
        }

        return  contador;
    }

    public String getTituloPDF() {
        return tituloPDF;
    }

    public void setTituloPDF(String tituloPDF) {
        this.tituloPDF = tituloPDF;
    }



    private void limpiarCampos()
    {
        txtCantidadUsuario.setText("");
        txtPasswordAdmin.setText("");
    }

    //Se crea los query de la bd para los reportes
    /*
        Se crea un query para contar los clientes
        registrados en la app
     */

    public String getCantidadClientesRegistrados()
    {
        Cursor cursor = db.rawQuery("SELECT count(idCliente)as cantidad_clientes from Clientes",
                null);
        if(cursor.moveToFirst())
        {
            do {
                dataObtenida= cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return dataObtenida;
    }

    /*
        Este query extrae el listado de todos los clientes registrados en la app

     */
    public void getListCLientes()
    {
        listaDatos.clear();
        Cursor cursor = db.rawQuery("select nombre,apellidos,count(idPrestamo) " +
                "as cantida_prestamo from Clientes as c\n" +
                "INNER JOIN Prestamos as p on p.IdClienteFK = c.idCliente",null);
        if(cursor.moveToFirst())
        {
            do {
                listaDatos.add(cursor.getString(0));
                listaDatos.add(cursor.getString(1));
                listaDatos.add(cursor.getString(2));
            }while (cursor.moveToNext());
        }
    }//Fin del metodo getListClientes
    /*
        Este query extrae la cantidad de prestamos generados en el sistema
     */
    public String getCantidadPrestamo()
    {
        Cursor cursor = db.rawQuery("select count(idPrestamo) as cantidad_prestamo from Prestamos",
                null);
        if(cursor.moveToFirst())
        {
            do {
               dataObtenida= cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return dataObtenida;
    }

    public String getCantidadUsuario()
    {
        Cursor cursor = db.rawQuery("SELECT count(idUsuario)as cantidad_usuario from Usuarios",
                null);
        if(cursor.moveToFirst())
        {
            do {
                dataObtenida= cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return dataObtenida;
    }

    public void getListUsuarios()
    {
        Cursor cursor = db.rawQuery("select nombre,apellidos,rol from Usuarios",
                null);
        if(cursor.moveToFirst())
        {
            do {
                listaDatos.add(cursor.getString(0));
                listaDatos.add(cursor.getString(1));
                listaDatos.add(cursor.getString(2));
            }while (cursor.moveToNext());
        }
    }

    public String getCantidadCobrosProcesados()
    {
        Cursor cursor = db.rawQuery("SELECT count(idCobro)as cobros_procesados from Cobros",
                null);
        if(cursor.moveToFirst())
        {
            do {
                dataObtenida = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return dataObtenida;
    }

    // Se procede a configurar los reportes

    //Ruta en donde se va a guardar el archivo PDF
            //Aqui se configura la ruta en la cual se van a guardar los archivos PDFs
    private String getRutaArchivoDispostivo()
    {
        return Environment.getExternalStorageDirectory().getPath() + "/Reportes/";

    }
    @SuppressLint("SimpleDateFormat")
    public String getFechaReporte()//Fecha en la cual se genero el reporte
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");//Se cambio por el guion
        return dateFormat.format(calendar.getTime());
    }

    public void pdfClientes() throws FileNotFoundException, DocumentException {
        getListCLientes();
        File file = new File(getRutaArchivoDispostivo());
        if (!file.exists()) { file.mkdirs(); }
        String targertPDF = getRutaArchivoDispostivo()+getFechaReporte()+"_"+"clientes"+".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(targertPDF)).setInitialLeading(20);
        document.open();
        //Fuentes y estilo
        Font fuenteColmunTablas = new Font(FontFactory.getFont("arial",13,Font.BOLD));
        Font fuenteTitulo = new Font(FontFactory.getFont("arial",22,Font.BOLD
                , BaseColor.RED));
        Font fuenteSubTitulo = new Font(FontFactory.getFont("arial",15,Font.BOLDITALIC
                ,BaseColor.BLACK));

        //Titulo principal
        Paragraph fechaReporte = new Paragraph(getFechaReporte(),fuenteSubTitulo);
        fechaReporte.setAlignment(Element.ALIGN_LEFT);
        document.add(fechaReporte);
        Paragraph paragraphTitulo = new Paragraph("REPORTE CLIENTES",fuenteTitulo);
        paragraphTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        LineSeparator separator = new LineSeparator(0.5f,120.0f,
                BaseColor.BLACK,Element.ALIGN_CENTER,10.5f);
        document.add(separator);
        // Cantidad Clientes Registrados
        Paragraph paragraphSubTitulo = new Paragraph("CANTIDAD DE CLIENTES REGISTRADOS"
                ,fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        /*
        //Tabla #1
        float[] columnwitdthTable1 = {0.5f,1.5f};
        PdfPTable table1 = new PdfPTable(columnwitdthTable1);
        table1.setTotalWidth(50f);
        PdfPCell cell = new PdfPCell(new Phrase("Qty",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell);
        table1.addCell(getCantidadClientesRegistrados());
        document.add(table1);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        */
        //Historico Clientes telefono, nombre completo, Qty vehiculos registrados
        /*
        paragraphSubTitulo = new Paragraph("HISTORICO CLIENTES",fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphSubTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphSubTitulo);
*/
        //Tabla #3
        /*
        float[] columnwidth = {3f,5f,3f};
        PdfPTable table3 = new PdfPTable(columnwidth);//Se crea la tabla
        table3.setTotalWidth(200f);
        cell = new PdfPCell(new Phrase("Nombre",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table3.addCell(cell);
        cell = new PdfPCell(new Phrase("Apellido",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table3.addCell(cell);
        cell = new PdfPCell(new Phrase("Cantidad de prestamos",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table3.addCell(cell);
        for(String str : listaDatos)
        {
            table3.addCell(str);
        }
        document.add(table3);

         */
        document.close();
    }//Fin del metodo pdfClientes

    public void pdfUsuarios() throws FileNotFoundException, DocumentException {
        getListUsuarios();
        File file = new File(getRutaArchivoDispostivo());
        if (!file.exists()) { file.mkdirs(); }
        String targertPDF = getRutaArchivoDispostivo()+getFechaReporte()+"_"+getTituloPDF()+".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(targertPDF)).setInitialLeading(20);
        document.open();
        //Fuentes y estilo
        Font fuenteColmunTablas = new Font(FontFactory.getFont("arial",13,Font.BOLD));
        Font fuenteTitulo = new Font(FontFactory.getFont("arial",22,Font.BOLD
                , BaseColor.RED));
        Font fuenteSubTitulo = new Font(FontFactory.getFont("arial",15,Font.BOLDITALIC
                ,BaseColor.BLACK));

        //Titulo principal
        Paragraph fechaReporte = new Paragraph(getFechaReporte(),fuenteSubTitulo);
        fechaReporte.setAlignment(Element.ALIGN_LEFT);
        document.add(fechaReporte);
        Paragraph paragraphTitulo = new Paragraph("REPORTE USUARIOS",fuenteTitulo);
        paragraphTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        LineSeparator separator = new LineSeparator(0.5f,120.0f,
                BaseColor.BLACK,Element.ALIGN_CENTER,10.5f);
        document.add(separator);
        // Cantidad Usuarios Registrados
        Paragraph paragraphSubTitulo = new Paragraph("CANTIDAD DE USUARIOS REGISTRADOS"
                ,fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        //Tabla #1
        float[] columnwitdthTable1 = {0.5f,1.5f};
        PdfPTable table1 = new PdfPTable(columnwitdthTable1);
        table1.setTotalWidth(50f);
        PdfPCell cell = new PdfPCell(new Phrase("Qty",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell);
        table1.addCell(getCantidadUsuario());
        document.add(table1);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);

        //Historico Clientes telefono, nombre completo, Qty vehiculos registrados
        paragraphSubTitulo = new Paragraph("HISTORICO CLIENTES",fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphSubTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphSubTitulo);
        //Tabla #3
        float[] columnwidth = {3f,5f,3f};
        PdfPTable table3 = new PdfPTable(columnwidth);//Se crea la tabla
        table3.setTotalWidth(200f);
        cell = new PdfPCell(new Phrase("Nombre",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table3.addCell(cell);
        cell = new PdfPCell(new Phrase("Apellido",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table3.addCell(cell);
        cell = new PdfPCell(new Phrase("Cantidad de prestamos",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table3.addCell(cell);
        for(String str : listaDatos)
        {
            table3.addCell(str);
        }
        document.add(table3);
        document.close();
    }//Fin del metodo pdfUsuarios


    public void pdfPrestamos() throws FileNotFoundException, DocumentException {
        getListUsuarios();
        File file = new File(getRutaArchivoDispostivo());
        if (!file.exists()) { file.mkdirs(); }
        String targertPDF = getRutaArchivoDispostivo()+getFechaReporte()+"_"+getTituloPDF()+".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(targertPDF)).setInitialLeading(20);
        document.open();
        //Fuentes y estilo
        Font fuenteColmunTablas = new Font(FontFactory.getFont("arial",13,Font.BOLD));
        Font fuenteTitulo = new Font(FontFactory.getFont("arial",22,Font.BOLD
                , BaseColor.RED));
        Font fuenteSubTitulo = new Font(FontFactory.getFont("arial",15,Font.BOLDITALIC
                ,BaseColor.BLACK));

        //Titulo principal
        Paragraph fechaReporte = new Paragraph(getFechaReporte(),fuenteSubTitulo);
        fechaReporte.setAlignment(Element.ALIGN_LEFT);
        document.add(fechaReporte);
        Paragraph paragraphTitulo = new Paragraph("REPORTE PRESTAMOS",fuenteTitulo);
        paragraphTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        LineSeparator separator = new LineSeparator(0.5f,120.0f,
                BaseColor.BLACK,Element.ALIGN_CENTER,10.5f);
        document.add(separator);
        // Cantidad Usuarios Registrados
        Paragraph paragraphSubTitulo = new Paragraph("CANTIDAD DE PRESTAMOS GENERADOS"
                ,fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        //Tabla #1
        float[] columnwitdthTable1 = {0.5f,1.5f};
        PdfPTable table1 = new PdfPTable(columnwitdthTable1);
        table1.setTotalWidth(50f);
        PdfPCell cell = new PdfPCell(new Phrase("Qty",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell);
        table1.addCell(getCantidadPrestamo());
        document.add(table1);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        document.close();
    }//Fin del metodo pdfPrestamos

    public void pdfCobros() throws FileNotFoundException, DocumentException {
        getListUsuarios();
        File file = new File(getRutaArchivoDispostivo());
        if (!file.exists()) { file.mkdirs(); }
        String targertPDF = getRutaArchivoDispostivo()+getFechaReporte()+"_"+getTituloPDF()+".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(targertPDF)).setInitialLeading(20);
        document.open();
        //Fuentes y estilo
        Font fuenteColmunTablas = new Font(FontFactory.getFont("arial",13,Font.BOLD));
        Font fuenteTitulo = new Font(FontFactory.getFont("arial",22,Font.BOLD
                , BaseColor.RED));
        Font fuenteSubTitulo = new Font(FontFactory.getFont("arial",15,Font.BOLDITALIC
                ,BaseColor.BLACK));

        //Titulo principal
        Paragraph fechaReporte = new Paragraph(getFechaReporte(),fuenteSubTitulo);
        fechaReporte.setAlignment(Element.ALIGN_LEFT);
        document.add(fechaReporte);
        Paragraph paragraphTitulo = new Paragraph("REPORTE COBROS",fuenteTitulo);
        paragraphTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        LineSeparator separator = new LineSeparator(0.5f,120.0f,
                BaseColor.BLACK,Element.ALIGN_CENTER,10.5f);
        document.add(separator);
        // Cantidad Usuarios Registrados
        Paragraph paragraphSubTitulo = new Paragraph("CANTIDAD DE COBROS PROCESADOS"
                ,fuenteSubTitulo);
        paragraphSubTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSubTitulo);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        //Tabla #1
        float[] columnwitdthTable1 = {0.5f,1.5f};
        PdfPTable table1 = new PdfPTable(columnwitdthTable1);
        table1.setTotalWidth(50f);
        PdfPCell cell = new PdfPCell(new Phrase("Qty",fuenteColmunTablas));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell);
        table1.addCell(getCantidadCobrosProcesados());
        document.add(table1);
        paragraphTitulo = new Paragraph("\n"+ "\n");
        document.add(paragraphTitulo);
        document.close();
    }//Fin del metodo pdfPrestamos

    private void descargarReporte()
    {

        String cobros = "Cobros";
        String clientes = "Clientes";
        String prestamos = "Prestamos";
        String usuarios = "Usuarios";

        if(clientes.equals(valorObtenidoSpinnerTipoReporte))
        {
            try {
                pdfClientes();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.getActivity(),"Reporte generado exitosamente",
                    LENGTH_SHORT).show();
        }

        if(usuarios.equals(valorObtenidoSpinnerTipoReporte))
        {

            try {
                pdfUsuarios();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.getActivity(),"Reporte generado exitosamente",
                    LENGTH_SHORT).show();
        }

        if(prestamos.equals(valorObtenidoSpinnerTipoReporte))
        {
            try {
                pdfPrestamos();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.getActivity(),"Reporte generado exitosamente",
                    LENGTH_SHORT).show();
        }

        if(cobros.equals(valorObtenidoSpinnerTipoReporte))
        {
            try {
                pdfCobros();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.getActivity(),"Reporte generado exitosamente",
                    LENGTH_SHORT).show();
        }



    }//Fin del metodo descargarReporte


}//Fin del a class ConfiguracionFragement