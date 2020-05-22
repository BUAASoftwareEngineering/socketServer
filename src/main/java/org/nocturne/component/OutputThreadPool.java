package org.nocturne.component;

import org.nocturne.task.OutputTask;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class OutputThreadPool {
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void execTask(OutputTask task) {
        threadPool.execute(task);
    }
}
