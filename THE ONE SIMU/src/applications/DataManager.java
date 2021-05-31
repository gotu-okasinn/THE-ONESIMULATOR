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
 *各ノードが受信したメッセージを保管し、共有率を計算する。
 * 
 * @author gotsu
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//��M�҃z�X�g�̃��X�g
	
	public static Message Management(DTNHost host,Message msg) {
	 double share;//共有率
	
		MsgHostData.add(host.toString());
		share=(double)MsgHostData.size()*(double)100/(double)125;//���X�g��String�ɕϊ��A��","���폜�B
		System.out.println("共有率:"+share+"%");
		
		return msg;
	}

}
