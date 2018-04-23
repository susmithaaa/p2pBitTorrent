package com.FileProcessor;

import java.io.*;
import java.net.Socket;
import java.util.*;

import com.Constants;
import com.Peer;
import com.PeerCommunicationHelper;
import com.messages.Message;
import com.messages.MessageUtil;

public class FileManagerExecutor  {
  // static Map<Integer,File> pieceMap;
  // static  Map<Integer,File> fileSoFar = new TreeMap<>();
	static Map<Integer,byte[]> pieceMap;
   static Map<Integer,byte[]> fileSoFar = new TreeMap<>();

    public static void fileSplit(File inputFile, int pieceSize){
        pieceMap = new HashMap<>();
        FileInputStream inputStream;
        FileOutputStream partOfFile;
        File newFilePart;
        int fileSize;
        int remainingFileSize;
        int bytesRead,count = 0;
        byte[] filePiece;
        try{
            inputStream = new FileInputStream(inputFile);
            fileSize = Constants.getFileSize();
            remainingFileSize = fileSize;
            while(fileSize>0){
            	if(remainingFileSize<pieceSize){
            		filePiece = new byte[remainingFileSize];
            	}
            	else{
                    filePiece = new byte[pieceSize];

            	}
                bytesRead = inputStream.read(filePiece);
                fileSize-=bytesRead;
                newFilePart = new File( Constants.root + "/peer_" + Peer.getPeerInstance().get_peerID() + "/" + "Part" + Integer.toString(count));
                partOfFile = new FileOutputStream(newFilePart);
                partOfFile.write(filePiece);
                pieceMap.put(count,filePiece);
                partOfFile.flush();
                partOfFile.close();
                count++;
                remainingFileSize = remainingFileSize-pieceSize;
            }
            inputStream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
    
    public static byte[] getFilePart(int filePartNumber){
    	if( fileSoFar.get(filePartNumber)==null)
    		return pieceMap.get(filePartNumber);
    	else return fileSoFar.get(filePartNumber);
    }
    
   /* public static void putFilePart(int filePartNumber, File filePart){
    	fileSoFar.put(filePartNumber,filePart);
    }*/

   /* public void sendFilePart(int filePart, Socket socket) {
        File fileToSend;
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        OutputStream outputStream;
        fileToSend = pieceMap.get(filePart);
        byte[] byteFile = new byte[(int) fileToSend.length()];
        try {

            fileInputStream = new FileInputStream(fileToSend);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(byteFile, 0, byteFile.length);
            outputStream = socket.getOutputStream();
            outputStream.write(byteFile, 0, byteFile.length);
            outputStream.flush();

        }catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void acceptFilePart(int filePart,Message message) {
       // FileOutputStream fileOutputStream;
      //  ObjectOutputStream objectOutputStream;
        File fileToWrite;
        //   fileToWrite = new File(Constants.root + "/peer_" + String.valueOf(Peer.getPeerInstance().get_peerID()) + "/"+"Part" + Integer.toString(filePart));
         //   fileOutputStream = new FileOutputStream(fileToWrite);
         //   objectOutputStream = new ObjectOutputStream(fileOutputStream);
            byte[] payLoadWithIndex = message.getMessagePayload();
            byte[] payLoad = MessageUtil.removeFourBytes(payLoadWithIndex);
         //   objectOutputStream.write(payLoad);
         //   fileSoFar.put(filePart, fileToWrite);
            fileSoFar.put(filePart, payLoad);
         //   objectOutputStream.flush();
        //    objectOutputStream.close();
    }

    /*public static  void filesmerge(){
        File mergeFile = new File(Constants.root + "/peer_" + String.valueOf(Peer.getPeerInstance().get_peerID()) + "/"+Constants.getFileName());    // change file name
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        byte[] fileBytes;
        int bytesRead=0;
        try{
            fileOutputStream = new FileOutputStream(mergeFile,true);
            Set<Integer> keys = fileSoFar.keySet();

            for(Integer key : keys){
                fileInputStream = new FileInputStream(fileSoFar.get(key));
                fileBytes = new byte[(int)fileSoFar.get(key).length()];
                bytesRead = fileInputStream.read(fileBytes,0,(int) fileSoFar.get(key).length());
                fileOutputStream.write(fileBytes);
                fileOutputStream.flush
                ();
                fileInputStream.close();
            }
            fileOutputStream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }*/
    
    public static void filesmerge() throws IOException{
        FileOutputStream fileOutputStream;
        File mergeFile = new File(Constants.root + "/peer_" + String.valueOf(Peer.getPeerInstance().get_peerID()) + "/"+Constants.getFileName());
    	byte[] combinedFile = new byte[Constants.getFileSize()];
    	for(Map.Entry<Integer,byte[]> e : fileSoFar.entrySet()) {
    		MessageUtil.concatenateByteArrays(combinedFile, e.getValue());
    	}
    	fileOutputStream = new FileOutputStream(mergeFile);
    	fileOutputStream.write(combinedFile);
    	fileOutputStream.flush();
    	fileOutputStream.close();
    	}
}


