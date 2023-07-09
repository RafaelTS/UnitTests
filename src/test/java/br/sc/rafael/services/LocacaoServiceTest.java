package br.sc.rafael.services;

import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;
import br.sc.rafael.exceptions.FilmeSemEstoqueException;
import br.sc.rafael.exceptions.LocadoraException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    public void deveAlugarFilmeComSucesso() throws Exception {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List <Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

            //verificacao
            error.checkThat(locacao.getValor(), is(equalTo(5.0)));
            error.checkThat(locacao.getValor(), is(not(3.0)));
            error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
            error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void deveLancarExccaoAoAlugarFilmeSemEstoque() throws Exception {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List <Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

        List <Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

        try {
            service.alugarFilme(null, filmes);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);

    }

    @Test
    public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0));

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(resultado.getValor(), is(11.0));

    }

    @Test
    public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(resultado.getValor(), is(13.0));

    }

    @Test
    public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                                           new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0),
                                           new Filme("Filme 5", 2, 4.0));

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(resultado.getValor(), is(14.0));

    }
    @Test
    public void devePagar0PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                                           new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0),
                                           new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0));

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(resultado.getValor(), is(14.0));

    }

}
