import com.krb.guaranty.common.utils.InternalExecutor;

/**
 * @author owell
 * @date 2019/12/31 11:27
 */
public class TestInternalExecutor {
    public static void main(String[] args) {
        InternalExecutor.execute(new InternalExecutor(2,100,1000) {
            @Override
            public InternalExecutorResult doRequest() {
                System.out.println("dooooooo something");
                return InternalExecutor.RETRY;
            }

            @Override
            public int getNextSleepMillis(int retryTimes, int retryCount, int lastSleepMillis) {
                return retryTimes*3;
            }
        });
    }
}
