<div align=justify>

# Monster Hunter - Juego V3

> ✒️ __Autor__: Nabil L.A. (@nalleon)

<div align=center>
    <img src="./img/image.png" width=370px heigth="auto">
</div>

## Índice

- 1.[ Descripción](#1-descripción)
    - 1.1[ Ejemplo de flujo](#11-ejemplo-del-flujo)
- 2.[ Implementación del código](#2-implementación-del-código)
- 3.[ Soluciones aportadas](#3-soluciones-añadidas)
- 4.[ Salida de la ejecución](#4-salida-de-la-ejecucución)


### 1. Descripción

Tercera versión del juego. En esta versión, añadimos la creación de una cueva donde los monstruos se pueden esconder. Una cueva permite la capacidad total de un monstruo.

#### 1.1 Ejemplo del flujo

> ***
>
> - Hay 3 monstruos y 2 cazadores
>
> - Según el tamaño del mapa aparecen un numero de trampas en este delimitadas por el tamaño total entre 2 (ej: si el mapa tiene tamaño 5 (5x5) habrán 2 trampas). Estas trampas son unas minas que matarán a quien las pise. Una vez activadas, desaparecen.
>
> - Los monstruos se mueven también de manera aleatoria
>
> - Si un cazador encuentra un monstruo este tiene un 70% de probabilidades de capturarlo, si falla el cazador huirá a otra posición y reportará la posición del enemigo a los otros cazadores.
> 
> - Si un monstruo en cambio es quien encuentra un cazador este atacará a su oponente teniendo un 70% de probabilidades de derrotarlo, en el caso contrario huirá a otra posición.
>
> - En el mapa de genera una cueva en una posición aleatoria. Los monstruos se moveran a esta, pero en una cueva solo cabe un monstruo.
>
> - Si la posición de un cazador es cercana a la de una cueva (2 o menos de diferencia en x,y) este se movera aún la posición más cercana y libre a esta.
>
> ***

***
</br>

***
</br>

### 2. Implementación del código

En esta versión la clase Hunter y Monster son 2 hilos.

- __Clase `Hunter`__: Define el objeto para los cazadores.
  - Contenido:

    ```code
        extends Thread

        //Propiedades

        private String hunterName;
        private String position;
        private MapGame mapGame;
        private static long TIME_TO_CATCH = 20000;
        private boolean isDefeated = false;
        private int monsterCaught = 0;
        private Cave cave;
        private List<String> failedPositons;


        // Constructor por defecto
        public Hunter (){
            hunterName = "";
            position="0,0";
            mapGame = new MapGame();
            failedPositons = new ArrayList<>();

        }

        // Constructor con nombre y mapa
        public Hunter(String hunterName, MapGame mapGame) {
            this.hunterName = hunterName;
            position = "0,0";
            this.mapGame = mapGame;
            failedPositons = new ArrayList<>();
        }

        // Run
        @Override
        public void run() {
            long initialTime = System.currentTimeMillis();
            long timePassed = 0;

            mapGame.addHunter(this, this.getPosition());

            while (!isDefeated() && !mapGame.getMonsters().isEmpty() && timePassed < TIME_TO_CATCH) {
                long endTime = System.currentTimeMillis();
                timePassed = (endTime - initialTime);

                if (timePassed >= TIME_TO_CATCH) {
                    break;
                }


                if (this.isDefeated()) {
                    break;
                }

                mapGame.moveHunter(this);

                if (mapGame.nearCave(this)){
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(hunterName + " interrupted");
                }
            }
            System.out.println(this.getHunterName() + " caught: " +  this.getMonsterCaught() + " monsters" );
        }

        // Equals y toString

        // Getters y setters

    ```

- __Clase `Monster`__: Define el objeto para los monstruos.
    - Contenido:

    ```code
        extends Thread

        // Propiedades
        private int monsterId;
        private String monsterName;
        private String position;
        private boolean captured;
        private MapGame mapGame;
        private static long TIME_TO_ESCAPE = 20000;
        private int huntersDefeated = 0;
        private Cave cave;

        // Constructor por defecto
        public Monster() {
            monsterId = 1;
            monsterName = "";
            position = "";
            captured = false;
            mapGame = new MapGame();
            cave = new Cave();
        }

        // Constructor con el id, el nombre y el mapa
        public Monster(int monsterId, String monsterName, MapGame mapGame) {
            this.monsterId = monsterId;
            this.monsterName = monsterName;
            position = "";
            captured = false;
            this.mapGame = mapGame;
            cave = new Cave();
        }

        // Run
        @Override
        public void run() {
            long initialTime = System.currentTimeMillis();
            long timePassed = 0;

            mapGame.addMonster(this, this.getPosition());

            while (!isCaptured() && !mapGame.getHunters().isEmpty() && timePassed < TIME_TO_ESCAPE) {
                long endTime = System.currentTimeMillis();
                timePassed = (endTime - initialTime);

                    if (timePassed >= TIME_TO_ESCAPE) {
                        break;
                    }

                    if (this.isCaptured()){
                        break;
                    }

                    mapGame.moveMonster(this);

                    if (timePassed >= 10000 && timePassed < TIME_TO_ESCAPE) {
                        if (!this.isCaptured()) {
                            this.setCaptured(true);
                            mapGame.monsterFleeFromMap(this);
                            System.out.println(this.getMonsterName() + " has fled the field!");
                        }
                    }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println(this.getMonsterName() + " interrupted");
                }
            }
            System.out.println();
            System.out.println(this.getMonsterName() + " defeated: " + this.getHuntersDefeated() + " hunters");
        }

        // Getters y setters

        // Equals y toString
    ```

- __Clase `Cave`__: Define el objeto para las cuevas.
    - Contenido:

    ```code
        private final Semaphore semaphore;
        int capacity;
        String position;
        boolean occupied;

        public Cave() {
            this.capacity =1;
            this.position = "0,0";
            this.semaphore = new Semaphore(capacity);
            this.occupied = false;
        }

        public Cave(int capacity, String position) {
            this.capacity = capacity;
            this.position = position;
            this.semaphore = new Semaphore(capacity);
            this.occupied = false;
        }

        public void enterCave(Monster monster, MapGame mapGame) throws InterruptedException {}

        public void exitCave(Monster monster, MapGame mapGame) {}

        // Getters y setters

        // Equals y toString
    ```

- __Clase `MapGame`__: Define el tamaño del mapa, las posiciones iniciales de las trampas, cuevas, cazadores y monstruos así cómo los movimientos de estos 2 ultimos, y como las acciones que ocurren en cada posición.
    - Contenido:
    
    ```code

        // Propiedades
        private int size;
        private ConcurrentHashMap<String, String> locations;
        private static final int DEFAULT_SIZE = 10;
        private String [][] map;
        private List<Monster> monsters;
        private List<Hunter> hunters;
        private BlockingQueue<String> monstersPositionsQueue;
        private String typeTraps = "mine";
        private Set<Monster> monstersInCave;
    

        // Constructor por defecto
        public MapGame() {
            locations = new ConcurrentHashMap<>();
            size = DEFAULT_SIZE;
            map = new String[size][size];
            monsters = new CopyOnWriteArrayList<>();
            hunters = new CopyOnWriteArrayList<>();
            monstersPositionsQueue = new LinkedBlockingQueue<>();
            monstersInCave = ConcurrentHashMap.newKeySet();
            generateMap();
        }

        // Constructor con el tamaño
        public MapGame(int size) {
            this.size = size;
            locations = new ConcurrentHashMap<>();
            map = new String[size][size];
            monsters = new CopyOnWriteArrayList<>();
            hunters = new CopyOnWriteArrayList<>();
            monstersPositionsQueue = new LinkedBlockingQueue<>();
            monstersInCave = ConcurrentHashMap.newKeySet();
            generateMap();
        }


        // Funciones
    
        public String generateLocations(){}

        public void generateMap() {}

        public void showMap(){}

        public boolean checkPositionsOverlap(String position){}

        public synchronized void addHunter(Hunter hunter, String location){}

        public synchronized void moveHunter(Hunter hunter){}

        public synchronized void moveMonster(Monster monster){}


        public synchronized void addMonster(Monster monster){}

        public synchronized void removeMonsterFromMap(Hunter hunter, Monster monster){}
        
        public synchronized void removeHunterFromMap(Hunter hunter, Monster monster){}

        public synchronized void monsterFleeFromMap(Monster monster){}

        public synchronized void catchMonster(List<Monster> monsters, Hunter hunter) {}

        public synchronized void fightHunter(List<Hunter> hunters, Monster monster) {}

        public synchronized boolean nearCave(Hunter hunter){}

        // Getters y setters

        // Equals y toString
    ```

***
</br>

### 3. Soluciones añadidas

1. __Control de entrada y salida de las cuevas__: Implementación de la entrada y salida de un monstruo de de la cueva por medio de un semáforo.

2. __Patrullas de las cuevas__: Implementación del movimiento de los cazadores a la cueva ocupada más cercana si estos se encuentran en un rango cercano. 

***
</br>

### 4. Salida de la ejecucución

```code
 H  .  .  M  M  
 .  .  M  .  .  
 .  H  .  .  x  
 .  .  c  x  .  
 .  .  .  .  .  
 
 H  .  .  .  M  
 .  .  M  .  .  
 .  H  .  .  x  
 .  M  c  x  .  
 .  .  .  .  .  
 
Hunter2 has landed on a mine and died
 H Monster1 has entered the cave.
 .  .  .  M  
 .  .  M  .  .  
 .  .  .  .  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
Hunter2(0,3) patrolling near the cave at 1,3
 H  .  .  H  M  
 .  .  .  .  .  
 M  .  .  .  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
 H  .  .  H  .  
 .  .  .  .  .  
 M  .  .  .  x  
 .  .  c  .  .  
 .  .  .  .  M  
 
 .  .  .  H  .  
 .  .  .  .  .  
 M  H  .  .  x  
 .  .  c  .  .  
 .  .  .  .  M  
 
Hunter1(0,3) patrolling near the cave at 1,3
 .  .  .  H  .  
 .  .  .  .  .  
 M  .  .  .  x  
 .  .  c  .  .  
 .  .  M  .  .  
 
 M  .  .  H  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  .  c  .  .  
 .  .  M  .  .  
 
Monster1 has exited the cave.
Hunter2 caught: 0 monsters
 M  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  M  c  .  H  
 .  .  M  .  .  
 
 M  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  M  c  .  H  
 M  .  .  .  .  
 
Monster3 has entered the cave.
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  M  x  
 .  M  c  .  H  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  M  x  
 .  .  c  .  H  
 .  .  .  M  .  
 
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  H  M  x  
 .  .  c  .  .  
 .  .  .  M  .  
 
Hunter1(1,2) patrolling near the cave at 1,3
 .  .  .  .  .  
 .  .  H  .  .  
 M  .  .  .  x  
 .  .  c  .  .  
 .  .  .  M  .  
 
 .  .  .  .  .  
 .  .  H  .  .  
 M  .  .  M  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
Monster1 has fled the field!
Monster3 has exited the cave.
Hunter1 failed to catch Monster2
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  .  c  .  .  
 M  .  .  .  .  
 
 .  H  .  .  .  
 .  .  .  .  .  
 H  .  .  .  x  
 .  .  c  .  .  
 M  .  .  .  .  
 
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 M  .  c  .  .  
 M  .  .  .  .  
 
Monster2 has entered the cave.
Monster1 defeated: 0 hunters
 .  H  .  .  .  
 .  .  .  .  .  
 .  .  M  .  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
Monster3 has fled the field!
Hunter1 has been informed about a monster at: 2,0
 .  .  .  .  .  
 .  .  .  .  .  
 H  .  .  .  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  .  c  .  .  
 .  H  .  .  .  
 
 .  .  H  .  .  
 .  .  .  .  .  
 .  .  .  .  x  
 .  .  c  .  .  
 .  .  .  .  .  
 
Hunter1(1,2) patrolling near the cave at 1,3
Monster3 defeated: 0 hunters
Monster2 has exited the cave.
Monster2 has fled the field!
Monster2 defeated: 0 hunters
Hunter1 caught: 0 monsters

```


***
</br>

</div>