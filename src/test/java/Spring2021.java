import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import com.codingame.gameengine.runner.CommandLinePlayerAgent;
import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;

public class Spring2021 {

    static String[] DEFAULT_AI = new String[] {
        "python3", "config/Boss.py"
    };
    static String[] BOSS_WOOD2 = new String[] {
        "python3", "config/level1/Boss.py"
    };
    static String[] BOSS_WOOD1 = new String[] {
        "python3", "config/level2/Boss.py"
    };
    
    private static int indexAgent1 = 588;
    private static int genAgent1 = 145;
    
    private static int indexAgent2 = 588;
    private static int genAgent2 = 145;

    private static boolean testMode = true;
    
    // n = population / 2
    private static int n = 1000;
    private static int top = (n * 2) / 10;

    private static Random random = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        
        if (testMode) {
            launchGameTest();
            return;
        }

        for (int gen=0; gen < 200; gen++)
        {
            LinkedList<int[]> bestScores = new LinkedList<int[]>();

            System.out.printf("start gen %d%n", gen);

            String[] genAgent = new String[] {
                "dotnet", "c:\\Users\\benoi\\source\\repos\\SpringChallenge2021\\bin\\Release\\net5.0\\SpringChallenge2021.dll", "evolution", Integer.toString(gen), Integer.toString(n * 2)
            };

            CommandLinePlayerAgent evol = new CommandLinePlayerAgent(genAgent);
            evol.initialize(new Properties());
            evol.execute();                        
            evol.getOutput(1, 30000);

            for (int i = 0; i < n; i++) {

                int indexAgent1 = i;
                int indexAgent2 = i + n;

                String[] agent1 = new String[] {
                    "dotnet", "c:\\Users\\benoi\\source\\repos\\SpringChallenge2021\\bin\\Release\\net5.0\\SpringChallenge2021.dll", "load", Integer.toString(indexAgent1), Integer.toString(gen)
                };
            
                String[] agent2 = new String[] {
                    "dotnet", "c:\\Users\\benoi\\source\\repos\\SpringChallenge2021\\bin\\Release\\net5.0\\SpringChallenge2021.dll", "load", Integer.toString(indexAgent2), Integer.toString(gen)
                };

                long seed = random.nextLong();

                int[] resultsA = launchGame(agent1, agent2, seed);
                int[] resultsB = launchGame(agent2, agent1, seed);

                int scoreAgent1 = resultsA[0] + resultsB[1];
                int scoreAgent2 = resultsA[1] + resultsB[0];

                if (scoreAgent1 > scoreAgent2) {
                    addBestScore(bestScores, new int[] { scoreAgent1, indexAgent1 });
                }
                else if (scoreAgent2 > scoreAgent1) {
                    addBestScore(bestScores, new int[] { scoreAgent2, indexAgent2 });
                }
                else {
                    addBestScore(bestScores, new int[] { scoreAgent1, indexAgent1 });
                }
            }   
            
            System.out.printf("best scores for gen %d%n", gen);
            for (int i = 0; i < bestScores.size(); i++) 
            {
                System.out.printf("%d = [%d, %d] %n", i, bestScores.get(i)[0], bestScores.get(i)[1]);
            }

            System.out.println("start natural selection");

            for (int i = 0; i < n * 2; i++)
            {
                boolean isBest = false;

                for (int[] score : bestScores) {
                    if (score[1] == i)
                    {
                        isBest = true;
                        break;
                    }
                }

                if (!isBest) {
                    String fileName = String.format("c:\\neuralNets\\gen%d\\neuralNet%04d.bin", gen, i);
                    java.io.File file = new java.io.File(fileName);
                    file.delete();                    
                }
            }
        }        
    }

    private static void addBestScore(LinkedList<int[]>llist, int[] val) {

        if (llist.size() == 0) {
            llist.add(val);
        } else if (llist.get(0)[0] < val[0]) {
            llist.add(0, val);
        } else if (llist.get(llist.size() - 1)[0] > val[0]) {
            llist.add(llist.size(), val);
        } else {
            int i = 0;
            while (llist.get(i)[0] > val[0]) {
                i++;
            }
            
            llist.add(i, val);
        }
        
        if (llist.size() > top)
            llist.remove(llist.size() - 1);
    }

    public static void launchGameTest()
    {
        String[] agent1 = new String[] {
            "dotnet", "c:\\Users\\benoi\\source\\repos\\SpringChallenge2021\\bin\\Release\\net5.0\\SpringChallenge2021.dll", "load", Integer.toString(indexAgent1), Integer.toString(genAgent1)
        };
    
        String[] agent2 = new String[] {
            "dotnet", "c:\\Users\\benoi\\source\\repos\\SpringChallenge2021\\bin\\Release\\net5.0\\SpringChallenge2021.dll", "load", Integer.toString(indexAgent2), Integer.toString(genAgent2)
        };

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(3);
        Properties gameParameters = new Properties();
        gameRunner.setGameParameters(gameParameters);

        gameRunner.addAgent(
            agent1,
            "gen" + Integer.toString(genAgent1) + "/" + Integer.toString(indexAgent1),
            "https://static.codingame.com/servlet/fileservlet?id=61910307869345"
        );
        
        gameRunner.addAgent(
            agent2,
            "gen" + Integer.toString(genAgent2) + "/" + Integer.toString(indexAgent2),
            "https://static.codingame.com/servlet/fileservlet?id=61910289640958"
        );

        gameRunner.setSeed(random.nextLong());

        gameRunner.start(8888);
    }

    public static int[] launchGame(String[] agent1, String[] agent2, long seed) throws IOException, InterruptedException {
        GameResult gameResult = null;
        boolean isOk = false;
        
        while (!isOk) {            
            MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
            gameRunner.setLeagueLevel(3);
            Properties gameParameters = new Properties();
            gameRunner.setGameParameters(gameParameters);

            gameRunner.addAgent(
                agent1,
                "gen" + agent1[4] + "/" + agent1[3],
                "https://static.codingame.com/servlet/fileservlet?id=61910307869345"
            );

            gameRunner.addAgent(
                agent2,
                "gen" + agent2[4] + "/" + agent2[3],
                "https://static.codingame.com/servlet/fileservlet?id=61910289640958"
            );

            gameRunner.setSeed(seed);

            gameResult = gameRunner.simulate();

            if (gameResult.scores.size() == 2) {
                isOk = true;
            }
            else {
                System.out.println("Aïe Aïe ouille");
                return new int[] { 0, 0 };
            }
        }      

        int score1 = gameResult.scores.get(0).intValue();
        int score2 = gameResult.scores.get(1).intValue();

        //String name1 = gameResult.agents.get(0).name;
        //String name2 = gameResult.agents.get(1).name;

        //System.out.printf("score de %s = %d, score de %s = %d%n", name1, score1, name2, score2);

        //System.out.printf("%s gagne%n", name1);
        return new int[] { score1, score2 };
        
        //gameRunner.start(8888);
    }
}
