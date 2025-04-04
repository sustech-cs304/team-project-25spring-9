package com.mumu.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.mumu.utils.Alipay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 支付宝的支付接口
 * 用户名：flrnht7304@sandbox.com
 * 密码：111111
 */

// http://localhost:9090/api/ali/pay?orderId=1213123&price=12.56&subject=iPhone
@RestController
@RequestMapping("/api/ali")
@Slf4j
public class AliPayController {

    @Autowired
    private AlipayClient alipayClient;

    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${ali.foodReturnUrl}")
    private String foodReturnUrl; // 支付成功返回的页面
    @Value("${ali.itemReturnUrl}")
    private String itemReturnUrl; // 支付成功返回的页面

    @Value("${ali.foodNotifyUrl}")
    private String foodNotifyUrl; // 支付宝支付成功异步回调的页面，需要内网穿透
    @Value("${ali.itemNotifyUrl}")
    private String itemNotifyUrl; // 支付宝支付成功异步回调的页面，需要内网穿透

    @GetMapping("/pay")
    public void payOrder(Alipay alipay, HttpServletResponse httpResponse) throws IOException {

        // 4.准备支付的参数，包括需要支付的订单的id，价格，物品名称，
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(alipay.getOrderId()); // 需要支付的订单id，自定义的订单id，不能重复，唯一，已经支付的无法继续支付
        model.setTotalAmount(alipay.getPrice()); // 需要支付的钱 model.setTotalAmount("88.88");
        model.setSubject(alipay.getSubject()); // 要支付的物品，比如 model.setSubject("Iphone6 16G");
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 5.创建支付的请求，把上面的准备支付的参数进行set
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizModel(model);

        /**
         * 设置支付宝异步回调，这个需要用内网穿透
         */
        request.setNotifyUrl(foodNotifyUrl);

        /**
         * 设置支付宝支付成功返回的页面
         * 这里让支付成功直接回到static下的一个静态页面中
         */
        request.setReturnUrl(foodReturnUrl);

        // 6.调用ali客户端alipayClient，用客户端执行请求，获取响应，获取.getBody()，拿到form表单
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
            log.debug(">>>>>>getAliPayFrom");
            System.out.println("getAliPayFrom");
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 表单数据作为响应返回给前端，显示前端页面
        httpResponse.setContentType("text/html;charset=UTF-8"); // 设置头部
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    /**
     * 支付宝异步回调
     * request.setNotifyUrl("");
     * http://localhost:9090/api/ali/notify
     * 上面这个网址用内网穿透代理一下
     * http://jqdxgm.natappfree.cc/api/ali/notify
     * 此时支付宝就能回调到这个页面
     */
    @PostMapping("/notify")
    public void aliPayNotify(HttpServletRequest request) throws AlipayApiException {
        log.debug(">>>>>>>支付宝异步回调");
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> keySet = parameterMap.keySet(); // 获取http请求里面的所有键

        /**
         * paramsMap如下：
         * {gmt_create=2023-07-31 15:54:11, charset=UTF8,
         * gmt_payment=2023-07-31 15:54:26,
         * notify_time=2023-07-31 15:54:27,
         * sign=fsCABNzc5hax4mwwMULluDiEAT70Kqj77uTMcCgSi82AU6tP5LGbXucEvP7CbvjXrYo5g3hrz5xRQAwddE7qU9Qyrg0v3EnearJBcW4It6N+VNBQ7yfY/W79eKRSKspLBKHRa21RILjyRrmQYG4Cw8R7twP7y0lDCOE8j3rV6ZyGfhiQ7EXp49d6HpgcIT1NjgJjQYSyJFdgyqkzFljKRfbhwPFAtubsmd8IcJCU7XI3YosSKnDhQaCA6ec4dmQiWtvcTbOLNR/r2Sou7rCnI7s1lc8pKeEsuacWTZW2FVR7hdHoLZ/expaRQIt+dNzA86lwQxu3SRCQ9wNTPICv1A==,
         * buyer_id=2088722005286475, invoice_amount=12.56, version=1.0, notify_id=2023073101222155427086470500776505,
         * fund_bill_list=[{"amount":"12.56","fundChannel":"ALIPAYACCOUNT"}],
         * notify_type=trade_status_sync,
         * subject=iPhone, // 物品名称
         * out_trade_no=202307311553, // 进行支付的订单id，唯一，商家自定义，即支付时传入的 String orderId
         * total_amount=12.56, // 总价格
         * trade_status=TRADE_SUCCESS, // 支付状态
         * trade_no=2023073122001486470500697216, auth_app_id=9021000123613164,
         * receipt_amount=12.56, point_amount=0.00,
         * buyer_pay_amount=12.56, app_id=9021000123613164, sign_type=RSA2, seller_id=2088721005318559}
         */
        Map<String, String> paramsMap = new HashMap<>(); // 专门用来放置请求里面的参数
        for (String key : keySet) {
            paramsMap.put(key, request.getParameter(key));
        }
        System.out.println("*************");
        System.out.println(paramsMap);

        // 验证签名
        String sign = paramsMap.get("sign");
        String contentV1 = AlipaySignature.getSignCheckContentV1(paramsMap);
        boolean rsa256CheckSignature = AlipaySignature.rsa256CheckContent(contentV1, sign, alipayPublicKey, "UTF-8");
        if (rsa256CheckSignature && "TRADE_SUCCESS".equals(paramsMap.get("trade_status"))) {
            // 签名验证成功 并且 支付宝回调的状态是成功状态 TRADE_SUCCESS
            log.info("在{}，买家{}进行订单{}的付款，交易名称{}，付款金额{}",
                    paramsMap.get("gmt_payment"), paramsMap.get("buyer_id"), paramsMap.get("out_trade_no"), paramsMap.get("subject"), paramsMap.get("total_amount"));

            // 支付成功，修改数据库中该订单的状态
            // 比如：流程，根据订单ID查询出一条数据，修改该条订单的数据，或者只有支付成功，才给数据库里面新增一条数据
        }
    }

}
