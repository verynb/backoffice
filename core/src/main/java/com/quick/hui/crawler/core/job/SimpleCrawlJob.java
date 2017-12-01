package com.quick.hui.crawler.core.job;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferParam;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.entity.UserInfo;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import com.quick.hui.crawler.core.mailClient.MailToken;
import com.quick.hui.crawler.core.mailClient.MailTokenData;
import com.quick.hui.crawler.core.task.GetReceiverTask;
import com.quick.hui.crawler.core.task.InitTask;
import com.quick.hui.crawler.core.task.LoginAuthTokenTask;
import com.quick.hui.crawler.core.task.LoginSuccessTask;
import com.quick.hui.crawler.core.task.LoginTask;
import com.quick.hui.crawler.core.task.SendMailTask;
import com.quick.hui.crawler.core.task.TransferPageTask;
import com.quick.hui.crawler.core.task.TransferTask;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

/**
 * 最简单的一个爬虫任务
 * <p>
 * Created by yihui on 2017/6/27.
 */
@Getter
@Setter
@Slf4j
public class SimpleCrawlJob extends AbstractJob {

  private TransferUserInfo userInfo;
  //发邮件与收邮件时间间隔，默认10s
  private Long mailSendReceiveSpace;
  //每个请求间隔时间，默认未2s
  private Long requestSpace;

  private Map<String, String> cookies;

  public SimpleCrawlJob(TransferUserInfo userInfo,
      Long mailSendReceiveSpace,
      Long requestSpace,
      Map<String, String> cookies) {
    this.userInfo = userInfo;
    this.mailSendReceiveSpace = mailSendReceiveSpace == null ? 10000L : mailSendReceiveSpace;
    this.requestSpace = requestSpace == null ? 2000L : requestSpace;
    this.cookies = cookies;
  }

  @Override
  public void beforeRun() {
//    InitTask.execute();
    initCookie();
  }

  private void initCookie() {
    if (Session.get() == null || CollectionUtils.isEmpty(Session.getCookies())) {

      if (Objects.isNull(cookies)) {
        throw new RuntimeException("请初始化cookies");
      }
      List<LocalCookie> localCookies = Lists.newArrayList();
      for (Map.Entry<String, String> entry : cookies.entrySet()) {
        localCookies.add(new LocalCookie(entry.getKey(), entry.getValue()));
      }
      Session session = Session.buildSession(localCookies);
      Session.persistenceCurrentSession(session);
    }
    if (InitTask.executeSucess() != 200) {
      throw new RuntimeException("cookies初始化失败，请输入有效cookie");
    }
  }

  /**
   * 转账流程如下
   * 1.抓取登录静态页面，取到页面[authenticity_token]的值
   * 2.执行登录请求，获取登录后的cookies
   * 3.拿到登录后重定向的页面，获取cookies
   * 4.进入转账页面
   * 4-1：获取转账金额
   * 4-2：获取转账钱包ID
   * 4-3：获取转账人ID
   * 4-4：获取转账转账页面authenticity_token
   * 5.获取转账户信息
   * 6.向转账账户发邮件生成转账TOKEN
   * 7.登录邮件获取token
   * 8.完成转账
   */
  @Override
  public void doFetchPage() throws Exception {

    //取登陆页面的authToken
    LoginAuthTokenData tokenData = LoginAuthTokenTask.execute();
    if (tokenData.getCode() == 200) {
      //登录
      int loginCode = LoginTask.execute(tokenData.getResult(),
          this.userInfo.getUserName(), this.userInfo.getPassword());
      //登录成功
      if (loginCode == 302) {
        //登录重定向后的页面
        int loginSuccessCode = LoginSuccessTask.execute();
        if (loginSuccessCode == 200) {
          transfer(this.userInfo.getEmail(), this.userInfo.getMailPassword(), this.userInfo.getTransferTo());
        }
      }
    }
  }

  /**
   * 执行转账功能
   */
  private void transfer(String email, String mailPassword, String transferTo) throws InterruptedException {

    TransferPageData getTransferPage = TransferPageTask.execute();
    if (CollectionUtils.isNotEmpty(getTransferPage.getTransferWallets())) {
      List<TransferWallet> filterList = getTransferPage.getTransferWallets()
          .stream()
          .filter(t -> t.getAmount() > 0)
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(filterList)) {
        return;
      }
      TransferWallet wallet = filterList.get(0);
      UserInfo receiverInfo = GetReceiverTask.execute(transferTo);
      if (!Objects.isNull(receiverInfo)) {

        SendMailResult mailResult =
            SendMailTask.execute(getTransferPage.getAuthToken(), getTransferPage.getTransferUserId());
        if (!Objects.isNull(mailResult)) {//邮件发送成功的情况
          Thread.sleep(this.mailSendReceiveSpace);
          List<MailTokenData> tokenData = MailToken
              .filterMails(email, mailPassword);
          System.out.print("wallet value======>" + wallet.toString());
          System.out.print("token======>" + tokenData.get(0).getToken());
          System.out.print("TransferUserId======>" + getTransferPage.getTransferUserId());
          System.out.print("User_id======>" + receiverInfo.getUser_id());
          TransferParam param = new TransferParam(getTransferPage.getAuthToken(),
              transferTo,
              wallet.getWalletId(),
              wallet.getAmount(),
              tokenData.get(0).getToken(),
              getTransferPage.getTransferUserId(),
              receiverInfo.getUser_id()
          );
          int transferCode = TransferTask.execute(param);
          if (transferCode == 302) {
            Thread.sleep(5000);
            transfer(email, mailPassword, transferTo);
          }
        }
      }
    }
  }
}
