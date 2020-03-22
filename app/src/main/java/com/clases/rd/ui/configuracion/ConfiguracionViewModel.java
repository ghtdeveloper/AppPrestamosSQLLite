package com.clases.rd.ui.configuracion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfiguracionViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private int cantidadUsuario;
    private String passwordAdministrativa;



    public ConfiguracionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    //Getters and Setter

    public int getCantidadUsuario() {
        return cantidadUsuario;
    }

    public void setCantidadUsuario(int cantidadUsuario) {
        this.cantidadUsuario = cantidadUsuario;
    }

    public String getPasswordAdministrativa() {
        return passwordAdministrativa;
    }

    public void setPasswordAdministrativa(String passwordAdministrativa) {
        this.passwordAdministrativa = passwordAdministrativa;
    }

}//Fin de la class ConfiguracionViewModel

