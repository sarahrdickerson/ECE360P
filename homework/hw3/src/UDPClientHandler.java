import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class UDPClientHandler extends Thread{
    DatagramSocket client;
    LinkedHashMap<String, Integer> inventory;
    HashMap<String, List<Loan>> users;
    int[] nextId;

    public UDPClientHandler(DatagramSocket client, LinkedHashMap<String, Integer> inventory, HashMap<String, List<Loan>> users, int[] nextId){
        this.client = client;
        this.inventory = inventory;
        this.users = users;
        this.nextId = nextId;
    }
}
