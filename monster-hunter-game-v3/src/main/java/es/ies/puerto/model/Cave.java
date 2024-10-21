package es.ies.puerto.model;

import java.util.concurrent.Semaphore;

public class Cave {
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

    public synchronized void enterCave(Monster monster, MapGame mapGame) throws InterruptedException {
        semaphore.acquire();
        mapGame.addMonsterToCave(monster);
        String[] position = monster.getPosition().split(",");
        int x = Integer.parseInt(position[0]);
        int y = Integer.parseInt(position[1]);
        mapGame.getMap()[x][y] = " . ";
        setOccupied(true);
        System.out.println(monster.getMonsterName() + " has entered the cave.");
    }

    public synchronized void exitCave(Monster monster, MapGame mapGame) {
        semaphore.release();
        mapGame.removeMonsterFromCave(monster);
        String[] position = monster.getPosition().split(",");
        int x = Integer.parseInt(position[0]);
        int y = Integer.parseInt(position[1]);
        mapGame.getMap()[x][y] = " M ";
        setOccupied(false);
        System.out.println(monster.getMonsterName() + " has exited the cave.");
    }

    /**
     * Getters and setters
     */
    public Semaphore getSemaphore() {
        return semaphore;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
