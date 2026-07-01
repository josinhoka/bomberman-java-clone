package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class GestorTablero {

    private int ancho;
    private int alto;

    // Nuestra matriz principal. Contiene objetos de la clase padre,
    // así que aceptará tanto MuroFijo como MuroDestructible (Polimorfismo en
    // acción)
    private Jugador jugadorPrincipal; // El mapa ahora conoce al actor
    private ElementoTablero[][] cuadricula;
    private int ticksPartida; // Para llevar el tiempo

    private boolean nivelSuperado;

    public GestorTablero(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        // Inicializamos la matriz vacía en memoria
        this.cuadricula = new ElementoTablero[ancho][alto];
        this.ticksPartida = 60 *60;
    }

    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }

    // Añadimos métodos para el jugador
    public void setJugador(Jugador j) {
        this.jugadorPrincipal = j;
    }

    public Jugador getJugador() {
        return this.jugadorPrincipal;
    }

    public boolean isNivelSuperado() { return nivelSuperado; }
    public void setNivelSuperado(boolean b) { this.nivelSuperado = b; }

    // El radar de enemigos
    public boolean quedanEnemigos() {
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                if (cuadricula[x][y] instanceof Enemigo) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método para colocar un elemento en el mapa
    public void agregarElemento(ElementoTablero elemento) {
        int x = elemento.getX();
        int y = elemento.getY();

        // BLINDAJE: Si intentan meter algo fuera de los límites, abortamos
        if (x < 0 || x >= this.ancho || y < 0 || y >= this.alto) {
            return; 
        }

        // Lo guardamos en la coordenada correspondiente
        cuadricula[x][y] = elemento;
    }

    // Método que usará el jugador para saber si puede pisar una casilla
    public ElementoTablero obtenerElementoEn(int x, int y) {
        // Primero, una pequeña comprobación de seguridad para no salirnos del mapa
        // (IndexOutOfBounds)
        if (x < 0 || x >= ancho || y < 0 || y >= alto) {
            return null;
        }
        return cuadricula[x][y];
    }

    public void generarBordesOptimizados() {
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                // Si la X o la Y están en alguno de los extremos del mapa...
                if (x == 0 || x == ancho - 1 || y == 0 || y == alto - 1) {
                    // ...fabricamos un muro indestructible y lo colocamos.
                    agregarElemento(new MuroFijo(x, y));
                }
                else if (y == x ){
                    // En el futuro, con un simple "else if", podríamos poner
                    // los muros destructibles o los pilares centrales aquí mismo.
                    agregarElemento(new MuroDestructible(x,y, false));
                }
            }
        }
        // Agregamos los enemigos 
        agregarElemento(new Enemigo(10, 5, this)); 
        agregarElemento(new Enemigo(12, 8, this));
    }

    public void eliminarElemento(int x, int y) {
        cuadricula[x][y] = null; // Borramos el rastro en la memoria
    }

    public void actualizarMapa(){
        // 1. Descontamos el reloj de la partida
        if (this.ticksPartida > 0) {
            this.ticksPartida--;
        } else {
            // TODO: En el futuro, aquí el jugador moriría por "Time Over"
        }
        // Envejeciendo BOMBAS
        // como no es necesario recorrer los muros que conforman los extremos del juego podemos comenzar en una casilla antes y terminar igual. 
        for (int x = 1; x < ancho-1; x++) {
            for (int y = 1; y < alto-1; y++) {
                
                ElementoTablero elemento = this.obtenerElementoEn(x, y);
                // 3. LA MAGIA: Lo "casteamos" (lo forzamos a ser tratado como Bomba) y así nos deja utilizar sus métodos declarados. 
                if (elemento instanceof Bomba){

                    Bomba laBomba = (Bomba) elemento;
                    laBomba.actualizarTick();
                }
                else if (elemento instanceof Fuego){
                    ((Fuego) elemento).actualizarTick(); 
                }
                else if(elemento instanceof Enemigo){
                    ((Enemigo) elemento).actualizarTick();
                }
            
            }
        }
    }

    // ¡El truco de backend! Un método que convierta los ticks brutos en texto bonito "01:00"
    public String getTiempoRestante() {
        int segundosTotales = this.ticksPartida / 60;
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        
        // Usamos String.format para asegurar los ceros a la izquierda (ej. 00:05)
        return String.format("%02d:%02d", minutos, segundos);
    }

    public int getTicksPartida() {
        // Si quedan 0 segundos quedan 0 ticks
        return this.ticksPartida;
    }

    /**
     * Limpia por completo la matriz lógica y restablece las variables globales
     */
    public void resetearTablero() {
        this.nivelSuperado = false;

        for (int x = 0; x < this.ancho; x++) {
            for (int y = 0; y < this.alto; y++) {
                this.cuadricula[x][y] = null; // Borrado completo de memoria
            }
        }
        // Duración de casa partida en segundos. 
        int segundosDeseados = 120;
        this.ticksPartida = segundosDeseados * 60; // Reiniciamos el reloj de la fase
        
        // Si el jugador ya existía, lo resucitamos para la nueva partida
        if (this.jugadorPrincipal != null) {
            this.jugadorPrincipal.setVivo(true); 
            this.jugadorPrincipal.resetearestadisticas();
        }
    }

    /**
     * Lee un archivo .txt de nivel y construye el escenario dinámicamente
     */
    public void cargarNivel(int numeroStage) {
        // 1. Limpiamos cualquier residuo de la partida anterior
        this.resetearTablero();

        String rutaArchivo = "recursos/nivel" + numeroStage + ".txt";
        
        // Usamos un try-with-resources para asegurar el cierre del descriptor de archivo de Linux/Windows
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int coordenadaY = 0;

            // 2. Leemos el archivo línea a línea
            while ((linea = br.readLine()) != null && coordenadaY < this.alto) {
                
                // 3. Procesamos carácter por carácter de la línea actual
                for (int coordenadaX = 0; coordenadaX < linea.length() && coordenadaX < this.ancho; coordenadaX++) {
                    char caracter = linea.charAt(coordenadaX);

                    switch (caracter) {
                        case '#':
                            this.agregarElemento(new MuroFijo(coordenadaX, coordenadaY));
                            break;
                        case '*':
                            this.agregarElemento(new MuroDestructible(coordenadaX, coordenadaY, false));
                            break;
                        case 'E':
                            this.agregarElemento(new Enemigo(coordenadaX, coordenadaY, this));
                            break;
                        case 'D':
                            this.agregarElemento(new MuroDestructible(coordenadaX, coordenadaY, true));
                            break;
                        case 'P':
                            // Posicionamos al jugador en su punto de spawn inicial
                            if (this.jugadorPrincipal != null) {
                                this.jugadorPrincipal.setPosicion(coordenadaX,coordenadaY);
                                //this.jugador.setX(coordenadaX);
                                //this.jugador.setY(coordenadaY);
                            }
                            break;
                        case ' ':
                        default:
                            // Espacio vacío, se queda en null gracias al reseteo previo
                            break;
                    }
                }
                coordenadaY++; // Bajamos a la siguiente fila de la matriz
            }
            System.out.println("-> [Backend] Stage " + numeroStage + " cargada con éxito.");
            
        } catch (IOException e) {
            System.out.println("¡Error crítico! No se pudo leer el mapa en: " + rutaArchivo);
            e.printStackTrace();
        }
    }


}