package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.dv8tion.jda.core.JDA;

public class GUI extends JFrame{

	JDA jda = null;
	JButton bt = new JButton("Close");
	JLabel lab = new JLabel();
	
	public GUI(JDA jda){

		this.jda = jda;
		this.setSize(300, 100);
		this.setTitle("Luka");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		initControles();
	}
	
	public void initControles(){
		
		JPanel zoneClient = (JPanel) this.getContentPane();
		zoneClient.setLayout(new BoxLayout(zoneClient, BoxLayout.Y_AXIS));
		zoneClient.add(bt);
		bt.addActionListener(new appActionListener());
		
	}
	
	class appActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			if(e.getSource() == bt){
				jda.shutdown();
				GUI.this.dispose();
			}
			
		}
		
	}
	
}
