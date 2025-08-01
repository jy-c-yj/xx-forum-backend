package com.cyj.xx.mapper;


import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.SecPostsPageDTO;
import com.cyj.xx.dvo.*;
import com.cyj.xx.entity.Posts;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostsMapper {

    public Page<PostsHomeDVO> page(PostsPageDTO postsPageDTO);

    PostDetailDVO selectPost(Long postId);

    void UpdataPost(Posts posts);

    @Select("SELECT * FROM posts where id = #{postId}")
    Posts getPostById(Long postId);

    Page<PostsHomeDVO> Secpage(SecPostsPageDTO secPostsPageDTO);

    void addNewPost(Posts post);

    Page<MyInformationMainPostsDVO> getPostByUserId(Long userId);

    Page<MyInformationPostDVO> getMyPostsByUserId(Long userId);

    Page<MyInformationMyCollectionDVO> getMyCollectionByUserId(Long userId);

    Page<SearchPost> searchPost(String keyword);
}
