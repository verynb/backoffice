package com.quick.hui.crawler.core.mailClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class ReceiveEmail {

  private MimeMessage mimeMessage = null;
  private String saveAttachPath = ""; // 附件下载后的存放目录
  private StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象
  private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

  public ReceiveEmail(MimeMessage mimeMessage) {
    this.mimeMessage = mimeMessage;
    System.out.println("创建一个ReceiveEmail对象....");
  }

  public void setMimeMessage(MimeMessage mimeMessage) {
    this.mimeMessage = mimeMessage;
    System.out.println("设置一个MimeMessage对象...");
  }


  public String getFrom() throws Exception {
    InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
    String from = address[0].getAddress();
    if (from == null) {
      from = "";
      System.out.println("无法知道发送者.");
    }
    String personal = address[0].getPersonal();

    if (personal == null) {
      personal = "";
      System.out.println("无法知道发送者的姓名.");
    }

    String fromAddr = null;
    if (personal != null || from != null) {
      fromAddr = personal + "<" + from + ">";
      System.out.println("发送者是：" + fromAddr);
    } else {
      System.out.println("无法获得发送者信息.");
    }
    return fromAddr;
  }


  public String getMailAddress(String type) throws Exception {
    String mailAddr = "";
    String addType = type.toUpperCase();

    InternetAddress[] address = null;
    if (addType.equals("TO") || addType.equals("CC")
        || addType.equals("BCC")) {

      if (addType.equals("TO")) {
        address = (InternetAddress[]) mimeMessage
            .getRecipients(Message.RecipientType.TO);
      } else if (addType.equals("CC")) {
        address = (InternetAddress[]) mimeMessage
            .getRecipients(Message.RecipientType.CC);
      } else {
        address = (InternetAddress[]) mimeMessage
            .getRecipients(Message.RecipientType.BCC);
      }

      if (address != null) {
        for (int i = 0; i < address.length; i++) {
          String emailAddr = address[i].getAddress();
          if (emailAddr == null) {
            emailAddr = "";
          } else {
            System.out.println("转换之前的emailAddr: " + emailAddr);
            emailAddr = MimeUtility.decodeText(emailAddr);
            System.out.println("转换之后的emailAddr: " + emailAddr);
          }
          String personal = address[i].getPersonal();
          if (personal == null) {
            personal = "";
          } else {
            System.out.println("转换之前的personal: " + personal);
            personal = MimeUtility.decodeText(personal);
            System.out.println("转换之后的personal: " + personal);
          }
          String compositeto = personal + "<" + emailAddr + ">";
          System.out.println("完整的邮件地址：" + compositeto);
          mailAddr += "," + compositeto;
        }
        mailAddr = mailAddr.substring(1);
      }
    } else {
      throw new Exception("错误的电子邮件类型!");
    }
    return mailAddr;
  }


  public String getSubject() throws MessagingException {
    String subject = "";
    try {
      System.out.println("转换前的subject：" + mimeMessage.getSubject());
      subject = MimeUtility.decodeText(mimeMessage.getSubject());
      System.out.println("转换后的subject: " + mimeMessage.getSubject());
      if (subject == null) {
        subject = "";
      }
    } catch (Exception exce) {
      exce.printStackTrace();
    }
    return subject;
  }


  public String getSentDate() throws Exception {
    Date sentDate = mimeMessage.getSentDate();
    System.out.println("发送日期 原始类型: " + dateFormat);
    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
    String strSentDate = format.format(sentDate);
    System.out.println("发送日期 可读类型: " + strSentDate);
    return strSentDate;
  }


  public String getBodyText() {
    return bodyText.toString();
  }


  public void getMailContent(Part part) throws Exception {

    String contentType = part.getContentType();
    // 获得邮件的MimeType类型
    System.out.println("邮件的MimeType类型: " + contentType);

    int nameIndex = contentType.indexOf("name");

    boolean conName = false;

    if (nameIndex != -1) {
      conName = true;
    }

    System.out.println("邮件内容的类型:　" + contentType);

    if (part.isMimeType("text/plain") && conName == false) {
      // text/plain 类型
      bodyText.append((String) part.getContent());
    } else if (part.isMimeType("text/html") && conName == false) {
      // text/html 类型
      bodyText.append((String) part.getContent());
    } else if (part.isMimeType("multipart")) {

    }
  }

  public boolean getReplySign() throws MessagingException {

    boolean replySign = false;

    String needReply[] = mimeMessage
        .getHeader("Disposition-Notification-To");

    if (needReply != null) {
      replySign = true;
    }
    if (replySign) {
      System.out.println("该邮件需要回复");
    } else {
      System.out.println("该邮件不需要回复");
    }
    return replySign;
  }


  public String getMessageId() throws MessagingException {
    String messageID = mimeMessage.getMessageID();
    System.out.println("邮件ID: " + messageID);
    return messageID;
  }


  public boolean isNew() throws MessagingException {
    boolean isNew = false;
    Flags flags = ((Message) mimeMessage).getFlags();
    Flags.Flag[] flag = flags.getSystemFlags();
    System.out.println("flags的长度:　" + flag.length);
    for (int i = 0; i < flag.length; i++) {
      if (flag[i] == Flags.Flag.SEEN) {
        isNew = true;
        System.out.println("seen email...");
        // break;
      }
    }
    return isNew;
  }


  public boolean isContainAttach(Part part) throws Exception {
    boolean attachFlag = false;
    // String contentType = part.getContentType();
    if (part.isMimeType("multipart")) {

    }
    return true;
  }

  public void saveAttachMent(Part part) throws Exception {
    String fileName = "";
    if (part.isMimeType("multipart")){

    }
  }
    public void setAttachPath (String attachPath){
      this.saveAttachPath = attachPath;
    }

  public void setDateFormat(String format) throws Exception {
    this.dateFormat = format;
  }

  public String getAttachPath() {
    return saveAttachPath;
  }

  private void saveFile(String fileName, InputStream in) throws Exception {
    String osName = System.getProperty("os.name");
    String storeDir = getAttachPath();
    String separator = "";
    if (osName == null) {
      osName = "";
    }
    if (osName.toLowerCase().indexOf("win") != -1) {
      separator = "\\";
      if (storeDir == null || storeDir.equals("")) {
        storeDir = "c:\\tmp";
      }
    } else {
      separator = "/";
      storeDir = "/tmp";
    }
    File storeFile = new File(storeDir + separator + fileName);
    System.out.println("附件的保存地址:　" + storeFile.toString());
    // for(int　i=0;storefile.exists();i++){
    // storefile　=　new　File(storedir+separator+fileName+i);
    // }
    BufferedOutputStream bos = null;
    BufferedInputStream bis = null;

    try {
      bos = new BufferedOutputStream(new FileOutputStream(storeFile));
      bis = new BufferedInputStream(in);
      int c;
      while ((c = bis.read()) != -1) {
        bos.write(c);
        bos.flush();
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new Exception("文件保存失败!");
    } finally {
      bos.close();
      bis.close();
    }
  }


  public static void main(String args[]) throws Exception {
    String host = "pop.163.com"; //
    String username = "m15928594217_2@163.com"; //
    String password = "yuanjiang123"; //

    Properties props = new Properties();
    props.setProperty("mail.store.protocol", "pop3");       // 协议
    props.setProperty("mail.pop3.port", "110");             // 端口
    props.setProperty("mail.pop3.host", "pop3.163.com");    // pop3服务器
    Session session = Session.getInstance(props);

    Store store = session.getStore("pop3");
    store.connect(username, password);

    Folder folder = store.getFolder("INBOX");
    folder.open(Folder.READ_ONLY);
    Message message[] = folder.getMessages();
    System.out.println("邮件数量:　" + message.length);
    ReceiveEmail re = null;

    for (int i = 0; i < message.length; i++) {
      re = new ReceiveEmail((MimeMessage) message[i]);
      System.out.println("邮件　" + i + "　主题:　" + re.getSubject());
      System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
      System.out.println("邮件　" + i + "　是否需要回复:　" + re.getReplySign());
      System.out.println("邮件　" + i + "　是否已读:　" + re.isNew());
      System.out.println("邮件　" + i + "　是否包含附件:　"
          + re.isContainAttach((Part) message[i]));
      System.out.println("邮件　" + i + "　发送人地址:　" + re.getFrom());
      System.out
          .println("邮件　" + i + "　收信人地址:　" + re.getMailAddress("to"));
      System.out.println("邮件　" + i + "　抄送:　" + re.getMailAddress("cc"));
      System.out.println("邮件　" + i + "　暗抄:　" + re.getMailAddress("bcc"));
      re.setDateFormat("yy年MM月dd日　HH:mm");
      System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
      System.out.println("邮件　" + i + "　邮件ID:　" + re.getMessageId());
      re.getMailContent((Part) message[i]);
      System.out.println("邮件　" + i + "　正文内容:　\r\n" + re.getBodyText());
      re.setAttachPath("e:\\");
      re.saveAttachMent((Part) message[i]);
    }
  }
}