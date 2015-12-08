package Simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import locals.Local;

public class SimulationFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private SimulationAgent myAgent;
	Panel panel = new Panel();
	List<String> visto = new ArrayList<String>();

	public SimulationFrame(SimulationAgent simulationAgent) {
		super();
		setSize(650, 600);
		setTitle("Simulation");
		myAgent = simulationAgent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().add(panel);
		dispose();
		setVisible(true);
	}

	public void paint(Graphics g) {
		panel.repaint();
	}

	class Panel extends JPanel {
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
			// super.paint(g); // fixes the immediate problem.
			Graphics2D g2 = (Graphics2D) g;

			visto = new ArrayList<String>();

			/*
			 * g2.setColor(Color.blue); g2.setStroke(new BasicStroke(10));
			 * g2.draw(line);
			 */

			g.drawRect(0, 0, 750, 750);
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, 750, 750);
			desenhaGrafo(g2, myAgent.map.get("A"));
			desenhaLocais(g2);

		}
	}

	void desenhaGrafo(Graphics2D g, Local init) {
		Set<DefaultWeightedEdge> sucessorEdges = myAgent.cityMap.edgesOf(init);

		Iterator<DefaultWeightedEdge> iter = sucessorEdges.iterator();

		// The Edge that comes from the parent to the current node must not be
		// present on the sucessors List

		while (iter.hasNext()) {

			DefaultWeightedEdge edge = iter.next();
			Local tempTarg = myAgent.cityMap.getEdgeTarget(edge);
			Local tempSource = myAgent.cityMap.getEdgeSource(edge);

			g.drawRect(tempTarg.getJ() * 50 + 75, tempTarg.getI() * 50 + 75, 10, 10);
			g.setColor(Color.BLACK);
			g.fillRect(tempTarg.getJ() * 50 + 75, tempTarg.getI() * 50 + 75, 10, 10);
			g.drawString(tempTarg.getName(), tempTarg.getJ() * 50 + 70, tempTarg.getI() * 50 + 70);
			g.drawLine(tempTarg.getJ() * 50 + 80, tempTarg.getI() * 50 + 80, tempSource.getJ() * 50 + 80,
					tempSource.getI() * 50 + 80);

			g.fillRect(tempSource.getJ() * 50 + 75, tempSource.getI() * 50 + 75, 10, 10);
			g.drawString(tempSource.getName(), tempSource.getJ() * 50 + 70, tempSource.getI() * 50 + 70);

			if (init.getName() == tempSource.getName()) {
				visto.add(tempSource.getName());
				if (!visto.contains(tempTarg.getName())) {
					desenhaGrafo(g, tempTarg);
				}
			} else if (init.getName() == tempTarg.getName()) {
				visto.add(tempTarg.getName());
				if (!visto.contains(tempSource.getName())) {
					desenhaGrafo(g, tempSource);
				}
			}

		}
	}

	void desenhaLocais(Graphics2D g) {
		for (int i = 0; i < myAgent.chargers.size(); i++) {
			g.drawRect(myAgent.chargers.get(i).getJ() * 50 + 75, myAgent.chargers.get(i).getI() * 50 + 75, 10, 10);
			g.setColor(Color.YELLOW);
			g.drawString("Charger", myAgent.chargers.get(i).getJ() * 50 + 85,
					myAgent.chargers.get(i).getI() * 50 + 100);
			g.fillRect(myAgent.chargers.get(i).getJ() * 50 + 75, myAgent.chargers.get(i).getI() * 50 + 75, 10, 10);
		}

		for (int i = 0; i < myAgent.dumps.size(); i++) {
			g.drawRect(myAgent.dumps.get(i).getJ() * 50 + 75, myAgent.dumps.get(i).getI() * 50 + 75, 10, 10);
			g.setColor(Color.BLUE);
			g.drawString("Dumps", myAgent.dumps.get(i).getJ() * 50 + 85,
					myAgent.dumps.get(i).getI() * 50 + 100);
			g.fillRect(myAgent.dumps.get(i).getJ() * 50 + 75, myAgent.dumps.get(i).getI() * 50 + 75, 10, 10);
		}

		for (int i = 0; i < myAgent.stores.size(); i++) {
			g.drawRect(myAgent.stores.get(i).getJ() * 50 + 75, myAgent.stores.get(i).getI() * 50 + 75, 10, 10);
			g.setColor(Color.RED);
			g.drawString("Stores", myAgent.stores.get(i).getJ() * 50 + 85,
					myAgent.stores.get(i).getI() * 50 + 100);
			g.fillRect(myAgent.stores.get(i).getJ() * 50 + 75, myAgent.stores.get(i).getI() * 50 + 75, 10, 10);
		}

		for (int i = 0; i < myAgent.houses.size(); i++) {
			g.drawRect(myAgent.houses.get(i).getJ() * 50 + 75, myAgent.houses.get(i).getI() * 50 + 75, 10, 10);
			g.setColor(Color.MAGENTA);
			g.drawString("WareHouse", myAgent.houses.get(i).getJ() * 50 + 85,
					myAgent.houses.get(i).getI() * 50 + 100);
			g.fillRect(myAgent.houses.get(i).getJ() * 50 + 75, myAgent.houses.get(i).getI() * 50 + 75, 10, 10);
		}

		for (int i = 0; i < myAgent.hands.size(); i++) {
			g.drawRect(myAgent.hands.get(i).getJ() * 50 + 75, myAgent.hands.get(i).getI() * 50 + 75, 10, 10);
			g.setColor(Color.ORANGE);
			g.drawString("Hands", myAgent.hands.get(i).getJ() * 50 + 85,
					myAgent.hands.get(i).getI() * 50 + 100);
			g.fillRect(myAgent.hands.get(i).getJ() * 50 + 75, myAgent.hands.get(i).getI() * 50 + 75, 10, 10);
		}

		for (int i = 0; i < myAgent.agentsFinal.size(); i++) {
			g.setColor(Color.WHITE);
			g.fillOval(myAgent.agentsFinal.get(i).getJ() * 50 + 75, myAgent.agentsFinal.get(i).getI() * 50 + 75, 10,
					10);
			g.drawString(myAgent.agentsFinal.get(i).getName(), myAgent.agentsFinal.get(i).getJ() * 50 + 80,
					myAgent.agentsFinal.get(i).getI() * 50 + 80);
		}
	}

	public double getShortestDistance(Local source, Local dest) {

		DijkstraShortestPath<Local, DefaultWeightedEdge> dj = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
				myAgent.cityMap, source, dest);

		return dj.getPathLength();
	}
}
