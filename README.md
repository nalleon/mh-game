<div align=justify>

# Monster Hunter - Juego V1

> ✒️ __Autor__: Nabil L.A. (@nalleon)

<div align=center>
    <img src="./img/image.png" width=370px heigth="auto">
</div>

## Índice

- 1.[ Descripción](#1-descripción)
    - 1.1[ Ejemplo de flujo](#11-ejemplo-de-flujo)
- 2.[ Implemenatcion del código](#2-implementación-del-código)
- 3.[ Soluciones aportadas](#3-soluciones-aportadas)
- 4.[ Salida de la ejecución](#4-salida-de-la-ejecucución)


### 1. Descripción

Pequeño juego de simulación donde cazadores y monstruos interactúan en un mapa definido por el usuario. El objetivo es que los cazadores capturen la mayor cantidad de monstruos posibles en un tiempo determinado.

#### 1.1 Ejemplo de flujo

> ***
>
> - El mapa por defecto tiene un tamaño de 10x10, pero se puede seleccionar un tamaño en específico.
>
> - Los cazadores se mueven a una cantidad de casillas aleatorias dentro de el tamaño del mapa definido por las posiciones x,y.
>
> - Los monstruos no realizan ningún tipo de movimiento.
>
> - La partida termina una vez no queden montruos, ya sean porque han sido cazados o porque han huido después de un tiempo.
>
> ***


***
</br>

### 2. Implementación del código

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
        public void run() {
            long initialTime = System.currentTimeMillis();
            long timePassed = 0;

            int monsterCaught = 0;
            boolean isOver = false;

            mapGame.addHunter(this, this.getPosition());

            while (!isOver && !mapGame.getMonsters().isEmpty() && timePassed < TIME_TO_CATCH) {
                mapGame.moveHunter(this);

                long endTime = System.currentTimeMillis();
                timePassed = (endTime - initialTime);

                if (timePassed >= TIME_TO_CATCH){
                    System.out.println("Time is up!");
                    System.out.println(hunterName + " caught " + monsterCaught + " monsters");
                    isOver = true;
                }


                for (Monster monster : mapGame.getMonsters()) {
                    if (monster.getPosition().equals(this.getPosition()) && !monster.isCaptured()) {
                        monster.setCaptured(true);
                        System.out.println(this.getHunterName() + " caught " + monster.getMonsterName());
                        mapGame.removeMonster(monster, monster.getPosition());
                        mapGame.getMonsters().remove(monster);
                        monsterCaught++;
                        break;
                    }
                }

                for (Monster monster : mapGame.getMonsters()) {
                    if (!monster.isCaptured() && timePassed >= 10000 && timePassed < TIME_TO_CATCH) {
                        mapGame.removeMonster(monster, monster.getPosition());
                        System.out.println(monster.getMonsterName() + " has fled the field!");
                    }
                }



                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(hunterName + " interrupted");
                }
            }
        }

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

### 3. Soluciones aportadas

1. __Control de movimiento aleatorio de cazadores__: Implementación del movimiento de los cazadores de manera aleatoria dentro del mapa, evitando que salgan de los límites definidos.

2. __Tiempo límite para la partida__: Tras un tiempo determinado, los monstruos huyen y el juego finaliza. Esta solución introduce un tiempo límite para que los cazadores encuentren a los monstruos.

3. __Tiempo límite para la huida__: Una vez pasa una cierta cantidad de tiempo inferior al tiempo máximo los monstruos comenzarán a huir.

***
</br>

### 4. Salida de la ejecucución

```code
 .  .  .  M  .  
 .  .  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 H  .  H  .  .  
 
 .  .  .  M  .  
 .  .  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  H  .  H  
 
 .  .  .  M  .  
 .  .  .  M  .  
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  H  .  .  
 
 .  .  .  M  .  
 .  H  .  M  .  
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  M  .  
 .  .  .  M  .  
 .  H  .  H  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  M  H  
 .  .  .  M  .  
 .  .  .  H  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  M  H  
 H  .  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  M  .  
 H  .  .  M  .  
 .  .  .  .  .  
 H  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  M  .  
 .  .  .  M  .  
 .  .  .  .  .  
 H  .  .  .  .  
 H  .  .  .  .  
 
 .  .  .  H  .  
 .  .  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 H  .  .  .  .  
 
Hunter2 caught Monster2
 .  .  .  H  .  
 .  H  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  H  .  M  .  
 .  .  H  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  H  .  .  
 .  H  .  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  H  .  .  
 .  .  .  M  .  
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  .  M  .  
 .  H  .  .  .  
 .  .  .  H  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  .  M  .  
 .  .  H  .  .  
 .  .  .  H  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  .  M  .  
 .  .  H  .  .  
 .  .  .  .  .  
 .  H  .  .  .  
 
 .  .  .  .  .  
 .  .  H  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  H  .  .  .  
 
 .  .  .  .  .  
 .  .  H  M  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  H  .  
 
 .  .  .  .  .  
 .  .  .  M  .  
 .  .  .  .  H  
 .  .  .  .  .  
 .  .  .  H  .  
 
 .  .  .  .  .  
 .  .  .  M  .  
 .  .  .  H  H  
 .  .  .  .  .  
 .  .  .  .  .  
 
Monster1 has fled the field!

Process finished with exit code 0
```

***
</br>

</div>