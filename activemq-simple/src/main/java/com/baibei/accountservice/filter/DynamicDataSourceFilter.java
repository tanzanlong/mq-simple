package com.baibei.accountservice.filter;

import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;
import com.baibei.accountservice.multidatasource.DateSourceLocal;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *     多数据源切换拦截器
 * </p>
 *
 * @author zhangyue
 * @date 2017/10/16
 */
@Slf4j
public class DynamicDataSourceFilter implements Filter {

    public static final String PARAM_NAME_EXCLUSIONS  = "exclusions";
    public static final String EXCHANGE_KEY = "EXCHANGE";

    private Set<String> excludesPattern;

    protected PatternMatcher pathMatcher = new ServletPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusions = filterConfig.getInitParameter(PARAM_NAME_EXCLUSIONS);
        if (exclusions != null && exclusions.trim().length() != 0) {
            excludesPattern = new HashSet<String>(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        //判断是否在排除之外
        if (isExclusion(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        //设置请求参数
       // String dataSourceKey = String.valueOf(request.getParameter(EXCHANGE_KEY));
        String dataSourceKey = ((HttpServletRequest)request).getHeader(EXCHANGE_KEY);
        log.info("current exchange tag:" + dataSourceKey);
        if (StringUtils.isNotEmpty(dataSourceKey) && !"null".equals(dataSourceKey)) {
            try {
                DateSourceLocal.setExchangeTag(dataSourceKey);
                filterChain.doFilter(request, response);
            }finally {
                DateSourceLocal.clean();
            }
        } else {

            log.warn("this request parameter '" + EXCHANGE_KEY + "' is empty. skip this request url=" + requestURI);

            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ObjectMapper mapper = new ObjectMapper();

            //ResponseDto responseDto = new ResponseDto();
          ///  responseDto.setStatus(false);
          //  responseDto.setCode(Constance.ERROR_CODE);
           // responseDto.setMsg("缺少交易所标识'exchange'");
            String responseDto="bussinessType不能为空";
            httpResponse.getWriter().write(mapper.writeValueAsString(responseDto));
            return;
        }
    }

    @Override
    public void destroy() {

    }

    public boolean isExclusion(String requestURI) {
        if (excludesPattern == null) {
            return false;
        }

        for (String pattern : excludesPattern) {
            if (pathMatcher.matches(pattern, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
