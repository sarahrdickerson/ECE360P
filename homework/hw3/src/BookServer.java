import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class BookServer {
    public static void main(String[] args) throws IOException {
        int tcpPort;
        int udpPort;
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        String fileName = args[0];
        tcpPort = 7000;
        udpPort = 8000;

        LinkedHashMap<String, Integer> inventory = new LinkedHashMap<>();
        HashMap<String, List<Loan>> users = new HashMap<>();
        //Terrible implementation, should be changed
        int[] nextLoanId = {1};

        // parse the inventory file
        File f = new File(fileName);
        Scanner scan = new Scanner(f);
        while(scan.hasNextLine()){
            String bookName = "";
            while(!scan.hasNextInt()) {
                bookName += scan.next();
                bookName += " ";
            }
            int quantity = scan.nextInt();
            inventory.put(bookName.substring(0, bookName.length() - 1), quantity);
        }

        // TODO: handle request from clients
        ServerSocket tcpListener = new ServerSocket(tcpPort);
        Socket s;
        DatagramSocket updListener = new DatagramSocket(udpPort);
        DatagramPacket dataPacket;
        // Accept client connections and have them running on separate threads
        while((s = tcpListener.accept()) != null){
            System.out.println("Client connected");
            Thread t = new ServerThread(s, inventory, users, nextLoanId);
            t.start();
        }

    }
}