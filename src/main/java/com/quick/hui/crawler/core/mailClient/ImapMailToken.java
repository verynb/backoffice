package com.quick.hui.crawler.core.mailClient;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yj on 2017/11/30.
 */
public class ImapMailToken {

  private static final String PROTOCOL = "imap";
  private static final String PORT = "143";
  private static final String HOST = "imap.mxhichina.com";

  private static Store getStore(String mail, String password) throws MessagingException {
    Properties props = new Properties();
    props.setProperty("mail.imap.protocol", PROTOCOL);       // 协议
    props.setProperty("mail.imap.port", PORT);             // 端口
    props.setProperty("mail.imap.host", HOST);    // imap服务器
    Session session = Session.getInstance(props);
    Store store = session.getStore("imap");
    store.connect(mail, password);
    return store;
  }


  public static List<MailTokenData> filterMailsForIsNew(String userName,String mail, String password) {
    Store store = null;
    Folder folder = null;
    Folder rubbishFolder = null;
    List<MailTokenData> dataList = Lists.newArrayList();
    try {
      store = getStore(mail, password);
      rubbishFolder = store.getFolder("垃圾邮件");
      folder = store.getFolder("INBOX");
      rubbishFolder.open(Folder.READ_WRITE);
      folder.open(Folder.READ_WRITE);
      List<Message> inboxMessages = new ArrayList<Message>(Arrays.asList(folder.getMessages()));
      List<Message> rubbishMessages=new ArrayList<Message>(Arrays.asList(rubbishFolder.getMessages()));
      inboxMessages.addAll(rubbishMessages);
      for (int i = 0; i < inboxMessages.size(); i++) {
        ReceiveEmail re = new ReceiveEmail((MimeMessage) inboxMessages.get(i));
        if (StringUtils.isBlank(re.getSubject()) || !re.getSubject()
            .equals("Token for your TRANSFER")) {
          continue;
        }
        if (!re.isNew()) {
          continue;
        }
        re.getMailContent((Part) inboxMessages.get(i));
        if(!re.getSendUser(userName.length()).equals(userName)){
          re.getMimeMessage().setFlag(Flags.Flag.SEEN,false);
          continue;
        }
        dataList.add(new MailTokenData(re.getToken(), re.getSentDate()));
      }
      dataList = dataList
          .stream()
          .sorted(Comparator.comparing(MailTokenData::getDate).reversed())
          .collect(Collectors.toList());
    } catch (Exception e) {
      return dataList;
    } finally {
      try {
        folder.close(true);
        store.close();
      } catch (MessagingException e) {

      }
      return dataList;
    }
  }

  public static List<MailTokenData> filterMailsForOld(String userName,String mail, String password) {
    Store store = null;
    Folder folder = null;
    Folder rubbishFolder = null;
    List<MailTokenData> dataList = Lists.newArrayList();
    try {
      store = getStore(mail, password);
      rubbishFolder = store.getFolder("垃圾邮件");
      folder = store.getFolder("INBOX");
      rubbishFolder.open(Folder.READ_WRITE);
      folder.open(Folder.READ_WRITE);
      List<Message> inboxMessages = new ArrayList<Message>(Arrays.asList(folder.getMessages()));
      List<Message> rubbishMessages=new ArrayList<Message>(Arrays.asList(rubbishFolder.getMessages()));
      inboxMessages.addAll(rubbishMessages);
      for (int i = 0; i < inboxMessages.size(); i++) {
        ReceiveEmail re = new ReceiveEmail((MimeMessage) inboxMessages.get(i));
        if (StringUtils.isBlank(re.getSubject()) || !re.getSubject()
            .equals("Token for your TRANSFER")) {
          continue;
        }
        re.getMailContent((Part) inboxMessages.get(i));
        if(!re.getSendUser(userName.length()).equals(userName)){
          re.getMimeMessage().setFlag(Flags.Flag.SEEN,false);
          continue;
        }
        dataList.add(new MailTokenData(re.getToken(), re.getSentDate()));
      }
      dataList = dataList
          .stream()
          .sorted(Comparator.comparing(MailTokenData::getDate).reversed())
          .collect(Collectors.toList());
    } catch (Exception e) {
      return dataList;
    } finally {
      try {
        folder.close(true);
        store.close();
      } catch (MessagingException e) {

      }
      return dataList;
    }
  }


  public static void main(String[] args) {
    System.out.println(ImapMailToken.filterMailsForIsNew("lhha001","lianghuihua01@bookbitbtc.com", "SHENzen007v"));
  }
}
