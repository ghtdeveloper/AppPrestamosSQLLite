package com.clases.rd.ui.prestamos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrestamosViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private double monto;
    private double tasaInteres;
    private int plazoArmotizacion;
    private double cuota;
    private double totalInteres;
    private double montoFinal;

    public PrestamosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    //Getters and Setters
    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public int getPlazoArmotizacion() {
        return plazoArmotizacion;
    }

    public void setPlazoArmotizacion(int plazoArmotizacion) {
        this.plazoArmotizacion = plazoArmotizacion;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public double getTotalInteres() {
        return totalInteres;
    }

    public void setTotalInteres(double totalInteres) {
        this.totalInteres = totalInteres;
    }

    public double getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(double montoFinal) {
        this.montoFinal = montoFinal;
    }







}//Fin de la class PresttamosViewModel