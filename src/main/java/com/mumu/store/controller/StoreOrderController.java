package com.mumu.store.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.mumu.entity.User;
import com.mumu.store.entity.Commodity;
import com.mumu.store.entity.DTO.OrderItemDTO;
import com.mumu.store.entity.DTO.StoreOrderDTO;
import com.mumu.store.entity.StoreOrder;
import com.mumu.store.entity.StoreOrderItem;
import com.mumu.store.service.CommodityService;
import com.mumu.store.service.StoreOrderItemService;
import com.mumu.store.service.StoreOrderService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.RedisLockService;
import com.mumu.utils.RedisOptService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
@RestController
@Slf4j
@RequestMapping("/store-order")
public class StoreOrderController {
    @Autowired
    StoreOrderService service;
    @Autowired
    CommodityService commodityService;
    @Autowired
    StoreOrderItemService storeOrderItemService;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    RedisOptService redisOptService;
    @Autowired
    RedisLockService redisLockService;

    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${ali.itemReturnUrl}")
    private String itemReturnUrl; // 支付成功返回的页面
    @Value("${ali.itemNotifyUrl}")
    private String itemNotifyUrl; // 支付宝支付成功异步回调的页面，需要内网穿透

    @ApiOperation(value = "跳转到这个链接进行支付", tags = "商店类")
    @GetMapping("/air/pay")
    public void payOrder(@RequestParam Integer orderId, HttpServletResponse httpResponse) throws IOException {

        // 4.准备支付的参数，包括需要支付的订单的id，价格，物品名称，
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo("g" + orderId); // 需要支付的订单id，自定义的订单id，不能重复，唯一，已经支付的无法继续支付
        model.setTotalAmount(service.getPrice(orderId)); // 需要支付的钱 model.setTotalAmount("88.88");
        model.setSubject("纪念品商店订单"); // 要支付的物品，比如 model.setSubject("Iphone6 16G");
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 5.创建支付的请求，把上面的准备支付的参数进行set
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizModel(model);

        /**
         * 设置支付宝异步回调，这个需要用内网穿透
         */
        request.setNotifyUrl(itemNotifyUrl);

        /**
         * 设置支付宝支付成功返回的页面
         * 这里让支付成功直接回到static下的一个静态页面中
         */
        request.setReturnUrl(itemReturnUrl);

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

    @ApiOperation(value = "跳转到这个链接进行告知成功", tags = "商店类")
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
            System.out.println("在" + paramsMap.get("gmt_payment") + "，买家" + paramsMap.get("buyer_id") + "进行订单" + paramsMap.get("out_trade_no") + "的付款，交易名称" + paramsMap.get("subject") + "，付款金额" + paramsMap.get("total_amount") + "");
            StoreOrder o = new StoreOrder();
            o.setOrderId(Integer.parseInt(parameterMap.get("out_trade_no")[0].substring(1)));
            o.setPayTime(LocalDateTime.now().withNano(0));
            service.pay(o);
            // 支付成功，修改数据库中该订单的状态
            // 比如：流程，根据订单ID查询出一条数据，修改该条订单的数据，或者只有支付成功，才给数据库里面新增一条数据
        } else {
            System.out.println("*************");
            System.out.println(rsa256CheckSignature);
            throw new RuntimeException();
        }
    }

    @ApiOperation(value = "显示当前用户全部订单", tags = "商店类")
    @GetMapping("/getallorder")
    public AjaxJson getAllOrder(Integer userId) {
        User u = new User();
        try {
            u.setUserId(Integer.parseInt((String) StpUtil.getLoginId()));
        } catch (Exception e) {
            u.setUserId(userId);
        }
        return AjaxJson.getSuccessData(service.getUserOrder(u));
    }

    //todo:这个类中未确认用户是否登录，是否显示的是他选中的订单
    //todo:添加订单号等信息
    @ApiOperation(value = "显示当前用户选中订单", tags = "商店类")
    @GetMapping("/getselectedorder")
    public AjaxJson getSelectedOrder(@RequestParam Integer orderId) {
        return AjaxJson.getSuccessData(service.getUserOrder(orderId));
    }


    @ApiOperation(value = "提交用户订单", notes = """
            body example:{
                           "commodityList": [
                             {
                               "itemCount": 1,
                               "itemId": 1
                             },
                         {
                               "itemCount": 1,
                               "itemId": 2
                             }
                           ],
                           "userId": 3
                         }""", tags = "商店类")
