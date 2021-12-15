

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MonthView extends JComponent {
    public LocalDate day;

    public   List<String> days = Arrays.asList("SUNDAY", "MONDAY","TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY");
    public boolean drag = false;
    public EventDetails drawn_event = null;
    public Boolean gesture = false;
    public Boolean fade = false;
    public ArrayList<Point2D> point_list = new ArrayList<>();
    public  boolean inAnim;
    public BufferedImage image1;
    public BufferedImage image2;
    public int counter;
    public String dir;
    public boolean animDrag;

    MonthView(){
        super();
        //Sets the sizes of the DayView component
        this.setPreferredSize(new Dimension(500, 500));
        this.setSize(500, 800);

    }

    public void paintComponent(Graphics g) {
        //--------------------Sets paint component stuff----------------------------------------------------------
        super.paintComponent(g);
        //Sets the background color of the DayView Component
        if (inAnim){
            animate(g);
        }
        else if(animDrag){
            animate_drag(g);
        }
        else{
            g.setColor(new Color(200,200,200));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            //Sets the font size and font color of the string to be printed at the top of the frame
            int fontSize = 20;
            g.setColor(Color.gray);
            Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
            g.setFont(f);

            //Draws the string at the top of the screen
            g.drawString(get_full_month(day),((CalendarFrame.width-450)/2),20);

            //Draws vertical line
            //g.drawLine(45, 0, 45, getHeight());

            //---------------------------------------------------------------------------------------------------
            //calls important functions
            drawTimeAndLines(g);
            update_apt(g);
            update_drag(g);
            paint_stroke(g);
        }

    }

    private void animate(Graphics g){
        //If there's an animation, we carefully choose the position of the images we want to draw
        //using the value of the counter
        if(Objects.equals(dir, "left")){
            BufferedImage portion1 = image1.getSubimage(image1.getWidth() - 1, 0, 1 , image1.getHeight());
            if(((image1.getWidth()/20) * counter) < image1.getWidth()){
                portion1 = image1.getSubimage( (image1.getWidth()/20) * counter, 0, image1.getWidth() -( (image1.getWidth()/20) * counter) , image1.getHeight());
            }

            g.drawImage(portion1,(getWidth()/20) * counter , 0, this);
            g.setColor(Color.gray);
            g.fillRect((getWidth()/20) * counter, 0, 50, getHeight());
            BufferedImage portion2 = image2.getSubimage(0, 0, image2.getWidth(), image2.getHeight());
            if((image2.getWidth()/20) * counter < image2.getWidth()){
                portion2 = image2.getSubimage(0, 0, (image2.getWidth()/20) * counter, image2.getHeight());
            }

            g.drawImage(portion2,0, 0, this);
        }
        else{
            BufferedImage portion1 = image1.getSubimage( 0, 0, 1 , image1.getHeight());
            if((image1.getWidth() -((image1.getWidth()/20) * counter)) > 0){
                portion1 = image1.getSubimage( 0, 0, image1.getWidth() -((image1.getWidth()/20) * counter) , image1.getHeight());
            }
            g.drawImage(portion1,0, 0, this);
            g.setColor(Color.gray);
            BufferedImage portion2 = image2.getSubimage(0, 0, image2.getWidth(), image2.getHeight());
            if((image2.getWidth()- (image2.getWidth()/20) * counter) > 0){
                portion2 = image2.getSubimage((image2.getWidth()- (image2.getWidth()/20) * counter), 0, (image2.getWidth() -((image2.getWidth()- (image2.getWidth()/20) * counter))) , image2.getHeight());
            }
            g.drawImage(portion2,(image2.getWidth()- (image2.getWidth()/20) * counter), 0, this);
            g.fillRect(getWidth()- (getWidth()/20) * counter , 0, 50, getHeight());

        }

    }

    private void animate_drag(Graphics g){
        //the mouse position is used to set the portions of the images
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);
        if(Objects.equals(dir, "left")){
            BufferedImage portion1 = image1.getSubimage( image1.getWidth()-1 , 0, 1 , image1.getHeight());
            if(p.x < image1.getWidth()){
                portion1 = image1.getSubimage( p.x , 0, image1.getWidth() - (p.x ) , image1.getHeight());
            }

            g.drawImage(portion1,p.x , 0, this);
            g.setColor(Color.gray);
            g.fillRect(p.x, 0, 50, getHeight());
            BufferedImage portion2 = image2.getSubimage(0, 0, image2.getWidth(), image2.getHeight());
            if(p.x < image2.getWidth()){
                portion2 = image2.getSubimage(0, 0, p.x, image2.getHeight());
            }

            g.drawImage(portion2,0, 0, this);



        }
        else if(Objects.equals(dir, "right")){
            BufferedImage portion1 = image1.getSubimage( 0, 0, 1 , image1.getHeight());
            if(p.x > 0){
                portion1 = image1.getSubimage( 0, 0, p.x , image1.getHeight());
            }

            g.drawImage(portion1,0, 0, this);
            g.setColor(Color.gray);
            BufferedImage portion2 = image2.getSubimage(1 , 0, image2.getWidth() -1 , image2.getHeight());
            if(p.x > 0){
                portion2 = image2.getSubimage(p.x , 0, image2.getWidth() -(p.x) , image2.getHeight());
            }
            else{
                p.x = 1;
            }
            g.drawImage(portion2,p.x, 0, this);
            g.fillRect(p.x , 0, 50, getHeight());
        }
    }
    private String get_full_month(LocalDate day){
        //receives a day as an input and returns  a string
        String value;
        value = ( day.getMonth() +
                 ", " + day.getYear());
        return value;
    }


    private void paint_stroke(Graphics g){
        if (gesture){
            Point p = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(p, this);
            for(int i = 0; i < point_list.size()-1; i++){
                g.setColor(new Color(178 -i, 30+i, 30+i));
                g.drawLine((int) point_list.get(i).getX(),(int) point_list.get(i).getY(),(int) point_list.get(i+1).getX(),(int) point_list.get(i+1).getY() );
            }
        }
    }
    private void drawTimeAndLines(Graphics g){
        // Sets color and font size of the "time"
        g.setColor(Color.gray);
        int fontSize = 10;
        Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
        g.setFont(f);

        //Sets variables used for painting the boxes
        int i;
        LocalDate first_day = day.withDayOfMonth(1);
        String start_day_str = String.valueOf(first_day.getDayOfWeek());
        int start_int = days.indexOf(start_day_str);
        LocalDate prev_month = day.minusMonths(1);
        int prev_num = prev_month.lengthOfMonth() - start_int+1;
        int next_num = 1;

        //iterates through the squares and colors or right on them
        int day_num = 1;
        for (i = 0; i < 6; i++){
            for (int j = 0; j < 7; j++){
                if(day_num == LocalDate.now().getDayOfMonth()){
                    if(Objects.equals(day, LocalDate.now())){
                        g.setColor(new Color(187, 200, 245));
                        g.fillOval((this.getWidth()/7)/2 -10 +(this.getWidth()/7) * j, 45+((this.getHeight()-45)/6 )*i , 30, 30);
                    }

                }
                if (j < start_int){
                    g.setColor(new Color(193, 218, 241));
                    g.fillRect((this.getWidth()/7) *j, 45, this.getWidth()/7 , ((this.getHeight()-45)/6 ));
                    g.setColor(Color.gray);
                    g.drawString(String.valueOf(prev_num),(this.getWidth()/7)/2 +(this.getWidth()/7) * j, 60);
                    prev_num++;
                }
                else if(day_num > day.lengthOfMonth()){
                    g.setColor(new Color(193, 218, 241));
                    g.fillRect((this.getWidth()/7) *j, ((this.getHeight()-45)/6)*i +45 , this.getWidth()/7, ((this.getHeight()-45)/6 ));
                    g.setColor(Color.gray);
                    g.drawString(String.valueOf(next_num),(this.getWidth()/7)/2 +(this.getWidth()/7) * j,60+((this.getHeight()-45)/6 )*i);
                    next_num++;
                }
                else{
                    g.setColor(Color.gray);
                    g.drawString(String.valueOf(day_num),(this.getWidth()/7)/2 +(this.getWidth()/7) * j,60+((this.getHeight()-45)/6 )*i);
                    day_num++;
                }

            }
            start_int = 0;
        }
        g.setColor(Color.gray);
        //draw horizontal lines
        for(i = 0; i < 7; i++){
            //g.drawString((i) + ":00",10,50 + 60*i);
            g.drawLine(0, 45 + ((this.getHeight()-45)/6 )*i, this.getWidth(), 45+((this.getHeight()-45)/6 )*i);
        }
        //draw vertical lines
        for(i = 0; i < 8; i++){
            if (i < 7)
            {g.drawString(days.get(i),20 +(this.getWidth()/7) * i,40);}
            g.drawLine((this.getWidth()/7) * i, 45,  (this.getWidth()/7) * i, this.getHeight());
        }
    }

    private void update_drag(Graphics g){
        //when an event is being dragged, it's continuously drawn in the mouse position
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);
        if (this.drag){
            g.setColor(drawn_event.color);
            g.fillRoundRect(p.x, p.y, (this.getWidth()/7)-5 , 15, 5, 5);

            String final_str = drawn_event.event_desc;
            int rect_size = (this.getWidth()/7)-5;
            int str_size = drawn_event.event_desc.length();

            //sets font size, color and type and draws it
            g.setColor(Color.black);
            int fontSize = 10;
            Font f = new Font("Comic Sans MS", Font.PLAIN, fontSize);
            g.setFont(f);
            if (((str_size-10)*5 +65) > rect_size) {
                final_str = final_str.substring(0, ((rect_size - 65)/5 +10)-3) +"...";
            }
            g.drawString(final_str,p.x, p.y + 10 );

        }
    }
    private void update_apt(Graphics g) {
        //This function iterates through the list of events of day and draws them on the screen
        //Creates a list that contains all the events for that day
        //This function loops through the days of the months, and renders all the events for each day
        int i;
        LocalDate first_day = day.withDayOfMonth(1);
        String start_day_str = String.valueOf(first_day.getDayOfWeek());
        int start_int = days.indexOf(start_day_str);
        int day_num = 1;
        for (i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (j >= start_int && day_num <= day.lengthOfMonth()) {
                    ArrayList<EventDetails> current_events = Panels.events.get(day.withDayOfMonth(day_num));
                    g.setColor(Color.gray);
                    if (current_events != null) {
                        int loop = current_events.size();
                        if (loop > 3){
                            loop =  3 + ((this.getHeight()/6-45) - 35)/ 20 ;
                            if (loop > current_events.size()){
                                loop = current_events.size();
                            }
                        }

                        //prints all events
                        for (int index =0; index < loop; index++) {
                            //coordinates for the rectangle
                            int x = (this.getWidth()/7) *j;
                            int y = ((this.getHeight()-45)/6)* i;

                            //Sets rectangles' color and draws them
                            g.setColor(current_events.get(index).color);
                            g.fillRoundRect(x+3, 65 +y + (index * 20), (this.getWidth()/7)-5 , 15, 5, 5);

                            //calculates size of string that can be printed
                            EventDetails curr_event= current_events.get(index);
                            LocalTime st = Panels.parse_double_to_time(curr_event.start_time);
                            String final_str = st  +" " + curr_event.event_desc;
                            int rect_size = (this.getWidth()/7)-5;
                            int str_size = current_events.get(index).event_desc.length();


                            //sets font size, color and type and draws it
                            g.setColor(Color.black);
                            int fontSize = 10;
                            Font f = new Font("Comic Sans MS", Font.PLAIN, fontSize);
                            g.setFont(f);
                            if (((str_size-10)*5 +88) > rect_size) {
                                final_str = final_str.substring(0, ((rect_size - 65)/5 +10)-3) +"...";
                            }
                            g.drawString(final_str, x + 10, y + 75 + (index * 20));

                        }
                    }
                    day_num++;
                }
            }
            start_int = 0;
        }
    }
}

