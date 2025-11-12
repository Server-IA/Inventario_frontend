package com.coagronet.auditoria;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@RequiredArgsConstructor
@Log4j2
public class RequestUtils {

    private final HttpServletRequest request;


    public String getClientIp(){
        String ip = request.getHeader("X-Forwarded-For");

        if(ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }else {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    public String getClientHost() {
        try {
            String host = request.getRemoteHost();

            if ("localhost".equalsIgnoreCase(host) || "0:0:0:0:0:0:0:1".equals(host) || "127.0.0.1".equals(host)) {
                InetAddress localHost = InetAddress.getLocalHost();
                host = localHost.getHostName();
            }
            else {
                try {
                    InetAddress remoteAddress = InetAddress.getByName(host);
                    String resolvedHostName = remoteAddress.getHostName();

                    if (!resolvedHostName.equals(host)) {
                        host = resolvedHostName;
                    }
                } catch (Exception ignored) {

                }
            }
            return host;
        } catch (Exception e) {
            return "unknown";
        }
    }

    public String getAuthenticatedRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }
        return "N/A";
    }

}
