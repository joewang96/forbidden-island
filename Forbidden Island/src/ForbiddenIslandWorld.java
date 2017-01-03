//part 2 of ForbiddenIsland
//wang, joseph
//fried, eylam


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tester.*;

import javalib.impworld.*;
import java.awt.Color; //???? colors aren't working so I imported javalib.colors
import javalib.worldimages.*; 

/** ---------------------- "BELLS AND WHISTLES" ADD-ONS -----------------------
 * ----------------------------- AND EXPLANATIONS -----------------------------
 *  - General update of the graphics (easier to tell what will flood)
 *  - Different character "sprites"
 *      - ones for running and standing (also allows for a "running animation")
 *      - ones for direction facing (left, right, up, down)
 *      - added scuba sprites with directions and running/standing "animations"
 * - Added the Scuba suit mechanic to the game
 * - Added optional oxygen tanks which give the user more time to use the scuba
 * - Added different end game scenes (for winning, losing by drowning, and 
 *                                    losing by having the plane be submerged)
 * - Added blue overlays to notify you are swimming
 * - Added an overlay which notifies you which game mode you are playing
 * - Added a red overlay to show you are running out of oxygen
 * - Added a step counter
 * - Added a timer which counts how much time is left until the island sinks
 * - Added keys which show you what the controls are
 *     - Additionally, the corresponding key flashes when you press that key 
 *         ** (note: s-key doesn't flash, but is red when scuba is active) 
 *         
 *                                         
 * ------------------------------ GAME GUIDE --------------------------------
 *  You start on a random land cell and must collect all three helicopter parts 
 *  to repair your helicopter and get off the island. On the island there is 
 *  also a diving suit and oxygen tanks which the player can pick up. Once
 *  the player has the scuba suit, simply press "S" in order to activate it.
 *  When using the suit the player uses the oxygen in the suit's tank, which
 *  depletes the oxygen supply. Upon reaching 0 the suit will shut off. Note 
 *  you cannot activate the suit when you have 0 oxygen left. The amount of 
 *  oxygen remaining is displayed in seconds on the top left of the screen along
 *  with a step counter, timer until the island fully sinks, and whether the 
 *  scuba suit is active or not.
 *  
 *  There are two ways to lose the game, the first is drowning, either through 
 *  the player's cell flooding or by running out of oxygen in a flooded cell
 *  (the scuba suit would turn off and the player would drown in the flooded
 *  cell). The other is if the helicopter is submerged, since the player cannot
 *  start a helicopter that is under water.
 *  
 *  To win the game collect all three (3) helicopter parts and make it back to
 *  the helicopter (located on the highest point of the map).
 */

/** ------------------------------------------------------------------------**/
/** -------------------- SETTING UP LISTS AND ITERATORS---------------------**/

//to represent a list
interface IList<T> extends Iterable<T> {

    //is this list the same as that list
    boolean sameList(IList<T> list);

    //is the cons list the same as that one
    boolean sameConsList(ConsList<T> other);

    //is the empty list the same as that one
    boolean sameMtList(MtList<T> other);

    //does this list contain the given object
    boolean contains(T t);

    //is this list a cons
    boolean isCons();

    //return the given list as a cons
    ConsList<T> asCons();

    //return the iterator 
    Iterator<T> iterator();

    //add the given item to be the first of this list
    IList<T> add(T t);

    //return the length of this list
    int length();

    //remove the given item from the list
    IList<T> remove(T t);
}

//to represent an empty list 
class MtList<T> implements IList<T> {

    //is this empty the same as that list
    public boolean sameList(IList<T> list) {
        return list.sameMtList(this);
    } 

    //is this empty list the same as that cons
    public boolean sameConsList(ConsList<T> other) {
        return false;
    }

    //is this empty list the same as that one
    public boolean sameMtList(MtList<T> other) {
        return true;
    }

    //does this list contain the given object
    public boolean contains(T t) {
        return false;
    }

    //return the iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    //is this list a cons
    public boolean isCons() {
        return false;
    }

    //return the given list as a cons
    public ConsList<T> asCons() {
        throw new ClassCastException("MtList<T> cannot be cast ot ConsList<T>");
    }

    //add the given item to be the first of this list
    public IList<T> add(T t) {
        return new ConsList<T>(t, this);
    }

    //return the length of this IList
    public int length() {
        return 0;
    }

    //remove the given item from this list
    public IList<T> remove(T t) {
        return new MtList<T>();
    }
}


//to represent a cons list
class ConsList<T> implements IList<T> {
    T first;
    IList<T> rest;

    ConsList(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    //is this list the same as that list
    public boolean sameList(IList<T> list) {
        return list.sameConsList(this);
    }

    //is this cons list the same as that cons list
    public boolean sameConsList(ConsList<T> other) {
        return this.first.equals(other.first) && other.rest.sameList(this.rest);
    }

    //is this cons list the same as that empty one
    public boolean sameMtList(MtList<T> other) {
        return false;
    }

    //does this list contain the given object
    public boolean contains(T t) {
        return (t.equals(this.first)) || this.rest.contains(t);
    }

    //return the iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    //is this a cons
    public boolean isCons() {
        return true;
    }

    //return this as a cons list
    public ConsList<T> asCons() {
        return this;
    }

    //add the given item to this cons
    public IList<T> add(T t) {
        return new ConsList<T>(t, this);
    }

    //return the length of this cons list
    public int length() {
        return 1 + this.rest.length();
    }

    //remove the given item 
    public IList<T> remove(T t) {
        if (t == this.first) {
            return this.rest;
        }
        else {
            return new ConsList<T>(this.first, this.rest.remove(t));
        }
    }

}


//to represent an iterator
class IListIterator<T> implements Iterator<T> {
    IList<T> items;

    IListIterator(IList<T> items) {
        this.items = items;
    }

    //does this iterator have a next
    public boolean hasNext() {  
        return this.items.isCons();
    }

    //return the next item
    public T next() {
        if (!this.hasNext()) {
            throw new RuntimeException("No Next Item");
        }
        else {
            ConsList<T> cons1 = this.items.asCons();
            T result = cons1.first;
            this.items = cons1.rest;
            return result;
        }
    }

    //remove this
    public void remove() {
        throw new 
        UnsupportedOperationException("Why did it have to come to this??");
    }
}

/** ------------------------------------------------------------------------**/
/** ----------------- SETTING CLASSES FOR GAME COMPONENTS ------------------**/

//to represent a cell
class Cell {
    double height;
    int x;
    int y;
    Cell left;
    Cell top;
    Cell right;
    Cell bottom;
    boolean isFlooded;

    Cell(double height, int x, int y) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.isFlooded = false;
        this.left = null;
        this.right = null;
        this.top = null;
        this.bottom = null;
    }

    //draw the cell
    public WorldImage cellImage(int waterHeight) {
        
        Color color;
        if (this.isFlooded) {
            color = new Color(0, 0, 
                    (int)Math.min(254, 254 - (waterHeight - this.height) * 5));
        }
        else if (this.height <= waterHeight) {
            color = new Color(
                    (int)Math.min(150, 200 - (waterHeight - this.height) * 7),
                    70 - waterHeight * 2, 0);
        }
        else {
            color = new Color(
                    (int)Math.min(255, (this.height - waterHeight) * 7), 
                    (int)Math.min(255, 203 + (this.height - waterHeight) * 6),
                    (int)Math.min(255, (this.height - waterHeight) * 7));    
        }
        
        return new RectangleImage(10, 10, OutlineMode.SOLID, color);      
    }

