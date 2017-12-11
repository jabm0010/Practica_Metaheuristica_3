/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.Memetico;

import static Algoritmos.Memetico.Hibrido.*;
import static Utils.Utilidades.*;
import Utils.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;
/**
 *
 * @author ptondreau
 */
public final class Generacional {

    final int numParejas = 7;
    final int numIndividuos = Hibrido.numIndividuos;
    
    List<List<Integer>> frecuencias = Hibrido.frecuencias;
    List<Integer> transmisores = Hibrido.transmisores;
    Restricciones restricciones = Hibrido.restricciones;

    List<Integer> resultado = Hibrido.resultado;
    List<List<Integer>> padres = Hibrido.padres;
    List<List<Integer>> hijos = Hibrido.hijos;
    
     int numEstancamiento = 0;
    int idMejorResult;
    int mejorResult = Integer.MAX_VALUE;
    int idMutado;

    public Generacional () throws FileNotFoundException {
        
       for ( int i = 0; i < numIndividuos; i ++ ) {
            construccionInicial(i);


            System.out.println("Greedy Inicial");

            System.out.println(resultado.get(i));
            //System.out.println(resultado.get(i));
            //Llamada a la búsqueda local inicial
            BusquedaLocal bl=new BusquedaLocal(i);
            bl.algoritmo();

            padres.set(i,bl.getFrecuenciasR());
 
            resultado.set(i, bl.getResultado());
            System.out.println(resultado.get(i));

//            
        }
       
    }
    
//    public Generacional ( listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest ) {
//        frecuencias = _frecuencias.rangoFrecuencias;
//        transmisores = _transmisores.transmisores;
//        restricciones = _rest;
//
//        for ( int i = 0; i < numIndividuos; i ++ ) {
//            padres.add(new ArrayList<>());
//        }
//
//    }

    void algoritmo () throws FileNotFoundException {
        
        numGeneraciones = 0;
        //Loop hasta 20000 evaluaciones
        while( numGeneraciones < 10 ) {
           // System.out.println(numGeneraciones);
            generarHijos();
           // System.out.println("Padres seleccionados");
            cruzarIndividuos();
            
           // System.out.println("Individuos cruzados ");
            mutarIndividuos();
         //   System.out.println("Hijos mutados ");
            nuevaGeneracion();
         //   System.out.println("Nueva gene ");
//           for(int i=0;i<transmisores.size();i++){
//           System.out.println( padres.get(0).get(i));
//        }
        }
    }
    
    void construccionInicial ( int id ) throws FileNotFoundException {
        
        List <Integer> frecuenciasR = new ArrayList<>();
        Random numero = NUMERO;
        
        for ( int i = 0; i < transmisores.size(); i++ ) {
            int indiceFrecAleatorio = numero.nextInt(frecuencias.get(transmisores.get(i)).size());
            int frecAleatoria = frecuencias.get(transmisores.get(i)).get(indiceFrecAleatorio);

            frecuenciasR.add(frecAleatoria);
        }
        
        resultado.set(id, rDiferencia(frecuenciasR, restricciones));
        padres.get(id).addAll(frecuenciasR);
        frecuenciasR.clear();
        
    }

    void generarHijos () {
        for ( int i = 0; i < numIndividuos; i ++ ) {
            Random numero = NUMERO;
            int seleccionado = numero.nextInt(numIndividuos);

            Random numero2 = NUMERO;
            int seleccionado2 = numero.nextInt(numIndividuos);
            
            //Añadido mecanismo para evitar que se repita el mismo padre
//            while(seleccionado2==seleccionado){
//                seleccionado2=numero.nextInt(numIndividuos);
//            }

            if ( resultado.get(seleccionado) < resultado.get(seleccionado2) ) {
                hijos.add(i, padres.get(seleccionado));

            } else {
                hijos.add(i, padres.get(seleccionado2));
            }
        }
    }

    void cruzarIndividuos () {
        int cont = 0;
        int numParejasActual=0;
        while( numParejasActual < numParejas ) {
          //  System.out.println("Individuo1 "+cont);
            int individuo1 = cont;
            
            cont++;
           // System.out.println("Individuo1 "+cont);
            int individuo2= cont;
            algBX(individuo1, individuo2);
            cont ++;
          //  System.out.println("Num parejas"+numParejasActual); 
            numParejasActual++;
           
        }

    }
    
     void algCruce2Puntos ( int individuo1, int individuo2 ) {
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());

        Random numero2 = NUMERO;
        int seleccionado2 = numero.nextInt(transmisores.size());

        if ( seleccionado2 < seleccionado ) {
            int temp = seleccionado;
            seleccionado = seleccionado2;
            seleccionado2 = temp;

        }

        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();

        //Primer cruce
        solucion1.addAll(0, hijos.get(individuo1).subList(0, seleccionado));
        solucion1.addAll(seleccionado, hijos.get(individuo2).subList(seleccionado, seleccionado2));
        solucion1.addAll(seleccionado2, hijos.get(individuo1).subList(seleccionado2, transmisores.size()));
        hijos.set(individuo1, solucion1);

