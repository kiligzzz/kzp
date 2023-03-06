package com.kiligz.kzp.nacos.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kiligz.kzp.common.domain.Access;
import com.kiligz.kzp.common.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * @author Ivan
 * @since 2022/11/25
 */
@Component
@SuppressWarnings("all")
public class NacosUtil {

    private static final Map<String, Object> dataIdToValueMap = new HashMap<>();

    private static final String ACCESS_JSON = "access.json";

    @Value("${spring.cloud.nacos.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    @PostConstruct
    public void init() throws Exception {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);

        String content = configService.getConfig(ACCESS_JSON, group, 5000);
        dataIdToValueMap.put(ACCESS_JSON, convertToAccess(content));
        configService.addListener(ACCESS_JSON, group,
                new NacosListener<>(ACCESS_JSON, this::convertToAccess));
    }

    public static List<Access> getAccessList() {
        return (List<Access>) dataIdToValueMap.get(ACCESS_JSON);
    }


    @SneakyThrows
    private List<Access> convertToAccess(String content) {
        return JsonUtil.getMapper().readValue(
                content, new TypeReference<List<Access>>() {});
    }

    @AllArgsConstructor
    private static class NacosListener<T> implements Listener {
        private final String dataId;
        private final Function<String, T> converter;

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String content) {
            dataIdToValueMap.put(dataId, converter.apply(content));
        }
    }
}
