import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadBlockingChatServer extends Thread
{
    public static List <Conversation> conversations =new ArrayList<>();
    int clientsCount = 0;
    public static void main(String[] args) {
        new MultiThreadBlockingChatServer().start();
    }

    @Override
    public void run() {
    try{
        ServerSocket serverSocket=new ServerSocket(3333);
        while(true){
            Socket socket = serverSocket.accept();
            InputStream is=socket.getInputStream();
            InputStreamReader isr=new InputStreamReader(is);
            BufferedReader br=new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println("Welcome to our chat platform");
            String clientInfos=br.readLine();
            String[] clientInfosArray = clientInfos.split(",");
            clientInfosArray[0]=String.valueOf(clientInfosArray[0]);
            clientsCount++;
            Conversation conversation = new Conversation(socket, Integer.parseInt(clientInfosArray[0]),clientInfosArray[1]);
            conversations.add(conversation);
            conversation.start();
        }
    } catch(IOException e) {
        throw new RuntimeException(e);
    }
    }

    class Conversation extends Thread{
        private int clientId;
        private String clientName;

        private Socket socket;

        public String getClientName() {
            return clientName;
        }
        public Conversation(Socket socket, int clientId,String clientName){
            this.socket=socket;
            this.clientId=clientId;
            this.clientName=clientName;
        }
        @Override
        public void run() {
            try {
                InputStream is=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br=new BufferedReader(isr);
                OutputStream os=socket.getOutputStream();
                System.out.println("New connection from Number "+clientId+" IP= " +socket.getRemoteSocketAddress());
                String request;
                String message="";
                List<Integer> ids=new ArrayList<>();
                while ((request=br.readLine())!=null){
                    System.out.println(request);
                    if(request.split(":")[1].contains("=>")){
                        ids=new ArrayList<>();
                        String[] items = request.split(" :")[1].split("=>");
                        String clients=items[0];
                        message=request.split(":")[0]+" :"+items[1] ;
                        if(clients.contains(",")){
                            String[] idsListStr = clients.split(",");
                            for(String id : idsListStr){ ids.add(Integer.parseInt(id)); }
                        } else { ids.add(Integer.parseInt(clients)); }
                    } else{
                        message=request;
                        ids=conversations.stream().map(c->c.clientId).collect(Collectors.toList());
                    }
                    System.out.println("New Request => "+request.split(":")[1]+" from "+socket.getRemoteSocketAddress());
                    broadcastMessage(message,socket,ids);
                }
            } catch (IOException e) {
                System.out.println("there is a client just disconnected");
            }
        }
    }

    public void broadcastMessage(String message, Socket from, List<Integer> clientIds){
        try {
            for (Conversation conversation:conversations){
                Socket socket = conversation.socket;
                if((socket!=from) && clientIds.contains(conversation.clientId)){
                    OutputStream os=socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(os,true);
                    printWriter.println(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}