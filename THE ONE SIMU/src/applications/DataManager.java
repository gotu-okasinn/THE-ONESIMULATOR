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
 * �e�m�[�h����M�������b�Z�[�W��ۊǂ��A���L�����v�Z����B
 * 
 * @author ����
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//�󂯎�������b�Z�[�W�̔��M�҃��X�g
	
	public static Message Management(DTNHost host,Message msg) {
	 double share;
	
		MsgHostData.add(host.toString());
		share=(double)MsgHostData.size()*(double)100/(double)125;
		System.out.println(" ���L��:"+share+"%");
		
		return msg;
	}

}
