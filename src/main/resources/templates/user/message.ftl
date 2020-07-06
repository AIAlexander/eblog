<#include '/inc/layout.ftl'>

<@layout "我的消息">
  <div class="layui-container fly-marginTop fly-user-main">
    <@userLeft level=3></@userLeft>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>


    <div class="fly-panel fly-panel-user" pad20>
      <div class="layui-tab layui-tab-brief" lay-filter="user" id="LAY_msg" style="margin-top: 15px;">
        <button class="layui-btn layui-btn-danger" id="LAY_delallmsg">清空全部消息</button>
        <div  id="LAY_minemsg" style="margin-top: 10px;">
          <!--<div class="fly-none">您暂时没有最新消息</div>-->
          <ul class="mine-msg">
            <#list pageData.records as message>

                <li data-id="${message.id}">
                <#if message.type == 0>
                  <blockquote class="layui-elem-quote">
                    系统消息：${message.content}
                  </blockquote>
                </#if>
                <#if message.type == 1>
                  <blockquote class="layui-elem-quote">
                    <a href="/jump?username=Absolutely" target="_blank"><cite>${message.fromUserName}</cite></a>评论了您的文章<a target="_blank" href="/post/${message.postId}"><cite>${message.postTitle}</cite></a>，内容是：${message.content}
                  </blockquote>
                </#if>
                <#if message.type == 2>
                  <blockquote class="layui-elem-quote">
                    <a href="/jump?username=Absolutely" target="_blank"><cite>${message.fromUserName}</cite></a>回复了您的评论
                  </blockquote>
                </#if>
                  <p><span>${time(message.created)}</span><a href="javascript:;" class="layui-btn layui-btn-small layui-btn-danger fly-delete">删除</a></p>
                </li>

            </#list>
          </ul>
          <@paging pageData></@paging>
        </div>
      </div>
    </div>
  </div>
  <script>
    layui.cache.page = 'user';
  </script>
</@layout>