//    @SaCheckLogin
    @PostMapping("/orderfood")
    public AjaxJson orderFood(@RequestBody StoreOrderDTO order) {
        System.out.println(order);
        StoreOrder o = new StoreOrder();
        try {
            o.setUserId(Integer.parseInt((String) StpUtil.getLoginId()));
        } catch (Exception e) {
            o.setUserId(3);
        }
        o.setOrderTime(LocalDateTime.now().withNano(0));
        if (!service.save(o)) {
            throw new RuntimeException("fail to add a new order");
        }
        Integer orderId = o.getOrderId();
        List<StoreOrderItem> foodOrderList = new ArrayList<>();
        redisLockService.lock("store");
        List<Commodity> commodityList = (List<Commodity>) redisOptService.get("item");
        if (commodityList == null) {
            commodityList = commodityService.getAll();
            redisOptService.set("item", commodityList);
            for (Commodity c : commodityList) {
                redisOptService.set("item" + c.getCommodityId(), c);
            }
        }
        System.out.println(commodityList.toString());
        for (OrderItemDTO orderDTO : order.getCommodityList()) {
            Commodity c;
            try {
                c = (Commodity) redisOptService.get("item" + orderDTO.getItemId());
            } catch (Exception e) {
                redisLockService.unlock("store");
                e.printStackTrace();
                throw new RuntimeException();
            }
            if ((c).getAmountLeft() > 0 && orderDTO.getItemCount() > 0) {
                StoreOrderItem storeOrderItem = new StoreOrderItem();
                storeOrderItem.setItemId(orderDTO.getItemId());
                storeOrderItem.setItemCount(orderDTO.getItemCount());
                storeOrderItem.setOrderId(orderId);
                c.setAmountLeft(c.getAmountLeft() - orderDTO.getItemCount());
                redisOptService.set("item" + orderDTO.getItemId(), c);
                foodOrderList.add(storeOrderItem);
            } else {
                for (StoreOrderItem s : foodOrderList) {
                    c = (Commodity) redisOptService.get("item" + s.getItemId());
                    c.setAmountLeft(c.getAmountLeft() - s.getItemCount());
                    redisOptService.set("item" + s.getItemId(), c);
                }
                redisLockService.unlock("store");
                throw new RuntimeException(c.getCommodityName() + "已经被买完了，下次再来吧");
            }
        }
        redisOptService.set("item", commodityList);
        redisLockService.unlock("store");
        if (!storeOrderItemService.saveBatch(foodOrderList)) {
            throw new RuntimeException("fail to add the food on the order");
        }
        StoreOrderDTO s = service.getUserOrder(orderId);
        Double price = 0.0;
        for (OrderItemDTO d : s.getCommodityList()) {
            price += d.getItemCount() * d.getCommoditylist().get(0).getPrice();
        }
        o.setPrice(price);
        service.updateById(o);
        return AjaxJson.getSuccessData(orderId);
    }

    @ApiOperation(value = "支付成功后查询是否支付成功", tags = "商店类")
    @SaCheckLogin
    @GetMapping("/payed")
    public AjaxJson payedFood(@RequestParam Integer orderId) {
        return service.getPayState(orderId) ? AjaxJson.getSuccess() : AjaxJson.getError("Not Payed");
    }
//    @ApiOperation(value = "更改用户订单", notes = """
//            body example:{
//            "orderId": 0,
//               "foodGetTime": "2023-12-02T15:16:41",
//               "foodOrders": [
//                 {
//                   "food": [
//                     {
//                       "foodId": 1,
//                       "foodName": "米饭"
//                     }
//                   ],
//                   "foodAmount": 1
//                 },
//                 {
//            "food": [
//            {
//            "foodId": 2,
//            "foodName": "鱼香肉丝"
//            }
//            ],
//            "foodAmount": 1
//            }
//               ]
//             },
//
//            ]
//            }""", tags = "商店类")
//    @SaCheckLogin
//    @PostMapping("/orderfood/change")
//    public AjaxJson changeOrder(@RequestBody OrderDTO order) {
//        System.out.println(order);
//        Order o = new Order();
//        o.setOrderId(order.getOrderId());
//        o.setFoodGetTime(order.getFoodGetTime());
//        o.setFoodOrderTime(LocalDateTime.now().withNano(0));
//        if (!service.updateById(o)) {
//            throw new RuntimeException("fail to update order");
//        }
//        List<FoodOrder> foodOrderList = new ArrayList<>();
//        foodOrderService.delete(order.getOrderId());
//        for (FoodOrderDTO foodOrderDTO : order.getFoodOrders()) {
//            FoodOrder foodOrder = new FoodOrder();
//            foodOrder.setFoodId(foodOrderDTO.getFood().get(0).getFoodId());
//            foodOrder.setFoodAmount(foodOrderDTO.getFoodAmount());
//            foodOrder.setOrderId(order.getOrderId());
//            foodOrderList.add(foodOrder);
//        }
//        if (!foodOrderService.saveBatch(foodOrderList)) {
//            throw new RuntimeException("fail to add the food on the order");
//        }
//        return AjaxJson.getSuccessData(order.getOrderId());
//    }

//    @ApiOperation(value = "支付成功后调用设置支付成功", tags = "商店类")
//    @SaCheckLogin
//    @GetMapping("/pay")
//    public AjaxJson payFood(@RequestParam Integer orderId) {
//        //todo:check payed
//        Order o = new Order();
//        o.setOrderId(orderId);
//        o.setPayTime(LocalDateTime.now().withNano(0));
//        service.pay(o);
//        return AjaxJson.getSuccess();
//    }
//
//    @ApiOperation(value = "支付成功后查询是否支付成功", tags = "商店类")
//    @SaCheckLogin
//    @GetMapping("/payed")
//    public AjaxJson payedFood(@RequestParam Integer orderId) {
//        return service.getPayState(orderId) ? AjaxJson.getSuccess() : AjaxJson.getError("Not Payed");
//    }

    @ApiOperation(value = "删除订单", tags = "商店类")
    @GetMapping("/delete")
    public AjaxJson deleteOrder(@RequestParam Integer orderId) {
        if (!service.deleteByOrderId(orderId)) {
            throw new RuntimeException("fail to delete order");
        }
        redisLockService.lock("store");
        redisOptService.deleteKey("item");
        redisLockService.unlock("store");

        return AjaxJson.getSuccess();
    }
}

