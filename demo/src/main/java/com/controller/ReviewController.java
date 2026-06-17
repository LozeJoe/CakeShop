package com.controller;

import com.javaBean.Review;
import com.javaBean.User;
import com.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/review")
@Controller
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @RequestMapping("/add")
    public ModelAndView addReview(@RequestParam("goodsId") int goodsId,
                                   @RequestParam("content") String content,
                                   @RequestParam(value = "rating", defaultValue = "5") int rating,
                                   HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Review review = new Review();
            review.setGoodsId(goodsId);
            review.setUserId(user.getId());
            review.setUserName(user.getName() != null ? user.getName() : user.getUsername());
            review.setContent(content);
            review.setRating(Math.max(1, Math.min(5, rating))); // 1-5
            reviewService.addReview(review);

            modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=评论发表成功");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/delete")
    public ModelAndView deleteReview(@RequestParam("id") int id,
                                      @RequestParam("goodsId") int goodsId,
                                      HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Review review = reviewService.getReviewById(id);
            if (review == null || (review.getUserId() != user.getId() && !"1".equals(user.getIsadmin()))) {
                modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=无权删除此评论");
                return modelAndView;
            }
            reviewService.deleteReview(id);
            modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=评论已删除");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}
