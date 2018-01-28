package com.nowcoder.controller;

import com.nowcoder.Util.ToutiaoUtil;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Administrator on 2018/1/27 0027.
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            news.setImage(image);
            if (hostHolder != null){
                news.setUserId(hostHolder.getUsers().getId());
            }
            else {
                //设置一个匿名用户
                news.setUserId(2);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯失败", e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }

    }




    @RequestMapping(path = {"/image/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void getImage(@RequestParam("name") String name,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + name)), response.getOutputStream());
        } catch (IOException e) {
            logger.error("读取图片错误" + name + e.getMessage());
        }
    }


    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
//    public String uploadImage(@RequestParam("file") MultipartFile file){
//        try {
//            String fileUrl = newsService.saveImage(file);
//            if (fileUrl == null){
//                return ToutiaoUtil.getJSONString(1,"上传图片失败");
//            }
//            return ToutiaoUtil.getJSONString(0,fileUrl);
//
//        } catch (IOException e) {
//            logger.error("上传图片失败" + e.getMessage());
//            return ToutiaoUtil.getJSONString(1, "上传失败");
//        }

    public String uploadImage(@RequestParam("file") MultipartFile[] files){
        Map<String, Object> map = new HashMap<>();
        int index = 1;
        for (MultipartFile file : files) {
            try {
                //String fileUrl = newsService.saveImage(file);
                String fileUrl = qiniuService.saveImage(file);
                if (fileUrl == null){
                    return ToutiaoUtil.getJSONString(1,"上传图片失败");
                }
                String msg = "msg" + index;
                map.put(msg, fileUrl);
                ++index;
                //return ToutiaoUtil.getJSONString(0,fileUrl);
            } catch (IOException e) {
                logger.error("上传图片失败" + e.getMessage());
                return ToutiaoUtil.getJSONString(1, "上传失败");
            }
        }
        return ToutiaoUtil.getJSONString(0, map).toString();
    }

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        try {
            News news = newsService.getById(newsId);
            if (news != null){
                int localUserId = hostHolder.getUsers() != null ? hostHolder.getUsers().getId() : 0;
                if (localUserId != 0) {
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
                } else {
                    model.addAttribute("like", 0);
                }
                List<Comment> comments = commentService.getCommentsByEntity(newsId, EntityType.ENTITY_NEWS);
                List<ViewObject> commentVos = new ArrayList<>();
                for (Comment comment : comments){
                    ViewObject commentVo = new ViewObject();
                    commentVo.set("comment", comment);
                    commentVo.set("user", userService.getUser(comment.getUserId()));
                    commentVos.add(commentVo);
                }
                model.addAttribute("comments", commentVos);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e) {
            logger.error("获取资讯明细错误" + e.getMessage());
        }
        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUsers().getId());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新评论数量，以后用异步实现
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }



}
