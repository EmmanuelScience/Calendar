import javax.swing.*;
import java.awt.*;



public class CalendarFrame extends JFrame {

    JMenuBar menuBar;
    JMenu file_menu, view_menu;
    ButtonGroup button_group;
    JMenuItem exit_item;
    JRadioButtonMenuItem day_radio_button;
    JRadioButtonMenuItem month_radio_button;
    Panels panel;
    public static int width;

    CalendarFrame(){
        //Frame Settings------------------------------------------------------------------------------------------------
        this.setSize(900,600); // sets x and y dimensions
        this.setTitle("My Calendar");// sets Title
        this.setResizable(true);//makes resizable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Exit out of application
        this.setVisible(true); //makes frame visible
        this.setMinimumSize(new Dimension(500, 500));


        Rectangle size = this.getBounds();
        System.out.println(size.getWidth());
        width = this.getWidth();

        //Menu Assignation Stuff----------------------------------------------------------------------------------------
        menuBar = new JMenuBar();
        file_menu = new JMenu("File");
        view_menu = new JMenu("Views");
        button_group = new ButtonGroup();
        menuBar.add(file_menu);
        menuBar.add(view_menu);
        exit_item = new JMenuItem("Exit");
        day_radio_button = new JRadioButtonMenuItem(" Day View");
        month_radio_button = new JRadioButtonMenuItem(" Month View");

        //Menu adding stuff---------------------------------------------------------------------------------------------
        button_group.add(day_radio_button);
        button_group.add(month_radio_button);
        file_menu.add(exit_item);
        view_menu.add(day_radio_button);
        view_menu.add(month_radio_button);

        //Creates a panel object----------------------------------------------------------------------------------------
        panel = new Panels();

        //Adds the panels to the frame----------------------------------------------------------------------------------
        this.add(panel.left_panel, BorderLayout.WEST);
        this.add(panel.right_panel, BorderLayout.CENTER);
        this.add(panel.status_panel, BorderLayout.SOUTH);

        //Sets Menu Bar-------------------------------------------------------------------------------------------------
        this.setJMenuBar(menuBar);

        //Sets Buttons functionalities----------------------------------------------------------------------------------
        exit_radio_button();
        day_radio_btn();
        month_radio_btn();
        file_menu_btn();
        view_menu_btn();

    }

    // Action listener for the file menu button
    public void file_menu_btn() {
        file_menu.addActionListener(e -> Panels.status_label.setText("File Menu Selected"));
    }

    // Action listener for the view menu button
    private void view_menu_btn() {
        view_menu.addActionListener(e -> Panels.status_label.setText("View Menu Selected"));
    }

    // Action listener for the month radio button
    // Changes the view to month
    private void month_radio_btn() {
        month_radio_button.addActionListener(e -> {
            Panels.status_label.setText("Month View Selected");
            panel.set_view_to_month();
        });
    }

    // Action listener for the day radio button
    // Changes the view to day
    private void day_radio_btn() {
        day_radio_button.addActionListener(e -> {
            Panels.status_label.setText("Day View Selected");
            panel.set_view_to_day();
        });
    }




    // Action listener for the exit menu item.
    // The application is closed when this button is pressed
    private void exit_radio_button() {
        exit_item.addActionListener(e -> {
            Panels.status_label.setText("Exiting App");
            System.out.println("exited");
            System.exit(0);
        });
    }
}
