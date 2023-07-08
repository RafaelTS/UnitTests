package br.sc.rafael.services;

import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;
import br.sc.rafael.exceptions.FilmeSemEstoqueException;
import br.sc.rafael.exceptions.LocadoraException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static br.sc.rafael.Utils.DataUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    private LocacaoService service;
    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testeLocacao() throws Exception {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme =  new Filme("Filme 1", 2, 5.0);

        //acao
        Locacao locacao = service.alugarFilme(usuario, filme);

            //verificacao
            error.checkThat(locacao.getValor(), is(equalTo(5.0)));
            error.checkThat(locacao.getValor(), is(not(3.0)));
            error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
            error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void testeLocacao_filmeSemEstoque() throws Exception {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 0, 5.0);

        service.alugarFilme(usuario, filme);
    }

    @Test
    public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {

        Filme filme = new Filme("Filme 1", 1, 5.0);

        try {
            service.alugarFilme(null, filme);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test
    public void testLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);

    }

}
