import dollar.DollarRecognizer;
import dollar.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Panels {
    //Sets private and public variables for the labels, buttons and panels
    public  JPanel left_panel = new JPanel();
    public  JPanel right_panel = new JPanel();
    public  JPanel status_panel = new JPanel();
    public static JLabel status_label = new JLabel();
    private LocalDate day = LocalDate.now();
    private LocalDate temp_date;
    private LocalDate month;
    public  String view_type = "day_view";
    public Boolean pop_visible = true;
    private final DayView dayView = new DayView();
    private final MonthView monthView = new MonthView();
    public static HashMap<LocalDate, ArrayList<EventDetails>> events = new HashMap<>();
    public static JPanel pop_up_panel = new JPanel();
    private Point pt;
    private  boolean is_event = false;
    private boolean is_created = false;
    private boolean keep_apt_values = false;
    private DollarRecognizer dollar =new DollarRecognizer();
    private EventDetails aux_event, aux_event1;

    public static JDialog apt_dialog;
    private static JTextField new_apt_text;
    private static JTextField date_text;
    private static JTextField start_time_text;
    private static JTextField end_time_text;
    public static JCheckBox vacation_check = getCheck_vacation();
    public static JCheckBox school_check = getCheck_school();
    public static JCheckBox work_check = getCheck_work();
    public static JCheckBox family_check = getCheck_family();
    private boolean isDragging;

    JScrollPane scrolly = new JScrollPane(dayView);


    Panels(){
        dayView.day = day;
        //Sets scroll Pane data-----------------------------------------------------------------------------------------

        scrolly.setPreferredSize(new Dimension(500, 500));
        scrolly.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrolly.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Set right panel ---------------------------------------------------------------------------------------------
        //Sets the size and dimensions of the right panel in the Frame, also adds the Date label
        right_panel.setBackground(Color.white);
        right_panel.setLayout(new BorderLayout());
        right_panel.add(scrolly, BorderLayout.CENTER);


        // Set left panel ----------------------------------------------------------------------------------------------
        //Sets the size,colour and Layout of the left panel.
        //Layout is set to null to allow for specific button placements.
        left_panel.setBackground(new Color(150,200,200));
        left_panel.setPreferredSize(new Dimension(300, 100));
        left_panel.setLayout(null);

        // Set bottom panel --------------------------------------------------------------------------------------------
        // Sets the size and color of the bottom panel. This will be used as a status bar.
        // The status label will be updated as actions are made
        status_panel.setBackground(Color.white);
        status_panel.setPreferredSize(new Dimension(400, 20));
        status_panel.add(status_label);


        // Call pop up button functions
        // The 'cancel' and 'ok' buttons at the bottom of the new appointment dialog are called here
        JButton button_cancel_pop_up = getButton_cancel_pop_up();
        JButton button_ok_pop_up = getButton_ok_pop_up();

        // Set pop up panel -----------------------------------------------------
        //Creates and sets the values of the new appointment pop-up panel
        pop_up_panel.setPreferredSize(new Dimension(800, 400));
        pop_up_panel.setVisible(pop_visible); //makes frame visible

        // Sets the values and positions of the text fields used for the appointment dialog
        new_apt_text = new JTextField();
        new_apt_text.setBounds(50,10,700,50);

        date_text = new JTextField();
        date_text.setBounds(50,70,400,40);

        start_time_text = new JTextField();
        start_time_text.setBounds(50,120,400,40);

        end_time_text = new JTextField();
        end_time_text.setBounds(50,170,400,40);

        // Adds the components created above to the pop-up panel
        pop_up_panel.add(end_time_text, BorderLayout.CENTER);
        pop_up_panel.add(start_time_text);
        pop_up_panel.add(date_text);
        pop_up_panel.add(new_apt_text);
        pop_up_panel.add(vacation_check);
        pop_up_panel.add(family_check);
        pop_up_panel.add(work_check);
        pop_up_panel.add(school_check);
        pop_up_panel.add(button_cancel_pop_up);
        pop_up_panel.add(button_ok_pop_up);
        pop_up_panel.setLayout(new BorderLayout());

        //Set and call button functions --------------------------------------------------------------------------------
        // Calls the functions of the buttons used in the main frame
        JButton button_today = getButton_today();
        JButton button_prev = getButton_prev();
        JButton button_next = getButton_next();
        JButton button_new_apt = getButton_new_apt();


        //Set left panel buttons ---------------------------------------------------------------------------------------
        //Adds the buttons to the left panel
        left_panel.add(button_today);
        left_panel.add(button_next);
        left_panel.add(button_prev);
        left_panel.add(button_new_apt);



        //Dialog stuff------------------------------------------------------
        // Creates and sets the values of the new appointment Dialog
        apt_dialog = new JDialog();
        apt_dialog.setSize(1000,600);
        apt_dialog.add(pop_up_panel);
        apt_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        apt_dialog.pack();
        apt_dialog.setResizable(false);
        apt_dialog_exit();


        //Call Mouse listener functions---------------------------------------------------------------------------------
        double_click();
        click_and_drag();
        //change_color();
        month_double_click();
        month_click_and_drag();
        month_change_color();
        day_delete_gesture();
        day_delete_all_gesture();
        day_next_gesture();
        day_prev_gesture();
        day_toggle_tag_gesture();
        day_move_apt_gesture();
        month_delete_gesture();
        month_delete_all_gesture();
        month_next_gesture();
        month_prev_gesture();
        month_toggle_tag_gesture();
        month_repeat_apt_gesture();
        month_move_apt_gesture();
        month_color_apt_gesture();
        animate_day_drag();
        animate_month_drag();

    }

    //Controls New Apt dialog exit-----------------------------------------------------------------------------------
    private  void apt_dialog_exit(){
        apt_dialog.addWindowListener(new WindowAdapter() {
            @Override
            //shows a pop-up dialog when a window is about be cancelled-------------------------------------------------
            public void windowClosing(WindowEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        apt_dialog,
                        "Are You sure you want to cancel?",
                        "Discard Changes",
                        JOptionPane.YES_NO_OPTION);
                if (n == 0){
                    status_label.setText("Create event dialog box cancelled.");
                    apt_dialog.dispose();
                }
            }
        });
    }

    // sets the view to month and updates the date label text when the month's radio button is clicked
    public void set_view_to_month(){
        view_type = "month";
        month = day;
        monthView.day = month;
        scrolly.setViewportView(monthView);
    }

    // sets the view to day and updates the date label text when the day's radio button is clicked
    public  void set_view_to_day(){
        view_type = "day_view";
        day = month;
        dayView.day = day;
        scrolly.setViewportView(dayView);
    }

    public BufferedImage makeOffscreenImage (JComponent source) {
        // Create our BufferedImage and get a Graphics object for it
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage offscreenImage = gfxConfig.createCompatibleImage(source.getWidth(), source.getHeight());
        Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();

        // Tell the component to paint itself onto the image
        source.paint(offscreenGraphics);

        // return the image
        return offscreenImage;
    }

    //Creates and returns the 'previous' button
    private JButton getButton_prev() {
        JButton button_prev = new JButton("Prev");
        button_prev.setBounds(60, 180, 80,20);
        // Action listener to update the view's value when the prev button is clicked
        // The status bar is also updated
        button_prev.addActionListener(e -> {
            status_label.setText("Previous Button Clicked, animation triggered");
            if (Objects.equals(view_type, "day_view")){
                if(!dayView.inAnim)
                animate_day("left", 20);
                dayView.repaint();
            }
            else if (Objects.equals(view_type, "month")){
                //only starts animation when there's no current animation
                if(!monthView.inAnim)
                animate_month("left", 20);
                monthView.repaint();
                //set date stuff
            }
        });
        return button_prev;
    }

    //Creates and returns the 'next' button
    private JButton getButton_next() {
        JButton button_next = new JButton("Next");
        button_next.setBounds(150, 180, 80,20);
        // Action listener to update the view's value when the next button is clicked
        // The status bar is also updated
        button_next.addActionListener(e -> {
            status_label.setText("Next Button Clicked, animation triggered");
            if (Objects.equals(view_type, "day_view")){
                if(!dayView.inAnim)
                animate_day("right", 20);
                dayView.repaint();
            }
            else if (Objects.equals(view_type, "month")){
                //only starts animation when there's no current animation
                if(!monthView.inAnim)
                animate_month("right", 20);
                monthView.repaint();
                //set date stuff
            }
        });
        return button_next;
    }

    private void animate_day( String dir, int count){
        //sets all the variables needed for the animation
        //and updates the days accordingly
        BufferedImage image1 = makeOffscreenImage(dayView);
        if(Objects.equals(dir, "left")){
            day = day.minusDays(1);
            dayView.day = day;
            BufferedImage image2 = makeOffscreenImage(dayView);
            dayView.image1 = image1;
            dayView.image2 = image2;
            dayView.counter = 20 - count + 1;
            dayView.inAnim = true;
            dayView.dir = "left";
            Timer timer = new Timer(1000 / 20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dayView.counter>= 20) {
                        ((Timer)e.getSource()).stop();
                        dayView.inAnim = false;
                        dayView.repaint();
                    } else {
                        dayView.counter++;
                        dayView.repaint();
                    }
                }
            });
            timer.start();
            dayView.repaint();
        }
        else{
            day = day.plusDays(1);
            dayView.day = day;
            BufferedImage image2 = makeOffscreenImage(dayView);
            dayView.repaint();
            dayView.image1 = image1;
            dayView.image2 = image2;

            dayView.counter = 20 - count + 1;
            dayView.inAnim = true;
            dayView.dir = "right";
            Timer timer = new Timer(1000 / 20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dayView.counter >= 20) {
                        ((Timer)e.getSource()).stop();
                        dayView.inAnim = false;
                        dayView.repaint();
                    } else {
                        dayView.counter++;
                        dayView.repaint();
                    }
                }
            });
            timer.start();
            dayView.repaint();
        }
        dayView.repaint();
    }

    private void animate_month(String dir, int count){
        //sets all the variables needed for the animation
        //and updates the months accordingly
        BufferedImage image1 = makeOffscreenImage(monthView);
        if(Objects.equals(dir, "left")){
            month = month.minusMonths(1);
            monthView.day = month;
            BufferedImage image2 = makeOffscreenImage(monthView);
            monthView.image1 = image1;
            monthView.image2 = image2;
            monthView.counter = 20 - count + 1;
            monthView.inAnim = true;
            monthView.dir = "left";
            Timer timer = new Timer(1000 / 20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (monthView.counter>= 20) {
                        ((Timer)e.getSource()).stop();
                        monthView.inAnim = false;
                        monthView.repaint();
                    } else {
                        monthView.counter++;
                        monthView.repaint();
                    }
                }
            });
            timer.start();
            monthView.repaint();
        }
        else{
            month = month.plusMonths(1);
            monthView.day = month;
            BufferedImage image2 = makeOffscreenImage(monthView);
            monthView.repaint();
            monthView.image1 = image1;
            monthView.image2 = image2;

            monthView.counter = 20 - count + 1;
            monthView.inAnim = true;
            monthView.dir = "right";
            Timer timer = new Timer(1000 / 20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (monthView.counter >= 20) {
                        ((Timer)e.getSource()).stop();
                        monthView.inAnim = false;
                        monthView.repaint();
                    } else {
                        monthView.counter++;
                        monthView.repaint();
                    }
                }
            });
            timer.start();
            monthView.repaint();
        }
        monthView.repaint();
    }

    private void animate_day_drag(){
        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    //sets the variables need for the drag
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, dayView);
                    BufferedImage image1 = makeOffscreenImage(dayView);
                    if(p.x < 10 && p.y > (dayView.getHeight() - 10)){
                        day = day.minusDays(1);
                        dayView.day = day;
                        BufferedImage image2 = makeOffscreenImage(dayView);
                        dayView.image1 = image1;
                        dayView.image2 = image2;
                        dayView.dir = "left";
                        isDragging = true;
                    }
                    else if(p.x > (dayView.getWidth() - 10) && p.y > (dayView.getHeight() - 10)){
                        day = day.plusDays(1);
                        dayView.day = day;
                        BufferedImage image2 = makeOffscreenImage(dayView);
                        dayView.image1 = image1;
                        dayView.image2 = image2;
                        dayView.dir = "right";
                        isDragging = true;
                    }
                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    //while its being dragged the animdrag variable remains true
                    status_label.setText("Dragging");
                    dayView.animDrag = isDragging;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if (dayView.animDrag){
                    //when the mouse is released, we check the position of the mouse
                    //to know if we have to cancel the update or not
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen(p, dayView);
                        dayView.animDrag = false;
                        isDragging = false;
                        if (Objects.equals(dayView.dir, "right")) {
                            if (p.x >= dayView.getWidth() / 2) {
                                animate_day("left", 20 - p.x / (dayView.getWidth() / 20));
                            } else {
                                day = day.minusDays(1);
                                dayView.day = day;
                                animate_day("right", p.x / (dayView.getWidth() / 20) );
                            }
                        } else {
                            if (p.x >= dayView.getWidth() / 2) {
                                day = day.plusDays(1);
                                dayView.day = day;
                                animate_day("left", 20 - p.x / (dayView.getWidth() / 20));
                            } else {
                                animate_day("right", p.x / (dayView.getWidth() / 20) );
                            }

                        }
                    }
                    dayView.repaint();
                }
            }
        });
    }
    private  void animate_month_drag(){
        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, monthView);
                    BufferedImage image1 = makeOffscreenImage(monthView);

                    if(p.x < 10 && p.y > (monthView.getHeight() - 10)){
                        month = month.minusMonths(1);
                        monthView.day = month;
                        BufferedImage image2 = makeOffscreenImage(monthView);
                        monthView.image1 = image1;
                        monthView.image2 = image2;
                        monthView.dir = "left";
                        isDragging = true;
                    }
                    else if(p.x > (monthView.getWidth() - 10) && p.y > (monthView.getHeight() - 10)){
                        month = month.plusMonths(1);
                        monthView.day = month;
                        BufferedImage image2 = makeOffscreenImage(monthView);
                        monthView.image1 = image1;
                        monthView.image2 = image2;
                        monthView.dir = "right";
                        isDragging = true;
                    }
                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    status_label.setText("Dragging");
                    monthView.animDrag = isDragging;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if (monthView.animDrag){
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen(p, monthView);
                        monthView.animDrag = false;
                        isDragging = false;
                        if (Objects.equals(monthView.dir, "right")) {
                            if (p.x >= monthView.getWidth() / 2) {
                                animate_month("left", 20 - p.x / (monthView.getWidth() / 20));
                            } else {
                                month = month.minusMonths(1);
                                monthView.day = month;
                                animate_month("right", p.x / (monthView.getWidth() / 20) );
                            }
                        } else {
                            if (p.x >= monthView.getWidth() / 2) {
                                month = month.plusMonths(1);
                                monthView.day = month;
                                animate_month("left", 20 - p.x / (monthView.getWidth() / 20));
                            } else {
                                animate_month("right", p.x / (monthView.getWidth() / 20) );
                            }

                        }
                    }
                    monthView.repaint();
                }
            }
        });
    }


    //Creates and returns the 'today' button
    private JButton getButton_today() {
        JButton button_today = new JButton("Today");
        button_today.setBounds(110, 150, 70,20);
        // Action listener to update the view's value when the today button is clicked
        // The status bar is also updated
        button_today.addActionListener(e -> {
            status_label.setText("Today Button Clicked");
            if (Objects.equals(view_type, "day_view")){
                temp_date = LocalDate.now();
                day = temp_date;
                dayView.day = day;
                dayView.repaint();
            }
            else{
                temp_date = LocalDate.now();
                month = temp_date;
                monthView.day = month;
                monthView.repaint();
                //set date stuff
            }

        });
        return button_today;
    }

    //Creates and returns the 'New Appointment' button
    private JButton getButton_new_apt() {
        JButton button_new_apt = new JButton("New Appointment");
        button_new_apt.setBounds(70, 210, 150,20);
        // Action listener to show the pop-up when the 'new appointment' button is clicked
        // The status bar is also updated
        button_new_apt.addActionListener(e -> {
            status_label.setText("New Appointment Dialog");
            EventDetails value = new EventDetails(day, Panels.parse_time_to_double(dayView.time), Panels.parse_time_to_double(dayView.time.plusHours(1))
                    , "New Event", false, false, false, false);
            Panels.set_pop_up(value);
            apt_dialog.setVisible(true);

        });
        return button_new_apt;
    }


    //convert a LocalTime object to a double
    private static double parse_time_to_double(LocalTime t){
        double time_hr = t.getHour();
        double time_min = t.getMinute()/60.0;
        return time_hr + time_min;
    }

    // Receives a double and returns the time equivalent of it
    public static LocalTime parse_double_to_time(double d){
        LocalTime dat = LocalTime.now();
        try{
            double int_part = (int)(d);
            double decimal = d - int_part;
            int min = (int)(decimal*60);
            int hr = (int)(int_part);
            String min_str = String.valueOf(min), hr_str = String.valueOf(hr);
            if(min < 10){
                min_str = "0"+min;
            }
            if(hr < 10){
                hr_str = "0"+hr;
            }
            String date = hr_str+":"+min_str;
            return LocalTime.parse(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dat;
    }
    public static void set_pop_up(EventDetails event){
        // Call pop up check box functions---------------------------------------------------------------------
        // The checkboxes method of the new appointment dialog are called and declared here.
        date_text.setText(String.valueOf(event.date));
        new_apt_text.setText(event.event_desc);
        start_time_text.setText(String.valueOf(Panels.parse_double_to_time(event.start_time)));
        end_time_text.setText(String.valueOf(Panels.parse_double_to_time(event.end_time)));
        vacation_check.setSelected(event.vacation);
        family_check.setSelected(event.family);
        work_check.setSelected(event.work);
        school_check.setSelected(event.school);
    }

    //Check the time provided by the user, if it's not in a valid format, an error message will be popped up
    private static LocalTime check_time_input(String a_time){
        try {
            LocalTime.parse(a_time);
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(
                    apt_dialog,
                    "The correct time format is \"hh:mm\"",
                    "Wrong time value(s)",JOptionPane.ERROR_MESSAGE);
        }
        return LocalTime.parse(a_time);
    }

    //Check the day provided by the user, if it's not in a valid format, an error message will be popped up
    //If the value is valid a dateTime is returned
    private static LocalDate check_date_input(String date){
        try {
            LocalDate.parse(date);
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(
                    apt_dialog,
                    "The correct time format is \"yyyy:mm:dd\"",
                    "Wrong date value",JOptionPane.ERROR_MESSAGE);
        }
        return LocalDate.parse(date);
    }

    //Creates and returns the 'OK' button in the pop-up dialog
    private JButton getButton_ok_pop_up() {
        JButton button_ok_pop_up = new JButton("OK");
        button_ok_pop_up.setBounds(50, 320, 150,20);
        // Action listener to disable the pop-up dialog and update the calendar events
        button_ok_pop_up.addActionListener(e -> {
            //updates status
            status_label.setText("Appointment Created "+ new_apt_text.getText()
                                + ' ' + date_text.getText() + ' ' + start_time_text.getText()
                                + " - " + end_time_text.getText());

            //Setting default values to be shown in the appointment dialog box
            String key = date_text.getText();
            LocalDate real_key = Panels.check_date_input(key);
            LocalTime start_time_date = Panels.check_time_input(start_time_text.getText());
            LocalTime end_time_date = Panels.check_time_input(end_time_text.getText());
            double start_time = Panels.parse_time_to_double(start_time_date);
            double end_time = Panels.parse_time_to_double(end_time_date);
            if(start_time > end_time){
                JOptionPane.showMessageDialog(
                        apt_dialog,
                        "The start time cannot be greater than the end time",
                        "Wrong time value",JOptionPane.ERROR_MESSAGE);
                return;
            }

            //If we are not editing an event, a new one is created and added to the event list
            if(!keep_apt_values){
                EventDetails value = new EventDetails(real_key, start_time,end_time
                        , new_apt_text.getText(), vacation_check.isSelected(),
                        family_check.isSelected(), school_check.isSelected(),
                        work_check.isSelected());
                events.computeIfAbsent(real_key, k -> new ArrayList<>());
                events.get(real_key).add(value);
            }
            //if an event is being edited, the edit event function is called, and the values of the event is set based
            //on users input
            if (keep_apt_values){
                edit_event_values(real_key, start_time, end_time);
            }
            //Repaints component and dispose dialog
            dayView.repaint();
            monthView.repaint();
            apt_dialog.dispose();
        });
        return button_ok_pop_up;
    }

    private void edit_event_values(LocalDate real_key, double start_time, double end_time) {
        // sets the variable according to user input
        ArrayList<EventDetails> current_events  = events.get(aux_event.date);
        current_events.get(current_events.indexOf(aux_event)).start_time = start_time;
        current_events.get(current_events.indexOf(aux_event)).end_time = end_time;
        current_events.get(current_events.indexOf(aux_event)).date = real_key;
        current_events.get(current_events.indexOf(aux_event)).event_desc = new_apt_text.getText();
        current_events.get(current_events.indexOf(aux_event)).vacation = vacation_check.isSelected();
        current_events.get(current_events.indexOf(aux_event)).family = family_check.isSelected();
        current_events.get(current_events.indexOf(aux_event)).school = school_check.isSelected();
        current_events.get(current_events.indexOf(aux_event)).work = work_check.isSelected();
        keep_apt_values = false;
    }

    //Controls double click action
    private void double_click(){
        dayView.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                //Actions only performed if the amount of clicks is 2 and done by the left mouse button
                if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
                    //To adjust point to scroll pane--------------------------------------------------------------------
                    SwingUtilities.convertPointFromScreen(e.getPoint(), dayView);
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;

                    //gets list of all events for the day---------------------------------------------------------------
                    ArrayList<EventDetails> current_events = Panels.events.get(day);
                    if(current_events != null) {
                        //loops through the list to find the event where the mouse point is
                        for (EventDetails elem : current_events) {
                            if(x > 45 && y > elem.start_time*60+45 && y < elem.end_time*60+45 ){
                                //If there's an event in the mouse point, a pop-up dialog is shown
                                //And the values are set to that of the current event
                                keep_apt_values = true;
                                aux_event = elem;
                                Panels.set_pop_up(elem);
                                Panels.apt_dialog.setVisible(true);
                                return;
                            }
                        }
                    }
                    //If there's no event in that position, a new one is created with the below default values
                    //At the point of the event
                    double start_time =  (y -45.0)/60.0;
                    double end_time = start_time + 1.0;
                    EventDetails value = new EventDetails(day, start_time,end_time
                            , "New Event", false, false, false, false);
                    Panels.events.computeIfAbsent(day, k -> new ArrayList<>());
                    Panels.events.get(day).lastIndexOf(value);
                    Panels.set_pop_up(value);
                    Panels.apt_dialog.setVisible(true);
                    dayView.repaint();
                }
            }

        });
    }

    private void month_double_click(){
        monthView.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                //Actions only performed if the amount of clicks is 2 and done by the left mouse button
                if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
                    //To adjust point to scroll pane--------------------------------------------------------------------
                    SwingUtilities.convertPointFromScreen(e.getPoint(), monthView);
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = ((x/(monthView.getWidth()/7)) + 1);
                    int day_num =   row *7 + col - start_int ;
                    int top_line = (row * (monthView.getHeight() - 45)/6);
                    int event_pos = (y -top_line - 45  - 20 ) / 20;

                    if(day_num >= 1 && day_num <= day.lengthOfMonth()){

                        LocalDate this_day = monthView.day.withDayOfMonth(day_num);
                        //gets list of all events for the day---------------------------------------------------------------
                        ArrayList<EventDetails> current_events = Panels.events.get(this_day);
                        if(current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            if (event_pos <= current_events.size()-1){
                                EventDetails elem = current_events.get(event_pos);
                                keep_apt_values = true;
                                aux_event = elem;
                                Panels.set_pop_up(elem);
                                Panels.apt_dialog.setVisible(true);
                                return;
                            }
                        }
                        //If there's no event in that position, a new one is created with the below default values
                        //At the point of the event
                        double start_time =  12.0;
                        double end_time = start_time + 1.0;
                        EventDetails value = new EventDetails(this_day, start_time,end_time
                                , "New Event", false, false, false, false);
                        Panels.events.computeIfAbsent(this_day, k -> new ArrayList<>());
                        Panels.events.get(this_day).lastIndexOf(value);
                        Panels.set_pop_up(value);
                        Panels.apt_dialog.setVisible(true);
                        monthView.repaint();
                    }

                }
            }

        });
    }

    private void month_click_and_drag(){
        //declare useful function variables
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, dayView);
        final EventDetails[] test = new EventDetails[1];
        is_event = false;
        final ArrayList<EventDetails>[] current_events = new ArrayList[1];
        final LocalDate[] this_day = {null};
        final LocalDate[] old_day = new LocalDate[1];

        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isLeftMouseButton(e)){
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;

                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        old_day[0] = this_day[0] = monthView.day.withDayOfMonth(day_num);
                        current_events[0] = Panels.events.get(this_day[0]);
                    }


                    if(current_events[0] != null) {
                        pt = e.getPoint();
                            if(event_pos <= current_events[0].size()-1 ){
                                EventDetails elem = current_events[0].get(event_pos);
                                test[0] = elem;

                                //The is_event variable is set to true, this is to tell that the mouse is currently on
                                // an event
                                is_event = true;
                                return;
                            }
                    }
                    monthView.repaint();
                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isLeftMouseButton(e) && e.getPoint().y > 45 && e.getPoint().y < 1485) {

                    if (is_event){
                        monthView.drag = true;
                        monthView.drawn_event = test[0];
                        monthView.repaint();
                    }

                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isLeftMouseButton(e)) {
                    //after the mouse is released, we reset the values below
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    if(day_num >= 1 && day_num <= day.lengthOfMonth()){
                        LocalDate this_day = monthView.day.withDayOfMonth(day_num);
                        is_created = false;
                        if (is_event && monthView.drag ){
                            current_events[0].get(current_events[0].indexOf(test[0])).date = this_day;
                            Panels.events.computeIfAbsent(this_day, k -> new ArrayList<>());
                            Panels.events.get(this_day).add(test[0]);
                            Panels.events.get(old_day[0]).remove(test[0]);

                        }
                    }
                    is_event = false;
                    monthView.repaint();
                    monthView.drag = false;
                }
            }
        });
    }

    //day gestures------------------------------------------------------------------------
    private  void day_delete_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, dayView);
        final int[] x = new int[1];
        final int[] y = new int[1];

        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;
                    x[0] = e.getX();
                    y[0] = e.getY();

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "delete")){
                        dayView.repaint();
                        return;
                    }
                    ArrayList<EventDetails> current_events = Panels.events.get(day);
                    if(current_events != null) {
                        //loops through the list to find the event where the mouse point is
                        for (EventDetails elem : current_events) {
                            if(x[0] > 45 && y[0] > elem.start_time*60+45 && y[0] < elem.end_time*60+45 ){
                                //If there's an event in the mouse point, a pop-up dialog is shown
                                //And the values are set to that of the current event
                                events.get(day).remove(elem);
                                dayView.repaint();
                                return;
                            }
                        }
                    }
                }
                dayView.repaint();
            }
        });
    }

    private  void day_delete_all_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, dayView);


        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "circle")){
                        dayView.repaint();
                        return;
                    }
                    ArrayList<EventDetails> current_events = Panels.events.get(day);
                    if(current_events != null) {
                        //loops through the list to find the event where the mouse point is
                        current_events.clear();

                    }
                }
                dayView.repaint();
            }
        });
    }

    private  void day_next_gesture(){

        ArrayList<Point2D> point_list = new ArrayList<>();
        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "right square bracket")){
                        dayView.repaint();
                        return;
                    }
                    if (Objects.equals(view_type, "day_view")){
                        if(!dayView.inAnim)
                            animate_day("right", 20);
                        dayView.repaint();
                    }
                }
                dayView.repaint();
            }
        });
    }

    private  void day_prev_gesture(){

        ArrayList<Point2D> point_list = new ArrayList<>();
        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "left square bracket")){
                        dayView.repaint();
                        return;
                    }
                    if (Objects.equals(view_type, "day_view")){
                        if(!dayView.inAnim)
                            animate_day("left", 20);
                        dayView.repaint();
                    }
                }
                dayView.repaint();
            }
        });
    }

    private  void day_toggle_tag_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, dayView);
        final int[] x = new int[1];
        final int[] y = new int[1];

        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;
                    x[0] = e.getX();
                    y[0] = e.getY();

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);

                    if (Objects.equals(result.toString(), "check")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).vacation =
                                            !(current_events.get(current_events.indexOf(elem)).vacation);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }

                    else if (Objects.equals(result.toString(), "x")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).school =
                                            !(current_events.get(current_events.indexOf(elem)).school);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }

                    else if (Objects.equals(result.toString(), "pigtail")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).work =
                                            !(current_events.get(current_events.indexOf(elem)).work);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }

                    else if (Objects.equals(result.toString(), "star")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).family =
                                            !(current_events.get(current_events.indexOf(elem)).family);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }

                }
                dayView.repaint();
            }
        });
    }

    private  void day_move_apt_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, dayView);
        final int[] x = new int[1];
        final int[] y = new int[1];

        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    dayView.gesture = true;
                    x[0] = e.getX();
                    y[0] = e.getY();

                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, dayView);
                    point_list.add(p);
                    dayView.point_list = point_list;
                    dayView.repaint();
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    dayView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (Objects.equals(result.toString(), "caret")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).start_time =
                                            (current_events.get(current_events.indexOf(elem)).start_time - 1.0);
                                    current_events.get(current_events.indexOf(elem)).end_time =
                                            (current_events.get(current_events.indexOf(elem)).end_time - 1.0);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }

                    else if (Objects.equals(result.toString(), "v")) {
                        ArrayList<EventDetails> current_events = Panels.events.get(day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            for (EventDetails elem : current_events) {
                                if (x[0] > 45 && y[0] > elem.start_time * 60 + 45 && y[0] < elem.end_time * 60 + 45) {
                                    //If there's an event in the mouse point, a pop-up dialog is shown
                                    //And the values are set to that of the current event
                                    current_events.get(current_events.indexOf(elem)).start_time =
                                            (current_events.get(current_events.indexOf(elem)).start_time + 1.0);
                                    current_events.get(current_events.indexOf(elem)).end_time =
                                            (current_events.get(current_events.indexOf(elem)).end_time + 1.0);
                                    dayView.repaint();
                                    return;
                                }
                            }
                        }
                    }
                }
                dayView.repaint();
            }
        });
    }

    //month gestures-----------------------------------------------------------------------
    private  void month_delete_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;
                    monthView.fade = false;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){

                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "delete")){
                        monthView.repaint();
                        return;
                    }
                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }

                    if(current_events != null) {
                        //loops through the list to find the event where the mouse point is
                        if(event_pos <= current_events.size()-1 ){
                            current_events.remove(event_pos);
                            monthView.repaint();
                        }
                    }
                    monthView.gesture = false;
                    monthView.fade = true;
                }

            }
        });
    }

    private  void month_delete_all_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "circle")){
                        monthView.repaint();
                        return;
                    }
                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }

                    if(current_events != null) {
                        current_events.clear();
                    }
                }

            }
        });
    }

    private  void month_next_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "right square bracket")){
                        monthView.repaint();
                        return;
                    }
                    if (Objects.equals(view_type, "month")){
                        if(!monthView.inAnim)
                            animate_month("right", 20);
                        monthView.repaint();
                        //set date stuff
                    }
                }

            }
        });
    }

    private  void month_prev_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);
                    if (!Objects.equals(result.toString(), "left square bracket")){
                        monthView.repaint();
                        return;
                    }
                    if (Objects.equals(view_type, "month")){
                        if(!monthView.inAnim)
                            animate_month("left", 20);
                        monthView.repaint();
                        //set date stuff
                    }
                }

            }
        });
    }

    private  void month_toggle_tag_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);

                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }
                    if (Objects.equals(result.toString(), "check")){
                        if(current_events != null) {
                            if(event_pos <= current_events.size()-1 ){
                                current_events.get(event_pos).vacation = !(current_events.get(event_pos).vacation);
                                monthView.repaint();
                            }
                        }
                    }
                    else  if(Objects.equals(result.toString(), "x")){
                        if(current_events != null) {
                            if(event_pos <= current_events.size()-1 ){
                                current_events.get(event_pos).school = !(current_events.get(event_pos).school);
                                monthView.repaint();
                            }
                        }
                    }
                    else  if(Objects.equals(result.toString(), "pigtail")){
                        if(current_events != null) {
                            if(event_pos <= current_events.size()-1 ){
                                current_events.get(event_pos).work = !(current_events.get(event_pos).work);
                                monthView.repaint();
                            }
                        }
                    }
                    else  if(Objects.equals(result.toString(), "star")){
                        if(current_events != null) {
                            if(event_pos <= current_events.size()-1 ){
                                current_events.get(event_pos).family = !(current_events.get(event_pos).family);
                                monthView.repaint();
                            }
                        }
                    }

                }

            }
        });
    }

    private  void month_repeat_apt_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);

                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }
                    if (current_events == null || event_pos > current_events.size()-1 ){
                        return;
                    }
                    if (Objects.equals(result.toString(), "zig-zag")){
                        int deltaX = Math.abs((int)(point_list.get(0).getX() - point_list.get(point_list.size()-1).getX()));
                        int deltaY = Math.abs((int)(point_list.get(0).getY() - point_list.get(point_list.size()-1).getY()));
                        EventDetails elem = current_events.get(event_pos);
                        if (deltaX >= deltaY){
                            int diff = monthView.days.lastIndexOf(elem.date.getDayOfWeek().toString())-1;
                            int day1 = day_num-diff;
                            for(int i = 0; i < 5; i++){
                                if (day1 != day_num && day1 > 0 && day1 <= monthView.day.lengthOfMonth()){
                                    LocalDate this_day = monthView.day.withDayOfMonth(day1);
                                    Panels.events.computeIfAbsent(this_day, k -> new ArrayList<>());
                                    EventDetails value = new EventDetails(this_day, elem.start_time,elem.end_time
                                            , elem.event_desc, elem.vacation, elem.family, elem.school, elem.work);
                                    Panels.events.get(this_day).add(value);
                                }
                                day1++;
                            }
                            System.out.println(diff);
                        }
                        else{
                            String weekday = elem.date.getDayOfWeek().toString();
                            int day1 = (monthView.days.lastIndexOf(weekday)-start_int)+1;
                            System.out.println(day1+" start int  "+start_int);
                            System.out.println(weekday);
                            for(int i = 0; i < 7; i++){
                                if (day1 != day_num && day1 > 0 && day1 <= monthView.day.lengthOfMonth()){
                                    LocalDate this_day = monthView.day.withDayOfMonth(day1);
                                    Panels.events.computeIfAbsent(this_day, k -> new ArrayList<>());
                                    EventDetails value = new EventDetails(this_day, elem.start_time,elem.end_time
                                            , elem.event_desc, elem.vacation, elem.family, elem.school, elem.work);
                                    Panels.events.get(this_day).add(value);
                                }
                                day1+=7;
                            }
                        }
                        monthView.repaint();
                    }


                }

            }
        });
    }

    private  void month_move_apt_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);

                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }
                    if (current_events == null || event_pos > current_events.size()-1 ){
                        return;
                    }
                    int day1;
                    if (Objects.equals(result.toString(), "caret")) {
                         day1 = day_num - 7;
                    }
                    else if (Objects.equals(result.toString(), "v"))  {
                         day1 = day_num+7;
                        }
                    else {
                        return;
                    }
                        EventDetails elem = current_events.get(event_pos);
                        if (  day1 > 0 && day1 <= monthView.day.lengthOfMonth()){
                            LocalDate this_day = monthView.day.withDayOfMonth(day1);
                            Panels.events.computeIfAbsent(this_day, k -> new ArrayList<>());
                            EventDetails value = new EventDetails(this_day, elem.start_time,elem.end_time
                                    , elem.event_desc, elem.vacation, elem.family, elem.school, elem.work);
                            Panels.events.get(this_day).add(value);
                            LocalDate old_day = monthView.day.withDayOfMonth(day_num);
                            Panels.events.get(old_day).remove(elem);
                        }
                        monthView.repaint();


                }
            }
        });
    }

    private  void month_color_apt_gesture(){
        Point p = MouseInfo.getPointerInfo().getLocation();
        ArrayList<Point2D> point_list = new ArrayList<>();
        SwingUtilities.convertPointFromScreen(p, monthView);


        monthView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isRightMouseButton(e)){
                    point_list.clear();
                    monthView.gesture = true;

                }
            }
        });


        monthView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    Point2D p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen((Point) p, monthView);
                    point_list.add(p);
                    monthView.point_list = point_list;
                    monthView.repaint();
                }
            }
        });

        monthView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    monthView.gesture = false;
                    if(point_list.size() == 0){
                        return;
                    }
                    Result result = dollar.recognize(point_list);
                    String label =result.toString() +"  "+result.getScore();
                    status_label.setText(label);

                    int x = (int) point_list.get(0).getX();
                    int y = (int) point_list.get(0).getY();
                    LocalDate first_day = monthView.day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = ((y-45)/((monthView.getHeight() - 45)/6));
                    int col = (x/(monthView.getWidth()/7)) + 1;
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    ArrayList<EventDetails> current_events = Panels.events.get(monthView.day);
                    if(day_num >= 1 && day_num <= monthView.day.lengthOfMonth()){
                        current_events = Panels.events.get(monthView.day.withDayOfMonth(day_num));
                    }
                    if (current_events == null || event_pos > current_events.size()-1 ){
                        return;
                    }
                    if (Objects.equals(result.toString(), "triangle")){
                        Color my_color = null;
                        String[] colors = {"red", "green", "blue", "cyan"};
                        String color = (String) JOptionPane.showInputDialog(null,
                                "Choose Color", "Input",
                                JOptionPane.INFORMATION_MESSAGE, null,
                                colors, colors[0]);
                        if (Objects.equals(color, "red")) {
                            my_color = Color.red;
                        } else if (Objects.equals(color, "blue")) {
                            my_color = Color.blue;
                        } else if (Objects.equals(color, "green")) {
                            my_color = Color.green;
                        } else if (Objects.equals(color, "cyan")) {
                            my_color = Color.cyan;
                        }

                            for(EventDetails elem: current_events){
                                elem.color = my_color;

                            }



                        monthView.repaint();
                    }


                }

            }
        });
    }


    private void month_change_color() {
        //Added functionality to change event color
        monthView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                aux_event1 = null;
                //If the right mouse button is clicked on an event, a dialog is shown asking the user to choose a color
                if (SwingUtilities.isRightMouseButton(e)) {
                    SwingUtilities.convertPointFromScreen(e.getPoint(), monthView);
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;
                    LocalDate first_day = day.withDayOfMonth(1);
                    String start_day_str = String.valueOf(first_day.getDayOfWeek());
                    int start_int = monthView.days.indexOf(start_day_str);
                    int row = (int) Math.floor((y-45)/Math.floor((monthView.getHeight() - 45.0)/6.0));
                    int col = (int) (Math.floor(x/Math.floor(monthView.getWidth()/7.0)) + 1.0);
                    int day_num =   row *7 + col - start_int ;
                    int top_line = row * (monthView.getHeight() - 45)/6;
                    int event_pos = (y -top_line - 45  - 20 ) / 20;
                    if(day_num >= 1 && day_num <= day.lengthOfMonth()) {
                        LocalDate this_day = day.withDayOfMonth(day_num);
                        //gets list of all events for the day---------------------------------------------------------------
                        ArrayList<EventDetails> current_events = Panels.events.get(this_day);
                        if (current_events != null) {
                            //loops through the list to find the event where the mouse point is
                            if (event_pos <= current_events.size() - 1) {
                                aux_event1 = current_events.get(event_pos);
                            }//When the user chooses a color, the color variable of the current event is changed and
                            // the repaint() function is called
                            if (aux_event1 != null) {
                                //System.out.println(current_events);
                                String[] colors = {"red", "green", "blue", "cyan"};
                                String color = (String) JOptionPane.showInputDialog(null,
                                        "Choose Color", "Input",
                                        JOptionPane.INFORMATION_MESSAGE, null,
                                        colors, colors[0]);
                                if (Objects.equals(color, "red")) {
                                    aux_event1.color = Color.red;
                                } else if (Objects.equals(color, "blue")) {
                                    aux_event1.color = Color.blue;
                                } else if (Objects.equals(color, "green")) {
                                    aux_event1.color = Color.green;
                                } else if (Objects.equals(color, "cyan")) {
                                    aux_event1.color = Color.cyan;
                                }
                            }

                        }monthView.repaint();
                    }

                }
            }
        });
    }
    private void change_color(){
        //Added functionality to change event color
        dayView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                aux_event1 = null;
                //If the right mouse button is clicked on an event, a dialog is shown asking the user to choose a color
                if (SwingUtilities.isRightMouseButton(e)) {
                    ArrayList<EventDetails> current_events = Panels.events.get(day);
                    if (current_events != null) {
                        for (EventDetails elem : current_events) {
                            if (e.getPoint().x > 45 && e.getPoint().y > elem.start_time * 60 +
                                    45 && e.getPoint().y < elem.end_time * 60 + 45) {
                                //Panels.events.get(day).lastIndexOf(elem);
                                aux_event1 = elem;
                                break;
                            }
                        }
                        //When the user chooses a color, the color variable of the current event is changed and
                        // the repaint() function is called
                        if(aux_event1 != null){
                            System.out.println(current_events);
                            String[] colors = {"red", "green", "blue", "cyan"};
                            String color = (String) JOptionPane.showInputDialog(null,
                                    "Choose Color", "Input",
                                    JOptionPane.INFORMATION_MESSAGE, null,
                                    colors, colors[0]);
                            if (Objects.equals(color, "red")){
                                aux_event1.color = Color.red;
                            }
                            else if(Objects.equals(color, "blue")){
                                aux_event1.color = Color.blue;
                            }
                            else if(Objects.equals(color, "green")){
                                aux_event1.color = Color.green;
                            }
                            else if(Objects.equals(color, "cyan")){
                                aux_event1.color = Color.cyan;
                            }
                        }

                    }dayView.repaint();
                }
            }
        });
    }

    //Method in charge of click and drag functionality
    private void click_and_drag(){
        //declare useful function variables
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, dayView);
        final double[] st = new double[1];
        final double[] end = new double[1];
        final EventDetails[] test = new EventDetails[1];
        is_event = false;
        final EventDetails[] value = new EventDetails[1];


        dayView.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                //if the left mouse button is pressed the event in that position is stored in a 1 element array
                if(SwingUtilities.isLeftMouseButton(e)){
                    ArrayList<EventDetails> current_events = Panels.events.get(day);
                    if(current_events != null) {
                        pt = e.getPoint();
                        for (EventDetails elem : current_events) {
                            if(pt.x > 15 && pt.y > elem.start_time*60+45 && pt.y < elem.end_time*60+45 ){
                                test[0] = elem;
                                //The is_event variable is set to true, this is to tell that the mouse is currently on
                                // an event
                                is_event = true;
                                return;
                            }
                        }
                    }
                    dayView.repaint();
                }
            }
        });


        dayView.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isLeftMouseButton(e) && e.getPoint().y > 45 && e.getPoint().y < 1485) {
                    ArrayList<EventDetails> current_events = Panels.events.get(day);

                    //If the mouse position was not pressed on an event, a new event will be created
                    if (!is_event) {
                        //Checks if an event as already been created , so that multiple events are not creates as the
                        // mouse is being dragged
                        if (!is_created) {
                            pt = e.getPoint();
                            Point p_aux = e.getPoint();

                            //the start time will be set to the original point
                            // The end time is updated as the screen is dragged
                            st[0] = (p_aux.y - 45.0) / 60.0;
                            end[0] = (pt.y - 45.0) / 60.0;
                            value[0] = new EventDetails(day, st[0], end[0]
                                    , "New Event", false, false, false, false);
                            Panels.events.computeIfAbsent(dayView.day, k -> new ArrayList<>());
                            Panels.events.get(dayView.day).add(value[0]);
                            is_created = true;
                            dayView.repaint();
                        } else {
                            //If the event has already been created, the end time is just set to the mouse point
                            pt = e.getPoint();
                            double start = st[0];
                            double end = pt.y;
                            end = (end - 45.0) / 60.0;
                            //If the start is greater than the end , the values are swapped
                            if (start > end){
                                double temp = start;
                                start = end;
                                end = temp;
                            }
                            current_events.get(current_events.indexOf(value[0])).start_time = start;
                            current_events.get(current_events.indexOf(value[0])).end_time = end ;
                            dayView.repaint();
                        }
                    }

                    //If the mouse was pressed on an event, we drag the event, basically updates the start and end time
                    // of the event in real time.
                    else if(current_events != null) {

                            pt = e.getPoint();
                            double start_time = (pt.y - 45.0) / 60.0;
                            double duration = current_events.get(current_events.indexOf(test[0])).end_time -
                                    current_events.get(current_events.indexOf(test[0])).start_time;
                            current_events.get(current_events.indexOf(test[0])).start_time = start_time;
                            current_events.get(current_events.indexOf(test[0])).end_time = start_time + duration;
                            dayView.repaint();


                    }
                }
            }
        });

        dayView.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isLeftMouseButton(e)) {
                    //after the mouse is released, we reset the values below
                    is_event = false;
                    is_created = false;
                }
            }
        });
    }


    //Creates and returns the 'OK' button in the pop-up dialog
    private JButton getButton_cancel_pop_up() {
        JButton button_cancel_pop_up = new JButton("Cancel");
        button_cancel_pop_up.setBounds(250, 320, 150,20);
        // Action listener to disable the pop-up dialog
        button_cancel_pop_up.addActionListener(e -> {

            int n = JOptionPane.showConfirmDialog(
                    apt_dialog,
                    "Are You sure you want to cancel?",
                    "Discard Changes",
                    JOptionPane.YES_NO_OPTION);
            if (n == 0){
                apt_dialog.dispose();
                status_label.setText("Create event dialog box cancelled.");
            }
        });
        return button_cancel_pop_up;
    }


    // Creates and returns the checkbox for vacation
    private static JCheckBox getCheck_vacation() {
        JCheckBox vacation_check = new JCheckBox("Vacation");
        vacation_check.setBounds(50, 220, 150,20);
        vacation_check.addActionListener(e -> {
            if (vacation_check.isSelected()) {
                status_label.setText("vacation checked");
            }
            else{
                status_label.setText("vacation unchecked");
            }
        });
        return vacation_check;
    }

    private static JCheckBox getCheck_family() {
        JCheckBox family_check = new JCheckBox("Family");
        family_check.setBounds(50, 240, 150,20);
        family_check.addActionListener(e -> {
            if (family_check.isSelected()) {
                status_label.setText("Family checked");
            }
            else{
                status_label.setText("Family unchecked");
            }
        });
        return family_check;
    }

    private static JCheckBox getCheck_school() {
        JCheckBox school_check = new JCheckBox("School");
        school_check.setBounds(50, 260, 150,20);
        school_check.addActionListener(e -> {
            if (school_check.isSelected()) {
                status_label.setText("School checked");
            }
            else{
                status_label.setText("School unchecked");
            }
        });
        return school_check;
    }

    private static JCheckBox getCheck_work() {
        JCheckBox work_check = new JCheckBox("Work");
        work_check.setBounds(50, 280, 150,20);
        work_check.addActionListener(e -> {
            if (work_check.isSelected()) {
                status_label.setText("Work checked");
            }
            else{
                status_label.setText("Work unchecked");
            }
        });
        return work_check;
    }
}
