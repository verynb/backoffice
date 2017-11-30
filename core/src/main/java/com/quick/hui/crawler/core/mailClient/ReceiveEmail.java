package com.quick.hui.crawler.core.mailClient;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class ReceiveEmail {


  private MimeMessage mimeMessage = null;
  private StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象

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

  public String getToken() {
    String str = replaceBlank(bodyText.toString());
    return str.substring(str.lastIndexOf("*") + 1, str.lastIndexOf("*") + 33);
  }

  public void getMailContent(Part part) throws Exception {
    String contentType = part.getContentType();
    int nameIndex = contentType.indexOf("name");
    boolean conName = false;
    if (nameIndex != -1) {
      conName = true;
    }
    if (part.isMimeType("text/plain") && conName == false) {
      // text/plain 类型
      bodyText.append((String) part.getContent());
      String str = replaceBlank(part.getContent().toString());
      System.out.println("token====" + str);
      String token = str.substring(str.lastIndexOf("*") + 1, str.lastIndexOf("*") + 33);
      System.out.println("token====" + token);
    } else if (part.isMimeType("text/html") && conName == false) {
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
}