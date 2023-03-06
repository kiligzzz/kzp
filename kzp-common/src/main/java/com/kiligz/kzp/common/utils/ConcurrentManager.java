package com.kiligz.kzp.common.utils;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 并发管理器 <br>
 * 1.线程池 <br>
 * - 支持创建、管理多个四种可命名线程池; <br>
 * - 添加CountDownLatch任务执行计数，可阻塞等待任务执行、获取结果; <br>
 * 2.阻塞队列 <br>
 * - 支持创建、管理多个两种阻塞队列; <br>
 * 3.结束标记 <br>
 * - 放入、记录、判断结束标记; <br>
 *
 * @author Ivan
 */
@Slf4j
public final class ConcurrentManager {
    /*-------------------------------------------------------------------------*/
    /*--------------------------------- 线程池 ---------------------------------*/
    /*-------------------------------------------------------------------------*/

    // 线程池名::类型 -> 线程池
    private static final Map<String, ThreadPool> theadPoolMap = new ConcurrentHashMap<>();

    /**
     * 获取Fixed线程池
     */
    public static ThreadPool getFixedThreadPool(String name) {
        return getThreadPool(name, ThreadPoolType.FIXED);
    }

    /**
     * 获取Single线程池
     */
    public static ThreadPool getSingleThreadPool(String name) {
        return getThreadPool(name, ThreadPoolType.SINGLE);
    }

    /**
     * 获取Cached线程池
     */
    public static ThreadPool getCachedThreadPool(String name) {
        return getThreadPool(name, ThreadPoolType.CACHED);
    }

    /**
     * 获取Scheduled线程池
     */
    public static ThreadPool getScheduledThreadPool(String name) {
        return getThreadPool(name, ThreadPoolType.SCHEDULED);
    }

    /**
     * 创建线程池
     */
    private static ThreadPool getThreadPool(String name, ThreadPoolType type) {
        // 不存在则放入，返回新结果，存在则直接返回该结果
        return theadPoolMap.computeIfAbsent(getKey(name, type.name()), type::create);
    }

    /**
     * 关闭所有线程池，并且丢弃记录打印日志
     */
    public static void shutdownWithLog() {
        theadPoolMap.values().parallelStream().forEach(ThreadPool::shutdownWithLog);
        theadPoolMap.clear();
    }



    /*-------------------------------------------------------------------------*/
    /*-------------------------------- 阻塞队列 --------------------------------*/
    /*-------------------------------------------------------------------------*/

    // 队列名::类名 -> BlockingQueue
    private static final Map<String, BlockingQueue<?>> blockingQueueMap = new ConcurrentHashMap<>();

    // 队列名::类名 -> TransferQueue
    private static final Map<String, TransferQueue<?>> transferQueueMap = new ConcurrentHashMap<>();

    /**
     * 获取BlockingQueue
     */
    @SuppressWarnings("unchecked")
    public static <T> BlockingQueue<T> getBlockingQueue(String name, Class<T> clazz) {
        return (BlockingQueue<T>) blockingQueueMap.computeIfAbsent(getKey(name, clazz.getName()),
                key -> new LinkedBlockingQueue<>());
    }

    /**
     * 获取TransferQueue
     */
    @SuppressWarnings("unchecked")
    public static <T> TransferQueue<T> getTransferQueue(String name, Class<T> clazz) {
        return (TransferQueue<T>) transferQueueMap.computeIfAbsent(getKey(name, clazz.getName()),
                key -> new LinkedTransferQueue<>());
    }

