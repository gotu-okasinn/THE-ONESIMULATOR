package applications;


import core.DTNHost;
import core.Message;
import java.util.List;
import java.util.ArrayList;
import applications.PingApplication;

/**
 *各ノードが受信したメッセージを保管し、共有率を計算する。
 * 
 * @author gotsu
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//受信ホストリスト
	
	public static Message Management(DTNHost host,Message msg) {
	 double share;//共有率
	
	   
		MsgHostData.add(host.toString());
		share=(double)MsgHostData.size()*(double)100/(double)(PingApplication.destMax-1);
		System.out.println("　共有率:"+share+"%");
		
		return msg;
	}

}
