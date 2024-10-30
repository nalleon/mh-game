package es.ies.puerto.model;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Nabil L. A.
 */
public class MapGame {
    /**
     * Properties
     */
    private int size;
    private ConcurrentHashMap<String, String> locations;

    private static final int DEFAULT_SIZE = 10;
    private String [][] map;

    private List<Monster> monsters;
    private List<Hunter> hunters;
    private BlockingQueue<String> monstersPositionsQueue;
    private String typeTraps = "mine";
    private Set<Monster> monstersInCave;
    private List<Cave> caves;


    /**
     * Default constructor of the class
     */
    public MapGame() {
        locations = new ConcurrentHashMap<>();
        size = DEFAULT_SIZE;
        map = new String[size][size];
        monsters = new CopyOnWriteArrayList<>();
        hunters = new CopyOnWriteArrayList<>();
        monstersPositionsQueue = new LinkedBlockingQueue<>();
        monstersInCave = ConcurrentHashMap.newKeySet();
        caves = new ArrayList<>();
        generateMap();
    }

    /**
     * Constructor of the class
     * @param size of the map
     */
    public MapGame(int size) {
        this.size = size;
        locations = new ConcurrentHashMap<>();
        map = new String[size][size];
        monsters = new CopyOnWriteArrayList<>();
        hunters = new CopyOnWriteArrayList<>();
        monstersPositionsQueue = new LinkedBlockingQueue<>();
        monstersInCave = ConcurrentHashMap.newKeySet();
        caves = new ArrayList<>();
        generateMap();
        showMap();
    }


    /**
     * Function to generate the locations of the map
     * @return positions
     */
    public String generateLocations(){
        Random random = new Random();
        String location;
        do {
            int y = random.nextInt(size);
            int x = random.nextInt(size);
            location = x + "," + y;
        } while (locations.containsValue(location));

        return location;
    }

  /**
     * Function to generate the map
     */

