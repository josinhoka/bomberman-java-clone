package core;
import modelo.*; // Importamos todas las clases de la carpeta modelo
import vista.VistaJuego;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // 1. Creamos la base de datos del juego (El Modelo)
        GestorTablero mapa = new GestorTablero(15, 15);
        mapa.generarBordesOptimizados();

        //Creamos y agregamos el jugados al mapa.
        Jugador bomberman = new Jugador(1, 1, mapa);
        mapa.setJugador(bomberman);
        //////////////////////////////////////
        //mapa.agregarElemento(bomberman);
        
        // 2. Creamos el pintor y le inyectamos el mapa (La Vista)
        VistaJuego vista = new VistaJuego(mapa, bomberman);
        
        // 3. Configuramos la ventana de escritorio (JFrame)
        JFrame ventana = new JFrame("Mi Bomberman POO");
        
        // Es vital que el programa de terminal muera al cerrar la cruz de la ventana
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        // No dejamos que el usuario estire la pantalla y rompa nuestra matriz
        ventana.setResizable(false); 
        
        // Añadimos el lienzo a la ventana
        ventana.add(vista);
        
        // Ajustamos el tamaño de la ventana a lo que ocupa el mapa (15x15 casillas de 40px)
        // El +30 en el alto compensa el grosor de la barra superior de la ventana del sistema operativo
        ventana.setSize(15 * 40, (15 * 40) + 30); 
        
        // La centramos en la pantalla y la hacemos visible
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);


        // La centramos en la pantalla y la hacemos visible
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        
        // ¡Arrancamos el Game Loop!
        //vista.iniciarJuego();
        vista.iniciarJuego();
    }
}