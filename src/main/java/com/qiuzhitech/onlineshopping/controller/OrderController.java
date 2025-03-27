package com.qiuzhitech.onlineshopping.controller;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.CommodityService;
import com.qiuzhitech.onlineshopping.service.OrderService;
import com.qiuzhitech.onlineshopping.service.RedisService;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import javax.annotation.Resource;

@Controller
public class OrderController {

    @Resource
    OrderService orderService;

    @Resource
    CommodityService commodityService;
    @Autowired
    private RedisService redisService;

    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
    public ModelAndView buyCommodity(@PathVariable long userId,
                                     @PathVariable long commodityId) {
        ModelAndView modelAndView = new ModelAndView();
        // check whether user already buy commodity
        if (redisService.isInDenyList(userId, commodityId)) {
            modelAndView.addObject("resultInfo", "Each user have only one " +
                    "quote for this commodity");
            modelAndView.setViewName("order_result");
            return modelAndView;
        }
        OnlineShoppingOrder order =
                orderService.placeOrderRedis(commodityId, userId);
        if (order != null) {
            modelAndView.addObject("resultInfo", "Order created successfully! Order ID: " + order.getOrderNo());
            modelAndView.addObject("orderNo", order.getOrderNo());
            redisService.addToDenyList(userId, commodityId);
        } else {
            modelAndView.addObject("resultInfo", "The Commodity is out of stock");
        }
        modelAndView.setViewName("order_result");
        return modelAndView;
    }

//    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
//    public String buy(@PathVariable("userId") long userId,
//                      @PathVariable("commodityId") long commodityId,
//                      Map<String, Object> resultMap) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
////        OnlineShoppingOrder onlineShoppingOrder = orderService.placeOrderRedis(userId,
////                commodityId);
////        OnlineShoppingOrder onlineShoppingOrder =
////                orderService.placeOrderDistributedLock(commodityId, userId);
//
//        OnlineShoppingOrder onlineShoppingOrder =
//                orderService.placeOrderRedisFinal(commodityId, userId);
//        if (onlineShoppingOrder == null) {
//            resultMap.put("orderNo", 0);
//            resultMap.put("resultInfo", "fail");
//        } else {
//            resultMap.put("orderNo", onlineShoppingOrder.getOrderNo());
//            resultMap.put("resultInfo", onlineShoppingOrder);
//        }
//        return "order_result";
//    }

    @GetMapping("/commodity/orderQuery/{orderNo}")
    public String listOrders(@PathVariable("orderNo") String orderNo,
                             Map<String, Object> resultMap) {
        OnlineShoppingOrder order = orderService.queryOrderByOrderNo(orderNo);
        OnlineShoppingCommodity commodity =
                commodityService.queryCommodityById(order.getCommodityId());
        resultMap.put("order", order);
        resultMap.put("commodity", commodity);
        return "order_check";
    }

    @GetMapping("/commodity/payOrder/{orderNo}")
    public String payOrder(@PathVariable("orderNo") String orderNo,
                           Map<String, Object> resultMap) {
        orderService.updateOrderStatus(orderNo, 2);
        return listOrders(orderNo, resultMap);
    }

}

