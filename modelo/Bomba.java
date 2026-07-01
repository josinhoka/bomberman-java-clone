package modelo;
// Como todas estas clases viven en la misma carpeta, se ven entre ellas automáticamente.
// No necesitan imports entre sí.

public class Bomba extends ElementoTablero {
    
    private int radioFuego;
    private GestorTablero mapa; // Guardamos el teléfono directo al mapa
    //private int tiempoParaExplotar; // Podrían ser "ticks" del Game Loop o segundos
    private int ticksParaExplotar;

    // El constructor recibe todo lo necesario en el momento de nacer
    public Bomba(int x, int y, int radioFuego, GestorTablero mapa) {
        
        // Llamamos al padre. La bomba nace en X, Y, y es sólida (true)
        super(x, y, true); 
        this.radioFuego = radioFuego;
        this.mapa = mapa;
        // Un game loop son 60FPS para que dure 3 segundos 3*60 = 180 ticks
        this.ticksParaExplotar = 180; // Por defecto, explota en 3 unidades de tiempo
    }

    // Llamada por el reloj central
    public void actualizarTick() {
        this.ticksParaExplotar--; // Restamos una unidad de tiempo
        
        if (this.ticksParaExplotar <= 0) {
            this.explotar();
        }
    }

    // Esta función se ejecutará cuando el tiempo llegue a cero
    public void explotar() {
        utils.GestorAudio.reproducirSFX("recursos/sonidos/explosion.wav");
        System.out.println("¡BUM!");

        // Por si hay un enemigo pisando la bomba. 
        ElementoTablero centro = this.mapa.obtenerElementoEn(this.x, this.y);
        if (centro instanceof Enemigo){
            this.mapa.eliminarElemento(this.x, this.y);
        }
        this.mapa.agregarElemento(new Fuego(this.x, this.y, this.mapa, false, false));

        // 1. Desaparecemos del mapa para dejar el hueco
        this.mapa.eliminarElemento(this.x, this.y);
        this.mapa.agregarElemento(new Fuego(this.x, this.y, this.mapa, false, false));

        Jugador prota = this.mapa.getJugador();
        if (prota != null && prota.getX() == this.x && prota.getY() == this.y) {
            prota.jugadorMuere();
        }

        // 2. Lanzamos los 4 rayos expansivos en forma de cruz
        // Eje X, Eje Y
        propagarRayo(1, 0);  // Derecha
        propagarRayo(-1, 0); // Izquierda
        propagarRayo(0, 1);  // Abajo
        propagarRayo(0, -1); // Arriba
    }

    private void propagarRayo(int dirX, int dirY) {
        int actualX = this.x + dirX;
        int actualY = this.y + dirY;
        Jugador prota = this.mapa.getJugador(); // Pedimos el jugador al mapa

        for (int i = 0; i < this.radioFuego; i++) {
            
            // ¿El rayo ha tocado al jugador en esta casilla?
            if (prota != null && prota.getX() == actualX && prota.getY() == actualY) {
                prota.jugadorMuere();
                // Ojo: no hacemos 'break' porque el fuego del Bomberman atraviesa a los jugadores
            }

            ElementoTablero elemento = this.mapa.obtenerElementoEn(actualX, actualY);

        
            // CASO A: Chocamos con acero indestructible
            if (elemento instanceof MuroFijo) {
                break;
            }
            // CASO B: Chocamos con muro naranja destructible
            else if (elemento instanceof MuroDestructible) {
                MuroDestructible muro = (MuroDestructible) elemento;
                muro.explota();
                this.mapa.eliminarElemento(actualX, actualY);

                boolean sueltaPuerta = muro.isEscondePuerta();
                // Si suelta la puerta, anulamos el dado del PowerUp para que no se solapen
                boolean sueltaObjeto = !sueltaPuerta && (Math.random() < 0.40);

                // ¡Pasamos los dos booleanos al fuego!
                this.mapa.agregarElemento(new Fuego(actualX, actualY, this.mapa, sueltaObjeto, sueltaPuerta));
                break;
            }
            // Cuando el fuego mata al enemigo.
            else if(elemento instanceof Enemigo){

                // Borramos al enemigo de la matriz. 
                this.mapa.eliminarElemento(actualX, actualY);
                //Colocamos fuego visual en su lugar para ver la llama
                this.mapa.agregarElemento(new Fuego(actualX, actualY, this.mapa, false, false));
                // No metemos un break por que el fuego traspasa a los enemigos. 
                // Disparamos el grito del enemigo
                utils.GestorAudio.reproducirSFX("recursos/sonidos/muerte_enemigo.wav");
            }
            // CASO C: La casilla está vacía (suelo limpio)
            else if (elemento == null) {
                this.mapa.agregarElemento(new Fuego(actualX, actualY, this.mapa, false, false));
            }
        
            
            actualX += dirX;
            actualY += dirY;
        }
    }

}