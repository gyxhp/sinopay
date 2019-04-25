$(function(){
	var money = $("#money");
	var isMoney = function(money){
		var exp = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/; 
		if(exp.test(money)){
    		return true;
	    }else{
	    	alert("请输入正确的金额");
	    	return false;
	    }
	};
	var btnClick = function(){
		if(isMoney(money.val())!=true)return;
		sessionStorage.setItem("money",money.val())
		window.location.href = "paymentPage.html";
	};
	$(".btn").on("click",btnClick);
});
