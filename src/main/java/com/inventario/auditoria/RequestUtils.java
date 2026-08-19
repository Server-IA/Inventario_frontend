package com.inventario.auditoria;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RequestUtils {

    public String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerNames) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                String first = value.split(",")[0].trim();
                return normalizeLocalIp(first);
            }
        }

        return normalizeLocalIp(request.getRemoteAddr());
    }

    public String getClientHost(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String hostHeader = request.getHeader("Host");
        if (hostHeader != null && !hostHeader.isBlank()) {
            return hostHeader;
        }

        String serverName = request.getServerName();
        if (serverName != null && !serverName.isBlank()) {
            return serverName;
        }

        String ip = getClientIp(request);
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            String canonicalHostName = inetAddress.getCanonicalHostName();
            if (canonicalHostName != null && !canonicalHostName.isBlank()) {
                return canonicalHostName;
            }
        } catch (UnknownHostException e) {
            log.debug("No se pudo resolver el host para la IP {}", ip, e);
        } catch (Exception e) {
            log.warn("Error inesperado resolviendo host para la IP {}", ip, e);
        }

        return "unknown";
    }

    public String getAuthenticatedRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
            return "N/A";
        }

        return auth.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
    }

    private String normalizeLocalIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return "unknown";
        }
        // IPv6 loopback → IPv4 loopback
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }
}
