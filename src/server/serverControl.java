/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import dao.sinhVienDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import model.SinhVien;

/**
 *
 * @author ADMIN
 */
public class serverControl {
    private sinhVienDAO svDAO;
    private int port = 8888;
    private ServerSocket serverSocket;
    private Socket client;

    public serverControl() {
        try {
            svDAO = new sinhVienDAO();
            openConnection();
            client = serverSocket.accept();
            while(true){
                listening();
            }
            
        } catch (Exception e) {
        }
    }

    private void openConnection() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
        }
    }

    private void listening() {
        Object obj = receiveData();
        if(obj instanceof String){
            String request = (String) obj;
            if(request.equals("getList")){
                sendData(svDAO.getAllSV());
            }
            if(request.equals("getByName")){
                String name = (String) receiveData();
                sendData(svDAO.getByName(name));
            }
            if(request.equals("getById")){
                int id = (int) receiveData();
                sendData(svDAO.getById(id));
            }
        }
        if(obj instanceof SinhVien){
            SinhVien s = (SinhVien) obj;
            if(svDAO.updateSV(s)){
                sendData("updateSuccess");
            }else{
                sendData("updateFailed");
            }
        }
    }
    private void sendData(Object obj){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
        }
    }
    private Object receiveData(){
        Object obj = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(client.getInputStream());
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return obj;   
    }
}
