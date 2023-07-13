package br.sc.rafael.services;

import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;
import br.sc.rafael.exceptions.FilmeSemEstoqueException;
import br.sc.rafael.exceptions.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private  LocacaoService service;
    @Parameterized.Parameter
    public List <Filme> filmes;
    @Parameterized.Parameter(value=1)
    public Double valorLocacao;

    private static Filme filme1 = new Filme("Filme 1", 2, 4.0);
    private static Filme filme2 = new Filme("Filme 2", 2, 4.0);
    private static Filme filme3 = new Filme("Filme 3", 2, 4.0);
    private static Filme filme4 = new Filme("Filme 4", 2, 4.0);
    private static Filme filme5 = new Filme("Filme 5", 2, 4.0);
    private static Filme filme6 = new Filme("Filme 6", 2, 4.0);

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getPametros() {
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1, filme2, filme3), 11.0},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13.0},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0},

        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(resultado.getValor(), is(valorLocacao));
        

    }

}
