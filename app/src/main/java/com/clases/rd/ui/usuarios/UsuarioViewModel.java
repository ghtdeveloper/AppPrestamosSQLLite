package com.clases.rd.ui.usuarios;

import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsuarioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    //Variables
    private String nombre;
    private String apellido;
    private String telefono;
    private String Direccion;
    private String Correo;
    private String Password;
    private String Rol;
    private String estadoUser;

    public MutableLiveData<String> getmText() {
        return mText;
    }


    public UsuarioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    //Getters and setters
    public void setmText(MutableLiveData<String> mText) {
        this.mText = mText;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRol() {
        return Rol;
    }

    public void setRol(String rol) {
        Rol = rol;
    }

    public String getEstadoUser() {
        return estadoUser;
    }

    public void setEstadoUser(String estadoUser) {
        this.estadoUser = estadoUser;
    }

}//Fin de la class UsuarioViewModel