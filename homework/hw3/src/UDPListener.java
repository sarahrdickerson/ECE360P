import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UDPListener extends Thread{
    LinkedHashMap<String, Integer> inventory;
    HashMap<String, List<Loan>> users;
    int port;

    public UDPListener(LinkedHashMap<String, Integer> inventory, HashMap<String, List<Loan>> users, int port){
        this.inventory = inventory;
        this.users = users;
        this.port = port;
    }

    // Have UDP connections running on separate threads
    public void run(){
    }
}
