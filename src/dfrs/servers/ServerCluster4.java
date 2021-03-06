package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster4 extends BaseServerCluster {
	
	public static final String CORBA = "4";
	public static final String SERVER_HOST = Config.getServerHost4();
	//CORBA
	public static final String SERVER_CORBA_PORT = "1050";//"9080";
	//RM COMMAND
	public static final int SC_RECEIVE_RM_PROT = 7340;
	
	private static ServerCluster4 rm;
	private String[] args;
	public ServerCluster4(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster4(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
		rm.startReceiveRM();
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(CorbaServer.createServer(this.getClass(), servers[i], args, i));
		}
	}
	
	@Override
	protected int getSCport() {
		return SC_RECEIVE_RM_PROT;
	}
	
	@Override
	protected String getCorba() {
		return CORBA;
	}
}
