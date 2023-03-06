package com.kiligz.kzp.processor.record;

import com.kiligz.kzp.common.domain.MsgWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 * @since 2023/2/7
 */
public class MsgManager {
    List<MsgWrapper> msgWrapperList = new ArrayList<>();

    public void accept(Operation operation) {
        for (MsgWrapper msgWrapper : msgWrapperList) {
            operation.accept(msgWrapper);
        }
    }

    public void add(MsgWrapper msgWrapper) {
        msgWrapperList.add(msgWrapper);
    }

    public void remove(MsgWrapper msgWrapper) {
        msgWrapperList.remove(msgWrapper);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>()
        ;
        for (int i = 1; i < list.size() - 1; i++) {
            System.out.println("1");
        }
    }
}
