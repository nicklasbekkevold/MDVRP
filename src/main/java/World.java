package main.java;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.ga.Population;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class World extends JPanel {

    private String problem = "p22";

    private Population population = new Population();
    private MDVRP mdvrp = MDVRPSerializer.readFromFile(problem);

    static final int WIDTH = 1000;
    static final int HEIGHT = 600;

    private World() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        final Timer timer = new Timer(5, (ActionEvent e) -> {
            population.update();
            repaint();
        });
        timer.start();
    }

    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g = (Graphics2D) graphics;
        g.translate(320, 300);
        g.scale(1.7, 1.7);
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString("Generation: " + population.getGeneration(), 220, -150);
        g.drawString("Max fitness: " + String.format("%.2f", population.getMaxFitness()), 220, -125);
        g.drawString("Diversity: " + String.format("%.2f", population.getDiversity()), 220, -100);
        drawCustomers(g);
        drawDepots(g);
    }

    private void drawCustomers(final Graphics2D g) {
        final List<Customer> customers = mdvrp.getCustomers();
        g.setColor(Color.BLACK);
        for (Customer customer : customers) {
            g.fillOval(customer.getX(), customer.getY(), 5 ,5);
        }
    }

    private void drawDepots(final Graphics2D g) {
        final List<Depot> depots = mdvrp.getDepots();
        g.setColor(Color.RED);
        for (Depot depot : depots) {
            g.fillOval(depot.getX(), depot.getY(), 5 ,5);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("MDVRP");
            frame.setResizable(false);
            frame.add(new World(), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
