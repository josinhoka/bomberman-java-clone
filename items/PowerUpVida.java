package items;
import modelo.Jugador;

public class PowerUpVida extends PowerUp {

    public PowerUpVida(int x, int y ){
        super(x,y);
    }
    
     @Override
    public void aplicarEfecto(Jugador jugador){
         // Incrementamos el radio de fuego del jugador en 1 unidad
        jugador.setVidas(jugador.getVidas() +1);
        System.out.print("1UP ++! Nueva Vida: "+ jugador.getVidas());
    }
}
