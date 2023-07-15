package br.sc.rafael.suites;

import br.sc.rafael.services.CalculadoraTest;
import br.sc.rafael.services.CalculoValorLocacaoTest;
import br.sc.rafael.services.LocacaoServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {

}
