import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UDPListener extends Thread{
    LinkedHashMap<String, Integer> inventory;
    HashMap<String, List<Loan>> users;
    int[] nextLoanId;
    int port;

    public UDPListener(LinkedHashMap<String, Integer> inventory, HashMap<String, List<Loan>> users, int[] nextLoanId, int port){
        this.inventory = inventory;
        this.users = users;
        this.nextLoanId = nextLoanId;
        this.port = port;
    }

    // Have UDP connections running on separate threads
    public void run(){
            try {
                DatagramSocket dataSocket = new DatagramSocket(port);
                DatagramPacket dataPacket;
                byte[] buf = new byte[1024];
                while (true){
                    dataPacket = new DatagramPacket(buf, buf.length);
                    dataSocket.receive(dataPacket);
                    System.out.println("UDP client connected");
                    Thread t = new UDPClientHandler(dataSocket, inventory, users, nextLoanId);
                    t.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
