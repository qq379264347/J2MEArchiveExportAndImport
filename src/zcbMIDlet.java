import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

public class zcbMIDlet extends MIDlet implements CommandListener {
	private TextBox tb = new TextBox("存档转存", "点击菜单，进行相应操作。", 65535, TextField.UNEDITABLE);
	private Command c1 = new Command("获取信息", Command.OK, 1);
	private Command c2 = new Command("路径信息", Command.OK, 2);
	private Command c3 = new Command("保存文件", Command.OK, 3);
	private Command c4 = new Command("保存记录", Command.OK, 3);
	private Command c5 = new Command("关于程序", Command.OK, 3);
	private Command c6 = new Command("退出程序", Command.EXIT, 1);
	public Display display;
	private String[] names;
	public static String CR = "\r\n";
	public static String root = null; //最后一个磁盘根目录
	public static String filePath = null; //存储的文件绝对路径

	static {
		Enumeration emun = FileSystemRegistry.listRoots();
		while (emun.hasMoreElements()) {
			root = emun.nextElement().toString();
		}
		if (root.endsWith("/")) {
			filePath = "file:///" + root + "J2MECloud/";
		} else {
			filePath = "file:///" + root + "/J2MECloud/";
		}
	}

	public zcbMIDlet() {
		display = Display.getDisplay(this);
		tb.addCommand(c1);
		tb.addCommand(c2);
		tb.addCommand(c3);
		tb.addCommand(c4);
		tb.addCommand(c5);
		tb.addCommand(c6);
		tb.setCommandListener(this);
		display.setCurrent(tb);
	}

