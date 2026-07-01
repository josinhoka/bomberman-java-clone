package vista;
import modelo.*;
import items.*;
//import items.Puerta;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

// Importamos las herramientas de eventos de teclado
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.Font;


// librerias para imagenes
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;

import core.EstadoJuego;

public class VistaJuego extends JPanel implements KeyListener, Runnable {
    
    // Añadimos una variable para guardar nuestro motor
    private Thread hiloJuego;
    // El pintor necesita ver el mapa para saber qué dibujar
    private GestorTablero mapa; 
    private Jugador jugador;
    // Nuestra escala: cada casilla ocupará 40x40 píxeles
    private final int TAMANO_CELDA = 40; 

    // 1. Declaramos la variable para la imagen en RAM
    private Image spriteJugador;
    private Image spriteBomba;

    private Image spriteMuroDeAcero;
    private Image spriteMuroDestructible;

    private BufferedImage spriteSheetJugador;

    // Variable que gobernará todo el motor
    private EstadoJuego estadoActual;
    private int nivelActual = 1; // Controla en qué stage estamos
    private final int MAX_NIVELES = 2;


    public VistaJuego(GestorTablero mapa, Jugador jugador) {
        this.mapa = mapa;
        this.jugador = jugador;
        
        // INICIALIZAMOS: El juego arranca en el menú principal
        this.estadoActual = EstadoJuego.MENU_PRINCIPAL;

        // 2. Cargamos el PNG desde el disco duro a la RAM
        try {
            this.spriteJugador = ImageIO.read(new File("recursos/jugador.png"));
        } catch (IOException e) {
            System.out.println("¡Error! No se ha encontrado la imagen jugador.png");
            e.printStackTrace();
        }
        /////////////////////////
         // 2.1 Cargamos el PNG desde el disco duro a la RAM --> Lo mismo pero con la bomba
        try {
            this.spriteBomba= ImageIO.read(new File("recursos/bomba.png"));
        } catch (IOException e) {
            System.out.println("¡Error! No se ha encontrado la imagen bomba.png");
            e.printStackTrace();
        }
        try {
            this.spriteMuroDeAcero= ImageIO.read(new File("recursos/MuroDeAcero.png"));
        } catch (IOException e) {
            System.out.println("¡Error! No se ha encontrado la imagen del Muro de Acero.png");
            e.printStackTrace();
        }
        try {
            this.spriteMuroDestructible= ImageIO.read(new File("recursos/MuroDestructible.png"));
        } catch (IOException e) {
            System.out.println("¡Error! No se ha encontrado la imagen del Muro Destructible.png");
            e.printStackTrace();
        }
        /////////////////////////

        this.setFocusable(true);
        this.addKeyListener(this);
    

        // TRAMPA CLÁSICA DE SWING: 
        // Si no le decimos al panel que puede tener el "foco" del sistema operativo,
        // ignorará todas las teclas que pulsemos.
        this.setFocusable(true);

        // Nos suscribimos a nosotros mismos para escuchar los eventos de teclado
        this.addKeyListener(this);

        try {
            // Te pedirá que busques un spritesheet real en Google y lo guardes aquí
            this.spriteSheetJugador = ImageIO.read(new File("recursos/spritesheet_bomberman.png"));
        } catch (IOException e) {};

    }

    public void iniciarJuego() {
        // Fabricamos el hilo pasándole esta misma clase (this) como la tarea a ejecutar
        hiloJuego = new Thread(this);
        // ¡Encendemos el motor! Esto llama automáticamente al método run() en segundo plano
        hiloJuego.start(); 
    }

