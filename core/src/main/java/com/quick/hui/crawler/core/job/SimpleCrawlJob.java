package com.quick.hui.crawler.core.job;

import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferParam;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.entity.UserInfo;
import com.quick.hui.crawler.core.task.GetReceiverTask;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.task.InitTask;
import com.quick.hui.crawler.core.task.LoginAuthTokenTask;
import com.quick.hui.crawler.core.task.LoginSuccessTask;
import com.quick.hui.crawler.core.task.LoginTask;
import com.quick.hui.crawler.core.task.SendMailTask;
import com.quick.hui.crawler.core.task.TransferPageTask;
import com.quick.hui.crawler.core.task.TransferTask;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 最简单的一个爬虫任务
 * <p>
 * Created by yihui on 2017/6/27.
 */
@Getter
@Setter
@Slf4j
public class SimpleCrawlJob extends AbstractJob {

  @Override
  public void beforeRun() {
    InitTask.execute();

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
    if (tokenData.getCode() != 200) {
      log.debug(tokenData.toString());
    }
    //登录
    int loginCode = LoginTask.execute(tokenData.getResult(), "xbya003", "xby19800716x");
    //登录成功
    if (loginCode == 302) {
      //登录重定向后的页面
      int loginSuccessCode = LoginSuccessTask.execute();
      transfer();
    }
  }


  /**
   * 执行转账功能
   * @throws InterruptedException
   */
  private void transfer() throws InterruptedException {

    TransferPageData getTransferPage = TransferPageTask.execute();
    UserInfo receiverInfo = GetReceiverTask.execute("xbya004");
    SendMailResult mailResult =
        SendMailTask.execute(getTransferPage.getAuthToken(), getTransferPage.getTransferUserId());
    if (getTransferPage.getTransferWallets()
        .stream()
        .filter(t -> t.getAmount() > 0).count() == 0) {
      return;
    }
    Optional<TransferWallet> wallet = getTransferPage.getTransferWallets()
        .stream()
        .filter(t -> t.getAmount() > 0)
        .findFirst();
    if(!wallet.isPresent()){

    }else {
      TransferParam param = new TransferParam(getTransferPage.getAuthToken(),
          "xbya004",
          wallet.get().getWalletId(),
          wallet.get().getAmount(),
          "",
          getTransferPage.getTransferUserId(),
          receiverInfo.getUser_id()
      );
      TransferTask.execute(param);
    }
    Thread.sleep(5000);
    transfer();
  }

}
