<#include '/inc/layout.ftl'>

<@layout "用户中心">
  <div class="layui-container fly-marginTop fly-user-main">
    <@userLeft level=1></@userLeft>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>


    <div class="fly-panel fly-panel-user" pad20>
      <!--
      <div class="fly-msg" style="margin-top: 15px;">
        您的邮箱尚未验证，这比较影响您的帐号安全，<a href="activate.html">立即去激活？</a>
      </div>
      -->
      <div class="layui-tab layui-tab-brief" lay-filter="user">
        <ul class="layui-tab-title" id="LAY_mine">
          <li data-type="mine-jie" lay-id="index" class="layui-this">我发的帖（<span id="postNum">0</span>）</li>
          <li data-type="collection" data-url="/collection/find/" lay-id="collection">我收藏的帖（<span id="collectionNum">0</span>）</li>
        </ul>
        <div class="layui-tab-content" style="padding: 20px 0;">
          <div class="layui-tab-item layui-show">
            <ul class="mine-view jie-row" id="post">
              <script id="tpl-post" type="text/html">
              <li>
                <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{d.title}}</a>
                <i>{{layui.util.toDateString(d.create, 'yyyy-MM-dd HH:mm:ss')}}</i>
                <a class="mine-edit" href="/post/edit?id={{post.id}}">编辑</a>
                <em>{{d.viewCount}}阅/{{d.commentCount}}答</em>
              </li>
              </script>
            </ul>
            <div id="LAY_page"></div>
          </div>
          <div class="layui-tab-item">
            <ul class="mine-view jie-row" id="collection">
              <script id="tpl-collection" type="text/html">
              <li>
                <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{d.title}}</a>
                <i>收藏于{{layui.util.timeAgo(d.created, true)}}</i>  </li>
              </script>
            </ul>
            <div id="LAY_page1"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script>
    layui.cache.page = 'user';
    layui.use(['laytpl', 'flow', 'util'], function () {
      var laytpl = layui.laytpl;
      var flow = layui.flow;
      var util = layui.util;
      var $ = layui.jquery;
      var postNum = 0;
      var collectionNum = 0;

      flow.load({
        elem: '#post',
        isAuto: false,
        done: function (page, next) {
          var lis = [];
          $.get('/user/post?pn='+page, function (res) {
            postNum = res.data.total;
            $("#postNum").text(postNum);
            layui.each(res.data.records, function (index, item){
              var tpl = $("#tpl-post").html();
              laytpl(tpl).render(item, function (html) {
                lis.push(html)
              })
            });
            next(lis.join(''), page < res.data.pages);
          })
        }
      });

      flow.load({
        elem: '#collection',
        isAuto: false,
        done: function (page, next) {
          var lis = [];
          $.get('/user/collection?pn='+page, function (res) {
            collectionNum = res.data.total;
            $("#collectionNum").text(collectionNum);
            layui.each(res.data.records, function (index, item){
              var tpl = $("#tpl-collection").html();
              laytpl(tpl).render(item, function (html) {
                lis.push(html)
              })
            });
            next(lis.join(''), page < res.data.pages);
          })
        }
      });
    })
  </script>
</@layout>