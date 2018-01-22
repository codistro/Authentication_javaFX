package io.github.codistro.authenticate;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    final String SEPARATOR = "break";

    public Client() throws IOException {
        socket = new Socket("127.0.0.1",1002);
    }

    public String request(String data)throws IOException{
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter pw = new PrintWriter(os);
        pw.println(data);
        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String result = br.readLine();
        String finalData[] = result.split(SEPARATOR);

        return result;
    }
}
