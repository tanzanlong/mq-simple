package com.baibei.account.dto.exception;

/**
 * 券已使用异常
 * @author peng
 *
 */
public class TicketUsedException extends RuntimeException {

    private static final long serialVersionUID = -3085186608675944621L;

    public TicketUsedException(String message){
        super(message);
    }
}
