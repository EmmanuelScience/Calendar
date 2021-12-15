import java.awt.*;
import java.time.LocalDate;

public class EventDetails {
    public LocalDate date;
    public  double start_time;
    public  double end_time;
    public String event_desc;
    public Boolean vacation;
    public Boolean family;
    public Boolean school;
    public Boolean work;
    public Color color  = (new Color(150, 200, 200));

    EventDetails(LocalDate a_date, double a_start_time, double a_end_time, String a_tag, Boolean aVacation,
                 Boolean aFamily, Boolean aSchool, Boolean aWork){
        this.date = a_date;
        this.start_time = a_start_time;
        this.end_time = a_end_time;
        this.event_desc = a_tag;
        this.vacation = aVacation;
        this.family = aFamily;
        this.school = aSchool;
        this.work = aWork;
    }


}
