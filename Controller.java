package testproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Ri Richi
 */
public class Controller extends JFrame
{

    private final int FRAME_WIDTH = 960;
    private final int FRAME_HEIGHT = 700;
    private final int INTERNAL_TITLEBAR_HEIGHT = 60;
    private final String TITLE = "Test project";

    private Toolkit toolkit;
    private Insets ins;

    private JDesktopPane desk;
    private JInternalFrame internalChartFrame,
            internalControlsFrame;

    private JMenuBar mb;
    private JMenu file, lookAndFeel, help;
    private JMenuItem jmiRefreshCharts,
            jmiResetWindows,
            jmiExit;

    private JTextField txtStockTicker, txtAPIkey;
    private JButton btnRefreshCharts;
    private JProgressBar progress;

    private int side;

    private StartDatePicker startDatePicker;
    private EndDatePicker endDatePicker;

    private ChartPanel chartPanel;
    private Chart chart;

    public Controller()
    {
        super();
        try
        {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
            try
            {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | UnsupportedLookAndFeelException ex)
            {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public void setUpGUI()
    {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new Close());

        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        Dimension dim1 = getToolkit().getScreenSize();
        int x = (int) dim1.getWidth() / 2 - (getWidth() / 2);
        int y = (int) dim1.getHeight() / 2 - (getHeight() / 2);
        setLocation(x, y);
        setLayout(new BorderLayout());

        toolkit = Toolkit.getDefaultToolkit();
        Image logoImage = toolkit.getImage(getClass().getResource("logo.png"));
        setIconImage(logoImage);
        setTitle(TITLE);

        mb = new JMenuBar();
        file = new JMenu("File");

        jmiRefreshCharts = new JMenuItem("Refresh charts");
        jmiRefreshCharts.addActionListener((ActionEvent e)
                ->
        {

            refreshCharts();
        });
        file.add(jmiRefreshCharts);
        file.addSeparator();
        jmiResetWindows = new JMenuItem("Reset windows");
        file.add(jmiResetWindows);
        file.addSeparator();
        jmiExit = new JMenuItem("Exit");
        jmiExit.addActionListener((ActionEvent e)
                ->
        {
            System.exit(0);
        });

        file.add(jmiExit);

        lookAndFeel = new JMenu("Look&Feel");

        ButtonGroup grp = new ButtonGroup();
        LookAndFeelInfo laf[] = getInstalledLookAndFeels();
        for (LookAndFeelInfo laf1 : laf)
        {
            if (!laf1.getName().equals("CDE/Motif"))
            {
                JRadioButtonMenuItem rbt = new JRadioButtonMenuItem(laf1.getName());
                rbt.setName("rbt" + laf1.getName());
                if (laf1.getName().equals("Nimbus"))
                {
                    rbt.setSelected(true);
                }
                lookAndFeel.add(rbt);
                grp.add(rbt);
                String s = laf1.getClassName();
                rbt.addActionListener((ActionEvent e)
                        ->
                {
                    try
                    {
                        setLookAndFeel(s);
                        SwingUtilities.updateComponentTreeUI(Controller.this);
                    } catch (ClassNotFoundException
                            | InstantiationException
                            | IllegalAccessException
                            | UnsupportedLookAndFeelException excep)
                    {
                        JOptionPane.showMessageDialog(this, excep.getMessage());
                        try
                        {
                            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        } catch (ClassNotFoundException
                                | InstantiationException
                                | IllegalAccessException
                                | UnsupportedLookAndFeelException ex)
                        {
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                        }
                    }
                });
            }
        }//end for.

        help = new JMenu("Help");
        

        mb.add(file);
        mb.add(lookAndFeel);
        mb.add(Box.createHorizontalGlue());
        mb.add(help);
        setJMenuBar(mb);
        //end menuBar.
        setVisible(true);
        startDatePicker = new StartDatePicker();
        endDatePicker = new EndDatePicker();

        ins = getInsets();
        side = (getWidth() / 5);

        progress = new JProgressBar();

        setUpControlsFrame();
        setUpChartFrame();

        resetWindows();
        jmiResetWindows.addActionListener((ActionEvent e)
                ->
        {
            resetWindows();
            internalControlsFrame.show();
            internalChartFrame.show();
            try
            {
                internalControlsFrame.setSelected(false);
                internalChartFrame.setSelected(false);
            } catch (PropertyVetoException ex)
            {
                //
            }
        });

        desk = new JDesktopPane();
        desk.setBackground(Color.LIGHT_GRAY);

        desk.add(internalControlsFrame);
        desk.add(internalChartFrame);

        add(desk, BorderLayout.CENTER);
        btnRefreshCharts.addActionListener((ActionEvent e)
                ->
        {
            refreshCharts();
        });
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                resetWindows();
                revalidate();
            }
        });
        revalidate();
    }//end setUpGUI.

    private void setUpControlsFrame()
    {
        internalControlsFrame = new JInternalFrame("Controls", true, true, true, true);
        internalControlsFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        internalControlsFrame.setLayout(new BorderLayout());
        internalControlsFrame.setVisible(true);

        txtStockTicker = new JTextField("MSFT");
        txtAPIkey = new JTextField("Bw_ab5sCuyZMttSWctc6");
        btnRefreshCharts = new JButton("Refresh Charts");
        Font font = new Font(null, Font.BOLD, 14);
        btnRefreshCharts.setFont(font);

        JPanel pnlEntryControls = new JPanel(new GridLayout(4, 0, 0, 15));

        JPanel pnlStockTicker = new JPanel(new GridLayout(2, 0, 0, 2));
        pnlStockTicker.add(new JLabel("Stock Ticker"));
        pnlStockTicker.add(txtStockTicker);

        JPanel pnlStartDate = new JPanel(new GridLayout(2, 0, 0, 2));
        pnlStartDate.add(new JLabel("Start Date"));
        pnlStartDate.add(startDatePicker);

        JPanel pnlEndDate = new JPanel(new GridLayout(2, 0, 0, 2));
        pnlEndDate.add(new JLabel("End Date"));
        pnlEndDate.add(endDatePicker);

        JPanel pnlAPIkey = new JPanel(new GridLayout(2, 0, 0, 2));
        pnlAPIkey.add(new JLabel("API key"));
        pnlAPIkey.add(txtAPIkey);

        pnlEntryControls.add(pnlStockTicker);
        pnlEntryControls.add(pnlStartDate);
        pnlEntryControls.add(pnlEndDate);
        pnlEntryControls.add(pnlAPIkey);

        internalControlsFrame.add(pnlEntryControls, BorderLayout.NORTH);
        internalControlsFrame.add(btnRefreshCharts, BorderLayout.SOUTH);
    }

    private void setUpChartFrame()
    {
        internalChartFrame = new JInternalFrame("Chart", true, true, true, true);
        internalChartFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        internalChartFrame.setVisible(true);

    }

    private void resetWindows()
    {
        internalControlsFrame.setBounds(0,
                0,
                getSide() - ins.right - ins.left,
                getHeight() - INTERNAL_TITLEBAR_HEIGHT);
        internalChartFrame.setBounds((getSide() - (ins.left + ins.right)), 0,
                getWidth() - (getSide() - ins.left + internalChartFrame.getInsets().right),
                getHeight() - INTERNAL_TITLEBAR_HEIGHT);
    }

    /**
     * @return the side
     */
    public int getSide()
    {
        return side;
    }

    /**
     * @param side the side to set
     */
    public void setSide(int side)
    {
        this.side = side;
    }

    private void refreshCharts()
    {
        if (chartPanel != null)
        {
            internalChartFrame.remove(chartPanel);
            repaint();
        }
        progress = new JProgressBar();
        progress.setIndeterminate(true);
        JPanel pnlProgress = new JPanel(new GridLayout(2, 0, 10, 100));
        JPanel pnlMessage = new JPanel();
        Font font = new Font(null, Font.BOLD, 24);
        JLabel lbl = new JLabel("Fetching data");
        lbl.setFont(font);
        pnlMessage.add(lbl);
        pnlProgress.add(pnlMessage);
        pnlProgress.add(progress);
        internalChartFrame.add(pnlProgress, BorderLayout.SOUTH);
        revalidate();
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {

                Date selectedStartDate = (Date) startDatePicker.getModel().getValue();
                DateFormat startDF = new SimpleDateFormat("y-MM-dd");
                String startDate = startDF.format(selectedStartDate);

                Date selectedEndDate = (Date) endDatePicker.getModel().getValue();
                DateFormat endDF = new SimpleDateFormat("y-MM-dd");
                String endDate = endDF.format(selectedEndDate);

                chart = new Chart(txtStockTicker.getText(), txtStockTicker.getText(), startDate, endDate, txtAPIkey.getText());

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        chartPanel = new ChartPanel(chart.getChart());
                        internalChartFrame.remove(pnlProgress);
                        internalChartFrame.add(chartPanel, BorderLayout.CENTER);
                        revalidate();
                    }
                });

            }
        };

        worker.start();
       
    }//end refreshCharts.


    //registered with the frame.
    //inner class Close extends the WindowAdapter class
    //and overrides the windowClosing method.
    private class Close extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent we)
        {
            System.exit(0);
        }
    }

}
