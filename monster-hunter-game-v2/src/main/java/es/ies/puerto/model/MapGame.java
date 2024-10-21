package es.ies.puerto.model;

import java.util.List;
import java.util.Objects;
import java.util.Random;
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
        generateMap();
        //addEvents(typeTraps, size/2);
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

    private void generateMap() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.map[i][j] = " . ";
            }
        }
    }

    public void showMap(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }


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
                hunter.setDefeated(true);
                locations.remove(hunter.getHunterName(), hunter.getPosition());
                hunters.remove(hunter);

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

    public boolean checkPositionsOverlap(String position){
        return !locations.containsValue(position);
    }
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

    public synchronized void monsterFleeFromMap(Monster monster){
        String[] positions = monster.getPosition().split(",");
        int row = Integer.parseInt(positions[0]);
        int col = Integer.parseInt(positions[1]);
        map[row][col] = " . ";

        locations.remove(monster.getMonsterName(), monster.getPosition());
        monsters.remove(monster);
    }

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
                    case "X":
                        message += " X ";
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
