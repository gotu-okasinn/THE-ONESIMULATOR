/*
* Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
/*
*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */

package applications;

import java.util.Random;

import report.PingAppReporter;
import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import core.World;
import java.util.List;
import java.util.ArrayList;
/**
 * Simple ping application to demonstrate the application support. The
 * application can be configured to send pings with a fixed interval or to only
 * answer to pings it receives. When the application receives a ping it sends
 * a pong message in response.
 *
 * The corresponding <code>PingAppReporter</code> class can be used to record
 * information about the application behavior.
 *
 * @see PingAppReporter
 * @author teemuk
 */
public class PingApplication extends Application {
	/** Run in passive mode - don't generate pings but respond */
	public static final String PING_PASSIVE = "passive";
	/** Ping generation interval */
	public static final String PING_INTERVAL = "interval";
	/** Ping interval offset - avoids synchronization of ping sending */
	public static final String PING_OFFSET = "offset";
	/** Destination address range - inclusive lower, exclusive upper */
	public static final String PING_DEST_RANGE = "destinationRange";
	/** Seed for the app's random number generator */
	public static final String PING_SEED = "seed";
	/** Size of the ping message */
	public static final String PING_PING_SIZE = "pingSize";
	/** Size of the pong message */
	public static final String PING_PONG_SIZE = "pongSize";

	/** Application ID */
	public static final String APP_ID = "gototest";

	// Private vars
	private double	lastPing = 0;
	private double	interval = 200;
	private boolean passive = false;
	private int		seed = 10;
	private int		destMin=0;
	private int		destMax=1;
	private int		pingSize=1;
	private int		pongSize=1;
	private Random	rng;
	private int i=0;
	private List<String> sharenode =new ArrayList<String>();
	private List<String> data =new ArrayList<String>();
	private double share;

   // private int share;
	/**
	 * Creates a new ping application with the given settings.
	 *
	 * @param s	Settings to use for initializing the application.
	 */
	public PingApplication(Settings s) {
		if (s.contains(PING_PASSIVE)){
			this.passive = s.getBoolean(PING_PASSIVE);
		}
		if (s.contains(PING_INTERVAL)){
			this.interval = s.getDouble(PING_INTERVAL);
		}
		if (s.contains(PING_OFFSET)){
			this.lastPing = s.getDouble(PING_OFFSET);
		}
		if (s.contains(PING_SEED)){
			this.seed = s.getInt(PING_SEED);
		}
		if (s.contains(PING_PING_SIZE)) {
			this.pingSize = s.getInt(PING_PING_SIZE);
		}
		if (s.contains(PING_PONG_SIZE)) {
			this.pongSize = s.getInt(PING_PONG_SIZE);
		}
		if (s.contains(PING_DEST_RANGE)){
			int[] destination = s.getCsvInts(PING_DEST_RANGE,2);
			this.destMin = destination[0];
			this.destMax = destination[1];
		}

		rng = new Random(this.seed);
		super.setAppID(APP_ID);
	}

	/**
	 * Copy-constructor
	 *
	 * @param a
	 */
	public PingApplication(PingApplication a) {
		super(a);
		this.lastPing = a.getLastPing();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destMax = a.getDestMax();
		this.destMin = a.getDestMin();
		this.seed = a.getSeed();
		this.pongSize = a.getPongSize();
		this.pingSize = a.getPingSize();
		this.rng = new Random(this.seed);
	}

