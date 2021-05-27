package applications;

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
 * 各ノードが受信したメッセージを保管し、共有率を計算する。
 * 
 * @author ごつ
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//受け取ったメッセージの発信者リスト
	
	public static Message Management(DTNHost host,Message msg) {
	 double share;
	
		MsgHostData.add(host.toString());
		share=(double)MsgHostData.size()*(double)100/(double)125;
		System.out.println(" 共有率:"+share+"%");
		
		return msg;
	}

}
