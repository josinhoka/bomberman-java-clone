package items;
import modelo.Jugador; // Necesitan conocer al jugador para aplicarle el efecto
import modelo.ElementoTablero;

public abstract class PowerUp extends ElementoTablero{
    
    public PowerUp(int x, int y){
        // Inicializamos a false ya que no es sólido
        super(x,y,false);
    }

    // Método abstracto: cada Power-Up hará algo único al ser recogido
    public abstract void aplicarEfecto(Jugador jugador);
    
}
