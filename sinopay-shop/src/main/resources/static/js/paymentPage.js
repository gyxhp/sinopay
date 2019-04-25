$(function () {
    var isWeixinBrowser = function () {
        var userAgent = navigator.userAgent.toLowerCase();
        if (userAgent.match(/MicroMessenger/i) == "micromessenger") {
            //判断是微信app的浏览器
            $(".methodList").eq(1).addClass("active").siblings().removeClass("active").unbind();
            //支付宝隐藏
            $(".methodList").eq(0).hide();
            //alert("微信");
            return "wx";
        }
        if (userAgent.match(/Alipay/i) == "alipay") {
            //判断是支付宝app的浏览器
            $(".methodList").eq(0).addClass("active").siblings().removeClass("active").unbind();
            //微信隐藏
            $(".methodList").eq(1).hide();
            //	alert("支付宝");
            return "zfb";
        } else {
            //	alert("其他浏览器");
            return "QT";
        }
    }
    //js获取项目根路径，如： http://localhost:8083/sinopay
    var getRootPath = function (){
        //获取当前网址，如： http://localhost:8083/sinopay/share/meun.jsp
        var curWwwPath=window.document.location.href;

        //获取主机地址之后的目录，如： sinopay/share/meun.jsp
        var pathName=window.document.location.pathname;
        var pos=curWwwPath.indexOf(pathName);

        //获取主机地址，如： http://localhost:8083
        var localhostPath=curWwwPath.substring(0,pos);

        return localhostPath;
    }
    var isBrowser = isWeixinBrowser();
    //var money = sessionStorage.getItem("money");
    //$("#money").html("￥" + Number(money).toFixed(2)).attr("data-money", money);
    $(".methodList").on("click", function () {
        $(this).addClass("active").siblings().removeClass("active");
    });
    $(".btn").on("click", function () {
        var money = $("#payamount").val(),
            methodVal = $(".active").attr("data-val"),
            goodsOrderNo = $("#goodsOrderNo").val(),
            paramsVal = $("#paramsVal").val();
        // alert(paramsVal);
        var params = "payamount=" + Number(money).toFixed(2) + "&paytype=" + methodVal
            +"&goodsOrderNo="+goodsOrderNo+"&params="+paramsVal;
        //"WX_JSAPI微信公众号支付; ALIPAY_WAP支付宝WAP支付"
        var redirectUrl = getRootPath()+"/sinopay/wechatpay/paymoney?"+encodeURI(params,'utf-8');
        console.log(redirectUrl);
        // alert(redirectUrl);
        if(methodVal=="YL_UNION"){
            alert("暂不支持");
            return;
        }

        if(methodVal=="WX_JSAPI"){
            //如果是微信支付，需要首先获取openid
            window.location="/sinopay/pay/getOpenId?redirectUrl="+redirectUrl;
        }else{
            var urlStr = "/sinopay/wechatpay/paymoney?" + params;
            console.log(urlStr);
            window.location=urlStr;
        }
    });
});
