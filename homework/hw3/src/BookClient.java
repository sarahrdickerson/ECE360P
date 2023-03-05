import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class BookClient {
    Scanner in;
    PrintStream out;
    Socket tcpSocket;
    DatagramSocket udpSocket;
    PrintWriter pw;
    char currentMode;

    public void set_mode(String[] tokens) throws IOException {
        String mode = tokens[1];
        if(currentMode == 't'){
            // Send change request to server
            out.println(tokens[0]);
            out.println(tokens[1]);
            // Receive confirmation to change
            String returnMessage = in.nextLine();
            pw.println(returnMessage);
            if(mode.equals("u")){
                in.close();
                out.close();
                tcpSocket.close();
                udpSocket = new DatagramSocket(8000);
            }
        }
    }

    public void beginLoan(String[] tokens){
        out.println(tokens[0]);
        out.println(tokens[1]);
        // Send the bookname as a whole string
        String bookname = "";
        for(int i = 2; i < tokens.length; i++) {
            bookname += tokens[i];
            bookname += " ";
        }
        bookname = bookname.substring(0, bookname.length()- 1);
        out.println(bookname);
        out.flush();
        String returnMessage = in.nextLine();
        pw.println(returnMessage);
    }

    public void endLoan(String[] tokens){
        out.println(tokens[0]);
        out.println(tokens[1]);
        out.flush();
        String returnMessage = in.nextLine();
        pw.println(returnMessage);
    }

    public void getLoans(String[] tokens){
        out.println(tokens[0]);
        out.println(tokens[1]);
        out.flush();
        while(true){
            String line = in.nextLine();
            if(line.equals("DONE"))
                break;
            pw.println(line);
        }
    }

    public void getInventory(String[] tokens){
        out.println(tokens[0]);
        out.flush();
        while(true){
            String line = in.nextLine();
            if(line.equals("DONE"))
                break;
            pw.println(line);
        }
    }

    public void exit(String[] tokens){
        out.println(tokens[0]);
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        String hostAddress;
        int tcpPort;
        int udpPort;
        int clientId;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: command-file, clientId");
            System.out.println("\t(1) command-file: file with commands to the server");
            System.out.println("\t(2) clientId: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";
        tcpPort = 7000;// hardcoded -- must match the server's tcp port
        udpPort = 8000;// hardcoded -- must match the server's udp port
        BookClient myClient = new BookClient();
        myClient.tcpSocket = new Socket(hostAddress, tcpPort);
        myClient.in = new Scanner(myClient.tcpSocket.getInputStream());
        myClient.out = new PrintStream(myClient.tcpSocket.getOutputStream());
        myClient.pw = new PrintWriter("out_" + clientId + ".txt", "UTF-8");
        myClient.currentMode = 't';

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("set-mode")) {
                    // TODO: set the mode of communication for sending commands to the server
                    myClient.set_mode(tokens);
                } else if (tokens[0].equals("begin-loan")) {
                    // TODO: send appropriate command to the server and display the
                    myClient.beginLoan(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("end-loan")) {
                    // TODO: send appropriate command to the server and display the
                    myClient.endLoan(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-loans")) {
                    // TODO: send appropriate command to the server and display the
                    myClient.getLoans(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-inventory")) {
                    // TODO: send appropriate command to the server and display the
                    myClient.getInventory(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("exit")) {
                    myClient.exit(tokens);
                    myClient.pw.close();
                    myClient.in.close();
                    myClient.out.close();
                    myClient.tcpSocket.close();
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}