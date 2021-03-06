package com.cb.web.zuitoutiao.controller;

import com.cb.web.zuitoutiao.entity.ArticleComment;
import com.cb.web.zuitoutiao.entity.Comment;
import com.cb.web.zuitoutiao.service.ArticleCommentService;
import com.cb.web.zuitoutiao.service.impl.ArticleCommentServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 负责资讯评论方面的操作
 * @author: YFZX-CB-1784 ChenBen
 * @create: 2018-08-20 14:31
 **/
@CrossOrigin(origins = "http://localhost:8081")
@Controller
@Api(value = "ArticleComment",description = "资讯评论接口")
@RequestMapping(value = "/ArticleComment")
public class ArticleCommentController {
    @Autowired
    private ArticleCommentService articleCommentService;
    private Logger logger = LoggerFactory.getLogger(ArticleCommentServiceImpl.class);

    /**
     * @Description: 添加资讯评论
     * @Param: [articleComment]
     * @return: java.util.Map<java.lang.String , java.lang.Object>
     * @Author: Chen Ben
     * @Date: 2018/8/20
     */
    @RequestMapping(value = "/addArticleComment", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ApiOperation(value="添加资讯评论接口", notes="添加资讯评论（createDate不用填）")
    @ResponseBody
    public Map<String, Object> addArticleComment(@ApiParam(required=true, name="articleComment", value="资讯评论")
                                  @RequestBody ArticleComment articleComment) {
        logger.info("评论资讯");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        articleCommentService.addComment(articleComment);
        resultMap.put("articleComment",articleComment);
        return resultMap;
    }

    /**
     * @Description: 添加子评论
     * @Param: [comment]
     * @return: void
     * @Author: Chen Ben
     * @Date: 2018/8/20
     */
    @RequestMapping(value = "/addSubreview", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ApiOperation(value="添加子评论接口", notes="添加子评论（createDate不用填）")
    @ResponseBody
    public Map<String, Object> addSubreview( @RequestBody Comment comment) {
        logger.info("添加子评论");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        articleCommentService.addSubreview(comment);
        resultMap.put("comment",comment);
        return resultMap;
    }

    /**
     * @Description: 根据资讯id获取资讯的所有评论及其子评论, articleCommentList为该资讯的所有父评论，commentList为所有子评论
     * @Param: [id]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Chen Ben
     * @Date: 2018/8/20
     */
    @RequestMapping(value = "/getComment", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    @ApiOperation(value="获取资讯评论接口", notes="根据资讯id获取资讯的所有评论及其子评论, articleCommentList为该资讯的所有父评论，commentList为所有子评论")
    public Map<String, Object> getComment(@ApiParam(required=true, name="id", value="资讯id")
                                          @RequestParam(value = "id", required = true) Integer id) {
        logger.info("获取资讯评论");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        JSONArray commentJsonArray = new JSONArray();
        List<ArticleComment> articleCommentList = articleCommentService.getArticleCommentListById(id);
        for (ArticleComment articleComments : articleCommentList) {
            JSONObject jsonObject = new JSONObject();
            List<Comment> commentList = articleCommentService.getCommentListById(articleComments.getId());
            jsonObject.put("commentList", commentList);
            commentJsonArray.add(jsonObject);
        }
        resultMap.put("articleCommentList", articleCommentList);
        resultMap.put("commentJsonArray", commentJsonArray);
        return resultMap;
    }

    /**
     * @Description: 根据评论的id以及type属性的值来辨别是给父评论进行点赞还是给子评论进行点赞
     * @Param: [id, type]
     * @return: void
     * @Author: Chen Ben
     * @Date: 2018/8/21
     */
    @RequestMapping(value = "/updateLikes", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ApiOperation(value="评论点赞接口", notes="根据评论的id以及type属性的值来辨别是给父评论进行点赞还是给子评论进行点赞")
    @ResponseBody
    public Map<String, Object> updateLikes(@ApiParam(required=true, name="userId", value="用户id")
                            @RequestParam(value = "userId", required = true) Integer userId,
                            @ApiParam(required=true, name="commentId", value="评论id（要跟数据库中type为2的id对应）")
                            @RequestParam(value = "commentId", required = true) Integer commentId,
                            @ApiParam(required=false, name="type", value="资讯类型（type为null时为给父评论点赞，type为1时为给普通的子评论点赞,type为2时给自评论中的回复评论点赞）")
                            @RequestParam(value = "type", required = false) String type) {
        logger.info("给评论点赞");
        Map<String, Object> resultMap = new HashMap<>();
        Integer likes = articleCommentService.updateLikes(userId,commentId,type);
        resultMap.put("likes",likes);
        return resultMap;
    }

    /**
     * @Description: 根据评论的id以及type属性的值来辨别是给父评论进行点踩还是给子评论进行点踩
     * @Param: [id, type]
     * @return: void
     * @Author: Chen Ben
     * @Date: 2018/8/21
     */
    @RequestMapping(value = "/updateDislikes", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ApiOperation(value="评论点踩接口", notes="根据评论的id以及type属性的值来辨别是给父评论进行点踩还是给子评论进行点踩")
    @ResponseBody
    public Map<String, Object> updateDislikes(@ApiParam(required=true, name="id", value="评论id")
                               @RequestParam(value = "id", required = true) Integer id,
                               @ApiParam(required=false, name="type", value="资讯类型（type为null时为给父评论点踩，type不为null时为给子评论点踩）")
                               @RequestParam(value = "type", required = false) Integer type) {
        logger.info("给评论点踩");
        Map<String, Object> resultMap = new HashMap<>();
        Integer dislikes = articleCommentService.updateDislikes(id, type);
        resultMap.put("dislikes",dislikes);
        return resultMap;
    }

    /**
     * @Description: 根据父评论的id获取相应的子评论
     * @Param [commentId]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Chen Ben
     * @Date 2018/9/26/026
     */
    @RequestMapping(value = "/getSubreview", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ApiOperation(value="获取子评论接口", notes="根据父评论的id获取相应的子评论")
    @ResponseBody
    public Map<String, Object> getSubreview(@ApiParam(required=true, name="commentId", value="父评论id")
                                            @RequestParam(value = "commentId", required = true) Integer commentId){
        logger.info("获取id为"+commentId+"的子评论");
        Map<String, Object> resultMap = new HashMap<>();
        List<Comment> commentList = articleCommentService.getCommentListById(commentId);
        resultMap.put("commentList",commentList);
        return resultMap;
    }
}
