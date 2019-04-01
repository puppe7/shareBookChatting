package cn.kai.book.test.service;

import cn.kai.book.exception.RollbackException;

public interface FooService {

  public void insertRecord();

  public  void insertThenRollback() throws RollbackException;

  public  void invokeInsertThenRollback() throws RollbackException;
}
