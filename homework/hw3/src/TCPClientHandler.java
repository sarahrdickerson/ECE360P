import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

public class TCPClientHandler extends Thread{
    Socket client;
    Scanner in;
    PrintStream out;
    LinkedHashMap<String, Integer> inventory;
    HashMap<String, List<Loan>> users;
    int[] nextId;
    Semaphore mutex;
    boolean exit;

    public TCPClientHandler(Socket client, LinkedHashMap<String, Integer> inventory, HashMap<String,
            List<Loan>> users, int[] nextId, Semaphore mutex){
        this.client = client;
        try {
            this.in = new Scanner(client.getInputStream());
            this.out = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.inventory = inventory;
        this.users = users;
        this.nextId = nextId;
        this.mutex = mutex;
        this.exit = false;
    }

    public void setMode(String mode) throws IOException {
        String outputMessage;
        if(mode.equals("u")){
            outputMessage = "The communication mode is set to UDP";
            out.println(outputMessage);
            out.flush();
            in.close();
            out.close();
            client.close();
            exit = true;
        }
        else{
            outputMessage = "The communication mode is set to TCP";
            out.println(outputMessage);
            out.flush();
        }
    }

    public void beginLoan(String username, String bookname) throws InterruptedException {
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
        out.println(outputMessage);
        out.flush();
        mutex.release();
    }

    public void end_loan(int loan_id) throws InterruptedException {
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
        out.println(outputMessage);
        out.flush();
        mutex.release();
    }

    public void get_loans(String username) throws InterruptedException {
        mutex.acquire();
        List<Loan> ll = users.get(username);
        boolean found = (ll != null && ll.size() != 0);
        if(found){
            for(Loan l : ll)
                out.println("" + l.id + " " + l.name);
            out.println("DONE");
            out.flush();
        }
        else{
            out.println("No record found for " + username);
            out.println("DONE");
            out.flush();
        }
        mutex.release();
    }

    public void get_inventory() throws InterruptedException {
        mutex.acquire();
        for(Map.Entry<String, Integer> entry : inventory.entrySet())
            out.println(entry.getKey() + " " + entry.getValue());
        out.println("DONE");
        out.flush();
        mutex.release();
    }

    public void exit() throws IOException, InterruptedException {
        mutex.acquire();
        PrintWriter pw = new PrintWriter("inventory.txt", "UTF-8");
        for(Map.Entry<String, Integer> entry : inventory.entrySet())
            pw.println(entry.getKey() + " " + entry.getValue());
        pw.close();
        in.close();
        out.close();
        client.close();
        mutex.release();
    }

    public void run(){
        while(!exit) {
            try {
                String command = in.nextLine();
                switch (command) {
                    case "set-mode":
                        setMode(in.nextLine());
                        break;
                    case "begin-loan":
                        beginLoan(in.nextLine(), in.nextLine());
                        break;
                    case "end-loan":
                        end_loan(Integer.parseInt(in.nextLine()));
                        break;
                    case "get-loans":
                        get_loans(in.nextLine());
                        break;
                    case "get-inventory":
                        get_inventory();
                        break;
                    case "exit":
                        exit();
                        exit = true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
