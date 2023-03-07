import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class BookClient {
    // To be used for TCP
    Scanner in;
    PrintStream out;
    Socket tcpSocket;
    int tcpPort;

    // To be used for UDP
    DatagramSocket udpSocket;
    InetAddress ia;
    DatagramPacket sPacket, rPacket;
    int udpPort;
    byte[] rbuffer = new byte[4096];
    // Common
    PrintWriter pw;
    char currentMode;
    String hostAddress;

    public void handleUDP(String[] tokens) throws IOException {
        String sendMessage = "";
        for(int i = 0; i < tokens.length; i++){
            sendMessage += tokens[i];
            if(i != tokens.length - 1)
                sendMessage += " ";
        }
        byte[] buffer = sendMessage.getBytes();
        sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
        udpSocket.send(sPacket);
        rPacket = new DatagramPacket(rbuffer, rbuffer.length);
        udpSocket.receive(rPacket);
        String returnMessage = new String(rPacket.getData(), 0, rPacket.getLength ());
        pw.println(returnMessage);
    }

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
                //udpSocket = new DatagramSocket(udpPort);
                currentMode = 'u';
            }
        }
        else{
            handleUDP(tokens);
            if(mode.equals("t")){
                //udpSocket.close();
                tcpSocket = new Socket(hostAddress, tcpPort);
                in = new Scanner(tcpSocket.getInputStream());
                out = new PrintStream(tcpSocket.getOutputStream());
                currentMode = 't';
            }
        }
    }

    public void beginLoan(String[] tokens) throws IOException {
        if(currentMode == 't') {
            out.println(tokens[0]);
            out.println(tokens[1]);
            // Send the bookname as a whole string
            String bookname = "";
            for (int i = 2; i < tokens.length; i++) {
                bookname += tokens[i];
                bookname += " ";
            }
            bookname = bookname.substring(0, bookname.length() - 1);
            out.println(bookname);
            out.flush();
            String returnMessage = in.nextLine();
            pw.println(returnMessage);
        }
        else{
            handleUDP(tokens);
        }
    }

    public void endLoan(String[] tokens) throws IOException {
        if(currentMode == 't') {
            out.println(tokens[0]);
            out.println(tokens[1]);
            out.flush();
            String returnMessage = in.nextLine();
            pw.println(returnMessage);
        }
        else {
            handleUDP(tokens);
        }
    }

    public void getLoans(String[] tokens) throws IOException {
        if(currentMode == 't') {
            out.println(tokens[0]);
            out.println(tokens[1]);
            out.flush();
            while (true) {
                String line = in.nextLine();
                if (line.equals("DONE"))
                    break;
                pw.println(line);
            }
        }
        else{
            handleUDP(tokens);
        }
    }

    public void getInventory(String[] tokens) throws IOException {
        if(currentMode == 't') {
            out.println(tokens[0]);
            out.flush();
            while (true) {
                String line = in.nextLine();
                if (line.equals("DONE"))
                    break;
                pw.println(line);
            }
        }
        else{
            handleUDP(tokens);
        }
    }

    public void exit(String[] tokens) throws IOException {
        if(currentMode == 't') {
            out.println(tokens[0]);
            out.flush();
            pw.close();
            in.close();
            out.close();
            tcpSocket.close();
        }
        else{
            String sendMessage = "";
            for(int i = 0; i < tokens.length; i++){
                sendMessage += tokens[i];
                if(i != tokens.length - 1)
                    sendMessage += " ";
            }
            byte[] buffer = sendMessage.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            pw.close();
            udpSocket.close();
        }
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
        BookClient myClient = new BookClient();
        myClient.pw = new PrintWriter("out_" + clientId + ".txt", "UTF-8");
        myClient.hostAddress = "localhost";
        myClient.tcpPort = 7001;// hardcoded -- must match the server's tcp port
        myClient.udpPort = 8001;// hardcoded -- must match the server's udp port

        myClient.ia = InetAddress.getByName(myClient.hostAddress);
        myClient.udpSocket = new DatagramSocket();
        myClient.currentMode = 'u';

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("set-mode")) {
                    myClient.set_mode(tokens);
                } else if (tokens[0].equals("begin-loan")) {
                    myClient.beginLoan(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("end-loan")) {
                    myClient.endLoan(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-loans")) {
                    myClient.getLoans(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-inventory")) {
                    myClient.getInventory(tokens);
                    // appropriate responses form the server
                } else if (tokens[0].equals("exit")) {
                    myClient.exit(tokens);
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            myClient.pw.close();
        }
    }
}