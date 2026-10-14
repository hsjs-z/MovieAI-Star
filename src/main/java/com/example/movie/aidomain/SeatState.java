package com.example.movie.aidomain;

import lombok.Builder;
import lombok.Data;

/**
 * 作者: 枷
 * 时间:2026/10/11
 * 描述:单个座位的占用状态（已售/已被他人锁定/你已锁定）
 */
@Data
@Builder
public class SeatState {
    private Integer seatRow;
    private Integer seatCol;
    private String state;
}
