$(function () {
    var jsonstr = {
        "mchId": "10000000",
        "reqKey": "asdfghjkl",
        "resKey": "asdfghjkl"
    }
    var urlStr = "/sinopay/wechatpay/mch";
    $.ajax({
        async: false,
        type: "post",
        url: urlStr,
        data: "jsonstr=" + JSON.stringify(jsonstr),
        datatype: "json",
        beforeSend: function () {

        },
        success: function (data) {
            var flag=data.code;
            if(flag=='true'){
                window.location.href=data.url;
            }
        }
    });
});


