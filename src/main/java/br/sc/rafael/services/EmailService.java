package br.sc.rafael.services;

import br.sc.rafael.entities.Usuario;

public interface EmailService {

    public void notificarAtraso(Usuario usuario);
}
