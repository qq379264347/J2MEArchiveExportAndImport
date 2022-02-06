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
	private TextBox tb = new TextBox("�浵ת��", "����˵���������Ӧ������", 65535, TextField.UNEDITABLE);
	private Command c1 = new Command("��ȡ��Ϣ", Command.OK, 1);
	private Command c2 = new Command("·����Ϣ", Command.OK, 2);
	private Command c3 = new Command("�����ļ�", Command.OK, 3);
	private Command c4 = new Command("�����¼", Command.OK, 3);
	private Command c5 = new Command("���ڳ���", Command.OK, 3);
	private Command c6 = new Command("�˳�����", Command.EXIT, 1);
	public Display display;
	private String[] names;
	public static String CR = "\r\n";
	public static String root = null; //���һ�����̸�Ŀ¼
	public static String filePath = null; //�洢���ļ�����·��

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
		System.out.println("setTextBox��" + str);
		this.tb.setString(str);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == tb) {
			if (c == c1) {
				System.out.println("1��ȡ��¼");
				getRms();
			} else if (c == c2) {
				System.out.println("2��ȡ·��");
				setTextBox(filePath);
			} else if (c == c3) {
				System.out.println("3ת���ļ�");
				try {
					saveFile("rms.txt");
				} catch (Exception e) {
					e.printStackTrace();
					setTextBox(e.getMessage());
				}
			} else if (c == c4) {
				System.out.println("4�����¼");
				try {
					saveRms("rms.txt");
				} catch (Exception e) {
					e.printStackTrace();
					setTextBox(e.getMessage());
				}
			} else if (c == c5) {
				setTextBox("��������'��ذ�(zcb)'��������д���������浵���ֻ�ģ�����浵������KEģ�����浵���뵼���������Ҳ�����޸ĺ���浵���ﵽ��ƽ̨��ֲ�浵��\n\nԭ��Ϸ�����¼�󸲸ǰ�װ��������ȶ�ȡ�浵��ȷ���м�¼ʱ�򣬻�ȡ·���鿴�����ļ�λ�ã����ת���ļ����ɱ����ļ������͸��ļ�������KE�򿪣��ŵ���ȡ��·�����ٵ㱣���¼���ɣ��ǵ������KE�浵����Ҳ����ֱ���ֻ��˻���Զ��޸ĺ��ٴε���浵��\n\n�����һ���̷���Ŀ¼����һ��J2MECloud�ļ��У���ȷ�������浵�ļ��ɹ�������浵ʱ���ǵ������ԭ�浵�ٵ����ֹ����\nhttps://github.com/qq379264347/J2MEArchiveExportAndImport\n\nqq379264347 (zcb) 2022.02.06");
			} else if (c == c6) {
				exitApp();
			}
		}
	}

	/**
	 * 1��ȡ��¼
	 */
	private void getRms() {
		String[] names = RecordStore.listRecordStores();
		this.names = names;
		if (this.names == null || this.names.length == 0) {
			setTextBox("���κμ�¼���ơ�");
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
	 * ת���ַ�����\\��\r��\nת��
	 * @param string ����null��ԭʼ�ַ���
	 * @return ת����ַ���
	 */
	private String escape(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuffer sb = new StringBuffer();
		char[] c1 = string.toCharArray();
		for (int i = 0; i < c1.length; i++) {
			switch (c1[i]) { //һ��\������\��'\r'��\��r��'\n'��\��n
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
	 * ת���ַ�����\\��\r��\nת��
	 * @param string ����null��ת����ַ���
	 * @return ԭʼ�ַ���
	 * @throws Exception ת���ַ�����
	 */
	private String unEscape(String string) throws Exception {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuffer sb = new StringBuffer();
		char[] c1 = string.toCharArray();
		for (int i = 0; i < c1.length; i++) {
			//\\��\��\r��'\r'��\n��'\n'
			if (i < c1.length - 1) { //��������һ��
				if (c1[i] == '\\') { //��ǰ��ת��\
					if (c1[i + 1] == '\\') {
						sb.append("\\");
					} else if (c1[i + 1] == 'r') {
						sb.append("\r");
					} else if (c1[i + 1] == 'n') {
						sb.append("\n");
					} else {
						throw new Exception("ת���ַ�����" + c1[i] + c1[i + 1]);
					}
					i++; //��һ���±���ʹ�ã�����
				} else { //��ǰ�������ַ�
					sb.append(c1[i]);
				}
			} else { //���һ���ַ�
				sb.append(c1[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�ļ����浽RMS��¼
	 */
	private void saveRms(String name) throws Exception {
		if (filePath == null || filePath.length() == 0) {
			setTextBox("·��Ϊ�գ������ļ���дȨ�ޡ�");
			return;
		}
		String[] s1 = getFile(name);
		if (s1 == null || s1.length == 0 || (s1.length == 1 && (s1[0] == null || s1[0].length() == 0))) {
			setTextBox("�ļ�Ϊ�գ����顣");
			return;
		}
		int nums = 0; //��¼����������ÿ����¼��������N����¼��id����δ����
		int index = 0; //0-��ǰ���Ǽ�¼���֣��򿪼�¼��1-��ǰ��Ϊ��¼��Ϣ���ȣ���һ��Ϊ��¼����
		RecordStore rs = null;
		for (int i = 0; i < s1.length; i++) {
			if (i == 0) { //��һ�У��ܼ�¼������
				nums = Integer.parseInt(s1[0]); //ò��ûɶ��
				if (nums <= 0) {
					setTextBox("��¼����С�ڵ���0�������ļ���"); //�е��ã�����һ�㣬���о��ǵ����ܿ���
					break;
				}
				continue;
			}
			
			if ("zcb-closeRS".equals(s1[i])) { //�ü�¼���ڼ�¼���Ѿ������رռ�¼��
				index = 0; //��һ���Ǽ�¼������
				if (rs != null) {
					rs.closeRecordStore();
					rs = null; //�رռ�¼
				}
				setTextBox(this.tb.getString() + CR + "�ѹرռ�¼");
				continue;
			}
			
			switch (index) {
			case 0: //��ǰ���Ǽ�¼���֣��򿪼�¼
				String rsName = unEscape(s1[i]); //unEscape�����޸����ֺ���\r��\n�����ַ���bug
				if (rs != null) {
					rs.closeRecordStore();
					rs = null; //�رռ�¼
				}
				rs = RecordStore.openRecordStore(rsName, true); //�򿪲�������¼
				
				setTextBox(this.tb.getString() + CR + "�򿪼�¼��" + rsName);
				index = 1;
				break;
			case 1: //��ǰ��Ϊ��¼��Ϣ���ȣ���һ��Ϊ��¼����
				int dataLength = Integer.parseInt(s1[i]); //����
				i++; //��һ��
				String hex = s1[i]; //����
				if (dataLength % 2 == 1) {
					setTextBox("����ֵӦ����ż�������顣");
					break;
				}
				if (hex.length() != dataLength) {
					setTextBox("����ֵ�����ݳ��Ȳ�һ�£����顣");
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
		setTextBox(this.tb.getString() + CR + "��ϲ����¼������ϣ�������ԭ����Ϸ��");
	}

	/**
	 * ��ȡ�ļ������ַ�������
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
	 * ��ȡ��¼�洢��Ӳ���ļ�
	 */
	private void saveFile(String name) throws Exception {
		if (filePath == null || filePath.length() == 0) {
			setTextBox("·��Ϊ�գ������ļ���дȨ�ޡ�");
			return;
		}
		getRms();
		if (names == null || names.length == 0) {
			setTextBox("��¼Ϊ�գ����顣");
			return;
		}
		String saveFileName = filePath + name;
		StringBuffer data = new StringBuffer();
		int nums = names.length; //���м�¼��
		data.append(nums + CR); //��¼����
		for (int i = 0; i < names.length; i++) {
			data.append(escape(names[i]) + CR); //��¼����
			RecordStore rs = RecordStore.openRecordStore(names[i], false); //������ģʽ��
			RecordEnumeration re = rs.enumerateRecords(null, null, false); //�������м�¼���õ�id
			while (re.hasNextElement()) {
//				int id = re.nextRecordId();
//				data += id + CR; //��¼id
				byte[] b1 = re.nextRecord();
				String s = byte2HexStr(b1);
				int length = s.length();
				data.append(length + CR); //��¼����
				data.append(s + CR); //��¼����
			}
			data.append("zcb-closeRS" + CR); //��¼����
			rs.closeRecordStore(); //�ر�
		}
		saveFile(saveFileName, data.toString().getBytes("UTF-8"));
		setTextBox("����ɹ�����鿴�ļ���" + saveFileName);
	}
	
	  /**�����ļ�
	   * @path:·��
	   * @fileData:�ļ�����
	   * @return: 0:�����쳣,1:����ɹ�
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
	 * byte[]ת����ʮ�������ַ���
	 * 
	 * @param byte b byteֵ
	 * @return String ��λ��дʮ�������ַ���
	 */
	public static String byte2HexStr(byte[] b1) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b1.length; i++) {
			sb.append(byte2HexStr(b1[i]));
		}
		return sb.toString();
	}

	/**
	 * byteת����ʮ�������ַ���
	 * 
	 * @param byte b byteֵ
	 * @return String ��λ��дʮ�������ַ���
	 */
	public static String byte2HexStr(byte b) {
		String stmp = "";
		stmp = Integer.toHexString(b & 0xFF);
		return ((stmp.length() == 1) ? "0" + stmp : stmp).toUpperCase();
	}

	/**
	 * ��ʮ������ת��Ϊʮ��������
	 * @param byte_hexstr 2λʮ�����Ƶ�byteֵ
	 */
	public static byte hex2Byte(String byte_hexstr) {
		return (byte)(Integer.parseInt(byte_hexstr, 16) & 0xFF); //int�Ļ��Ḻ������
	}

	/**
	 * ����ַ�����������s1����s2�ָ�ָ��洢��һά�����з��أ������е��ַ����Ѿ�û��s2�ַ����ˡ�
	 * ��������������s2��������ֳɿ��ַ���""����ԭ�ַ���Ϊs2 + "" + s2��
	 * ������s2��ͷ���β������ɾ��ͷβ����s1���ٲ�֡�
	 * @param s1 ԭʼ�ַ���
	 * @param s2 ����ַ���
	 * @return
	 */
	public static String[] chaifenstring(String s1, String s2) {
		if (s1 == null || s1.length() == 0) { //�ջ���nullʱ�򷵻���һ����Ԫ�ص�һά����
			return new String[]{""};
		}
		if (s1.endsWith(s2) || s1.startsWith(s2)) {
			//System.out.println("����ַ���ʧ�ܣ�Ҫ��ֵ��ַ��������Բ���ַ�����β����ͷ��");
			//return null; //ֱ�ӷ���null���ܻᵼ�º����Ĵ�������ȥ����׺�ٴ������ַ������顣
			s1 = delStr(s1, s2); //s2��β��ͷ��ȥ����ѭ��
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
	 * VectorתString[]
	 * @param vector
	 * @return
	 */
	public static String[] vectorToString(Vector vector) {
		String as[] = new String[vector.size()];
		for (int i = 0; i < as.length; i++) {
			as[i] = (String) vector.elementAt(i);
		}
		return as; //����һά�ַ�������
	}

	/**
	 * ȥ���ַ���str������sͷ������sβ
	 * @param str
	 * @param s
	 * @return
	 */
	public static String delStr(String str, String s) {
		while(str.startsWith(s)) str = delStrBefore(str, s); //s��ͷ��ȥ����ѭ��
		while(str.endsWith(s)) str = delStrAfter(str, s); //s��β��ȥ����ѭ��
		return str;
	}

	/**
	 * ȥ���ַ���ǰ
	 * @param str Ҫ������ַ���
	 * @param s Ҫȥ���Ŀ�ͷ�ַ���
	 * @return strȥ����ͷs����ַ���
	 */
	public static String delStrBefore(String str, String s) {
		//ȥ���ַ���str��sͷ��ֻ��ȥ��һ�Ρ�
		if(str.startsWith(s)) str = str.substring(s.length()); //s��ͷ��ȥ��
		return str;
	}

	/**
	 * ȥ���ַ�����
	 * @param str Ҫ������ַ���
	 * @param s Ҫȥ���Ľ�β�ַ���
	 * @return strȥ����βs����ַ���
	 */
	public static String delStrAfter(String str, String s) {
		//ȥ���ַ���str��sβ��ֻ��ȥ��һ�Ρ�
		if(str.endsWith(s)) str = str.substring(0, str.length() - s.length()); //s��β��ȥ��
		return str;
	}
}
