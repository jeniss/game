<%--
  Created by IntelliJ IDEA.
  User: jennifert
  Date: 2015/10/21
  Time: 14:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <title></title>
    <script type="text/javascript" src="<c:url value='/resources/script/jquery-3.1.1.min.js'/>"></script>
    <style type="text/css">
        .img-container {
            overflow: hidden
        }

        .img-container > img {
            display: block;
            width: 100%
        }

        .img-container > .fix-height {
            width: auto;
            height: 100%
        }

        .img-container {
            width: 500px;
            height: 600px;
        }
    </style>
</head>
<body>

<form id="addStockFlow" onsubmit="return false;">
    <input id="stockId" name="stockId" value="1"/>
    <select name="type" id="type">
        <option value="PUT">put</option>
        <option value="SOLD">sold</option>
    </select>
    <input name="price" id="price"/>
    <input name="amount" id="amount"/>
    <input type="button" value="submit" onclick="add()"/>
</form>
<script type="text/javascript">
    function add() {
//        var params = {
//            stockId: $('#stockId').val(),
//            type: $('#type').val(),
//            price: $('#price').val(),
//            amount: $('#amount').val()
//        };

        $.post({
                    url: "insertStockFlow.do",
                    type: "post",
                    dataType: "json",
                    data: $('#addStockFlow').serialize(),
                    function () {

                    }
                }
        );
    }
</script>
</body>
</html>
