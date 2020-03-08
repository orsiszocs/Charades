package controller;

import java.util.ArrayList;

public class Team {
    public int score;
    public int currentMember;
    public String name;
    ArrayList<String> members;
    public Team(String name, ArrayList<String> members) {
        score=0;
        this.name=name;
        this.members=members;
        currentMember=-1;
    }
    public void nextMember() {
        currentMember++;
        if(currentMember>=members.size()) {
            currentMember-=members.size();
        }
    }
}
