package Simulation;

import java.awt.BasicStroke;
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
		 
		 Line2D line = new Line2D.Double(10, 10, 40, 40);
        /* g2.setColor(Color.blue);
         g2.setStroke(new BasicStroke(10));
         g2.draw(line); */
		 
		 g.drawRect(10,10,1000,1000);  
		    g.setColor(Color.GREEN);  
		    g.fillRect(10,10,1000,1000);  
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
			
			
			
			
			
			g.drawRect( tempTarg.getJ() *  15 + 100,tempTarg.getI() * 15 + 100, 10, 10);
			g.setColor(Color.BLACK);
			g.fillRect(tempTarg.getJ() *  15 + 100,tempTarg.getI() * 15 + 100, 10, 10);
			
			
			g.drawRect( tempSource.getJ() *  15 + 100, tempSource.getI() * 15 + 100, 10, 10);
			g.setColor(Color.BLACK);
			g.fillRect( tempSource.getJ() *  15 + 100, tempSource.getI() * 15 + 100, 10, 10);
			
			if(init.getName() == tempSource.getName()){
				visto.add(tempSource.getName());
				if( !visto.contains(tempTarg.getName())){
					desenhaGrafo(g,tempTarg);
				}
			}
			else if(init.getName() == tempTarg.getName())
			{
				visto.add(tempTarg.getName());
				if( !visto.contains(tempSource.getName())){
					desenhaGrafo(g,tempSource);
				}	
			}
		
			
			
		}
	}

}
