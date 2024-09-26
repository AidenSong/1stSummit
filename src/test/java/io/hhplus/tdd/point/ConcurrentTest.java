package io.hhplus.tdd.point;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.impl.PointServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class ConcurrentTest {

    @Mock
    private PointHistoryTable pht;

    @Mock
    private UserPointTable upt;

    @InjectMocks
    private PointServiceImpl pointService;

    @Autowired
    public void initialize() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void concurrentChargeTest() throws Exception {
        long id = 1L;
        long amount = 1000L;

        UserPoint userPoint = new UserPoint(id, 2000, 1000);
        when(upt.selectById(id)).thenReturn(userPoint);
        when(upt.insertOrUpdate(id, amount)).thenReturn(new UserPoint(id, 7000, 1000));


        int numberOfThreads = 10;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    pointService.charge(id, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }



    }

}
