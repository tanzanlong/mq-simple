package com.baibei.accountservice.paycenter.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageBaseResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 2955810230509571569L;
    
    @Getter
    @Setter
    private Long total = -1L;
    
    @Getter
    @Setter
    private Integer currentPage;
    
    @Getter
    @Setter
    private Integer pageSize;
    
    @Getter
    @Setter
    private List<T> data;
}