    //update this cell on the left
    //EFFECT: change this.left to the given one
    void newLeft(Cell left) {
        this.left = left;
    }

    //update this cell on the right
    //EFFECT: change this.right to the given one
    void newRight(Cell right) {
        this.right = right;
    }

    //update this cell on top
    //EFFECT: change this.top to the given one
    void newTop(Cell top) {
        this.top = top;
    }

    //update this cell on the bottom
    //EFFECT: change this.left to the given one
    void newBottom(Cell bottom) {
        this.bottom = bottom;
    }

    //is the cell next to this one flooded but this one is not
    boolean stillUp() {
        return !this.isFlooded &&
                (this.left.isFlooded ||
                        this.right.isFlooded ||
                        this.top.isFlooded ||
                        this.bottom.isFlooded);
    }

    //flood this cell if it is under the waterHeight and not already flooded
    //also flood adjacent cells with the same properties (to simulate real 
    //                                                                flooding)
    //EFFECT: changes this.isFlooded boolean flag for this cell
    void flood(int waterHeight) {
        if (this.height <= waterHeight) {
            this.isFlooded = true;
            if (!this.left.isFlooded) { 
                this.left.flood(waterHeight);
            }
            if (!this.right.isFlooded) { 
                this.right.flood(waterHeight);
            }
            if (!this.top.isFlooded) { 
                this.top.flood(waterHeight);
            }
            if (!this.bottom.isFlooded) { 
                this.bottom.flood(waterHeight);
            }
        }
    }

}

//Represents a ocean variant of a square of the game area
class OceanCell extends Cell {

    //the constructor
    OceanCell(double height, int x, int y) {
        super(height, x, y);
        this.isFlooded = true;
    }
    
    public WorldImage cellImage(int waterHeight) {
        return new RectangleImage(10, 10, OutlineMode.SOLID, 
                new Color(0, 0, 255));
    }
}

// to represent the pilot (the player) on the island
class Pilot {
    int x;  // expressed in terms of cells
    int y;  // expressed in terms of cells
    boolean run; // tells if the player is in motion
    String dir; // tells which direction the player is in
    
    Pilot(int x, int y) {
        this.x = x;
        this.y = y;
        this.run = false;
        this.dir = "down";
    }
    
    // returns the image of the pilot, and if the given boolean is true he is
    // drawn as a diver (the boolean will be if the diving suit is active)
    public WorldImage drawPilot(boolean swim) {
        if (swim) {
            if (run) {
                if (dir.equals("up")) {
                    return new FromFileImage("scuba-r-up.png");
                }
                else if (dir.equals("down")) {
                    return new FromFileImage("scuba-r-down.png");
                }
                else if (dir.equals("left")) {
                    return new FromFileImage("scuba-r-left.png");
                }
                else {
                    return new FromFileImage("scuba-r-right.png");
                }
            }
            else {
                if (dir.equals("up")) {
                    return new FromFileImage("scuba-s-up.png");
                }
                else if (dir.equals("down")) {
                    return new FromFileImage("scuba-s-down.png");
                }
                else if (dir.equals("left")) {
                    return new FromFileImage("scuba-s-left.png");
                }
                else {
                    return new FromFileImage("scuba-s-right.png");
                }
            }
        }
        else if (run) {
            if (dir.equals("up")) {
                return new FromFileImage("running-up.png");
            }
            else if (dir.equals("down")) {
                return new FromFileImage("running-down.png");
            }
            else if (dir.equals("left")) {
                return new FromFileImage("running-left.png");
            }
            else {
                return new FromFileImage("running-right.png");
            }
        }
        else {
            if (dir.equals("up")) {
                return new FromFileImage("standing-top.png");
            }
            else if (dir.equals("down")) {
                return new FromFileImage("standing-down.png");
            }
            else if (dir.equals("left")) {
                return new FromFileImage("standing-left.png");
            }
            else {
                return new FromFileImage("standing-right.png");
            }
        }
    }
    
    // converts the x cell coordinates with that of pixels
    public int xToPixel() {
        return this.x * 10 + 5;
    }
    
    // converts the y cell coordinates with that of pixels
    public int yToPixel() {
        return this.y * 10 + 5;
    }
   
}

// represents the targets which the player must pick up
class Target {
    int x;
    int y;
    boolean available;
    
    Target(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // draws the base target image (used for helicopter parts)
    public WorldImage drawTarget() {
        return new FromFileImage("gear-icon.png");
    }
    
    // converts the x cell coordinates with that of pixels
    public int xToPixel() {
        return this.x * 10 + 5;
    }
    
    // converts the y cell coordinates with that of pixels
    public int yToPixel() {
        return this.y * 10 + 5;
    }
    
    // checks if available (always true)
    public boolean isAvailable() {
        return true;
    }
}

// to represent the helicopter the player needs to get to in order to win
class Helicopter extends Target {
    boolean available;
    
    Helicopter(int x, int y) {
        super(x, y);
        this.available = true;
    }
    
    // draws the helicopter target (using the provided helicopter icon)
    public WorldImage drawTarget() {
        return new FromFileImage("helicopter.png");
    }
    
    // checks if available, always false (a side effect later in the code is
    // used to override this when all pieces of the helicopter are collected
    public boolean isAvailable() {
        return false;
    }
}

// to represent the scuba gear the player can use to swim in the ocean
class Scuba extends Target {
    boolean available;
    
    Scuba(int x, int y) {
        super(x, y);
        this.available = true;
    }
    
    // draws the scuba gear (using the provided diving helmet icon)
    public WorldImage drawTarget() {
        return new FromFileImage("diving-helmet.png");
    }
}

// to represent extra oxygen tanks
class OxygenTank extends Target {
    boolean available;
    
    OxygenTank(int x, int y) {
        super(x, y);
        this.available = true;
    }
    
    // draws the oxygen tanks (using the provided scuba tank icons)
    public WorldImage drawTarget() {
        return new FromFileImage("scuba-tank.png");
    }
}


/** ------------------------------------------------------------------------**/
/** ----------------------- NOW FOR THE ACTUAL GAME ------------------------**/

// to represent the World of our game
class ForbiddenIslandWorld extends World {
    static final int ISLAND_SIZE = 64;
    static final double BOARD_HEIGHT = 32;
    IList<Cell> board;
    int waterHeight;
    Random rand;
    int count;
    Pilot player;
    IList<Target> objectives;  // includes the helicopter and helicopter parts
    int steps;  
    int timer;  // represented in seconds
    IList<Scuba> scuba;  
    IList<OxygenTank> o2tanks;
    boolean canSwim; 
    int oxygenLeft; // represented in seconds
    String mode;
    String lastKey;

    //the constructor
    ForbiddenIslandWorld() {
        this.waterHeight = 0;
        this.rand = new Random();
        this.board = this.makeBoard(this.createCells(this.makeTerrain(
                ISLAND_SIZE)));
        this.count = 0;
        this.mode = "terrain";
        this.reset();
    }
    
