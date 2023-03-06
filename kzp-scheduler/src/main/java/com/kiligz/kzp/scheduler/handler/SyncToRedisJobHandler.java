package com.kiligz.kzp.scheduler.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.kiligz.kzp.common.constant.Consts;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.exception.KzpException;
import com.kiligz.kzp.common.utils.ConvertUtil;
import com.kiligz.kzp.common.utils.RedisUtil;
import com.kiligz.kzp.common.utils.ReflectUtil;
import com.kiligz.kzp.entity.user.User;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 同步更新到redis的调度器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncToRedisJobHandler {
    /**
     * 目标类 -> 转换方法 map
     */
    private static final Map<Class<?>, Function<String, ?>> classToConverterMap = new HashMap<Class<?>, Function<String, ?>>() {{
        put(String.class, str -> str);
        put(Integer.class, Integer::parseInt);
        put(Timestamp.class, ConvertUtil::toTimestamp);
    }};

    /**
     * 需保证缓存一致性的数据表名 -> 转换方法 map
     */
    private final Map<String, Function<List<Column>, ?>> tableNameToConverterMap = new HashMap<String, Function<List<Column>, ?>>() {{
        put("user.user", SyncToRedisJobHandler::convertToUser);
    }};

    /**
     * canal端口
     */
    private static final int CANAL_PORT = 11111;

    /**
     * 主机地址
     */
    @Value("${host}")
    private String host;

    /**
     * canal连接器
     */
    private CanalConnector connector;

    /**
     * redis工具类
     */
    private final RedisUtil redisUtil;

    /**
     * 初始化canal连接器
     */
    @PostConstruct
    private void init() {
        connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(host, CANAL_PORT), Consts.KZP, "", "");
        connect();
    }

    /**
     * 同步Redis任务
     */
    @XxlJob("syncToRedisJobHandler")
    public void syncToRedisJobHandler() {
        try {
            Message message = connector.getWithoutAck(1000);
            long batchId = message.getId();
            List<Entry> entries = message.getEntries();

            if (batchId != -1 && parseEntries(entries)) {
                connector.ack(batchId);
            }
        } catch (CanalClientException e) {
            connect();
            log.error("Canal connection is lost, retry reconnect.", e);
            throw e;
        }
    }

    /**
     * 解析Entry集
     */
    private boolean parseEntries(List<Entry> entries) {
        try {
            for (Entry entry : entries) {
                Header header = entry.getHeader();
                String tableName = getTableName(header);
                if (!tableNameToConverterMap.containsKey(tableName)
                        || entry.getEntryType() == EntryType.TRANSACTIONEND
                        || entry.getEntryType() == EntryType.TRANSACTIONBEGIN)
                    continue;

                RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                for (RowData rowData : rowChange.getRowDatasList()) {
                    // 将更新同步到redis
                    syncToRedis(rowChange.getEventType(), rowData, tableName);
                }

                log.info("SyncToRedis: table[{}.{}], eventType[{}]",
                        header.getSchemaName(), header.getTableName(), rowChange.getEventType());
            }
            return true;
        } catch (Exception e) {
            log.error("Parse entries error.", e);
        }
        return false;
    }

    /**
     * 获取表名，database.table
     */
    private static String getTableName(Header header) {
        return header.getSchemaName() + "." + header.getTableName();
    }

    /**
     * 将更新同步到redis，以kzp:${database}.${table}:${userId}作为key
     */
    private void syncToRedis(CanalEntry.EventType eventType, RowData rowData, String tableName) {
        boolean isDelete = eventType == EventType.DELETE;
        List<Column> columnList = isDelete ?
                rowData.getBeforeColumnsList() : rowData.getAfterColumnsList();

        String redisKey = getRedisKey(tableName, columnList.get(0).getValue());
        if (isDelete) {
            redisUtil.del(redisKey);
            return;
        }

        Object value = tableNameToConverterMap.get(tableName).apply(columnList);
        redisUtil.set(redisKey, value);
    }


    /**
     * 获取redisKey
     */
    private static String getRedisKey(String tableName, String id) {
        return Consts.KZP + ":" + tableName + ":" + id;
    }


    /**
     * 建立canal连接
     */
    private void connect() {
        connector.disconnect();
        connector.connect();
        connector.subscribe("user\\..*");
        connector.rollback();
    }

    /**
     * 将columns转换为User
     */
    private static User convertToUser(List<Column> columnList) {
        return convertTo(columnList, User.class);
    }

    /**
     * 将columns转换为指定对象
     */
    private static <T> T convertTo(List<Column> columnList, Class<T> clazz) {
        FieldAccess fieldAccess = ReflectUtil.getFieldAccess(clazz);
        Field[] fields = fieldAccess.getFields();
        try {
            T target = clazz.newInstance();
            for (Field field : fields) {
                for (CanalEntry.Column column : columnList) {
                    String columnName = StrUtil.toCamelCase(column.getName());
                    String columnValue = column.getValue();

                    if (columnValue.isEmpty()
                            || !field.getName().equals(columnName) )
                        continue;

                    // 将value转换为对应类型
                    Object value = classToConverterMap.get(field.getType()).apply(columnValue);

                    field.setAccessible(true);
                    field.set(target, value);
                }
            }
            return target;
        } catch (Exception e) {
            throw new KzpException(e, Status.scheduler(StatusEnum.CONVERT_ERROR));
        }
    }
}