	protected void startApp() throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean u) throws MIDletStateChangeException {
	}

	private void exitApp() {
		try {
			destroyApp(true);
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
		notifyDestroyed();
	}

	public void setTextBox(String str) {
		System.out.println("setTextBox：" + str);
		this.tb.setString(str);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == tb) {
			if (c == c1) {
				System.out.println("1获取记录");
				getRms();
			} else if (c == c2) {
				System.out.println("2获取路径");
				setTextBox(filePath);
			} else if (c == c3) {
				System.out.println("3转存文件");
				try {
					saveFile("rms.txt");
				} catch (Exception e) {
					e.printStackTrace();
					setTextBox(e.getMessage());
				}
			} else if (c == c4) {
				System.out.println("4保存记录");
				try {
					saveRms("rms.txt");
				} catch (Exception e) {
					e.printStackTrace();
					setTextBox(e.getMessage());
				}
			} else if (c == c5) {
				setTextBox("本程序由'珍藏吧(zcb)'开发，编写的针对真机存档、手机模拟器存档、电脑KE模拟器存档导入导出的软件，也可以修改后导入存档，达到跨平台移植存档。\n\n原游戏保存记录后覆盖安装本软件，先读取存档，确保有记录时候，获取路径查看保存文件位置，点击转存文件即可保存文件。发送该文件到电脑KE打开，放到获取的路径内再点保存记录即可（记得先清空KE存档）。也可以直接手机端或电脑端修改后再次导入存档。\n\n在最后一个盘符根目录建立一个J2MECloud文件夹，以确保导出存档文件成功。导入存档时，记得先清空原存档再导入防止出错。\nhttps://github.com/qq379264347/J2MEArchiveExportAndImport\n\nqq379264347 (zcb) 2022.02.06");
			} else if (c == c6) {
				exitApp();
			}
		}
	}

	/**
	 * 1获取记录
	 */
	private void getRms() {
		String[] names = RecordStore.listRecordStores();
		this.names = names;
		if (this.names == null || this.names.length == 0) {
			setTextBox("无任何记录名称。");
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < this.names.length; i++) {
				sb.append(escape(this.names[i]) + "\n");
			}
			sb.delete(sb.length() - 1, sb.length());
			setTextBox(sb.toString());
		}
	}

	/**
	 * 转义字符串，\\、\r、\n转义
	 * @param string 不是null的原始字符串
	 * @return 转义后字符串
	 */
	private String escape(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuffer sb = new StringBuffer();
		char[] c1 = string.toCharArray();
		for (int i = 0; i < c1.length; i++) {
			switch (c1[i]) { //一个\变两个\，'\r'变\和r，'\n'变\和n
			case '\\':
				sb.append("\\\\");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			default:
				sb.append(c1[i]);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 转义字符串，\\、\r、\n转义
	 * @param string 不是null的转义后字符串
	 * @return 原始字符串
	 * @throws Exception 转义字符错误
	 */
	private String unEscape(String string) throws Exception {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuffer sb = new StringBuffer();
		char[] c1 = string.toCharArray();
		for (int i = 0; i < c1.length; i++) {
			//\\变\，\r变'\r'，\n变'\n'
			if (i < c1.length - 1) { //可以有下一个
				if (c1[i] == '\\') { //当前是转义\
					if (c1[i + 1] == '\\') {
						sb.append("\\");
					} else if (c1[i + 1] == 'r') {
						sb.append("\r");
					} else if (c1[i + 1] == 'n') {
						sb.append("\n");
					} else {
						throw new Exception("转义字符错误：" + c1[i] + c1[i + 1]);
					}
					i++; //下一个下标已使用，跳过
				} else { //当前是正常字符
					sb.append(c1[i]);
				}
			} else { //最后一个字符
				sb.append(c1[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * 读取文件保存到RMS记录
	 */
	private void saveRms(String name) throws Exception {
		if (filePath == null || filePath.length() == 0) {
			setTextBox("路径为空，请检查文件读写权限。");
			return;
		}
		String[] s1 = getFile(name);
		if (s1 == null || s1.length == 0 || (s1.length == 1 && (s1[0] == null || s1[0].length() == 0))) {
			setTextBox("文件为空，请检查。");
			return;
		}
		int nums = 0; //记录集总数量，每个记录集内又有N条记录（id号暂未处理）
		int index = 0; //0-当前行是记录名字，打开记录；1-当前行为记录信息长度，下一行为记录数据
		RecordStore rs = null;
		for (int i = 0; i < s1.length; i++) {
			if (i == 0) { //第一行，总记录集数量
				nums = Integer.parseInt(s1[0]); //貌似没啥用
				if (nums <= 0) {
					setTextBox("记录数量小于等于0，请检查文件。"); //有点用，就这一点，还有就是电脑能看咯
					break;
				}
				continue;
			}
			
			if ("zcb-closeRS".equals(s1[i])) { //该记录集内记录都已经处理，关闭记录集
				index = 0; //下一行是记录集名称
				if (rs != null) {
					rs.closeRecordStore();
					rs = null; //关闭记录
				}
				setTextBox(this.tb.getString() + CR + "已关闭记录");
				continue;
			}
			
			switch (index) {
			case 0: //当前行是记录名字，打开记录
				String rsName = unEscape(s1[i]); //unEscape用来修复名字含有\r、\n特殊字符的bug
				if (rs != null) {
					rs.closeRecordStore();
					rs = null; //关闭记录
				}
				rs = RecordStore.openRecordStore(rsName, true); //打开并创建记录
				
				setTextBox(this.tb.getString() + CR + "打开记录：" + rsName);
				index = 1;
				break;
			case 1: //当前行为记录信息长度，下一行为记录数据
				int dataLength = Integer.parseInt(s1[i]); //长度
				i++; //下一行
				String hex = s1[i]; //数据
				if (dataLength % 2 == 1) {
					setTextBox("长度值应该是偶数，请检查。");
					break;
				}
				if (hex.length() != dataLength) {
					setTextBox("长度值与数据长度不一致，请检查。");
					break;
				}
				byte[] b1 = new byte[dataLength >> 1];
				for (int j = 0; j < b1.length; j++) { //(j << 1)  (j << 1) + 1 --> byte
					b1[j] = hex2Byte(hex.substring(j << 1, (j << 1) + 2));
				}
				rs.addRecord(b1, 0, b1.length);
				//index = 1;
				break;
			default:
				break;
			}
		}
		setTextBox(this.tb.getString() + CR + "恭喜，记录保存完毕，请运行原版游戏。");
	}

	/**
	 * 获取文件内容字符串数组
	 * @param name
	 * @return
	 */
	private String[] getFile(String name) {
		String path = filePath + name;
		 FileConnection fc = null;
		 String[] s1 = null;
		    try {
		      fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);
		      if (!fc.exists()) {
		        fc.create();
		      }
		      InputStream is = fc.openInputStream();
		      DataInputStream dis = new DataInputStream(is);
		      int length = dis.available();
		      byte[] b1 = new byte[length];
		      dis.readFully(b1);
		      dis.close();
		      is.close();
		      String sss = new String(b1, "UTF-8");
		      s1 = chaifenstring(sss, CR);
		    } catch (IOException ex) {
		      ex.printStackTrace();
		      setTextBox(ex.getMessage());
		    }
		    return s1;
	}

	/**
	 * 读取记录存储到硬盘文件
	 */
	private void saveFile(String name) throws Exception {
		if (filePath == null || filePath.length() == 0) {
			setTextBox("路径为空，请检查文件读写权限。");
			return;
		}
		getRms();
		if (names == null || names.length == 0) {
			setTextBox("记录为空，请检查。");
			return;
		}
		String saveFileName = filePath + name;
		StringBuffer data = new StringBuffer();
		int nums = names.length; //所有记录集
		data.append(nums + CR); //记录数量
		for (int i = 0; i < names.length; i++) {
			data.append(escape(names[i]) + CR); //记录名称
			RecordStore rs = RecordStore.openRecordStore(names[i], false); //不创建模式打开
			RecordEnumeration re = rs.enumerateRecords(null, null, false); //遍历所有记录可用的id
			while (re.hasNextElement()) {
//				int id = re.nextRecordId();
//				data += id + CR; //记录id
				byte[] b1 = re.nextRecord();
				String s = byte2HexStr(b1);
				int length = s.length();
				data.append(length + CR); //记录长度
				data.append(s + CR); //记录内容
			}
			data.append("zcb-closeRS" + CR); //记录内容
			rs.closeRecordStore(); //关闭
		}
		saveFile(saveFileName, data.toString().getBytes("UTF-8"));
		setTextBox("保存成功，请查看文件：" + saveFileName);
	}
	
	  /**保存文件
	   * @path:路径
	   * @fileData:文件数据
	   * @return: 0:出现异常,1:保存成功
	   */
	  public static int saveFile(String path, byte[] fileData) {
	    FileConnection fc = null;
	    try {
	      fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);
	      if (!fc.exists()) {
	        fc.create();
	      }
	      OutputStream os = fc.openOutputStream();
	      os.write(fileData);
	      os.flush();
	      os.close();
	      fc.close();
	      return 1;
	    } catch (IOException ex) {
	      ex.printStackTrace();
	      return 0;
	    }
	  }

	/**
	 * byte[]转换成十六进制字符串
	 * 
	 * @param byte b byte值
	 * @return String 两位大写十六进制字符串
	 */
	public static String byte2HexStr(byte[] b1) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b1.length; i++) {
			sb.append(byte2HexStr(b1[i]));
		}
		return sb.toString();
	}

	/**
	 * byte转换成十六进制字符串
	 * 
	 * @param byte b byte值
	 * @return String 两位大写十六进制字符串
	 */
	public static String byte2HexStr(byte b) {
		String stmp = "";
		stmp = Integer.toHexString(b & 0xFF);
		return ((stmp.length() == 1) ? "0" + stmp : stmp).toUpperCase();
	}

	/**
	 * 将十六进制转化为十进制整数
	 * @param byte_hexstr 2位十六进制的byte值
	 */
	public static byte hex2Byte(String byte_hexstr) {
		return (byte)(Integer.parseInt(byte_hexstr, 16) & 0xFF); //int的话会负数报错
	}

	/**
	 * 拆分字符串方法，把s1按照s2分割，分割后存储在一维数组中返回，数组中的字符串已经没有s2字符串了。
	 * 若是两个连续的s2，将被拆分成空字符串""，即原字符串为s2 + "" + s2。
	 * 若是以s2开头或结尾，会先删除头尾更新s1，再拆分。
	 * @param s1 原始字符串
	 * @param s2 拆分字符串
	 * @return
	 */
	public static String[] chaifenstring(String s1, String s2) {
		if (s1 == null || s1.length() == 0) { //空或者null时候返回有一个空元素的一维数组
			return new String[]{""};
		}
		if (s1.endsWith(s2) || s1.startsWith(s2)) {
			//System.out.println("拆分字符串失败，要拆分的字符串不能以拆分字符串结尾，或开头！");
			//return null; //直接返回null可能会导致后续的错误，这里去除后缀再处理返回字符串数组。
			s1 = delStr(s1, s2); //s2结尾或开头，去掉，循环
		}
		
		Vector vc = new Vector();
		int m = 0;
		
		for (int i = s1.indexOf(s2); i < s1.length() && i != -1; i = s1.indexOf(s2, i + s2.length())) {
			String str = s1.substring(m, i);
			vc.addElement(str);
			m = i + s2.length();
		}
		vc.addElement(s1.substring(m));
		
		return vectorToString(vc);
	}

	/**
	 * Vector转String[]
	 * @param vector
	 * @return
	 */
	public static String[] vectorToString(Vector vector) {
		String as[] = new String[vector.size()];
		for (int i = 0; i < as.length; i++) {
			as[i] = (String) vector.elementAt(i);
		}
		return as; //返回一维字符串数组
	}

	/**
	 * 去除字符串str的所有s头与所有s尾
	 * @param str
	 * @param s
	 * @return
	 */
	public static String delStr(String str, String s) {
		while(str.startsWith(s)) str = delStrBefore(str, s); //s开头，去掉，循环
		while(str.endsWith(s)) str = delStrAfter(str, s); //s结尾，去掉，循环
		return str;
	}

	/**
	 * 去除字符串前
	 * @param str 要处理的字符串
	 * @param s 要去掉的开头字符串
	 * @return str去掉开头s后的字符串
	 */
	public static String delStrBefore(String str, String s) {
		//去除字符串str的s头，只能去掉一次。
		if(str.startsWith(s)) str = str.substring(s.length()); //s开头，去掉
		return str;
	}

	/**
	 * 去除字符串后
	 * @param str 要处理的字符串
	 * @param s 要去掉的结尾字符串
	 * @return str去掉结尾s后的字符串
	 */
	public static String delStrAfter(String str, String s) {
		//去除字符串str的s尾，只能去掉一次。
		if(str.endsWith(s)) str = str.substring(0, str.length() - s.length()); //s结尾，去掉
		return str;
	}
}
