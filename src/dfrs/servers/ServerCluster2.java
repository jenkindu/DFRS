package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster2 extends BaseServerCluster {
	
	public static final String SERVER_HOST = Config.getServerHost2();
	//CORBA
	public static final String SERVER_MTL_CORBA_PORT = "9060";
	public static final String SERVER_WST_CORBA_PORT = "9061";
	public static final String SERVER_NDL_CORBA_PORT = "9062";
	//HEARTBEAT
//	public static final int RM_HEARTBEAT_MTL_PORT = 7221;
//	public static final int RM_HEARTBEAT_WST_PORT = 7222;
//	public static final int RM_HEARTBEAT_NDL_PORT = 7223;
	
	public static String getCorbaPort(String server) {
		if(SERVER_MTL.equals(server)) {
			return SERVER_MTL_CORBA_PORT;
		} else if(SERVER_WST.equals(server)) {
			return SERVER_WST_CORBA_PORT;
		} else if(SERVER_NDL.equals(server)) {
			return SERVER_NDL_CORBA_PORT;
		}
		return "";
	}
	
	private static ServerCluster2 rm;
	private String[] args;
	public ServerCluster2(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster2(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(CorbaServer.createServer(this.getClass(), servers[i], args));
		}
	}
}
