package testproject;

import java.awt.BorderLayout;
import java.util.Properties;
import javax.swing.JPanel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author Ri Richi
 */
public class EndDatePicker extends JPanel
{

    private final UtilDateModel model;
    private final Properties pr;
    private final JDatePanelImpl datePanel;
    private final JDatePickerImpl dpStartDate;

    public EndDatePicker()
    {
        model = new UtilDateModel();
        pr = new Properties();
        pr.put("text.today", "Today");
        pr.put("text.month", "Month");
        pr.put("text.year", "Year");
        datePanel = new JDatePanelImpl(model, pr);
        dpStartDate = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        setUpEndDatePicker();
    }

    private void setUpEndDatePicker()
    {
        this.setLayout(new BorderLayout());
        this.add(dpStartDate);
    }
    
    public UtilDateModel getModel()
    {
        return model;
    }

}
