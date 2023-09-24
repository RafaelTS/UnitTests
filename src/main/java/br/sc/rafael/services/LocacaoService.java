package br.sc.rafael.services;

import br.sc.rafael.Utils.DataUtils;
import br.sc.rafael.daos.LocacaoDAO;
import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;
import br.sc.rafael.exceptions.FilmeSemEstoqueException;
import br.sc.rafael.exceptions.LocadoraException;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.sc.rafael.Utils.DataUtils.adicionarDias;

public class LocacaoService {

    private LocacaoDAO dao;
    private SPCService spcService;
    private EmailService emailService;

    public Locacao alugarFilme(Usuario usuario, List <Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

        if (usuario == null) {
            throw new LocadoraException("Usuario vazio");
        }

        if (filmes == null || filmes.isEmpty()) {
            throw new LocadoraException("Filme vazio");
        }

        for(Filme filme: filmes) {
            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException();
            }
        }

        boolean negativado;

        try {
            negativado = spcService.possuiNegativacao(usuario);
        } catch (Exception e) {
            throw new LocadoraException("Problemas com SPC, tente novamente");
        }
        if (negativado) {
            throw new LocadoraException("Usuário negativado");
        }

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
        Double valorTotal = 0d;

        for(int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);
            Double valorFilme = filme.getPrecoLocacao();
            switch (i){
                case 2: valorFilme = valorFilme * 0.75; break;
                case 3: valorFilme = valorFilme * 0.50; break;
                case 4: valorFilme = valorFilme * 0.25; break;
                case 5: valorFilme = 0d; break;
            }
            valorTotal += valorFilme;
        }
        locacao.setValor(valorTotal);

        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);
        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
            dataEntrega = adicionarDias(dataEntrega, 1);
        }
        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
        dao.salvar(locacao);

        return locacao;
    }

    public void notificarAtrasos() {
        List<Locacao> locacaes = dao.obterLocacoesPendentes();
        for (Locacao locacao: locacaes) {
            if (locacao.getDataRetorno().before(new Date())){
                emailService.notificarAtraso(locacao.getUsuario());
            }
        }
    }

    public void prorrogarLocacao(Locacao locacao, int dias) {
        Locacao novaLocacao = new Locacao();
        novaLocacao.setUsuario(locacao.getUsuario());
        novaLocacao.setFilmes(locacao.getFilmes());
        novaLocacao.setDataLocacao(new Date());
        novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
        novaLocacao.setValor(locacao.getValor() * dias);
        dao.salvar(novaLocacao);
    }
}