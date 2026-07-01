package modelo;

public class MuroFijo extends ElementoTablero {

    public MuroFijo(int x, int y) {
        // Llamamos al constructor de la clase padre (ElementoTablero)
        // Le pasamos la x, la y, y un "true" inamovible porque siempre bloquea el paso.
        super(x, y, true);
    }

    // Aquí no pondremos ninguna función para destruirlo o cambiar su solidez,
    // porque por definición, la única responsabilidad de este muro es estorbar.
}