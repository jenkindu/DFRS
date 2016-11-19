package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import dfrs.servers.BaseServerCluster;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public abstract class BaseRM {
	
	public static final String STATE_RUNNING = "Running";//0
	public static final String STATE_TERMINATED = "Terminated";//1
	public static final String STATE_RECOVERING = "Recovering";//2
	
	public static final String[] STATES = new String[] {STATE_RUNNING,STATE_TERMINATED,STATE_RECOVERING};
	
	private State state;
	private InetAddress Host;
	private boolean isStopped = false;
	private ClusterManager cluster;
	
	class State {
		String state;
		private long[] alive = new long[3];
		public State(String state) {
			super();
			this.state = state;
			for(int i=0;i<alive.length;i++) {
				alive[i] =  System.currentTimeMillis();
			}
		}
		public void setRMState(int s) {
			if(s<0||s>2)
				return;
			state = STATES[s];
		}

		public String getRMState() {
			return state;
		}
		
		public void setAlive(String server) {
			if(BaseServerCluster.SERVER_MTL.equals(server)) {
				alive[0] =  System.currentTimeMillis();
			} else if(BaseServerCluster.SERVER_WST.equals(server)) {
				alive[1] =  System.currentTimeMillis();
			}  else if(BaseServerCluster.SERVER_NDL.equals(server)) {
				alive[2] =  System.currentTimeMillis();
			} 
		}
		//s
		public long getAlive(String server) {
			long now = System.currentTimeMillis();
			long last = 0;
			if(BaseServerCluster.SERVER_MTL.equals(server)) {
				last = alive[0];
			} else if(BaseServerCluster.SERVER_WST.equals(server)) {
				last = alive[1];
			}  else if(BaseServerCluster.SERVER_NDL.equals(server)) {
				last = alive[2];
			}
			return (now-last)/1000;
		}
	}
	public BaseRM(String[] args) {
		this.cluster = new ClusterManager(this, args);
		this.state = new State(STATE_TERMINATED);
	}
	protected abstract String getRMName();
	protected abstract String getHost();
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getS2FEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	
	protected void startServer() {
		isStopped = false;
		startReceiveHeartBeat();
		startReceiveRM();
		startReceiveSE();
		startReceiveFE();
		state.setRMState(0);
	}
	
	private boolean recoveryServer() {
		state.setRMState(2);
		return true;
	}
	private String countingErrorTimes(String content) {
		String[] params = content.split("\\$");
//		if(getServer(SERVER_MTL).error()>=3) {
//			stopAllServer();
//			if(recoveryServer()) {
//				createServers(SERVERS);
//				startServer(SERVERS);
//			}
//		} else {
//			state.getAlive(SERVER_MTL);
//		}
		return BaseServerCluster.SERVER_MTL;
	}
	public String processSocketRequest(String source, String content) {
		if("RM".equals(source)) {
			return "";
		}
		while(!state.getRMState().equals("Running")) {
			continue;
		}
		if("FE".equals(source)) {
			 String server = countingErrorTimes(content);
		} else if("SE".equals(source)) {
			cluster.requestCorbaServer(content, getHost(), getS2FEport());
		} else if("HB".equals(source)) {
			state.setAlive(content);
			System.out.println(getRMName()+"-" + content + " alive");
		}
		return "";
	}
	
	//FE
	protected void startReceiveFE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getFEport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("FE: "+content);
		                String reply = processSocketRequest("FE", content);
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println(reply);
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//SE
	protected void startReceiveSE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getSEport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("SE: "+content);
		                processSocketRequest("SE",content);
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//RM
	protected void startReceiveRM() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getRMport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("RM: "+content);
		                processSocketRequest("RM", content);
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println("Processed Sentence From Server");
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//Heartbeat
	protected void startReceiveHeartBeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
				while (true) {
					try {
						if (serverSocket[0] == null)
							serverSocket[0] = new ReliableServerSocket(getHBport());
					} catch (IOException e) {
						try {
							Thread.sleep(10000);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}
						continue;
					}
					try {
						Socket connectionSocket = serverSocket[0].accept();
						while (true) {
							try {
								BufferedReader inFromClient = new BufferedReader(
										new InputStreamReader(connectionSocket.getInputStream()));
								String content = inFromClient.readLine();
								processSocketRequest("HB", content);
							} catch (Exception e) {
								System.out.println("Heartbeat readLine: " + e.getMessage());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println("Heartbeat accept: " + e.getMessage());
					}
				}
			}
		}).start();
	}
}