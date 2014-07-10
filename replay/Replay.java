import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Replay extends JFrame {

    /* 
     * would like to see/have: 
     * - full path with robot angles (and velocity arrows) maybe some timestamps
     * - locations of QR readings
     * - animated replay with same
     * - timestamp on replay
     * - zoom?!
     * - pose discontinuity detection
     */

    private TreeMap<Double,Pose> poses = new TreeMap<Double,Pose>();
    private TreeMap<Double,Pose> lcamposes = new TreeMap<Double,Pose>();
    private TreeMap<Double,Pose> rcamposes = new TreeMap<Double,Pose>();
    private TreeMap<Double,Pose> laserposes = new TreeMap<Double,Pose>();
    private PlotPanel plotarea;
    private JCheckBox stampbut;
    private JSlider stampslider;
    private boolean showStamps;

    public Replay (String logdirname)  throws IOException {
        readPoses(logdirname);
        readQRs(logdirname+"left-1-stdout.log", lcamposes);
        readQRs(logdirname+"right-9-stdout.log", rcamposes);
        readLaser(logdirname);
        plotarea = new PlotPanel();
        add(plotarea,BorderLayout.CENTER);
        JPanel bpan = new JPanel();
        JButton zinbut = new JButton("+");
        zinbut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    plotarea.scale *= 0.67; // fewer meters per pixel
                    repaint();
                }});
        bpan.add(zinbut);
        JButton zoutbut = new JButton("-");
        zoutbut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    plotarea.scale *= 1.5;
                    repaint();
                }});
        bpan.add(zoutbut);
        stampbut = new JCheckBox("Timestamps");
        stampbut.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) { 
                    showStamps = stampbut.isSelected(); repaint(); } });
        bpan.add(stampbut);
        stampslider = new JSlider(JSlider.HORIZONTAL,1,15,5);
        stampslider.setMajorTickSpacing(14);
        stampslider.setMinorTickSpacing(1);
        stampslider.setPaintTicks(true);
        stampslider.setPaintLabels(true);
        stampslider.addChangeListener(new ChangeListener() { 
                public void stateChanged(ChangeEvent e) { repaint(); } });
        bpan.add(stampslider);
        add(bpan,BorderLayout.SOUTH);
    }

    private void readPoses(String logdirname) throws IOException{
        Scanner navscan = new Scanner(new File(logdirname+"obstacle_avoidance-3-stdout.log"));
        while (navscan.hasNextLine()) {
            String line = navscan.nextLine();
            if (line.indexOf("Pose") != -1) {
                // should be [DEBUG] [time]: Pose: (x, y), <theta>
                String[] parts = line.split("\\s");
                double tm = Double.valueOf(parts[1].substring(1,parts[1].length()-2));
                double x = Double.valueOf(parts[3].substring(1,parts[3].length()-1));
                double y = Double.valueOf(parts[4].substring(0,parts[4].length()-2));
                double th = Double.valueOf(parts[5].substring(1,parts[5].indexOf(">")));
                // now consume until we get a NavVel to go with it
                try {
                    while (line.indexOf("NavVel") == -1)
                        line = navscan.nextLine();
                    // [DEBUG] [time]: NavVel: <+/- v, omega>
                    parts = line.split("\\s");
                    //System.out.println(parts[4]);
                    double v = Double.valueOf(parts[3].substring(1,parts[3].length()-1));
                    double omega = Double.valueOf(parts[4].substring(0,parts[4].indexOf(">")));
                    poses.put(tm, new Pose(x,y,th,v,omega));
                } catch (NoSuchElementException e) { 
                    // oh well, EOF in the middle of the update, just ignore the last pose
                }
            }
        }
        System.out.println(poses.firstEntry().getValue());
        System.out.println(poses.size());
    }

    private void readQRs(String logfile, TreeMap<Double,Pose> poses) throws IOException {
        Scanner scan = new Scanner(new File(logfile));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.indexOf("robx") != -1) {
                String[] parts = line.split("\\s");
                double tm = Double.valueOf(parts[2].substring(1,parts[2].length()-2));
                double x = Double.valueOf(parts[4]);
                double y = Double.valueOf(parts[6]);
                double th = Double.valueOf(parts[8].substring(0,4));
                poses.put(tm,new Pose(x,y,th,0,0));
            }
        }
    }

    private void readLaser(String logdirname) throws IOException {
        Scanner scan = new Scanner(new File(logdirname+"kinnav-5-stdout.log"));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.indexOf("Mean") != -1) {
                try {
                    String[] parts = line.split("\\s");
                    double tm = Double.valueOf(parts[2].substring(0,parts[2].length()-2));
                    double x = Double.valueOf(parts[5].substring(1,parts[5].length()-2));
                    double y = Double.valueOf(parts[6].substring(0,parts[6].length()-2));
                    double th = Double.valueOf(parts[7].substring(0,parts[7].length()-2));
                    line = scan.nextLine();
                    // now, is it valid?  let's do a bad thing and use pose.v as a flag
                    // negative v means we didn't get a good reading
                    if (line.indexOf("Skip") != -1) {
                        laserposes.put(tm, new Pose(x,y,th,-1,0));
                    }
                    else {
                        laserposes.put(tm, new Pose(x,y,th,1,0));
                    }
                } catch (NoSuchElementException e) { 
                } catch (NumberFormatException e) { }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Replay r = new Replay(args[0]);
            r.pack();
            r.setVisible(true);
        } catch (IOException e) {
            System.err.println("Construction failed: " + e);
        }
    }

    class PlotPanel extends JPanel implements MouseListener,MouseMotionListener {
        Dimension mySize;
        double scale;
        double xcenter, ycenter;
        boolean drawStamps;
        int fromx, fromy;
        double fromxc, fromyc;
        boolean dragging;

        public PlotPanel() {
            mySize = new Dimension(800,500);
            scale = 0.1; // (m/pix) so 50 m in 500 pix to start off
            xcenter = 50; // in meters
            ycenter = 25;
            drawStamps = false;
            setBackground(Color.WHITE);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public Dimension getPreferredSize() { return mySize; }
        
        public void mousePressed(MouseEvent e) {
            fromx = e.getX(); fromy = e.getY();
            fromxc = xcenter; fromyc = ycenter;
            //tox = fromx; toy = fromy;
            //dragging = true;
        }

        public void mouseDragged(MouseEvent e) { 
            xcenter = fromxc - (e.getX() - fromx)*scale;
            ycenter = fromyc - (fromy - e.getY())*scale;
            repaint();
        }
        
        public void mouseReleased(MouseEvent e) {
            //dragging = false;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                // double click, zoom in here
                xcenter += (e.getX() - mySize.width/2) * scale;
                ycenter -= (e.getY() - mySize.height/2) * scale;
                scale *= 0.67;
                repaint();
            }
        }
        public void mouseMoved(MouseEvent e) { }
        public void mouseEntered(MouseEvent event) { }
        public void mouseExited(MouseEvent event) { }

        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            //Graphics2D g = (Graphics2D)(gr);
            //            System.out.println("drawing?");
            double laststamp = 0;
            for (Double tm : poses.keySet()) {
                Pose p = poses.get(tm);
                int xpix = (int)((p.x - xcenter) / scale) + mySize.width/2;
                int ypix = mySize.height/2 - (int)((p.y - ycenter)/ scale);
                if (laserposes.lowerEntry(tm).getValue().v < 0)
                    gr.setColor(Color.RED);
                else
                    gr.setColor(Color.GREEN);
                gr.drawOval(xpix, ypix, 2, 2);
                if (showStamps && (tm - laststamp > stampslider.getValue())) {
                    laststamp = tm;
                    gr.drawString(stampstr(tm),xpix + 2, ypix);
                }
                //System.out.println("drawing at " + (int)((p.x / scale) - xoff) + ", " + 
                //                 (int)(mySize.height - ((p.y / scale) - yoff)));
            }            
            for (Double tm : lcamposes.keySet()) {
                Pose p = lcamposes.get(tm);
                int xqpix = (int)((p.x - xcenter) / scale) + mySize.width/2;
                int yqpix = mySize.height/2 - (int)((p.y - ycenter)/ scale);
                gr.setColor(Color.BLUE);
                gr.drawOval(xqpix, yqpix, 2, 2);
                gr.drawString("L"+(showStamps?stampstr(tm):""),xqpix + 2, yqpix);
            }
            for (Double tm : rcamposes.keySet()) {
                Pose p = rcamposes.get(tm);
                int xqpix = (int)((p.x - xcenter) / scale) + mySize.width/2;
                int yqpix = mySize.height/2 - (int)((p.y - ycenter)/ scale);
                gr.setColor(Color.BLUE);
                gr.drawOval(xqpix, yqpix, 2, 2);
                gr.drawString("R"+(showStamps?stampstr(tm):""),xqpix + 2, yqpix);
            }
            /* draw a scale bar */
            gr.setColor(Color.BLACK);
            int meters = (int)(scale * 100);
            if (meters == 0) {
                int cm = (int)(scale * 10000);
                gr.drawLine((mySize.width - 20) - (int)(cm/(100*scale)), mySize.height-20, 
                            mySize.width - 20, mySize.height - 20);
                gr.drawString(cm + "cm", mySize.width - 70, mySize.height - 30);
            } else {
                gr.drawLine((mySize.width - 20) - (int)(meters/scale), mySize.height-20, 
                            mySize.width - 20, mySize.height - 20);
                gr.drawString(meters + "m", mySize.width - 70, mySize.height - 30);
            }
        }
    }

    private String stampstr(Double tm) {
        return (new Double(tm % 10000)).toString().substring(0,6);
    }
    
    class Pose {
        public double x, y, theta, v, omega;
        public Pose(double x, double y, double theta, double v, double omega) {
            this.x = x; this.y = y; this.theta = theta; this.v = v; this.omega = omega;
        }
        public String toString() {
            return ("("+ x + ", " + y + ", " + theta + ") v: <" + v + "," + omega + ">");
        }
    }
            
}
