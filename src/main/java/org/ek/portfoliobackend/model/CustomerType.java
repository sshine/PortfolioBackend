package org.ek.portfoliobackend.model;

import lombok.Getter;

@Getter
public enum CustomerType {
    PRIVATE_CUSTOMER("Privat kunde"),
    BUSINESS_CUSTOMER("Erhvervskunde");

    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }


}