    /** --------------------------------------------------------------------**/
    /** ------------------ SETTING INITIAL CONDITIONS ----------------------**/
    
    // resets the player, objectives, counter, timers, etc.
    // EFFECT: Initializes all player and game piece components
    public void reset() {
        this.player = this.startPlayer();
        this.objectives = new ConsList<Target>(this.startTarget(), 
                new ConsList<Target>(this.startTarget(), new 
                        ConsList<Target>(this.startTarget(), new 
                        ConsList<Target>(new Helicopter(this.highestPoint().x, 
                                this.highestPoint().y), 
                                new MtList<Target>()))));
        this.steps = 0;
        this.timer = (int)((this.highestPoint().height * 2) + 2);
        this.scuba = new ConsList<Scuba>(this.startScuba(), 
                new MtList<Scuba>());
        this.canSwim = false;
        this.oxygenLeft = 0;
        this.o2tanks = new ConsList<OxygenTank>(this.startO2(), 
                new ConsList<OxygenTank>(this.startO2(), new 
                        ConsList<OxygenTank>(this.startO2(), 
                                new MtList<OxygenTank>())));
        this.lastKey = "none";
    }
    
    // creates a player with a random start position (on land)
    public Pilot startPlayer() {
        Cell start = this.randomLand();
        return new Pilot(start.x, start.y); 
    }
    
    // creates a helicopter part with a random start position (on land)
    public Target startTarget() {
        Cell start = this.randomLand();
        return new Target(start.x, start.y); 
    }
    
    // creates the helicopter starting at the highest point on the island
    public Helicopter startHeli() {
        Cell start = this.highestPoint();
        return new Helicopter(start.x, start.y);
    }
    
    // places the scuba in a random spot on land
    public Scuba startScuba() {
        Cell start = this.randomLand();
        return new Scuba(start.x, start.y);
    }
    
    // places the oxygen tanks in a random spot on the island
    public OxygenTank startO2() {
        Cell start = this.randomLand();
        return new OxygenTank(start.x, start.y);
    }
    
    // finds the highest point on the island
    public Cell highestPoint() {
        Iterator<Cell> iter = this.board.iterator();
        Cell highest = iter.next();
        while (iter.hasNext()) {
            Cell nextCell = iter.next();
            if (nextCell.height > highest.height) {
                highest = nextCell;
            }
        }
        return highest;
    }
    
    // returns a random land cell (used to place the player and parts)
    public Cell randomLand() {
        Cell land = this.randomCell();
        if (land.isFlooded) {
            return this.randomLand();
        }
        else {
            return land;
        }
    }

    /** --------------------------------------------------------------------**/
    /** ----------------------- MAKING THE BOARD ---------------------------**/

    //pick a random cell from the ones on land
    public Cell randomCell() {
        IList<Cell> landCells = this.landCells();
        int index = this.rand.nextInt(landCells.length());
        Iterator<Cell> landIterator = landCells.iterator();
        Cell cell1 = landIterator.next();
        index -= 1;
        while (index >= 0) {
            Cell current = landIterator.next();
            if (index == 0) {
                if (current.stillUp()) {
                    landIterator = landCells.iterator();
                    index = this.rand.nextInt(landCells.length());
                }
                else {
                    cell1 = current;                    
                }
            }
            index -= 1;
        }   

        return cell1;
    }

    //create a list of all the cells not flooded
    IList<Cell> landCells() {
        Iterator<Cell> iter = this.board.iterator();
        IList<Cell> result = new MtList<Cell>();
        while (iter.hasNext()) {
            Cell next = iter.next();
            if (!next.isFlooded &&
                    (!next.left.isFlooded ||
                            !next.right.isFlooded ||
                            !next.bottom.isFlooded ||
                            !next.top.isFlooded)) {
                result = result.add(next);
            }
        }
        return result; 
    }

    //turn the ArrayList<ArrayList<Cell>> into an IList
    IList<Cell> makeBoard(ArrayList<ArrayList<Cell>> cells) {
        IList<Cell> result = new MtList<Cell>();
        for (int index = 0; index < cells.size(); index += 1) {
            for (int index2 = 0; index2 < cells.get(index).size(); 
                    index2 += 1) {
                result = result.add(cells.get(index).get(index2));
            }   
        }
        return result;
    }

    //ArrayList<ArrayList<Double>> --> ArrayList<ArrayList<Cell>>
    ArrayList<ArrayList<Cell>> createCells(ArrayList<ArrayList<Double>> 
    height) {
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
        for (int index = 0; index < height.size(); index += 1) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            result.add(row);        
            for (int index2 = 0; index2 < height.get(index).size(); 
                    index2 += 1) {
                if (height.get(index).get(index2) > 0) {
                    result.get(index).add(
                            new Cell(height.get(index).get(index2).intValue(), 
                                    index2, index));
                }
                else {
                    result.get(index).add(new OceanCell(0, 
                            index2, index));  
                }  
            }                   
        }
        this.connectCells(result);
        return result;
    }


    //create an ArrayList<ArrayList<Double>> for a mountains
    ArrayList<ArrayList<Double>> makeMountains(int size) {
        ArrayList<ArrayList<Double>> result = 
                new ArrayList<ArrayList<Double>>();
        int centerX = size / 2 - 1; 
        int centerY = size / 2 - 1;
        for (int index = 0; index < size; index += 1) {
            ArrayList<Double> row = new ArrayList<Double>();
            result.add(row);
            for (int index2 = 0; index2 < size; index2 += 1) {
                result.get(index).add(BOARD_HEIGHT - (Math.abs(centerX - index2) 
                        + Math.abs(centerY - index)));
            }
        }    
        return result;
    }

    //compute an ArrayList<ArrayList<Double>> for a random board  
    ArrayList<ArrayList<Double>> makeRandom(int size) {
        ArrayList<ArrayList<Double>> result = 
                new ArrayList<ArrayList<Double>>();
        double temp;
        int centerX = ISLAND_SIZE / 2 - 1; 
        int centerY = ISLAND_SIZE / 2 - 1;
        for (int index = 0; index < size; index += 1) {
            ArrayList<Double> row = new ArrayList<Double>();
            result.add(row);
            for (int index2 = 0; index2 < size; index2 += 1) {
                temp = BOARD_HEIGHT - (Math.abs(centerX - index2) 
                        + Math.abs(centerY - index));
                if (temp <= 0.0) {
                    result.get(index).add(0.0);   
                }
                else {
                    result.get(index).add((double)rand.nextInt(30) + 1);                    
                }
            }
        }    
        return result;
    }

    //compute an ArrayList<ArrayList<Double>> for a board of size size
    ArrayList<ArrayList<Double>> makeTerrain(int size) {
        ArrayList<ArrayList<Double>> result = 
                new ArrayList<ArrayList<Double>>();
        for (int index = 0; index <= size; index += 1) {
            ArrayList<Double> row = new ArrayList<Double>();
            result.add(row);
            for (int index2 = 0; index2 <= size; index2 += 1) {
                result.get(index).add(0.0);
            }
        }
        //make top heights
        int mid = size / 2;
        result.get(0).set(mid, 1.0);
        result.get(mid).set(size - 1, 1.0);
        result.get(mid).set(0, 1.0);
        result.get(size - 1).set(mid, 1.0);
        result.get(size / 2).set(size / 2, BOARD_HEIGHT);
        this.makeTerrainHelp(result, 0, 0, size - 1, size - 1);

        return result;
    }

