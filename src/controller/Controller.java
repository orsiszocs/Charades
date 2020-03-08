package controller;

import gui.CharadesException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class Controller {

    boolean guessed;
    public ArrayList<Team> teams=new ArrayList<>();
    public int numberTeams;
    public int currentTeam;
    Random random;
    public String currentWord,currentDifficulty;
    private ArrayList<String> easyWords,mediumWords,hardWords;
    private HashMap<String, Integer> difficultyToPoint;
    private HashMap<String,ArrayList<String>> difficultyToList;

    /**
     * Sets the number of teams to the number given as a parameter.
     * @param number total number of teams
     * @throws CharadesException if the parameter is not a number*/
    public void setNumberTeams(String number) throws CharadesException {
        try {
            int n = Integer.parseInt(number);
            if (n<=0) {
                throw new CharadesException("A positive value is expected.");
            }
            numberTeams=n;
            currentTeam=1;
        }
        catch (NumberFormatException e) {
            throw new CharadesException("An integer value is expected.");
        }
    }

    /**
     * Adds the given team to the list of existing teams.
     * @param teamName name of the team (must be unique)
     * @param teamMembers names of team members, written on separate lines
     * @throws CharadesException if there are not enough team members, there are two team members with the same name, or there already is a team with that team name*/
    public void addTeam(String teamName,String teamMembers) throws CharadesException {
        if(teamName.equals("")) {
            throw new CharadesException("Team name was not added.");
        }
        ArrayList<String> members=new ArrayList<>();
        String[] lines = teamMembers.split("\\r?\\n");
        for(String line:lines) {
            members.add(line);
        }
        int i=0;
        while(i<members.size()) {
            if(members.get(i).equals("")) {
                members.remove(i);
            }
            else {
                i++;
            }
        }
        if(members.size()<2) {
            throw new CharadesException("At least two members are required in a team.");
        }
        for(i=0;i<members.size()-1;i++) {
            for(int j=i+1;j<members.size();j++) {
                if(members.get(i).equals(members.get(j))) {
                    throw new CharadesException("Two members in the same team cannot have the same name.");
                }
            }
        }
        for(Team t: teams) {
            if(t.name.equals(teamName)) {
                throw new CharadesException("Two teams cannot have the same name.");
            }
        }
        teams.add(new Team(teamName,members));
        currentTeam++;
        if(currentTeam>numberTeams) {
            currentTeam=-1;
        }
    }

    /**
     * Loads each line from a file and puts it into the list.
     * @param fileName name of the file
     * @param list empty list
     */
    private static void loadFile(String fileName,ArrayList<String> list) {
        try(FileReader fileReader=new FileReader(fileName)) {
            BufferedReader reader=new BufferedReader(fileReader);
            String line;
            while((line=reader.readLine())!=null) {
                list.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * easyWords, mediumWords, hardWords lists containing the words are initialized.
     * difficultyToPoint and difficultyToList maps are initialized.
     */
    public Controller() {
        easyWords=new ArrayList<>();
        mediumWords=new ArrayList<>();
        hardWords=new ArrayList<>();
        random=new Random();
        loadFile("easywords.txt",easyWords);
        loadFile("mediumwords.txt",mediumWords);
        loadFile("hardwords.txt",hardWords);
        difficultyToPoint=new HashMap<>();
        difficultyToPoint.put("easy",3);
        difficultyToPoint.put("medium",4);
        difficultyToPoint.put("hard",5);
        difficultyToList=new HashMap<>();
        difficultyToList.put("easy",easyWords);
        difficultyToList.put("medium",mediumWords);
        difficultyToList.put("hard",hardWords);
    }

    /**
     * Makes the list of teams empty.
     */
    public void startNewGame() {
        currentTeam=0;
        teams=new ArrayList<>();
    }

    /**
     * @return randomly one of: draw, explain, act
     */
    public String getNextAction() {
        String[] actions=new String[] {"draw","explain","act"};
        return actions[random.nextInt(3)];
    }

    /**
     * Sets the word to be guessed in the new round.
     * @param difficulty easy, medium or hard
     */
    public void startRound(String difficulty) {
        currentDifficulty=difficulty;
        ArrayList<String> list=difficultyToList.get(currentDifficulty);
        currentWord=list.get(random.nextInt(list.size()));
    }

    /**
     * @return team names and scores written on separate lines
     */
    public String getScoreBoard() {
        String scores="";
        ArrayList<Team> sortedTeams=new ArrayList<>();
        for(Team t:teams) {
            sortedTeams.add(t);
        }
        sortedTeams.sort(Comparator.comparingInt(team -> -team.score));
        for(Team t:sortedTeams) {
            scores+=t.name+": "+t.score+"\n";
        }
        return scores;
    }

    /**
     * @return name of the team that plays in the next round
     */
    public String getNextTeam() {
        currentTeam++;
        if(currentTeam>=numberTeams) {
            currentTeam-=numberTeams;
        }
        return teams.get(currentTeam).name;
    }

    /**
     * @return name of the player that plays in the next round
     */
    public String getNextPlayer() {
        teams.get(currentTeam).nextMember();
        return teams.get(currentTeam).members.get(teams.get(currentTeam).currentMember);
    }

    /**
     * @return true if all the teams were added and the game can start
     */
    public boolean gameCanStart() {
        return (teams.size() ==numberTeams);
    }

    /**
     * Changes the score of the team according to the number of points that word is worth.
     */
    public void goodGuess() {
        guessed=true;
        teams.get(currentTeam).score+=difficultyToPoint.get(currentDifficulty);
    }

    /**
     * Does not change the score of the team according to the number of points that word is worth.
     */
    public void badGuess() {
        guessed=false;
    }

    /**
     * Modifies the score by either adding or subtracting the number of points that word is worth.
     */
    public void changeGuess() {
        int previousTeam=currentTeam-1;
        if(previousTeam<0) {
            previousTeam=teams.size()-1;
        }
        if(guessed) {
            guessed=false;
            teams.get(previousTeam).score-=difficultyToPoint.get(currentDifficulty);
        }
        else {
            guessed=true;
            teams.get(previousTeam).score+=difficultyToPoint.get(currentDifficulty);
        }
    }

    /**
     * @return number corresponding to the team to be given as an input
     */
    public int getTeamOrder() {
        return currentTeam;
    }
}
