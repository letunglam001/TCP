/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import dao.sinhVienDAO;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import javax.swing.JOptionPane;
import model.SinhVien;

/**
 *
 * @author ADMIN
 */
public class testClient {
    private Socket client;
    private int port = 8888;
    private String host ="localhost";
    private sinhVienDAO svDAO;
    private SinhVienFrm svf;
    private UpdateFrm uf;

    public testClient(SinhVienFrm svf) {
        this.svf = svf;
        this.svf.addSearchListener(new SearchListener());
        this.svf.addUpdateListener(new PreUpdateListener());
        openConnection();
        sendData("getList");
        List<SinhVien> sv = (List<SinhVien>) receiveData();
        svf.getAll(sv);
    }

    public void openConnection() {
        try {
            client = new Socket(host, port);
        } catch (IOException e) {
        }
    }

    public void sendData(Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
        }
    }
    public Object receiveData(){
        Object obj=null;
        try {
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return obj;
    }

    private class UpdateListener implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SinhVien s = uf.getSVData();
                String response = (String) receiveData();
                if(response.equals("updateSuccess")){
                    uf.dispose();
                    JOptionPane.showMessageDialog(svf, response);
                    sendData("getList");
                    List<SinhVien> sv = (List<SinhVien>) receiveData();
                    svf.getAll(sv);
                }else{
                    JOptionPane.showMessageDialog(uf,response);
                }
            } catch (HeadlessException ex) {
            }
        }
    }

    private class SearchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = svf.getSearchName();
                sendData("getByName");
                List<SinhVien> sv = (List<SinhVien>) receiveData();
                svf.getAll(sv);
            } catch (Exception ex) {
            }
        }

        
    }

    private class PreUpdateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(e.getActionCommand());
                sendData("getById");
                sendData(id);
                SinhVien s = (SinhVien) receiveData();
                uf = new UpdateFrm();
                uf.setVisible(true);
                uf.addUpdateListener(new UpdateListener());
                uf.setSVData(s);
            } catch (NumberFormatException ex) {
            }
        }

        
    }
    
}
