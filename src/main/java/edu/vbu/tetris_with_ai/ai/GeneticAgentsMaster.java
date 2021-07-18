package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class GeneticAgentsMaster extends AgentsMaster {

    private static final Logger LOG = LogManager.getLogger(GeneticAlgoAgent.class);

    private int currentGeneration;
    private double bestScore;          // across generations.

    private TetrisGame previousTopGame;

    public GeneticAgentsMaster() {
        this.currentGeneration = 1;
        this.bestScore = 0;

        setAllGamesOverCallback(gamesAndAgents -> {
            handleNewGeneration(gamesAndAgents);
            waitForMillis(500L);
            start();

            LOG.info("Started generation #" + currentGeneration);
        });
    }

    private void handleNewGeneration(Map<TetrisGame, Agent> gamesAndAgents) {
        // Have the agents sorted in a list according to their corresponding game score (from highest to lowest).

        Set<TetrisGame> games = gamesAndAgents.keySet();
        List<TetrisGame> sortedGames = games.stream().sorted((game1, game2) -> Double.compare(game2.getScore(), game1.getScore())).collect(Collectors.toList());

        List<GeneticAlgoAgent> sortedAgents = new ArrayList<>(games.size());

        sortedGames.forEach(game -> sortedAgents.add((GeneticAlgoAgent) gamesAndAgents.get(game)));

        Iterator<TetrisGame> gameIterator = sortedGames.iterator();
        TetrisGame topGame = gameIterator.next();

        markTopGame(topGame);

        topGame = previousTopGame;  // they're identical now.

        // Update generation data.

        currentGeneration++;

        if (topGame.getScore() > bestScore) {
            bestScore = topGame.getScore();
        }

        // Remove the second half of the agent population (get rid of bad agents).

        int agentCount = sortedAgents.size();
        int halfAgentCount = agentCount / 2;
        int agentIndex = 0;
        Iterator<GeneticAlgoAgent> agentIterator = sortedAgents.iterator();

        while (agentIterator.hasNext()) {
            agentIterator.next();

            if (agentIndex >= halfAgentCount) {
                agentIterator.remove();
            }

            agentIndex++;
        }

        // Keep the first agent (top one) for the next generation.
        GeneticAlgoAgent topAgent = (GeneticAlgoAgent) gamesAndAgents.get(topGame);
        addAgent(topGame, topAgent);
        resetGame(topGame, "Tetris for random AI #" + topAgent.getId());

        LOG.info("Games ordered by their score: [{}]", () -> sortedGames);
        LOG.info("Agents ordered by game score: [{}]", () -> sortedAgents);
        LOG.info("Top game of generation #" + currentGeneration + " is '" + topAgent.getName() + "'");

        // Breed the rest of the agent population (randomly) based on the current remaining generation half.
        for (int k = 1; k < agentCount; k++) {
            Pair<GeneticAlgoAgent, GeneticAlgoAgent> randomAgentPairFromPopulation = getRandomAgentPairFromPopulation(sortedAgents);

            GeneticAlgoAgent agent1 = randomAgentPairFromPopulation.getLeftValue();
            GeneticAlgoAgent agent2 = randomAgentPairFromPopulation.getRightValue();

            TetrisGame nextGame = gameIterator.next();
            GeneticAlgoAgent childAgent = agent1.crossOver(agent2, getGameByAgent(gamesAndAgents, agent1), getGameByAgent(gamesAndAgents, agent2));

            resetGame(nextGame, "Tetris for random AI #" + childAgent.getId());
            addAgent(nextGame, childAgent);
        }
    }

    private TetrisGame getGameByAgent(Map<TetrisGame, Agent> games, GeneticAlgoAgent agentFilter) {
        return games.keySet().stream().filter(tetrisGame -> games.get(tetrisGame) == agentFilter).findFirst().orElse(null);
    }

    private Pair<GeneticAlgoAgent, GeneticAlgoAgent> getRandomAgentPairFromPopulation(Collection<GeneticAlgoAgent> agents) {
        List<GeneticAlgoAgent> copy = new ArrayList<>(agents);
        int elementsToRandomlyPickCount = 2;

        Collections.shuffle(copy);

        List<GeneticAlgoAgent> pair = elementsToRandomlyPickCount > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, elementsToRandomlyPickCount);

        return new Pair<>(pair.get(0), pair.get(1));
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public double getBestScore() {
        return bestScore;
    }

    public void markTopGame(TetrisGame newTopGame) {
        if (previousTopGame == null) {
            newTopGame.markAsTopGame(true);

            previousTopGame = newTopGame;
        } else {
            if (previousTopGame != newTopGame && previousTopGame.getScore() < newTopGame.getScore()) {
                newTopGame.markAsTopGame(true);

                previousTopGame.markAsTopGame(false);
                previousTopGame = newTopGame;
            }
        }
    }
}
