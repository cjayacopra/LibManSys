package libManSys;

import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        // Launch the Login frame on the Event Dispatch Thread (EDT)
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}