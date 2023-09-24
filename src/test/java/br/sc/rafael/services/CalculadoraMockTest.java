package br.sc.rafael.services;

import br.sc.rafael.entities.Locacao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        Mockito.when(calcMock.somar(1, 2)).thenReturn(6);
        Mockito.when(calcSpy.somar(1, 2)).thenReturn(8);

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));
    }

    @Test
    public void teste() {
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

        Assert.assertEquals(5, calc.somar(1, 100000));
    }
}
