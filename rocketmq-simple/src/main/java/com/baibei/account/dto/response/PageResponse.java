package com.baibei.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> implements Serializable {
    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 当前页数
     */
    private int currentPage;

    /**
     * 总条数
     */
    private long total;

    /**
     * 业务数据
     */
    private T data;
}
