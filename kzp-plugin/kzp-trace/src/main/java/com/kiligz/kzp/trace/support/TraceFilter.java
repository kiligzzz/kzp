package com.kiligz.kzp.trace.support;

import com.kiligz.kzp.trace.domain.Trace;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * 全链路追踪的过滤器，rpc请求处理
 *
 * @author Ivan
 * @since 2023/3/14
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class TraceFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 当前服务的上下文
        RpcContextAttachment self = RpcContext.getClientAttachment();
        // 调用端服务的上下文
        RpcContextAttachment caller = RpcContext.getServerAttachment();
        // 是否是提供方
        boolean isProviderSide = self.isProviderSide();

        Result result;
        try {
            if (isProviderSide) {
                // 从调用端Rpc上下文中获取
                Trace trace = (Trace) caller.getObjectAttachment(Trace.KEY);
                // 更新当前服务的trace
                Trace.refresh(trace);
            } else {
                // 将trace放入当前服务Rpc上下文
                self.setAttachment(Trace.KEY, Trace.get());
            }

            // 继续执行之后逻辑
            result = invoker.invoke(invocation);
        } finally {
            if (isProviderSide) {
                // 清空ThreadLocal
                Trace.remove();
            } else {
                // 清空当前服务Rpc上下文
                self.clearAttachments();
            }
        }
        return result;
    }
}
