package cn.kai.book.test.serviceImpl;

import cn.kai.book.exception.RollbackException;
import cn.kai.book.test.service.FooService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FooServiceImpl implements FooService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  @Transactional
  public void insertRecord(){
    jdbcTemplate.execute("insert into Foo (ID, BAR) VALUES (2, 'AAA');");
  }

  @Override
  @Transactional(rollbackFor = RollbackException.class)
  public void insertThenRollback() throws RollbackException{
    jdbcTemplate.execute("insert into Foo (BAR) VALUES ('BBB');");
    throw new RollbackException();
  }

  @Override
  //@Transactional(rollbackFor = RollbackException.class) --0
  // ---1
  public void invokeInsertThenRollback()throws RollbackException{
    //不带方法事务
    //insertThenRollback();

    //带事务
    /*@Override
    public void invokeInsertThenRollbackBySelfService() throws RollbackException {
        fooService.insertThenRollback();
    }
    //获取当前代理，这样写避免了自己调用自己的实例
    @Override
    public void invokeInsertThenRollbackByAopContext() throws RollbackException {
        ((FooService) (AopContext.currentProxy())).insertThenRollback();
    }
    //再加一层事务
    @Transactional(rollbackFor = RollbackException.class)
    @Override
    public void invokeInsertThenRollbackAddTransactional() throws RollbackException {
        insertThenRollback();
    }*/
    ((FooService) (AopContext.currentProxy())).insertThenRollback();
  }
}
