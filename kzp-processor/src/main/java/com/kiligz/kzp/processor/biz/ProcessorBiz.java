package com.kiligz.kzp.processor.biz;

import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Ivan
 * @since 2023/1/30
 */
@Service
@RequiredArgsConstructor
public class ProcessorBiz {

    private final RemoteUserService remoteUserService;

    public User getUser(Integer id) {
        return remoteUserService.get(id);
    }

}
