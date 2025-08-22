package com.susanayuque.screenmatch.principal;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Ejemplos {

    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Bruce","Madeleyna","Elena","Jefferson","Ledison","Susan");
        nombres.stream()
                .sorted()
                .limit(3)
                .filter(n->n.startsWith("B"))
                .map(n->n.toUpperCase())
                .forEach(System.out::println);
    }

    public void muestraEjemplo2(){
        List<Integer> numeros = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
        Set<Integer> numerosPares = numeros.stream().filter(n->n % 2 == 0).collect(Collectors.toSet());
        System.out.println(numerosPares);
    }

    public void muestraEjemplo3(){
        List<Integer> numeros = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
        List<Integer> numerosPares = numeros.stream().filter(n->n % 2 == 0).collect(Collectors.toList());
        System.out.println(numerosPares);
    }

}
