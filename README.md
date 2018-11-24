# Wechat Spellbook

[![Build Status](https://travis-ci.org/Gh0u1L5/WechatSpellbook.svg?branch=master)](https://travis-ci.org/Gh0u1L5/WechatSpellbook) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gh0u1l5/wechat-spellbook/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gh0u1l5/wechat-spellbook)

---

### 2018.11.24 状态更新

最近半年实在是太忙了，一直没有更新项目。

最近先是花了一天多时间，解决了一下拖了很久的性能问题。通过重写APK Parser、启用新的数据结构和并行计算模块，把性能提升了10倍不止，详情可以参见这两个commit：[5bf7804](../../commit/5bf7804664606dd6280d5a0dc6e33f3a9ffbb5a6) & [388e25f](../../commit/388e25f904e73633f6639ae1b9e1aa1d959607cb)

然后，就是把项目中的注释几乎全盘改成了中文，方便以后的交流。剩下还没有改完的部分诚求热心人帮一下忙，翻译起来实在是太累了。

接下来会找机会把目前提交的issue看一下，把6.6.x及以上的微信版本都适配一下，不过这可能要等到12月底了吧。

---

Wechat Spellbook 是一个使用Kotlin编写的开源微信插件框架，底层需要 [Xposed](https://forum.xda-developers.com/xposed) 或 [VirtualXposed](https://github.com/android-hacker/VirtualXposed) 等Hooking框架的支持，而顶层可以轻松对接Java、Kotlin、Scala等JVM系语言。让程序员能够在几分钟内编写出简单的微信插件，随意揉捏微信的内部逻辑。

另外，在编写项目文档的过程中，也会找机会向大家分享一些逆向微信的经验和适配不同操作系统踩到的坑，也欢迎大家把自己的经验分享上来自由讨论。

## 便利特色

* __精心设计各项机制，合理运用多线程和惰性求值等技巧，用不到的功能永远不会影响你的性能。__
* 使用一套API __自动分析__ 微信内部结构特征，__避免手工适配__ 每个微信版本不同的类名、方法名。
  - 每次微信更新的时候，都会使用写好的单元测试自动验证是否有特征失效。
  - 精心设计的框架保证了开发者可以轻松拓展添加自己需要的特征。
* 框架内部设计了 _EventCenter_ 和 _HookerProvider_ 两类不同的事件处理方式。
  - _EventCenter_ 让开发者直接使用设计好的事件消息来截获微信数据，保证 __便利性__ 。
  - _HookerProvider_ 允许熟悉Xposed的开发者调用Xposed接口进行自由发挥，保证 __自由度__ 。
* 正确使用 _EventCenter_ 方案，有助于回避Xposed的一些小问题，如
  - 函数调用被前一个劫持者打断导致的插件相互冲突。
  - Xposed自Android 7.0后偶发的，由于多线程导致ART崩溃的问题。

## 衍生项目
* [WechatMagician](https://github.com/Gh0u1L5/WechatMagician)
* [WechatBotXposed](https://github.com/Blankeer/WechatBotXposed)

## 开发文档
* [简介](https://github.com/Gh0u1L5/WechatSpellbook/wiki/Home)
* [快速上手](https://github.com/Gh0u1L5/WechatSpellbook/wiki/快速上手)
* 开发教程
  - [事件机制](https://github.com/Gh0u1L5/WechatSpellbook/wiki/事件机制)
  - 反射集合
  - 混淆与自动适配
* 逆向技巧
  - 常见逆向工具
  - 调试输出
  - 堆栈跟踪
  - Support库
  - 免重启调试插件
  

## 关于VirtualXposed

目前对于VirtualXposed的支持还算不上完善，因为VirtualXposed的环境和原生Xposed的环境实在是差了太多，我和weishu折腾了很久才算是在部分设备上解决了黑屏卡死的问题。现在我们俩发布代码的日子里，一个拜三清一个拜关公，希望能够帮Bug们早日往生。

## 写给开发者

在出于兴趣接触微信逆向的短短一年里，我接触到了形形色色的开发者、投资者、支持者。被微信本身复杂成熟的架构深深吸引的同时，也惊异于微信衍生出来的灰色产业之庞大、第三方微信竞争之激烈。
有不少朋友劝我闭源、商业化，而且拿出了很多细致的想法，我很感激他们的帮助，但是最后还是决定在开源的方向上再次迈出了一大步，原因有二。

其一，我不想停在这里。商业化必然牵扯大量的时间精力和权益纠葛。我不想在自己正处在上升期的时候，就草率地把大量的时间精力都花在一个刚满20岁的时候意外做出来的小成就上，然后吃上十几年的老本。而且这个项目，归根结底是一个寄生在微信上的项目。这种格局，跟我每天在实习岗位上接触到的、跟大学里的同学谈论到的，都根本不在一个层面上。我虽然知道自己的才华有限，但是只要有机会的话，我还是想看看更高处的风景。

其二，我太过理想主义。中国互联网在很早的时候就已经是一个相当商业化的世界了。我并不反对商业化，也不觉得商业化有任何的道德问题。人，总归是要吃饭的。像fkzhang这样能靠自己的才华吃饭，既不偷也不抢，这有什么错呢？但是我心里总归是感觉到失落的，因为我最早爱上的，是一个自由的互联网，是一个为每个有才华有求知欲的年轻人准备的儿童乐园。只要思想用0和1表达出来，就再也没有什么规则能够阻挡他们，他们就是儿童乐园之王。我希望当我国的年轻一代对计算机产生好奇时，也能像西方国家的孩子们一样，能够轻松地、无语言障碍地，接触到大量好玩有趣的个人开源项目，进而爱上这个0与1的世界。如果有年轻的初中、高中的学弟学妹，能够用我的项目把微信像橡皮泥一样随意揉捏，像当年的我一样深深地享受到这个世界的乐趣，那么对我实在是一种莫大的鼓励与快乐。

当然，对于诸位想要基于我的项目做商业项目的开发者，也请尽管拿去。我不喜欢道德绑架别人，也不认为这有什么绑架的必要。但是如果你在闲暇时间能够贡献十几行代码、修复些你发现的Bug，那就已经十分感激。

## 打赏二维码

应 [Issue #5](https://github.com/Gh0u1L5/WechatSpellbook/issues/5) 的请求，贴上打赏二维码，让我们回归平和的技术讨论。

<img src="https://github.com/Gh0u1L5/WechatMagician/raw/master/image/qrcode_tenpay.png" width="40%" /> <img src="https://github.com/Gh0u1L5/WechatMagician/raw/master/image/qrcode_alipay.jpg" width="40%" />
