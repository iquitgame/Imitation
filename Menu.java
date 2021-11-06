import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Menu {
	
	float speed = 1.0f;
	int loop = 1;

	final int MIN_SPEED = 1;
	final int MAX_SPEED = 7;
	final int DEFAULT_SPEED = 4;
	
	Menu(){
		TreeMap<Float, java.awt.Point> storedClicks = new TreeMap<>();
		
		final LocalTime initial = java.time.LocalTime.now();
		LocalTime current = java.time.LocalTime.now();
		
		Frame frame = new Frame();
		Button load = new Button("Load");
		Button export = new Button ("Export");
		Button record = new Button("Record");
		TextField filename = new TextField();
		Button stop = new Button("Stop");
		Button play = new Button("Play");
		Button exit = new Button("Exit");
		TextField loopTextField = new TextField();
		
		filename.setEditable(true);
		filename.setText("filename");
		
		loopTextField.setEditable(true);
		loopTextField.setText(String.valueOf(loop));
		
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
			              new FileOutputStream("D:\\Users\\admm6\\Desktop\\Imitation Files\\" + filename.getText() + ".txt"), "utf-8"))) {
					
					for (Map.Entry<Float, java.awt.Point> click : storedClicks.entrySet()) {
						writer.write(click.getKey().toString() + ":" + click.getValue().x +","+click.getValue().y + "\n");
					}
			} catch (IOException e1) {e1.printStackTrace();}
			}
		});
		
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				storedClicks.clear();
				try(BufferedReader reader
						   = new BufferedReader(new FileReader(System.getProperty("user.dir") + filename.getText()));){
					String line = reader.readLine();
					
					while(line != null) {
						System.out.println(line);
						String[] array = line.split(":");
						float time = Float.valueOf(array[0]);
						Point space = new Point(Integer.valueOf(array[1].split(",")[0]),Integer.valueOf(array[1].split(",")[1]));
						line = reader.readLine();
						storedClicks.put(time, space);
					}
					
				} catch (FileNotFoundException e1) {e1.printStackTrace();
				} catch (IOException e1) {e1.printStackTrace();}
			}
		});
		
		record.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				storedClicks.clear();
				System.out.println("Cleared: " + storedClicks);
				frame.remove(record);
				//for(ActionListener a : record.getActionListeners()) {
				//	record.removeActionListener(a);
				//}
				stop.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(storedClicks.toString());
						// TODO Auto-generated method stub
						frame.remove(stop);
						//for(ActionListener a : stop.getActionListeners()) {
						//	stop.removeActionListener(a);
						//}
						for(KeyListener a : stop.getKeyListeners()) {
							stop.removeKeyListener(a);
						}
						frame.add(record);
					}
					
				});
				frame.add(stop);
				
				stop.addKeyListener(new KeyListener() {
					final LocalTime initial = java.time.LocalTime.now();
					@Override
					public void keyTyped(KeyEvent e) {
						if(e.getKeyCode() >= KeyEvent.VK_A || e.getKeyCode() <= KeyEvent.VK_Z) {
							LocalTime current = java.time.LocalTime.now();
							Integer hourDiff = current.getHour() - initial.getHour();
							Integer minuteDiff = current.getMinute() - initial.getMinute();
							Integer secondDiff = current.getSecond() - initial.getSecond();
							Integer nanoDiff = current.getNano() - initial.getNano();
							
							float diff = hourDiff*60.0f*60.0f + minuteDiff*60.0f + secondDiff + (float)nanoDiff/1000000000.0f;
							System.out.println(diff);
							System.out.println(MouseInfo.getPointerInfo().getLocation());
							storedClicks.put(diff, MouseInfo.getPointerInfo().getLocation());
						}
					}

					@Override
					public void keyPressed(KeyEvent e) {
						
					}

					@Override
					public void keyReleased(KeyEvent e) {
						
					}
					
				});
				stop.requestFocus();
			}
		});
		play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loop = Integer.parseInt(loopTextField.getText());
				
				for(int i = 0; i < loop; i++) {
					System.out.println("Repeat: " + i);
					play.setLabel(String.valueOf(loop - i));
					LocalTime playStart = java.time.LocalTime.now();
					for (Map.Entry<Float, java.awt.Point> click : storedClicks.entrySet()) {
						Robot robot = null;
						try {
							robot = new Robot();
						} catch (AWTException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							System.out.println("Robot failed to initialize");
							break;
						}
						LocalTime now = java.time.LocalTime.now();
						float nowInSeconds = now.getHour()*60.0f*60.0f + now.getMinute()*60.0f + now.getSecond() + (float)now.getNano()/1000000000.0f;
						float playStartInSeconds = playStart.getHour()*60.0f*60.0f+playStart.getMinute()*60.0f+playStart.getSecond()+(float)playStart.getNano()/1000000000.0f;
						float waitTime = click.getKey() - (nowInSeconds - playStartInSeconds);
						try {
							System.out.println("waitTime: " + waitTime + " vs. waitTime with speed modifier: " + waitTime/speed);
							robot.delay((int)(waitTime*1000/speed));
						} catch (IllegalArgumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					    //System.out.println(click.getKey() + "/" + click.getValue());
					    robot.mouseMove(click.getValue().x, click.getValue().y);
					    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					}
				
				}
				play.setLabel("Play");
			}
			
		});
		play.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("canceled");
				loop = 1;
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
			}
		});

		loopTextField.setBounds(40,30,30,20);
		load.setBounds(80,30,50,20);
		export.setBounds(140,30,50,20);
		filename.setBounds(200,30,100,20);
		record.setBounds(40,100,80,30);
		stop.setBounds(40,100,80,30);
		play.setBounds(130, 100, 80, 30);
		exit.setBounds(220, 100, 80, 30);
		
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, MAX_SPEED, DEFAULT_SPEED);
		speedSlider.setBounds(40, 60, 260, 30);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setSnapToTicks(true);
		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider change = (JSlider)e.getSource();
				speed = (float) Math.pow(2, change.getValue()-4);
			}
			
		});
		
		frame.add(speedSlider);
		frame.add(loopTextField);
		frame.add(load);
		frame.add(export);
		frame.add(filename);
		frame.add(record);
		frame.add(play);
		frame.add(exit);
		frame.setSize(340,150);
		frame.setLayout(null);
		
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		frame.setVisible(true);
	}
	
}
