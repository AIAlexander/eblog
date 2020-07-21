package com.alex.service;

import com.alex.entity.Post;
import com.alex.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author alex
 * @since 2020-06-15
 */
public interface PostService extends IService<Post> {

    IPage<PostVO> getPostByPage(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVO getPostDetail(Long id);

    void initWeekRank();

    void increaseCommentCountAndUnionForRank(long postId, boolean isIncrease);

    void putViewCount(PostVO postVO);

    IPage<PostVO> getCollectionPagesByUserId(Page page, Long userIf);

    int getPostNumByPostId(Long postId);

    Long submitPost(PostVO postVO, Long userId);

    Boolean deletePost(Long postId);
}
