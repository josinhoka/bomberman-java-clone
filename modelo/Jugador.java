package modelo;
import items.*; // Necesita importar items para poder usar los PowerUps al pisarlos

public class Jugador extends ElementoTablero {

    //private int maxBombas;
    private int radioFuego;
    private boolean vivo;
    private int vidas;
    // Problema del cooldown. Jugador a veces se mueve dos veces seguidas. 
    private long ultimoMovimiento; // Guardamos la marca de tiempo

    // 0: Abajo, 1: Arriba, 2: Izquierda, 3: Derecha (Dependerá del orden de tu imagen)
    private int direccionMirada; 
    // 0, 1, 2 (Para ir ciclando entre los fotogramas del paso)
    private int fotogramaPaso;

    // ¡La clave! El jugador guarda un puntero/teléfono directo al mapa
    private GestorTablero mapa;

    public Jugador(int x, int y, GestorTablero mapa) {
        super(x, y, true); // El jugador es sólido para los enemigos
        this.mapa = mapa;
       // this.maxBombas = 1;
        this.radioFuego = 1;
        this.vivo = true;
        this.vidas = 1;
        this.ultimoMovimiento = 0;
        this.direccionMirada = 0; 
        this.fotogramaPaso = 0;
    }

    // Getters para que la Vista pueda leer el estado
    public int getDireccionMirada() { return direccionMirada; }
    public int getFotogramaPaso() { return fotogramaPaso; }

    public void intentarMover(int direccionX, int direccionY) {
        if (!vivo)
            return;

        // --- RATE LIMITING: Bloqueamos el spam del teclado ---
        long ahora = System.currentTimeMillis();
        if (ahora - this.ultimoMovimiento < 40) { 
            // Si han pasado menos de 150ms desde el último paso, ignoramos la tecla
            return; 
        }
        // -----------------------------------------------------

        // Mirar al futuro
        int nuevaX = this.x + direccionX;
        int nuevaY = this.y + direccionY;

        // Pedir permiso al mapa
        ElementoTablero obstaculo = mapa.obtenerElementoEn(nuevaX, nuevaY);

        if (obstaculo instanceof Enemigo){
            this.jugadorMuere();
            return;
        }

        if (obstaculo instanceof Puerta) {
            if (!this.mapa.quedanEnemigos()) {
                System.out.println("¡Nivel despejado! Abriendo puerta...");
                this.mapa.setNivelSuperado(true);
            } else {
                System.out.println("La puerta está cerrada. Aún quedan enemigos.");
            }
        }

        // Si no hay obstáculo (null) o el obstáculo no es sólido (ej. se puede pisar)
        if (obstaculo == null || !obstaculo.esSolido()) {
            this.x = nuevaX;
            this.y = nuevaY;

            // Actualizamos la marca de tiempo solo si el movimiento ha sido exitoso
            this.ultimoMovimiento = ahora;

            // 1. ACTUALIZAR ANIMACIÓN (Incluso si chocamos con la pared, queremos que gire la cabeza)
            if (direccionX == 1) this.direccionMirada = 3;      // Derecha
            else if (direccionX == -1) this.direccionMirada = 2; // Izquierda
            else if (direccionY == 1) this.direccionMirada = 0;  // Abajo
            else if (direccionY == -1) this.direccionMirada = 1; // Arriba

            // Avanzamos el ciclo de animación (0, 1, 2, 0, 1, 2...)
            this.fotogramaPaso = (this.fotogramaPaso + 1) % 5;

            // 2. ACTUALIZAR POSICIÓN LÓGICA
            //nuevaX = this.x + direccionX;

            if (obstaculo instanceof PowerUp) {
                // tratar el elmento dependiendo del tipo en el jugador
                // Lo "desenmascaramos" (Casting)
                PowerUp premio = (PowerUp) obstaculo;

                premio.aplicarEfecto(this);

                // Lo borrramos de la cuadríacula
                this.mapa.eliminarElemento(nuevaX, nuevaY);
                // Disparamos el efecto de sonido
                utils.GestorAudio.reproducirSFX("recursos/sonidos/powerup.wav");
            }

        }
    }

    public void ponerBomba() {
        // 1. Fabricamos una bomba nueva.
        // Le pasamos nuestra posición actual (this.x, this.y) para que nazca debajo de nosotros.
        // Le pasamos nuestro radio y nuestra referencia al mapa.
        if (!vivo) return;

        Bomba nuevaBomba = new Bomba(this.x, this.y, this.radioFuego, this.mapa);
        // 2. Le decimos al mapa que la guarde en la matriz.
        this.mapa.agregarElemento(nuevaBomba);
    }

    public void resetearestadisticas(){
        this.radioFuego = 1;
        // this.velocidad = 1;
        // this.maxBombas = 1;
    }

    public void jugadorMuere() {
        if (!vivo) return;

        this.vivo = false;
        this.vidas--;
        System.out.println("¡::CALAVERA::! Vidas Restantes: " + this.vidas);
    }

    public boolean isVivo(){
        return this.vivo;
    }

    public int getVidas(){
        return this.vidas;
    }

    public int getRadioFuego(){
        return this.radioFuego;
    }

    public void setRadioFuego(int a){
        this.radioFuego = a;
    }
    public void setVivo(boolean vivo){
        this.vivo = vivo;
    }
    public void setVidas (int vidas){
        this.vidas = vidas;
    }
    

}