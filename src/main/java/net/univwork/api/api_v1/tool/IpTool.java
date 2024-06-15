package net.univwork.api.api_v1.tool;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class IpTool {

    public static String getIpAddr() {
        String ip_addr = null;
        //TODO 동시성 문제 있을 수 있어서 파라미터로 받는게 좋을듯
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        ip_addr = request.getHeader("X-Forwarded-For");
        if (ip_addr == null) {
            ip_addr = request.getHeader("Proxy-Client-IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip_addr == null) {
            ip_addr = request.getRemoteAddr();
        }
        return ip_addr;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip_addr = null;

        ip_addr = request.getHeader("X-Forwarded-For");
        if (ip_addr == null) {
            ip_addr = request.getHeader("Proxy-Client-IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip_addr == null) {
            ip_addr = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip_addr == null) {
            ip_addr = request.getRemoteAddr();
        }
        return ip_addr;
    }
}