    // Al implementar Runnable, Java nos obliga a escribir el método public void run()
    @Override
    public void run() {
        // Bucle infinito: el corazón del juego
        while (true) {

            if (this.estadoActual == EstadoJuego.JUGANDO){
                this.mapa.actualizarMapa(); 

                // Comprobamos victoria
                if (this.mapa.isNivelSuperado()) {
                    // Aquí podrías sumar 1 a una variable nivelActual para que sea dinámico
                    System.out.println("Cargando la Stage 2...");
                    //this.mapa.cargarNivel(2);

                    this.mapa.setNivelSuperado(false); // Reseteamos la bandera
                    this.nivelActual++; // Sumamos 1 al nivel
                    if (this.nivelActual > MAX_NIVELES){
                        this.estadoActual = EstadoJuego.VICTORIA; // Llegaste al final del juego
                    } else {
                        this.estadoActual = EstadoJuego.TRANSICION_NIVEL; // Frenamos el juego
                    }
                }
                // Comprobamos si el motor del mapa se ha quedado sin tiempo
                else if (this.mapa.getTicksPartida() <= 0 || !this.jugador.isVivo()) {
                    System.out.println("Condición de fin de partida detectada.");
                    this.estadoActual = EstadoJuego.GAME_OVER;
                }

            }

            // Si estamos en MENU o GAME_OVER, el mapa simplemente se queda congelado

            // RENDER: Dibujamos en pantalla SIEMPRE (menú, juego o fin)
            
            // 1. UPDATE (Actualizar la lógica matemática)
            // Le decimos al mapa que envejezca a las bombas
            
            // 2. RENDER (Dibujar en pantalla)
            // Forzamos a que se llame a paintComponent()
            repaint(); 

            // 3. SLEEP (Descansar)
            // Si no dormimos al hilo, consumirá el 100% de tu CPU y el juego irá a velocidad hiperespacio.
            try {
                // Dormimos el hilo unos 16 milisegundos para lograr los ~60 FPS
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////// --- MÉTODOS OBLIGATORIOS DEL KEYLISTENER ---

    @Override
    public void keyTyped(KeyEvent e) { 
        // Lo dejamos vacío, no lo usamos
    }

    @Override
    public void keyReleased(KeyEvent e) { 
        // Lo dejamos vacío de momento
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // --- 1. Controles si estamos en el Menú ---
        if (estadoActual == EstadoJuego.MENU_PRINCIPAL) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                this.mapa.cargarNivel(1);
                System.out.println("Cambiando a estado: JUGANDO");
                estadoActual = EstadoJuego.JUGANDO;
                // (Aquí cargaremos el Nivel 1 más adelante)
            }
            return; // Bloqueamos el resto del teclado (mover, bombas)
        }

        // --- NUEVO: Controles en la Pantalla de Stage ---
        if (estadoActual == EstadoJuego.TRANSICION_NIVEL) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // El jugador ha respirado, cargamos el TXT correspondiente
                this.mapa.cargarNivel(this.nivelActual);
                this.estadoActual = EstadoJuego.JUGANDO;
            }
            return;
        }

        // --- 2. Controles si estamos en Game Over ---
        if (estadoActual == EstadoJuego.GAME_OVER) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                System.out.println("Reiniciando y volviendo al MENU");
                
