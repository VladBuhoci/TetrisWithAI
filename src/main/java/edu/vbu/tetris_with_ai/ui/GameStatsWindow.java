package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.ai.Agent;
import edu.vbu.tetris_with_ai.ai.GeneticAgentsMaster;
import edu.vbu.tetris_with_ai.ai.GeneticAlgoAgent;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Displays game and AI stats.
 */
public class GameStatsWindow extends JFrame {

    private final GeneticAgentsMaster geneticAgentsMaster;
    private final Map<TetrisGame, Agent> gamesAndAgents;
    private final ScheduledExecutorService executorService;

    private JLabel overallScoreValue;
    private JLabel currentScoreValue;
    private JLabel heightWeightValue;
    private JLabel holesWeightValue;
    private JLabel bumpWeightValue;
    private JLabel linesWeightValue;

    public GameStatsWindow(String windowTitle, GeneticAgentsMaster geneticAgentsMaster) {
        super(windowTitle);

        this.geneticAgentsMaster = geneticAgentsMaster;
        this.gamesAndAgents = geneticAgentsMaster.getGamesAndAgents();
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        setSize(400, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setIcon();
        initComponents();

        this.executorService.scheduleAtFixedRate(this::queryMasterThreadForData, 1_000L, 100L, TimeUnit.MILLISECONDS);
    }

    private void initComponents() {
        // Previous highest score

        JLabel overallScoreLabel = new JLabel("Overall best score: ");
        overallScoreValue = new JLabel("0");

        JPanel overallScoreRow = new JPanel();
        overallScoreRow.add(overallScoreLabel);
        overallScoreRow.add(overallScoreValue);

        // Current highest score

        JLabel currentScoreLabel = new JLabel("Current best score: ");
        currentScoreValue = new JLabel("0");

        JPanel currentScoreRow = new JPanel();
        currentScoreRow.add(currentScoreLabel);
        currentScoreRow.add(currentScoreValue);

        // Current best AI agent weights

        JLabel heightWeightLabel = new JLabel("Height weight: ");
        heightWeightValue = new JLabel("0.0");

        JLabel holesWeightLabel = new JLabel("Holes weight: ");
        holesWeightValue = new JLabel("0.0");

        JLabel bumpWeightLabel = new JLabel("Bumpiness weight: ");
        bumpWeightValue = new JLabel("0.0");

        JLabel linesWeightLabel = new JLabel("Cleared lines weight: ");
        linesWeightValue = new JLabel("0.0");

        JPanel currentWeightsGrid = new JPanel();
        currentWeightsGrid.setLayout(new GridLayout(4, 2));

        currentWeightsGrid.add(heightWeightLabel);
        currentWeightsGrid.add(heightWeightValue);

        currentWeightsGrid.add(holesWeightLabel);
        currentWeightsGrid.add(holesWeightValue);

        currentWeightsGrid.add(bumpWeightLabel);
        currentWeightsGrid.add(bumpWeightValue);

        currentWeightsGrid.add(linesWeightLabel);
        currentWeightsGrid.add(linesWeightValue);

        add(overallScoreRow);
        add(currentScoreRow);
        add(currentWeightsGrid);
    }

    public void display() {
        // Position to center of screen.
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void setIcon() {
        URL tetrisIconResource = ClassLoader.getSystemResource(Constants.WINDOW_ICON_PATH);
        Image tetrisIcon = Toolkit.getDefaultToolkit().createImage(tetrisIconResource);

        setIconImage(tetrisIcon);
    }

    private void queryMasterThreadForData() {
        overallScoreValue.setText(String.format("%.2f", geneticAgentsMaster.getBestScore()));

        TetrisGame currentTopGame = gamesAndAgents.keySet().stream().max(Comparator.comparingDouble(TetrisGame::getScore)).get();
        currentScoreValue.setText(String.format("%.2f", currentTopGame.getScore()));

        GeneticAlgoAgent currentTopAgent = (GeneticAlgoAgent) gamesAndAgents.get(currentTopGame);
        heightWeightValue.setText(String.valueOf(currentTopAgent.getWeightForHeight()));
        holesWeightValue.setText(String.valueOf(currentTopAgent.getWeightForHoles()));
        bumpWeightValue.setText(String.valueOf(currentTopAgent.getWeightForBumpiness()));
        linesWeightValue.setText(String.valueOf(currentTopAgent.getWeightForLineClear()));
    }
}
