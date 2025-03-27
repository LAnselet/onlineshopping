package com.qiuzhitech.onlineshopping.controller;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.service.EsService;
import com.qiuzhitech.onlineshopping.service.SearchService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Controller
public class CommodityController {

  @Resource
  OnlineShoppingCommodityDao onlineShoppingCommodityDao;

  @Resource
  SearchService searchService;

  @Resource
  EsService esService;


  @RequestMapping("/addCommodity")
  public String addCommodity() {
    return "add_commodity";
  }

  @PostMapping("/commodities")
  public String commodities(@RequestParam("commodityId") long commodityId,
                            @RequestParam("commodityName") String commodityName,
                            @RequestParam("commodityDesc") String commodityDesc,
                            @RequestParam("price") int price,
                            @RequestParam("availableStock") int availableStock,
                            @RequestParam("creatorUserId") long creatorUserId,
                            Map<String, Object> resultMap) throws IOException {
    OnlineShoppingCommodity onlineShoppingCommodity = OnlineShoppingCommodity
            .builder()
            .commodityId(commodityId)
            .commodityName(commodityName)
            .commodityDesc(commodityDesc)
            .price(price)
            .lockStock(0)
            .totalStock(availableStock)
            .availableStock(availableStock)
            .creatorUserId(creatorUserId)
            .build();
    onlineShoppingCommodityDao.insertCommodity(onlineShoppingCommodity);
    esService.addCommodity(onlineShoppingCommodity);
    resultMap.put("Item", onlineShoppingCommodity);
    return "add_commodity_success";
  }

  @GetMapping("/")
  public String listCommodities(Map<String, Object> resultMap) {
    List<OnlineShoppingCommodity> onlineShoppingCommodities =
            onlineShoppingCommodityDao.listCommodities();
    resultMap.put("itemList", onlineShoppingCommodities);
    return "list_items";
  }

  @GetMapping("/commodities/{sellerId}")
  public String listItems(@PathVariable("sellerId") String sellerId,
                          Map<String, Object> resultMap) {
    List<OnlineShoppingCommodity> onlineShoppingCommodities =
            onlineShoppingCommodityDao.listCommoditiesByUserId(Long.parseLong(sellerId));
    resultMap.put("itemList", onlineShoppingCommodities);
    return "list_items";
  }

  @GetMapping("/item/{commodityId}")
  public String getItemDetail(@PathVariable("commodityId") long commodityId
          , Map<String, Object> resultMap) {
    OnlineShoppingCommodity onlineShoppingCommodity =
            onlineShoppingCommodityDao.queryCommodityById(commodityId);
    resultMap.put("commodity", onlineShoppingCommodity);
    return "item_detail";
  }

  @GetMapping("/searchAction")
  public String searchAction(@RequestParam("keyWord") String keyWord,
                             Map<String, Object> resultMap) {

//    List<OnlineShoppingCommodity> onlineShoppingCommodities = searchService.searchCommodityWithMySQL(keyWord);
    List<OnlineShoppingCommodity> onlineShoppingCommodities = searchService.searchCommodityWithES(keyWord);
    resultMap.put("itemList", onlineShoppingCommodities);
    // need to ask for template
    return "search_items";
  }
}
