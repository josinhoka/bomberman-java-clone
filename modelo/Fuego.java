package modelo;
// Como todas estas clases viven en la misma carpeta, se ven entre ellas automáticamente.
// No necesitan imports entre sí.
import items.*;

public class Fuego extends ElementoTablero {

    private int ticksDeVida;
    private GestorTablero mapa;
    private boolean generaPowerUp;
    private boolean generaPuerta;

    public Fuego(int x, int y, GestorTablero mapa, boolean generaPowerUp, boolean generaPuerta) {
        // El fuego nace en X, Y y NO es sólido (false) para poder pisarlo
        super(x, y, false); 
        this.mapa = mapa;
        this.ticksDeVida = 30; // 30 ticks a 60 FPS es exactamente medio segundo de duración
        this.generaPowerUp = generaPowerUp;
        this.generaPuerta = generaPuerta;
    }

    public void actualizarTick() {
        this.ticksDeVida--;
        
        // Cuando se apaga, se elimina a sí mismo de la base de datos del mapa
        if (this.ticksDeVida <= 0) {
            // Prioridad 1: Si este fuego escondía la puerta, la soltamos
            if (this.generaPuerta){
                this.mapa.agregarElemento(new items.Puerta(this.x, this.y));
            // Prioridad 2: Si no había puerta pero tocaba premio, lo soltamos
            } else if (this.generaPowerUp) {
                // Tiramos una moneda (0.0 a 1.0)
                if (Math.random() < 0.5) {
                    this.mapa.agregarElemento(new items.PowerUpFuego(this.x, this.y));
                } else {
                    this.mapa.agregarElemento(new items.PowerUpVida(this.x, this.y));
                }
            // Prioridad 3: Se apaga y deja el suelo vacío
            } else {
                // ...si no, deja la casilla vacía como siempre
                this.mapa.eliminarElemento(this.x, this.y);
            }
        }
    }
}