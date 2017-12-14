package com.quick.hui.crawler.core.job;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import com.quick.hui.crawler.core.entity.ThreadResult;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferParam;
import com.quick.hui.crawler.core.entity.TransferResult;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.entity.UserInfo;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import com.quick.hui.crawler.core.mailClient.ImapMailToken;
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
import com.scheduled.ScheduledThread;
import com.util.GetNetworkTime;
import com.util.RandomUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 最简单的一个爬虫任务
 * <p>
 * Created by yihui on 2017/6/27.
 */
@Getter
@Setter
public class SimpleCrawlJob extends AbstractJob {

  private static Logger logger = LoggerFactory.getLogger(SimpleCrawlJob.class);
  private static Logger transferSuccessLogger = LoggerFactory.getLogger("transferSuccessLogger");
  private static Logger transferFailueLogger = LoggerFactory.getLogger("transferFailueLogger");
  private TransferUserInfo userInfo;
  //发邮件与收邮件时间间隔，默认10s
  private ThreadConfig config;

  private Map<String, String> cookies;

  public SimpleCrawlJob(TransferUserInfo userInfo,
      ThreadConfig config,
      Map<String, String> cookies) {
    this.userInfo = userInfo;
    this.config = config;
    this.cookies = cookies;
  }

  @Override
  public void beforeRun() {
    initCookie();
  }

