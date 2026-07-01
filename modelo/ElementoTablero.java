package modelo;

public abstract class ElementoTablero {
    protected int x;
    protected int y;
    protected boolean esSolido;

    public ElementoTablero(int x, int y, boolean esSolido) {
        this.x = x;
        this.y = y;
        this.esSolido = esSolido;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean esSolido() {
        return esSolido;
    }

    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
