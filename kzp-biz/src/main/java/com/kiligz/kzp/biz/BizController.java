package com.kiligz.kzp.biz;

import com.kiligz.kzp.biz.biz.BizBiz;
import com.kiligz.kzp.common.vo.RespVO;
import com.kiligz.kzp.rpc.dto.biz.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度控制器
 *
 * @author Ivan
 * @since 2022/11/17
 */
@RestController
@RequestMapping("biz")
@RequiredArgsConstructor
public class BizController {

    private final BizBiz bizBiz;

    @PostMapping("send")
    public RespVO<?> send(@RequestBody MessageDTO messageDTO) {
        bizBiz.send(messageDTO);
        return RespVO.success();
    }
}
