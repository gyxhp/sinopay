<#assign base = request.contextPath />
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>上海人寿收银台</title>
    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" type="text/css" href="${base}/css/index.css"/>
</head>
<body>
<div class="title">请在当天24点之前完成支付，逾期订单将自动取消</div>
<div class="money inputText">
    <span class="w100 tc">应付金额</span>
    <div class="w100 tc bdN pt2">
        <span id="money" class="moneyC" >¥ ${amount}</span>
        <span class="w100 color pt1">订单编号：${mchOrderNo}</span>
        <span class="w100 color pt1">商品名称：${subject}</span>
        <input type="hidden" id="goodsOrderNo" value="${goodsOrderNo}"></input>
        <input type="hidden" id="payamount" value="${amount}"></input>
        <input type="hidden" id="paramsVal" value="${params}"></input>
        </span
        <div class="clear"></div>
    </div>
    <div class="ht3"></div>
    <div class="method auto">
        <h3>支付方式</h3>
        <div class="paymentChoice">
            <ul>
                <!--支付宝-->
                <li class="methodList bdB active" data-val="ALIPAY_WAP">
							<span class="fl icon_img icon_zfb">
								<img src="${base}/img/zfb.png" alt="" />
							</span>
                    <span class="fl">支付宝</span>
                    <span class="fr icon_Select"></span>
                    <div class="clear"></div>
                </li>
                <!--微信-->
                <li class="methodList bdB"  data-val="WX_JSAPI">
							<span class="fl icon_img icon_wx">
								<img src="${base}/img/wx.png" alt="" />
							</span>
                    <span class="fl">微信支付</span>
                    <span class="fr icon_Select"></span>
                    <div class="clear"></div>
                </li>
                <!--银联-->
                <#--<li class="methodList"  data-val="YL_UNION">-->
							<#--<span class="fl icon_img icon_yl">-->
								<#--<img src="${base}/img/yl.png" alt="" />-->
							<#--</span>-->
                    <#--<span class="fl">银联</span>-->
                    <#--<span class="fr icon_Select"></span>-->
                    <#--<div class="clear"></div>-->
                <#--</li>-->
            </ul>
        </div>
    </div>
    <div class="btn w40">立即支付</div>
    <script src="${base}/js/zepto.min.js"></script>
    <script src="${base}/js/index.js" ></script>
    <script src="${base}/js/paymentPage.js" ></script>
    <script src="${base}/js/SHA-1.js" ></script>
</body>
</html>
