<html>
<div>
    <h3>${msg} is failed.</h3>

    <div>
        error message:
    </div>
    <ul>
        <li>${exceptionMsg}:</li>
    <#list stacks as item>
        <li>${item}</li>
    </#list>
    </ul>
<#if htmlContent??>
    <div>
        <h4>htmlContent:</h4>
    ${htmlContent}
    </div>
</#if>
</div>
</html>