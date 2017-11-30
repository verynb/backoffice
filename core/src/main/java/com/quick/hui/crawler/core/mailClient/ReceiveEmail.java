package com.quick.hui.crawler.core.mailClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import org.apache.commons.lang3.StringUtils;

public class ReceiveEmail {

  private MimeMessage mimeMessage = null;
  private String saveAttachPath = ""; // 附件下载后的存放目录
  private StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象
  private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

  public ReceiveEmail(MimeMessage mimeMessage) {
    this.mimeMessage = mimeMessage;
  }

  public String getSubject() throws MessagingException {
    String subject = "";
    try {
      subject = MimeUtility.decodeText(mimeMessage.getSubject());
      if (subject == null) {
        subject = "";
      }
    } catch (Exception exce) {
      exce.printStackTrace();
    }
    return subject;
  }

  public Long getSentDate() throws Exception {
    Date sentDate = mimeMessage.getSentDate();
    return sentDate.getTime();
  }

  public String getBodyText() {
    return bodyText.toString();
  }

  public void getMailContent(Part part) throws Exception {

    String contentType = part.getContentType();
    // 获得邮件的MimeType类型
//    System.out.println("邮件的MimeType类型: " + contentType);

    int nameIndex = contentType.indexOf("name");

    boolean conName = false;

    if (nameIndex != -1) {
      conName = true;
    }

    System.out.println("邮件内容的类型:　" + contentType);

    if (part.isMimeType("text/plain") && conName == false) {
      // text/plain 类型
      bodyText.append((String) part.getContent());

      String str = replaceBlank(part.getContent().toString());
      System.out.println("token====" + str);
      String token = str.substring(str.lastIndexOf("*") + 1, str.lastIndexOf("*") + 33);
      System.out.println("token====" + token);
    } else if (part.isMimeType("text/html") && conName == false) {
      // text/html 类型
      System.out.println("text====" + part.getContent());
      bodyText.append((String) part.getContent());
    } else if (part.isMimeType("multipart/alternative")) {
      Multipart multipart = (Multipart) part.getContent();
      int counts = multipart.getCount();
      for (int i = 0; i < counts; i++) {
        getMailContent(multipart.getBodyPart(i));
      }
    } else if (part.isMimeType("message/rfc822")) {
      getMailContent((Part) part.getContent());
    } else {
    }
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

  public String replaceBlank(String str) {
    String dest = "";
    if (str != null) {
      Pattern p = Pattern.compile("\\s*|\t|\r|\n");
      Matcher m = p.matcher(str);
      dest = m.replaceAll("");
    }
    return dest;
  }

  public static void main(String args[]) throws Exception {
    String host = "pop.163.com"; //
    String username = "wenfang@bookbitbtc.com"; //
    String password = "Wen13825769146"; //

    Properties props = new Properties();
    props.setProperty("mail.store.protocol", "pop3");       // 协议
    props.setProperty("mail.pop3.port", "110");             // 端口
    props.setProperty("mail.pop3.host", "pop3.mxhichina.com");    // pop3服务器
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
      if (StringUtils.isBlank(re.getSubject()) || !re.getSubject().equals("Token for your TRANSFER")) {
        continue;
      }
      System.out.println("subject:" + re.getSubject());
//      System.out.println("邮件　" + i + "　主题:　" + re.getSubject());
//      System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
//      System.out.println("邮件　" + i + "　是否需要回复:　" + re.getReplySign());
//      System.out.println("邮件　" + i + "　是否已读:　" + re.isNew());
     /* System.out.println("邮件　" + i + "　是否包含附件:　"
          + re.isContainAttach((Part) message[i]));
      System.out.println("邮件　" + i + "　发送人地址:　" + re.getFrom());
      System.out
          .println("邮件　" + i + "　收信人地址:　" + re.getMailAddress("to"));
      System.out.println("邮件　" + i + "　抄送:　" + re.getMailAddress("cc"));
      System.out.println("邮件　" + i + "　暗抄:　" + re.getMailAddress("bcc"));
      re.setDateFormat("yy年MM月dd日　HH:mm");
      System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
      System.out.println("邮件　" + i + "　邮件ID:　" + re.getMessageId());*/
      re.getMailContent((Part) message[i]);
//      System.out.println("邮件　" + i + "　正文内容:　\r\n" + re.getBodyText());
//      re.setAttachPath("e:\\");
//      re.saveAttachMent((Part) message[i]);
    }
  }
}