package applications;


import core.DTNHost;
import core.Message;
import core.SimClock;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *各ノードが受信したメッセージを保管し、共有率を計算する。
 * 
 * @author gotsu
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//受信ホストリスト
	
	/**
	 *各ノードが受信したメッセージを保管し、共有率を計算する。
	 * 
	 * @author ごつ
	 * @param host&msg
	 * @return msg
	 */
	public static Message Management(DTNHost host,Message msg) {
	   double share;//共有率
	   if(!(MsgHostData.contains(host.toString()))){
		   MsgHostData.add(host.toString());
		   share=(double)MsgHostData.size()*(double)100/(double)(PingApplication.destMax);
		   try {
			   FileWriter fw = new FileWriter("result/share.txt",true);
					//テキストファイルに２列で表示するためにStringBufferを形成後に結合
			   StringBuilder sb =new StringBuilder(String.valueOf(SimClock.getIntTime()));
			   sb.append("     "+share);
		
		   			//Stringに変換し、書き込み
			   fw.write(sb.toString());
			   fw.write("\n");
			   fw.close();
			
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   
		  
	    } 
	   
	   share=(double)MsgHostData.size()*(double)100/(double)(PingApplication.destMax);
	   System.out.println("　共有率:"+share+"%");
	   
		return msg;
		
	}
	

}



