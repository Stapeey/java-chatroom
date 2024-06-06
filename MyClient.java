import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClient {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader input;
    private String msg;
    private volatile boolean running = true;


    public void connect(String ip, int port){
        try {
            client = new Socket(ip, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e) {
            System.out.println("Connection failure");
            e.printStackTrace();
        }
    }

    public void message(){
        input = new BufferedReader(new InputStreamReader(System.in));
        while (running){
            try {
                msg = input.readLine();
                out.println(msg);
                if (msg.equals("!QUIT")){
                    running = false;
                    input.close();
                    exit();
                    break;
                }
            } catch (Exception e) {
                System.out.println("message error");
                e.printStackTrace();
            }
        }
    }

    public void listen(){
        while (running){
            try {
                String incoming = in.readLine();
                System.out.println(incoming);
                if (incoming != null && incoming.equals("!QUIT")){
                    running = false;
                    exit();
                    break;
                }
            } catch (Exception e) {
                System.out.println("listener failure");
                e.printStackTrace();
            }
        }
    }

    public void exit(){
        try {
            out.close();
            in.close();
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }

    class Listener extends Thread{
        @Override
        public void run(){
            listen();
        }
    }
    class Messenger extends Thread{
        @Override
        public void run(){
            message();
        }
    }

    public static void main(String[] args){
        MyClient client = new MyClient();
        client.connect("127.0.0.1", 9999);
        MyClient.Listener myListener = client.new Listener(); 
        MyClient.Messenger myMessenger = client.new Messenger();
        myListener.start();
        myMessenger.start();
        
    }
}