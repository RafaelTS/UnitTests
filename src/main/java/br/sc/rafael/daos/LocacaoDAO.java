package br.sc.rafael.daos;

import br.sc.rafael.entities.Locacao;

import java.util.List;

public interface LocacaoDAO {

    public void salvar(Locacao locacao);

    public List<Locacao> obterLocacoesPendentes();
}
