package com.huawei.insa2.util;

public abstract class WatchThread extends Thread {

  private boolean alive = true;

  private State state;

  public static final ThreadGroup tg = new ThreadGroup("watch-thread");

  public WatchThread(String name) {
    super(tg,name);
    setDaemon(true);
  }

  public void kill() {
    alive = false;
  }

  public final void run() {
    while (alive) {
      try {
        task();
      } 
      catch (Exception ex) {} 
      catch (Throwable t) {}
    }
  }
  
  protected void setState(State newState) {
    this.state = newState;
  }
  
  public State getState() {
    return this.state;
  }

  abstract protected void task();
}