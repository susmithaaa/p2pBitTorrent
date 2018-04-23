package com;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.FileProcessor.FileManagerExecutor;
import com.messages.Message;
import com.messages.MessageHandler;
import com.messages.MessageUtil;

public class PeerCommunicationHelper {
	
	public static Message sendBitSetMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)5, MessageUtil.toByteArray(Peer.getPeerInstance().getBitSet()));
		Message message = messageHandler.buildMessage();
		System.out.println(message.getMessage_type());
//		System.out.println(MessageUtil.byteArrayToInt(message.getMessage_length()));
//		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil
//				.concatenateByte(message.getMessage_length(), message.getMessage_type()),message.getMessagePayload());
		out.writeObject(message);
		out.flush();

		return message;
	}
	
	public static Message sendInterestedMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)2);
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendNotInterestedMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)3);
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendChokeMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)0);
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.writeObject(message);
//		out.write(messageToSend);
		out.flush();
		return message;
	}
	
	public static Message sendUnChokeMsg(ObjectOutputStream out) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)1);
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendRequestMsg(ObjectOutputStream out, RemotePeerInfo remote) throws Exception{
		int a = getPieceIndex(remote);
		if(a== -1){
			sendNotInterestedMsg(out);
			return null;
		}
		MessageHandler messageHandler = new MessageHandler((byte)6,MessageUtil.intToByteArray(a));
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
//		out.write(messageToSend);
		out.writeObject(message);
		out.flush();
		return message;
	}

	public static Message sendHaveMsg(ObjectOutputStream out, int recentReceivedPieceIndex) throws Exception{
		MessageHandler messageHandler = new MessageHandler((byte)4,MessageUtil.intToByteArray(recentReceivedPieceIndex));
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
//		out.write(messageToSend);
		out.writeObject(message);
		out.flush();
		return message;
	}
	
	public static Message sendPieceMsg(ObjectOutputStream out, int pieceIndex) throws Exception{
		//File piecePart = FileManagerExecutor.getFilePart(pieceIndex);
		byte[] index = MessageUtil.intToByteArray(pieceIndex);
		byte[] payload = FileManagerExecutor.getFilePart(pieceIndex);
		//byte[] payload = Files.readAllBytes(piecePart.toPath());
		byte[] payloadWithIndex = MessageUtil.concatenateByteArrays(index, payload);
		MessageHandler messageHandler = new MessageHandler((byte)7,payloadWithIndex );
		Message message = messageHandler.buildMessage();
//		byte[] messageToSend = MessageUtil.concatenateByte(message.getMessage_length(), message.getMessage_type());
//		out.write(messageToSend);
		out.writeObject(message);
		out.flush();
		return message;
	}

	public static Message getActualObjectMessage(ObjectInputStream in) {
		try {
			Message received = (Message) in.readObject();
			if (received == null) System.out.println("received null");
			else System.out.println("object received");

			System.out.println(received.toString());
			return received;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static byte[] getActualMessage(BufferedInputStream in) {
        byte[] lengthByte = new byte[12];
        int read = 0;
        byte[] data = null;
        try {
        	while ((read = in.read(lengthByte)) >= 0) {
        		for (int i = 0; i < read; i++) {
					System.out.println(read);
				}
			}
//            read = in.read(lengthByte);

            int dataLength = MessageUtil.byteArrayToInt(lengthByte);
            //read msg type
            byte[] msgType = new byte[1];
            in.read(msgType);
                int actualDataLength = dataLength - 1;
                data = new byte[actualDataLength];
                data = MessageUtil.readBytes(in, data, actualDataLength);

        } catch (IOException e) {
            System.out.println("Could not read length of actual message");
            e.printStackTrace();
        }
        return data;
    }
    
    public static byte getMessageType(BufferedInputStream in) throws IOException{
    	byte[] lengthBytePlusMsgType = new byte[5];
    	in.read(lengthBytePlusMsgType);
    	return lengthBytePlusMsgType[4];
    }
    
    public static boolean isInterseted(BitSet b1, BitSet b2){
    	for(int i=0; i<b2.length();i++){
    		if(b1.get(i)!=b2.get(i)){
    			return false;
    		}
    	}
		return true;
    }
    
    public static int getPieceIndex(RemotePeerInfo remote){
    	BitSet b1 = remote.getBitfield();
    	BitSet b2 = Peer.getPeerInstance().getBitSet();
    	int pieceIndex = compare(b1,b2);
    	if(pieceIndex == 0)
    	{
    		//send not interested
    		//PeerCommunicationHelper.sendInterestedMsg();
    	}
    	return pieceIndex;
    }

    public static int compare(BitSet lhs, BitSet rhs) {

        if(lhs.isEmpty() && rhs.isEmpty()){
            return -1;
        }
        if(rhs.isEmpty()){
            return lhs.nextSetBit(0);
        }

        if (lhs.equals(rhs)) return -1;
        ///BitSet temp = new BitSet();
        List<Integer> temp = new ArrayList<>(); 
        for(int i=0; i < lhs.length(); i++)
        {
        	if(!rhs.get(i))
        	{
        		temp.add(i);
        	}     	
        }
        
        int index = ThreadLocalRandom.current().nextInt(0, temp.size());
        //BitSet xor = (BitSet)lhs.clone();
        //xor.xor(rhs);
        //int firstDifferent = xor.length()-1;
        //if(firstDifferent==-1)
        //    return 0;

        return temp.get(index);
    }

   public static void computeDownloadRate(){
	   
   }

}