    //give values to random land
    //EFFECT: Sets up the initial conditions for making a random terrain
    void makeTerrainHelp(ArrayList<ArrayList<Double>> input, int bottomX, 
            int bottomY, int topX, int topY) {

        //midpoints to recur on
        int midX = (int)Math.ceil((bottomX + topX) / 2.0);
        int midY = (int)Math.ceil((bottomY + topY) / 2.0);
        int area = (topY - bottomY) * (topX - bottomX);
        double scale = Math.sqrt(area / Math.pow(ISLAND_SIZE, 1.96));

        //base case (do not recur if the size of area is too small)
        if (area < 2) {
            return;
        }
        //assign values and recur
        else {

            //heights of corners and midpoints
            double tl = input.get(bottomY).get(bottomX);
            double bl = input.get(topY).get(bottomX);
            double tr = input.get(bottomY).get(topX);
            double br = input.get(topY).get(topX);
            double t = (this.rand.nextInt((int)(BOARD_HEIGHT / 1.25)) - 
                    BOARD_HEIGHT / 4) * scale +
                    (tl + tr) / 2;
            double b = (this.rand.nextInt((int)(BOARD_HEIGHT / 1.25)) - 
                    BOARD_HEIGHT / 4) * scale +
                    (bl + br) / 2;
            double l = (this.rand.nextInt((int)(BOARD_HEIGHT / 1.25)) - 
                    BOARD_HEIGHT / 4) * scale +
                    (tl + bl) / 2;
            double r = (this.rand.nextInt((int)(BOARD_HEIGHT / 1.25)) - 
                    BOARD_HEIGHT / 4) * scale +
                    (tr + br) / 2;
            double m = (this.rand.nextInt((int)(BOARD_HEIGHT / 1.25)) - 
                    BOARD_HEIGHT / 5) * scale +
                    (tl + tr + bl + br) / 4;

            //middle
            input.get(midY).set(midX, m); 
            //top
            input.get(bottomY).set(midX, t);
            //left
            input.get(midY).set(bottomX, l);

            if (input.get(midY).get(topX) == 0) {
                input.get(midY).set(topX, r);
            }
            if (input.get(topY).get(midX) == 0) {
                input.get(topY).set(midX, b);
            }

            //first
            this.makeTerrainHelp(input, bottomX, bottomY, midX, midY);

            this.makeTerrainHelp(input, midX, bottomY, topX, midY);

            this.makeTerrainHelp(input, midX, midY, topX, topY);

            this.makeTerrainHelp(input, bottomX, midY, midX, topY);
        }
    } 


    // list of elements not yet flooded
    public IList<Cell> notFloodedCell() {
        IList<Cell> mtLoners = new MtList<Cell>();
        Iterator<Cell> iter = this.board.iterator();

        while (iter.hasNext()) {
            Cell nextCell = iter.next();
            if (nextCell.stillUp()) {
                mtLoners = mtLoners.add(nextCell);
            }
        }

        return mtLoners;    
    }

    // flood the still up cells
    //EFFECT: change the boolean and increase waterHeight
    public void flooding() {
        Iterator<Cell> iter = this.notFloodedCell().iterator();

        while (iter.hasNext()) {
            Cell nextCell = iter.next();
            nextCell.flood(this.waterHeight);
        }
        this.waterHeight = this.waterHeight + 1;
    }


    // connect all the cells in an ArrayList<ArrayList<Cells>> cells
    // EFFECT: change left, right, top and bottom cells of each cell in cells
    public void connectCells(ArrayList<ArrayList<Cell>> cells) {
        for (int index = 0; index < cells.size(); index += 1) {           
            for (int index2 = 0; index2 < cells.size(); index2 += 1) {                
                Cell current = cells.get(index).get(index2);

                if (index2 > 0) {
                    current.newLeft(cells.get(index).get(index2 - 1));
                }
                else {
                    current.newLeft(current);
                }
                if (index2 < cells.size() - 1) {
                    current.newRight(cells.get(index).get(index2 + 1));
                }
                else {
                    current.newRight(current);
                }
                if (index > 0) {
                    current.newTop(cells.get(index - 1).get(index2));
                }
                else {
                    current.newTop(current);
                }
                if (index < cells.size() - 1) {
                    current.newBottom(cells.get(index + 1).get(index2));
                }
                else {
                    current.newBottom(current);
                }
            }           
        }
    }
    
    
    /** --------------------------------------------------------------------**/
    /** ----------------------- ON KEY MECHANICS ---------------------------**/
    
    //change map type and moves the character
    public void onKeyEvent(String ke) {
        if (ke.equals("m")) { // resets the game as a "mountain"
            this.waterHeight = 0;
            this.board = this.makeBoard(this.createCells(
                    this.makeMountains(ISLAND_SIZE)));
            this.count = 0;
            this.mode = "mountain";
            this.reset();
        } 
        else if (ke.equals("r")) { // resets the game as a random diamond shape
            this.waterHeight = 0;
            this.board = this.makeBoard(this.createCells(
                    this.makeRandom(ISLAND_SIZE)));
            this.count = 0;
            this.mode = "random";
            this.reset();
        } 
        else if (ke.equals("t")) { // resets the game as a "random terrain"
            this.waterHeight = 0;
            this.board = this.makeBoard(this.createCells(
                    this.makeTerrain(ISLAND_SIZE)));
            this.count = 0;
            this.mode = "terrain";
            this.reset();
        }
        // uses scuba if you have scuba gear and have oxygen to use it
        else if (ke.equals("s") && this.hasScuba() && !this.gameEnded() &&
                this.oxygenLeft > 0) {
            this.canSwim = !this.canSwim;
        }
        // move left
        else if (ke.equals("left") && (!this.playerCell().left.isFlooded || 
                this.canSwim) && !this.gameEnded() && this.player.x != 0) {
            this.player.x = this.player.x - 1;
            this.steps = steps + 1;
            this.player.dir = "left";
            this.player.run = true;
            this.lastKey = "left";
        }
        // move right
        else if (ke.equals("right") && (!this.playerCell().right.isFlooded || 
                this.canSwim) && !this.gameEnded() && this.player.x != 
                ISLAND_SIZE - 1) {
            this.player.x = this.player.x + 1;
            this.steps = steps + 1;
            this.player.dir = "right";
            this.player.run = true;
            this.lastKey = "right";
        }
        // move up
        else if (ke.equals("up") && (!this.playerCell().top.isFlooded || 
                this.canSwim) && !this.gameEnded() && this.player.y != 0) {
            this.player.y = this.player.y - 1;
            this.steps = steps + 1;
            this.player.dir = "up";
            this.player.run = true;
            this.lastKey = "up";
        }
        // move down
        else if (ke.equals("down") && (!this.playerCell().bottom.isFlooded || 
                this.canSwim) && !this.gameEnded() && this.player.y != 
                ISLAND_SIZE - 1) {
            this.player.y = this.player.y + 1;
            this.steps = steps + 1;
            this.player.dir = "down";
            this.player.run = true;
            this.lastKey = "down";
        }
        else {
            return;
        }
    }

    /** --------------------------------------------------------------------**/
    /** ------------------------- RENDERING --------------------------------**/
    