    private void generateMap() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.map[i][j] = " . ";
            }
        }
    }

    /**
     * Function to show the map
     */
    public void showMap(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    /**
     * Funtion to generate the events on the map
     */

    public synchronized void addEvents(String type, int eventsToAdd){
        int eventsAdded = 0;

        while (eventsAdded < eventsToAdd){
            String position = generateLocations();
            String[] splitPos =position.split(",");

            if (checkPositionsOverlap(position)) {
                map[Integer.parseInt(splitPos[0])][Integer.parseInt(splitPos[1])] = " x ";
                locations.put(type, position);
                eventsAdded++;
            }

        }

    }

    /**
     * Funtion to generate the caves on the map
     */

    public synchronized void addCaves (int cavesToAdd){
        int cavesAdded = 0;

        while (cavesToAdd > cavesAdded){
            String position = generateLocations();
            String[] splitPos =position.split(",");

            if (checkPositionsOverlap(position)) {
                map[Integer.parseInt(splitPos[0])][Integer.parseInt(splitPos[1])] = " c ";
                locations.put("cave", position);
                cavesAdded++;
            }

        }
    }

    /**
     * Function to move the hunter to a new location in the map
     */
    public synchronized void moveHunter(Hunter hunter){
        Random random = new Random();
        int y;
        int x;

        String[] position = hunter.getPosition().split(",");

        String knownMonsterPosition = monstersPositionsQueue.poll();
        if (knownMonsterPosition != null) {
            String[] monsterPositionMap = knownMonsterPosition.split(",");
            x = Integer.parseInt(monsterPositionMap[0]);
            y = Integer.parseInt(monsterPositionMap[1]);
            System.out.println(hunter.getHunterName() + " has been informed about a monster at: " + x + "," + y);

        } else {
             y = random.nextInt(size);
             x = random.nextInt(size);
        }

        switch (map[x][y]){
            case " . ":
                map[x][y] = " H ";
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                hunter.setPosition(x + ","+ y);
                showMap();
                break;
            case " M ":
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                hunter.setPosition(x + ","+ y);

                if (!catchMonster(monsters, hunter)){
                    hunter.getFailedPositons().add(x+","+y);
                }

                map[x][y] = " H ";
                showMap();
                break;

            case " x ":
                hunter.setPosition(x + ","+ y);
                hunter.setDefeated(true);
                locations.remove(hunter.getHunterName(), hunter.getPosition());
                hunters.remove(hunter);
                locations.remove(typeTraps, hunter.getPosition());
                System.out.println(hunter.getHunterName() + " has landed on a mine and died");
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                map[x][y] = " . ";
                showMap();
                break;

            default:
                moveHunter(hunter);
                break;
        }

    }

     /**
     * Function to move the monster to a new location in the map
     */
    public synchronized void moveMonster(Monster monster){
        Random random = new Random();
        int y = random.nextInt(size);
        int x = random.nextInt(size);

        String[] position = monster.getPosition().split(",");
        switch (map[x][y]){
            case " . ":
                map[x][y] = " M ";
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                monster.setPosition(x + ","+ y);
                showMap();
            break;
            case " H ":
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                monster.setPosition(x + ","+ y);
                fightHunter(hunters, monster);
                map[x][y] = " M ";
                showMap();
                break;
            case " x ":
                monster.setCaptured(true);
                locations.remove(monster.getMonsterName(), monster.getPosition());
                monsters.remove(monster);

                System.out.println(monster.getMonsterName() + " has landed on a mine and died");
                map[x][y] = " . ";
                map[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = " . ";
                showMap();
                break;
            default:
                moveMonster(monster);
                break;
        }
    }

    /**
     * Function to check if position are overlapping
     */
    public boolean checkPositionsOverlap(String position){
        return !locations.containsValue(position);
    }


     /**
     * Function to add a Hunter to the map
     */
    public synchronized void addHunter(Hunter hunter, String location){
        if (checkPositionsOverlap(location)) {
            hunters.add(hunter);
            String[] positions = location.split(",");
            int row = Integer.parseInt(positions[0]);
            int col = Integer.parseInt(positions[1]);

            map[row][col] = " H ";
            locations.put(hunter.getHunterName(), location);
            showMap();
        }
    }

    /**
     * Function to add a Monster to the map
     */
    public synchronized void addMonster(Monster monster,  String location){
        if (checkPositionsOverlap(location)) {
            monsters.add(monster);
            String[] positions = location.split(",");
            int row = Integer.parseInt(positions[0]);
            int col = Integer.parseInt(positions[1]);
            map[row][col] = " M ";

            locations.put(monster.getMonsterName(), monster.getPosition());
            showMap();
        }
    }


    /**
     * Function to remove a monster from the map
     */
    public synchronized void removeMonsterFromMap(Hunter hunter, Monster monster){
        String[] positions = monster.getPosition().split(",");
        int row = Integer.parseInt(positions[0]);
        int col = Integer.parseInt(positions[1]);
        map[row][col] = " . ";

        positions = hunter.getPosition().split(",");
        row = Integer.parseInt(positions[0]);
        col = Integer.parseInt(positions[1]);
        map[row][col] = " H ";

        locations.remove(monster.getMonsterName(), monster.getPosition());
        monsters.remove(monster);
        locations.put(hunter.getHunterName(), hunter.getPosition());
    }

    /**
     * Function to remove a monster from the map when it escapes
     */
    public synchronized void monsterFleeFromMap(Monster monster){
        String[] positions = monster.getPosition().split(",");
        int row = Integer.parseInt(positions[0]);
        int col = Integer.parseInt(positions[1]);
        map[row][col] = " . ";

        locations.remove(monster.getMonsterName(), monster.getPosition());
        monsters.remove(monster);
    }


    /**
     * Function to catch a Monster if a hunter moved to its position
     */
    public synchronized void removeHunterFromMap(Hunter hunter, Monster monster){
        String[] positions = hunter.getPosition().split(",");
        int row = Integer.parseInt(positions[0]);
        int col = Integer.parseInt(positions[1]);
        map[row][col] = " . ";

        positions = monster.getPosition().split(",");
        row = Integer.parseInt(positions[0]);
        col = Integer.parseInt(positions[1]);
        map[row][col] = " M ";

        locations.remove(hunter.getHunterName(), hunter.getPosition());
        hunters.remove(hunter);
        locations.put(monster.getMonsterName(), monster.getPosition());
    }


     /**
     * Function to catch a Monster if a hunter moved to its position
     */
    public synchronized boolean catchMonster(List<Monster> monsters, Hunter hunter) {
        if (!hunters.contains(hunter)){
            return false;
        }

        Random random = new Random();

        for (int i = monsters.size() - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            if (hunter.getPosition().equals(monster.getPosition())) {
                if (random.nextInt(10)+1 < 7 ) {
                    System.out.println(hunter.getHunterName() + " caught " + monster.getMonsterName() + " at "
                            + monster.getPosition());
                    monster.setCaptured(true);
                    int monstersCounter = hunter.getMonsterCaught();
                    hunter.setMonsterCaught(monstersCounter + 1);
                    removeMonsterFromMap(hunter, monster);
                    System.out.println("Remaining monsters: " + getMonsters().size());
                    return true;
                } else {
                    System.out.println(hunter.getHunterName() + " failed to catch " + monster.getMonsterName());
                    moveHunter(hunter);
                    monstersPositionsQueue.offer(monster.getPosition());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Function to figth a Hunter if a Monster moved to its position
     */
    public synchronized void fightHunter(List<Hunter> hunters, Monster monster) {
        if (!monsters.contains(monster)){
            return;
        }

        Random random = new Random();

        for (int i = hunters.size() - 1; i >= 0; i--) {
            Hunter hunter = hunters.get(i);
            if (hunter.getPosition().equals(monster.getPosition())) {
                if (random.nextInt(10)+1 < 7 ) {
                    System.out.println(monster.getMonsterName() + " defeated " + hunter.getHunterName() + " at "
                            + monster.getPosition());
                    hunter.setDefeated(true);
                    int huntersCounter = monster.getHuntersDefeated();
                    monster.setHuntersDefeated(huntersCounter + 1);
                    removeHunterFromMap(hunter, monster);
                    System.out.println("Remaining hunters: " + getHunters().size());
                } else {
                    System.out.println(monster.getMonsterName() + " failed to defeat " + hunter.getHunterName());
                    moveMonster(monster);
                }
            }
        }
    }

    /**
     * Function to add a monster to the caves list
     */

    public synchronized void addMonsterToCave(Monster monster) {
        monstersInCave.add(monster);
    }

    
    /**
     * Function to remove a monster to the caves list
     */

    public synchronized void removeMonsterFromCave(Monster monster) {
        monstersInCave.remove(monster);
    }


    /**
     * Function to check if a monster is in a cave from the list
    */
    public boolean isMonsterInCave(Monster monster) {
        return monstersInCave.contains(monster);
    }


    /**
     * Function to check if a hunter is near an occupied cave
     */
    public synchronized boolean nearCave(Hunter hunter){
        String[] positions = hunter.getPosition().split(",");
        int x = Integer.parseInt(positions[0]);
        int y = Integer.parseInt(positions[1]);

        Cave cave = hunter.getCave();

        if (cave.isOccupied()){
            String[] positionCave = cave.getPosition().split(",");
            int caveX = Integer.parseInt(positionCave[0]);
            int caveY = Integer.parseInt(positionCave[1]);

            int differenceX = Math.abs(x-caveX);
            int differenceY = Math.abs(y-caveY);

            if (differenceX <= 2 && differenceY <= 2) {
                int newPosX = 0;
                int newPosY = 0;

                do {
                    newPosX = caveX + (int) (Math.random() * 3) - 1;
                    newPosY = caveY + (int) (Math.random() * 3) - 1;
                } while (checkPositionsOverlap(newPosX + "," + newPosY) || (newPosX == x && newPosY == y));

                hunter.setPosition(newPosX + "," + newPosY);
                map[newPosX][newPosY] = " H ";
                map[x][y] = " . ";

                locations.put(hunter.getHunterName(), hunter.getPosition());
                System.out.println(hunter.getHunterName() + "("+newPosX +"," + newPosY+") patrolling near the cave at " + caveX + "," + caveY);
                return true;
            }
        }

        return false;
    }



    /**
     * Getters/setters
     */

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ConcurrentHashMap<String, String> getLocations() {
        return locations;
    }

    public void setLocations(ConcurrentHashMap<String, String> locations) {
        this.locations = locations;
    }

    public String[][] getMap() {
        return map;
    }

    public void setMap(String[][] map) {
        this.map = map;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public List<Hunter> getHunters() {
        return hunters;
    }

    public void setHunters(List<Hunter> hunters) {
        this.hunters = hunters;
    }

    public String getTypeTraps() {
        return typeTraps;
    }

    public void setTypeTraps(String typeTraps) {
        this.typeTraps = typeTraps;
    }

    public BlockingQueue<String> getMonstersPositionsQueue() {
        return monstersPositionsQueue;
    }

    public void setMonstersPositionsQueue(BlockingQueue<String> monstersPositionsQueue) {
        this.monstersPositionsQueue = monstersPositionsQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapGame mapGame = (MapGame) o;
        return Objects.equals(locations, mapGame.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }

    @Override
    public synchronized String toString() {
        String message = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (map[i][j]) {
                    case "H":
                        message += " H ";
                        break;
                    case "M":
                        message += " M ";
                        break;
                    case "x":
                        message += " x ";
                        break;
                    case "c":
                        message += " c ";
                        break;
                    default:
                        message += " . ";
                        break;
                }
            }
            message += "\n";
        }
        return message;
    }

}
