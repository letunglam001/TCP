/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.swing.JOptionPane;
import model.SinhVien;

/**
 *
 * @author ADMIN
 */
public class clientControl {
    private Socket client;
    private int serverPort = 8888;
    private String serverName="localhost";
    private SinhVienFrm svf;
    private UpdateFrm uf;

    public clientControl(SinhVienFrm svf) {
        this.svf = svf;
        openConnect();
        sendData("getList");
        List<SinhVien> sv = (List<SinhVien>) receiveData();
        svf.getAll(sv);
        this.svf.addSearchListener(new SearchListener());
        this.svf.addUpdateListener(new PreUpdateListener());
        
    }

    private void openConnect() {
        try {
            client = new Socket(serverName, serverPort);
        } catch (IOException e) {
        }
    }
    public void sendData(Object obj){
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
        }
    }
    
    public Object receiveData(){
        Object obj = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(client.getInputStream());
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return obj;    
    }

    private class UpdateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sendData(uf.getSVData());
            String response = (String) receiveData();
            if(response.equals("updateSuccess")){
                uf.dispose();
                JOptionPane.showMessageDialog(svf, response);
                sendData("getList");
                List<SinhVien> sv = (List<SinhVien>) receiveData();
                svf.getAll(sv);
            }
            if (response.equals("updateFailed")){
                JOptionPane.showMessageDialog(uf, response);
            }
        } 
    }

    private class SearchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sendData("getByName");
            sendData(svf.getSearchName());
            List<SinhVien> sv = (List<SinhVien>) receiveData();
            svf.getAll(sv);
        }
    }

    private class PreUpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendData("getById");
            sendData(Integer.parseInt(e.getActionCommand()));
            uf = new UpdateFrm();
            uf.setVisible(true);
            uf.addUpdateListener(new UpdateListener());
            uf.setSVData((SinhVien) receiveData());
        }
    }
    
}
