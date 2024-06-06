import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer{
    private ServerSocket server;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader input;
    private String msg;
    private volatile boolean running = true;


    public void load(int port){
        try {

            server = new ServerSocket(port);
            client = server.accept();
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        } catch (Exception e) {
            // ignore
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
            in.close();
            out.close();
            client.close();
            server.close();
        } catch (Exception e) {
            // ignore
        }
       
    }

    


    public static void main(String[] args){
        MyServer server = new MyServer();
        server.load(9999);
        class Listener extends Thread{
            @Override
            public void run(){
                server.listen();
            }
        }
        class Messenger extends Thread{
            @Override
            public void run(){
                server.message();
            }
        }
        Listener myListener = new Listener();
        Messenger myMessenger = new Messenger();
        myListener.start();
        myMessenger.start();
    }
}
