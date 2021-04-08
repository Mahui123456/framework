import com.krb.guaranty.common.business.snowflake.service.ConfigWorkIdProvider;
import com.krb.guaranty.common.business.snowflake.service.SnowflakeIdService;
import javafx.util.Callback;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author owell
 * @date 2019/5/29 09:08
 */
public class TestSnowFlakeId {

    private Logger log = LoggerFactory.getLogger(TestSnowFlakeId.class);

    @Test
    public void test(){
        SnowflakeIdService service = new SnowflakeIdService();
        service.setWorkIdProvider(new ConfigWorkIdProvider());

        int i = 20;
        while(i>0){
            i--;
            execute(200,10000,(ll)->{
                final long l = service.nextId();
                return null;
            });
        }
    }

    @Test
    public void test2(){
        SnowflakeIdService service = new SnowflakeIdService();
        service.setWorkIdProvider(new ConfigWorkIdProvider());

        int i = 20;
        while(i>0){
            i--;
            execute(200,10000,(ll)->{
                final long l = service.makeId(ll.get(), System.currentTimeMillis());
                return null;
            });
        }
    }
    ExecutorService executor = Executors.newFixedThreadPool(8);

    public void execute(int maxThreadLength, int maxExecuteLength, Callback<AtomicInteger,Object> callback){
        AtomicInteger counter = new AtomicInteger(0);
        //ExecutorService executor = Executors.newFixedThreadPool(maxThreadLength);
        int max = maxExecuteLength;

        long start = System.currentTimeMillis();
        log.error("开始执行任务开始时间:{}",start);
        log.info("等待子线程执行ing...");

        final CountDownLatch latch = new CountDownLatch(max);
        int i =0;
        while(i<max){
            executor.execute(()->{
                try {
                    callback.call(counter);
                }finally {
                    latch.countDown();
                }
            });
            i ++;
        }

        try {
            latch.await();
            log.info("子线程已经执行完毕,当前counter:"+counter.get());
            long end = System.currentTimeMillis();
            log.error("执行完毕....执行时间:{}",(end-start));

            System.out.println();
            System.out.println();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
