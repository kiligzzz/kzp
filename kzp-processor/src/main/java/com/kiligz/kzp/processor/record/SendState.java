package com.kiligz.kzp.processor.record;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Ivan
 * @since 2023/2/3
 */
public enum SendState {
    NOT_YET_SEND {
        @Override
        void action() {
            // 配额不足？
        }
    },
    SENDING {
        @Override
        void action() {
            // 正在发送，请稍等
        }
    },
    SENT {
        @Override
        void action() {
            // 更新用户配额
        }
    };

    abstract void action();

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        String s = "sss,dfgg,sdfd";
        StringTokenizer token = new StringTokenizer(s, ",|");
        while (token.hasMoreElements()) {
            String str = token.nextToken().trim();
            if (!str.isEmpty()) {
                list.add(str);
            }
        }
        System.out.println(list);
    }
}
