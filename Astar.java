package astar;

/**
* Universidad Del Valle 
* Pablo Díaz 13203
* Adolfo Morales 13014
* Jorge García
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class Astar {

    private final Grafo grafo;
    private final Nodo inicio;
    private final Nodo destino;
    private final List<Nodo> path = new ArrayList<>();
    private  Set<Nodo> nodosEvaluados;
    
    public Astar(int ancho, int alto, int[] arregloDistri) 
    {
        this.grafo = new Grafo(ancho, alto, arregloDistri);
        this.inicio = new Nodo(0, alto-1, grafo);
        this.destino = new Nodo(ancho-1, 0, grafo);
        
        
    }

    public void calcular(boolean diagonales,boolean dijkstra,boolean greedy) 
    {
        nodosEvaluados= new HashSet<>(); //set de nodos evaluados
        PriorityQueue<Nodo> nodosPorEvaluar = new PriorityQueue<>();//set de nodos por evaluar, contiene inicialmente al nodo inicial

        inicio.setFuncionG(0); //costo desde el inicio hasta el mejor camino conocido
        
        nodosPorEvaluar.add(inicio); //se evalúa el primer nodo
        
        int contadorIteraciones=0; //contador de iteraciones de algoritmo
        while (!nodosPorEvaluar.isEmpty()) {
            
            Nodo actual = nodosPorEvaluar.poll();//obtener el nodo con menor funcion f

            if (actual.equals(destino)) //en caso de encontrar el destino, termina el algoritmo
            {  
                System.out.println("Iteraciones totales-> " + contadorIteraciones);
                System.out.println("Costo Total-> " + actual.getFuncionG()+actual.getFuncionHeuristica());
                reconstruirCamino(actual);//se muestra el nuevo camino
                break;
            }
            //cambio de estructura para evaluar el nodo actual
            nodosPorEvaluar.remove(actual);
            nodosEvaluados.add(actual);

            //función obtiene los nodos adyacentes del nodo actual
            for (Nodo adyacente : actual.getNodosAdyacente(diagonales)) {
            
                boolean adyacenteIsMejor;
                //en caso de que un nodo ya haya sido evaluado
                //se omite del ciclo
                if (nodosEvaluados.contains(adyacente))
                    continue; //se salta una iteracion
                
                //mientras no sea un obstaculo el nodo
                if (!adyacente.isObstaculo()) {
                    //costo real del nodo adyacente
                    double nuevoCosto = actual.getFuncionG() + getDistanciaEntre(actual, adyacente);
                    //se evalua si el nodoa adyacente tiene un menor costo
                    
                    if (!nodosPorEvaluar.contains(adyacente)||nuevoCosto < adyacente.getFuncionG()){
                        adyacente.setRaiz(actual); //añadir el camino nuevo
                        adyacente.setFuncionG(nuevoCosto); 
                        adyacente.setFuncionHeuristica(calcularHeuristica(adyacente, destino,diagonales));
                        if (dijkstra)//si es dijsktra la heuristica es cero
                            adyacente.setFuncionHeuristica(0);
                        if (greedy)
                            adyacente.setFuncionG(0);
                        //la cola prioritaria ordena los nodos cada vez que se inserta un nodo
                        //el nodo con menor funcion f es el primero
                        nodosPorEvaluar.add(adyacente);//se añade hasta de último el nodos adyacente
                    }
                   
                }
            }//cierra for adyacente
            contadorIteraciones++;
        }//cierra while
    }//cierra calcular

    //método para mostrar el camino más corto encontrado
    public void reconstruirCamino(Nodo nodo)
    {
        grafo.getGrafoGrafico(); //mostrar grafo en terminal
        while (!(nodo.getRaiz() == null)) {
            path.add(nodo);
            nodo = nodo.getRaiz();
        }
        path.add(nodo);//agregar el último nodo
        Collections.reverse(path); //cambiar el orden
        System.out.println("");
        System.out.println(path.toString() + " ->Camino más corto");
        
    }
    //distancia REAL entre nodos
    public double getDistanciaEntre(Nodo n1, Nodo n2) {
        if ((n1.getX() == n2.getX() ) || (n1.getY() == n2.getY()))
            return 1; //si estan a a la par el costo es constante
        else
            return Math.sqrt(2); //en otro caso estan en diagonal, costo = raiz de 2
    }
    
    
//referencia: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#diagonal-distance
    public double calcularHeuristica(Nodo current, Nodo goal, boolean diagonales) {
       
        double D = 1.0; //peso de aristas adyacentes
        double D2 = Math.sqrt(2); //peso de arista diagonales
        //manhattan de 4
        double dx = Math.abs(current.getX()-goal.getX());
        double dy= Math.abs(current.getY()-goal.getY());
        //cross breaking ties
        double dx1 = current.getX()-goal.getX();
        double dy1 = current.getY()-goal.getY();
        double p = 1/1000;//minimum cost of taking one step/expected maximum path length
        double dw = inicio.getX()-goal.getX(); //cross heuristics
        double dz = inicio.getY()-goal.getY(); //cross heuristics
    
        //se realiza un promedio de distancia manhattan de 8 movimientos y 4 movimientos
        double promedio = ((D*(dx+dy)+(D2-2*D)*Math.min(dx,dy))+(D*(dx+dy)))/2;
        if (diagonales)
            return promedio;//normal heuristics
        return (D*(dx+dy))+Math.abs(dx1*dz-dw*dy1); //normal heuristic + cross breaking ties
       
    }

    //método devuelve los nodos evaluados, para mostrar graficamente
    public Set<Nodo> getNodosEvaluados() {
        return nodosEvaluados;
    }

    public Grafo getGrafo() {
        return grafo;
    }
    //método para mostrar graficamente el camino encontrado
    public List<Nodo> getPath() {
        return path;
    }
}

