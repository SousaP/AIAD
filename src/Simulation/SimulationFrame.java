package Simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jgrapht.graph.DefaultWeightedEdge;

import locals.Local;

public class SimulationFrame extends JFrame {
	private SimulationAgent myAgent;
	JPanel panel = new JPanel();
	List<String> visto = new ArrayList<String>();

	private List<Local> locais;
		

	public SimulationFrame(SimulationAgent simulationAgent) {
		super();
		setSize(600, 600);
		setTitle("Simulation");
		myAgent = simulationAgent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		getContentPane().add(panel);
		String startTemp = null;
		dispose();
		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g); // fixes the immediate problem.
		Graphics2D g2 = (Graphics2D) g;
		

		 System.out.println("PATH GetSucessors");
		 visto = new ArrayList<String>();
		desenhaGrafo(g,myAgent.map.get("A"));		
		
		 System.out.println("PATH END");
			
	}
	
	
	
	void desenhaGrafo(Graphics g, Local init)
	{ 
		Set<DefaultWeightedEdge> sucessorEdges = myAgent.cityMap.edgesOf(init);

		Iterator<DefaultWeightedEdge> iter = sucessorEdges.iterator();
		


		// The Edge that comes from the parent to the current node must not be
		// present on the sucessors List
		
		

		while (iter.hasNext()) {

			DefaultWeightedEdge edge = iter.next();
			Local tempTarg = myAgent.cityMap.getEdgeTarget(edge);
			Local tempSource = myAgent.cityMap.getEdgeSource(edge);
			
			System.out.println("Source " + tempSource.getName());
			System.out.println("Targ " + tempTarg.getName());
			
			visto.add(tempSource.getName());
			
			g.drawRect(tempTarg.getI() * 15 + 2, tempTarg.getJ() * 60, 60, 60);
			g.setColor(Color.BLACK);
			g.fillRect(tempTarg.getI() * 15 + 2, tempTarg.getJ() * 60, 60, 60);
			
			
			g.drawRect(tempSource.getI() * 15 + 2, tempSource.getJ() * 60, 60, 60);
			g.setColor(Color.BLACK);
			g.fillRect(tempSource.getI() * 15 +2, tempSource.getJ() * 60, 60, 60);
			
			if(init.getName() == tempSource.getName()){
				if( !visto.contains(tempTarg.getName())){
					desenhaGrafo(g,tempTarg);
				}
			}
			else if(init.getName() == tempTarg.getName())
			{
				if( !visto.contains(tempSource.getName())){
					desenhaGrafo(g,tempSource);
				}	
			}
		
			
			
		}
	}

}