                // ¡IMPORTANTE! Antes de volver al menú, tenemos que RESETEAR la partida actual
                this.mapa.resetearTablero(); // (Ahora creamos este método en GestorTablero)
                estadoActual = EstadoJuego.MENU_PRINCIPAL;
            }
            return; // Bloqueamos el resto del teclado
        }

        // --- Controles si hemos GANADO el juego ---
        if (estadoActual == EstadoJuego.VICTORIA) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                System.out.println("Reiniciando desde la victoria al MENU");
                this.mapa.resetearTablero(); 
                this.estadoActual = EstadoJuego.MENU_PRINCIPAL;
            }
            return; 
        }


        // Todo el evento de la tecla que has pulsado viaja dentro del objeto 'e'
        int codigoTecla = e.getKeyCode();

        // --- 3. Controles normales (Solo si estamos JUGANDO) ---
        if (estadoActual == EstadoJuego.JUGANDO && jugador.isVivo()) {

            switch (codigoTecla){
                case KeyEvent.VK_UP:
                    jugador.intentarMover(0, -1);
                    break;
                case KeyEvent.VK_DOWN:
                    jugador.intentarMover(0, 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    jugador.intentarMover(1, 0);
                    break;
                case KeyEvent.VK_LEFT:
                    jugador.intentarMover(-1, 0);
                    break;
                case KeyEvent.VK_SPACE:
                    jugador.ponerBomba();
                    break;
            }

            repaint(); 
        }
    }

    /////////////////////////////////////////////////////////////// FIN KEYLISTENER //

    // Esta es la función nativa de Java que se encarga de dibujar.
    // El objeto "Graphics g" es nuestro pincel.
    @Override
    protected void paintComponent(Graphics g) {
        // Limpiamos el panel en cada fotograma para no dejar rastro
        super.paintComponent(g); 

        switch (estadoActual){
            case MENU_PRINCIPAL:
                // Pantalla negra con texto
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arilal", Font.BOLD, 30));

                // Texto centrado (coordenadas aproximadas)
                g.drawString("MI BOMBERMAN POO", 100, 200);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Pulsa ENTER para empezar", 150, 300);
                break;
        
            case JUGANDO:
                // 1. PINTAR EL ESCENARIO (El mapa y las bombas con sistema de CAPAS)
                for (int x=0; x < this.mapa.getAncho(); x++){
                    for(int y=0; y < this.mapa.getAlto(); y++){
                        ElementoTablero elemento = this.mapa.obtenerElementoEn(x, y);

                        if (elemento != null) {
                            // Multiplicamos la coordenada lógica por el tamaño en píxeles
                            int pixelX = x * TAMANO_CELDA;
                            int pixelY = y * TAMANO_CELDA;

                            if (elemento instanceof MuroDestructible) {
                                if (this.spriteMuroDestructible != null){
                                    g.drawImage(this.spriteMuroDestructible, pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA, null);
                                } else {
                                    g.setColor(Color.ORANGE);
                                    g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                                }
                            }
                            else if (elemento instanceof MuroFijo) {
                                if (this.spriteMuroDeAcero != null){
                                    g.drawImage(this.spriteMuroDeAcero, pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA, null);
                                } else {
                                g.setColor(Color.GRAY);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                                }
                            }
                            else if (elemento instanceof Bomba){
                                if (this.spriteBomba != null) {
                                    g.drawImage(this.spriteBomba, pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA, null);
                                } else {
                                    g.setColor(Color.BLACK);
                                    g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                                }
                            }
                            else if (elemento instanceof Fuego){
                                g.setColor(Color.YELLOW);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                            }
                            else if (elemento instanceof PowerUpFuego){
                                g.setColor(Color.ORANGE);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                            }
                            else if (elemento instanceof PowerUpVida){
                                g.setColor(Color.MAGENTA);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                            }
                            else if (elemento instanceof Enemigo){
                                g.setColor(Color.RED);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                            }
                            else if (elemento instanceof Puerta){
                                g.setColor(Color.CYAN);
                                g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                            }
                        } 
                    }
                }
                
                // 2. PINTAR A LOS ACTORES (Encima de todo lo demás)
                // Usamos el objeto jugador que recibimos en el constructor de la vista
                if (this.jugador != null) {
                    int pixelX = this.jugador.getX() * TAMANO_CELDA;
                    int pixelY = this.jugador.getY() * TAMANO_CELDA;

                    if (this.jugador.isVivo() && this.spriteSheetJugador != null) {
                        
                        // Pedimos el estado actual a la base de datos del jugador
                        int fila = this.jugador.getDireccionMirada();
                        int columna = this.jugador.getFotogramaPaso();

                        // 1. Preguntamos a la imagen cuánto mide en total
                        int anchoTotal = this.spriteSheetJugador.getWidth();
                        int altoTotal = this.spriteSheetJugador.getHeight();

                        // 2. Calculamos el tamaño de la celda (5 columnas, 4 filas)
                        int anchoFotograma = anchoTotal / 5;
                        int altoFotograma = altoTotal / 4;

                        // 3. RECORTAMOS LA IMAGEN EN RAM
                        BufferedImage recorteActual = this.spriteSheetJugador.getSubimage(
                            columna * anchoFotograma, 
                            fila * altoFotograma,    
                            anchoFotograma, 
                            altoFotograma
                        );

                        // Dibujamos el recorte escalándolo al tamaño de nuestra celda (40x40)
                        g.drawImage(recorteActual, pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA, null);
                        
                    } else {
                        // Si está muerto, mantenemos tu bloque rojo oscuro dramático
                        g.setColor(Color.RED); 
                        g.fillRect(pixelX, pixelY, TAMANO_CELDA, TAMANO_CELDA);
                    }
                }

                /////////////////// 3. PINTAR EL HUD  ///////////////////////////
                // Elegimos un color que resalte sobre el gris del muro superior
                g.setColor(Color.WHITE);
                // Creamos una fuente tipo Arial, en negrita, tamaño 20
                g.setFont(new Font("Arial", Font.BOLD, 20));

                if (this.jugador != null) {
                    // Escribimos las vidas a la izquierda (X=20, Y=25)
                    g.drawString("Vidas: " + this.jugador.getVidas(), 20, 28);
                }
                // Hueco reservado para la futura lógica de Puntos
                g.drawString("Puntos: 00000", 150, 28);
                // Nivel actual en el centro-derecha
                g.drawString("Stage: " + this.nivelActual, 320, 28);
                // Escribimos el tiempo un poco más al centro/derecha (X=200, Y=25)
                g.drawString("Tiempo: " + this.mapa.getTiempoRestante(), 435, 28);
                break;
                ///////////////////////////////////////////////////////////////////
            
            case TRANSICION_NIVEL:
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                // Usamos la variable dinámica para escribir "STAGE 2", "STAGE 3", etc.
                g.drawString("STAGE " + this.nivelActual, 150, 250); 
                
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Pulsa ENTER para empezar", 130, 350);
                break;

            case GAME_OVER:
                // Pantalla negra con texto rojo
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("HAS PERDIDO", 120, 250);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Pulsa ENTER para volver al menú", 140, 350);
                break;
            
            case VICTORIA:
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(Color.YELLOW); // Un color dorado para el triunfo
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("¡MISIÓN CUMPLIDA!", 100, 250);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Has superado todos los niveles", 160, 300);
                g.drawString("Pulsa ENTER para volver al menú", 150, 350);
                break;


            default: break;

        }

    }
    
}

