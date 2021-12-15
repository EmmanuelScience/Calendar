public class Calendar {
    public Calendar(){
        // Calls the Main frame
        new CalendarFrame();
    }

    //Starts the app
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Calendar app = new Calendar();
        });
    }
}
