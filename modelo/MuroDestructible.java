package modelo;

public class MuroDestructible extends ElementoTablero {

    // Variable exclusiva de los muros destructibles
    private boolean destruido;
    private boolean escondePuerta;

    public MuroDestructible(int x, int y, boolean escondePuerta) {
        // Nace en unas coordenadas concretas y bloqueando el paso (true)
        super(x, y, true);
        this.destruido = false; // Al nacer, está intacto
        this.escondePuerta = escondePuerta;
    }

    public boolean isEscondePuerta() {
        return this.escondePuerta;
    }

    // El mapa llamará a esta función cuando el fuego alcance esta casilla
    public void explota() {
        this.destruido = true;

        // Aquí es donde brilla la POO. En el futuro, dentro de esta misma función
        // podríamos añadir la lógica para:
        // 1. Reproducir un sonido de ladrillos rompiéndose.
        // 2. Sumar puntos al jugador.
        // 3. Calcular aleatoriamente si soltamos un potenciador (PowerUp).
    }

    // Un getter para que el gestor del mapa compruebe si debe borrar este muro
    public boolean isDestruido() {
        return destruido;
    }

    
}