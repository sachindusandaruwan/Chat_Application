package lk.ijse.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements  Runnable{
    public ServerSocket serverSocket;

    public static Server server;
    private ArrayList<LocalSocket> clientList;
    private ExecutorService pool;
    Server(){
        clientList=new ArrayList<>();
    }

    public void broadCast(String massege){
        for (LocalSocket localSocket:clientList){
            if(localSocket != null){
                localSocket.sentMassege(massege);
            }
        }
    }

    private void broadCastImage(String name, byte[] bytes) {
        for (LocalSocket localSocket:clientList){
            if (localSocket!=null){
                localSocket.sentImage(name,bytes);
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(5600);
            pool= Executors.newCachedThreadPool();
            while (true) {
                Socket accept = serverSocket.accept();
                LocalSocket localSocket=new LocalSocket(accept);
                clientList.add(localSocket);
                pool.execute(localSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public class LocalSocket implements Runnable{
        private Socket socket;
        private DataInputStream dataInputStream;

        private DataOutputStream dataOutputStream;
        @Override
        public void run() {
            try {
                dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dataOutputStream=new DataOutputStream(socket.getOutputStream());

                String massege;
                while ((massege=dataInputStream.readUTF())!=null){
                   if(massege.equals("/01")){
                       massege = dataInputStream.readUTF();
                       broadCast(massege);
                   } else if (massege.equals("/02")){
                        String name=dataInputStream.readUTF();
                        byte[] bytes=new byte[dataInputStream.readInt()];
                        dataInputStream.readFully(bytes);
                        broadCastImage(name,bytes);

                   }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void sentMassege(String massege){
            try {
                dataOutputStream.writeUTF("/01");
                dataOutputStream.flush();
                dataOutputStream.writeUTF(massege);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        private void sentImage(String name,byte[] bytes){
            try {
                dataOutputStream.writeUTF("/02");
                dataOutputStream.flush();
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();
                dataOutputStream.writeInt(bytes.length);
                dataOutputStream.flush();
                dataOutputStream.write(bytes);
                dataOutputStream.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public LocalSocket(Socket socket) {
            this.socket = socket;
        }

    }

    public static void main(String[] args) {
        Server server1=new Server();
        server1.run();
    }
}
