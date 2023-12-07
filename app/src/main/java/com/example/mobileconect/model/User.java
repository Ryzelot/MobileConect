package com.example.mobileconect.model;

public class User {

    String name, correo, numero;

    public  User (){}

    public User(String name, String correo, String numero) {
        this.name = name;
        this.correo = correo;
        this.numero = numero;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
