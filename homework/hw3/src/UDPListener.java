import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Semaphore;

public class UDPListener extends Thread{
    LinkedHashMap<String, Integer> inventory;
    HashMap<String, List<Loan>> users;
    int[] nextId;
    int port;
    Semaphore mutex;

    DatagramSocket dataSocket;
    DatagramPacket dataPacket, returnPacket;

    public UDPListener(LinkedHashMap<String, Integer> inventory, HashMap<String, List<Loan>> users,
                       int[] nextLoanId, int port, Semaphore mutex){
        this.inventory = inventory;
        this.users = users;
        this.nextId = nextLoanId;
        this.port = port;
        this.mutex = mutex;
    }

    public void setMode(InetAddress ia, int port, String mode) throws IOException {
        String outputMessage;
        if(mode.equals("t"))
            outputMessage = "The communication mode is set to TCP";
        else
            outputMessage = "The communication mode is set to UDP";
        returnPacket = new DatagramPacket(outputMessage.getBytes(), outputMessage.getBytes().length, ia, port);
        dataSocket.send(returnPacket);
    }

    public void beginLoan(InetAddress ia, int port, String username, String bookname) throws InterruptedException, IOException {
        mutex.acquire();
        String outputMessage;
        if(!inventory.containsKey(bookname))
            outputMessage = "Request Failed - We do not have this book";

        else if(inventory.get(bookname) == 0)
            outputMessage = "Request Failed - Book not available";
        else{
            inventory.put(bookname, inventory.get(bookname) - 1);
            if(!users.containsKey(username)){
                List<Loan> loans = new ArrayList<>();
                loans.add(new Loan(bookname, nextId[0]));
                users.put(username, loans);
            }
            else
                users.get(username).add(new Loan(bookname, nextId[0]));
            outputMessage = "Your request has been approved, " + nextId[0] + " " + username + " " + bookname;
            nextId[0]++;
        }
        returnPacket = new DatagramPacket(outputMessage.getBytes(), outputMessage.getBytes().length, ia, port);
        dataSocket.send(returnPacket);
        mutex.release();
    }

    public void end_loan(InetAddress ia, int port, int loan_id) throws InterruptedException, IOException {
        mutex.acquire();
        String outputMessage;
        boolean found = false;
        for(List<Loan> ll : users.values()){
            for(Loan l : ll){
                if(l.id == loan_id) {
                    ll.remove(l);
                    inventory.put(l.name, inventory.get(l.name) + 1);
                    found = true;
                    break;
                }
            }
            if(found)
                break;
        }
        if(found)
            outputMessage = "" + loan_id + " is returned";
        else
            outputMessage = "" + loan_id + " not found, no such borrow record";
        returnPacket = new DatagramPacket(outputMessage.getBytes(), outputMessage.getBytes().length, ia, port);
        dataSocket.send(returnPacket);
        mutex.release();
    }

    public void get_loans(InetAddress ia, int port, String username) throws InterruptedException, IOException {
        mutex.acquire();
        StringBuilder sb = new StringBuilder("");
        String outputMessage;
        List<Loan> ll = users.get(username);
        boolean found = (ll.size() != 0);
        if(found){
            for(Loan l : ll)
                sb.append(l.id + " " + l.name + "\n");
            outputMessage = sb.substring(0,sb.length() - 1);
        }
        else{
            outputMessage = "No record found for " + username;
        }
        returnPacket = new DatagramPacket(outputMessage.getBytes(), outputMessage.getBytes().length, ia, port);
        dataSocket.send(returnPacket);
        mutex.release();
    }

    public void get_inventory(InetAddress ia, int port) throws InterruptedException, IOException {
        mutex.acquire();
        StringBuilder sb = new StringBuilder("");
        String outputMessage;
        for(Map.Entry<String, Integer> entry : inventory.entrySet())
            sb.append(entry.getKey() + " " + entry.getValue() + "\n");
        outputMessage = sb.substring(0,sb.length() - 1);
        returnPacket = new DatagramPacket(outputMessage.getBytes(), outputMessage.getBytes().length, ia, port);
        dataSocket.send(returnPacket);
        mutex.release();
    }

    public void exit() throws IOException, InterruptedException {
        mutex.acquire();
        PrintWriter pw = new PrintWriter("inventory.txt", "UTF-8");
        for(Map.Entry<String, Integer> entry : inventory.entrySet())
            pw.println(entry.getKey() + " " + entry.getValue());
        pw.close();
        mutex.release();
    }

    // Have UDP connections running on separate threads
    public void run(){
        try {
            dataSocket = new DatagramSocket(port);
            byte[] buf = new byte[4096];
            while (true){
                dataPacket = new DatagramPacket(buf, buf.length);
                dataSocket.receive(dataPacket);
                String retString = new String(dataPacket.getData(), 0,
                        dataPacket.getLength());
                //System.out.println("Received: " + retString);
                // Handle the commands
                String[] tokens = retString.split("\\s+");
                switch (tokens[0]) {
                    case "set-mode":
                        setMode(dataPacket.getAddress(), dataPacket.getPort(), tokens[1]);
                        break;
                    case "begin-loan":
                        String bookName = "";
                        for(int i = 2; i < tokens.length; i++) {
                            bookName += tokens[i];
                            if(i != tokens.length - 1)
                                bookName += " ";
                        }
                        beginLoan(dataPacket.getAddress(), dataPacket.getPort(), tokens[1], bookName);
                        break;
                    case "end-loan":
                        end_loan(dataPacket.getAddress(), dataPacket.getPort(), Integer.parseInt(tokens[1]));
                        break;
                    case "get-loans":
                        get_loans(dataPacket.getAddress(), dataPacket.getPort(), tokens[1]);
                        break;
                    case "get-inventory":
                        get_inventory(dataPacket.getAddress(), dataPacket.getPort());
                        break;
                    case "exit":
                        exit();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