	/**
	 * Handles an incoming message. If the message is a ping message replies
	 * with a pong message. Generates events for ping and pong messages.
	 *
	 * @param msg	message received by the router
	 * @param host	host to which the application instance is attached
	 */
	@Override
	public Message handle(Message msg, DTNHost host) {

		/*if(!this.sharenode.contains(host)) {
			this.sharenode.add(host.toString());
			//System.out.println(this.sharenode);
		}*/
		this.sharenode.add(host.toString());
		share=(double)sharenode.size()/(double)125;

		 System.out.println("データの送り先:"+msg.getTo()+" 受け取ったホスト:"+host+" 受け取った時間:"+SimClock.getIntTime()
		 +"　共有率"+share+"%"+sharenode);
		String type = (String)msg.getProperty("type");//どのノードでも作成した情報を自分が持てばこの操作に行きつく。
		//System.out.println(host+"が"+msg.getFrom()+"からgototestを受け取りました。");

		if (type==null)
				return msg; // Not a ping/pong message

		if(msg.getTo()==host&&type.equalsIgnoreCase("pong"){
			DataManage(host,msg);
			return msg;
		}
		// Respond with pong if we're the recipient
		if (msg.getTo()==host && type.equalsIgnoreCase("gototest")) {
			//String id = "pong" + SimClock.getIntTime() + "-" +
			//	host.getAddress();
			//Message m = new Message(host, msg.getFrom(), id, getPongSize());
			//m.addProperty("type", "pong");
			System.out.println(host+"は"+msg.getFrom()+"からgototestを受信。 時間："+SimClock.getIntTime());
			Message m= new Message(host,msg.getFrom(),"gotoreturn",data.size());//メッセージを受け取ったら、送信元に報告
			m.addProperty("type","pong");
			host.createNewMessage(m);
			//m.setAppID(APP_ID);
			//host.createNewMessage(m);

			// Send event to listeners
			super.sendEventToListeners("GotPing", null, host);
			super.sendEventToListeners("SentPong", null, host);
		}
		

		// Received a pong reply
		//if (msg.getTo()==host && type.equalsIgnoreCase("pong")) {
			//System.out.println(host+"は"+msg.getFrom()+"からpongを受信");
			// Send event to listeners
			//super.sendEventToListeners("GotPong", null, host);
		//}

		return msg;
	}

	/**
	 * Draws a random host from the destination range
	 *
	 * @return host
	 */
	private DTNHost randomHost() {
		int destaddr = 0;
		if (destMax == destMin) {
			destaddr = destMin;
		}
		destaddr = destMin + rng.nextInt(destMax - destMin);
		World w = SimScenario.getInstance().getWorld();
		//System.out.println(destaddr+"変換"+w.getNodeByAddress(destaddr));

		return w.getNodeByAddress(destaddr);
	}

	@Override
	public Application replicate() {
		return new PingApplication(this);
	}

	/**
	 * Sends a ping packet if this is an active application instance.
	 *
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		if (this.passive) return;

		 double curTime = SimClock.getTime();
		if (curTime - this.lastPing >= this.interval) {

				this.DataSend(host);

			// Call listeners
			super.sendEventToListeners("SentPing", null, host);

			this.lastPing = curTime;
		}
	}

	public void DataSend(DTNHost host) {



		int srsaddrs=0;
		World w = SimScenario.getInstance().getWorld();
		if(host.address==srsaddrs&&i==0) {
		Message m = new Message(host, randomHost(), "gototest",data.size());
				/*+SimClock.getIntTime() + "-" + w.getNodeByAddress(srsaddrs).getAddress()*/
		//System.out.println(randomHost().address);

		m.addProperty("type", "gototest");
		m.setAppID(APP_ID);

		System.out.println(host+"はデータを"+m.getTo()+"あてに送りました。時間は"+SimClock.getIntTime()+"　サイズ："+data.size());

		host.createNewMessage(m);
		i++;

		// Call listeners
		super.sendEventToListeners("SentPing", null, host);

		}

	}



/**
		// Call listeners
		super.sendEventToListeners("SentPing", null, host);
		}
	}
	/**
	 * @return the lastPing
	 */
	public double getLastPing() {
		return lastPing;
	}


	/**
	 * @param lastPing the lastPing to set
	 */
	public void setLastPing(double lastPing) {
		this.lastPing = lastPing;
	}

	/**
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

	/**
	 * @return the passive
	 */
	public boolean isPassive() {
		return passive;
	}

	/**
	 * @param passive the passive to set
	 */
	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * @return the destMin
	 */
	public int getDestMin() {
		return destMin;
	}

	/**
	 * @param destMin the destMin to set
	 */
	public void setDestMin(int destMin) {
		this.destMin = destMin;
	}

	/**
	 * @return the destMax
	 */
	public int getDestMax() {
		return destMax;
	}

	/**
	 * @param destMax the destMax to set
	 */
	public void setDestMax(int destMax) {
		this.destMax = destMax;
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the pongSize
	 */
	public int getPongSize() {
		return pongSize;
	}

	/**
	 * @param pongSize the pongSize to set
	 */
	public void setPongSize(int pongSize) {
		this.pongSize = pongSize;
	}

	/**
	 * @return the pingSize
	 */
	public int getPingSize() {
		return pingSize;
	}

	/**
	 * @param pingSize the pingSize to set
	 */
	public void setPingSize(int pingSize) {
		this.pingSize = pingSize;
	}

}
