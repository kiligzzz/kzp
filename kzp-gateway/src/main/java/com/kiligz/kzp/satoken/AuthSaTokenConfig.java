package com.kiligz.kzp.satoken;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.kiligz.kzp.common.domain.Access;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.vo.RespVO;
import com.kiligz.kzp.nacos.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * gateway的sa-token配置，全局过滤器
 *
 * @author Ivan
 * @since 2022/11/15
 */
@Slf4j
@Configuration
public class AuthSaTokenConfig {
    /**
     * 全局登录、角色、权限控制
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(NacosUtil.getDirectUrls())
                .setAuth(obj -> checkAuth())
                .setError(this::handleError);
    }

    /**
     * 登录、角色、权限校验
     */
    private void checkAuth() {
        // 所有uri进行登录校验
        SaRouter.match("/**", StpUtil::checkLogin);
        System.out.println(StpUtil.getRoleList());
        System.out.println(StpUtil.getPermissionList());

        // 读取配置校验角色、权限
        List<Access> accessList = NacosUtil.getAccessList();
        for (Access access : accessList) {
            String uri = access.getUri();
            String accessible = access.getAccessible();

            if (accessible.contains("|")) {
                SaFunction checkFunction = access.isRole() ?
                        () -> StpUtil.checkRoleOr(accessible.split("\\|")) :
                        () -> StpUtil.checkPermissionOr(accessible.split("\\|"));
                SaRouter.match(uri, checkFunction);
            } else if (accessible.contains(",")) {
                SaRouter.match(uri,
                        () -> StpUtil.checkPermissionAnd(accessible.split(",")));
            } else {
                SaFunction checkFunction = access.isRole() ?
                        () -> StpUtil.checkRole(accessible) :
                        () -> StpUtil.checkPermission(accessible);
                SaRouter.match(uri, checkFunction);
            }
        }
    }

    /**
     * 处理异常
     */
    private RespVO<?> handleError(Throwable e) {
        if (e instanceof NotLoginException) {
            return RespVO.fail(
                    Status.gateway(StatusEnum.NOT_LOGIN));
        } else {
            return RespVO.fail(
                    Status.gateway(StatusEnum.NO_PERMISSION));
        }
    }
}
