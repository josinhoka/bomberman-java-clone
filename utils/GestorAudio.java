package utils;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class GestorAudio {

    // Método estático para poder llamarlo desde cualquier parte sin hacer "new GestorAudio()"
    public static void reproducirSFX(String rutaArchivo) {
        try {
            // 1. Localizamos el archivo en el disco
            File archivoSonido = new File(rutaArchivo);
            
            // 2. Abrimos el flujo de datos (Stream)
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);
            
            // 3. Pedimos a Java un Clip de memoria libre y volcamos el stream en él
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // 4. ¡Fuego! (Java lo reproduce en segundo plano automáticamente)
            clip.start();
            
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el sonido: " + rutaArchivo);
            e.printStackTrace();
        }
    }
}
