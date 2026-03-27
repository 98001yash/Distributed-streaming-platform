package com.distributed_streaming_platform.user_service.auth;

public class UserContextHolder {

    private static final ThreadLocal<String> userEmail = new ThreadLocal<>();
    private static final ThreadLocal<String> userRole = new ThreadLocal<>();

    public static void setUser(String email, String role) {
        userEmail.set(email);
        userRole.set(role);
    }

    public static String getUserEmail() {
        return userEmail.get();
    }

    public static String getUserRole() {
        return userRole.get();
    }

    public static void clear() {
        userEmail.remove();
        userRole.remove();
    }
}