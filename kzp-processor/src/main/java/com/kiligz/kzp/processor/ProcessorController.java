package com.kiligz.kzp.processor;

import com.kiligz.kzp.common.domain.PushWrapper;
import com.kiligz.kzp.processor.biz.ProcessorBiz;
import com.kiligz.kzp.processor.pattern.Pattern;
import com.kiligz.kzp.processor.support.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan
 * @since 2022/12/30
 */
@RestController
@RequestMapping("processor")
@RequiredArgsConstructor
public class ProcessorController {

    private final ProcessorBiz processorBiz;

    @PostMapping("send")
    public void send(@RequestBody PushWrapper pw) throws InterruptedException {
//        System.out.println(pw);
        Validator.check(pw);
        Pattern.chooseAndQueue(pw);
//        System.out.println(StpUtil.getLoginId());
//        pw.getMsgWrapper().setUser(processorBiz.getUser((Integer) StpUtil.getLoginId()));
    }
}
