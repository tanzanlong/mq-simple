package com.baibei.accountservice.paycenter.dto;


import lombok.Getter;
import lombok.Setter;

public class PageBaseRequest{

    private static final long serialVersionUID = 378120618595653545L;

    @Getter
    @Setter
    private int offset;

    @Getter
    @Setter
    private int limit;

    @Getter
    @Setter
    private int currentPage;

    @Getter
    @Setter
    private int pageSize;
    

}
