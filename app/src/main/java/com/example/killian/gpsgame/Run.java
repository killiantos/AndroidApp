package com.example.killian.gpsgame;

import java.util.ArrayList;

/**
 * Created by carlm on 21/11/2017.
 */

public class Run {
    public class Waypoint {
        int x;
        int y;

        public Waypoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public String date;
    public int distance;
    public int steps;
    public ArrayList<Waypoint> waypoints;

    public Run() {
        waypoints = new ArrayList<Waypoint>();
    }

    public void addWaypoint(int x, int y) {
        waypoints.add(new Waypoint(x, y));
    }
}
