package com.kiligz.kzp.nacos.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kiligz.kzp.common.domain.Access;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.exception.KzpException;
import com.kiligz.kzp.common.utils.JsonUtil;
import com.kiligz.kzp.common.utils.SpringUtil;
import com.kiligz.kzp.common.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Nacos工具类（用于实时获取nacos上的配置，并转换为相应格式）
 * <pre>
 * - 懒汉式单例获取配置，第一次调用时初始化、获取、格式化，不会重复
 * - 监听nacos上的配置，发生变化实时更新
 * - 若更新时出错，则放弃本次更新，使用上次正常更新时的配置
 * </pre>
 *
 * @author Ivan
 * @since 2022/11/25
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class NacosUtil {
    /**
     * dataId枚举 -> obj的map
     */
    private static final Map<DataId, Object> dataIdToValueMap = new HashMap<>();

    @Value("${spring.cloud.nacos.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    private ConfigService configService;

    /**
     * 获取nacos配置的访问权限
     */
    public static List<Access> getAccessList() {
        return (List<Access>) get(DataId.ACCESS);
    }

    /**
     * 获取nacos配置可直接访问的url
     */
    public static String[] getDirectUrls() {
        return (String[]) get(DataId.DIRECT_URL);
    }

    /**
     * 获取dataId对应的对象
     */
    private static Object get(DataId dataId) {
        return SpringUtil.getBean(NacosUtil.class).lazyGet(dataId);
    }

    /**
     * 从dataIdToValueMap中获取值（懒汉式单例获取）
     */
    private Object lazyGet(DataId dataId) {
        try {
            // 双重检查configService，只需初始化一次configService
            if (configService == null) {
                synchronized (NacosUtil.class) {
                    if (configService == null) {
                        Properties properties = new Properties();
                        properties.put("serverAddr", serverAddr);
                        configService = NacosFactory.createConfigService(properties);
                    }
                }
            }

            // 双重检查dataId是否已经获取
            if (!dataIdToValueMap.containsKey(dataId)) {
                synchronized (dataIdToValueMap) {
                    if (!dataIdToValueMap.containsKey(dataId)) {
                        // 获取配置
                        String content = configService.getConfig(dataId.dataId, group, 5000);
                        // 添加监听器，若发生改变则刷新
                        configService.addListener(dataId.dataId, group, new NacosListener(dataId));

                        dataIdToValueMap.put(dataId, dataId.convert(content));
                    }
                }
            }
        } catch (Exception e) {
            KzpException ke = new KzpException(
                    e, Status.global(StatusEnum.NACOS_ERROR), dataId.dataId);
            log.error(ke.toString(), e);
            throw ke;
        }
        return dataIdToValueMap.get(dataId);
    }


    /**
     * nacos监听器
     */
    @AllArgsConstructor
    private class NacosListener extends AbstractListener {
        private final DataId dataId;

        @Override
        public void receiveConfigInfo(String content) {
            try {
                dataIdToValueMap.put(dataId, dataId.convert(content));
                log.info("更新nacos配置成功 {}.", dataId.dataId);
            } catch (Exception e) {
                log.error("更新nacos配置出错，将使用上次正常的配置.", e);
            }
        }
    }


    /**
     * dataId的枚举
     */
    @AllArgsConstructor
    private enum DataId {
        ACCESS("access.json", new TypeReference<List<Access>>() {}),
        DIRECT_URL("direct_url.json", new TypeReference<String[]>() {});

        /**
         * 文件名
         */
        String dataId;

        /**
         * 转换的类型引用
         */
        TypeReference typeReference;

        /**
         * 转换为指定类型
         */
        public Object convert(String content) throws JsonProcessingException {
            content = StringUtil.removeExplain(content);
            return JsonUtil.getMapper().readValue(content, typeReference);
        }
    }
}
