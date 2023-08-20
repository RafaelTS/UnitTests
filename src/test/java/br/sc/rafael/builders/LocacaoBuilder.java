package br.sc.rafael.builders;

import br.sc.rafael.Utils.DataUtils;
import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;

import java.util.Arrays;
import java.util.Date;

import static br.sc.rafael.Utils.DataUtils.obterDataComDiferencaDias;
import static br.sc.rafael.builders.FilmeBuilder.*;
import static br.sc.rafael.builders.UsuarioBuilder.umUsuario;

public class LocacaoBuilder {

    private Locacao elemento;

    private LocacaoBuilder() {}

    public static LocacaoBuilder umaLocacao() {
        LocacaoBuilder  builder = new LocacaoBuilder();
        inicializarDadosPadroes(builder);
        return builder;
    }

    public static void inicializarDadosPadroes(LocacaoBuilder builder) {
        builder.elemento = new Locacao();
        Locacao elemento = builder.elemento;

        elemento.setUsuario(umUsuario().agora());
        elemento.setFilmes(Arrays.asList(umFilme().agora()));
        elemento.setDataLocacao(new Date());
        elemento.setDataRetorno(obterDataComDiferencaDias(1));
        elemento.setValor(4.0);
    }

    public LocacaoBuilder comUsuario(Usuario usuario) {
        elemento.setUsuario(usuario);
        return this;
    }

    public LocacaoBuilder comListaFilmes(Filme... filmes) {
        elemento.setFilmes(Arrays.asList(filmes));
        return this;
    }

    public LocacaoBuilder comDataLocacao(Date data) {
        elemento.setDataLocacao(data);
        return this;
    }

    public LocacaoBuilder comDataRetorno(Date data) {
        elemento.setDataRetorno(data);
        return this;
    }

    public LocacaoBuilder atrasada() {
        elemento.setDataLocacao(obterDataComDiferencaDias(-4));
        elemento.setDataRetorno(obterDataComDiferencaDias(-2));
        return this;
    }

    public LocacaoBuilder comValor(Double valor) {
        elemento.setValor(valor);
        return this;
    }

    public Locacao agora() {
        return elemento;
    }
       
}
