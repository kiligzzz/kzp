package com.kiligz.kzp.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 并发管理器
 * <pre>
 * 1.线程池 --- {@link #theadPoolMap}
 *   - 支持创建、管理多个四种可命名线程池;
 *   - 添加CountDownLatch任务执行计数，可阻塞等待所有或指定线程池或任务执行、获取结果;
 *   - 实现Executor，可供CompletableFuture使用;
 * 2.线程共享对象 --- {@link #threadSharedMap}
 *   - 支持添加、管理、获取线程共享对象;
 * 3.ThreadLocal --- {@link #threadLocalMap}
 *   - 支持创建、管理ThreadLocal及其值;
 *   - 若需在线程池中使用则依赖TransmittableThreadLocal，同时包装线程池
 * 4.结束标记 --- {@link #endMarkerMap}
 *   - 放入、记录、判断结束标记;
 * 5.线程池创建 --- {@link ThreadPool}
 *   - 线程池使用;
 *   - CountDownLatch;
 *   - Support;
 * 6.当前map信息获取 --- {@link #infoThreadPoolMap}
 * 7.工具方法 --- {@link #getKey}
 *   - 支持获取一个在当前线程执行任务的Executor对象
 * （所有异常均未抛出只打印了日志，需抛出则改写）
 * </pre>
 *
 * @author Ivan
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class Concurrents {
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
    /*------------------------------ 线程共享对象 -------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * name -> 线程共享对象
     */
    private static final Map<String, Object> threadSharedMap = new ConcurrentHashMap<>();

    /**
     * 刷新线程共享对象
     */
    public static <T> void refreshThreadShared(String name, T t) {
        threadSharedMap.put(name, t);
    }

    /**
     * 获取线程共享对象
     */
    public static <T> T getThreadShared(String name) {
        return (T) threadSharedMap.get(name);
    }



    /*-------------------------------------------------------------------------*/
    /*------------------------------ ThreadLocal ------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * threadLocal名 -> ThreadLocal的Map
     */
    private static final Map<String, ThreadLocal<?>> threadLocalMap = new ConcurrentHashMap<>();

    /**
     * 刷新ThreadLocal，没有则创建，接收给定值
     */
    public static <T> void refreshThreadLocal(String name, T t) {
        getThreadLocal(name, null).set(t);
    }

    /**
     * 刷新ThreadLocal，没有则创建，接收给定supplier
     */
    public static <T> void refreshThreadLocal(String name, Supplier<T> initial) {
        getThreadLocal(name, initial);
    }

    /**
     * 获取ThreadLocal值
     */
    public static <T> T getThreadLocalValue(String name) {
        return (T) getThreadLocal(name, null).get();
    }

    /**
     * 删除ThreadLocal
     */
    public static void removeThreadLocal(String name) {
        ThreadLocal<?> threadLocal = threadLocalMap.remove(name);
        if (threadLocal != null) {
            threadLocal.remove();
        }
    }

    /**
     * 获取ThreadLocal，没有则创建，每次获取调用supplier（若不为null）
     */
    private static <T> ThreadLocal<T> getThreadLocal(String name, Supplier<T> supplier) {
        return (ThreadLocal<T>) threadLocalMap.computeIfAbsent(
                name, key -> supplier == null ?
                        new ThreadLocal<>() : ThreadLocal.withInitial(supplier));
    }

//    /**
//     * 刷新TransmittableThreadLocal，没有则创建，接收给定值
//     */
//    public static <T> void refreshTransmittableThreadLocal(String name, T t) {
//        getThreadLocal(name, null).set(t);
//    }
//
//    /**
//     * 刷新TransmittableThreadLocal，没有则创建，接收给定supplier
//     */
//    public static <T> void refreshTransmittableThreadLocal(String name, Supplier<T> initial) {
//        getThreadLocal(name, initial);
//    }
//
//    /**
//     * 获取TransmittableThreadLocal，没有则创建，每次获取调用supplier（若不为null）
//     */
//    private static <T> ThreadLocal<T> getTransmittableThreadLocal(String name, Supplier<T> supplier) {
//        return (ThreadLocal<T>) threadLocalMap.computeIfAbsent(
//                name, key -> supplier == null ?
//                        new TransmittableThreadLocal<>() : TransmittableThreadLocal.withInitial(supplier));
//    }



    /*-------------------------------------------------------------------------*/
    /*-------------------------------- 结束标记 --------------------------------*/
    /*-------------------------------------------------------------------------*/

    // 队列结束标记map，队列 -> 结束标记
    private static final Map<Queue<?>, Object> endMarkerMap = new ConcurrentHashMap<>();

    /**
     * 往队列末尾put一个结束标记
     */
    public static <T> void putEnd(BlockingQueue<T> queue, T endMarker) {
        putEnd(queue, endMarker, 1);
    }

    /**
     * 往队列末尾transfer一个结束标记
     */
    public static <T> void transferEnd(TransferQueue<T> queue, T endMarker) {
        transferEnd(queue, endMarker, 1);
    }

    /**
     * 往队列末尾put指定个结束标记
     */
    public static <T> void putEnd(BlockingQueue<T> queue, T endMarker, long count) {
        try {
            // 记录结束标记
            endMarkerMap.put(queue, endMarker);
            // 放入queue
            for (long i = 0; i < count; i++) {
                queue.put(endMarker);
            }
        } catch (InterruptedException e) {
            log.error("Interrupted exception while await put {} to {}.", endMarker, queue, e);
        }
    }

    /**
     * 往队列末尾transfer指定个结束标记
     */
    public static <T> void transferEnd(TransferQueue<T> queue, T endMarker, long count) {
        try {
            // 记录结束标记
            endMarkerMap.put(queue, endMarker);
            // 放入queue
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
    public static <T> boolean isEnd(Queue<T> queue, T obj) {
        Object endMarker = endMarkerMap.get(queue);
        return endMarker != null && endMarker == obj;
    }

    /**
     * 清除队列对应的结束标记
     */
    public static <T> void clearEnd(Queue<T> queue) {
        endMarkerMap.remove(queue);
    }



    /*-------------------------------------------------------------------------*/
    /*------------------------------- 线程池创建 -------------------------------*/
    /*-------------------------------------------------------------------------*/

    /**
     * 线程池装饰类
     */
    public static class ThreadPool implements Executor {
        @Getter
        private final String name;
        @Getter
        private final String type;
        private final ThreadPoolExecutor executor;

        private ThreadPool(String name, String type, ThreadPoolExecutor executor) {
            this.name = name;
            this.type = type;
            this.executor = executor;
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
            return executor;
        }

        /**
         * 关闭线程池，并且丢弃记录
         */
        public void shutdownAndDiscard() {
            executor.shutdown();
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
            return executor.isShutdown();
        }

        /**
         * 添加一个任务到线程池中执行
         * 若需指定该任务await，使用{@link ThreadPool#execute(Runnable, int)}
         */
        public void execute(@NonNull Runnable task) {
            latchMap.put(task, new CountDownLatch(1));
            executor.execute(decorate(task));
        }

        /**
         * 添加taskCount个任务到线程池执行，返回原始task，可用来await
         */
        public Runnable execute(@NonNull Runnable task, int taskCount) {
            latchMap.put(task, new CountDownLatch(taskCount));

            Runnable decorator = decorate(task);
            for (int i = 0; i < taskCount; i++) {
                executor.execute(decorator);
            }
            return task;
        }

        /**
         * 添加一个任务到线程池中执行，返回Future
         */
        public <T> RunnableFuture<T> submit(@NonNull Callable<T> task) {
            RunnableFuture<T> runnableFuture = new FutureTaskDecorator<>(task);
            latchMap.put(runnableFuture, new CountDownLatch(1));

            executor.execute(new RunnableDecorator(runnableFuture));
            return runnableFuture;
        }

        /**
         * 添加一组任务到线程池执行，返回一组Future
         */
        public <T> List<Future<T>> invokeAll(@NonNull List<Callable<T>> taskList) {
            try {
                return executor.invokeAll(taskList);
            } catch (InterruptedException e) {
                log.error("Interrupted exception while invoke all task.", e);
            }
            return null;
        }

        /**
         * 添加一组任务到线程池执行，任意一个任务返回结果
         */
        public <T> T invokeAny(@NonNull List<Callable<T>> taskList) {
            try {
                return executor.invokeAny(taskList);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Interrupted or execution exception while invoke any task.", e);
            }
            return null;
        }

        /**
         * 添加一个任务到线程池中，一定延时后执行，返回原始task，可用来await
         */
        public Runnable schedule(@NonNull Runnable task, long delay, @NonNull TimeUnit unit) {
            try {
                latchMap.put(task, new CountDownLatch(1));

                ((ScheduledThreadPoolExecutor) executor).schedule(
                        decorate(task), delay, unit);
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

                Runnable decorator = decorate(task);
                ScheduledThreadPoolExecutor scheduledThreadPool = (ScheduledThreadPoolExecutor) executor;
                for (int i = 0; i < taskCount; i++) {
                    scheduledThreadPool.schedule(decorator, delay, unit);
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

                ((ScheduledThreadPoolExecutor) executor).scheduleAtFixedRate(
                        decorate(task), delay, period, unit);
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

                Runnable decorator = decorate(task);
                ScheduledThreadPoolExecutor scheduledThreadPool = (ScheduledThreadPoolExecutor) executor;
                for (int i = 0; i < taskCount; i++) {
                    scheduledThreadPool.scheduleAtFixedRate(decorator, delay, period, unit);
                }
            } catch (ClassCastException e) {
                log.error("This threadPool is not a scheduledThreadPool, please checked it.", e);
            }
            return task;
        }

        /**
         * 返回Runnable装饰器，增加任务执行完成计数功能
         */
        private Runnable decorate(Runnable origin) {
            return new RunnableDecorator(origin);
        }

        /**
         * Runnable装饰器，增加任务执行完成计数功能
         */
        @AllArgsConstructor
        private class RunnableDecorator implements Runnable {
            Runnable origin;

            @Override
            public void run() {
                try {
                    origin.run();
                } finally {
                    countDown(origin);
                }
            }
        }

        /**
         * FutureTask装饰器，增加获取原始任务名称功能
         */
        private static class FutureTaskDecorator<T> extends FutureTask<T> {
            Callable<T> origin;

            public FutureTaskDecorator(Callable<T> callable) {
                super(callable);
                origin = callable;
            }
        }



        /*---------------------------- CountDownLatch ----------------------------*/

        /**
         * 原始任务与CountDownLatch的映射
         */
        private final Map<Runnable, CountDownLatch> latchMap = new ConcurrentHashMap<>();

        /**
         * 获取当前 任务 -> count 的信息
         */
        public String infoLatchMap() {
            Map<String, Long> taskNameToCountMap = new HashMap<>();
            latchMap.forEach((k, v) -> {
                String taskName = k instanceof FutureTaskDecorator ?
                        getSimpleName(((FutureTaskDecorator<?>) k).origin) :
                        getSimpleName(k);
                taskNameToCountMap.put(taskName, v.getCount());
            });
            return taskNameToCountMap.toString();
        }

        /**
         * 任务未完成数-1
         */
        private void countDown(@NonNull Runnable task) {
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
                CountDownLatch latch = latchMap.get(task);
                if (latch != null)
                    latch.await();
            } catch (InterruptedException e) {
                log.error("Interrupted exception while await {}.", task, e);
            }
        }

        /**
         * 等待一定时间，返回指定任务是否执行完成
         */
        public boolean await(@NonNull Runnable task, long timeout, TimeUnit unit) {
            try {
                CountDownLatch latch = latchMap.get(task);
                return latch == null || latch.await(timeout, unit);
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



    /*------------------------------ Support ------------------------------*/

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
     * 获取当前所有线程池的latchInfo
     */
    public static Map<String, String> infoLatchInfoMap() {
        Map<String, String> latchInfoMap = new HashMap<>();
        theadPoolMap.forEach((k, v) -> latchInfoMap.put(k, v.infoLatchMap()));
        return latchInfoMap;
    }

    /**
     * 获取当前 线程共享对象 的信息
     */
    public static String infoThreadSharedMap() {
        return threadSharedMap.toString();
    }

    /**
     * 获取当前 ThreadLocal 的信息
     */
    public static String infoThreadLocalMap() {
        return threadLocalMap.toString();
    }

    /**
     * 获取当前 结束标记 的信息
     */
    public static String infoEndMarkerMap() {
        Map<String, Object> idToEndMarkerMap = new HashMap<>();
        endMarkerMap.forEach((k, v) -> idToEndMarkerMap.put(getSimpleName(k), v));
        return idToEndMarkerMap.toString();
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
     * 获取类的简名
     */
    private static String getSimpleName(Object obj) {
        return obj.getClass().getSimpleName();
    }

    /**
     * 返回一个Executor对象，该对象直接在当前线程执行任务，不会创建新线程
     */
    public static Executor directExecutor() {
        return Runnable::run;
    }

    /**
     * sleep
     */
    public static void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            log.error("Interrupted exception while sleep.", e);
        }
    }
}