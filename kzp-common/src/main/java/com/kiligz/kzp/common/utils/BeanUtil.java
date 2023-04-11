package com.kiligz.kzp.common.utils;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.exception.KzpException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 使用高速缓存ASM实现的beanCopy
 *
 * @author Ivan
 * @since 2022/11/18
 */
public class BeanUtil {
    /**
     * 复制属性并返回to对象
     */
    public static <F, T> T copy(F from, T to) {
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        MethodAccess fromMethodAccess = ReflectUtil.getMethodAccess(fromClass);
        MethodAccess toMethodAccess = ReflectUtil.getMethodAccess(toClass);
        FieldAccess fromFieldAccess = ReflectUtil.getFieldAccess(fromClass);
        FieldAccess toFieldAccess = ReflectUtil.getFieldAccess(toClass);

        Field[] fromFields = fromFieldAccess.getFields();
        List<String> toNames = Arrays.asList(toFieldAccess.getFieldNames());

        // 若没读取到属性值抛出异常
        if (fromFields.length == 0 || toNames.isEmpty()) {
            throw new KzpException(Status.global(StatusEnum.BEAN_COPY),
                    from.getClass().getSimpleName() + " -> " + to.getClass().getSimpleName());
        }

        for(Field field : fromFields) {
            String name = field.getName();
            if (toNames.contains(name)) {
                String capitalize = StringUtil.capitalize(name);
                String fromPrefix = field.getType() == Boolean.class ? "is" : "get";

                Object value = fromMethodAccess.invoke(from, fromPrefix + capitalize);
                toMethodAccess.invoke(to, "set" + capitalize, value);
            }
        }
        return to;
    }
}