var imageAdapt = function (imgContainer) {
    var img = imgContainer.getElementsByTagName('IMG')[0];

    if (img) {
        if (img.height < imgContainer.clientHeight) {
            img.classList.add('fix-height');
            img.style.marginLeft = '-' + ((img.width - imgContainer.clientHeight) / 2) + 'px';
            img.style.marginTop = '';
        }
        else if (img.height > imgContainer.clientHeight) {
            img.classList.remove('fix-height');
            img.style.marginLeft = '';
            img.style.marginTop = '-' + ((img.height - imgContainer.clientHeight) / 2) + 'px';
        }
    }
};

(function (window) {

    var UI = {
        util: {}
    };

    UI.util.singleton = function(fn) {
        var result;
        return function() {
            return result || (result = fn.apply(this, arguments));
        }
    };

    /**
     * @private
     * @param {Function} fn1 (will be processed when we invoke _createAlert at the first time)
     * */
    var _createAlert = UI.util.singleton(function (options) {
        var className = 'alert';
        var span = document.createElement('span');

        options = options ? options : {};
        className = className + (options.className ? options.className : '');

        span.setAttribute('class', className);
        document.body.appendChild(span);

        return span;

    });

    /**
     * @param {Object} options.msg (message to show)
     * @param {Object} options.dur (display duration)
     * @param {Object} options.permanent (display permanently)
     * */
    UI.util.alert = function (options) {
        if(!(options && typeof options === "object" && options.msg)) return;

        var alertNode = _createAlert();
        var dur = options.dur ? options.dur : 1;

        alertNode.innerHTML = options.msg;

        //show message
        setTimeout(function () {
            alertNode.style.opacity = 1;
        }, 0);

        //hide message
        if (!options.permanent) {
            setTimeout(function () {
                alertNode.style.opacity = 0;
            }, dur * 1000);
        }
    }

    window.UI = UI;

})(window);
