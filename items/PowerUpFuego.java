package items;
import modelo.Jugador; // Necesitan conocer al jugador para aplicarle el efecto

public class PowerUpFuego extends PowerUp {

    public PowerUpFuego(int x, int y){
        super(x,y);
    }

    @Override
    public void aplicarEfecto(Jugador jugador){
        // Incrementamos el radio de fuego del jugador en 1 unidad
        jugador.setRadioFuego(jugador.getRadioFuego() +1);
        System.out.print("Bomba mejorada! Nuevo Rango: "+ jugador.getRadioFuego());
    }


    
}
