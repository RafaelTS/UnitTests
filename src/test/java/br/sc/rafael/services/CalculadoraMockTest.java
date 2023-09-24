package br.sc.rafael.services;

import br.sc.rafael.entities.Locacao;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste() {
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

        Assert.assertEquals(5, calc.somar(1, 100000));
    }
}
