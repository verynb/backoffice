package com.quick.hui.crawler.core.localSession;

import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * Created by yuanj on 9/21/16.
 */
@Data
public class Session {

  private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();

  private List<LocalCookie> cookies;

  public static Session buildSession() {
    Session session = new Session();
    return session;
  }

  public static Session buildSession(List<LocalCookie> cookies) {
    Session session = new Session();
    session.cookies = cookies;
    return session;
  }


  public static void persistenceCurrentSession(Session session) {
    Session profile = threadSession.get();
    if (profile == null || profile.cookies == null) {
      threadSession.set(session);
    }
  }

  public static Session get() {
    return threadSession.get();
  }

  public static List<LocalCookie> getCookies() {
    return threadSession.get().cookies;
  }

  public static void writeSession(List<LocalCookie> localCookies) {
    if (get() == null) {
      Session session = buildSession(localCookies);
      persistenceCurrentSession(session);
    } else {
      localCookies.forEach(lc -> {
        Optional<LocalCookie> filter = getCookies().stream()
            .filter(c -> c.getSessionKey().equals(lc.getSessionKey()))
            .findFirst();
        if(filter.isPresent()){
          if(!filter.get().getSessionValue().equals(lc.getSessionValue())){
            filter.get().setSessionValue(lc.getSessionValue());
          }
        }else {
          getCookies().add(lc);
        }
      });
    }
  }

  public static void remove() {
    if (threadSession != null) {
      threadSession.remove();
    }
  }
}
