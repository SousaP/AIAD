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
		setSize(1000, 600);
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


		 visto = new ArrayList<String>();
		 
		 Line2D line = new Line2D.Double(10, 10, 40, 40);
        /* g2.setColor(Color.blue);
         g2.setStroke(new BasicStroke(10));
         g2.draw(line); */
		 
		 g.drawRect(10,10,1000,1000);  
		    g.setColor(Color.GREEN);  
		    g.fillRect(10,10,1000,1000);  
		 desenhaGrafo(g2,myAgent.map.get("A"));
		 desenhaLocais(g2);
		
			
	}
	
	
	
	void desenhaGrafo(Graphics2D g, Local init)
	{ 
		Set<DefaultWeightedEdge> sucessorEdges = myAgent.cityMap.edgesOf(init);

		Iterator<DefaultWeightedEdge> iter = sucessorEdges.iterator();
		


		// The Edge that comes from the parent to the current node must not be
		// present on the sucessors List
		
		

		while (iter.hasNext()) {

			DefaultWeightedEdge edge = iter.next();
			Local tempTarg = myAgent.cityMap.getEdgeTarget(edge);
			Local tempSource = myAgent.cityMap.getEdgeSource(edge);
			
			g.drawRect( tempTarg.getJ() * 75 + 100,tempTarg.getI() * 15 + 100, 10, 10);
			g.setColor(Color.BLACK);
			g.fillRect(tempTarg.getJ() *  75 + 100,tempTarg.getI() * 15 + 100, 10, 10);
			g.drawLine(tempTarg.getJ() *  75 + 105, tempTarg.getI() * 15 + 105, tempSource.getJ() *  15 + 105, tempSource.getI() *  15 + 105);
			
			
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

	void desenhaLocais(Graphics2D g){
		for(int i = 0; i < myAgent.chargers.size(); i++){
		g.drawRect( myAgent.chargers.get(i).getJ() *  75 + 100,myAgent.chargers.get(i).getI() * 15 + 100, 10, 10);
		g.setColor(Color.YELLOW);
		g.fillRect(myAgent.chargers.get(i).getJ() *  75 + 100,myAgent.chargers.get(i).getI() * 15 + 100, 10, 10);
		}
		
		for(int i = 0; i < myAgent.dumps.size(); i++){
			g.drawRect( myAgent.dumps.get(i).getJ() *  75 + 100,myAgent.dumps.get(i).getI() * 15 + 100, 10, 10);
			g.setColor(Color.BLUE);
			g.fillRect(myAgent.dumps.get(i).getJ() *  75 + 100,myAgent.dumps.get(i).getI() * 15 + 100, 10, 10);
			}
		
		for(int i = 0; i < myAgent.stores.size(); i++){
			g.drawRect( myAgent.stores.get(i).getJ() *  75 + 100,myAgent.stores.get(i).getI() * 15 + 100, 10, 10);
			g.setColor(Color.RED);
			g.fillRect(myAgent.stores.get(i).getJ() *  75 + 100,myAgent.stores.get(i).getI() * 15 + 100, 10, 10);
			}
		
		for(int i = 0; i < myAgent.houses.size(); i++){
			g.drawRect( myAgent.houses.get(i).getJ() *  75 + 100,myAgent.houses.get(i).getI() * 15 + 100, 10, 10);
			g.setColor(Color.MAGENTA);
			g.fillRect(myAgent.houses.get(i).getJ() *  75 + 100,myAgent.houses.get(i).getI() * 15 + 100, 10, 10);
			}
		
		for(int i = 0; i < myAgent.hands.size(); i++){
			g.drawRect( myAgent.hands.get(i).getJ() *  75 + 100,myAgent.hands.get(i).getI() * 15 + 100, 10, 10);
			g.setColor(Color.ORANGE);
			g.fillRect(myAgent.hands.get(i).getJ() *  75 + 100,myAgent.hands.get(i).getI() * 15 + 100, 10, 10);
			}
	}
	
	
}

