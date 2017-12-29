package player;

import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;  
  
public class Encrypt  
{  
  
	public static final int SALT_SIZE = 10;
	
  /** 
   * 传入文本内容，返回 SHA-256 串 
   *  
   * @param strText 
   * @return 
   */  
  public String SHA256(final String strText)  
  {  
    return SHA(strText, "SHA-256");  
  }  
  
  /** 
   * 传入文本内容，返回 SHA-512 串 
   *  
   * @param strText 
   * @return 
   */  
  public String SHA512(final String strText)  
  {  
    return SHA(strText, "SHA-512");  
  }  
  
  /** 
   * 字符串 SHA 加密 
   *  
   * @param strSourceText 
   * @return 
   */  
  private String SHA(final String strText, final String strType)  
  {  
    // 返回值  
    String strResult = null;  
  
    // 是否是有效字符串  
    if (strText != null && strText.length() > 0)  
    {  
      try  
      {  
        // SHA 加密开始  
        // 创建加密对象 并傳入加密類型  
        MessageDigest messageDigest = MessageDigest.getInstance(strType);  
        // 传入要加密的字符串  
        messageDigest.update(strText.getBytes());  
        // 得到 byte 類型结果  
        byte byteBuffer[] = messageDigest.digest();  
  
        // 將 byte 轉換爲 string  
        StringBuffer strHexString = new StringBuffer();  
        // 遍歷 byte buffer  
        for (int i = 0; i < byteBuffer.length; i++)  
        {  
          String hex = Integer.toHexString(0xff & byteBuffer[i]);  
          if (hex.length() == 1)  
          {  
            strHexString.append('0');  
          }  
          strHexString.append(hex);  
        }  
        // 得到返回結果  
        strResult = strHexString.toString();  
      }  
      catch (NoSuchAlgorithmException e)  
      {  
        e.printStackTrace();  
      }  
    }  
  
    return strResult;  
  }  
  
  /*
   * 传进来一个password,进行加盐哈希加密处理，返回一个数组
   * 第一个为SHA256的密码，第二个为盐值
   */
  public String[] getPassword(String passInit) {
	  String[] strings = new String [2];
      SecureRandom random = new SecureRandom();
      char[] saltChar = new char [SALT_SIZE];
      for (int i = 0; i < SALT_SIZE; i++) {
    	  saltChar[i] = (char) (random.nextInt(75)+'0');
      }
      strings[1] = String.valueOf(saltChar);
      System.out.println("the salt:"+strings[1]);
      //System.out.println(strings[1].length());
      String newPass = passInit + strings[1]; //对原密码先进行加盐处理
      strings[0] = this.SHA256(newPass);
      System.out.println("stored password:"+strings[0]);
      //System.out.println(strings[0].length());
      return strings;
  }
  
  public static void main(String[] args) {
	  Encrypt en = new Encrypt();
	  String initPass = "123456";
	  System.out.println("initial password:"+initPass);
	  String[] passes = en.getPassword(initPass);
	  
	  System.out.println("\nanother turn,");
	  System.out.println("initial password:"+initPass);
	  passes = en.getPassword(initPass);
  }
}  
