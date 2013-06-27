import java.net.Socket;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Simple monitor for the simulator (or whoever)
 */
public class SimMonitor extends JFrame implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private JTextField xField, yField, dxField, dyField;
    private JTextArea displayArea;
    private JOptionPane confPane;
    private JDialog dialog; // only one at a time, right?
    private long msgTimeout, confTime;

    /**
     * Constructor, opens a socket to the simulator
     */
    public SimMonitor() {
	setTitle("Simple Sim Monitor");
	JPanel pospanel = new JPanel();
	pospanel.add(new JLabel("X: "));
	xField = new JTextField(15);
	xField.setEditable(false);
	pospanel.add(xField);
	pospanel.add(new JLabel("Y: "));
	yField = new JTextField(15);
	yField.setEditable(false);
	pospanel.add(yField);
	add(pospanel, BorderLayout.NORTH);

	JPanel destpanel = new JPanel();
	destpanel.add(new JLabel("DEST X: "));
	dxField = new JTextField(15);
	dxField.setEditable(false);
	destpanel.add(dxField);
	destpanel.add(new JLabel("Y: "));
	dyField = new JTextField(15);
	dyField.setEditable(false);
	destpanel.add(dyField);
	add(destpanel, BorderLayout.SOUTH);

	displayArea = new JTextArea(10,40);
	displayArea.setEditable(false);
	add(displayArea);
	
	try {
	    Socket s = new Socket("localhost",SimpleSim.MONITOR_PORT);
            out = new PrintWriter(s.getOutputStream());
	    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	} catch (IOException e) {
	    displayArea.setText("Unable to establish connection to robot");
	}
    }

    /**
     * Main loop, listens to the socket and updates the GUI.
     */    
    public void run() {
	while(true) {
	    //System.out.println("Waiting for messages on " + in);
	    try {
                // ordinarily we would be worried about blocking but we are
                // more or less guaranteed to get a POS update once per second.
		String l = in.readLine();
		String[] parts = l.split(" ");
		if (!parts[0].equals("POS") && !parts[0].equals("DEST")) System.out.println(l);
		if (parts[0].equals("POS")) {
		    xField.setText(parts[1]);
		    yField.setText(parts[2]);
		} else if (parts[0].equals("DEST")) {
		    dxField.setText(parts[1]);
		    dyField.setText(parts[2]);
		} else if (parts[0].equals("SHOW_MSG")) {
                    msgTimeout = System.currentTimeMillis() + 1000 * Long.valueOf(parts[1]);
		    for (int i = 2; i < parts.length; i++) {
			displayArea.append(parts[i] + " ");
		    }
		    displayArea.append("\n");
		} else if (parts[0].equals("SHOW_MSG_CONFIRM")) {
                    String msg = l.substring(l.indexOf(' ', l.indexOf(' ')+1));
                    confPane = new JOptionPane(msg);
                    dialog = confPane.createDialog(this, "Confirmation request");
                    dialog.setVisible(true);
                    confTime = System.currentTimeMillis() + 1000 * Long.valueOf(parts[1]);
                }
                if (dialog != null) {
                    if (confTime < System.currentTimeMillis()) {
                        dialog.dispose();
                        confPane = null;
                        dialog = null;
                        out.println("TIMEOUT");
                        out.flush();
                    } else if (confPane.getValue() == null) {
                        // null if window closed(?!)
                        confPane = null;
                        dialog = null;
                        out.println("IGNORED");
                        out.flush();
                    } else if (confPane.getValue() != JOptionPane.UNINITIALIZED_VALUE) {
                        confPane = null;
                        dialog = null;
                        out.println("CONFIRMED");
                        out.flush();
                    }
                }
                if (msgTimeout > 0 && msgTimeout < System.currentTimeMillis()) {
                    displayArea.setText("");
                    msgTimeout = 0;
                }
	    } catch (IOException e) {
		displayArea.setText("Lost connection to robot");
	    }
	}
    }

    public static void main(String[] args) {
	SimMonitor sm = new SimMonitor();
	sm.pack();
	new Thread(sm).start();
	sm.setVisible(true);
    }
}