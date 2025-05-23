package com.mumu.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alipay {
    private String orderId; // 商家自定义的订单编号，唯一
    private String price; // 商品价格
    private String subject; // 支付主题
}
