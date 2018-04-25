package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class OutgoingRequestsHandler implements Runnable {
	private RemotePeerInfo remotePeerInfo;
	Socket socket;
	BufferedOutputStream out;
	BufferedInputStream in;

	OutgoingRequestsHandler(RemotePeerInfo remote) {
		this.remotePeerInfo = remote;
	}

	@Override
	public void run() {
		System.out.println("Thread from outgoing pool spawned");
		PeerCommunication peerCommunication = null;
		try {
			peerCommunication = new PeerCommunication(this.remotePeerInfo);
            peerProcess.log.TCPConnection(this.remotePeerInfo.get_peerID(), true);
            while(Peer.getPeerInstance().connectedPeers.size() != Peer.getPeerInstance().handShakeCount){

			}
            Peer.getPeerInstance().PreferredNeighbours();
            Peer.getPeerInstance().OptimisticallyUnchokedNeighbour();
            /**/
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			peerCommunication.startMessageExchange();
		} catch (Exception e) {
			throw new RuntimeException("Error starting message exchange in outgoing request handler for "
					+ this.remotePeerInfo.get_peerID(), e);
		}
	}
}
