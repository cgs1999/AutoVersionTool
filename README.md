*自动化版本工具(AutoVersionTool)*

在进行Web项目开发过程中，经常会遇到静态文件，如：css、js、images等文件，

由于缓存原因导致不能在用户浏览器端达到效果，需要手工清空浏览器缓存或强制刷新Web页面才行。

相关的解决方案是在引用静态文件时增加时间戳或版本号，这样就可以达到解决该问题。

但由于静态文件在不断的更新，需要人工维护引用静态文件的时间戳或版本号，忘记更新或有个别引用没有修改到都可能导致问题的重现。


自动化版本工具就是用来解决引用静态文件的时间戳或版本号的维护问题，只要设置好相关的规则，

在提交变更或部署项目前执行一下自动化版本工具，即可完成指定目录、指定类型文件、指定匹配规则的静态文件引用的时间戳或版本号的自动更新



*V0.3.2*
-------------------------------------------------
1、更新内容

（1）重构所有代码及相关设计;

（2）增加配置文件config.xml，并支持灵活的配置，详见config.xml说明;

（3）增加文件编码格式识别功能，并支持统一转换修改文件的文件编码格式;


2、遗留问题

（1）配置文件支持相对路径;

（2）不支持忽略指定文件（即不检测）;

（3）待增加代码规范说明;

（4）待增加代码范例demo;


3、特别说明

（1）当前版本存在循环处理逻辑问题，主要场景：

     A文件包含B文件的引用，B文件包含了C文件的引用，C文件发生变化，
     
     那么处理过程中若先处理A文件，发现B文件没有变化则不修改A文件中B文件的引用版本，
     
     而后续处理B文件时，发现其引用C文件发生了变化，修改了B文件中C文件的引用版本，
     
     从而导致了B版本修改后的版本与A文件中引用B文件的版本不一致
     
（2）解决方案

	 运行多次（次数取决于最大的嵌套引用层数）,多次运行后会存在版本不一致的情况,
	 
	 不过虽然版本不一致,但对于浏览器来说都是新的引用,可以解决缓存的更新问题;
     