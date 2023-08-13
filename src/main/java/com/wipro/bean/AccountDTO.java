package com.wipro.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountDTO {
    private Integer accountId;
    private String accountType;
    private Double balance;
    private Integer customerId;
    private String customerName;
    private String customerAddress;
    private String customerMobileNum;

    // Constructors, getters, setters
}