  private void initCookie() {
    logger.info("开始初始化cookie");
    if (Session.get() == null || CollectionUtils.isEmpty(Session.getCookies())) {

      if (Objects.isNull(cookies)) {
        logger.info("请初始化cookies");
        return;
      }
      List<LocalCookie> localCookies = Lists.newArrayList();
      for (Map.Entry<String, String> entry : cookies.entrySet()) {
        localCookies.add(new LocalCookie(entry.getKey(), entry.getValue()));
      }
      Session session = Session.buildSession(localCookies);
      Session.persistenceCurrentSession(session);
    }
    if (InitTask.executeSucess() != 200) {
      logger.info("cookies初始化失败，请输入有效cookie");
      ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
      transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:cookie初始化失败");
      throw new RuntimeException("cookies初始化失败，请输入有效cookie");
    } else {
      logger.info("初始化cookie成功");
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
    logger.info("开始抓取登录页面");
    //取登陆页面的authToken
    LoginAuthTokenData tokenData = LoginAuthTokenTask.tryTimes(config);
    if (tokenData.getCode() == 200) {
      //登录
      logger.info("开始登录");
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
      int loginCode = LoginTask.execute(tokenData.getResult(),
          this.userInfo.getUserName(), this.userInfo.getPassword());
      //登录成功
      if (loginCode == 302) {
        Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
        transfer(this.userInfo.getEmail(), this.userInfo.getMailPassword(),
            this.userInfo.getTransferTo());
      } else {
        ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
        transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:登录失败");
        throw new RuntimeException("登录失败");
      }
    } else {
      ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
      transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:抓取登录页面失败");
      throw new RuntimeException("抓取登录页面失败");
    }
  }

  /**
   * 执行转账功能
   */
  private void transfer(String email, String mailPassword, String transferTo)
      throws InterruptedException {
    logger.info("开始抓取抓取转账页面数据");
    TransferPageData getTransferPage = TransferPageTask.execute();
    if (CollectionUtils.isNotEmpty(getTransferPage.getTransferWallets())) {
      List<TransferWallet> filterList = getTransferPage.getTransferWallets()
          .stream()
          .filter(t -> t.getAmount() > 0)
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(filterList)) {
        logger.info("转账金额没有大于0的数据");
        ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), true));
        return;
      }
      TransferWallet wallet = filterList.get(0);
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
      UserInfo receiverInfo = GetReceiverTask.execute(transferTo);
      if (!Objects.isNull(receiverInfo) && receiverInfo.getResponse()) {
        Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
        logger.info(
            "获取转出账户信息成功===>" + receiverInfo.toString());
        SendMailResult mailResult =
            SendMailTask
                .execute(getTransferPage.getAuthToken(), getTransferPage.getTransferUserId());
        if (!Objects.isNull(mailResult)) {//邮件发送成功的情况
          long mailStartTime = GetNetworkTime.getNetworkDatetime();
          long mailSpace = RandomUtil.ranNum(config.getMailSpaceTime()) * 1000 + 30000;
          logger.info(
              "休眠" + mailSpace + "ms后读取邮件");
          Thread.sleep(mailSpace);
          logger.info("开始读取邮件");
          List<MailTokenData> tokenData = tryReceiveMail(email, mailPassword, mailStartTime, mailSpace,
                config.getMailReceiveErrorTimes());
          if (CollectionUtils.isEmpty(tokenData)) {
            ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
            transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:获取邮件信息失败");
            throw new RuntimeException("获取邮件信息失败");
          } else {
            logger
                .info("邮件解析成功");

            transferByToken(email, mailPassword, getTransferPage, wallet, transferTo, receiverInfo, tokenData);
          }
        } else {
          ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
          transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:获取邮件信息失败");
          throw new RuntimeException("获取邮件信息失败");
        }
      } else {
        ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
        transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:获取转出人信息失败");
        throw new RuntimeException("获取转出人信息失败");
      }
    } else {
      ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
      transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:抓取转账页面数据失败");
      throw new RuntimeException("抓取转账页面数据失败");
    }
  }

  private List<MailTokenData> tryReceiveMail(String email, String mailPassword, long mailStartTime, long mailSpace,
      int tryTimes)
      throws InterruptedException {
    for (int i = 1; i <= tryTimes; i++) {
      List<MailTokenData> tokenData = ImapMailToken
          .filterMailsForIsNew(userInfo.getUserName(),email, mailPassword);
      if (CollectionUtils.isEmpty(tokenData)) {
        long tryMailSpace = RandomUtil.ranNum(config.getMailSpaceTime()) * 1000 + 30000;
        logger
            .info("获取邮件失败,等待" + tryMailSpace + "ms重新获取");
        Thread.sleep(tryMailSpace);
        logger
            .info("重新获取邮件开始剩余重试次数" + (tryTimes - i));
      } else {
        return tokenData;
      }
    }
    return null;
  }

  private void transferByToken(String email,
      String mailPassword,
      TransferPageData getTransferPage,
      TransferWallet wallet,
      String transferTo,
      UserInfo receiverInfo,
      List<MailTokenData> tokenData) throws InterruptedException {
    TransferParam param = new TransferParam(getTransferPage.getAuthToken(),
        transferTo,
        wallet.getWalletId(),
        wallet.getAmount(),
        tokenData.get(0).getToken(),
        getTransferPage.getTransferUserId(),
        receiverInfo.getUser_id()
    );
    logger.info("开始转账");
    logger.info("转账参数=" + param.toString());
    TransferResult transferCode = TransferTask.execute(param);
    if (transferCode.getError().equals("invalid_token")) {
      tokenData.remove(0);
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
      logger.info("重试已有token");
      transferByToken(email, mailPassword, getTransferPage, wallet, transferTo, receiverInfo, tokenData);
    }
    if (transferCode.getStatus().equals("success")) {
      transferSuccessLogger.info(
          "账户：" + this.userInfo.getUserName() + "转出：" + wallet.getAmount() + " 到账户："
              + transferTo);
      logger.info("转账成功，休眠500毫秒执行下一轮转账");
      ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), true));
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000);
      logger.info("下一轮转账开始");
      transfer(email, mailPassword, transferTo);
    } else {
      ScheduledThread.getThreadResults().add(new ThreadResult(userInfo.getRow(), false));
      transferFailueLogger.info("转账失败账户:" + userInfo.getUserName() + "原因:服务器错误");
      throw new RuntimeException("转账失败");
    }
  }

  @Override
  public void afterRun() {
    Session.remove();
  }
}
