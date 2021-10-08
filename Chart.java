package testproject;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

/**
 *
 * @author Ri Richi
 */
public final class Chart extends JComponent
{

    private final ArrayList<OHLCDataItem> dataItems;
    
    private JFreeChart chart;
    private TimeSeries series;
    private StringBuilder sb;
    private final String chartTitle;
    private final String stockTicker;
    private final String startDate;
    private final String endDate;
    private final String apikey;

    public Chart(String title, String ticker, String sDate, String eDate, String key)
    {
        super();

        this.dataItems = new ArrayList<>();
        this.chartTitle = title;
        this.stockTicker = ticker;
        this.startDate = sDate;
        this.endDate = eDate;
        this.apikey = key;
        

        this.createChart();
    }//end constructor.

    public void createChart()
    {
        double volume;
        series = new TimeSeries("");
        //===========================================
        
            if (getStockTicker() != null || getStartDate() != null || getEndDate() != null || getKey() != null)
            {
                

                sb = new StringBuilder();
                sb.append("/api/v3/datasets/WIKI/");
                sb.append(getStockTicker());
                sb.append(".csv?start_date=");
                sb.append(getStartDate());
                sb.append("&end_date=");
                sb.append(getEndDate());
                sb.append("&api_key=");
                sb.append(getKey());
            } else
            {
                JOptionPane.showMessageDialog(
                        null, "Invalid request."
                        + System.lineSeparator()
                        + "Please enter correct Stock Ticker, Start & End dates.",
                        "Test Project", JOptionPane.INFORMATION_MESSAGE);

            }
            
        
        //=============================================
        try
        {
            URL url = new URL("https", "data.nasdaq.com", sb.toString());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream())))
            {
                Date date;
                DateFormat df = new SimpleDateFormat("y-M-d");
                String inputLine;
                in.readLine();
                while ((inputLine = in.readLine()) != null)
                {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    
                    date = df.parse(st.nextToken());
                    double open = Double.parseDouble(st.nextToken());
                    double high = Double.parseDouble(st.nextToken());
                    double low = Double.parseDouble(st.nextToken());
                    double close = Double.parseDouble(st.nextToken());
                    volume = Double.parseDouble(st.nextToken());
                    OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                    dataItems.add(item);
                    
                    series.add(new Day(date), volume);
                }
            }
        } catch (IOException | NumberFormatException | ParseException e)
        {
            JOptionPane.showMessageDialog(
                    null, "Failed to fetch data." + System.lineSeparator()
                    + e.toString(), "Test Project", JOptionPane.INFORMATION_MESSAGE);
        }

        Collections.reverse(dataItems); // Data is from newest to oldest. Reverse it.
        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        OHLCDataset datasetCandle = new DefaultOHLCDataset(getStockTicker(), data);

        //===========================
        ValueAxis dateAxis = new DateAxis("Date");
        dateAxis.setLowerMargin(0.02); // reduce the default margins on the time axis
        dateAxis.setUpperMargin(0.02);

        NumberAxis priceAxis = new NumberAxis("Price");
        priceAxis.setAutoRangeIncludesZero(false); // override default

        NumberAxis volumeAxis = new NumberAxis("Volume");
        volumeAxis.setNumberFormatOverride(new DecimalFormat("0.00"));

        CandlestickRenderer candle = new CandlestickRenderer(4, false, new HighLowItemLabelGenerator());
        candle.setDefaultSeriesVisibleInLegend(false);
        XYPlot subplotCandle = new XYPlot(datasetCandle, dateAxis, priceAxis, candle);
        subplotCandle.setBackgroundPaint(Color.white);
        subplotCandle.setDomainGridlinesVisible(true);
        subplotCandle.setDomainGridlinePaint(Color.lightGray);
        subplotCandle.setRangeGridlinePaint(Color.lightGray);
        ((NumberAxis) subplotCandle.getRangeAxis()).setAutoRangeIncludesZero(false);

        XYBarRenderer bar = new XYBarRenderer();
        bar.setDefaultSeriesVisibleInLegend(false);
        bar.setDefaultToolTipGenerator(new StandardXYToolTipGenerator("date={1}, volume={2}", new SimpleDateFormat("y-M-d"), new DecimalFormat("0")));
        TimeSeriesCollection datasetBar = new TimeSeriesCollection();
        datasetBar.addSeries(series);
        XYPlot subplotBar = new XYPlot(datasetBar, dateAxis, volumeAxis, bar);
        subplotBar.setBackgroundPaint(Color.white);

        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(dateAxis);
        plot.setGap(10.0);
        plot.add(subplotCandle, 3);
        plot.add(subplotBar, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        chart = new JFreeChart(getChartTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.white);
        
    }//end createChart.

    /**
     * @return the chart
     */
    public JFreeChart getChart()
    {
        return chart;
    }

    /**
     * @return the chartTitle
     */
    public String getChartTitle()
    {
        return chartTitle;
    }

    /**
     * @return the stockTicker
     */
    public String getStockTicker()
    {
        return stockTicker;
    }

    /**
     * @return the startDate
     */
    public String getStartDate()
    {
        return startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate()
    {
        return endDate;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return apikey;
    }

   
}
