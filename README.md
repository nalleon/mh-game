<div align=justify>

# Monster Hunter - Juego V2

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

Segunda versión del juego. En esta versión, los monstruos también se mueven y pueden atacar los cazadores jugando con las probabilidades. Además en el mapa abran unas minas que perjudicarán aquel que caiga en ellas.

#### 1.1 Ejemplo del flujo

> ***
> - Hay 2 monstruos y 2 cazadores.
>
> - Según el tamaño del mapa aparecen un numero de trampas en este delimitadas por el tamaño total entre 2 (ej: si el mapa tiene tamaño 5 (5x5) habrán 2 trampas). Estas trampas son unas minas que matarán a quien las pise. Una vez activadas, desaparecen.
>
> - Los monstruos se mueven también de manera aleatoria.
>
> - Si un cazador encuentra un monstruo este tiene un 70% de probabilidades de capturarlo, si falla el cazador huirá a otra posición y reportará la posición del enemigo a los otros cazadores.
> 
> - Si un monstruo en cambio es quien encuentra un cazador este atacará a su oponente teniendo un 70% de probabilidades de derrotarlo, en el caso contrario huirá a otra posición.
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

        // Constructor por defecto
        public Monster() {
            monsterId = 1;
            monsterName = "";
            position = "";
            captured = false;
            mapGame = new MapGame();
        }

        // Constructor con el id y el nombre
        public Monster(int monsterId, String monsterName) {
            this.monsterId = monsterId;
            this.monsterName = monsterName;
            position = "";
            captured = false;
            mapGame = new MapGame();
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

        public synchronized void moveMonster(Monster monster){}


        public synchronized void addMonster(Monster monster){}

        public synchronized void removeMonsterFromMap(Hunter hunter, Monster monster){}
        
        public synchronized void removeHunterFromMap(Hunter hunter, Monster monster){}

        public synchronized void monsterFleeFromMap(Monster monster){}

        public synchronized void catchMonster(List<Monster> monsters, Hunter hunter) {}

        public synchronized void fightHunter(List<Hunter> hunters, Monster monster) {}


        // Getters y setters

        // Equals y toString
    ```

***
</br>

### 3. Soluciones añadidas
   
1. **Control de movimiento aleatorio de monstruos**: Implementación del movimiento de los monstruo de manera aleatoria dentro del mapa, evitando que salgan de los límites definidos.

2. **Acción de los monstruos**: Implementación del ataque de los monstruos si estos son quienes encuentran a los cazadores.ç

3. **Comunicación entre cazadores**: Implementación de una lista de posiciones donde hay monstruos conocidas por al menos un cazador.

4. **Control de trampas**: Implementación de consecuencias al estar en la posición de una trampa.

***
</br>

### 4. Salida de la ejecucución

```code
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 H  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 H  .  .  .  .  
 .  .  .  .  .  
 H  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 H  .  .  .  .  
 .  .  .  .  M  
 H  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 
 H  .  .  .  .  
 .  .  .  .  M  
 H  .  .  .  .  
 .  M  .  .  .  
 .  .  .  .  .  
 
 .  .  .  .  .  
 .  .  H  .  M  
 H  .  x  .  .  
 .  M  .  .  .  
 .  .  x  .  .  
 
Monster2 defeated Hunter2 at 2,0
Remaining hunters: 1
 .  .  .  .  .  
 .  .  H  .  M  
 M  .  x  .  .  
 .  .  .  .  .  
 .  .  x  .  .  
 
 .  .  M  .  .  
 .  .  H  .  .  
 M  .  x  .  .  
 .  .  .  .  .  
 .  .  x  .  .  
 
Hunter2 caught: 0 monsters
 .  .  M  .  .  
 .  H  .  .  .  
 M  .  x  .  .  
 .  .  .  .  .  
 .  .  x  .  .  
 
 .  .  M  .  .  
 .  .  .  H  .  
 M  .  x  .  .  
 .  .  .  .  .  
 .  .  x  .  .  
 
 .  .  M  .  .  
 .  .  .  .  .  
 M  .  x  H  .  
 .  .  .  .  .  
 .  .  x  .  .  
 
 .  .  M  .  .  
 .  .  .  .  .  
 .  .  x  H  .  
 .  .  .  .  .  
 .  M  x  .  .  
 
 .  M  .  .  .  
 .  .  .  .  .  
 .  .  x  H  .  
 .  .  .  .  .  
 .  M  x  .  .  
 
 .  M  .  .  .  
 .  .  .  .  .  
 .  .  x  .  .  
 .  .  .  .  .  
 .  M  x  .  H  
 
 .  M  .  .  .  
 .  .  .  .  .  
 .  .  x  H  .  
 .  .  .  .  .  
 .  M  x  .  .  
 
Hunter1 has landed on a mine and died
 .  M  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  .  .  .  .  
 .  M  x  .  .  
 

Monster2 defeated: 1 hunters

Monster1 defeated: 0 hunters
Hunter1 caught: 0 monsters

Process finished with exit code 0
```

***
</br>

</div>