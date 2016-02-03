package com.example.hexi.canvastest.concurrent;

import java.util.Comparator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hexi on 15/11/18.
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor
        implements PriorityExecutorService {

    private final BlockingDeque<Runnable> workQueue;

    private static final RejectedExecutionHandler defaultHandler = new ThreadPoolExecutor.AbortPolicy();

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), defaultHandler);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, defaultHandler);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), handler);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingDeque<Runnable>(corePoolSize, new PriorityFutureTaskComparator()), threadFactory, handler);
        this.workQueue = (BlockingDeque<Runnable>) super.getQueue();
    }

    public Future<?> submit(Runnable task)
    {
        return this.submit(task, Thread.NORM_PRIORITY);
    }

    public <T> Future<T> submit(Runnable task, T result)
    {
        return this.submit(task, result, Thread.NORM_PRIORITY);
    }

    public <T> Future<T> submit(Callable<T> task)
    {
        return this.submit(task, Thread.NORM_PRIORITY);
    }

    public Future<?> submit(Runnable task, int priority)
    {
        if (task == null)
            throw new NullPointerException();
        RunnableFuture<Object> ftask = newPriorityTaskFor(task, null, priority);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result, int priority)
    {
        if (task == null)
            throw new NullPointerException();
        RunnableFuture<T> ftask = newPriorityTaskFor(task, result, priority);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task, int priority)
    {
        if (task == null)
            throw new NullPointerException();
        RunnableFuture<T> ftask = newPriorityTaskFor(task, priority);
        execute(ftask);
        return ftask;
    }

    protected <T> RunnableFuture<T> newPriorityTaskFor(Runnable runnable, T value, int priority)
    {
        return new PriorityFutureTask<T>(runnable, value, priority);
    }

    protected <T> RunnableFuture<T> newPriorityTaskFor(Callable<T> callable, int priority)
    {
        return new PriorityFutureTask<T>(callable, priority);
    }

    @SuppressWarnings("rawtypes")
    public int getLeastPriority()
    {
        PriorityFutureTask task = ((PriorityFutureTask) this.workQueue.peekLast());
        return task != null ? task.getPriority() : Integer.MIN_VALUE;
    }

    @SuppressWarnings("rawtypes")
    public int getHighestPriority()
    {
        PriorityFutureTask task = ((PriorityFutureTask) this.workQueue.peekFirst());
        return task != null ? task.getPriority() : Integer.MAX_VALUE;
    }

    /**
     * makes best possible effort to change priorities but does not guarantee
     * the same.
     */
    @Override
    public void changePriorities(int fromPriority, int toPriority)
    {
        if (fromPriority < Thread.MIN_PRIORITY || fromPriority > Thread.MAX_PRIORITY || toPriority < Thread.MIN_PRIORITY || toPriority > Thread.MAX_PRIORITY || fromPriority == toPriority)
            throw new IllegalArgumentException("Invalid from/to priority values");

        PriorityFutureTask<?>[] tasks = this.workQueue.toArray(new PriorityFutureTask<?>[0]);

        for (PriorityFutureTask<?> task : tasks)
        {
            if (task.getPriority() == fromPriority)
                if (this.workQueue.remove(task))
                {
                    task.setPriority(toPriority);
                    this.workQueue.offer(task);
                }
        }
    }

    @SuppressWarnings("rawtypes")
    private static class PriorityFutureTaskComparator<T extends PriorityFutureTask> implements Comparator<T>
    {
        @Override
        public int compare(T t1, T t2)
        {
            return t2.getPriority() - t1.getPriority();
        }
    }

}