    // creates the text used for signifying if you can swim or not
    public WorldImage swimText() {
        if (this.scuba.length() == 0) {
            return new TextImage("Scuba: Available", 17, Color.WHITE);
        }
        else {
            return new TextImage("Scuba: Not Found ", 17, Color.WHITE);
        }
    }
    
    // sets a transparency over the scene (notifies you are swimming)
    public void overlaySwim(WorldScene bg) {
        bg.placeImageXY(new RectangleImage(640, 640, OutlineMode.SOLID, 
                new Color(0, 0, 200, 100)), 320, 320);
        bg.placeImageXY(new TextImage("Diving Suit ON", 25, Color.WHITE), 320, 
                160);
        
        // pulses the screen red if you are about to run out of oxygen and
        // displays the text "LOW 02 LEVELS"
        if (0 <= this.oxygenLeft && this.oxygenLeft <= 6 && 
                (this.oxygenLeft % 2 == 0)) {
            bg.placeImageXY(new RectangleImage(640, 640, OutlineMode.SOLID, 
                    new Color(200, 0, 0, 100)), 320, 320);
            bg.placeImageXY(new TextImage("[ LOW 02 LEVELS ]", 20, Color.WHITE), 
                    320, 320);
        }
    }
    
    // notifies the player what kind of game mode they are currently playing
    public void displayGameMode(WorldScene bg) {
        if (this.mode.equals("terrain")) {
            bg.placeImageXY(new TextImage("Game Mode: Terrain", 17,
                    Color.WHITE), 525, 20);
        }
        else if (this.mode.equals("mountain")) {
            bg.placeImageXY(new TextImage("Game Mode: Mountain", 17,
                    Color.WHITE), 525, 20);
        }
        else {
            bg.placeImageXY(new TextImage("Game Mode: Random Mt.", 17,
                    Color.WHITE), 525, 20);
        }
    }

    // creates the scene of the game
    public WorldScene makeScene() {
        // sets the base scene
        WorldScene bg = new WorldScene(ISLAND_SIZE * 10, ISLAND_SIZE * 10);
        
        // draws the board
        Iterator<Cell> iter = this.board.iterator();
        while (iter.hasNext()) {
            Cell nextCell = iter.next();
            bg.placeImageXY(nextCell.cellImage(waterHeight), 
                    (nextCell.x * 10) + 5, (nextCell.y * 10) + 5);
        }
        
        // draws the pilot, and if he has the scuba on he is drawn as a diver
        bg.placeImageXY(this.player.drawPilot(this.canSwim), 
                this.player.xToPixel(), this.player.yToPixel());
        
        // draws the helicopter and its parts
        Iterator<Target> iter2 = this.objectives.iterator();
        while (iter2.hasNext()) {
            Target nextTarget = iter2.next();
            bg.placeImageXY(nextTarget.drawTarget(), nextTarget.xToPixel(), 
                    nextTarget.yToPixel());
        }
        
        // draws the scuba tank
        Iterator<Scuba> iter3 = this.scuba.iterator();
        while (iter3.hasNext()) {
            Scuba nextScuba = iter3.next();
            bg.placeImageXY(nextScuba.drawTarget(), nextScuba.xToPixel(), 
                    nextScuba.yToPixel());
        }
        
        // draws the oxygen tanks
        Iterator<OxygenTank> iter4 = this.o2tanks.iterator();
        while (iter4.hasNext()) {
            OxygenTank nextO2 = iter4.next();
            bg.placeImageXY(nextO2.drawTarget(), nextO2.xToPixel(), 
                    nextO2.yToPixel());
        }
        
        // ------------------ Head's Up Display (HUD) ------------------
        
        // places a background for the "head's up display" in the top left
        bg.placeImageXY(new RectangleImage(151, 89, OutlineMode.SOLID, 
                new Color(0, 0, 0, 50)), 80, 54);
        
        // displays the step count
        bg.placeImageXY(new TextImage("Steps taken: " + Integer.toString(steps)
            , 17, Color.WHITE), 80, 21);
        
        // displays the time left
        bg.placeImageXY((new TextImage("Time Left: " + Double.toString(timer),
                17, Color.WHITE)), 80, 42);
        
        // displays the text saying if they can swim or not
        bg.placeImageXY(this.swimText(), 80, 63);
        
        // displays the oxygen remaining
        bg.placeImageXY((new TextImage("O2 Remaining: " + 
            Integer.toString(oxygenLeft), 17, Color.WHITE)), 80, 84);
        
        // places a transparent background for the HUD for game mode
        bg.placeImageXY(new RectangleImage(206, 20, OutlineMode.SOLID,
                new Color(0, 0, 0, 50)), 525, 20);
        
        // displays the current game mode
        this.displayGameMode(bg);
        
        // overlays a blue transparency when the player is swimming
        if (this.canSwim && !this.gameEnded()) {
            this.overlaySwim(bg);
        }
        
        /** -------------------- PLAYER CONTROLS --------------------------**/
        // displays a key image in the corner to help indicate player controls
        bg.placeImageXY(new FromFileImage("arrow-keys.png"), 575, 600);
        
        // displays the s key for swimming to indicate player controls
        bg.placeImageXY(new FromFileImage("s-key.png"), 24, 618);
        
        // highlights the appropriate arrow key whenever a player presses a key
        if (this.lastKey.equals("up")) {
            bg.placeImageXY(new RectangleImage(27, 29, OutlineMode.SOLID, 
                    new Color(255, 0, 0, 100)), 576, 583);
        }
        if (this.lastKey.equals("down")) {
            bg.placeImageXY(new RectangleImage(27, 29, OutlineMode.SOLID, 
                    new Color(255, 0, 0, 100)), 576, 616);
        }
        if (this.lastKey.equals("left")) {
            bg.placeImageXY(new RectangleImage(27, 29, OutlineMode.SOLID, 
                    new Color(255, 0, 0, 100)), 545, 616);
        }
        if (this.lastKey.equals("right")) {
            bg.placeImageXY(new RectangleImage(27, 29, OutlineMode.SOLID, 
                    new Color(255, 0, 0, 100)), 609, 616);
        }
        if (this.canSwim) {
            bg.placeImageXY(new RectangleImage(32, 32, OutlineMode.SOLID, 
                    new Color(255, 0, 0, 100)), 24, 618);
        }
                
        /** ---------------------------------------------------------------**/
        
        // checks for the different win or lose conditions to display the 
        // appropriate screens on each scenario
        if (this.winCondition()) {
            return this.winScreen(bg);  // found in END GAME MECHANICS
        }
        else if (this.loseCondition()) {
            return this.loseScreen(bg);  // found in END GAME MECHANICS
        }
        else if (this.heliSubmerged()) {
            return this.lostHeliScreen(bg);   // found in END GAME MECHANICS
        }
        else {
            return bg;
        }
    }
    
    /** --------------------------------------------------------------------**/
    /** -----------------------  SCUBA MECHANICS ---------------------------**/
    
    // says if you have the scuba gear or not
    public boolean hasScuba() {
        return this.scuba.length() == 0;
    }
    
