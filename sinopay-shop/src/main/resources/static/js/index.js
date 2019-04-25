!function() {//根据屏幕大小适配不同尺寸
    var html = document.documentElement;
    var setFontSize = function() {
        var width = html.offsetWidth;
        if(width <= 640) {
            html.style.fontSize = width / 6.4+ 'px';
        } else {
            html.style.fontSize = 100 + 'px';
        }
    };
    var timer;
    var setDelay = function() {
        return clearTimeout(timer), (timer = setTimeout(setFontSize, 150));
    }
    window.addEventListener('pageshow', function(evt) {
        return evt.persisted && setDelay();
    });
    window.addEventListener('resize', setDelay);
    setFontSize();
}();