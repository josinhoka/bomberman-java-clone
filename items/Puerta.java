package items;

import modelo.ElementoTablero;

public class Puerta extends ElementoTablero {

    public Puerta(int x, int y){
        // Es false porque el jugador necesita caminar sobre ella para cruzarla
        super(x,y,false);
    }
    
}
