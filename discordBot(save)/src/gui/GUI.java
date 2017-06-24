package gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import handy.Handler;
import net.dv8tion.jda.core.JDA;

public class GUI extends JFrame{

	public JDA jda = null;
	public JButton bt = new JButton("Close");
	public JLabel lab = new JLabel();
	
	public GUI(JDA jda){

		this.jda = jda;
		this.setSize(250, 250);
		this.setTitle(Handler.botName + ", V" + Handler.versionNumber);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		initControles();
	}
	
	public void initControles(){
		
		JPanel zoneClient = (JPanel) this.getContentPane();
		zoneClient.setLayout(new BoxLayout(zoneClient, BoxLayout.Y_AXIS));
		lab.setAlignmentX(CENTER_ALIGNMENT);
		bt.setAlignmentX(CENTER_ALIGNMENT);
		
		lab.setBorder(BorderFactory.createBevelBorder(0));
		
		zoneClient.add(Box.createVerticalGlue());
		zoneClient.add(lab);
		zoneClient.add(Box.createVerticalGlue());
		zoneClient.add(bt);
		zoneClient.add(Box.createVerticalGlue());
		bt.addActionListener(new appActionListener());
		String link = jda.getSelfUser().getAvatarUrl();
		try {
			Image image = saveStream(link);
			ImageIcon icon = new ImageIcon(image);
			lab.setIcon(icon);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public BufferedImage saveStream( String mURL) throws Exception {
	    InputStream in = null;
	    FileOutputStream out = null;
	    try {
	        URL url = new URL(mURL);
	        URLConnection urlConn = url.openConnection();
	        urlConn.setRequestProperty ("User-agent", "Mozilla/5.0");
	        in = urlConn.getInputStream();
	        BufferedImage bufferedImage = ImageIO.read(in);
	        return bufferedImage;
	    } finally {
	        if (in != null)
	            in.close();
	        if (out != null)
	            out.close();
	    }
	}
	
}
