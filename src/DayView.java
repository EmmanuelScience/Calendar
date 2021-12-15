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

public class DayView extends JComponent {
    //date variable for the date component
    public LocalDate day = LocalDate.now();
    public LocalTime time = LocalTime.now();
    public Boolean gesture = false;
    public ArrayList<Point2D> point_list = new ArrayList<>();
    public  boolean inAnim;
    public BufferedImage image1;
    public BufferedImage image2;
    public int counter;
    public String dir;
    public boolean animDrag;

    DayView(){
        super();
        //Sets the sizes of the DayView component
        this.setPreferredSize(new Dimension(500, 500));
        this.setSize(500, 800);
    }


    public void paintComponent(Graphics g) {
        //--------------------Sets paint component stuff----------------------------------------------------------
        super.paintComponent(g);
        if (inAnim){
            animate(g);
        }
        else if(animDrag){
            animate_drag(g);
        }
        else{
            //Sets the background color of the DayView Component
            g.setColor(new Color(200,200,200));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            //Sets the font size and font color of the string to be printed at the top of the frame
            int fontSize = 20;
            g.setColor(Color.gray);
            Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
            g.setFont(f);

            //Draws the string at the top of the screen
            g.drawString(get_full_day(day),100,20);

            //Draws vertical line
            g.drawLine(45, 0, 45, getHeight());

            //---------------------------------------------------------------------------------------------------
            drawTimeAndLines(g);
            update_apt(g);
            current_line(g);
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

    private String get_full_day(LocalDate day){
        //receives a day as an input and returns  a string
        String value;
        value = (day.getDayOfWeek() + ", " + day.getMonth() + " "
                + day.getDayOfMonth() + ", " + day.getYear());
        return value;
    }

    private void paint_stroke(Graphics g){
        if (gesture){
            Point p = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(p, this);
            g.setColor(new Color(178, 30, 30));
            for(int i = 0; i < point_list.size()-1; i++){
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

        //Iterates through the time of a day, draw the time and a line for each time
        int i;

        for(i = 0; i < 24; i++){
            g.drawString((i) + ":00",10,50 + 60*i);
            g.drawLine(40, 45+ 60*i, CalendarFrame.width, 45+60*i);
        }
    }

    private void update_apt(Graphics g){
        //This function iterates through the list of events of day and draws them on the screen
        //Creates a list that contains all the events for that day

        ArrayList<EventDetails> current_events = Panels.events.get(day);
        if(current_events != null) {
            for (EventDetails elem : current_events) {
                //Gets the duration of each event
                //this variable will be used to calculate the rectangle size
                double duration =  (elem.end_time - elem.start_time);
                //Makes minimum duration 10 minutes
                if (duration < 0.1667){
                    elem.end_time = elem.start_time + 0.1667;
                    duration = 0.1667;
                }

                //coordinates for the rectangle
                int x = 45;
                int y = (int) (45 + 60 * elem.start_time);

                //Sets rectangles' color and draws them
                g.setColor(elem.color);
                g.fillRoundRect(x, y, 500, (int)(60 * duration), 30,30);

                //sets font size, color and type and draws it
                g.setColor(Color.black);

                if (duration < 0.30){
                    int fontSize = 10;
                    Font f = new Font("Comic Sans MS",Font.PLAIN, fontSize);
                    g.setFont(f);
                    g.drawString(elem.event_desc, x+5, y+10);
                    g.drawString((Panels.parse_double_to_time(elem.start_time))+" - " +
                            (Panels.parse_double_to_time(elem.end_time)),x+60, y+10 );
                }
                //set string values
                else if (0.30 < duration && duration < 0.60){
                    int fontSize = 12;
                    Font f = new Font("Comic Sans MS",Font.PLAIN, fontSize);
                    g.setFont(f);
                    g.drawString(elem.event_desc, x+5, y+10);
                    g.drawString((Panels.parse_double_to_time(elem.start_time))+" - " +
                            (Panels.parse_double_to_time(elem.end_time)),x+5, y+20 );
                    int xp = x + 100;
                    if (elem.family){
                        xp += 50;
                        g.drawString("family",xp, y+15 );
                    }
                    if (elem.vacation){
                        xp += 50;
                        g.drawString("vacation",xp, y+15 );
                    }
                    if (elem.work){
                        xp += 50;
                        g.drawString("work",xp, y+15 );
                    }
                    if (elem.school){
                        xp += 50;
                        g.drawString("school",xp, y+15 );
                    }
                }
                else{
                    int fontSize = 12;
                    Font f = new Font("Comic Sans MS",Font.PLAIN, fontSize);
                    g.setFont(f);
                    g.drawString(elem.event_desc, x+5, y+10);
                    g.drawString((Panels.parse_double_to_time(elem.start_time))+" - " +
                            (Panels.parse_double_to_time(elem.end_time)),x+5, y+25 );
                    int xp = 0 ;
                    if (elem.family){
                        xp += 50;
                        g.drawString("family",xp, y+40 );
                    }
                    if (elem.vacation){
                        xp += 50;
                        g.drawString("vacation",xp, y+40 );
                    }
                    if (elem.work){
                        xp += 50;
                        g.drawString("work",xp, y+40 );
                    }
                    if (elem.school){
                        xp += 50;
                        g.drawString("school",xp, y+40 );
                    }

                }

            }
        }
    }

    private void current_line(Graphics g){
        time = LocalTime.now();
        double hour = time.getHour();
        double min = time.getMinute();
        min = min/60;
        hour = hour + min;
        g.setColor(Color.red);
        g.drawLine(45,(int)(45 + 60 * hour), 540, (int)(45 + 60 * hour) );
        g.fillOval(45,(int)(40 + 60 * hour), 10,10);

    }

}
