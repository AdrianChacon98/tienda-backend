package com.store.example.enums;

public enum Authorities {

    //User
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    //Admin
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write");

    private final String authority;

    Authorities(String authority){
        this.authority=authority;
    }

    public String getAuthority() {
        return authority;
    }
}
