package com.example.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("seat_lock")
public class SeatLock {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

    private Integer seatRow;

    private Integer seatCol;

    private Long userId;

    private Long orderId;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;
}