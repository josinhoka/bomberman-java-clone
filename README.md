# 💣 Bomberman Clone - Custom Java 2D Engine

Un clon clásico de Bomberman desarrollado desde cero en **Java puro (AWT/Swing)**. Este proyecto nace como una demostración técnica de arquitectura de software, aplicando principios de Programación Orientada a Objetos (POO), patrones de diseño y gestión de concurrencia sin depender de motores gráficos de terceros.

## 🏗️ Arquitectura y Patrones de Diseño

Este proyecto no es solo un juego, es un motor 2D construido a medida. Las principales decisiones técnicas incluyen:

* **Custom Game Loop (Multithreading):** Implementación de un bucle de juego manual aislando la lógica matemática (`Update`) de la renderización gráfica (`Render`). El motor corre en un `Thread` independiente para garantizar fluidez sin bloquear el hilo principal de la interfaz de usuario, controlando los ciclos de CPU mediante pausas calculadas (ticks/FPS).
* **Máquina de Estados Finita (FSM):** Gestión del flujo de la aplicación mediante un `Enum` (`MENU_PRINCIPAL`, `JUGANDO`, `TRANSICION_NIVEL`, `GAME_OVER`, `VICTORIA`). Esto permite desacoplar los controles del teclado y el renderizado visual dependiendo del contexto en el que se encuentre la ejecución.
* **Diseño Guiado por Datos (Data-Driven Design):** El motor separa estrictamente el código fuente del diseño de niveles. Los mapas se parsean dinámicamente en tiempo de ejecución leyendo simples archivos de texto plano (`.txt`). Esto permite escalar el juego a infinitos niveles sin necesidad de recompilar el código.
* **Polimorfismo y Herencia:** Uso intensivo de principios SOLID. Las entidades del juego (Muros, Bombas, Enemigos, PowerUps) heredan de clases base abstractas e implementan comportamientos específicos polimórficos, facilitando un código escalable a la hora de añadir nuevas mecánicas o ítems (ej. `PowerUpFuego`, `PowerUpVida`).
* **I/O y Gestión de Recursos:** Carga asíncrona de efectos de sonido cortos (SFX) mediante `Clip` e `InputStream`, garantizando que la lectura a disco no penalice el rendimiento del Game Loop.

## 🚀 Instalación y Ejecución

Al estar desarrollado en Java puro, no requiere dependencias externas pesadas. 

1. Clona el repositorio:
   ```bash
   git clone [https://github.com/josinhoka/bomberman-java-clone.git](https://github.com/josinhoka/bomberman-java-clone.git)```
   
2. Entra en la carpeta del proyecto:
```bash
cd bomberman-java-clone```

3. Compila todos los paquetes desde el directorio raíz:
```bash
javac core\Main.java modelo\*.java items\*.java vista\*.java```

4. Ejecuta el motor llamando a la clase principal junto con su paquete:
```bash
java core.Main```

## 🎮 Controles

* **Flechas direccionales:** Movimiento del jugador.
* **Espacio:** Colocar bomba.
* **Enter:** Navegar por los menús / Iniciar partida.

## 👨‍💻 Sobre el Autor

Desarrollado por **José Carlos Lorente**.
Backend & Software Developer con experiencia en Java, SQL, Linux y desarrollo de arquitecturas robustas.

🌐 **Portfolio:** [jclorente.dev](https://jclorente.dev)  
💼 **GitHub:** [@josinhoka](https://github.com/josinhoka)

---
*Este proyecto se distribuye bajo la licencia MIT.*