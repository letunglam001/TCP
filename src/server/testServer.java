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
public class testServer {
    private ServerSocket server;
    private Socket client;
    private sinhVienDAO svDAO;
    private int port = 8888;

    public testServer() {
        svDAO = new sinhVienDAO();
        openConnection();
        while(true){
            listening();
        }
    }

    private void openConnection() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
        }
    }

    private void listening() {
        Object obj = receiveData();
        if(obj instanceof String){
            String a = String.valueOf(obj);
            if(a.equals("getList")){
                sendData(svDAO.getAllSV());
            }
            if(a.equals("getByName")){
                Object obj1 = receiveData();
                String name = String.valueOf(obj1);
                sendData(svDAO.getByName(name));
            }
            if(a.equals("getById")){
                Object obj1 = receiveData();
                int id = Integer.parseInt(String.valueOf(obj1));
                sendData(svDAO.getById(id));
            }
        }
        if(obj instanceof SinhVien){
            SinhVien s = (SinhVien) receiveData();
            if(svDAO.updateSV(s)){
                sendData("updateSuccess");
            }
            else{
                sendData("updateFailed");
            }
        }
    }
    
    public void sendData(Object obj){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(obj);
        } catch (Exception e) {
        }
    }
    public Object receiveData(){
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            ois.readObject();
        } catch (Exception e) {
        }
        return obj;
    }
}