    // decreases the amount of oxygen you have left when you have the scuba on
    // if you have no oxygen, then you can't swim
    // EFFECT: decreases the oxygenLeft if it is > 0 and sets canSwim to false
    //         if you don't have any oxygen left
    public void useOxygen() {
        if (this.canSwim && !this.gameEnded()) {
            if (this.oxygenLeft > 0) {
                this.oxygenLeft = this.oxygenLeft - 1;
            }
            else {
                this.canSwim = false;
            }
        }
    }
    
    /** --------------------------------------------------------------------**/
    /** ---------------------- ON TICK MECHANICS --------------------------**/
    
    // checks if the player is on a target
    // EFFECT: If on a target, removes that target you are on from objectives
    public void onTarget() {
        Iterator<Target> iter = this.objectives.iterator();
        while (iter.hasNext()) {
            Target nextTarget = iter.next();
            if (nextTarget instanceof Helicopter && nextTarget.x == 
                    this.player.x && nextTarget.y == this.player.y) {
                if (this.objectives.length() == 1) {
                    this.objectives = this.objectives.remove(nextTarget);
                }
            }
            else if (nextTarget.x == this.player.x && 
                    nextTarget.y == this.player.y && nextTarget.isAvailable()) {
                this.objectives = this.objectives.remove(nextTarget);
            }
        }
    }
    
    // checks if the player is on the scuba gear
    // EFFECT: If on the gear, picks up the target and removes it
    // EFFECT: Adds 25 oxygen to the player
    public void onScuba() {
        Iterator<Scuba> iter = this.scuba.iterator();
        while (iter.hasNext()) {
            Scuba nextScuba = iter.next();
            if (nextScuba.x == this.player.x && nextScuba.y == this.player.y) {
                this.scuba = this.scuba.remove(nextScuba);
                this.oxygenLeft = this.oxygenLeft + 10;
            }
        }
    }
    
    // checks if the player is on the oxygen tank
    // EFFECT: If on the tank, picks it up, removes it from the list, and adds
    //         10 oxygen to the oxygenLeft
    public void onOxygen() {
        Iterator<OxygenTank> iter = this.o2tanks.iterator();
        while (iter.hasNext()) {
            OxygenTank nextO2 = iter.next();
            if (nextO2.x == this.player.x && nextO2.y == this.player.y) {
                this.o2tanks = this.o2tanks.remove(nextO2);
                this.oxygenLeft = this.oxygenLeft + 5;
            }
        }
    }
    
    // floods the island and checks if the player can pick up the targets
    // if the player wins or loses, the game will "end" by ending the flooding
    // and just displaying the  win or lose screen
    public void onTick() {
        //this.useOxygen();
        if (this.count % 5 == 0 && !this.winCondition() && 
                !this.loseCondition() && !this.heliSubmerged()) {
            this.timer = (int)(this.timer - 1);
            this.useOxygen();
        }
        this.count = this.count + 1;
        this.onScuba();
        this.onTarget();
        this.onOxygen();
        this.lastKey = "none";
        this.player.run = false;
        if (this.count % 10 == 0 && !this.winCondition() && 
                !this.loseCondition() & !this.heliSubmerged()) {
            this.flooding();
        }
    }
    
    // returns the cell that the player is standing on
    public Cell playerCell() {
        Iterator<Cell> iter = this.board.iterator();
        while (iter.hasNext()) {
            Cell nextCell = iter.next();
            if (nextCell.x == this.player.x && nextCell.y == this.player.y) {
                return nextCell;
            }
        }
        // this is impossible to get to, but if you do get here it is easy to
        // check the game to see if you have an error (the game will auto-end)
        return new OceanCell(0, 0, 0);
    }
        
    /** --------------------------------------------------------------------**/
    /** ---------------------- End Game Mechanics --------------------------**/
    
    // checks if any of the end conditions have been met
    public boolean gameEnded() {
        return this.loseCondition() || this.winCondition() || 
                this.heliSubmerged();
    }
    
    // the condition to win the game
    public boolean winCondition() {
        return this.objectives.length() == 0;
    }
    
    // the ending "win screen"
    public WorldScene winScreen(WorldScene bg) {
        // overlays a white transparency showing that the player has won
        bg.placeImageXY(new RectangleImage(640, 640, OutlineMode.SOLID, 
                new Color(255, 255, 255, 100)), 320, 320);
        bg.placeImageXY(new TextImage("CONGRATULATIONS,", 
                60, Color.WHITE), 320, 290);
        bg.placeImageXY(new TextImage("YOU'VE ESCAPED!", 60, Color.WHITE), 
                320, 350);
        
        // reset game prompt
        bg.placeImageXY(new TextImage("Play Again?", 20, Color.WHITE), 
                320, 420);
        bg.placeImageXY(new TextImage("Press M for Mountain", 17, Color.WHITE),
                320, 450);
        bg.placeImageXY(new TextImage("Press R for Random Mountain", 17, 
                Color.WHITE), 320, 470);
        bg.placeImageXY(new TextImage("Press T Terrain", 17, Color.WHITE), 
                320, 490);
        
        return bg;
    }
    
    // the condition to lose the game by drowning
    public boolean loseCondition() {
        return this.playerCell().isFlooded && !this.canSwim;
    }
    
    // the condition to lose the game by having your helicopter be submerged
    public boolean heliSubmerged() {
        return this.highestPoint().isFlooded;
    }
    
    // the ending "lose screen" if you drown
    public WorldScene loseScreen(WorldScene bg) {
        //overlays a black transparency to show that the player has died
        bg.placeImageXY(new RectangleImage(640, 640, OutlineMode.SOLID, 
                new Color(0, 0, 0, 150)), 320, 320);
        bg.placeImageXY(new TextImage("YOU HAVE DROWNED ON", 
                50, Color.RED), 320, 190);
        bg.placeImageXY(new TextImage("THE FORBIDDEN ISLAND!", 50, Color.RED), 
                320, 450);
        bg.placeImageXY(new FromFileImage("skull-bones.png"), 320, 320);
        
        // reset game prompt
        bg.placeImageXY(new TextImage("Try Again?", 20, Color.WHITE), 
                320, 510);
        bg.placeImageXY(new TextImage("Press M for Mountain", 17, Color.WHITE),
                320, 540);
        bg.placeImageXY(new TextImage("Press R for Random Mountain", 17, 
                Color.WHITE), 320, 560);
        bg.placeImageXY(new TextImage("Press T Terrain", 17, Color.WHITE), 
                320, 580);
        return bg;
    }
    
    // the ending "lose screen" if your helicopter is submerged
    public WorldScene lostHeliScreen(WorldScene bg) {
        //overlays a black transparency to show that the player has died
        bg.placeImageXY(new RectangleImage(640, 640, OutlineMode.SOLID, 
                new Color(0, 0, 0, 150)), 320, 320);
        bg.placeImageXY(new TextImage("YOUR HELICOPTER HAS", 
                50, Color.RED), 320, 190);
        bg.placeImageXY(new TextImage("BEEN SUBMERGED!", 50, Color.RED), 
                320, 450);
        bg.placeImageXY(new FromFileImage("skull-bones.png"), 320, 320);
        
        // reset game prompt
        bg.placeImageXY(new TextImage("Try Again?", 20, Color.WHITE), 320, 510);
        bg.placeImageXY(new TextImage("Press M for Mountain", 17, Color.WHITE),
                320, 540);
        bg.placeImageXY(new TextImage("Press R for Random Mountain", 17, 
                Color.WHITE), 320, 560);
        bg.placeImageXY(new TextImage("Press T Terrain", 17, Color.WHITE), 
                320, 580);

        return bg;
    }   
}

