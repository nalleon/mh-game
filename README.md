<div align=justify>

# Monster Hunter - Juego V1

> ✒️ __Autor__: Nabil L.A. (@nalleon)

<div align=center>
    <img src="./img/image.png" width=370px heigth="auto">
</div>

## Índice

- 1.[ Descripción](#1-descripción)
    - 1.1[ Desarrollo del juego](#11-desarrollo-del-juego)
- 2.[ Implemenatcion del código](#2-implementación)
- 3.[ Soluciones aportadas](#3-soluciones)
- 4.[ Salida de la ejecución](#4-salida-de-la-ejecucución)


### 1. Descripción

Pequeño juego de simulación donde cazadores y monstruos interactúan en un mapa definido por el usuario. El objetivo es que los cazadores capturen la mayor cantidad de monstruos posibles en un tiempo determinado.

#### 1.1 Desarrollo del juego

> ***
> - El mapa por defecto tiene un tamaño de 10x10, pero se puede seleccionar un tamaño en específico.
>
> - Los cazadores se mueven a una cantidad de casillas aleatorias dentro de el tamaño del mapa definido por las posiciones x,y.
>
> - Los monstruos no realizan ningún tipo de movimiento.
> 
> ***

***
</br>

***
</br>

### 2. Implementación 

En esta versión del juego la clase Hunter es el único hilo.

- __Clase `Hunter`__: Define el objeto para los cazadores.
  - Contenido:

    ```code
        extends Thread

        //Propiedades

        private String hunterName;
        private String position;
        private MapGame mapGame;
        private static long TIME_TO_CATCH = 20000;

        // Constructor por defecto
        public Hunter (){
            hunterName = "";
            position="0,0";
            mapGame = new MapGame();
        }

        // Constructor con nombre y mapa
        public Hunter(String hunterName, MapGame mapGame) {
            this.hunterName = hunterName;
            position = "0,0";
            this.mapGame = mapGame;
        }


        // Getters y setters

        // Run
        @Override
        public void run() {}

        // Equals y toString
    ```

- __Clase `Monster`__: Define el objeto para los monstruos.
     - Contenido:

    ```code

        // Propiedades
        private int monsterId;
        private String monsterName;
        private String position;
        private boolean captured;

        // Constructor por defecto
        public Monster() {
            monsterId = 1;
            monsterName = "";
            position = "";
            captured = false;
        }

        // Constructor con el id y el nombre
        public Monster(int monsterId, String monsterName) {
            this.monsterId = monsterId;
            this.monsterName = monsterName;
            position = "";
            captured = false;
        }

        // Getters y setters

        // Equals y toString
    ```

- __Clase `MapGame`__: Define el tamaño del mapa, las posiciones iniciales de los cazadores y los monstruos (además del movimiento del primero) así como las acciones que ocurren en cada casilla.
- Contenido:

    ```code

        // Propiedades
        private int size;
        private ConcurrentHashMap<String, String> locations;

        private static final int DEFAULT_SIZE = 10;
        private String [][] map;

        private List<Monster> monsters;
        private List<Hunter> hunters;

        // Constructor por defecto
        public MapGame() {
            locations = new ConcurrentHashMap<>();
            size = DEFAULT_SIZE;
            map = new String[size][size];
            monsters = new CopyOnWriteArrayList<>();
            hunters = new CopyOnWriteArrayList<>();
            generateMap();
        }

        // Constructor con el tamaño
        public MapGame(int size) {
            this.size = size;
            locations = new ConcurrentHashMap<>();
            map = new String[size][size];
            monsters = new CopyOnWriteArrayList<>();
            hunters = new CopyOnWriteArrayList<>();
            generateMap();
        }

        // Función para generar las localizaciones
        public String generateLocations(){}

        public void generateMap() {}

        public void showMap(){}

        public boolean checkPositionsOverlap(String position){}

        public synchronized void addHunter(Hunter hunter, String location){}

        public synchronized void moveHunter(Hunter hunter){}

        public synchronized void addMonster(Monster monster){}

        public synchronized void removeMonster(Monster monster, String location){}

        public synchronized void catchMonster(List<Monster> monsters, Hunter hunter) {}

        // Getters y setters

        // Equals y toString

    ```

***
</br>

### 3. Soluciones 

1. **Control de movimiento aleatorio de cazadores**: Se ha implementado una función para que los cazadores se desplacen de manera aleatoria dentro del mapa, evitando que salgan de los límites definidos.
   
2. **Tiempo límite para la partida**: Tras un número de turnos predefinido, los monstruos huyen y el juego finaliza. Esta solución introduce un tiempo límite para que los cazadores encuentren a los monstruos.

3. **Escalabilidad del mapa**: El usuario puede definir el tamaño del mapa al inicio del juego, lo que permite una mayor flexibilidad y adaptabilidad de la simulación.

***
</br>

### 4. Salida de la ejecucución

```code
work in progress
```

***
</br>

</div>