package modelo;
import java.util.Random;

public class Enemigo extends ElementoTablero {
    
    private GestorTablero mapa;
    private int ticksParaMoverse;
    // Se moverá cada 30 ticks (medio segundo). Puedes bajarlo para hacerlo más difícil.
    private final int VELOCIDAD = 30;
    private Random random;


    public Enemigo(int x, int y, GestorTablero mapa){
        super(x,y,true);
        this.mapa = mapa;
        this.ticksParaMoverse = VELOCIDAD;
        this.random = new Random();
    }

    public void actualizarTick(){
        this.ticksParaMoverse--;

        // Cuando el temporizador llega a 0. toma una decisión y se reinicia
        if (this.ticksParaMoverse <=0){
            intertarMoverAleatorio();
            this.ticksParaMoverse = VELOCIDAD;
        }
    }

    private void intertarMoverAleatorio(){
        // 0: Arriba, 1:Abajo, 2: Izquierda, 3: Derecha
        int direccion = random.nextInt(4);
        int dirX = 0, dirY = 0;

        if (direccion == 0) dirY = -1;
        else if (direccion == 1) dirY = 1;
        else if (direccion == 2) dirX = -1;
        else if (direccion == 3) dirX = 1;

        int nuevaX = this.x + dirX;
        int nuevaY = this.y + dirY;

        // 1. ¿Chocamos con el jugador? (Recuerda que el jugador vuela fuera de la matriz)
        Jugador prota = this.mapa.getJugador();
        if (prota != null && prota.getX() == nuevaX && prota.getY() == nuevaY) {
            prota.jugadorMuere();
            return; // Matamos y no nos movemos
        }

        // 2. ¿La casilla de la matriz está libre?
        ElementoTablero obstaculo = mapa.obtenerElementoEn(nuevaX, nuevaY);
        
        if (obstaculo == null) {
            // Actualizamos la base de datos de la cuadrícula
            this.mapa.eliminarElemento(this.x, this.y);
            this.x = nuevaX;
            this.y = nuevaY;
            this.mapa.agregarElemento(this);
        }
        // Si hay muro o bomba (obstaculo != null), el enemigo pierde su turno y se queda quieto
    }



}