    /**
     * 处理异常的put
     */
    public static <T> void put(BlockingQueue<T> queue, T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            log.error("Interrupted exception while await put {} to {}.", t, queue, e);
        }
    }

    /**
     * 处理异常的transfer
     */
    public static <T> void transfer(TransferQueue<T> queue, T t) {
        try {
            queue.transfer(t);
        } catch (InterruptedException e) {
            log.error("Interrupted exception while await transfer {} to {}.", t, queue, e);
        }
    }

    /**
     * 处理异常的take
     */
    public static <T> T take(BlockingQueue<T> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("Interrupted exception while await take from {}.", queue, e);
        }
        return null;
    }



    /*-------------------------------------------------------------------------*/
    /*-------------------------------- 结束标记 --------------------------------*/
    /*-------------------------------------------------------------------------*/

    // 队列结束标记map，队列 -> 结束标记
    private static final Map<Queue<?>, Object> endMarkerMap = new ConcurrentHashMap<>();

    /**
     * 往队列末尾put指定个结束标记，末尾可接收一个数字
     */
    public static <T> void putEnd(BlockingQueue<T> queue, T endMarker, long... countFirst) {
        try {
            // 记录结束标记
            endMarkerMap.put(queue, endMarker);
            // 放入queue
            long count = countFirst.length == 0 ? 1 : countFirst[0];
            for (long i = 0; i < count; i++) {
                queue.put(endMarker);
            }
        } catch (InterruptedException e) {
            log.error("Interrupted exception while await put {} to {}.", endMarker, queue, e);
        }
    }

    /**
     * 往队列末尾transfer一个结束标记，末尾可接收一个数字
     */
    public static <T> void transferEnd(TransferQueue<T> queue, T endMarker, long... countFirst) {
        try {
            // 记录结束标记
            endMarkerMap.put(queue, endMarker);
            // 放入queue
            long count = countFirst.length == 0 ? 1 : countFirst[0];
            for (long i = 0; i < count; i++) {
                queue.transfer(endMarker);
            }
        } catch (InterruptedException e) {
            log.error("Interrupted exception while transfer {} to {}.", endMarker, queue, e);
        }
    }

    /**
     * 是否是队列的结束标记
     */
    public static <T> boolean isEndMarker(Queue<T> queue, T obj) {
        Object endMarker = endMarkerMap.get(queue);
        return endMarker != null && endMarker == obj;
    }



    /*-------------------------------------------------------------------------*/
    /*------------------------------- 线程池创建 -------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * 线程池封装类
     */
    public static class ThreadPool {
        @Getter
        private final String name;
        @Getter
        private final String type;
        private final ThreadPoolExecutor threadPool;

        private ThreadPool(String name, String type, ThreadPoolExecutor threadPool) {
            this.name = name;
            this.type = type;
            this.threadPool = threadPool;
        }

        @Override
        public String toString() {
            return String.format("[[ name=%s, type=%s ]]", name, type);
        }



        /*------------------------------ 线程池使用 ------------------------------*/

        /**
         * 获取原始线程池
         */
        public ThreadPoolExecutor getOriginInstance() {
            return threadPool;
        }

        /**
         * 关闭线程池，并且丢弃记录
         */
        public void shutdownAndDiscard() {
            threadPool.shutdown();
            theadPoolMap.remove(getKey(name, type));
        }

        /**
         * 打印日志并关闭线程池
         */
        public void shutdownWithLog() {
            await();
            log.info("[ {} finish. ]", name);
            log.info("[ {} finish. ]", name);
            log.info("[ {} finish. ]", name);
            shutdownAndDiscard();
        }

        /**
         * 返回线程池是否是关闭状态
         */
        public boolean isShutdown() {
            return threadPool.isShutdown();
        }


        /**
         * 任务代理，增加countDownLatch执行计数，增加错误日志
         */
        private final Function<Runnable, Runnable> taskProxy = originTask -> () -> {
            try {
                originTask.run();
            } catch (Exception e) {
                log.error("Task run error.", e);
            } finally {
                // 添加任务执行完成计数
                countDown(originTask);
            }
        };

        /**
         * 添加一个任务到线程池中执行，返回原始task，可用来await
         */
        public Runnable execute(@NonNull Runnable task) {
            latchMap.put(task, new CountDownLatch(1));
            threadPool.execute(taskProxy.apply(task));
            return task;
        }

        /**
         * 添加taskCount个任务到线程池执行，返回原始task，可用来await
         */
        public Runnable execute(@NonNull Runnable task, int taskCount) {
            latchMap.put(task, new CountDownLatch(taskCount));
            Runnable proxyTask = taskProxy.apply(task);
            for (int i = 0; i < taskCount; i++) {
                threadPool.execute(proxyTask);
            }
            return task;
        }

        /**
         * 添加一个任务到线程池中执行，返回Future
         */
        public <T> RunnableFuture<T> submit(@NonNull Callable<T> task) {
            RunnableFuture<T> runnableFuture = new FutureTask<>(task);

            latchMap.put(runnableFuture, new CountDownLatch(1));
            threadPool.execute(taskProxy.apply(runnableFuture));
            return runnableFuture;
        }

        /**
         * 添加taskCount个任务到线程池执行，返回一组Future
         */
        public <T> List<RunnableFuture<T>> submit(@NonNull Callable<T> task, int taskCount) {
            List<RunnableFuture<T>> futureList = new ArrayList<>();
            RunnableFuture<T> runnableFuture = new FutureTask<>(task);

            latchMap.put(runnableFuture, new CountDownLatch(taskCount));
            Runnable proxyTask = taskProxy.apply(runnableFuture);
            for (int i = 0; i < taskCount; i++) {
                threadPool.execute(proxyTask);
                futureList.add(runnableFuture);
            }
            return futureList;
        }

        /**
         * 添加一组任务到线程池执行，返回一组Future
         */
        public <T> List<Future<T>> invokeAll(@NonNull List<Callable<T>> taskList) throws InterruptedException {
            return threadPool.invokeAll(taskList);
        }

        /**
         * 添加一组任务到线程池执行，执行任意一个任务返回Future
         */
        public <T> T invokeAny(@NonNull List<Callable<T>> taskList) throws ExecutionException, InterruptedException {
            return threadPool.invokeAny(taskList);
        }

        /**
         * 添加一个任务到线程池中，一定延时后执行，返回原始task，可用来await
         */
        public Runnable schedule(@NonNull Runnable task, long delay, @NonNull TimeUnit unit) {
            try {
                latchMap.put(task, new CountDownLatch(1));
                ((ScheduledThreadPoolExecutor) threadPool).schedule(taskProxy.apply(task), delay, unit);
            } catch (ClassCastException e) {
                log.error("This threadPool is not a scheduledThreadPool, please checked it.", e);
            }
            return task;
        }

        /**
         * 添加taskCount个任务到线程池中，一定延时后执行，返回原始task，可用来await
         */
        public Runnable schedule(@NonNull Runnable task, int taskCount, long delay, @NonNull TimeUnit unit) {
            try {
                latchMap.put(task, new CountDownLatch(taskCount));
                Runnable proxyTask = taskProxy.apply(task);
                ScheduledThreadPoolExecutor scheduledThreadPool = (ScheduledThreadPoolExecutor) threadPool;
                for (int i = 0; i < taskCount; i++) {
                    scheduledThreadPool.schedule(proxyTask, delay, unit);
                }
            } catch (ClassCastException e) {
                log.error("This threadPool is not a scheduledThreadPool, please checked it.", e);
            }
            return task;
        }

        /**
         * 添加一个任务到线程池中，一定延时之后定时执行，返回原始task，可用来await
         */
        public Runnable scheduleAtFixedRate(@NonNull Runnable task, long delay, long period, @NonNull TimeUnit unit) {
            try {
                latchMap.put(task, new CountDownLatch(1));
                ((ScheduledThreadPoolExecutor) threadPool).scheduleAtFixedRate(taskProxy.apply(task), delay, period, unit);
            } catch (ClassCastException e) {
                log.error("This threadPool is not a scheduledThreadPool, please checked it.", e);
            }
            return task;
        }

        /**
         * 添加taskCount个任务到线程池中，一定延时后定时执行，返回原始task，可用来await
         */
        public Runnable scheduleAtFixedRate(@NonNull Runnable task, int taskCount, long delay, long period, @NonNull TimeUnit unit) {
            try {
                latchMap.put(task, new CountDownLatch(taskCount));
                Runnable proxyTask = taskProxy.apply(task);
                ScheduledThreadPoolExecutor scheduledThreadPool = (ScheduledThreadPoolExecutor) threadPool;
                for (int i = 0; i < taskCount; i++) {
                    scheduledThreadPool.scheduleAtFixedRate(proxyTask, delay, period, unit);
                }
            } catch (ClassCastException e) {
                log.error("This threadPool is not a scheduledThreadPool, please checked it.", e);
            }
            return task;
        }



        /*---------------------------- CountDownLatch ----------------------------*/

        /**
         * 原始任务与CountDownLatch的映射
         */
        private final Map<Runnable, CountDownLatch> latchMap = new ConcurrentHashMap<>();

        /**
         * 获取当前 任务 -> CountDownLatch 的信息
         */
        public String infoLatchMap() {
            return latchMap.toString();
        }

        /**
         * 任务未完成数-1
         */
        public void countDown(@NonNull Runnable task) {
            // 若countDownLatch为0时删除，不为0时返回
            latchMap.computeIfPresent(task, (k, v) -> {
                v.countDown();
                return v.getCount() == 0 ? null : v;
            });
        }

        /**
         * 等待所有任务执行完成
         */
        public void await() {
            try {
                for (CountDownLatch latch : latchMap.values())
                    latch.await();
            } catch (InterruptedException e) {
                log.error("Interrupted exception while await all task.", e);
            }
        }

        /**
         * 等待一定时间，返回所有任务是否执行完成
         */
        public boolean await(long timeout, TimeUnit timeUnit) {
            try {
                for (CountDownLatch latch : latchMap.values())
                    if (!latch.await(timeout, timeUnit))
                        return false;
            } catch (InterruptedException e) {
                log.error("Interrupted exception while timeout await all task.", e);
            }
            return true;
        }

        /**
         * 等待指定任务执行完成
         */
        public void await(@NonNull Runnable task) {
            try {
                if (!latchMap.containsKey(task)) return;
                latchMap.get(task).await();
            } catch (InterruptedException e) {
                log.error("Interrupted exception while await {}.", task, e);
            }
        }

        /**
         * 等待一定时间，返回指定任务是否执行完成
         */
        public boolean await(@NonNull Runnable task, long timeout, TimeUnit unit) {
            try {
                return !latchMap.containsKey(task) ||
                        latchMap.get(task).await(timeout, unit);
            } catch (InterruptedException e) {
                log.error("Interrupted exception while timeout await {}.", task, e);
            }
            return false;
        }

        /**
         * 获取未完成任务的数量
         */
        public long getCount(@NonNull Runnable task) {
            return latchMap.get(task).getCount();
        }

        /**
         * 获取所有未完成任务的数量
         */
        public long getCount() {
            return latchMap.values().stream().mapToLong(CountDownLatch::getCount).sum();
        }
    }


    /**
     * 线程池 类型 -> 创建方法 枚举类
     */
    private enum ThreadPoolType {
        FIXED {
            @Override
            ThreadPoolExecutor createExecutor(String name) {
                return newFixedThreadPool(name);
            }
        },
        SINGLE {
            @Override
            ThreadPoolExecutor createExecutor(String name) {
                return newSingleThreadPool(name);
            }
        },
        CACHED {
            @Override
            ThreadPoolExecutor createExecutor(String name) {
                return newCachedThreadPool(name);
            }
        },
        SCHEDULED {
            @Override
            ThreadPoolExecutor createExecutor(String name) {
                return newScheduledThreadPool(name);
            }
        };

        /**
         * 创建ThreadPoolExecutor
         */
        abstract ThreadPoolExecutor createExecutor(String name);

        /**
         * 创建ThreadPool
         */
        ThreadPool create(String nameWithType) {
            String name = nameWithType.substring(0, nameWithType.lastIndexOf("::"));
            return new ThreadPool(name, this.name(), createExecutor(name));
        }
    }


    // cpu密集
    public static final int CPU_INTENSIVE = Runtime.getRuntime().availableProcessors() + 1;

    // io密集
    public static final int IO_INTENSIVE = Runtime.getRuntime().availableProcessors() * 2;

    // 核心线程数
    @Setter
    private static int corePoolSize = IO_INTENSIVE;

    /**
     * 新建Fixed线程池
     */
    public static ThreadPoolExecutor newFixedThreadPool(String name) {
        return new ThreadPoolExecutor(corePoolSize, corePoolSize,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(corePoolSize * 10),
                new NamedThreadFactory(name));
    }

    /**
     * 新建Single线程池
     */
    public static ThreadPoolExecutor newSingleThreadPool(String name) {
        return new ThreadPoolExecutor(1, 1,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory(name));
    }

    /**
     * 新建Cached线程池
     */
    public static ThreadPoolExecutor newCachedThreadPool(String name) {
        return new ThreadPoolExecutor(0, corePoolSize * 10,
                3, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory(name));
    }

    /**
     * 新建Scheduled线程池
     */
    public static ScheduledThreadPoolExecutor newScheduledThreadPool(String name) {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new NamedThreadFactory(name));
    }


    /**
     * 命名工厂
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger num = new AtomicInteger();

        private NamedThreadFactory(String name) {
            this.namePrefix = name + "-";
        }

        @Override
        public Thread newThread(@NonNull Runnable task) {
            return new Thread(task, namePrefix + num.getAndIncrement());
        }
    }



    /*-------------------------------------------------------------------------*/
    /*-------------------------------- 信息记录 --------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * 获取当前 线程池名::类型 -> 线程池 的信息
     */
    public static String infoThreadPoolMap() {
        return theadPoolMap.toString();
    }

    /**
     * 获取所有线程池的latchInfo
     */
    public static Map<String, String> infoLatchInfoMap() {
        Map<String, String> latchInfoMap = new HashMap<>();
        theadPoolMap.forEach((k, v) -> latchInfoMap.put(k, v.infoLatchMap()));
        return latchInfoMap;
    }

    /**
     * 获取当前 队列名::类名 的信息
     */
    public static String infoBlockingQueueMap() {
        return blockingQueueMap.keySet().toString();
    }

    /**
     * 获取当前 队列名::类名 的信息
     */
    public static String infoTransferQueueMap() {
        return transferQueueMap.keySet().toString();
    }



    /*-------------------------------------------------------------------------*/
    /*-------------------------------- 工具方法 --------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * 构造key
     */
    private static String getKey(String prefix, String suffix) {
        return prefix + "::" + suffix;
    }

    /**
     * 处理异常的sleep
     */
    public static void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            log.error("Interrupted exception while sleep.", e);
        }
    }
}