/** ------------------------------------------------------------------------**/
/** ------------------------------------------------------------------------**/
/** ------------------------------------------------------------------------**/
/** ---------------- END OF GAME CODE / START OF EXAMPLES ------------------**/

//examples and methods
class ExamplesForbiddenIslandWorld2 {

    IList<String> EXlist1 = new ConsList<String>("This ", 
            new ConsList<String>("is ", new ConsList<String>("a ", 
                    new ConsList<String>("string.", new MtList<String>()))));
    ForbiddenIslandWorld world0;
    IList<Cell> list1;
    Cell cellTester;
    Cell leftCellTester;
    Cell rightCellTester;
    Cell topCellTester;
    Cell bottomCellTester;
    Cell c1;
    Cell c2;
    Cell c3;
    Cell c4;
    OceanCell oc = new OceanCell(0.0, 1, 1);
    ArrayList<ArrayList<Cell>> arr;
    ArrayList<ArrayList<Double>> arr1;
    ArrayList<ArrayList<Double>> arr2;
    Pilot pi;
    Helicopter heli1;
    Target gear;
    Scuba s;
    Scuba s2;
    IList<String> IListTester = new ConsList<String>("This ",
            new ConsList<String>("is ", new ConsList<String>("a ", 
                    new ConsList<String>("string.", new MtList<String>()))));
    Iterator<String> iter1 = IListTester.iterator();
    IList<String> mt = new MtList<String>();
    
    void initIslandTests() {
        world0 = new ForbiddenIslandWorld();
        
        cellTester = new Cell(2, 1, 1);
       
        leftCellTester = new Cell(1, 0, 1);
        rightCellTester = new Cell(1, 2, 1);
        topCellTester = new Cell(1, 1, 0);
        bottomCellTester = new Cell(1, 1, 2);
        
        cellTester.newLeft(leftCellTester);
        cellTester.newRight(rightCellTester);
        cellTester.newBottom(bottomCellTester);
        cellTester.newTop(topCellTester);
        
        leftCellTester.newRight(cellTester);
        leftCellTester.newLeft(leftCellTester);
        leftCellTester.newBottom(leftCellTester);
        leftCellTester.newTop(leftCellTester);
        
        rightCellTester.newLeft(cellTester);
        rightCellTester.newRight(rightCellTester);
        rightCellTester.newBottom(rightCellTester);
        rightCellTester.newTop(rightCellTester);
        
        topCellTester.newLeft(topCellTester);
        topCellTester.newRight(topCellTester);
        topCellTester.newTop(topCellTester);
        topCellTester.newBottom(cellTester);
        
        bottomCellTester.newLeft(bottomCellTester);
        bottomCellTester.newRight(bottomCellTester);
        bottomCellTester.newTop(cellTester);
        bottomCellTester.newBottom(bottomCellTester);
        
        arr = world0.createCells(world0.makeMountains(64));
        arr1 = world0.makeMountains(64);
        arr2 = world0.makeMountains(2);
        pi = new Pilot(0, 0);
        heli1 = new Helicopter(5, 5);
        gear = new Target(0, 0);
        s = new Scuba(0, 0);
        s2 = new Scuba(10, 10);
        
        
        
    }
    /**
    void testBoard(Tester t) {
        this.initIslandTests();
        System.out.println(this.arr.toString());
    }
    **/
    
  //RUN THE GAME BY REMOVING THE COMMENTS ON THE LINE BELOW    
    
    void testBigBang(Tester t) {
        this.initIslandTests();
        this.world0.bigBang(640, 640, .2); // Game speed is 5 ticks per second,
                                          // so it sinks every 2 seconds.
                                         // If you change this you will need
                                        // to adjust the timer as well
    }
    
    
    /** --------------------------- TESTS ------------------------------**/
    
