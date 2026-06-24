package com.controller;

import com.javaBean.*;
import com.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.util.List;


/**
 * 商品控制器，处理商品列表展示、商品详情查看及商品搜索等前端展示相关请求。
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    
    @Resource
    private TypeService typeService;
    
    @Resource
    private CartService cartService;

    @Resource
    private ReviewService reviewService;

    @Resource
    private FavoriteService favoriteService;

    @RequestMapping("/goodList")
    /**
     * 分页显示商品列表。
     */
    public ModelAndView goodList(@RequestParam(value = "typeid", defaultValue = "0") int typeId,
                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                                  @RequestParam(value = "msg", required = false) String msg) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            PageResult<Goods> pageResult;
            if (typeId == 0) {
                pageResult = goodsService.getGoodsByPage(page, pageSize);
            } else {
                pageResult = goodsService.getGoodsByTypePage(typeId, page, pageSize);
            }
            modelAndView.addObject("goods", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.addObject("typeId", typeId);
            if (msg != null && !msg.isEmpty()) {
                modelAndView.addObject("msg", msg);
            }
            modelAndView.setViewName("goodsList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/detail")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView detail(@RequestParam("id") int id,
                               @RequestParam(value = "msg", required = false) String msg,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            Goods goods = goodsService.getGoodsById(id);
            if (goods == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                modelAndView.addObject("error", "商品不存在");
                modelAndView.setViewName("error");
                return modelAndView;
            }
            modelAndView.addObject("goods", goods);
            
            // 获取商品类型名称
            String typeName = "";
            if (goods.getTypeId() > 0) {
                Type type = typeService.getTypeById(goods.getTypeId());
                if (type != null) {
                    typeName = type.getName();
                }
            }
            modelAndView.addObject("typeName", typeName);
            
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            if (msg != null && !msg.isEmpty()) {
                modelAndView.addObject("msg", msg);
            }
            
            // 评论和评分
            List<Review> reviews = reviewService.getReviewsByGoodsId(id);
            Double avgRating = reviewService.getAvgRating(id);
            modelAndView.addObject("reviews", reviews);
            modelAndView.addObject("reviewCount", reviews.size());
            modelAndView.addObject("avgRating", avgRating != null ? avgRating : 0);
            
            // 收藏状态
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user != null) {
                modelAndView.addObject("isFavorited", favoriteService.isFavorited(user.getId(), id));
            }
            
            modelAndView.setViewName("goodsDetail");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/search")
    /**
     * 按关键词搜索商品。
     */
    public ModelAndView search(@RequestParam("keyword") String keyword,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            // Guard against empty keyword
            if (keyword == null || keyword.trim().isEmpty()) {
                modelAndView.addObject("error", "请输入搜索关键词");
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.setViewName("goodsList");
                return modelAndView;
            }
            
            PageResult<Goods> pageResult = goodsService.searchGoodsPage(keyword.trim(), page, pageSize);
            modelAndView.addObject("goods", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.addObject("keyword", keyword);
            modelAndView.setViewName("goodsList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/addToCart")
    /**
     * 添加商品到购物车。
     */
    public ModelAndView addToCart(@RequestParam("goodId") int goodId, 
                                   @RequestParam(value = "amount", defaultValue = "1") int amount,
                                   HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user != null) {
                Goods goods = goodsService.getGoodsById(goodId);
                
                if (goods != null) {
                    // 检查库存
                    int currentCartAmount = 0;
                    Cart existingCart = cartService.getCartByUserNameAndGoodId(user.getUsername(), String.valueOf(goodId));
                    if (existingCart != null) {
                        currentCartAmount = existingCart.getAmount();
                    }
                    int newTotalAmount = currentCartAmount + amount;
                    if (newTotalAmount > goods.getStock()) {
                        modelAndView.addObject("msg", "库存不足！当前库存: " + goods.getStock());
                        modelAndView.setViewName("redirect:/goods/detail?id=" + goodId + "&msg=" + URLEncoder.encode("库存不足", "UTF-8"));
                        return modelAndView;
                    }
                    
                    if (existingCart != null) {
                        existingCart.setAmount(newTotalAmount);
                        existingCart.setTotalPrice(existingCart.getPrice() * newTotalAmount);
                        cartService.updateCart(existingCart);
                    } else {
                        Cart cart = new Cart();
                        cart.setGoodId(String.valueOf(goodId));
                        cart.setUserName(user.getUsername());
                        cart.setIntro(goods.getIntro());
                        cart.setAmount(amount);
                        cart.setPrice(goods.getPrice());
                        cart.setTotalPrice(goods.getPrice() * amount);
                        cart.setCover(goods.getCover());
                        cartService.addCart(cart);
                    }
                }
            }
            
            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/topSell")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView topSell(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            PageResult<Goods> pageResult = goodsService.getTopSellGoodsPage(page, pageSize);
            modelAndView.addObject("goods", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 10);
            modelAndView.addObject("title", "热销排行榜");
            modelAndView.addObject("intro", "最受欢迎的蛋糕，销量领先");
            modelAndView.setViewName("topSell");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/newGoods")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView newGoods(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            PageResult<Goods> pageResult = goodsService.getNewGoodsPage(page, pageSize);
            modelAndView.addObject("goods", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 11);
            modelAndView.addObject("title", "新品上市");
            modelAndView.addObject("intro", "最新上架的美味蛋糕");
            modelAndView.setViewName("newGoods");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/favor")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView favor(@RequestParam("goodsId") int goodsId,
                               HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (!favoriteService.isFavorited(user.getId(), goodsId)) {
                Favorite favorite = new Favorite();
                favorite.setUserId(user.getId());
                favorite.setGoodsId(goodsId);
                favoriteService.addFavorite(favorite);
            }

            modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=" + URLEncoder.encode("已收藏", "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/unfavor")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView unfavor(@RequestParam("goodsId") int goodsId,
                                 HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            favoriteService.removeFavorite(user.getId(), goodsId);
            modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=" + URLEncoder.encode("已取消收藏", "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/myFavorites")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView myFavorites(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            List<Favorite> favorites = favoriteService.getFavoritesByUserId(user.getId());
            // 批量加载商品，避免模板中 N+1 查询
            java.util.Map<Integer, Goods> goodsMap = new java.util.HashMap<>();
            for (Favorite fav : favorites) {
                Goods g = goodsService.getGoodsById(fav.getGoodsId());
                if (g != null) {
                    goodsMap.put(fav.getGoodsId(), g);
                }
            }
            modelAndView.addObject("favorites", favorites);
            modelAndView.addObject("goodsMap", goodsMap);
            modelAndView.addObject("favoriteCount", favorites.size());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.setViewName("favoriteList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}