        //Segundo cruce
        solucion2.addAll(0, hijos.get(individuo2).subList(0, seleccionado));
        solucion2.addAll(seleccionado, hijos.get(individuo1).subList(seleccionado, seleccionado2));
        solucion2.addAll(seleccionado2, hijos.get(individuo2).subList(seleccionado2, transmisores.size()));
        hijos.set(individuo2, solucion2);

    }

    // No estoy seguro de si habría que hacerla así.
    final double alfa = 0.5;
    void mutarIndividuos () {
        //Mutamos solo un individuo

        //Seleccionamos el individuo a mutar
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(numIndividuos);

        int transmisorMut = numero.nextInt(transmisores.size());
        int frecAsociada = transmisores.get(transmisorMut);

        int frecuenciaMut = numero.nextInt(frecuencias.get(frecAsociada).size());
        hijos.get(seleccionado).set(transmisorMut, frecuencias.get(frecAsociada).get(frecuenciaMut));

        idMutado = seleccionado;

    }
    


    void algBX ( int individuo1, int individuo2 ) {
        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();

        for ( int i = 0; i < transmisores.size(); i ++ ) {
            
                int d = Math.abs(hijos.get(individuo1).get(i) - hijos.get(individuo2).get(i));
               // System.out.println(hijos.get(individuo1).get(i));
               // System.out.println(hijos.get(individuo2).get(i));
                
                int cmin = Integer.MAX_VALUE;
                int cmax = Integer.MIN_VALUE;

                if ( hijos.get(individuo1).get(i) < hijos.get(individuo2).get(i) ) {
                    cmin = hijos.get(individuo1).get(i);
                } else {
                    cmin = hijos.get(individuo2).get(i);
                }

                if ( hijos.get(individuo1).get(i) > hijos.get(individuo2).get(i) ) {
                    cmax = hijos.get(individuo1).get(i);
                } else {
                    cmax = hijos.get(individuo2).get(i);
                }
              //  System.out.println(cmax);
              //  System.out.println(cmin);

                int vmin = (int) (cmin - d * alfa);
                int vmax = (int) (cmax + d * alfa);

                int frecAsociada = transmisores.get(i);
                System.out.println("Vmax:"+vmax);
                System.out.println("Vmin:"+vmin);
                //Para la solución 1
                Random numero = NUMERO;
                int valorObtenido = numero.nextInt(vmax+1)+vmin;
                int minimaDiferencia = Integer.MAX_VALUE;
                int frecuenciaFinal = 0;
                System.out.println(valorObtenido);
                for ( int j = 0; j < frecuencias.get(frecAsociada).size(); j ++ ) {
                    if ( Math.abs(valorObtenido - frecuencias.get(frecAsociada).get(j)) < minimaDiferencia ) {
                        minimaDiferencia = Math.abs(valorObtenido - frecuencias.get(frecAsociada).get(j));
                        frecuenciaFinal = frecuencias.get(frecAsociada).get(j);
                    }
                }
                System.out.println(frecuenciaFinal);
                solucion1.add(i, frecuenciaFinal);

                //Para la solución 2
                int valorObtenido2 = numero.nextInt(vmax+1)+vmin;
                int minimaDiferencia2 = Integer.MAX_VALUE;
                int frecuenciaFinal2 = 0;

                for ( int j = 0; j < frecuencias.get(frecAsociada).size(); j ++ ) {
                    if ( Math.abs(valorObtenido2 - frecuencias.get(frecAsociada).get(j)) < minimaDiferencia2 ) {
                        minimaDiferencia2 = Math.abs(valorObtenido2 - frecuencias.get(frecAsociada).get(j));
                        frecuenciaFinal2 = frecuencias.get(frecAsociada).get(j);
                    }
                }

                solucion2.add(i, frecuenciaFinal2);
        }
            hijos.set(individuo1, solucion1);
            hijos.set(individuo2, solucion2);
    }
    
    public void nuevaGeneracion () throws FileNotFoundException {
        //Elitismo

        int aux = mejorResult;
        int resultadoHijos[] = new int[ numIndividuos ];
        //Buscamos el peor individuo de la generación de padres
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for ( int i = 0; i < numIndividuos; i ++ ) {
            if ( resultado.get(i) < minimo ) {
                minimo = resultado.get(i);
                actual = i;
            }
        }

        List<Integer> mejorIndividuo = padres.get(actual);
        
        /* Modificar todo esto, hay menos individuos a evaluar a cada iteracion */
        // Evaluamos los hijos
        // Evaluamos los hijos
        if ( idMutado <= 14 ) {
            resultadoHijos = evaluar(hijos, 14);
            numEvaluaciones += 14;
        } else {
            resultadoHijos = evaluar(hijos, idMutado);
            numEvaluaciones += 15;
        }

        for ( int i = 14; i < numIndividuos; i ++ ) {
            if ( i != idMutado ) {
                resultadoHijos[ i ] = resultado.get(i);
            }
        }

        //Buscamos el hijo con el mayor coste
        int maximo = Integer.MIN_VALUE;
        int actual2 = 0;

        for ( int i = 0; i < numIndividuos; i ++ ) {
            if ( resultadoHijos[ i ] > maximo ) {
                maximo = resultadoHijos[ i ];
                actual2 = i;
            }
        }

        //Si el menor de los padres tiene menor coste que el mayor de los hijos se reemplaza
        if ( minimo < maximo ) {
            hijos.set(actual2, mejorIndividuo);
            resultadoHijos[ actual2 ] = minimo;

        }

        //Los hijos serán los padres para la siguiente generación
        padres.clear();
        padres.addAll(hijos);
        hijos.clear();

        for ( int i = 0; i < resultado.size(); i ++ ) {
            resultado.set(i, resultadoHijos[ i ]); //rDiferencia(padres.get(i), restricciones);
            if ( resultado.get(i) < mejorResult ) {
                mejorResult = resultado.get(i);
                idMejorResult = i;
            }
        }

        if ( aux == mejorResult ) {
            numEstancamiento ++;
        } else {
            
            numEstancamiento = 0;
        }
      //  System.out.println(numEstancamiento);

        if ( numEstancamiento >= 20 || comprobarConvergencia() ) {
      //      System.out.println("reinicializacion");
            reinicializacion();
            numEstancamiento = 0;
        }
        
        
        numGeneraciones++;
    }

    public int[] evaluar ( List<List<Integer>> individuos, int mutado ) throws FileNotFoundException {
        int[] resultados = new int[ numIndividuos ];

        for ( int i = 0; i < 14; i ++ ) {
            resultados[ i ] = rDiferencia(individuos.get(i), restricciones);
        }
        if ( idMutado >= 14 ) {
            resultados[ mutado ] = rDiferencia(individuos.get(mutado), restricciones);
        }

        return resultados;
    }

    private void reinicializacion () throws FileNotFoundException {
        List<Integer> mejorSolucion = new ArrayList();
        mejorSolucion.addAll(padres.get(idMejorResult));
        padres.clear();
        hijos.clear();

        for ( int i = 0; i < numIndividuos; i ++ ) {
            padres.add(new ArrayList<>());
        }

        padres.set(0, mejorSolucion);
        resultado.set(0, mejorResult);

        for ( int i = 1; i < numIndividuos; i ++ ) {
            construccionInicial(i);
        }

    }

    private boolean comprobarConvergencia () {

        List<Integer> auxiliar = new ArrayList<>();
        auxiliar.addAll(resultado);
        Collections.sort(auxiliar);

        int contador = 1;
        boolean convergencia = false;
        int maximo = Integer.MIN_VALUE;

        for ( int i = 1; i < auxiliar.size(); i ++ ) {
            if ( contador >= 16 ) {
                convergencia = true;
                break;
            }
            if ( auxiliar.get(i) == (auxiliar.get(i - 1)) ) {
                contador ++;
            } else {
                contador = 1;
            }
        }

        // Preguntar al profesor puesto que añade complejidad
        if ( convergencia ) {

            List<List<Integer>> auxiliarP = new ArrayList<>();

            for ( int i = 0; i < padres.size(); i ++ ) {
                auxiliarP.add(new ArrayList<>());
                auxiliarP.get(i).addAll(padres.get(i));
                Collections.sort(auxiliarP.get(i));
            }

            contador = 1;
            convergencia = false;
            for ( int i = 1; i < auxiliarP.size(); i ++ ) {
                if ( contador >= 16 ) {
                    convergencia = true;
                    break;
                }
                if ( auxiliarP.get(i).equals(auxiliarP.get(i - 1)) ) {
                    contador ++;
                } else {
                    contador = 1;
                }
            }
        }

        return convergencia;
    } 
    
    public void resMejorIndividuo () throws FileNotFoundException {
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for ( int i = 0; i < numIndividuos; i ++ ) {
            if ( resultado.get(i) < minimo ) {
                minimo = resultado.get(i);
                actual = i;
            }
        }
        List<Integer> mejorIndividuo = padres.get(actual);

        for ( int i = 0; i < mejorIndividuo.size(); i ++ ) {
            if ( mejorIndividuo.get(i) != 0 ) {
                System.out.println("Transmisor " + (i + 1) + ": " + mejorIndividuo.get(i));
            }
        }

        System.out.println(resultado.get(actual));
    }
    
    public int resultadoFinal () {
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for ( int i = 0; i < numIndividuos; i ++ ) {
            if ( resultado.get(i) < minimo ) {
                minimo = resultado.get(i);
                actual = i;
            }
        }
        List<Integer> mejorIndividuo = padres.get(actual);
        
        return resultado.get(actual);
    }
}