    //test the method contains
    void testContains(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.EXlist1.contains("is "), true);
        t.checkExpect(this.EXlist1.contains("hello"), false);
    }
    
    //test the method length
    void testLength(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.EXlist1.length(), 4);
        t.checkExpect(new MtList<String>().length(), 0);
    }

   
  //test the On Key events 
    
    void testOnKey(Tester t) {
        this.initIslandTests();
        
        arr = world0.createCells(world0.makeMountains(64));
        world0.board = 
                world0.makeBoard(world0.createCells(world0.makeMountains(64)));
        world0.waterHeight = 20;
        world0.count = 9;
        world0.onKeyEvent("m");
        t.checkExpect(world0.waterHeight == 0, true);
        t.checkExpect(world0.count == 0, true);
        world0.waterHeight = 20;
        world0.count = 9;
        world0.onKeyEvent("r");
        t.checkExpect(world0.waterHeight == 0, true);
        t.checkExpect(world0.count == 0, true);
        world0.waterHeight = 20;
        world0.count = 9;
        world0.onKeyEvent("t");
        t.checkExpect(world0.waterHeight == 0, true);
        t.checkExpect(world0.count == 0, true);
    
        
        
    }
    
    //test On KEy movements 2
    void testOnKey2(Tester t) {
        this.initIslandTests();
        world0.player = new Pilot(2, 2);
        world0.onKeyEvent("right");
        t.checkExpect(world0.player.x == 3, true);
        world0.player = new Pilot(2, 2);
        world0.onKeyEvent("left");
        t.checkExpect(world0.player.x == 1, true);
        world0.player = new Pilot(2, 2);
        world0.onKeyEvent("up");
        t.checkExpect(world0.player.y == 1, true);
        world0.player = new Pilot(2, 2);
        world0.onKeyEvent("down");
        t.checkExpect(world0.player.y == 3, true);
    }
    
    //test the iter methods
    void testIterMethods(Tester t) {
        this.initIslandTests();
        t.checkExpect(iter1.hasNext(), true);
        t.checkExpect(iter1.next().equals("This "), true);
    }
    
    //test the land method
    void testLand(Tester t) {
        this.initIslandTests();
        arr = world0.createCells(world0.makeMountains(64));
        world0.board = world0.makeBoard(arr);
        list1 = world0.landCells();
        t.checkExpect(list1.contains(arr.get(31).get(31)), true);
        t.checkExpect(list1.contains(arr.get(0).get(0)), false);
    }
    
    //test the method createCells
    void testCreateCells(Tester t) {
        this.initIslandTests();
        arr = world0.createCells(arr2);
        t.checkExpect(arr.get(0).get(0).right.equals(arr.get(0).get(1)), true);
        t.checkExpect(arr.get(0).get(1).bottom.equals(arr.get(1).get(1)), true);
    }
    
    //test the method makeBoard
    void testMakeBoard(Tester t) {
        this.initIslandTests();
        list1 = world0.makeBoard(world0.createCells(world0.makeMountains(64)));
        t.checkExpect(world0.board.equals(world0.board), true);
    }
    
    //test makeTerrain
    void testMakeTerrain(Tester t) {
        this.initIslandTests();
        arr1 = world0.makeTerrain(64);
        t.checkExpect(arr1.get(0).get(0) == 0, true);
        t.checkExpect(arr1.get(21).get(21) >= 1, true);
    }
    
    //test makeTerrainHelper
    void testMakeTerrainHelp(Tester t) {
        this.initIslandTests();
        arr1 = world0.makeTerrain(64);
        t.checkExpect(arr1.get(0).get(0) == 0, true);
        t.checkExpect(arr1.get(63).get(63) == 0,  true);
        world0.makeTerrainHelp(arr1, 0, 0, 63, 63);
        t.checkExpect(arr1.get(0).get(0) == 0, true);
        t.checkExpect(arr1.get(63).get(63) == 0,  true);
    }
    
    //test the method onTick
    void testOnTick(Tester t) {
        this.initIslandTests();
        t.checkExpect(world0.count == 0, true);
        world0.onTick();
        t.checkExpect(world0.count == 1, true);
        t.checkExpect(world0.waterHeight == 0, true);
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        world0.onTick();
        t.checkExpect(world0.waterHeight == 1, true);
    }
    
    //test timeLEft
    void testTime(Tester t) {
        this.initIslandTests();
        world0.timer = 40;
        world0.onTick();
        t.checkExpect(world0.timer == 39, true);
        
    }
    
    //test the makeRandom method
    void testMakeRandom(Tester t) {
        this.initIslandTests();
        arr1 = world0.makeRandom(64);
        t.checkExpect(this.arr1.get(0).get(0) == 0, true);
    }
    
    //test the randomCell method
    void testRandomCell(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.world0.landCells().contains(world0.randomCell()), 
                true);
    }
    
    //test the drawObjectives method
    void testObjective(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.world0.objectives.equals(world0.objectives), true);
        t.checkExpect(this.world0.o2tanks.equals(world0.o2tanks), true);
    }
    
    //test the method isCons
    void testIsCons(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.IListTester.isCons(), true);
        t.checkExpect(this.mt.isCons(), false);
    }
    
    //test the method add
    void testAdd(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.mt.add("helloWorld"), 
                new ConsList<String>("helloWorld", this.mt));
        t.checkExpect(this.IListTester.add("...blank..."), 
                new ConsList<String>("...blank...", this.IListTester));
    }
    
    //test remove
    void testRemove(Tester t) {
        this.initIslandTests();
        IList<String> mt2 = new ConsList<String>("hello", 
                this.mt).remove("hello");
        t.checkExpect(mt2.sameList(this.mt), true);
    }
    
    //test the method sameList
    void testSameList(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.IListTester.sameList(this.mt), false);
        IList<String> mt2 = new MtList<String>();
        t.checkExpect(this.mt.sameList(mt2), true);
        IList<String> listCopy = IListTester;
        t.checkExpect(this.IListTester.sameList(listCopy), true);
    }
    
    //test the method sameCons
    void testSameCons(Tester t) {
        this.initIslandTests();
        ConsList<Integer> ints1 = new ConsList<Integer>(1, 
                new ConsList<Integer>(2, new MtList<Integer>()));
        ConsList<Integer> ints2 = new ConsList<Integer>(1, 
                new ConsList<Integer>(3, new MtList<Integer>()));
        ConsList<Integer> ints3 = new ConsList<Integer>(1, 
                new ConsList<Integer>(2, new MtList<Integer>()));
        t.checkExpect(ints1.sameConsList(ints3), true);
        t.checkExpect(ints2.sameConsList(ints3), false);
        
    }
    
    //test the method sameMt
    void testSameEmpty(Tester t) {
        this.initIslandTests();
        MtList<Integer> mt1 = new MtList<Integer>();
        MtList<Integer> mt2 = new MtList<Integer>();
        t.checkExpect(mt1.sameMtList(mt2), true);
    }
    
    //test for the iterator method on lists
    void testIterator(Tester t) {
        this.initIslandTests();
        t.checkExpect(iter1.hasNext(), true);
        t.checkExpect(iter1.next().equals("is "), true);
    }
    
    //test the method flood
    void testFlood(Tester t) {
        this.initIslandTests();
        t.checkExpect(cellTester.isFlooded, false);
        cellTester.flood(3);
        t.checkExpect(cellTester.isFlooded, true);
    }
    
    //test the method reset
    void testReset(Tester t) {
        this.initIslandTests();
        world0.steps = 1;
        world0.reset();
        t.checkExpect(world0.steps == 0, true);
        
    }
    
    //test the method connectCells
    void testConnectCells(Tester t) {
        this.initIslandTests();
        t.checkExpect(heli1.drawTarget(), new FromFileImage("helicopter.png"));
    }
    
    //test the drawTarget method
    //on some of the images in the game
    void testDrawTarget(Tester t) {
        this.initIslandTests();
        t.checkExpect(gear.drawTarget(), new FromFileImage("gear-icon.png"));
    }
    
    //test steps
    void testSteps(Tester t) {
        this.initIslandTests();
        world0.steps = 0;
        world0.onKeyEvent("right");
        t.checkExpect(world0.steps == 1, true);
    }
    
    //test player attributes
    void testPlayer(Tester t) {
        this.initIslandTests();
        world0.player.run = false;
        world0.player.dir = "down";
        world0.onKeyEvent("right");
        t.checkExpect(world0.player.run, true);
        t.checkExpect(world0.player.dir.equals("right"), true);
        
    }

    
    //test onTarget
    void testOnTarget(Tester t) {
        this.initIslandTests();
        world0.player = pi;
        world0.objectives = new ConsList<Target>(gear, new MtList<Target>());
        t.checkExpect(pi.x == gear.x && pi.y == gear.y, true);
        
    }
    //test onScuba
    void testOnScuba(Tester t) {
        this.initIslandTests();
        world0.player = pi;
        t.checkExpect(pi.x == s.x && pi.y == s.y, true);
        t.checkExpect(pi.x == s2.x && pi.y == s2.y, false);
    }
    
    //test scuba key
    void testS(Tester t) {
        this.initIslandTests();
        world0.canSwim = false;
        world0.oxygenLeft = 1;
        world0.useOxygen();
        world0.onKeyEvent("s");
        t.checkExpect(world0.canSwim = true, true);
    }
    
    //test cellImage
    void testCellImage(Tester t) {
        this.initIslandTests();
        t.checkExpect(cellTester.cellImage(2), new RectangleImage(10, 
                10, OutlineMode.SOLID, new Color(150, 66, 0)));
        t.checkExpect(cellTester.cellImage(0), new RectangleImage(10, 10, 
                OutlineMode.SOLID, new Color(14, 215, 14)));
        
    }
    
    //test the methods on new cells (updating them) 
    void testNewCells(Tester t) {
        this.initIslandTests();
        cellTester.newLeft(topCellTester);
        t.checkExpect(cellTester.left == topCellTester,  true);
        cellTester.newTop(rightCellTester);
        t.checkExpect(cellTester.top == rightCellTester, true);
        cellTester.newBottom(leftCellTester);
        t.checkExpect(cellTester.bottom == leftCellTester, true);
        cellTester.newRight(topCellTester);
        t.checkExpect(cellTester.right == topCellTester, true);
    }
    
    //test stillUp
    void testStillUp(Tester t) {
        this.initIslandTests();
        t.checkExpect(this.cellTester.stillUp(), false);
        
    }
    
}

