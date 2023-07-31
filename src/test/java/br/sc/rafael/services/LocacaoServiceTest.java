package br.sc.rafael.services;

import br.sc.rafael.Utils.DataUtils;
import br.sc.rafael.builders.FilmeBuilder;
import br.sc.rafael.builders.LocacaoBuilder;
import br.sc.rafael.builders.UsuarioBuilder;
import br.sc.rafael.daos.LocacaoDAO;
import br.sc.rafael.daos.LocacaoDAOFake;
import br.sc.rafael.entities.Filme;
import br.sc.rafael.entities.Locacao;
import br.sc.rafael.entities.Usuario;
import br.sc.rafael.exceptions.FilmeSemEstoqueException;
import br.sc.rafael.exceptions.LocadoraException;
import br.sc.rafael.matchers.DiaSemanaMatcher;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.sc.rafael.Utils.DataUtils.*;
import static br.sc.rafael.builders.FilmeBuilder.umFilme;
import static br.sc.rafael.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.sc.rafael.builders.LocacaoBuilder.umaLocacao;
import static br.sc.rafael.builders.UsuarioBuilder.umUsuario;
import static br.sc.rafael.matchers.MyMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocacaoServiceTest {

    private LocacaoService service;
    private SPCService spc;
    private LocacaoDAO dao;
    private EmailService emailService;
    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
        dao = Mockito.mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);
        spc = Mockito.mock(SPCService.class);
        service.setSpcService(spc);
        emailService = Mockito.mock(EmailService.class);
        service.setEmailService(emailService);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void deveAlugarFilmeComSucesso() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().agora();
        List <Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

            //verificacao
            error.checkThat(locacao.getValor(), is(equalTo(5.0)));
            error.checkThat(locacao.getValor(), is(not(3.0)));
            error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
            error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
            //NEW MATCHERS
            error.checkThat(locacao.getDataLocacao(), ehHoje());
            error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void deveLancarExccaoAoAlugarFilmeSemEstoque() throws Exception {

        //cenario
        Usuario usuario = umUsuario().agora();
        //Linha abaixo está comentada apenas para manter um outro exemplo
        //List <Filme> filmes = Arrays.asList(umFilme().semEstoque().agora());

        List <Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

        List <Filme> filmes = Arrays.asList(umFilme().agora());

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
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);

    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = umUsuario().agora();
        List <Filme> filmes = Arrays.asList(umFilme().agora());

        Locacao retorno = service.alugarFilme(usuario,filmes);

        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        Assert.assertTrue(ehSegunda);

        //OU USANDO PELO MATCHER CRIADO
        Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));

        //or specifing myMatcher
        Assert.assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());

    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
        //cenário
        Usuario usuario = umUsuario().agora();
        List <Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenReturn(true);

        //acao
        try {
            service.alugarFilme(usuario, filmes);
        //verificacao
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuário negativado"));
        }

        Mockito.verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        //cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuário em dia").agora();
        List<Locacao> locacoes = Arrays.asList(
                            umaLocacao().atrasada().comUsuario(usuario).agora(),
                            umaLocacao().comUsuario(usuario2).agora());

        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //acao
        service.notificarAtrasos();

        //verificacao
        Mockito.verify(emailService).notificarAtraso(usuario);
    }
}
