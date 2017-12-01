/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.BL;

import Utils.*;
import static Utils.Utilidades.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author pierrest
 */
public class BusquedaLocal {

    List<List<Integer>> frecuencias = new ArrayList<>();
    List<Integer> transmisores = new ArrayList<>();
    List<Integer> frecuenciasR = new ArrayList<>(); // Cada posicion es la frecuencia asignada a dicho transmisor
    Restricciones rest;
    int resultado;

    public BusquedaLocal ( listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _restricciones ) {
        frecuencias = _frecuencias.rangoFrecuencias;
        transmisores = _transmisores.transmisores;
        rest = _restricciones;
        resultado = Integer.MAX_VALUE;
        for ( int i = 0; i < transmisores.size(); i ++ ) {
            frecuenciasR.add(frecuencias.get(transmisores.get(i)).get(NUMERO.nextInt(frecuencias.get(transmisores.get(i)).size())));
        }
    }

    /**
     * Algoritmo greedy: Asignar un valor al transmisor de forma iterativa e ir
     * calculando uno por uno. Si el resultado mejora sustituir la lista de
     * solución
     *
     */
    public void algoritmo () throws FileNotFoundException {
        if ( resultado == Integer.MAX_VALUE ) {
            int result = rDiferencia(frecuenciasR, rest); // Da lugar a la solucion inicial
            if ( resultado > result ) {
                resultado = result;
            }
            algoritmo();
        } else {
            Random numero = NUMERO;
            int token = numero.nextInt(transmisores.size());
            for ( int i = 0; i < 10000; i ++ ) {
                double sentido = numero.nextDouble();
                int valorInicial = frecuenciasR.get(token); // Se obtiene la frecuencia del token
                int indiceInicial;
                int nuevoCoste = Integer.MAX_VALUE;

                indiceInicial = frecuencias.get(transmisores.get(token)).indexOf(valorInicial); // Mas corto que codigo de abajo

                if ( sentido < 0.5 ) {
                    boolean encontrado = false;
                    while( indiceInicial >= 0 &&  ! encontrado ) {
                        int fact1 = rDiferencia(frecuenciasR, token, rest);
                        valorInicial = frecuencias.get(transmisores.get(token)).get(indiceInicial);
                        List<Integer> nuevaSolucion = new ArrayList<>();
                        nuevaSolucion.addAll(frecuenciasR);
                        nuevaSolucion.set(token, valorInicial);
                        int fact2 = rDiferencia(nuevaSolucion, token, rest);
                        nuevoCoste = resultado + (fact2 - fact1);

                        if ( nuevoCoste < resultado ) {
                            frecuenciasR.set(token, valorInicial);
                            resultado = nuevoCoste;
                            encontrado = true;
                        }
                        indiceInicial --;
                    }
                } else {
                    boolean encontrado = false;
                    while( indiceInicial < frecuencias.get(transmisores.get(token)).size() &&  ! encontrado ) {
                        int fact1 = rDiferencia(frecuenciasR, token, rest);
                        valorInicial = frecuencias.get(transmisores.get(token)).get(indiceInicial);
                        List<Integer> nuevaSolucion = new ArrayList<>();
                        nuevaSolucion.addAll(frecuenciasR);
                        nuevaSolucion.set(token, valorInicial);
                        int fact2 = rDiferencia(nuevaSolucion, token, rest);
                        nuevoCoste = resultado + (fact2 - fact1);

                        if ( nuevoCoste < resultado ) {
                            frecuenciasR.set(token, valorInicial);
                            resultado = nuevoCoste;
                            encontrado = true;
                        }
                        indiceInicial ++;
                    }
                }
                token = Math.floorMod(token + 1, transmisores.size());
            }
        }
    }

    /**
     * Función que devuelve por pantalla los resultados obtenidos
     */
    public void resultados () throws FileNotFoundException {

        List<List<Integer>> listaTrans = new ArrayList<>();
        for ( int i = 0; i < transmisores.size(); i ++ ) {
            listaTrans = rest.restriccionesTransmisor(i);
            if ( listaTrans.size() > 0 ) {
                System.out.println("Transmisor " + (i + 1) + ": " + frecuenciasR.get(i));
            }
        }
        System.out.println("Coste: " + rDiferencia(frecuenciasR, rest));
    }

}
